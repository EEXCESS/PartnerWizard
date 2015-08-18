package eu.eexcess.partnerwizard.webservice.servlets;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-08-18
 */
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloServlet{

	@GET
	@Path("test")
	@Produces(MediaType.APPLICATION_JSON)
	public HelloClass test(@QueryParam("param") String param ){
		return new HelloClass( param, "hello" );
	}

	public class HelloClass{
		public String param;
		public String hello;

		public HelloClass( String parameter, String hello ){
			this.param = parameter;
			this.hello = hello;
		}
	}

}
