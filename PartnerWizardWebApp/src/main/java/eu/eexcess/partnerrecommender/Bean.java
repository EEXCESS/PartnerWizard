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
	
	private PartnerInfo partnerInfo;
	
	public PartnerInfo getPartnerInfo() {
		if (this.partnerInfo == null )
			this.partnerInfo = new PartnerInfo();
		return partnerInfo;
	}

	public void setPartnerInfo(PartnerInfo partnerInfo) {
		this.partnerInfo = partnerInfo;
	}


	private MappingConfigBean searchMappingConfig;
	
	private MappingConfigBean detailMappingConfig;

	public MappingConfigBean getDetailMappingConfig() {
		return detailMappingConfig;
	}

	public void setDetailMappingConfig(MappingConfigBean detailMappingConfig) {
		this.detailMappingConfig = detailMappingConfig;
	}

	public MappingConfigBean getSearchMappingConfig() {
		return searchMappingConfig;
	}

	public void setSearchMappingConfig(MappingConfigBean searchMappingConfig) {
		this.searchMappingConfig = searchMappingConfig;
	}

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

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

/*
*/

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
		this.searchMappingConfig = new MappingConfigBean();
		this.searchMappingConfig.setBean(this);
		this.searchMappingConfig.setMappingFields(initMappingFields());
		this.detailMappingConfig = new MappingConfigBean();
		this.detailMappingConfig.setBean(this);
		this.detailMappingConfig.setMappingFields(initMappingFields());
		defaultTestValuesRIJKMuseum();
		//defaultTestValues();
	}

	private ArrayList<MappingField> initMappingFields() {
		ArrayList<MappingField> mappingFields = new ArrayList<MappingField>();
		MappingField mappingField = new MappingField();
		mappingField.setName("ID");
		mappingField.setDescription("identifier");
		mappingFields.add(mappingField);
		mappingField = new MappingField();
		mappingField.setName("URI");
		mappingField.setDescription("URI of the object");
		mappingFields.add(mappingField);
		mappingField = new MappingField();
		mappingField.setName("Title");
		mappingField.setDescription("title");
		mappingFields.add(mappingField);
		mappingField = new MappingField();
		mappingField.setName("Description");
		mappingField.setDescription("description of the item");
		mappingFields.add(mappingField);
		for (int i = 0; i < mappingFields.size(); i++) {
			mappingFields.get(i).setId(i);
		}
		return mappingFields;
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
				+ " -DpartnerAPIsearchEndpoint=\""+ this.searchMappingConfig.getSearchEndpoint()+"\""
				+ " -DpartnerAPIsearchTerm=\""+ this.searchMappingConfig.getSearchEndpointSearchTerm()+"\""
				+ " -DpartnerAPIsearchMappingFieldsLoopXPath=\""+ this.searchMappingConfig.getEexcessFieldsXPathLoop()+"\"";
		for (int i = 0; i < this.searchMappingConfig.getMappingFields().size(); i++) {
			if (this.searchMappingConfig.getMappingFields().get(i).getxPath() != null && !this.searchMappingConfig.getMappingFields().get(i).getxPath().trim().isEmpty())
				this.buildCMD += " -DpartnerAPIsearchMappingFieldsXPath"+this.searchMappingConfig.getMappingFields().get(i).getName()+"=\""+ this.searchMappingConfig.getMappingFields().get(i).getxPath()+"\"";
		}
		this.buildCMD += " -DpartnerAPIdetailEndpoint=\""+ this.detailMappingConfig.getSearchEndpoint()+"\"";
		this.buildCMD += " -DpartnerAPIdetailTerm=\""+ this.detailMappingConfig.getSearchEndpointSearchTerm()+"\"";
		this.buildCMD += " -DpartnerAPIdetailMappingFieldsLoopXPath=\""+ this.detailMappingConfig.getEexcessFieldsXPathLoop()+"\"";
		for (int i = 0; i < this.detailMappingConfig.getMappingFields().size(); i++) {
			if (this.detailMappingConfig.getMappingFields().get(i).getxPath() != null && !this.detailMappingConfig.getMappingFields().get(i).getxPath().trim().isEmpty())
				this.buildCMD += " -DpartnerAPIdetailMappingFieldsXPath"+this.detailMappingConfig.getMappingFields().get(i).getName()+"=\""+ this.detailMappingConfig.getMappingFields().get(i).getxPath()+"\"";
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
			System.out.println("finished!");
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

	private void defaultTestValues()
	{
		this.groupId = "at.joanneum";
		this.artifactId ="MyPartnerRecommender";
		this.version = "1.0-SNAPSHOT"; 
		this.packageStr = "at.joanneum";
		this.partnerName = "Joanneum Partner Recommender";
		this.partnerURL = "http://example.org/";
		this.dataLicense ="http://creativecommons.org/licenses/by-nc-sa/4.0/";

		this.searchMappingConfig.setSearchEndpoint("https://kgapi.bl.ch/solr/kim-portal.objects/select/xml?q=_fulltext_:${query}&rows=${numResults}");
		this.searchMappingConfig.setEexcessFieldsXPathLoop("/response/result/doc/");
		this.searchMappingConfig.setSearchEndpointSearchTerm("Basel");
		this.searchMappingConfig.getMappingFields().get(0).setxPath("str[@name='uuid']");
		this.searchMappingConfig.getMappingFields().get(1).setxPath("str[@name='uuid']");
		this.searchMappingConfig.getMappingFields().get(2).setxPath("str[@name='_display_']");
		this.searchMappingConfig.getMappingFields().get(3).setxPath("str[@name='beschreibung']");

		this.detailMappingConfig.setSearchEndpoint("https://kgapi.bl.ch/solr/kim-portal.objects/select/xml?q=uuid:${detailQuery}");
	}

	private void defaultTestValuesRIJKMuseum()
	{
		String key= "";
		
		this.groupId = "nl.rijksmuseum";
		this.artifactId ="RijksMuseumPartnerRecommender";
		this.version = "1.0-SNAPSHOT"; 
		this.packageStr = "nl.rijksmuseum";
		this.partnerName = "RijksMuseum Partner Recommender";
		this.partnerURL = "http://example.org/";
		this.dataLicense ="http://creativecommons.org/licenses/by-nc-sa/4.0/";

		this.searchMappingConfig.setSearchEndpoint("https://www.rijksmuseum.nl/api/en/collection?q=${query}&key="+key+"&format=xml");
		this.searchMappingConfig.setSearchEndpointSearchTerm("Basel");
		this.searchMappingConfig.setEexcessFieldsXPathLoop("/searchGetResponse/artObjects/");
		this.searchMappingConfig.getMappingFields().get(0).setxPath("objectNumber");
		this.searchMappingConfig.getMappingFields().get(1).setxPath("links/web");
		this.searchMappingConfig.getMappingFields().get(2).setxPath("title");
		this.searchMappingConfig.getMappingFields().get(3).setxPath("longTitle");
		
		this.detailMappingConfig.setSearchEndpoint("https://www.rijksmuseum.nl/api/en/collection/${detailQuery}?format=xml&key="+key);
		this.detailMappingConfig.setSearchEndpointSearchTerm("RP-P-1959-614");
		this.detailMappingConfig.setEexcessFieldsXPathLoop("/artObjectGetResponse/artObject/");
		this.detailMappingConfig.getMappingFields().get(0).setxPath("objectNumber");
		this.detailMappingConfig.getMappingFields().get(1).setxPath("id");
		this.detailMappingConfig.getMappingFields().get(2).setxPath("longTitle");
		this.detailMappingConfig.getMappingFields().get(3).setxPath("description");
	}


}
