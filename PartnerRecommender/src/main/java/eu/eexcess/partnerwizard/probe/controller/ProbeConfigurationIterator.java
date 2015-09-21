package eu.eexcess.partnerwizard.probe.controller;

import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerwizard.probe.model.Pair;
import eu.eexcess.partnerwizard.probe.model.ProbeConfiguration;
import eu.eexcess.partnerwizard.probe.model.ProbeStatus;
import eu.eexcess.partnerwizard.probe.model.QueryOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-08-21
 */
public class ProbeConfigurationIterator{
	ProbeStatus state;

	private final List<SecureUserProfile> keywords;
	private int currentKeyword;
	private final PairGenerator<String> generators;
	private final PairGenerator<QueryOptions> queryOptions;

	private final Map<String, Integer> generatorWinners;
	private final Map<QueryOptions, Integer> queryOptionsWinners;

	public ProbeConfigurationIterator( List<SecureUserProfile> keywords, List<String> generators, boolean enableExpansion, boolean enableSplitting ){
		this.keywords = keywords;
		this.currentKeyword = 0;
		this.generators = new CombinatorialPairGenerator<>( generators );
		List<QueryOptions> queryOptions = QueryOptions.getQueryOptions( enableExpansion, enableSplitting );
		this.queryOptions = new CombinatorialPairGenerator<>( queryOptions );

		this.generatorWinners = new HashMap<>( generators.size() );
		this.queryOptionsWinners = new HashMap<>( queryOptions.size() );

		state = ProbeStatus.Init;
		toNextState();
	}

	public boolean isNextPairAvailable(){
		return currentKeyword<keywords.size() && state.isNextState();
	}

	public boolean isWaitingForStore(){
		return state.isStoreState();
	}

	public boolean isDone(){
		return state.isDone();
	}

	public Pair<ProbeConfiguration> nextPair(){
		if( !isNextPairAvailable() ){
			throw new IllegalStateException( "No more next elements!" );
		}
		if( state.isStoreState() ){
			throw new IllegalStateException( "Last element must be stored before next element can be fetched!" );
		}

		Pair<ProbeConfiguration> configPair = new Pair<>();
		SecureUserProfile keyword = keywords.get( currentKeyword );

		if( state==ProbeStatus.GeneratorsNext ){
			Pair<String> generatorPair = generators.nextPair();
			QueryOptions queryOptionsFirstOrWinner = queryOptions.getFirstOrWinner();
			configPair.first = new ProbeConfiguration( keyword, generatorPair.first, queryOptionsFirstOrWinner );
			configPair.second = new ProbeConfiguration( keyword, generatorPair.second, queryOptionsFirstOrWinner );
			state = ProbeStatus.GeneratorsStore;
		}
		else if( state==ProbeStatus.QueryOptionsNext ){
			String generatorsFirstOrWinner = generators.getFirstOrWinner();
			Pair<QueryOptions> queryOptionsPair = queryOptions.nextPair();
			configPair.first = new ProbeConfiguration( keyword, generatorsFirstOrWinner, queryOptionsPair.first );
			configPair.second = new ProbeConfiguration( keyword, generatorsFirstOrWinner, queryOptionsPair.second );
			state = ProbeStatus.QueryOptionsStore;
		}
		else{
			throw new IllegalStateException( "Invalid state to generate a configuration!" );
		}

		return configPair;
	}

	public boolean storeResponse( boolean hasWinner, int responseNumber ){
		switch( state ){
			case GeneratorsStore:
				generators.storeElement( hasWinner, responseNumber );
				break;
			case QueryOptionsStore:
				queryOptions.storeElement( hasWinner, responseNumber );
				break;
			default:
				throw new IllegalStateException( "No element can be stored in \""+state+"\"-state." );
		}
		toNextState();

		return isNextPairAvailable();
	}

	public ProbeConfiguration getWinningConfiguration(){
		if( isNextPairAvailable() ){
			throw new IllegalStateException("Iteration of possible configurations is not finished yet. Hence, no winning configuration is available.");
		}

		String generator = getEntryWithMaxValue( generatorWinners );
		QueryOptions queryOptions = getEntryWithMaxValue( this.queryOptionsWinners );

		return new ProbeConfiguration( null, generator, queryOptions );
	}


	private void toNextState(){
		switch( state ){
			case Init:
			case GeneratorsStore:
				if( generators.isNextPairAvailable() ){
					state = ProbeStatus.GeneratorsNext;
					return;
				}

			case QueryOptionsStore:
				if( queryOptions.isNextPairAvailable() ){
					state = ProbeStatus.QueryOptionsNext;
					return;
				}
				else{
					putWinners( generators.getWinners( Integer.MAX_VALUE ), generatorWinners  );
					generators.reset();
					putWinners( queryOptions.getWinners( Integer.MAX_VALUE ), queryOptionsWinners  );
					queryOptions.reset();

					currentKeyword++;
					if( currentKeyword<keywords.size() ){
						state = ProbeStatus.Init;
						toNextState();
					}
					else{
						state = ProbeStatus.Done;
					}
				}
				return;
			default:
				throw new IllegalStateException( "No next state defined for '"+state.toString()+"'" );
		}
	}


	private static <T> T getEntryWithMaxValue(Map<T, Integer> map ){
		T key = null;
		int value = Integer.MIN_VALUE;

		for( Map.Entry<T, Integer> entry : map.entrySet() ){
			if( entry.getValue() > value ){
				value = entry.getValue();
				key = entry.getKey();
			}
		}

		return key;
	}

	private static <T> void putWinners(List<T> winners, Map<T, Integer> map ){
		int score = winners.size()-1;

		for( T winner : winners ){
			Integer oldScore = map.get( winner );
			if( oldScore==null ){
				map.put( winner, score );
			}
			else{
				map.put( winner, score+oldScore );
			}
			score--;
		}
	}
}
