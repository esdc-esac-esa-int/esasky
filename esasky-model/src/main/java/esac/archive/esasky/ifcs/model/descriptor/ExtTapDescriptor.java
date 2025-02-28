/*
ESASky
Copyright (C) 2025 European Space Agency

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


import java.util.ArrayList;
import java.util.HashMap;

import com.allen_sauer.gwt.log.client.Log;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.ifcs.model.shared.contentdescriptors.UCD;


/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ExtTapDescriptor extends BaseDescriptor {

    private String whereADQL;
    private String dateADQL;
    private String selectADQL;
    private String responseFormat;
    private String searchFunction;
    private ArrayList<String> levelColumnNames;
    private HashMap<String, ExtTapTreeMapLevel> subLevels;
    private boolean isInBackend = true;
    private int treeMapLevel;
    private ExtTapDescriptor parent;
    private boolean isObsCore;
    private String intersectColumn;
    private String baseMission;
    

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
    	levelColumnNames = parent.getLevelColumnNames();
    	baseMission = parent.getBaseMission();
    	
    	isInBackend = true;
    	tapUrl = parent.getTapUrl();
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
		return getMission() + "_" + generateNextTabCount();
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

	public HashMap<String, ExtTapTreeMapLevel> getSubLevels() {
		return subLevels;
	}

	public ArrayList<String> getLevelColumnNames() {
		return levelColumnNames;
	}

	public void setLevelColumnNames(ArrayList<String> levelColumnNames) {
		this.levelColumnNames = levelColumnNames;
	}

	public void setSubLevels(HashMap<String, ExtTapTreeMapLevel> subLevels) {
		this.subLevels = subLevels;
	}

	public int getTreeMapLevel() {
		return treeMapLevel;
	}

	public void setTreeMapLevel(int treeMapLevel) {
		this.treeMapLevel = treeMapLevel;
	}

	public ExtTapDescriptor getParent() {
		return parent;
	}

	public void setParent(ExtTapDescriptor parent) {
		this.parent = parent;
	}

    public String getTapQuery(String tapContext, String metadataAdql, String responseFormat) {
        Long timecall = System.currentTimeMillis();
        String adqlParameterAndValue = "";
        if(!metadataAdql.isEmpty()) {
            adqlParameterAndValue = "&" + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + metadataAdql;
        }

        Log.debug("[TAPUtils/getTAPQuery()] timecall " + timecall);
        String url = tapContext + "&" + EsaSkyConstants.EXT_TAP_TARGET_FLAG
                    + "=" + getBaseMission();
        if(!isInBackend()) {
            String tapUrl = getTapUrl();
            if(!tapUrl.endsWith("/sync")) {
                tapUrl += "/sync";
            }
            url += "&" + EsaSkyConstants.EXT_TAP_URL_FLAG + "=" + tapUrl +
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
		if(intersectColumn != null && !"".equals(intersectColumn)) {
			return intersectColumn;
		}
		return getTapSTCSColumn();
	}

	public void setIntersectColumn(String intersectColumn) {
		this.intersectColumn = intersectColumn;
	}

	public String getBaseMission() {
		if(baseMission == null) {
			return getMission();
		}
		return baseMission;
	}

	public void setBaseMission(String baseMission) {
		this.baseMission = baseMission;
	}

	@Override
	public String getTapRaColumn() {
		return tapRaColumn;
	}

	@Override
	public void setTapRaColumn(String tapRaColumn) {
		this.tapRaColumn = tapRaColumn;
	}

	@Override
	public String getTapDecColumn() {
		return tapDecColumn;
	}
}
