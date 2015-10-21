package eu.eexcess.partnerwizard.probe.controller;

import eu.eexcess.partnerwizard.probe.model.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-02
 */
public abstract class PairGenerator<T> {

	public abstract boolean isNextPairAvailable();

	public abstract boolean isWaitingForStore();

	public abstract Pair<T> nextPair();

	public abstract void storeElement( boolean hasWinner, int elementPosition );

	public abstract List<T> getWinners( int numberOfElements );

	public abstract T getWinner();

	public abstract T getFirstOrWinner();

	public abstract void reset();

	public static <T> ArrayList<T> removeDublicates( Collection<T> collection ){
		return  new ArrayList<T>(new LinkedHashSet<T>( collection ));
	}

}
