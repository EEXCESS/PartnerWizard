package eu.eexcess.partnerwizard.datalayer;

import org.w3c.dom.Document;

import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.partnerdata.api.ITransformer;
import eu.eexcess.partnerdata.reference.Transformer;

public class PartnerWizardTransformer extends Transformer implements ITransformer {

    @Override
    protected ResultList postProcessResults(Document orgPartnerResult, ResultList resultList) {
        resultList.totalResults = resultList.results.size();
        return super.postProcessResults(orgPartnerResult, resultList);
    }
}
