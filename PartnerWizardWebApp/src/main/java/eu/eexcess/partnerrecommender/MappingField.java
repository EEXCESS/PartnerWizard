package eu.eexcess.partnerrecommender;

import java.io.Serializable;

public class MappingField implements Serializable{

	private String name="";
	private String description="";
	private String xPath = "";
	private String exampleValue = "";
	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getxPath() {
		return xPath;
	}
	public void setxPath(String xPath) {
		this.xPath = xPath;
	}
	public String getExampleValue() {
		return exampleValue;
	}
	public void setExampleValue(String exampleValue) {
		this.exampleValue = exampleValue;
	}

}
