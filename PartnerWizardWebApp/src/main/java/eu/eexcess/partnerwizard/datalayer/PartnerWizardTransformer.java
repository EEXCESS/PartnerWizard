package eu.eexcess.partnerwizard.datalayer;

import org.w3c.dom.Document;

import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.partnerdata.api.EEXCESSDataTransformationException;
import eu.eexcess.partnerdata.api.ITransformer;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerdata.reference.Transformer;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PartnerWizardTransformer extends Transformer implements ITransformer {

    @Override
    protected ResultList postProcessResults(Document orgPartnerResult, ResultList resultList) {
        resultList.totalResults = resultList.results.size();
        return super.postProcessResults(orgPartnerResult, resultList);
    }

	@Override
	public Document preProcessTransform(Document input, PartnerdataLogger logger)  throws EEXCESSDataTransformationException{
		PartnerdataTracer.dumpFile(this.getClass(), partnerConfig, input, "before-transform-before-process", logger);

		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		try {
			NodeList itemsRootNode = (NodeList)xPath.evaluate("/o/items",
					input.getDocumentElement(), XPathConstants.NODESET);
			nodes = (NodeList)xPath.evaluate("/o/items/e",
					input.getDocumentElement(), XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength();i++) {
			    Element e = (Element) nodes.item(i);
			    NodeList itemFields = e.getChildNodes();
			    for (int j = 0; j < itemFields.getLength(); j++) {
					Node field = itemFields.item(j);
					if (field.getNodeName().equalsIgnoreCase("edmIsShownAt"))
					{
						if (!field.hasChildNodes())
						{
							itemsRootNode.item(0).removeChild(e);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		PartnerdataTracer.dumpFile(this.getClass(), partnerConfig, input, "before-transform-done-process", logger);
		return input;
	}
}
