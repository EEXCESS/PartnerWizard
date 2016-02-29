package eu.eexcess.partnerwizard.probe.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.partnerwizard.probe.model.web.ProberKeyword;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-08-27
 */
public class ProbeConfiguration {
	@JsonInclude(Include.NON_NULL)
	public final ProberKeyword[] keywords;
	public final String queryGeneratorClass;
	public final Boolean queryExpansionEnabled;
	public final Boolean querySplittingEnabled;


	public ProbeConfiguration( ProberKeyword[] keywords, String queryGeneratorClass, Boolean queryExpansionEnabled, Boolean querySplittingEnabled ){
		this.keywords = keywords;
		this.queryGeneratorClass = queryGeneratorClass;
		this.queryExpansionEnabled = queryExpansionEnabled;
		this.querySplittingEnabled = querySplittingEnabled;
	}

	public ProbeConfiguration( ProberKeyword[] keywords, String queryGeneratorClass, QueryOptions queryOptions ){
		this.keywords = keywords;
		this.queryGeneratorClass = queryGeneratorClass;
		this.queryExpansionEnabled = queryOptions.expansion;
		this.querySplittingEnabled = queryOptions.splitting;
	}


	public List<ContextKeyword> toContextKeywords(){
		List<ContextKeyword> contextKeywords = new ArrayList<>( keywords.length );

		for( ProberKeyword keyword : keywords ){
			ContextKeyword contextKeyword = new ContextKeyword( keyword.keyword );
			contextKeyword.setIsMainTopic( keyword.isMainTopic );

			contextKeywords.add( contextKeyword );
		}

		return contextKeywords;
	}

	@Override
	public int hashCode(){
		int hash = 3;
		hash = 19*hash+Objects.hashCode( this.keywords );
		hash = 19*hash+Objects.hashCode( this.queryGeneratorClass );
		hash = 19*hash+Objects.hashCode( this.queryExpansionEnabled );
		hash = 19*hash+Objects.hashCode( this.querySplittingEnabled );
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
		if( !Objects.equals( this.keywords, other.keywords ) ){
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
		return "ProbeConfiguration{"+"keywords="+Arrays.toString( keywords )+", queryGeneratorClass="+queryGeneratorClass+", queryExpansionEnabled="+queryExpansionEnabled+", querySplittingEnabled="+querySplittingEnabled+'}';
	}
}
