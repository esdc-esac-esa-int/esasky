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

package esac.archive.esasky.ifcs.model.coordinatesutils;

import java.util.Map.Entry;

public class CoordinateValidator {

    /** Logger. */
    // private static final Logger LOGGER = Logger.getLogger(CoordinateValidator.class);

    public enum SearchInputType {
        TARGET("byTarget"), NOT_VALID("inputNotValid"), SPACE_RA_HHMMSS_DEC_DDMMSS("byCoords1"), 
        	COLUMN_RA_HHMMSS_DEC_DDMMSS("byCoords2"), SPACE_RA_HHMMSS_DEC_DEG("byCoords3"), COLUMN_RA_HHMMSS_DEC_DEG("byCoords4"),
    		SPACE_RA_DEG_DEC_DDMMSS("byCoords5"), COLUMN_RA_DEG_DEC_DDMMSS("byCoords6"), RA_DEG_DEC_DEG("byCoords7"),
        	COLUMN_RA_DDDMMSS_DEC_DDMMSS("byCoords8"), SPACE_RA_DDDMMSS_DEC_DDMMSS("byCoords9"),
        	COLUMN_RA_DDDMMSS_DEC_DEG("byCoords10"), SPACE_RA_DDMMSS_DEC_DEG("byCoords11"),
        	LETTERS_RA_HHMMSS_DEC_DDMMSS("byCoords12"), LETTERS_RA_DDDMMSS_DEC_DDMMSS("byCoords13"),
        	NO_SPACE_RA_HHMMSS_DEC_DDMMSS("byCoords14"), BIBCODE("byBibcode"), AUTHOR("byAuthor"), SEARCH_SHAPE("searchShape");

        String type;

        SearchInputType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }
    }

    public static SearchInputType checkInputType(RegexClass regex, String input, CoordinatesFrame cooFrame) {

        input = input.trim();

        for (Entry<String, SearchInputType> currPattern : ESASkySearchRegEx.explainSearchArea.entrySet()) {
            if (regex.test(currPattern.getKey(), input)) {
                return currPattern.getValue();
            }
        }

        if (CoordinatesFrame.J2000 == cooFrame) {
            for (Entry<String, SearchInputType> currCoordPattern : ESASkySearchRegEx.explainEquatorial
                    .entrySet()) {
                if (regex.test(currCoordPattern.getKey(), input)) {
                    return currCoordPattern.getValue();
                }
            }
        }

        for (Entry<String, SearchInputType> currCoordPattern : ESASkySearchRegEx.explainGeneral
                .entrySet()) {
            if (regex.test(currCoordPattern.getKey(), input)) {
                return currCoordPattern.getValue();
            }
        }


        if (regex.test(ESASkySearchRegEx.TARGET, input)) {
            return SearchInputType.TARGET;
        }

        return SearchInputType.NOT_VALID;
    }
    
    

    /**
     * Checks that the string passed as parameter conform to any of the accepted RA formats.
     * @param ra Input ra.
     * @return true if format is valid, false otherwise
     */
    public static boolean isRaFormatValid(RegexClass regex, final String ra) {
        if (ra == null || ra.trim().isEmpty()) {
            return false;
        }
        for (String raPattern : ESASkySearchRegEx.RAValid) {
            if (regex.test(raPattern, ra)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks that the string passed as parameter conform to any of the accepted DEC formats.
     * @param dec Input dec.
     * @return true if format is valid, false otherwise
     */
    public static boolean isDecFormatValid(RegexClass regex, final String dec) {
        if (dec == null || dec.trim().isEmpty()) {
            return false;
        }
        for (String decPattern : ESASkySearchRegEx.DECValid) {
            if (regex.test(decPattern, dec)) {
                return true;
            }
        }

        return false;
    }
    
    public interface RegexClass {
    	public boolean test(String pattern, String stringToTest);
    }
}
