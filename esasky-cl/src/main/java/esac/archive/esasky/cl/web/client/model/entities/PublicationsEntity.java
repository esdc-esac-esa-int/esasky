package esac.archive.esasky.cl.web.client.model.entities;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.model.SourceShape;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.SourceConstant;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;

public class PublicationsEntity extends CatalogEntity {

    Map<String, Integer> sourceIndexes = new HashMap<String, Integer>();
    
    private int sourceLimit;
    private String orderByDescription = "";

    public PublicationsEntity(PublicationsDescriptor pubDescriptor, CountStatus countStatus,
            JavaScriptObject pubOverlay, SkyViewPosition skyViewPosition,
            String esaSkyUniqObsId) {
        super(pubDescriptor, countStatus, pubOverlay, skyViewPosition, esaSkyUniqObsId);
    }
    
    @Override
    public void addShapes(TapRowList rowList, GeneralJavaScriptObject javaScriptObject) {
    	if(Modules.useTabulator) {
    		return; //TODO
    	} else {
	        sourceIndexes.clear();
	        super.addShapes(rowList, javaScriptObject);
    	}
    }
    
    @Override
    public SourceShape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject row) {
    	if(Modules.useTabulator) {
    		return null; //TODO
    	} else {
    		SourceShape mySource = new SourceShape();
    		mySource.setShapeId(rowId);
    		mySource.setDec((getTAPDataByTAPName(rowList, rowId,
    				descriptor.getTapDecColumn())).toString());
    		mySource.setRa((getTAPDataByTAPName(rowList, rowId,
    				descriptor.getTapRaColumn())).toString());
    		mySource.setSourceName(((String) getTAPDataByTAPName(rowList, rowId,
    				descriptor.getUniqueIdentifierField())).toString());
    		
    		Map<String, String> details = new HashMap<String, String>();
    		
    		details.put(SourceConstant.SOURCE_NAME, mySource.getSourceName());
    		
    		details.put(EsaSkyWebConstants.SOURCE_TYPE,
    				EsaSkyWebConstants.SourceType.PUBLICATION.toString());
    		details.put(SourceConstant.CATALOGE_NAME, getEsaSkyUniqId());
    		details.put(SourceConstant.IDX, Integer.toString(rowId));
    		
    		sourceIndexes.put(mySource.getSourceName(), rowId);
    		
    		details.put(SourceConstant.EXTRA_PARAMS, "BIBCOUNT");
    		details.put("BIBCOUNT", rowList.getDataValue(SourceConstant.BIBCOUNT, rowId));
    		
    		mySource.setJsObject(AladinLiteWrapper.getAladinLite().newApi_createSourceJSObj(
    				mySource.getRa(), mySource.getDec(), details, rowId));
    		return mySource;
    	}
    }

    public int getSourceIdx(String sourceName) {
        if (sourceIndexes.containsKey(sourceName)) {
            return sourceIndexes.get(sourceName);
        }

        return -1;
    }
    
    @Override
    public boolean isSampEnabled() {
    	return false;
    }
    
    @Override
    public boolean isRefreshable() {
    	return false;
    }
    
    @Override
    public boolean isCustomizable() {
    	return false;
    }
    
    @Override
    public int getSourceLimit() {
    	return sourceLimit;
    }
    
	public void setPublicationsSourceLimit(int sourceLimit) {
		this.sourceLimit = sourceLimit;
	}
	
	@Override
	protected String getOrderByDescription() {
		return orderByDescription;
	}
	
	public void setOrderByDescription(String orderBy) {
		this.orderByDescription = orderBy;
	}
}
