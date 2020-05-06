package esac.archive.esasky.cl.web.client.model;

import com.google.gwt.i18n.client.NumberFormat;

public class DecPosition {
    private final double decDeg;
    private String degString;
    private String degStringFloored;
    private String minutesString;
    private String secondsString;
    private String sign;
    
    
    public DecPosition(double decDeg) {
        if(decDeg < 0) {
            sign = "-";
        } else {
            sign = "+";
        }
        this.decDeg = Math.abs(decDeg);
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
    
    public String getSymbolDmsString() {
        if(degStringFloored == null) {
            copmuteDegreesMinutesSeconds();
        }
        
        return sign + degStringFloored + "\u00B0 " + minutesString + "\" " + secondsString + "'";
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
    
    public static DecPosition construct(double decDeg) { //for JSNI
        return new DecPosition(decDeg);
    }
}
