package esac.archive.esasky.ifcs.model.descriptor;

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
    private String type;
    private String whereADQL;
    private String responseFormat;
    private String searchFunction;
    
    
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String tapUrl;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	
}
