package esac.archive.ammi.ifcs.model.shared;

import esac.archive.ammi.ifcs.model.shared.ESASkySSOSearchResultList;
import esac.archive.ammi.ifcs.model.shared.ESASkySearchResult;

public class ESASkyGeneralResultList {

    private ESASkySSOSearchResultList ssoDnetResults;
    private ESASkySearchResult simbadResult;

    public ESASkyGeneralResultList() {

    }

    public ESASkySSOSearchResultList getSsoDnetResults() {
        return ssoDnetResults;
    }

    public void setSsoDnetResults(ESASkySSOSearchResultList ssoDnetResults) {
        this.ssoDnetResults = ssoDnetResults;
    }

    public ESASkySearchResult getSimbadResult() {
        return simbadResult;
    }

    public void setSimbadResult(ESASkySearchResult simbadResult) {
        this.simbadResult = simbadResult;
    }

}
