package eu.eexcess.partnerwizard.probe.model.web;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-28
 */
@XmlRootElement(name="keyword")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProberKeyword {
	@XmlValue
	public String keyword;
	@XmlAttribute(name="main-topic")
	public boolean isMainTopic;


	public ProberKeyword(){
	}

	public ProberKeyword( String keyword, boolean isMainTopic ){
		this.keyword = keyword;
		this.isMainTopic = isMainTopic;
	}


	@Override
	public int hashCode(){
		int hash = 3;
		hash = 19*hash+Objects.hashCode( this.keyword );
		hash = 19*hash+(this.isMainTopic?1:0);
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
		final ProberKeyword other = (ProberKeyword) obj;
		if( !Objects.equals( this.keyword, other.keyword ) ){
			return false;
		}
		if( this.isMainTopic!=other.isMainTopic ){
			return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return "ProberKeyword{"+"keyword="+keyword+", isMainTopic="+isMainTopic+'}';
	}
}
