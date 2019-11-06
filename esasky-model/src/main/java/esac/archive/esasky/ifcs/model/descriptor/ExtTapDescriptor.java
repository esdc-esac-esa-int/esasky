package esac.archive.esasky.ifcs.model.descriptor;

import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ExtTapDescriptor extends BaseDescriptor {

    private String tapSTCSColumn;
    private String tapRaColumn;
    private String tapDecColumn;
    private String uniqueIdentifierField;
    private ArrayList<String> dataProductTypes;
    private String whereADQL;
    private String selectADQL;
    private String orderByADQL;
    private String responseFormat;
    private String searchFunction;
    private Map<String, ArrayList<String>> collections;
    private boolean isInBackend = true;
    private String treeMapType;
    private ExtTapDescriptor parent;
    private String ingestedTable;
    private int sourceLimit;
    

    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String tapUrl;
    
    
    public void copyParentValues(ExtTapDescriptor parent) {
    	//Creates a shallow copy of the parent which is fine since String and Boolean is immutable
    	this.parent = parent;
    	
    	tapSTCSColumn = parent.getTapSTCSColumn();
    	tapRaColumn = parent.getTapRaColumn();
    	tapDecColumn = parent.getTapDecColumn();
    	uniqueIdentifierField = parent.getUniqueIdentifierField();
    	dataProductTypes = parent.getDataProductTypes();
    	whereADQL = parent.getWhereADQL();
    	selectADQL = parent.getSelectADQL();
        orderByADQL = parent.getOrderByADQL();
    	responseFormat = parent.getResponseFormat();
    	searchFunction = parent.getSearchFunction();
    	isInBackend = false;
    	tapUrl = parent.getTapUrl();
    	ingestedTable = parent.getIngestedTable();
    	sourceLimit = parent.getSourceLimit();
    	
    	//From BaseDescriptor
    	setGuiShortName(parent.getGuiShortName());
    	setGuiLongName(parent.getGuiLongName());
    	setMission(parent.getMission());
    	setCreditedInstitutions(parent.getCreditedInstitutions());
    	setHistoColor(parent.getHistoColor());
    	setTapTable(parent.getTapTable());
    	setArchiveURL(parent.getAdsAuthorUrl());
    	setArchiveProductURI(parent.getArchiveProductURI());
    	setFovLimit(parent.getFovLimit());
    }

	@Override
	public String generateId() {
		return getMission() + " ExtTap " + generateNextTabCount();
	}

    public String getUniqueIdentifierField(){
    	return uniqueIdentifierField;
    }
    
    public void setUniqueIdentifierField(String field){
    	uniqueIdentifierField = field;
    }

	public String getTapUrl() {
		return tapUrl;
	}

	public void setTapUrl(String tapUrl) {
		this.tapUrl = tapUrl;
	}

	public String getTapSTCSColumn() {
		return tapSTCSColumn;
	}
	
	public void setTapSTCSColumn(String tapSTCSColumn) {
		this.tapSTCSColumn = tapSTCSColumn;
	}

	public String getTapRaColumn() {
		return tapRaColumn;
	}

	public void setTapRaColumn(String tapRaColumn) {
		this.tapRaColumn = tapRaColumn;
	}

	public String getTapDecColumn() {
		return tapDecColumn;
	}

	public void setTapDecColumn(String tapDecColumn) {
		this.tapDecColumn = tapDecColumn;
	}

	public ArrayList<String> getDataProductTypes() {
		return dataProductTypes;
	}

	public void setDataProductTypes(ArrayList<String> dataProductTypes) {
		this.dataProductTypes = dataProductTypes;
	}

	public String getWhereADQL() {
		return whereADQL;
	}

	public void setWhereADQL(String whereADQL) {
		this.whereADQL = whereADQL;
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

	public Map<String, ArrayList<String>> getCollections() {
		return collections;
	}

	public void setCollections(Map<String, ArrayList<String>> collections) {
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

	public String getOrderByADQL() {
		return orderByADQL;
	}

	public void setOrderByADQL(String orderByADQL) {
		this.orderByADQL = orderByADQL;
	}

	public int getSourceLimit() {
		return sourceLimit;
	}

	public void setSourceLimit(int sourceLimit) {
		this.sourceLimit = sourceLimit;
	}
	
	
	
}
