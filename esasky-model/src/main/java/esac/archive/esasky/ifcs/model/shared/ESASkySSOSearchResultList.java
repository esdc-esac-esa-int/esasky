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
