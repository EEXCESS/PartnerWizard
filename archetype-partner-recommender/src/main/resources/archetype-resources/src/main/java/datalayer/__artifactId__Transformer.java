/* Copyright (C) 2014
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
package ${package}.datalayer;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.query.QuerySolution;

import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.partnerdata.api.EEXCESSDataTransformationException;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerdata.reference.Transformer;

public class ${artifactId}Transformer extends Transformer{

	@Override
	protected Result postProcessResult(Document orgPartnerResult, Result result, QuerySolution querySol) {
		return result;
	}
	
	

	@Override
	public Document preProcessTransform(Document input, PartnerdataLogger logger)
			throws EEXCESSDataTransformationException {
		PartnerdataTracer.dumpFile(this.getClass(), partnerConfig, input, "before-transform-before-process", logger); 
		PartnerdataTracer.dumpFile(this.getClass(), partnerConfig, input, "before-transform-done-process", logger); 
		return input;
	}



	@Override
	protected ResultList postProcessResults(Document orgPartnerResult, ResultList resultList) {
		return resultList;
	}

}
