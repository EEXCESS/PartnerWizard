package eu.eexcess.partnerrecommender;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class Bean implements Serializable {

    private static final long serialVersionUID = -2403138958014741653L;

    private String groupId = "at.joanneum";
    private String artifactId ="MyPartnerRecommender";
    private String version = "1.0-SNAPSHOT"; 
    private String packageStr = "at.joanneum";
	private String partnerName = "Joanneum PartnerRecommender";
	
	private String buildCMD="";
	
	private String buildCMDHTML="";
	
	private String searchEndpoint = "https://kgapi.bl.ch/solr/kim-portal.objects/select/xml?q=_fulltext_:${query}&rows=${numResults}";

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


	private String apiResponse ="<?xml version=\"1.0\" encoding=\"UTF-8\"?> <response> <lst name=\"responseHeader\"> <int name=\"status\">0</int> <int name=\"QTime\">0</int> <lst name=\"params\"> <str name=\"q\">uuid:aa0b5559-6e86-46db-9785-0329ab800956</str> </lst> </lst> <result name=\"response\" numFound=\"1\" start=\"0\"> <doc> <str name=\"_participant_\">museum-pro-muttenz</str> <arr name=\"alte_inventarnummern\"> <str>30.0101</str> </arr> <str name=\"objekttyp\">Fotografie</str> <float name=\"anzahl\">1.0</float> <str name=\"beschreibung\">Mitte: das Schulhaus Hinterzweien mit der Turnhalle; rechts oben: die katholische Kirche.</str> <str name=\"datentraeger\">Farb-Positiv</str> <str name=\"copyright\">Museen Muttenz</str> <arr name=\"klassifikation_sachgruppe\"> <str>- Fotografie / Ortsbild / Quartier - Architektur / Öffentliche Bauten / Schulhaus, Kindergarten - Architektur / Öffentliche Bauten / Kirchliche Baute und Nebenbaute - Fotografie / Luftbild </str> <arr name=\"person_name_fotograf\"> <str>SP Luftbild AG Möhlin</str> <str name=\"inventarnummer\">Mz 000068</str> <arr name=\"_thumbs_mini_\"> <str>media/museum-pro-muttenz/resources/images/thumbs-mini/AA0B5559-6E86-46DB-9785-0329AB800956_001.jpg</str> <arr name=\"_thumbs_\"> <str>media/museum-pro-muttenz/resources/images/thumbs/AA0B5559-6E86-46DB-9785-0329AB800956_001.jpg</str> <str name=\"titel\">Quartier Hinterzweien </str> <str name=\"sammlung\">Museen Muttenz</str> <date name=\"_lastchange_\">2015-05-28T06:57:22Z</date> <int name=\"_imagecount_\">1</int> <str name=\"objektbezeichnung\">Fotografie</str> <str name=\"datierung_beschreibung\">1993</str> <str name=\"uuid\">aa0b5559-6e86-46db-9785-0329ab800956</str> <arr name=\"_previews_\"> <str>media/museum-pro-muttenz/resources/images/previews/AA0B5559-6E86-46DB-9785-0329AB800956_001.jpg</str> <str name=\"institution\">Museen Muttenz</str> <str name=\"_display_\">Fotografie, Quartier Hinterzweien </str> <long name=\"_version_\">1504134220904136704</long> <str name=\"lizenzbedingung_url\">https://creativecommons.org/licenses/by-nc-sa/4.0/</str> <str name=\"lizenzbedingung\">CC BY-NC-SA 4.0</str> <str name=\"sprache\">de</str> </doc> </result> </response>";

    public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getApiResponse() {
		return apiResponse;
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
    }
    
    public void callPartnerAPI()
    {
    	
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
