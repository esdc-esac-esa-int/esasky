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
