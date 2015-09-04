package eu.eexcess.partnerwizard.probe.controller;

import eu.eexcess.partnerwizard.probe.model.Pair;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-08-26
 */
public class TournamentPairGenerator<T> extends PairGenerator<T>{
	private final List<T> backup;
	private final Queue<T> input;
	private final Deque<T> output;
	private Pair<T> pair;


	public TournamentPairGenerator( Collection<T> elements ){
		if( elements==null||elements.isEmpty() ){
			throw new IllegalArgumentException( "Input collection must not be null or empty!" );
		}
		input = new ArrayDeque<>( elements );
		output = new ArrayDeque<>( elements.size() );
		backup = new ArrayList<>( elements );
	}


	@Override
	public boolean isNextPairAvailable(){
		return input.size()>1 && pair==null;
	}

	@Override
	public boolean isWaitingForStore(){
		return pair!=null;
	}

	@Override
	public Pair<T> nextPair(){
		if( input.size()>1 ){
			if( pair!=null ){
				throw new IllegalStateException( "Store must be called befor the next pair can be retrieved!" );
			}

			pair = new Pair<>( input.poll(), input.poll() );
		}
		else{
			pair = null;
		}

		return pair;
	}

	@Override
	public void storeElement(  boolean hasWinner, int elementPosition ){
		if( !hasWinner ){
			throw new IllegalArgumentException( "This implementation of requres a winner.");
		}
		if( pair==null ){
			throw new IllegalStateException( "No current pair to store! Call next element before calling store." );
		}
		if( elementPosition<0||elementPosition>1 ){
			throw new IllegalArgumentException( "elementPosition must be 0 or 1!" );
		}

		input.add( pair.getElement( elementPosition ) );
		elementPosition ^= 1;
		output.add( pair.getElement( elementPosition ) );

		pair = null;
	}

	@Override
	public List<T> getWinners( int numberOfElements ){
		if( input.size()>1 ){
			throw new IllegalStateException( "Winner is not decided jet!" );
		}
		if( numberOfElements<1 ){
			throw new IllegalArgumentException( "Argument must be grater or equal to one!" );
		}

		List<T> winners = new ArrayList<>( Math.min( numberOfElements, output.size()+1 ) );
		winners.add( input.peek() );

		numberOfElements--;

		Iterator<T> iterator = output.descendingIterator();
		while( numberOfElements>0 && iterator.hasNext() ){
			winners.add( iterator.next() );
			numberOfElements--;
		}

		return winners;
	}

	@Override
	public T getWinner(){
		if( input.size()>1 ){
			throw new IllegalStateException( "Winner is not decided jet!" );
		}

		return input.peek();
	}

	@Override
	public T getFirstOrWinner(){
		return input.peek();
	}

	@Override
	public void reset(){
		output.clear();
		input.clear();
		input.addAll( backup );
	}
}
