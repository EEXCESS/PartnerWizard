package eu.eexcess.partnerrecommender;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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

@ManagedBean
@SessionScoped
public class Bean implements Serializable {

	private final String PATH_BUILD_SANDBOX = "C:\\dev\\eexcess-partnerrecommender-archetype-sandbox\\";

	private static final long serialVersionUID = -2403138958014741653L;

	private String groupId = "";
	private String artifactId ="";
	private String version = ""; 
	private String packageStr = "";
	private String partnerName = "";
	private String partnerURL = "";
	private String dataLicense = "";

	public String getDataLicense() {
		return dataLicense;
	}

	public void setDataLicense(String dataLicense) {
		this.dataLicense = dataLicense;
	}

	public String getPartnerURL() {
		return partnerURL;
	}

	public void setPartnerURL(String partnerURL) {
		this.partnerURL = partnerURL;
	}


	private String buildCMD="";

	private String buildOutput="";

	public String getBuildOutput() {
		return buildOutput;
	}

	public void setBuildOutput(String buildOutput) {
		this.buildOutput = buildOutput;
	}


	private String searchEndpoint = "";
	private String searchEndpointSearchTerm = "";

	private String eexcessFieldsXPathLoop = "";
	private String apiResponse ="";

	private ArrayList<MappingField> mappingFields = new ArrayList<MappingField>();
	private int actMappingFieldId = -1; 

	public int getActMappingFieldId() {
		return actMappingFieldId;
	}

	public void setActMappingFieldId(int actMappingFieldId) {
		this.actMappingFieldId = actMappingFieldId;
	}

	public String getSearchEndpointSearchTerm() {
		return searchEndpointSearchTerm;
	}

	public void setSearchEndpointSearchTerm(String searchEndpointSearchTerm) {
		this.searchEndpointSearchTerm = searchEndpointSearchTerm;
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
		if ( this.apiResponse != null && !this.apiResponse.trim().isEmpty())
			return this.getXmlTools().format(apiResponse);
		return "";
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
		this.searchEndpoint = "https://kgapi.bl.ch/solr/kim-portal.objects/select/xml?q=_fulltext_:${query}&rows=${numResults}";
		this.groupId = "at.joanneum";
		this.artifactId ="MyPartnerRecommender";
		this.version = "1.0-SNAPSHOT"; 
		this.packageStr = "at.joanneum";
		this.partnerName = "Joanneum Partner Recommender";
		this.partnerURL = "http://example.org/";
		this.dataLicense ="http://creativecommons.org/licenses/by-nc-sa/4.0/";
		
		this.searchEndpointSearchTerm="Basel";
		this.getMappingFields().get(0).setxPath("/str[@name='uuid']");
		this.getMappingFields().get(1).setxPath("/str[@name='_display_']");
		this.getMappingFields().get(2).setxPath("/str[@name='beschreibung']");
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
		try {
			PartnerConnectorApi partnerConnector = (PartnerConnectorApi) Class.forName("eu.eexcess.partnerrecommender.reference.PartnerConnectorBase").newInstance();
			PartnerConfiguration partnerConfiguration = PartnerConfigurationCache.CONFIG.getPartnerConfiguration();

			partnerConfiguration.setDetailEndpoint("");
			partnerConfiguration.setEnableEnriching(false);
			partnerConfiguration.setTransformedNative(false);
			partnerConfiguration.setMakeCleanupBeforeTransformation(false);
			//partnerConfiguration.partnerConnectorClass = "";
			//partnerConfiguration.queryGeneratorClass = "";
			partnerConfiguration.setSystemId(this.partnerName);
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


	public void generatePR()
	{
		this.buildCMD = "mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=eu.eexcess -DarchetypeArtifactId=eexcess-partner-recommender-archetype -DinteractiveMode=false " 
				+ "-DgroupId=" + this.groupId 
				+ " -DartifactId="+ this.artifactId 
				+ " -Dversion="+this.version 
				+ " -Dpackage="+ this.packageStr 
				+ " -DpartnerName=\""+ this.partnerName+"\""
				+ " -DpartnerURL=\""+ this.partnerURL+"\""
				+ " -DdataLicense=\""+ this.dataLicense+"\""
				+ " -DpartnerAPIsearchTerm=\""+ this.searchEndpointSearchTerm+"\""
				+ " -DeexcessMappingFieldsLoopXPath=\""+ this.eexcessFieldsXPathLoop+"\"";
		for (int i = 0; i < this.mappingFields.size(); i++) {
			if (this.mappingFields.get(i).getxPath() != null && !this.mappingFields.get(i).getxPath().trim().isEmpty())
				this.buildCMD += " -DeexcessMappingFieldsXPath"+this.mappingFields.get(i).getName()+"=\""+ this.mappingFields.get(i).getxPath()+"\"";
		}

		ArrayList<String> commands = new ArrayList<String>();
		commands.add(buildENVsetup());
		commands.add(buildENVgotoSandbox());
		commands.add("rd " + this.artifactId + " /s /Q");
		commands.add(this.buildCMD);
		commands.add("cd " + this.artifactId);
		commands.add("mvn clean install -DskipTests");
		commands.add("mvn eclipse:eclipse -DdownloadSources=true -DdownloadJavadocs=true ");
		this.buildOutput = this.cmdExecute(commands);
	}

	public void compilePR()
	{
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(buildENVsetup());
		commands.add(buildENVgotoSandbox());
		commands.add("cd " + this.artifactId);
		commands.add("mvn clean install -DskipTests");
		commands.add("mvn eclipse:eclipse -DdownloadSources=true -DdownloadJavadocs=true ");
		this.buildOutput = this.cmdExecute(commands);
	}

	private String buildENVgotoSandbox() {
		return "cd "+PATH_BUILD_SANDBOX;
	}

	private String buildENVsetup() {
		return "set PATH=%PATH%;C:\\java\\jdk1.8.0_25\\bin\\;C:\\java\\apache-maven-3.2.3\\bin";
	}

	public String getBuildCMD() {
		return buildCMD;
	}

	public void setBuildCMD(String buildCMD) {
		this.buildCMD = buildCMD;
	}

	public String cleanupPR() {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(buildENVsetup());
		commands.add(buildENVgotoSandbox());
		commands.add(this.buildCMD);
		commands.add("cd " + this.artifactId);
		commands.add("mvn clean ");
		commands.add("mvn eclipse:clean ");
		return this.cmdExecute(commands);
	}

	public String cmdExecute(ArrayList<String> commands) {
		Process shell = null;
		DataOutputStream out = null;
		BufferedReader in = null;
		StringBuilder processOutput = new StringBuilder();
		processOutput.append(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())).append("\n");
		try {
			shell = Runtime.getRuntime().exec("cmd");//su if needed
			out = new DataOutputStream(shell.getOutputStream());

			in = new BufferedReader(new InputStreamReader(shell.getInputStream()));

			// Executing commands 
			for (String command : commands) {
				System.out.println("executing:\n" + command);
				out.writeBytes(command + "\n");
				out.flush();
			}

			out.writeBytes("exit\n");
			out.flush();
			String line;
			while ((line = in.readLine()) != null) {
				processOutput.append(line).append("\n");
			}

			//System.out.println("result:\n" + processOutput);
			processOutput.append(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())).append("\n");
			String output = processOutput.toString();
			shell.waitFor();
			return output;
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
		return "";
	}

	public void downloadWAR() throws IOException {
		compilePR();
		InputStream input = null;
		OutputStream output = null;
		try {
			String fileName = "eexcess-partner-"+ this.artifactId +"-"+ this.version+".war";

			String fileNameWithPath = this.PATH_BUILD_SANDBOX + this.artifactId + "\\" + "target\\"+fileName;
			input = new FileInputStream(fileNameWithPath);
			byte[] buf = new byte[1024];
			int bytesRead;

			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();

			ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
			String contentType = "application/zip";
			ec.setResponseContentType(contentType); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
			//		    int contentLength = 0;
			//			ec.setResponseContentLength(input.); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

			output = ec.getResponseOutputStream();

			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}

			fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
		} 
		finally {
			input.close();
			output.close();
		}

	}

	public void createSourceZIP() throws IOException {
		System.out.println(this.cleanupPR());
		String fileName = "eexcess-partner-"+ this.artifactId +"-"+ this.version+".war";

		try {
			zipDir(this.PATH_BUILD_SANDBOX+fileName+".zip", this.PATH_BUILD_SANDBOX+ this.artifactId );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downloadSourceZIP() throws IOException {
		createSourceZIP();
		InputStream input = null;
		OutputStream output = null;
		try {
			String fileName = "eexcess-partner-"+ this.artifactId +"-"+ this.version+".war"+".zip";

			String fileNameWithPath = this.PATH_BUILD_SANDBOX+fileName;
			input = new FileInputStream(fileNameWithPath);
			byte[] buf = new byte[1024];
			int bytesRead;

			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();

			ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
			String contentType = "application/zip";
			ec.setResponseContentType(contentType); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
			//		    int contentLength = 0;
			//			ec.setResponseContentLength(input.); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
			ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

			output = ec.getResponseOutputStream();

			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}

			fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
		} 
		finally {
			input.close();
			output.close();
		}

	}

	private void zipDir(String zipFileName, String dir) throws Exception {
		File dirObj = new File(dir);

		Path fileToDeletePath = Paths.get(zipFileName);
		Files.delete(fileToDeletePath);

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		//System.out.println("Creating : " + zipFileName);
		addDir(dirObj, out, dir.substring(0, dir.lastIndexOf('\\')+1));
		out.close();
	}

	private void addDir(File dirObj, ZipOutputStream out, String basePath) throws IOException {
		File[] files = dirObj.listFiles();
		byte[] tmpBuf = new byte[1024];

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				addDir(files[i], out, basePath);
				continue;
			}
			FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
			String entryName = files[i].getAbsolutePath().substring(basePath.length());
			//System.out.println(" Adding: " + files[i].getAbsolutePath() + "\n  with entryName:"+ entryName);
			out.putNextEntry(new ZipEntry(entryName));
			int len;
			while ((len = in.read(tmpBuf)) > 0) {
				out.write(tmpBuf, 0, len);
			}
			out.closeEntry();
			in.close();
		}
	}

}
