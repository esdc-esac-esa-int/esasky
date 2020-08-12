package esac.archive.esasky.cl.web.client.utility;

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
}
