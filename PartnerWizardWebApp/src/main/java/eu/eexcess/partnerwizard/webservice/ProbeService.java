package eu.eexcess.partnerwizard.webservice;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-08-18
 */
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.resource.Singleton;
import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerwizard.probe.PartnerProber;
import eu.eexcess.partnerwizard.probe.model.ProbeConfiguration;
import eu.eexcess.partnerwizard.probe.model.web.ProberKeyword;
import eu.eexcess.partnerwizard.probe.model.web.ProberResponse;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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
public class ProbeService implements ServletContextListener{
	private static final Logger LOGGER = Logger.getLogger( ProbeService.class.getName() );
	private static final String QUERY_CONFIG_FILE_PATH = "/WEB-INF/query-config.xml";
	private static PartnerConfiguration partnerConfigurationMaster;
	private static PartnerProber prober;
	private static List<ProberKeyword[]> proberQueries;


	@GET
	@Path("show")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public PartnerConfiguration config() throws Exception{
		return partnerConfigurationMaster;
	}

	@GET
	@Path("queries")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public List<ProberKeyword[]> queries() throws Exception{
		LOGGER.log( Level.INFO, "GET queries execeuted" );

		return proberQueries;
	}

	@POST
	@Path("init")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ProberResponse init( List<ProberKeyword[]> queries ){
		long start = System.nanoTime();

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

		ProberResponse init = prober.init( queries );

		long duration = (System.nanoTime()-start)/1000000;
		LOGGER.log( Level.INFO, "POST init of ID ''{0}'' took {1}ms", new Object[] {init.id, duration} );

		return init;
	}

	@GET
	@Path("next")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ProberResponse storeAndNext( @QueryParam("id") String id,
										@QueryParam("hasWinner") @DefaultValue("true") boolean hasWinner,
										@QueryParam("winner") @DefaultValue("-1") int winner ){
		long start = System.nanoTime();
		try{
			return prober.storeAndNext( id, hasWinner, winner );
		}
		catch( IllegalStateException ex ){
			throw new WebApplicationException( ex, Response.Status.FORBIDDEN );
		}
		catch( IllegalArgumentException ex ){
			throw new WebApplicationException( ex, Response.Status.BAD_REQUEST );
		}
		finally{
			long duration = (System.nanoTime()-start)/1000000;
			LOGGER.log( Level.INFO, "GET next of ID ''{0}'' took {1}ms", new Object[] {id, duration} );
		}
	}

	@GET
	@Path("get")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ProbeConfiguration getConfiguration( @QueryParam("id") String id ){
		long start = System.nanoTime();
		try{
			return prober.getConfiguration( id );
		}
		catch( IllegalStateException ex ){
			throw new WebApplicationException( ex, Response.Status.FORBIDDEN );
		}
		catch( IllegalArgumentException ex ){
			throw new WebApplicationException( ex, Response.Status.BAD_REQUEST );
		}
		finally{
			long duration = (System.nanoTime()-start)/1000000;
			LOGGER.log( Level.INFO, "GET configuration of ID ''{0}'' took {1}ms", new Object[] {id, duration} );
		}
	}


	@GET
	@Path("store")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public boolean storeConfiguration( @Context HttpServletRequest request, @QueryParam("id") String id ){
		long start = System.nanoTime();
		PartnerConfiguration config;

		try{
			config = prober.getParnterConfiguration( id );
		}
		catch( IllegalStateException ex ){
			throw new WebApplicationException( ex, Response.Status.FORBIDDEN );
		}
		catch( IllegalArgumentException ex ){
			throw new WebApplicationException( ex, Response.Status.BAD_REQUEST );
		}

		StringBuilder url = new StringBuilder();
		url.append( "http://localhost:" )
			.append( request.getLocalPort() )
			.append( request.getContextPath() )
			.append( request.getServletPath() )
			.append( WizardRESTService.URL_PATTERN );

		Client client = new Client( PartnerConfigurationCache.CONFIG.getClientJacksonJson() );
		WebResource.Builder builder = client.resource( url.toString() )
			.type( MediaType.APPLICATION_JSON_TYPE )
			.accept( MediaType.APPLICATION_JSON_TYPE );

		try{
			return builder.post( Boolean.class, config );
		}
		catch( ClientHandlerException|UniformInterfaceException ex ){
			throw new WebApplicationException( ex, Response.Status.INTERNAL_SERVER_ERROR );
		}
		finally{
			long duration = (System.nanoTime()-start)/1000000;
			LOGGER.log( Level.INFO, "GET store of ID ''{0}'' took {1}ms", new Object[] {id, duration} );
		}
	}


	@Override
	public void contextInitialized( ServletContextEvent contextEvent ){
		this.partnerConfigurationMaster = PartnerConfigurationCache.CONFIG.getPartnerConfiguration();

		try{
			InputStream stream = contextEvent.getServletContext().getResourceAsStream( QUERY_CONFIG_FILE_PATH );

			Unmarshaller unmarshaller = JAXBContext.newInstance( ServiceConfiguration.class )
				.createUnmarshaller();
			ServiceConfiguration config = (ServiceConfiguration) unmarshaller.unmarshal( stream );

			Map<String, Integer> queryGenerators = toPositionMap( config.queryGenerators );
			this.prober = new PartnerProber( queryGenerators, PartnerConfigurationCache.CONFIG.getPartnerConfiguration() );

			this.proberQueries = config.getProberKeywords();
		}
		catch( JAXBException ex ){
			throw new RuntimeException( "Unable to load XML-Konfiguration from resource file '"+QUERY_CONFIG_FILE_PATH+"'. Service terminated!", ex );
		}
	}

	@Override
	public void contextDestroyed( ServletContextEvent sce ){
	}


	private static <T> Map<T, Integer> toPositionMap( List<T> list ){
		Map<T, Integer> map = new HashMap<>( list.size() );
		int positionCounter = 0;
		for( T entry: list ){
			map.put( entry, positionCounter++ );
		}

		return map;
	}

}
