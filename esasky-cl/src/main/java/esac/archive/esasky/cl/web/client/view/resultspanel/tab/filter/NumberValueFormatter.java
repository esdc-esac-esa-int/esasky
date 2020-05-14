package esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter;

import com.google.gwt.i18n.client.NumberFormat;

public class NumberValueFormatter implements ValueFormatter{

    private NumberFormat format;
    private NumberFormat scientificFormat;
    private int numberOfDecimals;
    
    public NumberValueFormatter() {
        this(4);
    }

    public NumberValueFormatter(int numberOfDecimals) {
        setFormat(4);
    }
    
    private void setFormat(int numberOfDecimals) {
        this.numberOfDecimals = numberOfDecimals;
        String pattern = "";
        for(int i = 0; i < numberOfDecimals; i++) {
            pattern += "#";
        }
        if(numberOfDecimals > 0) {
            pattern = "0." + pattern;
        }
        format = NumberFormat.getFormat(pattern);
        scientificFormat = NumberFormat.getFormat(pattern + "E0");
    }
    
    @Override
    public String formatValue(double value) {
        if(Math.abs(value) > Math.pow(10, -numberOfDecimals)){
            return format.format(value);
        }
        String sciFormatResult = scientificFormat.format(value);
        if(sciFormatResult.equals("0E0")) {
            return "0";
        }
        return sciFormatResult;
    }

    @Override
    public double getValueFromFormat(String formattedValue) {
        return format.parse(formattedValue);
    }
    
    //for JSNI
    public static String formatDouble(double value, int numberOfDecimals) {
        return new NumberValueFormatter(numberOfDecimals).formatValue(value);
    }
}
