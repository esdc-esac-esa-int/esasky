package esac.archive.esasky.ifcs.model.coordinatesutils;

import java.util.HashMap;
import java.util.Map;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;

public class ESASkySearchRegEx {

    final static String CONJ_2 = "(\\s?:\\s?|\\s)";
    final static String CONJ_3 = "\\s?";
    final static String COLUMN = "\\s?:\\s?";
    final static String RADEC_SEPARATOR = "\\s?(\\+|-)";
    final static String SIGN = "(\\+|-)?";
    final static String SPACE = "\\s";
    final static String END = "$";
    final static String BEGIN = "^";

    final static String RA_hh_column = "(\\d(\\.\\d+)?" + "|[01]\\d(\\.\\d+)?"
            + "|2[0-3](\\.\\d+)?)";
    final static String RA_hh_space = "(\\d(\\.\\d+)?" + "|[01]\\d(\\.\\d+)?"
            + "|2[0-3](\\.\\d+)?)";

    final static String base60_space = "(" + SPACE + "([0-5]\\d?(\\.\\d+)?|[0-9](\\.\\d+)?))";
    final static String base60_column = "(" + COLUMN + "([0-5]\\d?(\\.\\d+)?|[0-9](\\.\\d+)?))";

    final static String RA_hhmmss_space_pattern = RA_hh_space + "(" + base60_space + base60_space
            + "?)";
    final static String RA_hhmmss_column_pattern = RA_hh_column + "(" + base60_column
            + base60_column + "?)";

    /**
     * RA and DEC in decimal DEGREES 214.123 +89.123 214.123 -89.123
     */
    final static String RA_DEGREES_decimal_base360 = "(\\d?\\d(\\.\\d+)?"
            + "|[0-2]\\d\\d(\\.\\d+)?" + "|3[0-5]\\d(\\.\\d+)?)";
    final static String RA_DEGREES_integer_base360 = "(\\d" + "|0\\d?" + "|\\d\\d"
            + "|[0-2][0-9]\\d" + "|3[0-5]\\d" + "|360)";

    final static String RA_dddmmss_space_pattern = RA_DEGREES_integer_base360 + "(" + base60_space
            + base60_space + "?)";
    final static String RA_dddmmss_column_pattern = RA_DEGREES_integer_base360 + "("
            + base60_column + base60_column + "?)";

    final static String DEC_DEGREES_decimal_base90 = "(\\d(\\.\\d+)?" + "|0\\d?(\\.\\d+)?"
            + "|[0-8]\\d(\\.\\d+)?" + "|90)";

    final static String DEC_DEGREES_integer_base90 = "(\\d" + "|0\\d?" + "|[0-8]\\d" + "|90)";

    final static String DEC_ddmmss_space_pattern = DEC_DEGREES_integer_base90 + "(" + base60_space
            + base60_space + "?)";
    final static String DEC_ddmmss_column_pattern = DEC_DEGREES_integer_base90 + "("
            + base60_column + base60_column + "?)";

    /**
     * PATTERNS!!!
     */

    final static String PATTERN_RAdddmmss_DECddmmss_column = BEGIN + RA_dddmmss_column_pattern
            + RADEC_SEPARATOR + DEC_ddmmss_column_pattern + END;

    final static String PATTERN_RAdddmmss_DECddmmss_space = BEGIN + RA_dddmmss_space_pattern
            + RADEC_SEPARATOR + DEC_ddmmss_space_pattern + END;

    final static String PATTERN_RAdddmmss_DECdeg_column = BEGIN + RA_dddmmss_column_pattern
            + RADEC_SEPARATOR + DEC_DEGREES_decimal_base90 + END;

    final static String PATTERN_RAdddmmss_DECdeg_space = BEGIN + RA_dddmmss_space_pattern
            + RADEC_SEPARATOR + DEC_DEGREES_decimal_base90 + END;

    final static String PATTERN_RAhhmmss_DECddmmss_space = BEGIN + RA_hhmmss_space_pattern
            + RADEC_SEPARATOR + DEC_ddmmss_space_pattern + END;

    final static String PATTERN_RAhhmmss_DECddmmss_column = BEGIN + RA_hhmmss_column_pattern
            + RADEC_SEPARATOR + DEC_ddmmss_column_pattern + END;

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

    public static Map<String, SearchInputType> explainGalactic = new HashMap<String, SearchInputType>() {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {

            //
            put(PATTERN_RAdddmmss_DECddmmss_column, SearchInputType.COLUMN_RAdddmmssDECddmmss);

            //
            put(PATTERN_RAdddmmss_DECddmmss_space, SearchInputType.SPACE_RAdddmmssDECddmmss);

            //
            put(PATTERN_RAdddmmss_DECdeg_column, SearchInputType.COLUMN_RAdddmmssDECdeg);

            //
            put(PATTERN_RAdddmmss_DECdeg_space, SearchInputType.SPACE_RAdddmmssDECdeg);

        }
    };

    public static Map<String, SearchInputType> explainEquatorial = new HashMap<String, SearchInputType>() {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {
            // RA_sex_hours_space_pattern + RADEC_SEPARATOR + DEC_sex_degrees_space_pattern + END
            put(PATTERN_RAhhmmss_DECddmmss_space, SearchInputType.SPACE_RAhhmmssDECddmmss);

            // RA_sex_hours_column_pattern + RADEC_SEPARATOR + DEC_sex_degrees_column_pattern + END
            put(PATTERN_RAhhmmss_DECddmmss_column, SearchInputType.COLUMN_RAhhmmssDECddmmss);

            // RA_sex_hours_space_pattern + RADEC_SEPARATOR + DEGREES_decimal
            put(PATTERN_RAhhmmss_DECdeg_space, SearchInputType.SPACE_RAhhmmssDECdeg);

            // RA_sex_hours_space_pattern + RADEC_SEPARATOR + DEGREES_decimal
            put(PATTERN_RAhhmmss_DECdeg_column, SearchInputType.COLUMN_RAhhmmssDECdeg);

        }
    };

    public static Map<String, SearchInputType> explainGeneral = new HashMap<String, SearchInputType>() {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {

            // DEGREES_decimal + RADEC_SEPARATOR + DEC_sex_degrees_space_pattern
            put(PATTERN_RAdeg_DECddmmss_space, SearchInputType.SPACE_RAdegDECddmmss);

            // DEGREES_decimal + RADEC_SEPARATOR + DEC_sex_degrees_column_pattern
            put(PATTERN_RAdeg_DECddmmss_column, SearchInputType.COLUMN_RAdegDECddmmss);

            // DEGREES_decimal + RADEC_SEPARATOR + DEGREES_decimal
            put(PATTERN_RAdeg_DECdeg, SearchInputType.RAdegDECdeg);

            put(TARGET, SearchInputType.TARGET);

        }
    };

    private static String RA_1 = BEGIN + SIGN + RA_DEGREES_decimal_base360 + END;
    private static String RA_2 = BEGIN + SIGN + RA_DEGREES_integer_base360 + END;
    private static String RA_3 = BEGIN + SIGN + RA_hhmmss_space_pattern + END;
    private static String RA_4 = BEGIN + SIGN + RA_hhmmss_column_pattern + END;
    private static String RA_5 = BEGIN + SIGN + RA_dddmmss_space_pattern + END;
    private static String RA_6 = BEGIN + SIGN + RA_dddmmss_column_pattern + END;

    public static String[] RAValid = { RA_1, RA_2, RA_3, RA_4, RA_5, RA_6 };

    public static String DEC_1 = BEGIN + SIGN + DEC_DEGREES_decimal_base90 + END;
    public static String DEC_2 = BEGIN + SIGN + DEC_DEGREES_integer_base90 + END;
    public static String DEC_3 = BEGIN + SIGN + DEC_ddmmss_space_pattern + END;
    public static String DEC_4 = BEGIN + SIGN + DEC_ddmmss_column_pattern + END;
    public static String[] DECValid = { DEC_1, DEC_2, DEC_3, DEC_4 };

}
