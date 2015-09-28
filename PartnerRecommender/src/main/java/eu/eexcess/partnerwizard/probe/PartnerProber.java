package eu.eexcess.partnerwizard.probe;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.reference.PartnerRecommender;
import eu.eexcess.partnerwizard.probe.model.ProbeConfiguration;
import eu.eexcess.partnerwizard.probe.controller.ProbeConfigurationIterator;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponseIteration;
import eu.eexcess.partnerwizard.probe.model.Pair;
import eu.eexcess.partnerwizard.probe.model.ProberResult;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponse;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponse.State;
import java.io.IOException;
import java.util.ArrayList;
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

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-08-21
 */
public class PartnerProber{
	private static final Logger LOGGER = Logger.getLogger( PartnerProber.class.getName() );
	private static final String[] DEFAULT_GENERATORS = {"eu.eexcess.partnerrecommender.reference.LuceneQueryGenerator",
														"eu.eexcess.partnerrecommender.reference.LuceneQueryGeneratorFieldTermConjunction",
														"eu.eexcess.partnerrecommender.reference.OrQueryGenerator",
														"eu.eexcess.partnerrecommender.reference.OrQueryGeneratorFieldTermConjunction"};
	private static final String ID_Prefix = "id-";

	private int idCounter = 0;
	private final ExecutorService executorService = Executors.newFixedThreadPool( 10 );
	private final Map<String, ProbeConfigurationIterator> configs = new HashMap<>();

	public PartnerRecommender recommender = new PartnerRecommender();


	public ProberResponse init( List<String> keywords ){
		List<String> generators = testWorkingGeneratorClasses( keywords );

		if( !generators.isEmpty() ){
			String id = getId();

			ProbeConfigurationIterator iterator = new ProbeConfigurationIterator( keywords, generators, true, true );
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
			return new ProberResponse( "Error", State.Error );
		}
	}


	public ProberResponse storeAndNext( String id, boolean hasWinner, int result ){
		ProbeConfigurationIterator iterator = configs.get( id );
		if( iterator==null ){
			throw new IllegalArgumentException( "Unknown Id" );
		}
		else{
			try{
				Pair<ProbeConfiguration> probeConfigs;
				List<ProberResult> firstList;
				List<ProberResult> secondList;
				do{
					if( iterator.isWaitingForStore() ){
						iterator.storeResponse( hasWinner, result );

						hasWinner = false;
						result = -1;
					}

					if( iterator.isNextPairAvailable()) {
						probeConfigs = iterator.nextPair();
						Pair<FutureTask<List<ProberResult>>> resultListPair = retriveNextResultListPair( probeConfigs );
						firstList = resultListPair.first.get();
						secondList = resultListPair.second.get();
					}
					else{
						return new ProberResponse( id, State.Done );
					}
				}
				while( areResultListsEqual(firstList, secondList) );

				ProberResponseIteration response = new ProberResponseIteration( id, State.Iteration );
				response.keywords = probeConfigs.first.keyword;
				response.firstList = firstList;
				response.secondList = secondList;

				return response;
			}
			catch( InterruptedException|ExecutionException ex ){
				LOGGER.log( Level.SEVERE, "Query was interrupted ", ex );
				return new ProberResponse( id, State.Error );
			}
		}
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


	private synchronized String getId(){
		idCounter++;
		return ID_Prefix+idCounter;
	}

	private List<String> testWorkingGeneratorClasses( List<String> keywords ){
		Map<String, Integer> generatorResults = Collections.synchronizedMap( new HashMap<String, Integer>( DEFAULT_GENERATORS.length ) );
		List<FutureTask<Void>> tasks = new ArrayList<>( DEFAULT_GENERATORS.length*keywords.size() );
		for( String keyword: keywords ){
			for( String generatorClass: DEFAULT_GENERATORS ){
				FutureTask<Void> recommenderTask = new FutureTask<>( () -> {
					ProbeConfiguration config = new ProbeConfiguration( keyword, generatorClass, Boolean.FALSE, Boolean.FALSE );
					SecureUserProfile userProfile = toUserProfile( config );

					ResultList results;
					try{
						results = new PartnerRecommender().recommend( userProfile );
						Integer resultCount = generatorResults.get( generatorClass );
						if( resultCount==null ){
							generatorResults.put( generatorClass, results.totalResults );
						}
						else{
							generatorResults.put( generatorClass, resultCount+results.totalResults );
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

		tasks.forEach( ( FutureTask<Void> future ) -> {
			try{
				future.get();
			}
			catch( InterruptedException|ExecutionException ex ){
				LOGGER.log( Level.SEVERE, "Execution of generator class validity test was unexcpetedly terminated!", ex );
			}
		} );

		ArrayList<String> generators = new ArrayList<>();
		generatorResults.forEach( ( String generator, Integer count ) -> {
			if( count>0 ){
				generators.add( generator );
			}
		} );
		generators.trimToSize();

		return generators;
	}

	private Pair<FutureTask<List<ProberResult>>> retriveNextResultListPair( Pair<ProbeConfiguration> probeConfigs ){
		FutureTask<List<ProberResult>> firstResponse = new FutureTask<>( () -> {
			SecureUserProfile profile = toUserProfile( probeConfigs.first );
			List<Result> recommenderResults = recommender.recommend( profile ).results;
			List<ProberResult> results = new ArrayList<>( recommenderResults.size() );

			for( Result recommenderResult: recommenderResults ){
				results.add( new ProberResult( recommenderResult.title, recommenderResult.description, recommenderResult.description ) );
			}

			return results;
		} );
		executorService.submit( firstResponse );

		FutureTask<List<ProberResult>> secondResponse = new FutureTask<>( () -> {
			SecureUserProfile profile = toUserProfile( probeConfigs.second );
			List<Result> recommenderResults = recommender.recommend( profile ).results;
			List<ProberResult> results = new ArrayList<>( recommenderResults.size() );

			for( Result recommenderResult: recommenderResults ){
				results.add( new ProberResult( recommenderResult.title, recommenderResult.description, recommenderResult.description ) );
			}

			return results;
		} );
		executorService.submit( secondResponse );

		return new Pair<>(firstResponse, secondResponse);
	}

	
	private static boolean areResultListsEqual(List<ProberResult> first, List<ProberResult> second){
		if( first.size()!=second.size() ){
			return false;
		}
		else{
			for( int i=0; i<first.size(); i++ ){
				ProberResult firstElement = first.get( i );
				ProberResult secondElement = second.get( i );

				if( !firstElement.equals( secondElement ) ){
					return false;
				}
			}
			return true;
		}
	}

	private static SecureUserProfile toUserProfile( ProbeConfiguration config ){
		PartnerBadge partnerBadge = new PartnerBadge();
		partnerBadge.setSystemId( PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getSystemId() );
		partnerBadge.setQueryGeneratorClass( config.queryGeneratorClass );
		partnerBadge.setIsQueryExpansionEnabled( config.queryExpansionEnabled );
		partnerBadge.setIsQuerySplittingEnabled( config.querySplittingEnabled );

		SecureUserProfile userProfile = new SecureUserProfile();
		userProfile.contextKeywords.add( new ContextKeyword( config.keyword ) );
		userProfile.partnerList.add( partnerBadge );

		return userProfile;
	}
}