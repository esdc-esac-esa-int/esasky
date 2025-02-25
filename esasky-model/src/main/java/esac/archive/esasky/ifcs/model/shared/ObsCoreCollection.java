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

import java.util.HashMap;
import java.util.Map;


public class ObsCoreCollection {
    
	private static Map<String, String> map = new HashMap<String, String>() {/**
         * 
         */
        private static final long serialVersionUID = -3907810103082399126L;

    {
        put("image", "Images");
        put("spectrum", "Spectra");
        put("cube", "Cubes");
        put("timeseries", "Time series");
        put("http://www.opencadc.org/caom2/DataProductType#catalog", "Catalogues");
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
