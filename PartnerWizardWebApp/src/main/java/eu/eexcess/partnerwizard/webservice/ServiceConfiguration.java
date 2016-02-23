package eu.eexcess.partnerwizard.webservice;

import eu.eexcess.partnerwizard.probe.model.web.ProberKeyword;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2016-02-23
 */
@XmlRootElement(name="partnerwizard-configuration")
@XmlAccessorType (XmlAccessType.FIELD)
public class ServiceConfiguration {
	@XmlElementWrapper( name="query-generators" )
	@XmlElement(name="query-generator")
	public List<String> queryGenerators;

	@XmlElementWrapper( name="prober-queries" )
	@XmlElement( name="prober-query" )
	public List<ProberKeywordArray> proberQueries;

	@XmlRootElement(name="keyword")
	public static class ProberKeywordArray{
		public ProberKeywordArray(){
		}

		public ProberKeywordArray( ProberKeyword[] proberQueries ){
			this.proberQueries = proberQueries;
		}

		@XmlElement(name = "keyword")
		ProberKeyword[] proberQueries;
	}

	public List<ProberKeyword[]> getProberKeywords(){
		List<ProberKeyword[]> allKeywords = new ArrayList<>( proberQueries.size() );
		for( ProberKeywordArray array : proberQueries ){
			allKeywords.add( array.proberQueries );
		}

		return allKeywords;
	}

}
