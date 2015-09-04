package eu.eexcess.partnerwizard.probe.model.web;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-04
 */
public class ProberResponseInit extends ProberResponse {

	public ProberResponseInit( String id, State currentState, State nextState ){
		super( id, currentState, nextState );
	}
}
