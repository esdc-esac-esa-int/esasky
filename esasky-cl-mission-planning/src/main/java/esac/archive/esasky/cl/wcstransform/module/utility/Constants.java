package esac.archive.esasky.cl.wcstransform.module.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants.JWSTInstrument;

/**
 * @author Fabrizio Giordano Copyright (c) 2016 - European Space Agency
 */
public class Constants {

    // public static final String JWST_MISSION_NAME = "JWST";
    // public static final String JWST_NIRISS = "NIRISS";
    // public static final String JWST_NIRSPEC = "NIRSPEC";
    // public static final String JWST_NIRCAM = "NIRCAM";
    // public static final String JWST_MIRI = "MIRI";
    // public static final String JWST_ALL = "All";

    public enum PlanningMission {
        // JWST("JWST", 0), XMM("XMM", 1);
        JWST("JWST", 0);

        private String mission;
        private int index;

        PlanningMission(String mission, int index) {
            this.mission = mission;
            this.index = index;
        }

        public String getMissionName() {
            return this.mission;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public String toString() {
            return this.mission + " " + this.index;
        }
    }

    public enum Instrument {
        NIRSPEC(PlanningMission.JWST, JWSTInstrument.NIRSPEC_MSA.toString()), NIRCAM(PlanningMission.JWST, JWSTInstrument.NIRCAFULL.toString()), NIRISS(
                PlanningMission.JWST, JWSTInstrument.NIRISS_CEN.toString()), MIRI(PlanningMission.JWST, JWSTInstrument.MIRIM_FULL.toString()), FGS(
                        PlanningMission.JWST,JWSTInstrument.FGS1.toString() );

        private String instrument;
        private PlanningMission mission;

        Instrument(PlanningMission mission, String instrument) {
            this.mission = mission;
            this.instrument = instrument;

        }

        public String getInstrumentName() {
            return this.instrument;
        }

        public PlanningMission getMission() {
            return this.mission;
        }
        
 
        public static List<Instrument> getInstrumentsPerMission(PlanningMission mission) {
            List<Instrument> instrumentsName = new ArrayList<Instrument>();
            for (Instrument currInstrument : Instrument.values()) {
                if (currInstrument.getMission() == mission) {
                    instrumentsName.add(currInstrument);
                }
            }
            return instrumentsName;
        }

        public static Instrument getSingleInstrument(PlanningMission pm, String instrument) {
            for (Instrument curr : Instrument.values()) {
                if (curr.getMission() == pm && curr.getInstrumentName().equalsIgnoreCase(instrument)) {
                    return curr;
                }
            }
            return null;
        }
//        public static List<String> getDetectorsPerInstrument(Instrument instrument) {
//            List<String> detectorsName = new ArrayList<String>();
//            for (Instrument currInstrument : Instrument.values()) {
//                if (currInstrument.getInstrumentName() == instrument.getInstrumentName()) {
//                    detectorsName.add(currInstrument.getDetectorName());
//                }
//            }
//            return detectorsName;
//        }

        @Override
        public String toString() {
            return this.mission.toString() + " - " + this.instrument;
        }
    }
    
    public static Map<Instrument, Double> INSTRUMENT_ANGLES = new HashMap<Instrument, Double>() {{
        put(Instrument.NIRSPEC, -138.5);
        put(Instrument.NIRCAM, 0.0);
        put(Instrument.NIRISS, -0.57);
        put(Instrument.MIRI, -4.45);
        put(Instrument.FGS, 0.0);
        
    }};
    
//    public enum Detectors {
//
//        FGS1("FGS","FGS1", 207.19,-697.50), 
//        FGS2("FGS","FGS2",24.43,-697.50), 
//        NIS_CEN("NIRISS","Imaging FULL",-290.10,-697.50),
//        NIS_SUB256("NIRISS","Imaging SUB256",-349.2667,-638.7592),
//        NIS_SUB128("NIRISS","Imaging SUB128",-353.4934,-634.5678),
//        NIS_SUB64("NIRISS","Imaging SUB64",-355.6069,-632.4727),
//        NIS_WFSS_OFFSET("NIRISS","WFSS",-289.9670,-697.7233),
//        NIS_CEN_SOSS("NIRISS","SOSS",-290.10,-697.50),
//        NIS_AMI1("NIRISS","AMI 1",-292.1146,-760.9194),
//        NIS_AMI2("NIRISS","AMI 2",-292.0907,-763.2905),
//        NIS_AMI3("NIRISS","AMI 3",-289.4726,-763.5390),
//        NIS_AMI4("NIRISS","AMI 4",-289.4991,-760.9045),
//        NRS_FULL_MSA("NIRSPEC","MOS",376.905,-428.576), 
//        NRS_FULL_IFU("NIRSPEC","IFU",300.2961,-497.900),
//        NRS_S200A1_SLIT("NIRSPEC","Slit S200A1",330.7811,-479.2541), 
//        NRS_S200A2_SLIT("NIRSPEC","Slit S200A2",313.5764,-489.5507), 
//        NRS_S200B1_SLIT("NIRSPEC","Slit S200B1",438.9313,-364.6900), 
//        NRS_S400A1_SLIT("NIRSPEC","Slit S400A1",320.5037,-478.0541), 
//        NRS_S1600A1_SLIT("NIRSPEC","Slit S1600A1",320.2083,-473.8023), 
//        NIRCALL_FULL("NIRCAM","Imaging ALL",-0.3174,-492.5913),
//        NIRCA5_FULL("NIRCAM","Imaging A",86.1035,-493.2275),
//        NIRCB5_FULL("NIRCAM","Imaging B",-89.3892,-491.4440),
//        NIRCA_WFSS_ALL("NIRCAM","WFSS ALL",-0.3174,-492.5913),
//        MIRIM_FULL("MIRI", "Imaging", -453.5134,-373.4826),
//        MIRIM_SUB256("MIRI", "Imaging SUB256", -439.8060,-411.8255),
//        MIRIM_SUB128("MIRI", "Imaging SUB128", -381.1321, -330.6637),
//        MIRIM_SUB64("MIRI", "Imaging SUB64", -378.8662,-346.6130),
//        MIRIM_SLIT("MIRI", "LRS", -415.0568,-400.2337),
//        MIRIFU_CHANNEL1A("MIRI", "MRS (IFU)",-503.6712,-318.9195),
//        MIRIM_MASK1065("MIRI","4QPM/F1065C ",-393.7480,-420.7276), 
//        MIRIM_MASK1140("MIRI","4QPM/F1140C",-391.7395,-396.2745),
//        MIRIM_MASK1550("MIRI","4QPM/F1550C",-389.8113,-371.7931),
//        MIRIM_MASKLYOT("MIRI","LYOT/F2300C",-389.6296,-337.4260);
//
////      NIRCA1_FULL("NIRCAM","NIRCA1_FULL",120.6714,-527.3877),
////      NIRCA2_FULL("NIRCAM","NIRCA2_FULL",120.1121,-459.6806),
////      NIRCA3_FULL("NIRCAM","NIRCA3_FULL",51.9345,-527.8034),
////      NIRCA4_FULL("NIRCAM","NIRCA4_FULL",52.2768,-459.8097),
//
////      NIRCB1_FULL("NIRCAM","NIRCB1_FULL",-120.9682,-457.7527),
////      NIRCB2_FULL("NIRCAM","NIRCB2_FULL",-121.1443,-525.4582),
////      NIRCB3_FULL("NIRCAM","NIRCB3_FULL",-53.1238,-457.7804),
////      NIRCB4_FULL("NIRCAM","NIRCB4_FULL",-52.8182,-525.7273),
//        
////      NRS_FULL_MSA1("NIRSPEC","NRS_FULL_MSA1",474.032,-429.698),
////      NRS_FULL_MSA2("NIRSPEC","NRS_FULL_MSA2",379.572,-340.102),
////      NRS_FULL_MSA3("NIRSPEC","NRS_FULL_MSA3",374.105,-517.288),
////      NRS_FULL_MSA4("NIRSPEC","NRS_FULL_MSA4",288.753,-420.481),
//        
////      MIRIM_ILLUM("MIRI", "MIRIM_ILLUM",-453.5134,-373.48260), 
////      MIRIM_BRIGHTSKY("MIRI","MIRIM_BRIGHTSKY",-457.6068,-396.1679),
////        MIRIM_TALYOT_LL("MIRI","MIRIM_TALYOT_LL",-381.8058,-346.0118);
//	        
//        private String instrument;
//        private String detector;
//        private double posV2Ref;
//        private double posV3Ref;
//
//        Detectors(String instrument, String detector, double posV2Ref, double posV3Ref) {
//            this.instrument = instrument;
//        	this.detector = detector;
//            this.posV2Ref = posV2Ref;
//            this.posV3Ref = posV3Ref;
//        }
//
//
//        public String getInstrumentName() {
//            return this.instrument;
//        }
//
//        public String getDetectorName() {
//            return this.detector;
//        }
//
//        public double[] getPointReferenceInVFrame() {
//            return new double[]{this.posV2Ref,this.posV3Ref};
//        }
//        
//
//        public static List<Detectors> getDetectorsForInstrument(String instrument) {
//            List<Detectors> detectorList = new ArrayList<Detectors>();
//            for (Detectors det : Detectors.values()) {
//                if (det.getInstrumentName() == instrument) {
//                    detectorList.add(det);
//                }
//            }
//            return detectorList;
//        }
//        
//        public static Detectors getDefaultDetectorPerInstrument(String instrument){
//        		
//        	for(Detectors d: Detectors.values()){
//				
//				if(d.getInstrumentName().equals(instrument) && instrument.equals("FGS")){
//					return Detectors.FGS1;
//					
//				}else if(d.getInstrumentName().equals(instrument) && instrument.equals("NIRISS")){
//					return Detectors.NIS_CEN;
//					
//				}else if(d.getInstrumentName().equals(instrument) && instrument.equals("NIRCAM")){
//					return Detectors.NIRCALL_FULL;
//					
//				}else if(d.getInstrumentName().equals(instrument) && instrument.equals("NIRSPEC")){
//					return Detectors.NRS_FULL_MSA;
//					
//				}else if (d.getInstrumentName().equals(instrument) && instrument.equals("MIRI")){
//					return Detectors.MIRIM_FULL;
//				}
//			}
//        	
//        	return null;
//        }
//        
//        @Override
//        public String toString() {
//            return this.detector + ": v2:" + this.posV2Ref + " ,v3:" + this.posV3Ref;
//        }
//    }
}
