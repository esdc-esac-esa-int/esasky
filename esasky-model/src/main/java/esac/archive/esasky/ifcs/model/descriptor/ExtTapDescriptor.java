package esac.archive.esasky.ifcs.model.descriptor;

import java.util.ArrayList;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gwt.http.client.URL;

import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;


/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ExtTapDescriptor extends BaseDescriptor {

    private String whereADQL;
    private String dateADQL;
    private String selectADQL;
    private String responseFormat;
    private String searchFunction;
    private Map<String, Map<String, ArrayList<String>>> collections;
    private boolean isInBackend = true;
    private String treeMapType;
    private ExtTapDescriptor parent;
    private String ingestedTable;
    private boolean isObsCore;
    private String intersectColumn;
    

    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String tapUrl;
    
    
    public void copyParentValues(ExtTapDescriptor parent) {
    	//Creates a shallow copy of the parent which is fine since String and Boolean is immutable
    	this.parent = parent;
    	
    	whereADQL = parent.getWhereADQL();
    	dateADQL = parent.getDateADQL();
    	selectADQL = parent.getSelectADQL();
    	responseFormat = parent.getResponseFormat();
    	searchFunction = parent.getSearchFunction();
    	isInBackend = false;
    	tapUrl = parent.getTapUrl();
    	ingestedTable = parent.getIngestedTable();
    	isObsCore = parent.getIsObsCore();
    	intersectColumn = parent.getIntersectColumn();
    	
    	//From BaseDescriptor
    	setGuiShortName(parent.getGuiShortName());
    	setGuiLongName(parent.getGuiLongName());
    	setMission(parent.getMission());
    	setCreditedInstitutions(parent.getCreditedInstitutions());
    	setPrimaryColor(parent.getPrimaryColor());
    	setTapTable(parent.getTapTable());
    	setArchiveProductURI(parent.getArchiveProductURI());
    	setFovLimit(parent.getFovLimit());
    	setTapRaColumn(parent.getTapRaColumn());
    	setTapDecColumn(parent.getTapDecColumn());
    	setTapSTCSColumn(parent.getTapSTCSColumn());
    	setUniqueIdentifierField(parent.getUniqueIdentifierField());
    	setShapeLimit(parent.getShapeLimit());
    	
    	setArchiveURL(parent.getArchiveURL());
    	setSampEnabled(parent.getSampEnabled());
    }

	@Override
	public String generateId() {
		return getMission() + " EXT_TAP_ " + generateNextTabCount();
	}

	public String getTapUrl() {
		return tapUrl;
	}

	public void setTapUrl(String tapUrl) {
		this.tapUrl = tapUrl;
	}

	public String getWhereADQL() {
		return whereADQL;
	}

	public void setWhereADQL(String whereADQL) {
		this.whereADQL = whereADQL;
	}
	
	public String getDateADQL() {
		return dateADQL;
	}

	public void setDateADQL(String dateADQL) {
		this.dateADQL = dateADQL;
	}

	public String getResponseFormat() {
		return responseFormat;
	}

	public void setResponseFormat(String responseFormat) {
		this.responseFormat = responseFormat;
	}

	public String getSearchFunction() {
		return searchFunction;
	}

	public void setSearchFunction(String searchFunction) {
		this.searchFunction = searchFunction;
	}

	public boolean isInBackend() {
		return isInBackend;
	}

	public void setInBackend(boolean isInBackend) {
		this.isInBackend = isInBackend;
	}

	public String getSelectADQL() {
		return selectADQL;
	}

	public void setSelectADQL(String selectADQL) {
		this.selectADQL = selectADQL;
	}

	public Map<String, Map<String, ArrayList<String>>> getCollections() {
		return collections;
	}

	public void setCollections(Map<String, Map<String, ArrayList<String>>> collections) {
		this.collections = collections;
	}

	public String getTreeMapType() {
		return treeMapType;
	}

	public void setTreeMapType(String treeMapType) {
		this.treeMapType = treeMapType;
	}

	public ExtTapDescriptor getParent() {
		return parent;
	}

	public void setParent(ExtTapDescriptor parent) {
		this.parent = parent;
	}

	public String getIngestedTable() {
		return ingestedTable;
	}

	public void setIngestedTable(String ingestedTable) {
		this.ingestedTable = ingestedTable;
	}

    public String getTapQuery(String tapContext, String metadataAdql, String responseFormat) {
        Long timecall = System.currentTimeMillis();
        String adqlParameterAndValue = "";
        String adql = URL.encodeQueryString(metadataAdql);
        if(!adql.isEmpty()) {
            adqlParameterAndValue = "&" + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + adql;
        }

        Log.debug("[TAPUtils/getTAPQuery()] timecall " + timecall);
        String url = tapContext + "&" + EsaSkyConstants.EXT_TAP_TARGET_FLAG
                    + "=" + getMission();
        if(!isInBackend()) {
            String tapUrl = getTapUrl();
            if(!tapUrl.endsWith("/sync")) {
                tapUrl += "/sync";
            }
            url += "&" + EsaSkyConstants.EXT_TAP_URL + "=" + getTapUrl() +
                    "&" + EsaSkyConstants.EXT_TAP_RESPONSE_FORMAT + "=" + getResponseFormat();
        }
        return url + adqlParameterAndValue;
    }

    @Override
    public String getIcon() {
        return "ext_tap";
    }
    
    @Override
    public String getDescriptorId() {
        if(descriptorId == null || descriptorId.isEmpty()) {
            return "EXT_TAP_" + getCreditedInstitutions();
        }
        return descriptorId;
    }

	public boolean getIsObsCore() {
		return isObsCore;
	}

	public void setIsObsCore(boolean isObsCore) {
		this.isObsCore = isObsCore;
	}
	
	public boolean hasParent(ExtTapDescriptor possibleParent) {
		if(parent != null && (possibleParent == parent || (parent.getParent() != null && possibleParent == parent.getParent()))) {
			return true;
		}else {
			return false;
		}
	}
	
	@JsonIgnore
	public ExtTapDescriptor getLastParent() {
		if(parent != null) {
			return parent.getLastParent();
		}else {
			return this;
		}
	}

	public String getIntersectColumn() {
		if(intersectColumn != null && intersectColumn != "") {
			return intersectColumn;
		}
		return getTapSTCSColumn();
	}

	public void setIntersectColumn(String intersectColumn) {
		this.intersectColumn = intersectColumn;
	}
	
	
}
