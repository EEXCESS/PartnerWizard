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
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ManagedBean
@SessionScoped
public class Bean implements Serializable {

	private static final String COOKIE_NAME_SEARCH_MAPPING_CONFIG = "SearchMappingConfig";

	private static final String COOKIE_NAME_MAPPING_FIELDS = "MappingFields";

	private static final String COOKIE_NAME_PARTNER_INFO = "PartnerInfo";

	private static final String COOKIE_NAME_DETAIL_MAPPING_CONFIG = "DetailMappingConfig";

	private static final String COOKIENAME = "EEXCESSPartnerWizard";

	private final String PATH_BUILD_SANDBOX = "C:\\dev\\eexcess-partnerrecommender-archetype-sandbox\\";

	private static final long serialVersionUID = -2403138958014741653L;

	private String groupId = "";
	private String artifactId ="";
	private String version = ""; 
	private String packageStr = "";
	private String partnerName = "";
	private String partnerURL = "";
	private String dataLicense = "";
	private String apiPreviewImagePrefix="";
	private String apiURIPathPrefix="";
	
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
	
	public String getApiPreviewImagePrefix() {
		return apiPreviewImagePrefix;
	}

	public void setApiPreviewImagePrefix(String apiPreviewImagePrefix) {
		this.apiPreviewImagePrefix = apiPreviewImagePrefix;
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
		//wenn cookie vorhanden dann werte aus cookie auslesen
		loadFromCookie();
	}

	public void saveToCookie(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		
		response.addCookie(createCookie(request,"ArtifactId", this.getArtifactId()));
		response.addCookie(createCookie(request,"DataLicense", this.getDataLicense()));
		response.addCookie(createCookie(request,"GroupId", this.getGroupId()));
		response.addCookie(createCookie(request,"PackageStr", this.getPackageStr()));
		response.addCookie(createCookie(request,"PartnerName", this.getPartnerName()));
		response.addCookie(createCookie(request,"PartnerURL", this.getPartnerURL()));
		response.addCookie(createCookie(request,"Version", this.getVersion()));
		response.addCookie(createCookie(request,"ApiPreviewImagePrefix", this.getApiPreviewImagePrefix()));
		response.addCookie(createCookie(request,"ApiURIPathPrefix", this.getApiURIPathPrefix()));
		
		createCookie(response,request,COOKIE_NAME_DETAIL_MAPPING_CONFIG, this.getDetailMappingConfig());
		createCookie(response, request,COOKIE_NAME_PARTNER_INFO, this.getPartnerInfo());
		createCookie(response,request,COOKIE_NAME_SEARCH_MAPPING_CONFIG, this.getSearchMappingConfig());
	}

	private void createCookie(HttpServletResponse response,
			HttpServletRequest request, String prefix,
			PartnerInfo myPartnerInfo) {
		response.addCookie(createCookie(request,prefix+"ContactEmail", myPartnerInfo.getContactEmail()));
		response.addCookie(createCookie(request,prefix+"Username", myPartnerInfo.getUsername()));
	}

	private void createCookie(HttpServletResponse response,
			HttpServletRequest request, String prefix,
			MappingConfigBean myMappingConfigBean) {
		response.addCookie(createCookie(request,prefix+"EexcessFieldsXPathLoop", myMappingConfigBean.getEexcessFieldsXPathLoop()));
		response.addCookie(createCookie(request,prefix+"SearchEndpoint", myMappingConfigBean.getSearchEndpoint()));
		response.addCookie(createCookie(request,prefix+"SearchEndpointSearchTerm", myMappingConfigBean.getSearchEndpointSearchTerm()));
		createCookie(response, request,prefix+COOKIE_NAME_MAPPING_FIELDS, myMappingConfigBean.getMappingFields());
	}

	private void createCookie(HttpServletResponse response,
			HttpServletRequest request, String prefix,
			ArrayList<MappingField> myMappingFields) {
		for (int i = 0; i < myMappingFields.size(); i++) {
			createCookie(response,request,prefix+i, myMappingFields.get(i));
		}
	}

	private void createCookie(HttpServletResponse response,
			HttpServletRequest request, String prefix,
			MappingField myMappingField) {
		response.addCookie(createCookie(request,prefix+"xPath", myMappingField.getxPath()));
	}

	private Cookie createCookie(HttpServletRequest request, String name, String value) {
		Cookie myNewCookie = new Cookie(COOKIENAME+name, value);
	    myNewCookie.setPath(request.getContextPath());
		return myNewCookie;
	}
	
	public void loadFromCookie(){
		ArrayList<Cookie> myCookies = this.getCookies();
		for (Cookie myActCookie : myCookies) {
			System.out.println("cookie:"+ myActCookie.getName() + " " + myActCookie.getValue());
			if (myActCookie.getName().startsWith(COOKIENAME))
			{
				if (myActCookie.getName().equals(COOKIENAME+"ArtifactId"))
					this.setArtifactId(myActCookie.getValue());
				if (myActCookie.getName().equals(COOKIENAME+"DataLicense"))
					this.setDataLicense(myActCookie.getValue());
				if (myActCookie.getName().equals(COOKIENAME+"GroupId"))
					this.setGroupId(myActCookie.getValue());
				if (myActCookie.getName().equals(COOKIENAME+"PackageStr"))
					this.setPackageStr(myActCookie.getValue());
				if (myActCookie.getName().equals(COOKIENAME+"PartnerName"))
					this.setPartnerName(myActCookie.getValue());
				if (myActCookie.getName().equals(COOKIENAME+"PartnerURL"))
					this.setPartnerURL(myActCookie.getValue());
				if (myActCookie.getName().equals(COOKIENAME+"Version"))
					this.setVersion(myActCookie.getValue());
				if (myActCookie.getName().equals(COOKIENAME+"ApiPreviewImagePrefix"))
					this.setApiPreviewImagePrefix(myActCookie.getValue());
				if (myActCookie.getName().equals(COOKIENAME+"ApiURIPathPrefix"))
					this.setApiURIPathPrefix(myActCookie.getValue());
				
				
				if (myActCookie.getName().startsWith(COOKIENAME+COOKIE_NAME_DETAIL_MAPPING_CONFIG))
				{
					String actPrefix = COOKIENAME+COOKIE_NAME_DETAIL_MAPPING_CONFIG;
					if (myActCookie.getName().equals(actPrefix+"EexcessFieldsXPathLoop"))
						this.getDetailMappingConfig().setEexcessFieldsXPathLoop(myActCookie.getValue());
					if (myActCookie.getName().equals(actPrefix+"SearchEndpoint"))
						this.getDetailMappingConfig().setSearchEndpoint(myActCookie.getValue());
					if (myActCookie.getName().equals(actPrefix+"SearchEndpointSearchTerm"))
						this.getDetailMappingConfig().setSearchEndpointSearchTerm(myActCookie.getValue());
					if (myActCookie.getName().startsWith(actPrefix+COOKIE_NAME_MAPPING_FIELDS))
					{
						String actPrefixField = actPrefix+COOKIE_NAME_MAPPING_FIELDS;
						for (int i = 0; i < this.getDetailMappingConfig().getMappingFields().size(); i++) {
							if (myActCookie.getName().equals(actPrefixField+i+"xPath"))
								this.getDetailMappingConfig().getMappingFields().get(i).setxPath(myActCookie.getValue());
						}
					}					
				}

				if (myActCookie.getName().startsWith(COOKIENAME+COOKIE_NAME_PARTNER_INFO))
				{
					String actPrefix = COOKIENAME+COOKIE_NAME_PARTNER_INFO;
					if (myActCookie.getName().equals(actPrefix+"ContactEmail"))
						this.getPartnerInfo().setContactEmail(myActCookie.getValue());
					if (myActCookie.getName().equals(actPrefix+"Username"))
						this.getPartnerInfo().setUsername(myActCookie.getValue());
				}

				if (myActCookie.getName().startsWith(COOKIENAME+COOKIE_NAME_SEARCH_MAPPING_CONFIG))
				{
					String actPrefix = COOKIENAME+COOKIE_NAME_SEARCH_MAPPING_CONFIG;
					if (myActCookie.getName().equals(actPrefix+"EexcessFieldsXPathLoop"))
						this.getSearchMappingConfig().setEexcessFieldsXPathLoop(myActCookie.getValue());
					if (myActCookie.getName().equals(actPrefix+"SearchEndpoint"))
						this.getSearchMappingConfig().setSearchEndpoint(myActCookie.getValue());
					if (myActCookie.getName().equals(actPrefix+"SearchEndpointSearchTerm"))
						this.getSearchMappingConfig().setSearchEndpointSearchTerm(myActCookie.getValue());
					if (myActCookie.getName().startsWith(actPrefix+COOKIE_NAME_MAPPING_FIELDS))
					{
						String actPrefixField = actPrefix+COOKIE_NAME_MAPPING_FIELDS;
						for (int i = 0; i < this.getSearchMappingConfig().getMappingFields().size(); i++) {
							if (myActCookie.getName().equals(actPrefixField+i+"xPath"))
								this.getSearchMappingConfig().getMappingFields().get(i).setxPath(myActCookie.getValue());
						}
					}					
				}
				
			}
		}

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
		mappingField = new MappingField();
		mappingField.setName("previewImage");
		mappingField.setDescription("URL of the preview image");
		mappingFields.add(mappingField);
		for (int i = 0; i < mappingFields.size(); i++) {
			mappingFields.get(i).setId(i);
		}
		return mappingFields;
	}

	public void generatePR()
	{
		this.buildCMD = "mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=eu.eexcess -DarchetypeArtifactId=eexcess-partner-recommender-archetype -DarchetypeVersion=1.0-SNAPSHOT -DinteractiveMode=false " 
				+ "-DgroupId=" + this.groupId 
				+ " -DartifactId="+ this.artifactId 
				+ " -Dversion="+this.version 
				+ " -Dpackage="+ this.packageStr 
				+ " -DpartnerName=\""+ this.partnerName+"\""
				+ " -DpartnerURL=\""+ this.partnerURL+"\""
				+ " -DdataLicense=\""+ this.dataLicense+"\""
				+ " -DpartnerAPIpreviewImagePathPrefix=\""+ this.apiPreviewImagePrefix+"\""
				+ " -DpartnerAPIURIPathPrefix=\""+ this.apiURIPathPrefix+"\""
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
		commands= buildENVsetup(commands);
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
		commands=buildENVsetup(commands);
		commands.add(buildENVgotoSandbox());
		commands.add("cd " + this.artifactId);
		commands.add("mvn clean install -DskipTests");
		commands.add("mvn eclipse:eclipse -DdownloadSources=true -DdownloadJavadocs=true ");
		this.buildOutput = this.cmdExecute(commands);
	}

	private String buildENVgotoSandbox() {
		return "cd "+PATH_BUILD_SANDBOX;
	}

	private ArrayList<String> buildENVsetup(ArrayList<String> commands) {
//		commands.add("set PATH="+PATH_JDK+"bin\\;C:\\java\\apache-maven-3.2.3\\bin;%PATH%;");
//		commands.add("set JAVA_HOME="+PATH_JDK);
		return commands;
	}

	public String getBuildCMD() {
		return buildCMD;
	}

	public void setBuildCMD(String buildCMD) {
		this.buildCMD = buildCMD;
	}

	public String cleanupPR() {
		ArrayList<String> commands = new ArrayList<String>();
		commands = buildENVsetup(commands);
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
	
	  public ArrayList<Cookie> getCookies() {
	        FacesContext context = FacesContext.getCurrentInstance();
	        Map cookieMap = context.getExternalContext().getRequestCookieMap();
	        ArrayList<Cookie> cookies = new ArrayList<Cookie>(cookieMap.values()); 
	        return cookies;
	    }

	public void defaultTestValuesKIMportal()
	{
		this.groupId = "at.joanneum";
		this.artifactId ="MyPartnerRecommender";
		this.version = "1.0-SNAPSHOT"; 
		this.packageStr = "at.joanneum";
		this.partnerName = "archetype example";
		this.partnerURL = "https://www.kgportal.bl.ch/";
		this.dataLicense ="http://creativecommons.org/licenses/by-nc-sa/4.0/";

		this.searchMappingConfig.setSearchEndpoint("https://kgapi.bl.ch/solr/kim-portal.objects/select/xml?q=_fulltext_:${query}&rows=${numResults}");
		this.searchMappingConfig.setEexcessFieldsXPathLoop("/response/result/doc/");
		this.searchMappingConfig.setSearchEndpointSearchTerm("Basel");
		this.searchMappingConfig.getMappingFields().get(0).setxPath("str[@name='uuid']");
		this.searchMappingConfig.getMappingFields().get(1).setxPath("str[@name='uuid']");
		this.searchMappingConfig.getMappingFields().get(2).setxPath("str[@name='_display_']");
		this.searchMappingConfig.getMappingFields().get(3).setxPath("str[@name='beschreibung']");

		this.detailMappingConfig.setSearchEndpoint("https://kgapi.bl.ch/solr/kim-portal.objects/select/xml?q=uuid:${detailQuery}");
		this.detailMappingConfig.setEexcessFieldsXPathLoop("/response/result/doc/");
		this.detailMappingConfig.setSearchEndpointSearchTerm("70e1531b-4ce5-33cb-8ba7-91b2dcd033f8");
		this.detailMappingConfig.getMappingFields().get(0).setxPath("str[@name='uuid']");
		this.detailMappingConfig.getMappingFields().get(1).setxPath("str[@name='uuid']");
		this.detailMappingConfig.getMappingFields().get(2).setxPath("str[@name='_display_']");
		this.detailMappingConfig.getMappingFields().get(3).setxPath("str[@name='beschreibung']");
		
		this.apiPreviewImagePrefix = "https://kgapi.bl.ch/";
		this.apiURIPathPrefix = "https://www.kgportal.bl.ch/sammlungen#";
	}

	public void defaultTestValuesRIJKMuseum()
	{
		String key= "";
		
		this.groupId = "nl.rijksmuseum";
		this.artifactId ="RijksMuseumPartnerRecommender";
		this.version = "1.0-SNAPSHOT"; 
		this.packageStr = "nl.rijksmuseum";
		this.partnerName = "RijksMuseum";
		this.partnerURL = "https://www.rijksmuseum.nl/";
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

		this.apiPreviewImagePrefix = "remove";
		this.apiURIPathPrefix = "remove";

	}

	public void defaultTestValuesSolRKierling()
	{
		this.groupId = "at.joanneum";
		this.artifactId ="SOLRKierlingRecommender";
		this.version = "1.0-SNAPSHOT"; 
		this.packageStr = "at.joanneum";
		this.partnerName = "Kierling Recommender";
		this.partnerURL = "http://digv548.joanneum.at/";
		this.dataLicense ="http://creativecommons.org/licenses/by-nc-sa/4.0/";

		this.searchMappingConfig.setSearchEndpoint("http://digv548.joanneum.at:8983/solr/imdas-online.common/select?q=${query}&wt=xml&indent=true&rows=${numResults}");
		this.searchMappingConfig.setEexcessFieldsXPathLoop("/response/result/doc/");
		this.searchMappingConfig.setSearchEndpointSearchTerm("Allmayer");
		this.searchMappingConfig.getMappingFields().get(0).setxPath("str[@name='uuid']");
		this.searchMappingConfig.getMappingFields().get(1).setxPath("str[@name='uuid']");
		this.searchMappingConfig.getMappingFields().get(2).setxPath("str[@name='_display_']");
		this.searchMappingConfig.getMappingFields().get(3).setxPath("str[@name='beschreibung']");
		this.searchMappingConfig.getMappingFields().get(4).setxPath("arr[@name='_thumbs_']");

		this.detailMappingConfig.setSearchEndpoint("http://digv548.joanneum.at:8983/solr/imdas-online.common/select?q=uuid:${detailQuery}&wt=xml&indent=true");
		this.detailMappingConfig.setEexcessFieldsXPathLoop("/response/result/doc/");
		this.detailMappingConfig.setSearchEndpointSearchTerm("6e4888c5-f636-4d60-9145-82b22adecf67");
		this.detailMappingConfig.getMappingFields().get(0).setxPath("str[@name='uuid']");
		this.detailMappingConfig.getMappingFields().get(1).setxPath("str[@name='uuid']");
		this.detailMappingConfig.getMappingFields().get(2).setxPath("str[@name='_display_']");
		this.detailMappingConfig.getMappingFields().get(3).setxPath("str[@name='beschreibung']");
		this.detailMappingConfig.getMappingFields().get(4).setxPath("arr[@name='_thumbs_']");
		
		this.apiPreviewImagePrefix = "http://digv548.joanneum.at/";
		this.apiURIPathPrefix = "http://digv548.joanneum.at/detailview/";
	}

	public String getApiURIPathPrefix() {
		return apiURIPathPrefix;
	}

	public void setApiURIPathPrefix(String apiURIPathPrefix) {
		this.apiURIPathPrefix = apiURIPathPrefix;
	}

}
