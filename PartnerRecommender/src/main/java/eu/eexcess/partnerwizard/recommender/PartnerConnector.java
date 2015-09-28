/**
 * Copyright (C) 2014 "Kompetenzzentrum fuer wissensbasierte Anwendungen
 * Forschungs- und EntwicklungsgmbH" (Know-Center), Graz, Austria,
 * office@know-center.at.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package eu.eexcess.partnerwizard.recommender;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.api.EEXCESSDataTransformationException;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;
import java.util.logging.Level;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.lang.text.StrSubstitutor;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;

/**
 *
 * @author hgursch
 */
public class PartnerConnector implements PartnerConnectorApi{
	private static final Logger LOGGER = Logger.getLogger( PartnerConnector.class.getName() );
	private static final String NUMBER_OF_RESULTS_PARAMETER_NAME = "numResults";
	private static final String NUMBER_OF_RESULTS_PARAMETER = "25";
	private static final String USER_NAME_PARAMETER_NAME = "userName";
	private static final String PASSWORD_PARAMETER_NAME = "password";
	private static final String API_KEY_PARAMETER_NAME = "apiKey";

	@Override
	public Document queryPartner( PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger ) throws IOException{
		Map<String, String> parameters = new HashMap<>();

		QueryGeneratorApi queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator( partnerConfiguration.getQueryGeneratorClass() );
		String query = queryGenerator.toQuery( userProfile );
		parameters.put( "query", urlEncode( query ) );

		if( userProfile.numResults!=null&&userProfile.numResults>0 ){
			parameters.put( NUMBER_OF_RESULTS_PARAMETER_NAME, userProfile.numResults.toString() );
		}
		else{
			parameters.put( NUMBER_OF_RESULTS_PARAMETER_NAME, NUMBER_OF_RESULTS_PARAMETER );
		}

		String userName = partnerConfiguration.getUserName();
		if( userName!=null&&!userName.isEmpty() ){
			parameters.put( USER_NAME_PARAMETER_NAME, userName );
		}

		String password = partnerConfiguration.getPassword();
		if( password!=null&&!password.isEmpty() ){
			parameters.put( PASSWORD_PARAMETER_NAME, password );
		}

		String apiKey = partnerConfiguration.getApiKey();
		if( apiKey!=null&&!apiKey.isEmpty() ){
			parameters.put( API_KEY_PARAMETER_NAME, apiKey );
		}

		String searchUrl = StrSubstitutor.replace( partnerConfiguration.getSearchEndpoint(), parameters );

		Client client = new Client( PartnerConfigurationCache.CONFIG.getClientDefault() );
		WebResource.Builder builder = client.resource( searchUrl )
				.accept( MediaType.APPLICATION_JSON_TYPE )
				.accept( MediaType.APPLICATION_XML_TYPE );
		ClientResponse response;
		try{
			response = builder.get( ClientResponse.class );
		}
		catch( ClientHandlerException|UniformInterfaceException ex ){
			throw new IOException( "Cannot query partner API!", ex );
		}
		try{
			return parseResponse( response );
		}
		catch( EEXCESSDataTransformationException ex ){
			throw new IOException( "The partner's response could not be parsed", ex );
		}
	}

	@Override
	public Document queryPartnerDetails( PartnerConfiguration partnerConfiguration, DocumentBadge document, PartnerdataLogger logger ){
		LOGGER.log( Level.SEVERE, "Call of not implemented queryPartnerDetails method. Returned null." );
		return null;
	}

	@Override
	public ResultList queryPartnerNative( PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger ) throws IOException{
		return null;
	}

	private Document parseResponse( ClientResponse response ) throws EEXCESSDataTransformationException{
		MediaType type = response.getType();

		if( type.equals( MediaType.APPLICATION_ATOM_XML_TYPE ) ){
			return response.getEntity( Document.class );
		}
		else if( type.equals( MediaType.APPLICATION_JSON_TYPE ) ){
			String jsonString = response.getEntity( String.class );
			String xmlString = jsonToXmlString( jsonString );
			return xmlStringToDocument( xmlString );
		}
		else{
			String plainContent = response.getEntity( String.class ).trim();
			if( plainContent.startsWith( "{" ) ){
				String xmlString = jsonToXmlString( plainContent );
				return xmlStringToDocument( xmlString );
			}
			else{
				return xmlStringToDocument( plainContent );
			}
		}
	}

	private String jsonToXmlString( String jsonString ){
		XMLSerializer serializer = new XMLSerializer();
		JSON json = JSONSerializer.toJSON( jsonString );
		serializer.setTypeHintsEnabled( false );

		return serializer.write( json );
	}

	private Document xmlStringToDocument( String xmlData ) throws EEXCESSDataTransformationException{
		try{
//			// Alternative implementation:
//			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//			return builder.parse( new InputSource( new StringReader( xmlData ) ) );
			SAXReader reader = new SAXReader();
			org.dom4j.Document dom4jDoc = reader.read( new StringReader( xmlData ) );

			DOMWriter writer = new DOMWriter();
			org.w3c.dom.Document w3cDoc = writer.write( dom4jDoc );

			return w3cDoc;
		}
		catch( DocumentException ex ){
			LOGGER.log( Level.WARNING, "XML could not be parsed into a valid org.w3c.dom.Document.", ex );
			throw new EEXCESSDataTransformationException( ex );
		}

	}

	private static String urlEncode( String field ){
		try{
			return URLEncoder.encode( field, "UTF-8" );
		}
		catch( UnsupportedEncodingException unsupportedEncodingException ){
			LOGGER.log( Level.SEVERE, "No support for UTF-8 encoding found. URL-Parameters could not be encoded!" );
			throw new RuntimeException( "No support for UTF-8 encoding found. URL-Parameters could not be encoded!" );
		}
	}
}
