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

package esac.archive.esasky.cl.web.client.utility;

import java.util.List;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;

import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;
import esac.archive.esasky.ifcs.model.shared.ESASkyTarget;

public final class ParseUtils {

    /** Interface List<ESASkySearchResult> mapper. */
    public interface ESASkySearchResultListMapper extends ObjectMapper<List<ESASkySearchResult>> {
    }
    
    /** Interface ESASkyTarget mapper. */
    public interface ESASkyTargetMapper extends ObjectMapper<ESASkyTarget> {
    }
    
    public static List<ESASkySearchResult> parseJsonSearchResults(String JsonSearchResults) {
        ESASkySearchResultListMapper mapper = GWT.create(ESASkySearchResultListMapper.class);
        return mapper.read(JsonSearchResults);
    }
 
    public static ESASkyTarget parseJsonTarget(String JsonESASkyTarget) {
        ESASkyTargetMapper mapper = GWT.create(ESASkyTargetMapper.class);
        return mapper.read(JsonESASkyTarget);
    }
}
