package esac.archive.esasky.ifcs.model.shared;

import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResultList;
import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;

public class ESASkyGeneralResultList {

    private ESASkySSOSearchResultList ssoDnetResults;
    private ESASkySearchResult simbadResult;
    private ESASkyPublicationSearchResultList simbadAuthorResultWithWildcards;
    private ESASkyPublicationSearchResultList simbadAuthorResultExact;
    private ESASkyPublicationSearchResultList simbadBibcodeResultWithWildcards;
    private ESASkyPublicationSearchResultList simbadBibcodeResultExact;

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
    
    public ESASkyPublicationSearchResultList getSimbadAuthorResultWithWildcards() {
    	return simbadAuthorResultWithWildcards;
    }
    
    public void setSimbadAuthorResultWithWildcards(ESASkyPublicationSearchResultList simbadResult) {
    	this.simbadAuthorResultWithWildcards = simbadResult;
    }
    
    public ESASkyPublicationSearchResultList getSimbadAuthorResultExact() {
    	return simbadAuthorResultExact;
    }
    
    public void setSimbadAuthorResultExact(ESASkyPublicationSearchResultList simbadResult) {
    	this.simbadAuthorResultExact = simbadResult;
    }
    
    public ESASkyPublicationSearchResultList getSimbadBibcodeResultWithWildcards() {
    	return simbadBibcodeResultWithWildcards;
    }
    
    public void setSimbadBibcodeResultWithWildcards(ESASkyPublicationSearchResultList simbadResult) {
    	this.simbadBibcodeResultWithWildcards = simbadResult;
    }
    
    public ESASkyPublicationSearchResultList getSimbadBibcodeResultExact() {
    	return simbadBibcodeResultExact;
    }
    
    public void setSimbadBibcodeResultExact(ESASkyPublicationSearchResultList simbadResult) {
    	this.simbadBibcodeResultExact = simbadResult;
    }

}
