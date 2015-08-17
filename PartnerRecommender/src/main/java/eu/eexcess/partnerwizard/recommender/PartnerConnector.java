/**
 * Copyright (C) 2014
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.eexcess.partnerwizard.recommender;

import java.io.IOException;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;
import eu.eexcess.partnerrecommender.reference.PartnerConnectorBase;

public class PartnerConnector extends PartnerConnectorBase implements PartnerConnectorApi {

    private static final Logger LOGGER = Logger.getLogger(PartnerConnector.class.getName());

    public PartnerConnector() {

    }

    /**
     * Call to get the documents from the partners API
     */
    @Override
    public Document queryPartner(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger) throws IOException {
        return null;
    }

    /**
     * Call to get the details (more facets) of an document
     */
    @Override
    public Document queryPartnerDetails(PartnerConfiguration partnerConfiguration, DocumentBadge document, PartnerdataLogger logger) throws IOException {

        return null;
    }

    /**
     * Could be used if we have no transformation file for that Partner.
     */
    @Override
    public ResultList queryPartnerNative(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger dataLogger) throws IOException {
        return null;
    }
}
