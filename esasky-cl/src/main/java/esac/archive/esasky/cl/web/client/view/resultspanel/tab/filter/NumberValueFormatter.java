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
            format = NumberFormat.getFormat(pattern.substring(1));
        } else {
            pattern = "0";
            format = NumberFormat.getFormat(pattern);
        }
        
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
            	return formatDecimalValue(value);
                
            }
        }
        String sciFormatResult = scientificFormat.format(value);
        if(sciFormatResult.equals("0E0")) {
            return "0";
        }
        return sciFormatResult;
    }

	private String formatDecimalValue(double value) {
		String decimal = format.format(Math.abs(value) % 1);
		while (decimal.endsWith("0")) {
			decimal = decimal.substring(0, decimal.length() - 1);
		}
		if("0.".equals(decimal) || ".".equals(decimal)) {
			decimal = "";
		}
		if (decimal.startsWith("1")) {
			decimal = decimal.replaceFirst("1", "");
			if (".".equals(decimal)) {
				decimal = "";
			}
			return ((int)value == 0 && value < 0 ? "-": "") + NumberFormatter.formatToNumberWithSpaces((int)value + 1) + decimal;
		} else {
			return ((int)value == 0 && value < 0 ? "-": "") + NumberFormatter.formatToNumberWithSpaces((int)value) + decimal;
		}
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
        if(NumberValueFormatter.isString(value)) {
            return new NumberValueFormatter(numberOfDecimals).getValueFromFormat(value);
        }
        return NumberValueFormatter.convertToDouble(value);
    }
    
    private static native boolean isString(String text)/*-{
        return typeof text === 'string';
    }-*/;
    
    private static native double convertToDouble(String object)/*-{
        return object;
    }-*/;
}
