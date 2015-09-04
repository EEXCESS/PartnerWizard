package eu.eexcess.partnerwizard.probe.model;

import java.lang.reflect.Array;
import java.util.Objects;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-02
 */
public class Pair<T>{
	public T first;
	public T second;

	public Pair(){

	}

	public Pair( T first, T second ){
		this.first = first;
		this.second = second;
	}


	public T getElement( int number ){
		if( number <0 || number>1){
			throw new IllegalArgumentException("Argument 'number' must be '0' or '1'!");
		}
		else if( number==0 ){
			return first;
		}
		else{
			return second;
		}
	}

	public void setElement( T value, int number ){
		if( number <0 || number>1){
			throw new IllegalArgumentException("Argument 'number' must be '0' or '1'!");
		}
		else if( number==0 ){
			first = value;
		}
		else{
			second = value;
		}
	}

	public T getOther( T element ){
		if( element.equals( first ) ){
			return second;
		}
		else if( element.equals( second ) ){
			return first;
		}
		else{
			throw new IllegalArgumentException("The specified element is not part of this pair.");
		}
	}

	public int getPosition( T element ){
		if( element.equals( first ) ){
			return 0;
		}
		else if( element.equals( second ) ){
			return 1;
		}
		else{
			throw new IllegalArgumentException("The specified element is not part of this pair.");
		}

	}

	public T[] toArray(){
		T[] array = (T[])Array.newInstance( first.getClass(), 2);
		array[0] = first;
		array[1] = second;

		return array;
	}

	public void setArray( T[] array ){
		if( array.length!=2 ){
			throw new IllegalArgumentException("Lengh of array must be equal to two!");
		}

		first = array[0];
		second = array[1];
	}

	@Override
	public int hashCode(){
		int hash = 7;
		hash = 71*hash+Objects.hashCode( this.first );
		hash = 71*hash+Objects.hashCode( this.second );
		return hash;
	}

	@Override
	public boolean equals( Object obj ){
		if( obj==null ){
			return false;
		}
		if( getClass()!=obj.getClass() ){
			return false;
		}
		final Pair<?> other = (Pair<?>) obj;
		if( !Objects.equals( this.first, other.first ) ){
			return false;
		}
		if( !Objects.equals( this.second, other.second ) ){
			return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return "Pair{"+"first="+first+", second="+second+'}';
	}

}
