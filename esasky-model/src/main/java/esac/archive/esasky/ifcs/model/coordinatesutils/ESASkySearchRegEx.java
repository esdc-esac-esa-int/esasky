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

    final static String RA_hh_column = "(\\d(\\.\\d+)?" + "|[01]\\d(\\.\\d+)?"
            + "|2[0-3](\\.\\d+)?)";
    final static String RA_hh_space = "(\\d(\\.\\d+)?" + "|[01]\\d(\\.\\d+)?"
            + "|2[0-3](\\.\\d+)?)";
    final static String RA_hh_no_space = "(\\d" + "|[01]\\d"+ "|2[0-3])";

    final static String base60 = "([0-5]\\d?(\\.\\d+)?|[0-9](\\.\\d+)?)";
    final static String base60_all = "([0-5]\\d|[0-9])";
    final static String base60_space = "(" + SPACE + "([0-5]\\d?(\\.\\d+)?|[0-9](\\.\\d+)?))";
    final static String base60_column = "(" + COLUMN + "([0-5]\\d?(\\.\\d+)?|[0-9](\\.\\d+)?))";
    final static String base60_nospace_end = "([0-5]\\d(\\d+)|[0-9](\\d+)?)";

    final static String RA_hhmmss_space_pattern = RA_hh_space + "(" + base60_space + base60_space
            + "?)+";
    final static String RA_hhmmss_no_space_pattern = RA_hh_no_space + "(" + base60_all + base60_nospace_end + ")";
    final static String RA_hhmmss_column_pattern = RA_hh_column + "(" + base60_column
            + base60_column + "?)+";
    final static String RA_hhmmss_letters_pattern = RA_hh_column + "h?(\\s?(" + base60
    		+ "(m|\\'))?\\s?(" + base60 + "(s|\\'\\'|\\\"))?\\s?)";

    /**
     * RA and DEC in decimal DEGREES 214.123 +89.123 214.123 -89.123
     */
    final static String RA_DEGREES_decimal_base360 = "(\\d?\\d(\\.\\d+)?"
            + "|[0-2]\\d\\d(\\.\\d+)?" + "|3[0-5]\\d(\\.\\d+)?)";
    final static String RA_DEGREES_integer_base360 = "(\\d" + "|0\\d?" + "|\\d\\d"
            + "|[0-2][0-9]\\d" + "|3[0-5]\\d" + "|360)";

    final static String RA_dddmmss_space_pattern = RA_DEGREES_integer_base360 + "(" + base60_space
            + base60_space + "?)?";
    final static String RA_dddmmss_column_pattern = RA_DEGREES_integer_base360 + "("
            + base60_column + base60_column + "?)?";
    final static String RA_dddmmss_letters_pattern = RA_DEGREES_integer_base360 + "d(\\s?(" + base60
    		+ "(m|\\'))?\\s?(" + base60 + "(s|\\'\\'|\"))?\\s?)?";
    
    final static String DEC_DEGREES_decimal_base90 = "(\\d(\\.\\d+)?" + "|0\\d?(\\.\\d+)?"
            + "|[0-8]\\d(\\.\\d+)?" + "|90)";

    final static String DEC_DEGREES_integer_base90 = "(\\d" + "|0\\d?" + "|[0-8]\\d" + "|90)";

    final static String DEC_ddmmss_space_pattern = DEC_DEGREES_integer_base90 + "(" + base60_space
            + base60_space + "?)";
    final static String DEC_ddmmss_no_space_pattern = DEC_DEGREES_integer_base90 + "(" + base60_all	+ base60_nospace_end + ")";
    final static String DEC_ddmmss_column_pattern = DEC_DEGREES_integer_base90 + "("
            + base60_column + base60_column + "?)";
    final static String DEC_ddmmss_letters_pattern = DEC_DEGREES_integer_base90 + "d(\\s?(" + base60
    		+ "(m|\\'))?\\s?(" + base60 + "(s|\\'\\'|\"))?\\s?)";

    /**
     * PATTERNS!!!
     */

    final static String PATTERN_RAdddmmss_DECddmmss_column = BEGIN + RA_dddmmss_column_pattern
            + RADEC_SEPARATOR + DEC_ddmmss_column_pattern + END;
    
    final static String PATTERN_RAdddmmss_DECddmmss_space = BEGIN + RA_dddmmss_space_pattern
            + RADEC_SEPARATOR + DEC_ddmmss_space_pattern + END;
    
    final static String PATTERN_RAdddmmss_DECddmmss_letters = BEGIN + RA_dddmmss_letters_pattern
    		+ RADEC_SEPARATOR + DEC_ddmmss_letters_pattern + END;

    final static String PATTERN_RAdddmmss_DECdeg_column = BEGIN + RA_dddmmss_column_pattern
            + RADEC_SEPARATOR + DEC_DEGREES_decimal_base90 + END;

    final static String PATTERN_RAdddmmss_DECdeg_space = BEGIN + RA_dddmmss_space_pattern
            + RADEC_SEPARATOR + DEC_DEGREES_decimal_base90 + END;

    final static String PATTERN_RAhhmmss_DECddmmss_space = BEGIN + RA_hhmmss_space_pattern
            + RADEC_SEPARATOR + DEC_ddmmss_space_pattern + END;
    
    final static String PATTERN_RAhhmmss_DECddmmss_no_space = BEGIN + RA_hhmmss_no_space_pattern
    		+ RADEC_SEPARATOR + DEC_ddmmss_no_space_pattern + END;

    final static String PATTERN_RAhhmmss_DECddmmss_column = BEGIN + RA_hhmmss_column_pattern
            + RADEC_SEPARATOR + DEC_ddmmss_column_pattern + END;

    final static String PATTERN_RAhhmmss_DECddmmss_letters = BEGIN + RA_hhmmss_letters_pattern
    		+ RADEC_SEPARATOR + DEC_ddmmss_letters_pattern + END;

    final static String PATTERN_RAdeg_DECdeg = BEGIN + RA_DEGREES_decimal_base360 + RADEC_SEPARATOR
            + DEC_DEGREES_decimal_base90 + END;

    final static String PATTERN_RAhhmmss_DECdeg_space = BEGIN + RA_hhmmss_space_pattern
            + RADEC_SEPARATOR + DEC_DEGREES_decimal_base90 + END;

    final static String PATTERN_RAhhmmss_DECdeg_column = BEGIN + RA_hhmmss_column_pattern
            + RADEC_SEPARATOR + DEC_DEGREES_decimal_base90 + END;

    final static String PATTERN_RAdeg_DECddmmss_space = BEGIN + RA_DEGREES_decimal_base360
            + RADEC_SEPARATOR + DEC_ddmmss_space_pattern + END;

    final static String PATTERN_RAdeg_DECddmmss_column = BEGIN + RA_DEGREES_decimal_base360
            + RADEC_SEPARATOR + DEC_ddmmss_column_pattern + END;

    final static String TARGET = "^[0-9]*$|^\\[.+\\].*$|^[0-9].*[\\p{L}]+.*$|^[\\p{L}]+.*$|^[\\p{L}]+\\s[\\p{L}]+$|^[\\p{L}]+\\s[\\p{L}_0-9]+\\*$|^[\\p{L}]+\\s?\\d+$|^[\\p{L}]+\\s?\\+?\\s?[\\p{L}_0-9]+$|^[\\p{L}]+\\s?[\\p{L}]*-?[\\p{L}_0-9]+$";


    protected static final Map<String, SearchInputType> explainEquatorial = new HashMap<String, SearchInputType>() {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {
            // RA_sex_hours_space_pattern + RADEC_SEPARATOR + DEC_sex_degrees_space_pattern + END
            put(PATTERN_RAhhmmss_DECddmmss_space, SearchInputType.SPACE_RAhhmmssDECddmmss);
            
            // RA_sex_hours_no_space_pattern + RADEC_SEPARATOR + DEC_sex_degrees_no_space_pattern + END
            put(PATTERN_RAhhmmss_DECddmmss_no_space, SearchInputType.NO_SPACE_RAhhmmssDECddmmss);

            // RA_sex_hours_column_pattern + RADEC_SEPARATOR + DEC_sex_degrees_column_pattern + END
            put(PATTERN_RAhhmmss_DECddmmss_column, SearchInputType.COLUMN_RAhhmmssDECddmmss);

            // RA_sex_hours_space_pattern + RADEC_SEPARATOR + DEGREES_decimal
            put(PATTERN_RAhhmmss_DECdeg_space, SearchInputType.SPACE_RAhhmmssDECdeg);

            // RA_sex_hours_letters_pattern + RADEC_SEPARATOR + DEGREES_decimal
            put(PATTERN_RAhhmmss_DECddmmss_letters, SearchInputType.LETTERS_RAhhmmssDECddmmss);

            // RA_sex_hours_space_pattern + RADEC_SEPARATOR + DEGREES_decimal
            put(PATTERN_RAhhmmss_DECdeg_column, SearchInputType.COLUMN_RAhhmmssDECdeg);

        }
    };

    protected static final Map<String, SearchInputType> explainGeneral = new HashMap<String, SearchInputType>() {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {

            // DEGREES_decimal + RADEC_SEPARATOR + DEGREES_decimal
            put(PATTERN_RAdeg_DECdeg, SearchInputType.RAdegDECdeg);
            put(PATTERN_RAdddmmss_DECddmmss_space, SearchInputType.SPACE_RAdddmmssDECddmmss);
            put(PATTERN_RAdddmmss_DECddmmss_letters, SearchInputType.LETTERS_RAdddmmssDECddmmss);
            put(PATTERN_RAdddmmss_DECddmmss_column, SearchInputType.COLUMN_RAdddmmssDECddmmss);

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

    private static String RA_1 = BEGIN + SIGN + RA_DEGREES_decimal_base360 + END;
    private static String RA_2 = BEGIN + SIGN + RA_DEGREES_integer_base360 + END;
    private static String RA_3 = BEGIN + SIGN + RA_hhmmss_space_pattern + END;
    private static String RA_4 = BEGIN + SIGN + RA_hhmmss_column_pattern + END;
    private static String RA_7 = BEGIN + SIGN + RA_hhmmss_letters_pattern + END;

    protected static final String[] RAValid = { RA_1, RA_2, RA_3, RA_4,RA_7};

    public static final String DEC_1 = BEGIN + SIGN + DEC_DEGREES_decimal_base90 + END;
    public static final String DEC_2 = BEGIN + SIGN + DEC_DEGREES_integer_base90 + END;
    public static final String DEC_3 = BEGIN + SIGN + DEC_ddmmss_space_pattern + END;
    public static final String DEC_4 = BEGIN + SIGN + DEC_ddmmss_column_pattern + END;
    public static final String DEC_5 = BEGIN + SIGN + DEC_ddmmss_letters_pattern + END;
    protected static final String[] DECValid = { DEC_1, DEC_2, DEC_3, DEC_4, DEC_5 };

}
