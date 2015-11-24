package eu.eexcess.partnerwizard.webservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.ServletContext;
import javax.smartcardio.ATR;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.json.JSONObject;

import org.apache.jena.atlas.json.io.parserjavacc.javacc.JSON_Parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.spi.resource.Singleton;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.partnerrecommender.Bean;

@Path("/wizard")
@Singleton
public class WizardRESTService {

	
	@Context
	private ServletContext context;

	private final ObjectMapper mapper = new ObjectMapper();

	
	@GET
	@Path("ping")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public String queries() {
		System.out.println("Service wizard: ping called....");
		return "";
	}

	@POST
	@Path("updateConfigAndDeploy")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public boolean updateConfigAndDeploy( PartnerConfiguration config){
		if( config==null ){
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		if (config.getTransformerClass() != null && !config.getTransformerClass().isEmpty()) {
			String artifactId = config.getTransformerClass();
			artifactId = artifactId.substring(artifactId.lastIndexOf(".")+1);
			artifactId = artifactId.replace("Transformer", "");
			String copyTargetPath = Bean.PATH_BUILD_SANDBOX + artifactId + "\\src\\main\\resources\\";
			String dateString = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

			System.out.println("artifactId:"+artifactId);
			System.out.println("copyTargetPath:"+copyTargetPath);

			// backup old config
			Charset charset = StandardCharsets.UTF_8;
			try {
				Files.copy(Paths.get(copyTargetPath+"partner-config.json"), Paths.get(Bean.PATH_BUILD_SANDBOX + artifactId +"-" + dateString + ".old.partner-config.json"), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// neues config reinkopieren
			try {
				String newFile = mapper.writeValueAsString(config);
				Files.write(Paths.get(copyTargetPath + "partner-config.json"), newFile.getBytes(charset));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			// new config saving 
			try {
				String newFile = mapper.writeValueAsString(config);
				Files.write(Paths.get(Bean.PATH_BUILD_SANDBOX + artifactId +"-" + dateString + ".partner-config.json"), newFile.getBytes(charset));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// build new version
			
			ArrayList<String> commands = new ArrayList<String>();
			commands.add("cd " + Bean.PATH_BUILD_SANDBOX);
			commands.add("cd " + artifactId);
			commands.add("mvn install -DskipTests");
			commands.add("cd target");
			String warName= "eexcess-partner-"+artifactId+"-*";
			commands.add("del %TOMCAT%webapps\\"+warName+"*.war");
			commands.add("rd /S /Q %TOMCAT%webapps\\"+warName);
			commands.add("xcopy "+warName+".war %TOMCAT%webapps\\ /Y");
			this.cmdExecute(commands);
			
		}
		return true;
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
			System.out.println(processOutput);
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

}
