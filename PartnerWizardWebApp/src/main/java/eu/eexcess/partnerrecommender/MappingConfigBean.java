package eu.eexcess.partnerrecommender;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;

public class MappingConfigBean implements Serializable{

	private Bean bean;
	
	public Bean getBean() {
		return bean;
	}
	public void setBean(Bean bean) {
		this.bean = bean;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -4524157484703075677L;
	private String searchEndpoint = "";
	private String searchEndpointSearchTerm = "";

	private ArrayList<MappingField> mappingFields = new ArrayList<MappingField>();
	private int actMappingFieldId = -1;
	
	private String eexcessFieldsXPathLoop = "";
	private String apiResponse ="";

	public String getEexcessFieldsXPathLoop() {
		return eexcessFieldsXPathLoop;
	}
	public void setEexcessFieldsXPathLoop(String eexcessFieldsXPathLoop) {
		this.eexcessFieldsXPathLoop = eexcessFieldsXPathLoop;
	}
	public String getApiResponse() {
		return apiResponse;
	}
	public void setApiResponse(String apiResponse) {
		this.apiResponse = apiResponse;
	}
	public String getSearchEndpoint() {
		return searchEndpoint;
	}
	public void setSearchEndpoint(String searchEndpoint) {
		this.searchEndpoint = searchEndpoint;
	}
	public String getSearchEndpointSearchTerm() {
		return searchEndpointSearchTerm;
	}
	public void setSearchEndpointSearchTerm(String searchEndpointSearchTerm) {
		this.searchEndpointSearchTerm = searchEndpointSearchTerm;
	}
	public ArrayList<MappingField> getMappingFields() {
		return mappingFields;
	}
	public void setMappingFields(ArrayList<MappingField> mappingFields) {
		this.mappingFields = mappingFields;
	}
	public int getActMappingFieldId() {
		return actMappingFieldId;
	}
	public void setActMappingFieldId(int actMappingFieldId) {
		this.actMappingFieldId = actMappingFieldId;
	} 
	public XMLTools getXmlTools() {
		if (this.xmlTools == null)
			this.xmlTools = new XMLTools();
		return xmlTools;
	}

	public void setXmlTools(XMLTools xmlTools) {
		this.xmlTools = xmlTools;
	}
	private XMLTools xmlTools = new XMLTools();
	public String getApiResponseFormated() {
		if ( this.apiResponse != null && !this.apiResponse.trim().isEmpty())
			return this.getXmlTools().format(apiResponse);
		return "";
	}


	public void probeXPath()
	{
		if (this.actMappingFieldId != -1)
		{
//			System.out.println("probeXPath called");
//			System.out.println("actMappingFieldId:" + this.actMappingFieldId);
//			System.out.println("actMappingField:xpath:" + this.getMappingFields().get(this.actMappingFieldId).getxPath());
			String fieldXPath = this.getMappingFields().get(this.actMappingFieldId).getxPath();
			if (fieldXPath == null || fieldXPath.trim().isEmpty()) return;
			if (eexcessFieldsXPathLoop == null || eexcessFieldsXPathLoop.trim().isEmpty()) return;
			
			String xpath = this.eexcessFieldsXPathLoop + fieldXPath;

//			System.out.println("xpath:" + xpath);

			Document apiResponseDoc = this.xmlTools.convertStringToDocument(this.apiResponse);
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodes;
			try {
				nodes = (NodeList)xPath.evaluate(xpath,
						apiResponseDoc.getDocumentElement(), XPathConstants.NODESET);
//				System.out.println("found:" + nodes.toString());
				ArrayList<String> values= new ArrayList<String>();
				for (int i = 0; i < nodes.getLength();i++) {
					values.add(nodes.item(i).getTextContent());
//					System.out.println("found:" + nodes.item(i).getTextContent());
				}
				this.getMappingFields().get(this.actMappingFieldId).setExampleValues(values);
			} catch (XPathExpressionException e1) {
				e1.printStackTrace();
			}

		}
	}
	public void callPartnerAPI()
	{
		try {
			PartnerConnectorApi partnerConnector = (PartnerConnectorApi) Class.forName("eu.eexcess.partnerrecommender.reference.PartnerConnectorBase").newInstance();
			PartnerConfiguration partnerConfiguration = PartnerConfigurationCache.CONFIG.getPartnerConfiguration();

			partnerConfiguration.setDetailEndpoint("");
			partnerConfiguration.setEnableEnriching(false);
			partnerConfiguration.setTransformedNative(false);
			partnerConfiguration.setMakeCleanupBeforeTransformation(false);
			//partnerConfiguration.partnerConnectorClass = "";
			//partnerConfiguration.queryGeneratorClass = "";
			partnerConfiguration.setSystemId(this.bean.getPartnerName());
			partnerConfiguration.setSearchEndpoint(this.searchEndpoint);
			SecureUserProfile userProfile = createUserProfile();
			PartnerdataLogger logger = null;
			Document response = partnerConnector.queryPartner(partnerConfiguration, userProfile, logger);
			this.apiResponse = this.xmlTools.getStringFromDocument(response);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	ArrayList<String> contextList = new ArrayList<String>();


	public SecureUserProfile createUserProfile() {
		this.contextList = new ArrayList<String>();
		this.contextList.add(this.searchEndpointSearchTerm);
		SecureUserProfile profile = new SecureUserProfile();
		profile.numResults = 40;
		profile.contextKeywords = new ArrayList<ContextKeyword>();

		for (int i = 0; i < contextList.size(); i++) {
			String actValue = contextList.get(i);
			ContextKeyword contextKeyword = new ContextKeyword();
			contextKeyword.text = actValue;
			contextKeyword.weight = 0.1;
			contextKeyword.reason = "manual";
			profile.contextKeywords.add(contextKeyword);
		}
		return profile;
	}

}
