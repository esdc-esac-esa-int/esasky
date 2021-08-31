package esac.archive.esasky.cl.web.client.utility;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.WavelengthDescriptor;

public class WavelengthUtils {

    public static double minWavelengthRange = Double.MAX_VALUE;
    public static double maxWavelengthRange = Double.MIN_VALUE;

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

    public static double getMeanWavelength(IDescriptor descriptor) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for(WavelengthDescriptor wavelengthDescriptor : descriptor.getWavelengths()) {
            min = Math.min(min, wavelengthDescriptor.getRange().get(0));
            max = Math.max(max, wavelengthDescriptor.getRange().get(1));
        }
        return (min + max) / 2;
    }
    
    public static void setWavelengthRangeMaxMin(List<? extends IDescriptor> descriptors) {
        for(IDescriptor descriptor : descriptors) {
            double meanWavelength = getMeanWavelength(descriptor);
            minWavelengthRange = Math.min(minWavelengthRange, meanWavelength);
            maxWavelengthRange = Math.max(maxWavelengthRange, meanWavelength);
        }
    }

    public static List<WavelengthDescriptor> createWavelengthDescriptor(double minWavelength, double maxWavelength) {
        List<WavelengthDescriptor> wavelengths = new LinkedList<WavelengthDescriptor>();
        
        addWavelengthDescriptor(minWavelength, maxWavelength, wavelengths, 0);
        
        return wavelengths;
    }
    
    public static class WavelengthName {
        public String shortName;
        public String longName;
        public double maxWavelength;
        
        public WavelengthName(String shortName, String longName, double maxWavelength) {
            this.shortName = shortName;
            this.longName = longName;
            this.maxWavelength = maxWavelength;
        }
    }
    
    public static WavelengthName [] wavelengthNames = new WavelengthName[] {
            new WavelengthName("Radio", "Radio", 2.0),
            new WavelengthName("Submm", "Submillimeter", 3),
            new WavelengthName("IR", "Infrared", 6),
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
}
