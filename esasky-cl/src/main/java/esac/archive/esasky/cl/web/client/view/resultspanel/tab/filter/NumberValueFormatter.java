package esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter;

import com.google.gwt.i18n.client.NumberFormat;

import esac.archive.esasky.cl.web.client.utility.NumberFormatter;

public class NumberValueFormatter implements ValueFormatter{

    private NumberFormat format;
    private NumberFormat scientificFormat;
    private int numberOfDecimals;
    
    public NumberValueFormatter() {
        this(4);
    }

    public NumberValueFormatter(int numberOfDecimals) {
        setFormat(numberOfDecimals);
    }
    
    private void setFormat(int numberOfDecimals) {
        this.numberOfDecimals = numberOfDecimals;
        String pattern = "";
        for(int i = 0; i < numberOfDecimals; i++) {
            pattern += "#";
        }
        if(numberOfDecimals > 0) {
            pattern = "0." + pattern;
        } else {
            pattern = "0";
        }
        format = NumberFormat.getFormat(pattern.substring(1));
        scientificFormat = NumberFormat.getFormat(pattern + "E0");
    }
    
    @Override
    public String formatValue(double value) {
        if(numberOfDecimals == 0) {
            return NumberFormatter.formatToNumberWithSpaces((int)Math.round(value));
        }
        if(Math.abs(value) >= Math.pow(10, -numberOfDecimals)){
            if(format.format(value % 1).equals(".0") || format.format(value % 1).equals("0")) {
                return NumberFormatter.formatToNumberWithSpaces((int)value);
            } else {
                return ((int)value == 0 && value < 0 ? "-": "") + NumberFormatter.formatToNumberWithSpaces((int)value) + format.format(Math.abs(value) % 1);
            }
        }
        String sciFormatResult = scientificFormat.format(value);
        if(sciFormatResult.equals("0E0")) {
            return "0";
        }
        return sciFormatResult;
    }

    @Override
    public double getValueFromFormat(String formattedValue) {
        return format.parse(formattedValue.replaceAll("\u2009| ", ""));
    }
    
    //for JSNI
    public static String formatDouble(double value, int numberOfDecimals) {
        return new NumberValueFormatter(numberOfDecimals).formatValue(value);
    }
    public static double formatStringToDouble(String value, int numberOfDecimals) {
        return new NumberValueFormatter(numberOfDecimals).getValueFromFormat(value);
    }
}
