package eu.eexcess.partnerwizard.probe.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-01
 */
public class QueryOptions{
	public final Boolean expansion;
	public final Boolean splitting;


	private QueryOptions( Boolean expansion, Boolean splitting ){
		this.expansion = expansion;
		this.splitting = splitting;
	}


	public static List<QueryOptions> getQueryOptions( boolean enableExpansion, boolean enableSplitting ){
		List<QueryOptions> options = new ArrayList<>( 3 );

		options.add( new QueryOptions( Boolean.FALSE, Boolean.FALSE ) );
		if( enableExpansion ){
			options.add( new QueryOptions( Boolean.TRUE, Boolean.FALSE ) );
		}
		if( enableSplitting ){
			options.add( new QueryOptions( Boolean.FALSE, Boolean.TRUE ) );
		}

		return options;
	}

	@Override
	public int hashCode(){
		int hash = 7;
		hash = 47*hash+Objects.hashCode( this.expansion );
		hash = 47*hash+Objects.hashCode( this.splitting );
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
		final QueryOptions other = (QueryOptions) obj;
		if( !Objects.equals( this.expansion, other.expansion ) ){
			return false;
		}
		if( !Objects.equals( this.splitting, other.splitting ) ){
			return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return "QueryOptions{"+"expansion="+expansion+", splitting="+splitting+'}';
	}
}

