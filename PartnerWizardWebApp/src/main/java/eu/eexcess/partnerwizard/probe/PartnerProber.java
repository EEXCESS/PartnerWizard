package eu.eexcess.partnerwizard.probe;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.reference.PartnerRecommender;
import eu.eexcess.partnerwizard.datalayer.PartnerWizardTransformer;
import eu.eexcess.partnerwizard.probe.model.ProbeConfiguration;
import eu.eexcess.partnerwizard.probe.controller.ProbeConfigurationIterator;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponseIteration;
import eu.eexcess.partnerwizard.probe.model.Pair;
import eu.eexcess.partnerwizard.probe.model.ProberResult;
import eu.eexcess.partnerwizard.probe.model.web.ProberKeyword;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponse;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponse.State;
import eu.eexcess.partnerwizard.recommender.PartnerConnector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.xml.transform.TransformerConfigurationException;
import org.apache.commons.lang3.SerializationUtils;


/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-08-21
 */
public class PartnerProber{
	private static final int MAX_NUMBER_OF_RESULTS = 10;
	private static final Logger LOGGER = Logger.getLogger( PartnerProber.class.getName() );
	private static final String ID_PREFIX = "id-";
	private final Map<String, Integer> generators;
	private final PartnerConfiguration partnerConfigurationMaster;
	private final String systemId;

	private final ExecutorService executorService;
	private final Map<String, ProbeConfigurationIterator> configs;
	private int idCounter;

	public PartnerProber(Map<String, Integer> generators, PartnerConfiguration partnerConfiguration){
		this.generators = generators;
		this.partnerConfigurationMaster = partnerConfiguration;
		this.systemId = PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getSystemId();

		this.idCounter = 0;
		this.executorService = Executors.newFixedThreadPool( 10 );
		this.configs = new HashMap<>();
	}


	public ProberResponse init( List<ProberKeyword[]> queries ){
		List<String> generators = testWorkingGeneratorClasses( queries );

		if( !generators.isEmpty() ){
			String id = getId();

			ProbeConfigurationIterator iterator = new ProbeConfigurationIterator( queries, generators, true, true );
			configs.put( id, iterator );

			State nextState;
			if( iterator.isDone() ){
				nextState = State.Done;
			}
			else{
				nextState = State.Iteration;
			}

			return new ProberResponse( id, nextState );
		}
		else{
			LOGGER.info( "No Generator class returned at least one result. Aborting.");
			return new ProberResponse( "Error", State.Error );
		}
	}

	public ProberResponse storeAndNext( String id, boolean hasWinner, int result ){
		ProbeConfigurationIterator iterator = configs.get( id );
		if( iterator==null ){
			throw new IllegalArgumentException( "Unknown Id" );
		}

		Pair<ProbeConfiguration> probeConfigs;
		List<ProberResult> firstList;
		List<ProberResult> secondList;
		boolean equalResultLists;
		do{
			if( iterator.isWaitingForStore() ){
				iterator.storeResponse( hasWinner, result );

				hasWinner = false;
				result = -1;
			}

			if( iterator.isNextPairAvailable() ){
				probeConfigs = iterator.nextPair();
				Pair<FutureTask<List<ProberResult>>> resultListPair = retriveNextResultListPair( probeConfigs );

				try{
					firstList = resultListPair.first.get();
					secondList = resultListPair.second.get();
				}
				catch( InterruptedException|ExecutionException ex ){
					LOGGER.log( Level.SEVERE, "Query was interrupted ", ex );
					return new ProberResponse( id, State.Error );
				}
			}
			else{
				return new ProberResponse( id, State.Done );
			}

			equalResultLists = areResultListsEqual( firstList, secondList );
			if( equalResultLists ){
				LOGGER.log(Level.INFO, "Both result lists are equal, generation next pair. The configurations ''{0}'' and ''{1}'' where compared.", new Object[] {probeConfigs.first, probeConfigs.second});
			}
		}
		while( equalResultLists );

		ProberResponseIteration response = new ProberResponseIteration( id, State.Iteration );
		response.keywords = probeConfigs.first.keywords;
		response.firstList = firstList;
		response.secondList = secondList;

		return response;
	}

	public ProbeConfiguration getConfiguration( String id ){
		ProbeConfigurationIterator iterator = configs.get( id );
		if( iterator==null ){
			throw new IllegalArgumentException( "Unknown Id" );
		}
		else{
			return iterator.getWinningConfiguration();
		}
	}

	public PartnerConfiguration getParnterConfiguration( String id ){
		ProbeConfiguration probeConfig = getConfiguration( id );

		PartnerConfiguration partnerConfig = SerializationUtils.clone( partnerConfigurationMaster );

		partnerConfig.setIsQueryExpansionEnabled( probeConfig.queryExpansionEnabled );
		partnerConfig.setIsQuerySplittingEnabled( probeConfig.querySplittingEnabled );
		partnerConfig.setQueryGeneratorClass( probeConfig.queryGeneratorClass );

		return partnerConfig;
	}

	private synchronized String getId(){
		idCounter++;
		return ID_PREFIX+idCounter;
	}

	private List<String> testWorkingGeneratorClasses( List<ProberKeyword[]> queries ){
		final Map<String, Integer> generatorResults = Collections.synchronizedMap( new HashMap<String, Integer>( generators.size() ) );
		List<FutureTask<Void>> tasks = new ArrayList<>( generators.size()*queries.size() );
		for( final ProberKeyword[] keywords: queries ){
			for( final String generatorClass: generators.keySet() ){
				FutureTask<Void> recommenderTask = new FutureTask<>( () -> {
					try{
						ProbeConfiguration config = new ProbeConfiguration( keywords, generatorClass, Boolean.FALSE, Boolean.FALSE );
						SecureUserProfile userProfile = toUserProfile( config );

						ResultList results = getNewPartnerRecommender().recommend( userProfile );
						Integer resultCount = generatorResults.get( generatorClass );

						if( resultCount==null ){
							generatorResults.put( generatorClass, results.results.size() );
						}
						else{
							generatorResults.put( generatorClass, resultCount+results.results.size() );
						}
					}
					catch( IOException ex ){
						LOGGER.log( Level.SEVERE, "Partner could not be queried!", ex );
					}
					return null;
				} );
				executorService.execute( recommenderTask );

				tasks.add( recommenderTask );
			}
		}

		for( FutureTask<Void> future: tasks ){
			try{
				future.get();
			}
			catch( InterruptedException|ExecutionException ex ){
				LOGGER.log( Level.SEVERE, "Execution of generator class validity test was unexcpetedly terminated!", ex );
			}

		}

		return toSortedGeneratorList( generatorResults );
	}

	private Pair<FutureTask<List<ProberResult>>> retriveNextResultListPair( final Pair<ProbeConfiguration> probeConfigs ){
		FutureTask<List<ProberResult>> firstResponse = new FutureTask<>( () -> {
			SecureUserProfile profile = toUserProfile( probeConfigs.first );

			return getNewPartnerRecommender()
				.recommend( profile ).results
				.stream()
				.limit( MAX_NUMBER_OF_RESULTS )
				.map( result -> new ProberResult( result.title, result.description, result.description ) )
				.collect( Collectors.toList() );
		} );
		executorService.submit( firstResponse );

		FutureTask<List<ProberResult>> secondResponse = new FutureTask<>( () -> {
			SecureUserProfile profile = toUserProfile( probeConfigs.second );

			return getNewPartnerRecommender()
				.recommend( profile ).results
				.stream()
				.limit( MAX_NUMBER_OF_RESULTS )
				.map( result -> new ProberResult( result.title, result.description, result.description ) )
				.collect( Collectors.toList() );
		} );
		executorService.submit( secondResponse );

		return new Pair<>( firstResponse, secondResponse );
	}

	private List<String> toSortedGeneratorList( Map<String, Integer> generatorResults ){
		ArrayList<String> sortedGenerators = new ArrayList<>( generatorResults.size() );

		generatorResults.forEach( ( String generatorClass, Integer resultCount ) -> {
			if( resultCount>0 ){
				sortedGenerators.add( generatorClass );
			}
			else{
				LOGGER.log(Level.INFO, "Generator class ''{0}'' will not be used as it did not produce any results.", generatorClass);
			}
		} );
		sortedGenerators.trimToSize();

		Collections.sort( sortedGenerators, ( String generator1, String generator2 ) -> {
						  int ordinal1 = generators.get( generator1 );
						  int ordinal2 = generators.get( generator2 );

						  return Integer.compare( ordinal1, ordinal2 );
					  } );

		return sortedGenerators;
	}

	private static boolean areResultListsEqual( List<ProberResult> first, List<ProberResult> second ){
		if( first.size()!=second.size() ){
			return false;
		}
		else{
			for( int i = 0; i<first.size(); i++ ){
				ProberResult firstElement = first.get( i );
				ProberResult secondElement = second.get( i );

				if( !firstElement.equals( secondElement ) ){
					return false;
				}
			}
			return true;
		}
	}

	private SecureUserProfile toUserProfile( ProbeConfiguration config ) {
		PartnerBadge partnerBadge = new PartnerBadge();
		partnerBadge.setSystemId( systemId );
		partnerBadge.setQueryGeneratorClass( config.queryGeneratorClass );
		partnerBadge.setIsQueryExpansionEnabled( config.queryExpansionEnabled );
		partnerBadge.setIsQuerySplittingEnabled( config.querySplittingEnabled );

		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.setContextKeywords( config.toContextKeywords() );
		userProfile.setPartnerList( Arrays.asList( partnerBadge ) );

		return userProfile;
	}

	private PartnerRecommender getNewPartnerRecommender() throws TransformerConfigurationException{
		PartnerConfiguration partnerConfiguration = SerializationUtils.clone( partnerConfigurationMaster );
		PartnerConnector partnerConnector = new PartnerConnector();
		PartnerWizardTransformer transformer = new PartnerWizardTransformer();
		transformer.init( partnerConfiguration );

		return new PartnerRecommender( partnerConfiguration, partnerConnector, transformer);
	}

}
