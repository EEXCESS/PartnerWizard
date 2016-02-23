package eu.eexcess.partnerwizard.webservice;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-08-18
 */
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.resource.Singleton;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerwizard.probe.PartnerProber;
import eu.eexcess.partnerwizard.probe.model.ProbeConfiguration;
import eu.eexcess.partnerwizard.probe.model.web.ProberKeyword;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponse;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@Path("/probe")
@Singleton
public class ProbeService{
	private final static String CONFIG_FILE_PATH = "/WEB-INF/query-config.xml";
	private final PartnerProber prober;
	private final List<ProberKeyword[]> proberQueries;


	public ProbeService(@Context ServletContext servletContext) throws  JAXBException {
		InputStream stream = servletContext.getResourceAsStream( CONFIG_FILE_PATH );

		JAXBContext jaxbContext = JAXBContext.newInstance(ServiceConfiguration.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		ServiceConfiguration config = (ServiceConfiguration) unmarshaller.unmarshal( stream );

		Map<String, Integer> queryGenerators = toPositionMap( config.queryGenerators );
		this.prober = new PartnerProber( queryGenerators );

		this.proberQueries = config.getProberKeywords();
	}


	@GET
	@Path("queries")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public List<ProberKeyword[]> queries() throws Exception{
		return proberQueries;
	}

	@POST
	@Path("init")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ProberResponse init( List<ProberKeyword[]> queries ){
		if( queries==null||queries.isEmpty() ){
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		for( int i = 0; i<queries.size(); i++ ){
			ProberKeyword[] keywords = queries.get( i );
			if( keywords.length<1 ){
				queries.remove( i );
			}
		}
		if( queries.isEmpty() ){
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		return prober.init( queries );
	}

	@GET
	@Path("next")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ProberResponse storeAndNext( @QueryParam("id") String id,
										@QueryParam("hasWinner") @DefaultValue("true") boolean hasWinner,
										@QueryParam("winner") @DefaultValue("-1") int winner ){
		try{
			return prober.storeAndNext( id, hasWinner, winner );
		}
		catch( IllegalStateException ex ){
			throw new WebApplicationException( ex, Response.Status.FORBIDDEN );
		}
		catch( IllegalArgumentException ex ){
			throw new WebApplicationException( ex, Response.Status.BAD_REQUEST );
		}
	}

	@GET
	@Path("get")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ProbeConfiguration getConfiguration( @QueryParam("id") String id ){
		try{
			ProbeConfiguration config = prober.getConfiguration( id );

			return config;
		}
		catch( IllegalStateException ex ){
			throw new WebApplicationException( ex, Response.Status.FORBIDDEN );
		}
		catch( IllegalArgumentException ex ){
			throw new WebApplicationException( ex, Response.Status.BAD_REQUEST );
		}
	}

//	@GET
//	@Path("store")
//	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//	public PartnerConfiguration storeConfiguration( @QueryParam("id") String id ){
//		try{
//			PartnerConfiguration config = prober.getParnterConfiguration(id );
//			return config;
//		}
//		catch( IllegalStateException ex ){
//			throw new WebApplicationException( Response.Status.FORBIDDEN );
//		}
//		catch( IllegalArgumentException ex ){
//			throw new WebApplicationException( Response.Status.BAD_REQUEST );
//		}
//	}

	@GET
	@Path("store")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public boolean storeConfiguration(@Context HttpServletRequest request, @QueryParam("id") String id){
		ProbeConfiguration config;
		try{
			config = prober.getConfiguration( id );
		}
		catch( IllegalStateException ex ){
			throw new WebApplicationException( ex, Response.Status.FORBIDDEN );
		}
		catch( IllegalArgumentException ex ){
			throw new WebApplicationException( ex, Response.Status.BAD_REQUEST );
		}

		StringBuilder url = new StringBuilder();
		url.append( "http://localhost:")
			.append( request.getLocalPort() )
			.append( request.getContextPath() )
			.append( request.getServletPath() )
			.append( WizardRESTService.URL_PATTERN );

		Client client = new Client( PartnerConfigurationCache.CONFIG.getClientJacksonJson() );
		WebResource.Builder builder = client.resource( url.toString() )
				.type( MediaType.APPLICATION_JSON_TYPE )
				.accept( MediaType.APPLICATION_JSON_TYPE )
				.accept( MediaType.APPLICATION_XML_TYPE );

		try{
			return builder.post( Boolean.class, config );
		}
		catch( ClientHandlerException|UniformInterfaceException ex){
			throw new WebApplicationException( ex, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}


	private static <T> Map<T, Integer> toPositionMap(List<T> list){
		Map<T, Integer> map = new HashMap<>( list.size() );
		int positionCounter = 0;
		for(T entry : list ){
			map.put( entry, positionCounter++);
		}

		return map;
	}
}
