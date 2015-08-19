package eu.eexcess.partnerwizard.webservice;

/**
 *
 *
 * @author Heimo Gursch <gursch@tugraz.at>
 * @date 2015-08-18
 */
import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/probe")
public class ProbeService{

	@GET
	@Path("test")
	@Produces(MediaType.APPLICATION_JSON)
	public String probe( ){
//		PartnerConfiguration partnerConfig = PartnerConfigurationCache.CONFIG.getPartnerConfiguration();

//		QueryGeneratorApi queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator( "eu.eexcess.partnerrecommender.reference.LuceneQueryGenerator" );
		QueryGeneratorApi queryGenerator = PartnerConfigurationCache.CONFIG.getQueryGenerator( "eu.eexcess.partnerrecommender.reference.OrQueryGenerator" );

		SecureUserProfile prof = new SecureUserProfile();
		prof.contextKeywords.add( new ContextKeyword("this is the query"));

		String query = queryGenerator.toQuery( prof );

		return query;
	}

}
