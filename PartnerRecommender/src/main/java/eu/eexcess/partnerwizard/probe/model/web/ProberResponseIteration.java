package eu.eexcess.partnerwizard.probe.model.web;

import eu.eexcess.partnerwizard.probe.model.ProberResult;
import java.util.List;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-02
 */
public class ProberResponseIteration extends ProberResponse{
	public String keywords;
	public List<ProberResult> firstList;
	public List<ProberResult> secondList;

	public ProberResponseIteration( String id, State nextState ){
		super( id, nextState );
	}
}
