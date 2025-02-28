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
            return NumberFormatter.formatToNumberWithSpaces(Math.round(value));
        }
        if(Math.abs(value) >= Math.pow(10, -numberOfDecimals)){
            if(format.format(value % 1).equals(".0") || format.format(value % 1).equals("0")) {
                return NumberFormatter.formatToNumberWithSpaces((long)value);
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
			return ((long)value == 0 && value < 0 ? "-": "") + NumberFormatter.formatToNumberWithSpaces((long)value + 1) + decimal;
		} else {
			return ((long)value == 0 && value < 0 ? "-": "") + NumberFormatter.formatToNumberWithSpaces((long)value) + decimal;
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
