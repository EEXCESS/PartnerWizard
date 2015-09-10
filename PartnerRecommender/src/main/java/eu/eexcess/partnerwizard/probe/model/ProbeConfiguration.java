package eu.eexcess.partnerwizard.probe.model;

import java.util.Objects;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-08-27
 */
public class ProbeConfiguration {
	public final String keyword;
	public final String queryGeneratorClass;
	public final Boolean queryExpansionEnabled;
	public final Boolean querySplittingEnabled;


	public ProbeConfiguration( String keyword, String queryGeneratorClass, Boolean queryExpansionEnabled, Boolean querySplittingEnabled ){
		this.keyword = keyword;
		this.queryGeneratorClass = queryGeneratorClass;
		this.queryExpansionEnabled = queryExpansionEnabled;
		this.querySplittingEnabled = querySplittingEnabled;
	}

	public ProbeConfiguration( String keyword, String queryGeneratorClass, QueryOptions queryOptions ){
		this.keyword = keyword;
		this.queryGeneratorClass = queryGeneratorClass;
		this.queryExpansionEnabled = queryOptions.expansion;
		this.querySplittingEnabled = queryOptions.splitting;
	}


	@Override
	public int hashCode(){
		int hash = 7;
		hash = 89*hash+Objects.hashCode( this.keyword );
		hash = 89*hash+Objects.hashCode( this.queryGeneratorClass );
		hash = 89*hash+Objects.hashCode( this.queryExpansionEnabled );
		hash = 89*hash+Objects.hashCode( this.querySplittingEnabled );
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
		final ProbeConfiguration other = (ProbeConfiguration) obj;
		if( !Objects.equals( this.keyword, other.keyword ) ){
			return false;
		}
		if( !Objects.equals( this.queryGeneratorClass, other.queryGeneratorClass ) ){
			return false;
		}
		if( !Objects.equals( this.queryExpansionEnabled, other.queryExpansionEnabled ) ){
			return false;
		}
		if( !Objects.equals( this.querySplittingEnabled, other.querySplittingEnabled ) ){
			return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return "ProbeConfiguration{"+"keyword="+keyword+", queryGeneratorClass="+queryGeneratorClass+", queryExpansionEnabled="+queryExpansionEnabled+", querySplittingEnabled="+querySplittingEnabled+'}';
	}

}
