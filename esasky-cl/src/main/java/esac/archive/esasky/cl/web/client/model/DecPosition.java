package esac.archive.esasky.cl.web.client.model;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.NumberFormat;

import esac.archive.esasky.cl.web.client.utility.NumberFormatter;

public class DecPosition {
    private final double decDeg;
    private String degString;
    private String degStringFloored;
    private String minutesString;
    private String secondsString;
    private final String sign;


    public DecPosition(double decDeg) {
        if(decDeg < 0) {
            sign = "-";
        } else {
            sign = "+";
        }
        this.decDeg = Math.abs(decDeg);
    }

    public DecPosition(String dmsOrDegrees) {
        String dmsSplit [] = dmsOrDegrees.split("\u00B0|d|m|s| |'|\"");
        if(dmsSplit.length > 3){
            for(int i = 0; i < dmsSplit.length; i++) {
                if(NumberFormatter.isNumber(dmsSplit[i])) {
                    setNextDms(dmsSplit[i]);
                }
            }

            decDeg = Double.parseDouble(degString)
                    + Double.parseDouble(minutesString) / 60
                    + Double.parseDouble(secondsString) / 60 / 60;
        } else {
            String degreeSplit [] = dmsOrDegrees.split("d|\u00B0");
            decDeg = Double.parseDouble(degreeSplit[0]);
        }
        if(decDeg < 0) {
            sign = "-";
        } else {
            sign = "+";
        }
    }

    // Corrected calculation, leaving original for now due to compability
    public DecPosition(String dmsOrDegrees, boolean fix) {
        String dmsSplit [] = dmsOrDegrees.split("\u00B0|d|m|s| |'|\"");
        double signVal = 0;
        if(dmsSplit.length > 2){
            for(int i = 0; i < dmsSplit.length; i++) {
                if(NumberFormatter.isNumber(dmsSplit[i])) {
                    setNextDms(dmsSplit[i]);
                }
            }

            signVal =  Double.parseDouble(degString)
                    + Double.parseDouble(minutesString) / 60
                    + Double.parseDouble(secondsString) / 60 / 60;


            decDeg = Math.abs(Double.parseDouble(degString))
                    + Math.abs(Double.parseDouble(minutesString)) / 60
                    + Math.abs(Double.parseDouble(secondsString)) / 60 / 60;
        } else {
            String degreeSplit [] = dmsOrDegrees.split("d|\u00B0");
            decDeg = Double.parseDouble(degreeSplit[0]);
            signVal = decDeg;
        }
        if(signVal < 0) {
            sign = "-";
        } else {
            sign = "+";
        }
    }

    private void setNextDms(String dmsPart) {
        if(degString == null) {
            degString = dmsPart;
        } else if(minutesString == null) {
            minutesString = dmsPart;
        } else if(secondsString == null) {
            secondsString = dmsPart;
        } else {
            Log.warn("Degrees, minutes & seconds already set");
        }
    }

    private void copmuteDegreesMinutesSeconds() {
        double decDegrees = decDeg;
        double minutes = (decDegrees - (int) decDegrees) * 60;
        double seconds = (minutes - (int) minutes) * 60;
        String secondsFormat = "00.00";

        if(NumberFormat.getFormat(secondsFormat).format(seconds).equals("60.00")) {
            seconds = 0.0;
            minutes += 1;
            if(minutes >= 60) {
                minutes = 0;
                decDegrees += 1;
            }
        }

        degStringFloored = NumberFormat.getFormat("00").format((int) decDegrees);
        minutesString =  NumberFormat.getFormat("00").format((int) minutes);
        secondsString = NumberFormat.getFormat(secondsFormat).format(seconds);
    }

    // Corrected calculation, leaving original for now due to compability
    private void copmuteDegreesMinutesSecondsFix() {
        double decDegrees = decDeg;
        double minutes = Math.abs((decDegrees - (int) decDegrees) * 60);
        double seconds = Math.abs((minutes - (int) minutes) * 60);
        String secondsFormat = "00.00";

        if(NumberFormat.getFormat(secondsFormat).format(seconds).equals("60.00")) {
            seconds = 0.0;
            minutes += 1;
            if(minutes >= 60) {
                minutes = 0;
                decDegrees += 1;
            }
        }

        degStringFloored = NumberFormat.getFormat("00").format((int) decDegrees);
        minutesString =  NumberFormat.getFormat("00").format((int) minutes);
        secondsString = NumberFormat.getFormat(secondsFormat).format(seconds);
    }

    private void computeFormattedDegree() {
        double decDegrees = decDeg;
        String format = "00.0000000";

        if(NumberFormat.getFormat(format).format(decDegrees).equals("90.0000000")) {
            decDegrees = 0.0;
        }

        degString = NumberFormat.getFormat(format).format(decDegrees);
    }

    public String getSpacedDmsString() {
        if(degStringFloored == null) {
            copmuteDegreesMinutesSeconds();
        }
        return sign + degStringFloored + " " + minutesString + " " + secondsString;
    }

    //Corrected calculation, leaving original for now due to compability
    public String getSpacedDmsStringFix() {
        if(degStringFloored == null) {
            copmuteDegreesMinutesSecondsFix();
        }
        if (degStringFloored.startsWith("-")) {
            return degStringFloored + " " + minutesString + " " + secondsString;
        }
        else {
            return sign + degStringFloored + " " + minutesString + " " + secondsString;
        }
    }

    public String getSymbolDmsString() {
        if(degStringFloored == null) {
            copmuteDegreesMinutesSeconds();
        }

        return sign + degStringFloored + "\u00B0 " + minutesString + "' " + secondsString + "\"";
    }

    public String getDegreeString() {
        if(degString == null) {
            computeFormattedDegree();
        }
        return sign + degString + "\u00B0";
    }

    public String getDegreeStringWithoutDegreeSymbol() {
        if(degString == null) {
            computeFormattedDegree();
        }
        return sign + degString;
    }

    public double getDecDeg() {
        return decDeg;
    }

    // Corrected calculation, leaving original for now due to compability
    public double getDecDegFix() {
        if (sign == "-" && decDeg > 0)  {
            return -decDeg;
        } else {
            return decDeg;
        }

    }

    public static DecPosition construct(double decDeg) { //for JSNI
        return new DecPosition(decDeg);
    }
    public static DecPosition construct(String decDmsOrDegrees) { //for JSNI
        return new DecPosition(decDmsOrDegrees);
    }
}
