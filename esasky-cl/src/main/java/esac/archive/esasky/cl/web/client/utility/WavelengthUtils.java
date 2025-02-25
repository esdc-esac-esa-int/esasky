/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.cl.web.client.utility;

import java.util.*;
import java.util.stream.Collectors;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.WavelengthDescriptor;

public class WavelengthUtils {

    private WavelengthUtils() {}

    private static double minWavelengthRange = Double.MAX_VALUE;
    private static double maxWavelengthRange = Double.MIN_VALUE;

    private static final String UNKNOWN = "Unknown";

    public static String getShortName(CommonTapDescriptor descriptor) {
        final Double wavelengthStart = descriptor.getWavelengthStart();
        final Double wavelengthEnd = descriptor.getWavelengthEnd();

        WavelengthName wls = getWavelengthNameFromValue(wavelengthEnd);
        WavelengthName wle = getWavelengthNameFromValue(wavelengthStart);
        String wavelengthStartName=  wls != null ? TextMgr.getInstance().getText(wls.shortName) : UNKNOWN;
        String wavelengthEndName = wle != null ? TextMgr.getInstance().getText(wle.shortName) : UNKNOWN;

        if (!wavelengthStartName.equals(wavelengthEndName)) {
            final String toText = TextMgr.getInstance().getText("PointInformation_to");
            return toText.replace("$FROM$", wavelengthStartName).replace("$TO$", wavelengthEndName);
        } else {
            return wavelengthEndName;
        }

    }
    
    public static String getLongName(CommonTapDescriptor descriptor) {
        final Double wavelengthStart = descriptor.getWavelengthStart();
        final Double wavelengthEnd = descriptor.getWavelengthEnd();

        WavelengthName wls = getWavelengthNameFromValue(wavelengthEnd);
        WavelengthName wle = getWavelengthNameFromValue(wavelengthStart);
        String wavelengthStartName=  wls != null ? TextMgr.getInstance().getText(wls.longName) : UNKNOWN;
        String wavelengthEndName = wle != null ? TextMgr.getInstance().getText(wle.longName) : UNKNOWN;

        if (!wavelengthStartName.equals(wavelengthEndName)) {
            final String toText = TextMgr.getInstance().getText("PointInformation_to");
            return toText.replace("$FROM$", wavelengthStartName).replace("$TO$", wavelengthEndName);
        } else {
            return wavelengthEndName;
        }

    }

    public static void setWavelengthRangeMaxMin(List<CommonTapDescriptor> descriptors) {
        for(CommonTapDescriptor descriptor : descriptors) {
            Double meanWavelength = descriptor.getWavelengthCenter();
            if(meanWavelength != null) {
            	minWavelengthRange = Math.min(minWavelengthRange, meanWavelength);
            	maxWavelengthRange = Math.max(maxWavelengthRange, meanWavelength);
            }
        }
    }
    
    public static WavelengthName getWavelengthNameFromValue(Double value) {
    	if(value == null) {
    		return null;
    	}
    	for(WavelengthName wavelengthName : wavelengthNames) {
    		if(value < wavelengthName.maxWavelength) {
    			return wavelengthName;
    		}
    	}
    	return null;
    }

    public static List<WavelengthName> getWavelengthsNameFromRange(double min, double max) {
        List<WavelengthName> result = new ArrayList<>();

        // Since order is important, sort asc on wavelength
        List<WavelengthName> sortedWavelengthNames = Arrays.stream(wavelengthNames)
                .sorted(Comparator.comparing(wln -> wln.maxWavelength)).collect(Collectors.toList());

        double lastWavelengthMax = 0;
        for(WavelengthName wavelengthName : sortedWavelengthNames) {
            if(min < wavelengthName.maxWavelength && lastWavelengthMax < max) {
                result.add(wavelengthName);
            }
            lastWavelengthMax = wavelengthName.maxWavelength;
        }
        return result;
    }

    public static Double getWavelengthValueFromName(String name) {
        WavelengthName wln =  Arrays.stream(wavelengthNames).filter(x -> x.shortName.contains(name)
                || x.longName.contains(name)).findFirst().orElse(null);

        if (wln != null) {
            return wln.maxWavelength;
        }

        return null;
    }

    public static List<WavelengthDescriptor> createWavelengthDescriptor(double minWavelength, double maxWavelength) {
        List<WavelengthDescriptor> wavelengths = new LinkedList<WavelengthDescriptor>();
        
        addWavelengthDescriptor(minWavelength, maxWavelength, wavelengths, 0);
        
        return wavelengths;
    }
    
    public static class WavelengthName {
        public final String shortName;
        public final String longName;
        public final double maxWavelength;
        
        public WavelengthName(String shortName, String longName, double maxWavelength) {
            this.shortName = shortName;
            this.longName = longName;
            this.maxWavelength = maxWavelength;
        }

    }
    
    protected static WavelengthName [] wavelengthNames = new WavelengthName[] {
            new WavelengthName("Radio", "Radio", 3.0),
            new WavelengthName("Submm", "Submillimeter", 3.45),
            new WavelengthName("Far-IR", "Far-Infrared", 4.5),
            new WavelengthName("Mid-IR", "Mid-Infrared", 5.3),
            new WavelengthName("Near-IR", "Near-Infrared", 6.1),
            new WavelengthName("Optical", "Optical", 6.5),
            new WavelengthName("UV", "Ultraviolet", 8),
            new WavelengthName("Soft X-ray", "Soft X-ray", 10.),
            new WavelengthName("Hard X-ray", "Hard X-ray", 11),
            new WavelengthName("Gamma-ray", "Gamma-ray", 15.5),
            new WavelengthName("Neutrino", "Neutrino", Double.MAX_VALUE)
    };
    
    private static void addWavelengthDescriptor(double minWavelength, double maxWavelength, List<WavelengthDescriptor> wavelengths, int wavelengthIndex) {
        WavelengthDescriptor wavelengthDescriptor = new WavelengthDescriptor();
        
        ArrayList<Double> wavelengthRange = new ArrayList<Double>();
        wavelengthRange.add(minWavelength);
        WavelengthName wavelengthName = wavelengthNames[wavelengthIndex];
        if(minWavelength < wavelengthName.maxWavelength) {
            wavelengthDescriptor.setShortName(wavelengthName.shortName);
            wavelengthDescriptor.setLongName(wavelengthName.longName);
            wavelengthDescriptor.setPrefix("");
            if(maxWavelength <= wavelengthName.maxWavelength) {
                wavelengthRange.add(maxWavelength);
                wavelengthDescriptor.setRange(wavelengthRange);
                wavelengths.add(wavelengthDescriptor);
            } else {
                wavelengthRange.add(wavelengthName.maxWavelength);
                wavelengthDescriptor.setRange(wavelengthRange);
                wavelengths.add(wavelengthDescriptor);
                addWavelengthDescriptor(wavelengthName.maxWavelength, maxWavelength, wavelengths, wavelengthIndex + 1);
            }
        } else {
            addWavelengthDescriptor(minWavelength, maxWavelength, wavelengths, wavelengthIndex + 1);
        }
    }
    
    public static double getMinWavelengthRange() {
        return minWavelengthRange;
    }
    
    public static double getMaxWavelengthRange() {
        return maxWavelengthRange;
    }
}
