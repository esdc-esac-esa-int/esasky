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

package esac.archive.esasky.cl.web.client.model;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.NumberFormat;

import esac.archive.esasky.cl.web.client.utility.NumberFormatter;

public class RaPosition {
    private final double raDeg;
    private String raDegString;
    private String hours;
    private String minutes;
    private String seconds;
    
    
    public RaPosition(double raDeg) {
        this.raDeg = raDeg;
    }

    public RaPosition(String hmsOrDegrees) {
        String hmsSplit [] = hmsOrDegrees.split("h|m|s| ");
        if(hmsSplit.length > 1){
            for(int i = 0; i < hmsSplit.length; i++) {
                if(NumberFormatter.isNumber(hmsSplit[i])) {
                    setNextHms(hmsSplit[i]);
                }
            }
            
            this.raDeg = Double.parseDouble(hours) / 24 * 360
                    + Double.parseDouble(minutes) / 60 / 24 * 360
                    + Double.parseDouble(seconds) / 60 / 60 / 24 * 360;
        } else {
            String degreeSplit [] = hmsOrDegrees.split("d|\u00B0");
            raDeg = Double.parseDouble(degreeSplit[0]);
        }
    }
    
    private void setNextHms(String hmsPart) {
        if(hours == null) {
            hours = hmsPart;
        } else if(minutes == null) {
            minutes = hmsPart;
        } else if(seconds == null) {
            seconds = hmsPart;
        } else {
            Log.warn("Hours, minutes & seconds already set");
        }
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
        String format = "###.0000000";

        if(NumberFormat.getFormat(format).format(raDeg).equals("360.0000000")) {
            this.raDegString =  NumberFormat.getFormat(format).format(0.0);
        } else {
            this.raDegString =  NumberFormat.getFormat(format).format(raDeg);
        }
    }
    
    public double getRaDeg() {
        return raDeg;
    }
    
    
    public static RaPosition construct(double raDeg) { //for JSNI
        return new RaPosition(raDeg);
    }
    public static RaPosition construct(String raHmsOrDegrees) { //for JSNI
        return new RaPosition(raHmsOrDegrees);
    }
}
