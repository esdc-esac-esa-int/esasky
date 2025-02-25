/*
ESASky
Copyright (C) 2025 Henrik Norman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.ifcs.model.shared;

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
