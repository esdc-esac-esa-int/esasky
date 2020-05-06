package esac.archive.esasky.cl.web.client.model;

import com.google.gwt.i18n.client.NumberFormat;

public class RaPosition {
    private final double raDeg;
    private String raDegString;
    private String hours;
    private String minutes;
    private String seconds;
    
    
    public RaPosition(double raDeg) {
        this.raDeg = raDeg;
    }
    
    private void computeHoursMinutesSeconds() {
        double hours = (raDeg / 360) * 24;
        double minutes = (hours - (int) hours) * 60;
        Double seconds = (minutes - (int) minutes) * 60;
        String secondsFormat = "00.000";
        
        if(NumberFormat.getFormat(secondsFormat).format(seconds).equals("60.000")) {
            seconds = 0.0;
            minutes += 1;
            if(minutes >= 60) {
                minutes = 0;
                hours += 1;
                if(hours >= 24) {
                    hours = 0;
                }
            }
        }
        
        this.hours = NumberFormat.getFormat("00").format((int) hours);
        this.minutes = NumberFormat.getFormat("00").format((int) minutes);
        this.seconds = NumberFormat.getFormat(secondsFormat).format(seconds);
    }

    public String getSpacedHmsString() {
        if(hours == null) {
            computeHoursMinutesSeconds();
        }
        return hours + " " + minutes + " " + seconds;
    }
    
    public String getHmsString() {
        if(hours == null) {
            computeHoursMinutesSeconds();
        }
        return hours + "h " + minutes + "m " + seconds + "s";
    }
    
    public String getDegreeString() {
        if(raDegString == null) {
            computeFormattedDegree();
        }
        return raDegString + "\u00B0";
    }
    
    public String getDegreeStringWithoutDegreeSymbol() {
        if(raDegString == null) {
            computeFormattedDegree();
        }
        return raDegString;
    }
    
    private void computeFormattedDegree() {
        String format = "000.0000000";

        if(NumberFormat.getFormat(format).format(raDeg).equals("360.0000000")) {
            this.raDegString =  NumberFormat.getFormat(format).format(0.0);
        } else {
            this.raDegString =  NumberFormat.getFormat(format).format(raDeg);
        }
    }
    
    
    public static RaPosition construct(double raDeg) { //for JSNI
        return new RaPosition(raDeg);
    }
}
