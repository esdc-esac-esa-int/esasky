package esac.archive.esasky.cl.wcstransform.module.utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import esac.archive.esasky.ifcs.model.descriptor.SiafEntries;
import esac.archive.esasky.ifcs.model.descriptor.SiafEntry;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants.JWSTInstrument;

/**
 * Contains a Mapping for SIAF XMLs
 *
 * @author eracero@sciops.esa.int
 *
 */
public class InstrumentMapping {
	private static SiafEntries entries;
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
    
	public void setSiafEntries(SiafEntries entries) {
		InstrumentMapping.entries = entries;
		setInstrumentList();

	}
	
	public List<SiafEntry> getSiafEntries() {
		return entries.getSiafEntry();
	}

	private void setInstrumentList() {

		for (JWSTInstrument ins : JWSTInstrument.values()) {
			if (!instrumentList.contains(ins.toString())) {
				instrumentList.add(ins.toString());
			}

			List<String> aperturesPerInstrument = new ArrayList<String>();
			for (SiafEntry entry : entries.getSiafEntry()) {
				if(entry.getInstrName().equalsIgnoreCase(ins.toString())) {
				if (!aperturesPerInstrument.contains(entry.getAperName())) {
					aperturesPerInstrument.add(entry.getAperName());

				}
				}
			}

			apertureList.put(ins.toString(), aperturesPerInstrument);
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
    		default:{
    			return JWSTInstrument.FGS1.getAperName();
    		}
    	}
    }

	public SiafEntry getApertureDetails(String aperName) {
		for (SiafEntry entry : entries.getSiafEntry()) {
			if (entry.getAperName().equalsIgnoreCase(aperName)) {
				return entry;
			}
		}
		return null;
	}
	
	public double[] selectReferencePosVFrame(String aperName) {
		for (SiafEntry entry : entries.getSiafEntry()) {
			if (entry.getAperName().equalsIgnoreCase(aperName)) {
				return new double[] {entry.getV2Ref(),entry.getV3Ref()};
			}
		}
		return null;
	}
	
    public double[] selectDefaultReferencePosVFrame(String instrument, String detector) {

        double[] reference = null;

        if (detector == null) {
            for (String d : apertureList.keySet()) {

                if (d.equals(instrument) && instrument.equals(JWSTInstrument.FGS1.toString())) {
                    reference = selectReferencePosVFrame(JWSTInstrument.FGS1.getAperName());
                    break;
                } else if (d.equals(instrument) && instrument.equals(JWSTInstrument.NIRISS_CEN.toString())) {
                    reference = selectReferencePosVFrame(JWSTInstrument.NIRISS_CEN.getAperName());
                    break;
                } else if (d.equals(instrument) && instrument.equals(JWSTInstrument.NIRCAFULL.toString())) {
                    reference = selectReferencePosVFrame(JWSTInstrument.NIRCAFULL.getAperName());
                    break;
                } else if (d.equals(instrument) && instrument.equals(JWSTInstrument.NIRSPEC_MSA.toString())) {
                    reference = selectReferencePosVFrame(JWSTInstrument.NIRSPEC_MSA.getAperName());
                    break;
                } else if (d.equals(instrument) && instrument.equals(JWSTInstrument.MIRIM_FULL.toString())) {
                    reference = selectReferencePosVFrame(JWSTInstrument.MIRIM_FULL.getAperName());
                    break;
                }
            }
        } else {
            reference = selectReferencePosVFrame(detector);

        }
        return new double[] { reference[0] / 3600., reference[1] / 3600. };

    }
    
    public List<String> getInstrumentList(){
    	return instrumentList;
    }


}

