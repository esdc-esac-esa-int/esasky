package esac.archive.esasky.cl.web.client.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.ifcs.model.shared.ESASkyResultMOC;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class TapRowList {

    private List<TapMetadata> metadata = new LinkedList<TapMetadata>();
    private ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
    private ESASkyResultMOC moc = new ESASkyResultMOC(2,-1);
    private int nDataRowsInMOC = 0;
    int raColumnIndex = -1;
    int decColumnIndex = -1;
    
    public TapRowList() {
    }
    
    public int getNDataRowsInMOC() {
    	return nDataRowsInMOC;
    }
    
    public ESASkyResultMOC getMOC() {
		return moc;
	}

	public void setMOC(ESASkyResultMOC moc) {
		this.moc = moc;
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
    
    public void updateMOC() {
    	if(raColumnIndex < 0 || decColumnIndex < 0) {
    		setRaDecColumnIndexes();
    	}
    	
    	while(nDataRowsInMOC < data.size()) {
    		ArrayList<Object> currentData = data.get(nDataRowsInMOC);
    		double ra = Double.parseDouble((String) currentData.get(raColumnIndex));
    		double dec = Double.parseDouble((String) currentData.get(decColumnIndex));

    		int nOrder = 9;
    		int ipix = AladinLiteWrapper.getAladinLite().getIpixFromRaDec(ra, dec, nOrder);
    		
    		moc.addData(nDataRowsInMOC, nOrder, ipix);
    		nDataRowsInMOC++;
    	}
    	Log.debug(Integer.toString(nDataRowsInMOC));
    }
    
    
   public ESASkyResultMOC createMOC() {
	   moc = new ESASkyResultMOC(2,-1);
	   int iPixIndex = getColumnIndex(EsaSkyConstants.Q3C_IPIX);
	   int orderIndex = getColumnIndex(EsaSkyConstants.Q3C_ORDER);
	   int countIndex = getColumnIndex(EsaSkyConstants.Q3C_COUNT);
	   
	   if(iPixIndex > -1) {
		   for(ArrayList<Object> row : data) {
			   int iPix = Integer.parseInt((String) row.get(iPixIndex));
			   int order = 8;
			   if(orderIndex > -1) {
				   order = Integer.parseInt((String) row.get(orderIndex));
			   }
			   int count = 1;
			   if(countIndex > -1 ) {
				   count = Integer.parseInt((String) row.get(countIndex));
			   }
			   moc.addData(order, iPix, count, false);
		   }
	   }
	   
	   return moc;
   }
   
   public ESASkyResultMOC createMOCFromIntegers() {
	   moc = new ESASkyResultMOC(2,-1);
	   int iPixIndex = getColumnIndex(EsaSkyConstants.Q3C_IPIX);
	   int orderIndex = getColumnIndex(EsaSkyConstants.Q3C_ORDER);
	   int countIndex = getColumnIndex(EsaSkyConstants.Q3C_COUNT);
	   
	   if(iPixIndex > -1) {
		   for(ArrayList<Object> row : data) {
			   int iPix = (int) row.get(iPixIndex);
			   int order = 8;
			   if(orderIndex > -1) {
				   order = (int) row.get(orderIndex);
			   }
			   int count = 1;
			   if(countIndex > -1 ) {
				   count = (int) row.get(countIndex);
			   }
			   moc.addData(order, iPix, count, false);
		   }
	   }
	   
	   return moc;
   }
}
