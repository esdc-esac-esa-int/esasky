package esac.archive.esasky.ifcs.model.shared;

import java.util.LinkedList;
import java.util.List;

public class ESASkyPublicationSearchResultList {

    List<ESASkyPublicationSearchResult> results = new LinkedList<ESASkyPublicationSearchResult>();
    String userInput;
    String errorMessage = "";

    public ESASkyPublicationSearchResultList() {
    	super();
    }
    
    public ESASkyPublicationSearchResultList(String userInput) {
        this();
        this.userInput = userInput;
    }

    public List<ESASkyPublicationSearchResult> getResults() {
        return results;
    }

    public void setResults(List<ESASkyPublicationSearchResult> results) {
        this.results = results;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
