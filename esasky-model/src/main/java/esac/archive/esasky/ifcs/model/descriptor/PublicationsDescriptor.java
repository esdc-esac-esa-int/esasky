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

package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author ESDC team Copyright (c) 2017 - European Space Agency
 */
public class PublicationsDescriptor extends BaseDescriptor {

    /** ADS Author search related URL, must have a adsAuthorUrlReplace string value to replace by author name */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String adsAuthorUrl;

    /** ADS Author search value to replace in adsAuthorUrl for author name */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String adsAuthorUrlReplace;
    
    /** ADS names separator value for authors in authors field for publications by source response */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String adsAuthorSeparator;

    /** ADS max returned rows for publications by source response */
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private int adsPublicationsMaxRows;
    
    @Override
    public String getDescriptorId() {
        if(descriptorId == null || descriptorId.isEmpty()) {
            return "PUBLICATIONS_" + getMission();
        }
        return descriptorId;
    }

    @Override
    public String getIcon() {
        return "publications";
    }
    
    public String getAdsAuthorUrl() {
        return adsAuthorUrl;
    }

    public void setAdsAuthorUrl(String adsAuthorURL) {
        this.adsAuthorUrl = adsAuthorURL;
    }

    public String getAdsAuthorUrlReplace() {
        return adsAuthorUrlReplace;
    }

    public void setAdsAuthorUrlReplace(String adsAuthorUrlReplace) {
        this.adsAuthorUrlReplace = adsAuthorUrlReplace;
    }

    public String getAdsAuthorSeparator(){
        return adsAuthorSeparator;
    }

    public void setAdsAuthorSeparator(String adsAuthorSeparator){
        this.adsAuthorSeparator = adsAuthorSeparator;
    }
    
    public int getAdsPublicationsMaxRows(){
        return adsPublicationsMaxRows;
    }

    public void setAdsPublicationsMaxRows(int adsPublicationsMaxRows){
        this.adsPublicationsMaxRows = adsPublicationsMaxRows;
    }
}
