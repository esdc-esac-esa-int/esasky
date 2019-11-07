package esac.archive.esasky.ifcs.model.shared;

import java.util.HashMap;
import java.util.Map;


public class ObsCoreCollection {
    
	private static Map<String, String> map = new HashMap<String, String>() {{
        put("image", "Images");
        put("spectrum", "Spectra");
        put("cube", "Cubes");
        put("timeseries", "Time series");
        put("http://www.opencadc.org/caom2/DataProductType#catalog", "Catalogs");
        put("measurements", "Measurements");
        put("visibility", "Visibility");
    }};

    public static String get(String key) {
    	if( map.containsKey(key)){
    		return map.get(key);
    	}
    	
    	return key;
    }
}
