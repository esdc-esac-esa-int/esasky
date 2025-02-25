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

package esac.archive.esasky.cl.web.client.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;

public class TapRowList {

    private List<TapMetadata> metadata = new LinkedList<TapMetadata>();
    private ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
    private int nDataRowsInMOC = 0;
    int raColumnIndex = -1;
    int decColumnIndex = -1;
    
    public TapRowList() {
    }
    
    public int getNDataRowsInMOC() {
    	return nDataRowsInMOC;
    }
    
    public List<TapMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<TapMetadata> metadata) {
        this.metadata = metadata;
    }

    public ArrayList<ArrayList<Object>> getData() {
        return data;
    }

    public void setData(ArrayList<ArrayList<Object>> data) {
        this.data = data;
    }

    public void addDataRow(ArrayList<Object> arrayList) {
        this.data.add((ArrayList<Object>) arrayList);
    }
    
    public void addDataRows(ArrayList<ArrayList<Object>> arrayList) {
    	this.data.addAll(arrayList);
    }

    public ArrayList<Object> getDataRow(int index) {
        return this.data.get(index);
    }
    
    public ArrayList<Object> getDataRow(String columnName, String value) {
        final int index = getColumnIndex(columnName);
        if (index >= 0) {
            for (ArrayList<Object> row : this.data) {
                if (row.get(index).equals(value)) {
                    return row;
                }
            }
        }
        
        return null;
    }
    
    public int getColumnIndex (String columnName) {
        for (TapMetadata currentMd : metadata) {
            if (currentMd.getName().equals(columnName)) {
                return  metadata.indexOf(currentMd);
            }
        }
        
        return -1;
    }
    
    public void setRaDecColumnIndexes() {
    	  for(TapMetadata currentMd : metadata) {
          	if(currentMd.getName().equals("ra") || currentMd.getName().equals("ra_deg") || currentMd.getName().equals("s_ra")) {
          		raColumnIndex = metadata.indexOf(currentMd); 
          	}else if(currentMd.getName().equals("dec") || currentMd.getName().equals("dec_deg") || currentMd.getName().equals("s_dec")) {
          		decColumnIndex = metadata.indexOf(currentMd); 
          	}
          }
    	
    	if(raColumnIndex < 0 || decColumnIndex < 0) {
    		Log.error("[TapRowList] Ra or Dec column could not be set"); 
    	}
    }
    
    public String getDataValue(String columnName, int entryNumber) {
        final int index = getColumnIndex(columnName); 
        if (index >= 0) {
            
            Object value = getDataRow(entryNumber).get(index);
            if (value instanceof Double) {
                return Double.toString((Double) value);
            } else if (value instanceof Integer) {
                return Integer.toString((Integer) value);
            } else if (value instanceof String) {
                return (String) value;
            }
        }
        
        return null;
    }
    
}
