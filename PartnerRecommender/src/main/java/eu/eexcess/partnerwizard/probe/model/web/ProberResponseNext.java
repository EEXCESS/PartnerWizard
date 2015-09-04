package eu.eexcess.partnerwizard.probe.model.web;

import eu.eexcess.dataformats.result.Result;
import java.util.List;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-02
 */
public class ProberResponseNext extends ProberResponse {
	public String keywords;
	public List<Result> firstList;
	public List<Result> secondList;


	public ProberResponseNext( String id, State currentState, State nextState ){
		super( id, currentState, nextState );
	}
}
