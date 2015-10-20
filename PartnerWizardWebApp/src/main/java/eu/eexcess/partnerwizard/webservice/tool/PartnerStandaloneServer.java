/* Copyright (C) 2014
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH"
(Know-Center), Graz, Austria, office@know-center.at.

Licensees holding valid Know-Center Commercial licenses may use this file in
accordance with the Know-Center Commercial License Agreement provided with
the Software or, alternatively, in accordance with the terms contained in
a written agreement between Licensees and Know-Center.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.eexcess.partnerwizard.webservice.tool;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 * Standalone server to test the web service.
 *
 * @author rkern@know-center.at
 */
public class PartnerStandaloneServer {
    private static Server server;
    private static final Logger LOGGER = Logger.getLogger(PartnerStandaloneServer.class.getName());

    private PartnerStandaloneServer() {
    }

    public static synchronized void start(int port) {
        if (server != null) {
            throw new IllegalStateException("Server is already running");
        }

		// Configuration Handler to add the servlet
		ServletContextHandler probeServletHandler = new ServletContextHandler();
		probeServletHandler.setContextPath( "/api" );

		// Configure the servlet container containg the servelt for training
		// the partner connector
		ServletHolder probeServletHolder = new ServletHolder( ServletContainer.class );
		// Setting the init order to 1 will load the servlets on statup
		probeServletHolder.setInitOrder( 1 );
		probeServletHolder.setInitParameter( "com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig" );
		probeServletHolder.setInitParameter( "com.sun.jersey.api.json.POJOMappingFeature", "true" );
		// Specify package containing Jersey Servlets of the partner connector
		probeServletHolder.setInitParameter( "com.sun.jersey.config.property.packages", "eu.eexcess.partnerwizard.webservice" );
//		// Extra debugging output, comment in if needed
//		probeServletHolder.setInitParameter("com.sun.jersey.config.feature.Debug", "true");
//		probeServletHolder.setInitParameter("com.sun.jersey.config.feature.Trace", "true");
//		probeServletHolder.setInitParameter("com.sun.jersey.spi.container.ContainerRequestFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
//		probeServletHolder.setInitParameter("com.sun.jersey.spi.container.ContainerResponseFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
		// Add servlets to server
		probeServletHandler.addServlet( probeServletHolder, "/*" );


		// Repeat exaclty the same from above to add the EEXCESS partnerwebservice
		ServletContextHandler partnerServletHandler = new ServletContextHandler();
		partnerServletHandler.setContextPath( "/api" );
		ServletHolder partnerServletHolder = new ServletHolder( ServletContainer.class );
		partnerServletHolder.setInitOrder( 1 );
		partnerServletHolder.setInitParameter( "com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig" );
		partnerServletHolder.setInitParameter( "com.sun.jersey.api.json.POJOMappingFeature", "true" );
		partnerServletHolder.setInitParameter( "com.sun.jersey.config.property.packages", "eu.eexcess.partnerwebservice" );
		partnerServletHandler.addServlet( partnerServletHolder, "/*" );


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
		handlers.setHandlers( new Handler[] {contexts, staticFileHandler, probeServletHandler, partnerServletHandler, new DefaultHandler(), requestLogHandler } );
		server.setHandler( handlers );



		// Configure thread pool manually, comment in if needed
//		QueuedThreadPool queuedThreadPool = new QueuedThreadPool( 10 );
//		queuedThreadPool.setName( "HttpServ" );
//		server.setThreadPool( queuedThreadPool );


		try{
			server.start();
		}
		catch( Exception e ){
			LOGGER.log( Level.SEVERE, "Server could not be started", e );
		}
    }

    public static synchronized void stop() {
        if (server == null) {
            throw new IllegalStateException("Server not running");
        }
        try {
            server.stop();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Server Could not be stoped", e);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            LOGGER.log(Level.INFO, "USAGE: PartnerStandaloneServer <port-number>");
            System.exit(-1);
        }

        start(Integer.parseInt(args[0]));
    }

}
