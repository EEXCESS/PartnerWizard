package eu.eexcess.partnerwizard.probe.controller;

import eu.eexcess.partnerwizard.probe.model.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-03
 */
public class CombinatorialPairGenerator<T> extends PairGenerator<T>{
	private boolean done;
	private final T[] elements;
	private T[] sortedElements;
	private Pair<T> pair;
	private int firstIndex;
	private int secondIndex;
	private final Map<T, Integer> scores;
	private final Map<T, Integer> positions;


	public CombinatorialPairGenerator( Collection<T> elements ){
		if( elements==null||elements.isEmpty() ){
			throw new IllegalArgumentException( "Input Collection must not be null or empty." );
		}
		elements = PairGenerator.removeDublicates( elements );

		this.elements = (T[]) elements.toArray();
		if( this.elements.length==1 ){
			done = true;
			sortedElements = Arrays.copyOf( this.elements, 1 );
		}
		else{
			this.sortedElements = null;
			this.done = false;
		}

		this.pair = null;

		this.scores = new HashMap<>( this.elements.length );
		this.positions = new HashMap<>( this.elements.length );
		int i = 0;
		for( T element: elements ){
			scores.put( element, 0 );
			positions.put( element, i );
			i++;
		}

		firstIndex = 0;
		secondIndex = 0;
		toNextIndex();
	}


	@Override
	public boolean isNextPairAvailable(){
		return !done&&pair==null;
	}

	@Override
	public boolean isWaitingForStore(){
		return pair!=null;
	}

	@Override
	public Pair<T> nextPair(){
		if( done ){
			throw new IllegalStateException( "Iteration is finished no more pairs available." );
		}
		if( pair!=null ){
			throw new IllegalStateException( "The result of the last pair needs to be stored before a new pair can be provided." );
		}

		pair = new Pair<>( elements[firstIndex], elements[secondIndex] );

		return pair;
	}

	@Override
	public void storeElement( boolean hasWinner, int elementPosition ){
		if( pair==null ){
			throw new IllegalStateException( "No current pair to store! Call next element before calling store." );
		}
		if( hasWinner ){
			if( (elementPosition<0||elementPosition>1) ){
				throw new IllegalArgumentException( "If parameter 'hasWinner' is ture, elementPosition must be 0 or 1!" );
			}

			T element = pair.getElement( elementPosition );
			int newScore = scores.get( element )+1;
			scores.put( element, newScore );
		}

		toNextIndex();
		pair = null;
	}

	@Override
	public List<T> getWinners( int numberOfElements ){
		if( !done ){
			throw new IllegalStateException( "Winner is not decided jet!" );
		}
		if( numberOfElements<1 ){
			throw new IllegalArgumentException( "Argument must be grater or equal to one!" );
		}

		if( sortedElements==null ){
			generateSortedElements();
		}

		return Arrays.asList( Arrays.copyOf( sortedElements, Math.min( sortedElements.length, numberOfElements ) ) );
	}

	@Override
	public T getWinner(){
		if( !done ){
			throw new IllegalStateException( "Winner is not decided jet!" );
		}
		if( sortedElements==null ){
			generateSortedElements();
		}
		return sortedElements[0];
	}

	@Override
	public T getFirstOrWinner(){
		if( done ){
			if( sortedElements==null ){
				generateSortedElements();
			}
			return sortedElements[0];
		}
		else{
			return elements[0];
		}
	}

	@Override
	public void reset(){
		this.sortedElements = null;
		this.done = false;
		this.pair = null;

		for( T element: elements ){
			scores.put( element, 0 );
		}

		firstIndex = 0;
		secondIndex = 0;
		toNextIndex();
	}

	private void toNextIndex(){
		secondIndex++;
		if( secondIndex>=elements.length ){
			firstIndex++;
			if( firstIndex>elements.length-2 ){
				done = true;
				firstIndex--;
				secondIndex--;
			}
			else{
				secondIndex = firstIndex+1;
			}
		}
	}


	private void generateSortedElements(){
		sortedElements = Arrays.copyOf( elements, elements.length );

		Arrays.sort( sortedElements, ( T element1, T element2 ) -> {
			int ordinal1 = this.scores.get( element1 );
			int ordinal2 = this.scores.get( element2 );
			if( ordinal1==ordinal2 ){
				ordinal1 = this.positions.get( element1 );
				ordinal2 = this.positions.get( element2 );
			}

			return Integer.compare( ordinal2, ordinal1 );
		} );
	}
}
