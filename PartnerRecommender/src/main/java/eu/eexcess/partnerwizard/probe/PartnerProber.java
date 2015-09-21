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
import eu.eexcess.partnerwizard.probe.model.web.ProberResponseNext;
import eu.eexcess.partnerwizard.probe.model.Pair;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponse.State;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponseDone;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponseInit;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponseStore;
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


	public ProberResponseInit init( List<SecureUserProfile> keywords ){
		List<String> generators = testWorkingGeneratorClasses( keywords );
		String id = getId();

		ProbeConfigurationIterator iterator = new ProbeConfigurationIterator( keywords, generators, true, true );
		configs.put( id, iterator );


		State nextState;
		if( iterator.isDone() ){
			nextState = State.Done;
		}
		else{
			nextState = State.Next;
		}

		return  new ProberResponseInit(id, State.Init, nextState );
	}

	public ProberResponseNext next( String id ){
		ProbeConfigurationIterator iterator = configs.get( id );
		if( iterator==null ){
			throw new IllegalArgumentException("Unknown Id");
		}
		else {
			Pair<ProbeConfiguration> probeConfigs = iterator.nextPair();

			FutureTask<List<Result>> fristResponse = new FutureTask<>( () -> {
				ProbeConfiguration config = probeConfigs.first;
				return recommender.recommend( toUserProfile( config ) ).results;
			});
			executorService.submit( fristResponse );

			FutureTask<List<Result>> secondResponse = new FutureTask<>( () -> {
				ProbeConfiguration config = probeConfigs.second;
				return recommender.recommend( toUserProfile( config ) ).results;
			});
			executorService.submit( secondResponse );


			try{
				ProberResponseNext response = new ProberResponseNext(id, State.Next, State.Store);
				StringBuilder builder = new StringBuilder();

				probeConfigs.first.keyword.contextKeywords.forEach((kw)->{builder.append(kw.text +" ");});
				response.keywords = builder.toString();
				response.firstList = fristResponse.get();
				response.secondList = secondResponse.get();

				return response;
			}
			catch( InterruptedException|ExecutionException ex ){
				LOGGER.log( Level.SEVERE, "Query was interrupted ", ex );
				ProberResponseNext response = new ProberResponseNext(id, State.Next, State.Next);
				response.keywords = "ERROR: An Excpetion happend!";

				return response;
			}
		}
	}

	public ProberResponseStore store( String id, boolean hasWinner, int result ){
		ProbeConfigurationIterator iterator = configs.get( id );
		if( iterator==null ){
			throw new IllegalArgumentException("Unknown Id");
		}
		else {
			boolean hasNext = iterator.storeResponse( hasWinner, result );

			if(hasNext){
				return new ProberResponseStore(id, State.Store, State.Next );
			}
			else{
				return new ProberResponseStore(id, State.Store, State.Done );
			}
		}
	}

	public ProberResponseDone getConfiguration( String id ){
		ProbeConfigurationIterator iterator = configs.get( id );
		if( iterator==null ){
			throw new IllegalArgumentException("Unknown Id");
		}
		else{
			ProberResponseDone response = new ProberResponseDone(id, State.Done, State.Done);
			response.configuration = iterator.getWinningConfiguration();
			
			return response;
		}
	}


	private List<String> testWorkingGeneratorClasses( List<SecureUserProfile> keywords ){
		Map<String, Integer> generatorResults = Collections.synchronizedMap( new HashMap<String, Integer>( DEFAULT_GENERATORS.length ) );
		List<FutureTask<Void>> tasks = new ArrayList<>( DEFAULT_GENERATORS.length*keywords.size() );
		for( SecureUserProfile keyword: keywords ){
			for( String generatorClass: DEFAULT_GENERATORS ){
				FutureTask<Void> recommenderTask = new FutureTask<>( () -> {
					ProbeConfiguration config = new ProbeConfiguration( keyword, generatorClass, Boolean.FALSE, Boolean.FALSE );
					SecureUserProfile userProfile = keyword;//toUserProfile( config );

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
			catch( InterruptedException | ExecutionException ex ){
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

	private synchronized String getId(){
		idCounter++;
		return ID_Prefix+idCounter;
	}

	private static SecureUserProfile toUserProfile( ProbeConfiguration config ){
		PartnerBadge partnerBadge = new PartnerBadge();
		partnerBadge.setSystemId( PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getSystemId() );
		partnerBadge.setQueryGeneratorClass( config.queryGeneratorClass );
		partnerBadge.setIsQueryExpansionEnabled( config.queryExpansionEnabled );
		partnerBadge.setIsQuerySplittingEnabled( config.querySplittingEnabled );
		
//		S new SecureUserProfile();
//		userProfile.contextKeywords.add( new ContextKeyword( config.keyword ) );
//		userProfile.partnerList.add( partnerBadge );

		return config.keyword;
	}

}
