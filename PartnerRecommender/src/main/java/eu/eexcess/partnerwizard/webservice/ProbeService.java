package eu.eexcess.partnerwizard.webservice;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-08-18
 */
import com.sun.jersey.spi.resource.Singleton;

import eu.eexcess.partnerwizard.probe.PartnerProber;
import eu.eexcess.partnerwizard.probe.model.ProbeConfiguration;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponse;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/probe")
@Singleton
public class ProbeService{
	private final PartnerProber prober = new PartnerProber();


	@POST
	@Path("init")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ProberResponse init( List<String> keywords ){
		if( keywords==null || keywords.isEmpty() ){
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		keywords.removeAll( Collections.<String>singleton(null) );
		keywords.removeAll( Collections.<String>singleton( "" ) );
		if( keywords.isEmpty() ){
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		return prober.init( keywords );
	}

	@GET
	@Path("next")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ProberResponse storeAndNext( @QueryParam("id") String id,
										 @QueryParam("hasWinner") @DefaultValue("true") boolean hasWinner,
										 @QueryParam("winner") @DefaultValue("-1") int winner ){
		try{
			return prober.storeAndNext( id, hasWinner, winner );
		}
		catch(IllegalStateException ex){
			throw new WebApplicationException( Response.Status.FORBIDDEN );
		}
		catch(IllegalArgumentException ex){
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
	}

	@GET
	@Path("get")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ProbeConfiguration getConfiguration( @QueryParam("id") String id ) {
		try{
			return prober.getConfiguration( id );
		}
		catch(IllegalStateException ex){
			throw new WebApplicationException( Response.Status.FORBIDDEN );
		}
		catch(IllegalArgumentException ex){
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
	}

}
