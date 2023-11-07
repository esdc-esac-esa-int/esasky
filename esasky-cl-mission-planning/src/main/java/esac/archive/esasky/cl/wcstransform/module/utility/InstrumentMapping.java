package esac.archive.esasky.cl.wcstransform.module.utility;

import esac.archive.esasky.ifcs.model.descriptor.DS9DescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.SiafEntries;
import esac.archive.esasky.ifcs.model.descriptor.SiafEntry;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants.JWSTInstrument;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains a Mapping for SIAF XMLs
 *
 * @author eracero@sciops.esa.int
 *
 */
public class InstrumentMapping {
	private static SiafEntries siafEntries;
	private static DS9DescriptorList ds9Descriptors;
    private static Map<String, List<String>> apertureList;
    private static List<String> instrumentList;
    private static InstrumentMapping instance = null;

    
    /** private constructor **/
    private InstrumentMapping() {
    	instrumentList = new LinkedList<String>();
    	apertureList = new LinkedHashMap<String,List<String>>();
    }

    /**
     * Returns the InstrumentMapping instance.
     * @return The InstrumentMapping instance
     */
    public synchronized static InstrumentMapping getInstance() {
        if (instance == null) {
            instance = new InstrumentMapping();
        }
        return instance;
    }

	public static DS9DescriptorList getDs9Descriptors() {
		return ds9Descriptors;
	}

	public void setSiafEntries(SiafEntries entries) {
		InstrumentMapping.siafEntries = entries;
		setInstrumentList();

	}

	public void setDs9Entries(DS9DescriptorList descriptors) {
		ds9Descriptors = descriptors;

	}
	
	public List<SiafEntry> getSiafEntries() {
		return siafEntries.getSiafEntry();
	}

	private void setInstrumentList() {

		for (JWSTInstrument ins : JWSTInstrument.values()) {
			if (!instrumentList.contains(ins.toString())) {
				instrumentList.add(ins.toString());
			}

			List<String> aperturesPerInstrument = new ArrayList<>();
			for (SiafEntry entry : siafEntries.getSiafEntry()) {
				if(entry.getInstrName().equalsIgnoreCase(ins.toString())) {
					if (!aperturesPerInstrument.contains(entry.getAperName())) {
						aperturesPerInstrument.add(entry.getAperName());

					}
				}
			}

			apertureList.put(ins.toString(), aperturesPerInstrument);
		}

		for (EsaSkyConstants.XMMInstrument ins : EsaSkyConstants.XMMInstrument.values()) {
			if (!instrumentList.contains(ins.toString())) {
				instrumentList.add(ins.toString());
				apertureList.put(ins.toString(), Arrays.asList(ins.getAperName()));
			}
		}
	}
	
    
    public Map<String,List<String>> getApertureMap(){
    	return apertureList;
    }
    
    public List<String> getApertureListForInstrument(String instrument){
    	for(String insname: apertureList.keySet()) {
    		if(insname.toLowerCase().equalsIgnoreCase(instrument)) {
    			return apertureList.get(insname);
    		}
    	}
    	return null;
    }
    
    public String getDefaultApertureForInstrument(String instrument) {
    	switch(instrument) {
    		case "FGS":{
    			return JWSTInstrument.FGS1.getAperName();
    		}
    		case "NIRISS":{
    			return JWSTInstrument.NIRISS_CEN.getAperName();
    		}
    		case "NIRCam":{
    			return JWSTInstrument.NIRCAFULL.getAperName();
    		}
    		case "NIRSpec":{
    			return JWSTInstrument.NIRSPEC_MSA.getAperName();
    		}
    		case "MIRI":{
    			return JWSTInstrument.MIRIM_FULL.getAperName();
    		}
			case "EPIC-pn": {
				return EsaSkyConstants.XMMInstrument.XMM_EPIC_PN.getAperName();
			}
    		default:{
    			return JWSTInstrument.FGS1.getAperName();
    		}
    	}
    }

	public SiafEntry getApertureDetails(String aperName) {
		for (SiafEntry entry : siafEntries.getSiafEntry()) {
			if (entry.getAperName().equalsIgnoreCase(aperName)) {
				return entry;
			}
		}
		return null;
	}
	
	public double[] selectReferencePosVFrame(String aperName) {
		for (SiafEntry entry : siafEntries.getSiafEntry()) {
			if (entry.getAperName().equalsIgnoreCase(aperName)) {
				return new double[] {entry.getV2Ref(),entry.getV3Ref()};
			}
		}
		return null;
	}
	
    public double[] selectDefaultReferencePosVFrame(String instrument, String detector) {

        double[] reference = null;

        if (detector == null) {
        	
        	detector = getDefaultApertureForInstrument(instrument);
        } 

        reference = selectReferencePosVFrame(detector);

        return new double[] { reference[0] / 3600., reference[1] / 3600. };

    }
    
    public List<String> getInstrumentList(String mission){
		if (Constants.PlanningMission.JWST.getMissionName().equals(mission)) {
			return Arrays.stream(JWSTInstrument.values()).map(d -> d.toString()).distinct().collect(Collectors.toList());
		} else if (Constants.PlanningMission.XMM.getMissionName().equals(mission)) {
			return Arrays.stream(EsaSkyConstants.XMMInstrument.values()).map(d -> d.toString()).distinct().collect(Collectors.toList());
		} else {
			return null;
		}
    }


}

