package esac.archive.esasky.cl.web.client.utility;

import java.util.*;
import java.util.stream.Collectors;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.WavelengthDescriptor;

public class WavelengthUtils {

    private static double minWavelengthRange = Double.MAX_VALUE;
    private static double maxWavelengthRange = Double.MIN_VALUE;

    public static String getShortName(IDescriptor descriptor) {
        List<WavelengthDescriptor> wavelengthDescriptors = descriptor.getWavelengths();
        final WavelengthDescriptor firstWlDesc = wavelengthDescriptors.get(0);
        String wavelengthShortName = TextMgr.getInstance().getText(firstWlDesc.getPrefix() + firstWlDesc.getShortName());
        if(descriptor.getWavelengths().size() > 1) {
            final WavelengthDescriptor lastWlDesc = wavelengthDescriptors.get(descriptor.getWavelengths().size() - 1);
            final String toText = TextMgr.getInstance().getText("PointInformation_to");
            wavelengthShortName = toText.replace("$FROM$", wavelengthShortName)
                    .replace("$TO$", TextMgr.getInstance().getText(lastWlDesc.getPrefix() + lastWlDesc.getShortName()));
        }
        return wavelengthShortName;
    }
    
    public static String getLongName(IDescriptor descriptor) {
        List<WavelengthDescriptor> wavelengthDescriptors = descriptor.getWavelengths();
        final WavelengthDescriptor firstWlDesc = wavelengthDescriptors.get(0);
        String wavelengthLongName = TextMgr.getInstance().getText(firstWlDesc.getPrefix() + firstWlDesc.getLongName());
        if(descriptor.getWavelengths().size() > 1) {
            final WavelengthDescriptor lastWlDesc = wavelengthDescriptors.get(descriptor.getWavelengths().size() - 1);
            final String toText = TextMgr.getInstance().getText("PointInformation_to");
            wavelengthLongName = toText.replace("$FROM$", wavelengthLongName)
                    .replace("$TO$", TextMgr.getInstance().getText(lastWlDesc.getPrefix() + lastWlDesc.getLongName()));
        }
        return wavelengthLongName;
    }

    public static void setWavelengthRangeMaxMin(List<? extends IDescriptor> descriptors) {
        for(IDescriptor descriptor : descriptors) {
            double meanWavelength = descriptor.getCenterWavelengthValue();
            minWavelengthRange = Math.min(minWavelengthRange, meanWavelength);
            maxWavelengthRange = Math.max(maxWavelengthRange, meanWavelength);
        }
    }
    
    public static WavelengthName getWavelengthNameFromValue(double value) {
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
            new WavelengthName("Far-IR", "Far-Infrared", 4.6),
            new WavelengthName("Mid-IR", "Mid-Infrared", 5.8),
            new WavelengthName("Near-IR", "Near-Infrared", 6.1),
            new WavelengthName("Optical", "Optical", 6.5),
            new WavelengthName("UV", "Ultraviolet", 8),
            new WavelengthName("SoftX-ray", "SoftX-ray", 10.),
            new WavelengthName("HardX-ray", "HardX-ray", 11),
            new WavelengthName("Gamma-ray", "Gamma-ray", Double.MAX_VALUE)
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
