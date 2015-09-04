package eu.eexcess.partnerwizard.probe.model.web;

import eu.eexcess.partnerwizard.probe.model.ProbeConfiguration;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-04
 */
public class ProberResponseDone extends ProberResponse {

	public ProbeConfiguration configuration;

	public ProberResponseDone( String id, State currentState, State nextState ){
		super( id, currentState, nextState );
	}
}
