/*
Copyright (C) 2015
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package eu.eexcess.partnerrecommender;

import java.io.Serializable;
import java.util.ArrayList;

public class MappingField implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -404101504030651302L;
	private String name="";
	private String description="";
	private String xPath = "";
	private ArrayList<String> exampleValues;
	
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
	public ArrayList<String> getExampleValues() {
		return exampleValues;
	}
	public void setExampleValues(ArrayList<String> exampleValues) {
		this.exampleValues = exampleValues;
	}

}
