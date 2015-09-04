package eu.eexcess.partnerwizard.probe.model.web;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-04
 */
public abstract class ProberResponse {
	public enum State{
		Init, Next, Store, Done;
	}


	public final String id;
	public final State currentState;
	public final State nextState;


	protected ProberResponse( String id, State currentState, State nextState ){
		this.id = id;
		this.currentState = currentState;
		this.nextState = nextState;
	}


}
