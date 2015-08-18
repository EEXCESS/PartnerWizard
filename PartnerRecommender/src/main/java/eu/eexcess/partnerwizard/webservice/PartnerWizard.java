package eu.eexcess.partnerwizard.webservice;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import eu.eexcess.partnerwizard.webservice.tool.PartnerStandaloneServer;
import java.util.TimeZone;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-08-17
 */
public class PartnerWizard{
	private static final Logger LOG = LoggerFactory.getLogger( PartnerStandaloneServer.class );

	private static Server server = null;

	/**
	 * Starts the server if it is not already running
	 *
	 * @param port Port the server is stated on
	 */
	public static synchronized void start( int port ){
		if( server!=null ){
			throw new IllegalStateException( "Server is already running" );
		}

		// Configuration context of complete server
		ServletContextHandler servletHandler = new ServletContextHandler();
		servletHandler.setContextPath( "/api" );

		// Configure the servlet container running the Jersey servlets
		ServletHolder servletHolder = new ServletHolder( ServletContainer.class );
		// Setting the init order to 1 will load the servlets on statup
		servletHolder.setInitOrder( 1 );
		servletHolder.setInitParameter( "com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig" );
		servletHolder.setInitParameter( "com.sun.jersey.api.json.POJOMappingFeature", "true" );
		// Specify package containing Jersey Servlets
		servletHolder.setInitParameter( "com.sun.jersey.config.property.packages", "eu.eexcess.partnerwizard.webservice.servlets" );
//		// Extra debugging output, comment in if needed
//		servletHolder.setInitParameter("com.sun.jersey.config.feature.Debug", "true");
//		servletHolder.setInitParameter("com.sun.jersey.config.feature.Trace", "true");
//		servletHolder.setInitParameter("com.sun.jersey.spi.container.ContainerRequestFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
//		servletHolder.setInitParameter("com.sun.jersey.spi.container.ContainerResponseFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
		// Add servlets to server
		servletHandler.addServlet( servletHolder, "/*" );


		// Create and configure handler to serve static files
		ResourceHandler staticFileHandler = new ResourceHandler();
		staticFileHandler.setDirectoriesListed( false );
		staticFileHandler.setResourceBase( "./src/main/web" );
		staticFileHandler.setWelcomeFiles( new String[] {"index.html"} );

		// Log HTTP Requests
		NCSARequestLog requestLog = new NCSARequestLog();
		// Set time zone to the one used by the JVM
		requestLog.setLogTimeZone( TimeZone.getDefault().getID() );
		// Handler needed for Logging
		RequestLogHandler requestLogHandler = new RequestLogHandler();
		requestLogHandler.setRequestLog(requestLog);


		// Create server and set configuration
		server = new Server( port );

		// Add all Handlers to the server
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		HandlerCollection handlers = new HandlerCollection();
		handlers.setHandlers( new Handler[] {contexts, staticFileHandler, servletHandler, new DefaultHandler(), requestLogHandler } );
		server.setHandler( handlers );


//		// Configure thread pool manually, comment in if needed
//		QueuedThreadPool queuedThreadPool = new QueuedThreadPool( 10 );
//		queuedThreadPool.setName( "HttpServ" );
//		server.setThreadPool( queuedThreadPool );


		try{
			server.start();
		}
		catch( Exception e ){
			LOG.error( "Server could not be started", e );
		}
	}

	/**
	 * Stops the server if it is running
	 */
	public static synchronized void stop(){
		if( server==null ){
			throw new IllegalStateException( "Server not running" );
		}
		try{
			server.stop();
		}
		catch( Exception e ){
			LOG.warn( "Server Could not be stoped", e );
		}
	}

	/**
	 * Starts the server listening to the specified port.
	 *
	 * @param args Must contain the port number as the only entry
	 */
	public static void main( String[] args ) {
		if( args.length!=1 ){
			LOG.info( "USAGE: PartnerStandaloneServer <port-number>" );
			LOG.info( "EXAMPLE: PartnerStandaloneServer 8080" );
			System.exit( -1 );
		}
		int portNumber = 8080;
		try{
			portNumber = Integer.parseInt( args[0] );
		}
		catch( NumberFormatException numberFormatException ){
			LOG.info( "USAGE: PartnerStandaloneServer <port-number>" );
			LOG.info( "EXAMPLE: PartnerStandaloneServer 8080" );
			System.exit( -1 );
		}

		start( portNumber );
	}

}
