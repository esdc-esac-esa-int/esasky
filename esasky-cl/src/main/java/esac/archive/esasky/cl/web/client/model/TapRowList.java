package esac.archive.esasky.cl.web.client.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TapRowList {

    private List<TapMetadata> metadata = new LinkedList<TapMetadata>();
    private ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
    
    public TapRowList() {
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
