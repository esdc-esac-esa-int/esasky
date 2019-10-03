package esac.archive.esasky.ifcs.model.shared;

import java.util.LinkedList;
import java.util.List;

public class ESASkySSOSearchResultList {

    List<ESASkySSOSearchResult> results = new LinkedList<ESASkySSOSearchResult>();
    String userInput;
    String errorMessage;

    public ESASkySSOSearchResultList() {
        super();
    }

    public List<ESASkySSOSearchResult> getResults() {
        return results;
    }

    public void setResults(List<ESASkySSOSearchResult> results) {
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
