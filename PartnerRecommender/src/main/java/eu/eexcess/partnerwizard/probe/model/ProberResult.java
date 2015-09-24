package eu.eexcess.partnerwizard.probe.model;

import java.util.Objects;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-09-22
 */
public class ProberResult {
	public final String title;
    public final String description;
	public final String previewImage;


	public ProberResult( String title, String description, String previewImage ){
		this.title = title;
		this.description = description;
		this.previewImage = previewImage;
	}


	@Override
	public int hashCode(){
		int hash = 3;
		hash = 47*hash+Objects.hashCode( this.title );
		hash = 47*hash+Objects.hashCode( this.description );
		hash = 47*hash+Objects.hashCode( this.previewImage );
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
		final ProberResult other = (ProberResult) obj;
		if( !Objects.equals( this.title, other.title ) ){
			return false;
		}
		if( !Objects.equals( this.description, other.description ) ){
			return false;
		}
		if( !Objects.equals( this.previewImage, other.previewImage ) ){
			return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return "ProberResult{"+"title="+title+", description="+description+", previewImage="+previewImage+'}';
	}
}
