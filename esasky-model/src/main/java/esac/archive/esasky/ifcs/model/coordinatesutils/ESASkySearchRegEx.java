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

import java.util.HashMap;
import java.util.Map;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;

public class ESASkySearchRegEx {

    final static String CONJ_2 = "(\\s*:\\s*|\\s)";
    final static String CONJ_3 = "\\s*";
    final static String COLUMN = "\\s*:\\s*";
    final static String RADEC_SEPARATOR = "\\s*(\\+|-)";
    final static String SIGN = "(\\+|-)?";
    final static String SPACE = "\\s+";
    final static String END = "$";
    final static String BEGIN = "^";

    final static String RA_HH_COLUMN = "(\\d(\\.\\d+)?" + "|[01]\\d(\\.\\d+)?"
            + "|2[0-3](\\.\\d+)?)";
    final static String RA_HH_SPACE = "(\\d(\\.\\d+)?" + "|[01]\\d(\\.\\d+)?"
            + "|2[0-3](\\.\\d+)?)";
    final static String RA_HH_NO_SPACE = "(\\d" + "|[01]\\d"+ "|2[0-3])";

    final static String BASE60 = "([0-5]\\d?(\\.\\d+)?|[0-9](\\.\\d+)?)";
    final static String BASE60_ALL = "([0-5]\\d|[0-9])";
    final static String BASE60_SPACE = "(" + SPACE + "([0-5]\\d?(\\.\\d+)?|[0-9](\\.\\d+)?))";
    final static String BASE60_COLUMN = "(" + COLUMN + "([0-5]\\d?(\\.\\d+)?|[0-9](\\.\\d+)?))";
    final static String BASE60_NO_SPACE_END = "([0-5]\\d(\\d+)|[0-9](\\d+)?)";

    final static String RA_HHMMSS_SPACE_PATTERN = RA_HH_SPACE + "(" + BASE60_SPACE + BASE60_SPACE
            + "?)+";
    final static String RA_HHMMSS_NO_SPACE_PATTERN = RA_HH_NO_SPACE + "(" + BASE60_ALL + BASE60_NO_SPACE_END + ")";
    final static String RA_HHMMSS_COLUMN_PATTERN = RA_HH_COLUMN + "(" + BASE60_COLUMN
            + BASE60_COLUMN + "?)+";
    final static String RA_HHMMSS_LETTERS_PATTERN = RA_HH_COLUMN + "h?(\\s?(" + BASE60
    		+ "(m|\\'))?\\s?(" + BASE60 + "(s|\\'\\'|\\\"))?\\s?)";

    /**
     * RA and DEC in decimal DEGREES 214.123 +89.123 214.123 -89.123
     */
    final static String RA_DEGREES_DECIMAL_BASE360 = "(\\d?\\d(\\.\\d+)?"
            + "|[0-2]\\d\\d(\\.\\d+)?" + "|3[0-5]\\d(\\.\\d+)?)";
    final static String RA_DEGREES_INTEGER_BASE360 = "(\\d" + "|0\\d?" + "|\\d\\d"
            + "|[0-2][0-9]\\d" + "|3[0-5]\\d" + "|360)";

    final static String RA_DDDMMSS_SPACE_PATTERN = RA_DEGREES_INTEGER_BASE360 + "(" + BASE60_SPACE
            + BASE60_SPACE + "?)?";
    final static String RA_DDDMMSS_COLUMN_PATTERN = RA_DEGREES_INTEGER_BASE360 + "("
            + BASE60_COLUMN + BASE60_COLUMN + "?)?";
    final static String RA_DDDMMSS_LETTERS_PATTERNS = RA_DEGREES_INTEGER_BASE360 + "d(\\s?(" + BASE60
    		+ "(m|\\'))?\\s?(" + BASE60 + "(s|\\'\\'|\"))?\\s?)?";
    
    final static String DEC_DEGREES_DECIMAL_BASE90 = "(\\d(\\.\\d+)?" + "|0\\d?(\\.\\d+)?"
            + "|[0-8]\\d(\\.\\d+)?" + "|90)";

    final static String DEC_DEGREES_INTEGER_BASE90 = "(\\d" + "|0\\d?" + "|[0-8]\\d" + "|90)";

    final static String DEC_DDMMSS_SPACE_PATTERN = DEC_DEGREES_INTEGER_BASE90 + "(" + BASE60_SPACE
            + BASE60_SPACE + "?)";
    final static String DEC_DDMMSS_NO_SPACE_PATTERN = DEC_DEGREES_INTEGER_BASE90 + "(" + BASE60_ALL	+ BASE60_NO_SPACE_END + ")";
    final static String DEC_DDMMSS_COLUMN_PATTERN = DEC_DEGREES_INTEGER_BASE90 + "("
            + BASE60_COLUMN + BASE60_COLUMN + "?)";
    final static String DEC_DDMMSS_LETTERS_PATTERN = DEC_DEGREES_INTEGER_BASE90 + "d(\\s?(" + BASE60
    		+ "(m|\\'))?\\s?(" + BASE60 + "(s|\\'\\'|\"))?\\s?)";

    /**
     * PATTERNS!!!
     */

    final static String PATTERN_RA_DDDMMSS_DEC_DDMMSS_COLUMN = BEGIN + RA_DDDMMSS_COLUMN_PATTERN
            + RADEC_SEPARATOR + DEC_DDMMSS_COLUMN_PATTERN + END;
    
    final static String PATTERN_RA_DDDMMSS_DEC_DDMMSS_SPACE = BEGIN + RA_DDDMMSS_SPACE_PATTERN
            + RADEC_SEPARATOR + DEC_DDMMSS_SPACE_PATTERN + END;
    
    final static String PATTERN_RA_DDDMMSS_DEC_DDMMSS_LETTERS = BEGIN + RA_DDDMMSS_LETTERS_PATTERNS
    		+ RADEC_SEPARATOR + DEC_DDMMSS_LETTERS_PATTERN + END;

    final static String PATTERN_RA_DDDMMSS_DEC_DEG_COLUMN = BEGIN + RA_DDDMMSS_COLUMN_PATTERN
            + RADEC_SEPARATOR + DEC_DEGREES_DECIMAL_BASE90 + END;

    final static String PATTERN_RA_DDDMMSS_DEC_DEG_SPACE = BEGIN + RA_DDDMMSS_SPACE_PATTERN
            + RADEC_SEPARATOR + DEC_DEGREES_DECIMAL_BASE90 + END;

    final static String PATTERN_RA_HHMMSS_DEC_DDMMSS_SPACE = BEGIN + RA_HHMMSS_SPACE_PATTERN
            + RADEC_SEPARATOR + DEC_DDMMSS_SPACE_PATTERN + END;
    
    final static String PATTERN_RA_HHMMSS_DEC_DDMMSS_NO_SPACE = BEGIN + RA_HHMMSS_NO_SPACE_PATTERN
    		+ RADEC_SEPARATOR + DEC_DDMMSS_NO_SPACE_PATTERN + END;

    final static String PATTERN_RA_HHMMSS_DEC_DDMMSS_COLUMN = BEGIN + RA_HHMMSS_COLUMN_PATTERN
            + RADEC_SEPARATOR + DEC_DDMMSS_COLUMN_PATTERN + END;

    final static String PATTERN_RA_HHMMSS_DEC_DDMMSS_LETTERS = BEGIN + RA_HHMMSS_LETTERS_PATTERN
    		+ RADEC_SEPARATOR + DEC_DDMMSS_LETTERS_PATTERN + END;

    final static String PATTERN_RA_DEG_DEC_DEG = BEGIN + RA_DEGREES_DECIMAL_BASE360 + RADEC_SEPARATOR
            + DEC_DEGREES_DECIMAL_BASE90 + END;

    final static String PATTERN_RA_HHMMSS_DEC_DEG_SPACE = BEGIN + RA_HHMMSS_SPACE_PATTERN
            + RADEC_SEPARATOR + DEC_DEGREES_DECIMAL_BASE90 + END;

    final static String PATTERN_RA_HHMMSS_DEC_DEG_COLUMN = BEGIN + RA_HHMMSS_COLUMN_PATTERN
            + RADEC_SEPARATOR + DEC_DEGREES_DECIMAL_BASE90 + END;

    final static String PATTERN_RA_DEG_DEC_DDMMSS_SPACE = BEGIN + RA_DEGREES_DECIMAL_BASE360
            + RADEC_SEPARATOR + DEC_DDMMSS_SPACE_PATTERN + END;

    final static String PATTERN_RA_DEG_DEC_DDMMSS_COLUMN = BEGIN + RA_DEGREES_DECIMAL_BASE360
            + RADEC_SEPARATOR + DEC_DDMMSS_COLUMN_PATTERN + END;

    final static String TARGET = "^[0-9]*$|^\\[.+\\].*$|^[0-9].*[\\p{L}]+.*$|^[\\p{L}]+.*$|^[\\p{L}]+\\s[\\p{L}]+$|^[\\p{L}]+\\s[\\p{L}_0-9]+\\*$|^[\\p{L}]+\\s?\\d+$|^[\\p{L}]+\\s?\\+?\\s?[\\p{L}_0-9]+$|^[\\p{L}]+\\s?[\\p{L}]*-?[\\p{L}_0-9]+$";


    protected static final Map<String, SearchInputType> explainEquatorial = new HashMap<String, SearchInputType>() {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {
            // RA_sex_hours_space_pattern + RADEC_SEPARATOR + DEC_sex_degrees_space_pattern + END
            put(PATTERN_RA_HHMMSS_DEC_DDMMSS_SPACE, SearchInputType.SPACE_RA_HHMMSS_DEC_DDMMSS);
            
            // RA_sex_hours_no_space_pattern + RADEC_SEPARATOR + DEC_sex_degrees_no_space_pattern + END
            put(PATTERN_RA_HHMMSS_DEC_DDMMSS_NO_SPACE, SearchInputType.NO_SPACE_RA_HHMMSS_DEC_DDMMSS);

            // RA_sex_hours_column_pattern + RADEC_SEPARATOR + DEC_sex_degrees_column_pattern + END
            put(PATTERN_RA_HHMMSS_DEC_DDMMSS_COLUMN, SearchInputType.COLUMN_RA_HHMMSS_DEC_DDMMSS);

            // RA_sex_hours_space_pattern + RADEC_SEPARATOR + DEGREES_decimal
            put(PATTERN_RA_HHMMSS_DEC_DEG_SPACE, SearchInputType.SPACE_RA_HHMMSS_DEC_DEG);

            // RA_sex_hours_letters_pattern + RADEC_SEPARATOR + DEGREES_decimal
            put(PATTERN_RA_HHMMSS_DEC_DDMMSS_LETTERS, SearchInputType.LETTERS_RA_HHMMSS_DEC_DDMMSS);

            // RA_sex_hours_space_pattern + RADEC_SEPARATOR + DEGREES_decimal
            put(PATTERN_RA_HHMMSS_DEC_DEG_COLUMN, SearchInputType.COLUMN_RA_HHMMSS_DEC_DEG);

        }
    };

    protected static final Map<String, SearchInputType> explainGeneral = new HashMap<String, SearchInputType>() {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {

            // DEGREES_decimal + RADEC_SEPARATOR + DEGREES_decimal
            put(PATTERN_RA_DEG_DEC_DEG, SearchInputType.RA_DEG_DEC_DEG);
            put(PATTERN_RA_DDDMMSS_DEC_DDMMSS_SPACE, SearchInputType.SPACE_RA_DDDMMSS_DEC_DDMMSS);
            put(PATTERN_RA_DDDMMSS_DEC_DDMMSS_LETTERS, SearchInputType.LETTERS_RA_DDDMMSS_DEC_DDMMSS);
            put(PATTERN_RA_DDDMMSS_DEC_DDMMSS_COLUMN, SearchInputType.COLUMN_RA_DDDMMSS_DEC_DDMMSS);

            put(TARGET, SearchInputType.TARGET);

        }
    };

    protected static final Map<String, SearchInputType> explainSearchArea = new HashMap<String, SearchInputType>() {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {
            put("^(BOX|POLYGON|CIRCLE){1}.*", SearchInputType.SEARCH_SHAPE);

        }
    };

    private static String RA_1 = BEGIN + SIGN + RA_DEGREES_DECIMAL_BASE360 + END;
    private static String RA_2 = BEGIN + SIGN + RA_DEGREES_INTEGER_BASE360 + END;
    private static String RA_3 = BEGIN + SIGN + RA_HHMMSS_SPACE_PATTERN + END;
    private static String RA_4 = BEGIN + SIGN + RA_HHMMSS_COLUMN_PATTERN + END;
    private static String RA_7 = BEGIN + SIGN + RA_HHMMSS_LETTERS_PATTERN + END;

    protected static final String[] RAValid = { RA_1, RA_2, RA_3, RA_4,RA_7};

    public static final String DEC_1 = BEGIN + SIGN + DEC_DEGREES_DECIMAL_BASE90 + END;
    public static final String DEC_2 = BEGIN + SIGN + DEC_DEGREES_INTEGER_BASE90 + END;
    public static final String DEC_3 = BEGIN + SIGN + DEC_DDMMSS_SPACE_PATTERN + END;
    public static final String DEC_4 = BEGIN + SIGN + DEC_DDMMSS_COLUMN_PATTERN + END;
    public static final String DEC_5 = BEGIN + SIGN + DEC_DDMMSS_LETTERS_PATTERN + END;
    protected static final String[] DECValid = { DEC_1, DEC_2, DEC_3, DEC_4, DEC_5 };

}
