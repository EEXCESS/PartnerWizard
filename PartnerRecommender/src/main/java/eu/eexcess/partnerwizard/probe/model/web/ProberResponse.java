package eu.eexcess.partnerwizard.probe.model.web;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-04
 */
public class ProberResponse {
	public enum State{
		Init, Iteration, Done, Error;
	}


	public final String id;
	public final State nextState;


	public ProberResponse( String id, State nextState ){
		this.id = id;
		this.nextState = nextState;
	}

}
