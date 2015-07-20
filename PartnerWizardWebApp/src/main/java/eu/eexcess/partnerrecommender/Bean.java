package eu.eexcess.partnerrecommender;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;

@ManagedBean
@SessionScoped
public class Bean implements Serializable {

    private static final long serialVersionUID = -2403138958014741653L;

    private String groupId = "";
    private String artifactId ="";
    private String version = ""; 
    private String packageStr = "";
	private String partnerName = "";
	
	private String buildCMD="";
	
	private String buildCMDHTML="";
	
	private String searchEndpoint = "";
	
	private String eexcessFieldsXPathLoop = "";
	private String apiResponse ="";
	private String apiResponseHTML = "";
	private ArrayList<MappingField> mappingFields = new ArrayList<MappingField>();
	private int actMappingFieldId = -1; 
	
	public int getActMappingFieldId() {
		return actMappingFieldId;
	}

	public void setActMappingFieldId(int actMappingFieldId) {
		this.actMappingFieldId = actMappingFieldId;
	}


	private XMLTools xmlTools = new XMLTools();
	
	public void probeXPath()
	{
		if (this.actMappingFieldId != -1)
		{
			System.out.println("probeXPath called");
			System.out.println("actMappingFieldId:" + this.actMappingFieldId);
			System.out.println("actMappingField:xpath:" + this.getMappingFields().get(this.actMappingFieldId).getxPath());
			String fieldXPath = this.getMappingFields().get(this.actMappingFieldId).getxPath();
			if (fieldXPath == null || fieldXPath.trim().isEmpty()) return;
			if (eexcessFieldsXPathLoop == null || eexcessFieldsXPathLoop.trim().isEmpty()) return;
			String xpath = this.eexcessFieldsXPathLoop + fieldXPath;
			
			System.out.println("xpath:" + xpath);
			
			Document apiResponseDoc = this.xmlTools.convertStringToDocument(this.apiResponse);
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodes;
			try {
				nodes = (NodeList)xPath.evaluate(xpath,
						apiResponseDoc.getDocumentElement(), XPathConstants.NODESET);
			    System.out.println("found:" + nodes.toString());
			    String values= "";
				for (int i = 0; i < nodes.getLength();i++) {
					values += nodes.item(i).getTextContent() + "\n";
				    System.out.println("found:" + nodes.item(i).getTextContent());
				}
				this.getMappingFields().get(this.actMappingFieldId).setExampleValue(values);
			} catch (XPathExpressionException e1) {
				e1.printStackTrace();
			}

		}
	}
	
	public String getEexcessFieldsXPathLoop() {
		return eexcessFieldsXPathLoop;
	}

	public void setEexcessFieldsXPathLoop(String eexcessFieldsXPathLoop) {
		this.eexcessFieldsXPathLoop = eexcessFieldsXPathLoop;
	}
	
	public XMLTools getXmlTools() {
		if (this.xmlTools == null)
			this.xmlTools = new XMLTools();
		return xmlTools;
	}

	public void setXmlTools(XMLTools xmlTools) {
		this.xmlTools = xmlTools;
	}

	public String getSearchEndpoint() {
		return searchEndpoint;
	}

	public void setSearchEndpoint(String searchEndpoint) {
		this.searchEndpoint = searchEndpoint;
	}

	public String getBuildCMDHTML() {
		return buildCMDHTML;
	}

	public void setBuildCMDHTML(String buildCMDHTML) {
		this.buildCMDHTML = buildCMDHTML;
	}



    public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getApiResponse() {
		return apiResponse;
	}

	public String getApiResponseFormated() {
		return this.getXmlTools().format(apiResponse);
	}
	
	public void setApiResponse(String apiResponse) {
		this.apiResponse = apiResponse;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPackageStr() {
		return packageStr;
	}

	public void setPackageStr(String packageStr) {
		this.packageStr = packageStr;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}


    public Bean() {
    	initMappingFields();
    	defaultTestValues();
    }
    
    private void defaultTestValues()
    {
    	this.eexcessFieldsXPathLoop = "/response/result/doc";
    	this.apiResponse ="<?xml version=\"1.0\" encoding=\"UTF-8\"?> <response> <lst name=\"responseHeader\"> <int name=\"status\">0</int> <int name=\"QTime\">0</int> <lst name=\"params\"> <str name=\"q\">uuid:aa0b5559-6e86-46db-9785-0329ab800956</str> </lst> </lst> <result name=\"response\" numFound=\"1\" start=\"0\"> <doc> <str name=\"_participant_\">museum-pro-muttenz</str> <arr name=\"alte_inventarnummern\"> <str>30.0101</str> </arr> <str name=\"objekttyp\">Fotografie</str> <float name=\"anzahl\">1.0</float> <str name=\"beschreibung\">Mitte: das Schulhaus Hinterzweien mit der Turnhalle; rechts oben: die katholische Kirche.</str> <str name=\"datentraeger\">Farb-Positiv</str> <str name=\"copyright\">Museen Muttenz</str> <arr name=\"klassifikation_sachgruppe\"> <str>- Fotografie / Ortsbild / Quartier - Architektur / Öffentliche Bauten / Schulhaus, Kindergarten - Architektur / Öffentliche Bauten / Kirchliche Baute und Nebenbaute - Fotografie / Luftbild </str></arr> <arr name=\"person_name_fotograf\"> <str>SP Luftbild AG Möhlin</str> </arr><str name=\"inventarnummer\">Mz 000068</str> </doc> </result> </response>";
    	this.searchEndpoint = "https://kgapi.bl.ch/solr/kim-portal.objects/select/xml?q=_fulltext_:${query}&rows=${numResults}";
        this.groupId = "at.joanneum";
        this.artifactId ="MyPartnerRecommender";
        this.version = "1.0-SNAPSHOT"; 
        this.packageStr = "at.joanneum";
    	this.partnerName = "Joanneum PartnerRecommender";
    	this.getMappingFields().get(1).setxPath("/str[@name='datentraeger']");

    }

	private void initMappingFields() {
		this.mappingFields = new ArrayList<MappingField>();
    	MappingField mappingField = new MappingField();
    	mappingField.setName("ID");
    	mappingField.setDescription("identifier");
    	this.mappingFields.add(mappingField);
    	mappingField = new MappingField();
    	mappingField.setName("Title");
    	mappingField.setDescription("title");
    	this.mappingFields.add(mappingField);
    	mappingField = new MappingField();
    	mappingField.setName("Description");
    	mappingField.setDescription("description of the item");
    	this.mappingFields.add(mappingField);
    	for (int i = 0; i < mappingFields.size(); i++) {
    		this.mappingFields.get(i).setId(i);
		}
	}
    
    public ArrayList<MappingField> getMappingFields() {
		return mappingFields;
	}

	public void setMappingFields(ArrayList<MappingField> mappingFields) {
		this.mappingFields = mappingFields;
	}

	public void callPartnerAPI()
    {
//    	String api = this.searchEndpoint;
//    	api.replaceAll("${query}", "graz");
		try {
			PartnerConnectorApi partnerConnector = (PartnerConnectorApi) Class.forName("eu.eexcess.partnerrecommender.reference.PartnerConnectorBase").newInstance();
			PartnerConfiguration partnerConfiguration = PartnerConfigurationCache.CONFIG.getPartnerConfiguration();

			partnerConfiguration.detailEndpoint = "";
			partnerConfiguration.enableEnriching = false;
			partnerConfiguration.isTransformedNative = false;
			partnerConfiguration.makeCleanupBeforeTransformation = false;
			partnerConfiguration.partnerConnectorClass = "";
			partnerConfiguration.queryGeneratorClass = "";
			partnerConfiguration.systemId = this.partnerName;
			SecureUserProfile userProfile = null;
			PartnerdataLogger logger = null;
			Document response = partnerConnector.queryPartner(partnerConfiguration, userProfile, logger);
			this.apiResponse = this.xmlTools.getStringFromDocument(response);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public void buildPR()
    {
    	this.buildCMDHTML = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    	this.buildCMD = "mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=eu.eexcess -DarchetypeArtifactId=eexcess-partner-recommender-archetype -DinteractiveMode=false " 
    			+ "-DgroupId=" + this.groupId 
    			+ " -DartifactId="+ this.artifactId 
    			+ " -Dversion="+this.version 
    			+ " -Dpackage="+ this.packageStr 
    			+ " -DpartnerName=\""+ this.partnerName+"\"";
    	
    	//System.out.println(this.executeCommand(this.buildCMD));
    	String[] commands = new String[6];
    	commands[0] = "set PATH=%PATH%;C:\\java\\jdk1.8.0_25\\bin\\;C:\\java\\apache-maven-3.2.3\\bin";
    	commands[1] = "cd C:\\dev\\eexcess-partnerrecommender-archetype-sandbox\\ ";
    	commands[2] = this.buildCMD;
    	commands[3] = "cd " + this.artifactId;
    	commands[4] = "mvn clean install -DskipTests";
    	commands[5] = "mvn eclipse:eclipse -DdownloadSources=true -DdownloadJavadocs=true ";
    	this.cmdExecute(commands);
    	this.buildCMDHTML += "\n" + this.buildCMD + "\n" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

    	System.out.println("finished....");
    }

	public String getBuildCMD() {
		return buildCMD;
	}

	public void setBuildCMD(String buildCMD) {
		this.buildCMD = buildCMD;
	}

	
	private String executeCommand(String command) {
		 
		StringBuffer output = new StringBuffer();
 
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));
 
                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		return output.toString();
 
	}
	
	
	public static void cmdExecute(String[] commands) {
	    Process shell = null;
	    DataOutputStream out = null;
	    BufferedReader in = null;

	    try {
	        shell = Runtime.getRuntime().exec("cmd");//su if needed
	        out = new DataOutputStream(shell.getOutputStream());

	        in = new BufferedReader(new InputStreamReader(shell.getInputStream()));

	        // Executing commands without root rights
	        for (String command : commands) {
	        	System.out.println("executing:\n" + command);
	            out.writeBytes(command + "\n");
	            out.flush();
	        }

	        out.writeBytes("exit\n");
	        out.flush();
	        String line;
	        StringBuilder sb = new StringBuilder();
	        while ((line = in.readLine()) != null) {
	            sb.append(line).append("\n");
	        }
	        shell.waitFor();

	    } catch (Exception e) {
	    } finally {
	        try {
	            if (out != null) {
	                out.close();
	            }
	            if(in != null){
	                in.close();
	            }
	            // shell.destroy();
	        } catch (Exception e) {
	            // hopeless
	        }
	    }
	}
}
