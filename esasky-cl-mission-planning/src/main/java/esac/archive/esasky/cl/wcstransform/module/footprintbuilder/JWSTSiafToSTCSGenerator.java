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

package esac.archive.esasky.cl.wcstransform.module.footprintbuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.wcstransform.module.utility.Constants.Instrument;
import esac.archive.esasky.cl.wcstransform.module.utility.Constants.PlanningMission;
import esac.archive.esasky.cl.wcstransform.module.utility.InstrumentMapping;
import esac.archive.esasky.ifcs.model.descriptor.SiafEntry;

/**
 * @author eracero@sciops.esa.int Copyright (c) 2016 - European Space Agency
 */

public class JWSTSiafToSTCSGenerator extends STCSAbstractGenerator {

    public JWSTSiafToSTCSGenerator(String mission) {
        super.mission = mission;
        setIntruments();
    }

    private void setIntruments() {
        super.instruments = new ArrayList<String>();
        List<String> instrumentParsed = InstrumentMapping.getInstance().getInstrumentList(this.mission);
       for(int i =0; i<instrumentParsed.size();i++) {
    	   super.instruments.add(instrumentParsed.get(i));
       }
    }
    
    private Vector<double[]> InstrumentPoly(String instrument) {

        Vector<double[]> pixels = new Vector<double[]>();
        List<String> apertures = InstrumentMapping.getInstance().getApertureListForInstrument(instrument);
       
        for(String aperture: apertures) {
        	
        	SiafEntry entry = InstrumentMapping.getInstance().getApertureDetails(aperture);
        	
        	double v2Ref = entry.getV2Ref();
        	double v3Ref = entry.getV3Ref();
        	int parity = entry.getvIdlParity();
        	double angle = entry.getV3IdlYAngle();
        	double[] vertex1 = new double[]{entry.getxIdlVert1(),entry.getyIdlVert1()};
        	double[] vertex2 = new double[]{entry.getxIdlVert2(),entry.getyIdlVert2()};
        	double[] vertex3 = new double[]{entry.getxIdlVert3(),entry.getyIdlVert3()};
        	double[] vertex4 = new double[]{entry.getxIdlVert4(),entry.getyIdlVert4()};

        	double[] pixel1 = JWSTSIAFUtils.convertIdealToScienceCoords(vertex1, v2Ref, v3Ref, parity,angle);
        	double[] pixel2 = JWSTSIAFUtils.convertIdealToScienceCoords(vertex2, v2Ref, v3Ref, parity,angle);
        	double[] pixel3 = JWSTSIAFUtils.convertIdealToScienceCoords(vertex3, v2Ref, v3Ref, parity,angle);
        	double[] pixel4 = JWSTSIAFUtils.convertIdealToScienceCoords(vertex4, v2Ref, v3Ref, parity,angle);
        	
        	pixels.addElement(pixel1);
        	pixels.addElement(pixel2);
        	pixels.addElement(pixel3);
        	pixels.addElement(pixel4);
//    		  double[] p4nca = JWSTSIAFUtils.convertIdealToScienceCoords(p4nca_ideal, v2ref, v3ref, -1,-1.25);
            Log.debug(instrument + "," + aperture);
            Log.debug("		Ideal to Sci:" + pixel1[0] + "," + pixel1[1]);
            Log.debug("		Ideal to Sci:" + pixel2[0] + "," + pixel2[1]);
            Log.debug("		Ideal to Sci:" + pixel3[0] + "," + pixel3[1]);
            Log.debug("		Ideal to Sci:" + pixel4[0] + "," + pixel4[1]);
        }
        
        return pixels;
    }
  
    @Override
    public Map<String, Vector<double[]>> computeInstrumentPolygon(String instrument,
            String detector, double rotationDeg, double raDeg, double decDeg) {
        Log.debug("[JWSTSiafToSTCS][computeInstrumentPolygon] Ins=" + instrument + ",aper=" + detector);
    	Map<String, Vector<double[]>> fullFovPixels = new HashMap<String, Vector<double[]>>();
        Map<String, Vector<double[]>> fullFovSkyCoords = new HashMap<String, Vector<double[]>>();

        List<String> availableInstruments = InstrumentMapping.getInstance().getInstrumentList(this.mission);
        for(String instrumentName : availableInstruments) {
        	fullFovPixels.put(instrumentName, InstrumentPoly(instrumentName));
        }


        double[] referencePosV2V3 = InstrumentMapping.getInstance().selectDefaultReferencePosVFrame(instrument,detector);

        Log.debug("{ComputeInstrumentPolygon: RA:" + raDeg + ",Dec:" + decDeg + ", PA: " + rotationDeg + "{V2="
                + referencePosV2V3[0] * 3600 + ", V3=" + referencePosV2V3[1] * 3600 + "}");

        BigDecimal[][] vToSkyFrameMatrix = JWSTSIAFUtils.getTelescopeToSkyFrameReferenceMatrix(
                raDeg, decDeg, rotationDeg, referencePosV2V3[0], referencePosV2V3[1]);

        for (String inst : fullFovPixels.keySet()) {
            fullFovSkyCoords.put(inst,
                    projectPixelsToSkyCoords(fullFovPixels.get(inst), vToSkyFrameMatrix));
        }

        return fullFovSkyCoords;

    }

    @Override
    public Map<String, double[]> computeInstrumentLabels(String instrument, String detector, double angle, double ra, double dec) {
        return Collections.emptyMap();
    }

    private Vector<double[]> projectPixelsToSkyCoords(Vector<double[]> pixels,
            BigDecimal[][] rotationMatrix) {

        Vector<double[]> skycoords = new Vector<double[]>();

        for (double[] pixel : pixels) {

            double[] coords = JWSTSIAFUtils.convertTelescopeToSkyCoords(pixel[0] / 3600.,
                    pixel[1] / 3600., rotationMatrix);

            skycoords.addElement(coords);
        }

        return skycoords;
    }

    @Override
    public String generateSTCS(Vector<double[]> pixels) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < pixels.size(); k++) {
            if ((k % 4) == 0) {
                sb.append(" POLYGON J2000");
            }

            double[] point = pixels.elementAt(k);

            sb.append(" ").append(point[0]).append(" ").append(point[1]);
        }
        // System.out.println(sb.toString());
        return sb.toString();
    }

    @Override
    public double[] selectReferencePosVFrame(String instrument, String detector) {
    	return InstrumentMapping.getInstance().selectDefaultReferencePosVFrame(instrument,detector);
    }
//
//        double[] reference = null;
//
//        if (detector == null) {
//            for (Detectors d : Detectors.values()) {
//
//                if (d.getInstrumentName().equals(instrument) && instrument.equals("FGS")) {
//                    reference = (Detectors.FGS1.getPointReferenceInVFrame());
//                    break;
//                } else if (d.getInstrumentName().equals(instrument) && instrument.equals("NIRISS")) {
//                    reference = Detectors.NIS_CEN.getPointReferenceInVFrame();
//                    break;
//                } else if (d.getInstrumentName().equals(instrument) && instrument.equals("NIRCAM")) {
//                    reference = Detectors.NIRCALL_FULL.getPointReferenceInVFrame();
//                    break;
//                } else if (d.getInstrumentName().equals(instrument) && instrument.equals("NIRSPEC")) {
//                    reference = Detectors.NRS_FULL_MSA.getPointReferenceInVFrame();
//                    break;
//                } else if (d.getInstrumentName().equals(instrument) && instrument.equals("MIRI")) {
//                    reference = Detectors.MIRIM_FULL.getPointReferenceInVFrame();
//                    break;
//                }
//            }
//        } else {
//            for (Detectors d : Detectors.values()) {
//                if (d.getDetectorName().equals(detector) && d.getInstrumentName().equals(instrument)) {
//                    reference = d.getPointReferenceInVFrame();
//                    break;
//                }
//            }
//
//        }
//
//        return new double[] { reference[0] / 3600., reference[1] / 3600. };
//
//    }

    // @Override
    @Override
    public Map<String, Vector<double[]>> getDetectorsSkyCoordsForInstrument(double raDeg,
            double decDeg, double rotationDeg, String instrument, String detector) {

        Map<String, Vector<double[]>> detectorMap = new HashMap<String, Vector<double[]>>();

        List<String> listOfDetectors = InstrumentMapping.getInstance().getApertureListForInstrument(instrument);

//        double[] referencePosV2V3 = selectReferencePosVFrame(instrument, detector);
        
        double[] referencePosV2V3 = InstrumentMapping.getInstance().selectDefaultReferencePosVFrame(instrument, detector);

        System.out.println("{DetectorsSkyCoordsForInstrument: RA:" + raDeg + ",Dec:" + decDeg + ", PA: " + rotationDeg + "{V2="
                + referencePosV2V3[0] * 3600 + ", V3=" + referencePosV2V3[1] * 3600 + "}");

        BigDecimal[][] vToSkyFrameMatrix = JWSTSIAFUtils.getTelescopeToSkyFrameReferenceMatrix(
                raDeg, decDeg, rotationDeg, referencePosV2V3[0], referencePosV2V3[1]);

        Vector<double[]> detectorCentersInVFrame = new Vector<double[]>();
        Vector<double[]> detectorCentersInSkyFrame = new Vector<double[]>();

        for (String d : listOfDetectors) {

            detectorCentersInVFrame = new Vector<double[]>();
            detectorCentersInSkyFrame = new Vector<double[]>();
            
            SiafEntry entry = InstrumentMapping.getInstance().getApertureDetails(d);
            
            detectorCentersInVFrame.add(new double[]{entry.getV2Ref(),entry.getV3Ref()});
            
            detectorCentersInSkyFrame = projectPixelsToSkyCoords(detectorCentersInVFrame,
                    vToSkyFrameMatrix);

            detectorMap.put(d, detectorCentersInSkyFrame);
        }

        return detectorMap;
    }

    // @Override
    @Override
    public Map<String, Vector<double[]>> getDetectorsSkyCoordsForFoV(double raDeg,
            double decDeg, double rotationDeg, String instrument, String detector) {

    	   Map<String, Vector<double[]>> detectorMap = new HashMap<String, Vector<double[]>>();

           List<String> listOfDetectors = InstrumentMapping.getInstance().getApertureListForInstrument(instrument);

//           double[] referencePosV2V3 = selectReferencePosVFrame(instrument, detector);
           
           double[] referencePosV2V3 = InstrumentMapping.getInstance().selectDefaultReferencePosVFrame(instrument, detector);
           
    	
//    	Map<String, Vector<double[]>> detectorMap = new HashMap<String, Vector<double[]>>();
//
//        double[] referencePosV2V3 = selectReferencePosVFrame(instrument, detector);

        System.out.println("getDetectorsSkyCoords{RA:" + raDeg + ",Dec:" + decDeg + ", PA: " + rotationDeg + "{V2="
                + referencePosV2V3[0] * 3600 + ", V3=" + referencePosV2V3[1] * 3600 + "}");

        BigDecimal[][] vToSkyFrameMatrix = JWSTSIAFUtils.getTelescopeToSkyFrameReferenceMatrix(
                raDeg, decDeg, rotationDeg, referencePosV2V3[0], referencePosV2V3[1]);

        Vector<double[]> detectorCentersInVFrame = new Vector<double[]>();
        Vector<double[]> detectorCentersInSkyFrame = new Vector<double[]>();

        for (Instrument inst : Instrument.getInstrumentsPerMission(PlanningMission.JWST)) {

            for (String d : listOfDetectors) {

                detectorCentersInVFrame = new Vector<double[]>();
                detectorCentersInSkyFrame = new Vector<double[]>();

                SiafEntry entry = InstrumentMapping.getInstance().getApertureDetails(d);
                
                detectorCentersInVFrame.add(new double[]{entry.getV2Ref(),entry.getV3Ref()});
                
                detectorCentersInSkyFrame = projectPixelsToSkyCoords(detectorCentersInVFrame,
                        vToSkyFrameMatrix);

                detectorMap.put(d, detectorCentersInSkyFrame);
            }
        }

        return detectorMap;
    }

}
