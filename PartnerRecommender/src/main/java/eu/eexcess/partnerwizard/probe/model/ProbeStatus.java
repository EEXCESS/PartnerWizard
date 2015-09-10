package eu.eexcess.partnerwizard.probe.model;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-01
 */
public enum ProbeStatus{
	Init, GeneratorsNext, GeneratorsStore, QueryOptionsNext, QueryOptionsStore, Done;

	private static final ProbeStatus[] values = values();
	private static final int maxValue = Done.ordinal();

	public ProbeStatus next(){
		int newOrdinal = Math.max( this.ordinal()+1, maxValue );
		return values[newOrdinal];
	}

	public boolean isDone(){
		return this.equals( Done );
	}

	public boolean isStoreState(){
		return this==GeneratorsStore || this==QueryOptionsStore;
	}

	public boolean isNextState(){
		return this==GeneratorsNext || this==QueryOptionsNext;
	}
}
