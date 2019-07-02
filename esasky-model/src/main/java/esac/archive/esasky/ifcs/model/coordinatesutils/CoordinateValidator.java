package esac.archive.esasky.ifcs.model.coordinatesutils;

import java.util.Map.Entry;

public class CoordinateValidator {

    /** Logger. */
    // private static final Logger LOGGER = Logger.getLogger(CoordinateValidator.class);

    public enum SearchInputType {
        TARGET("byTarget"), NOT_VALID("inputNotValid"), SPACE_RAhhmmssDECddmmss("byCoords1"), COLUMN_RAhhmmssDECddmmss(
                "byCoords2"), SPACE_RAhhmmssDECdeg("byCoords3"), COLUMN_RAhhmmssDECdeg("byCoords4"), SPACE_RAdegDECddmmss(
                "byCoords5"), COLUMN_RAdegDECddmmss("byCoords6"), RAdegDECdeg("byCoords7"), COLUMN_RAdddmmssDECddmmss(
                                "byCoords8"), SPACE_RAdddmmssDECddmmss("byCoords9"), COLUMN_RAdddmmssDECdeg(
                "byCoords10"), SPACE_RAdddmmssDECdeg("byCoords11"),LETTERS_RAhhmmssDECddmmss("byCoords12"),
        		LETTERS_RAdddmmssDECddmmss("byCoords13"), BIBCODE("byBibcode"), AUTHOR("byAuthor");

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

       
        boolean matchFound = false;
        // LOGGER.debug("@@@ COORD Validator @@@");

        if (CoordinatesFrame.GALACTIC == cooFrame) {
            for (Entry<String, SearchInputType> currCoordPattern : ESASkySearchRegEx.explainGalactic
                    .entrySet()) {
                if (regex.test(currCoordPattern.getKey(), input)) {
                    return currCoordPattern.getValue();
                }
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
        if (!matchFound) {
            return SearchInputType.NOT_VALID;
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
