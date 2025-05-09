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

import esac.archive.esasky.cl.wcstransform.module.utility.Constants;
import esac.archive.esasky.cl.wcstransform.module.utility.InstrumentMapping;
import esac.archive.esasky.ifcs.model.descriptor.DS9Descriptor;
import esac.archive.esasky.ifcs.model.descriptor.DS9TextDescriptor;

import java.math.BigDecimal;
import java.util.*;

public class DS9ToSTCSGenerator extends STCSAbstractGenerator {

    public DS9ToSTCSGenerator(String mission) {
        super.mission = mission;
        setIntruments();
    }

    private void setIntruments() {
        super.instruments = new ArrayList<>();
        List<String> instrumentParsed = InstrumentMapping.getInstance().getInstrumentList(this.mission);
        for (int i = 0; i < instrumentParsed.size(); i++) {
            super.instruments.add(instrumentParsed.get(i));
        }
    }

    private Vector<double[]> InstrumentPoly(DS9Descriptor entry) {

        Vector<double[]> pixels = new Vector<>();

        if (entry != null) {
            for (String shape : entry.getShapes()) {
                if (shape.toLowerCase().contains("polygon")) {
                    pixels.addAll(DS9Utils.getShapePixels(shape));
                }
            }
        }

        return pixels;
    }

    @Override
    public Map<String, Vector<double[]>> computeInstrumentPolygon(String instrument, String detector, double rotationDeg, double raDeg, double decDeg) {
        Map<String, Vector<double[]>> fullFovPixels = new HashMap<>();
        Map<String, Vector<double[]>> fullFovSkyCoords = new HashMap<>();


        DS9Descriptor descriptor = InstrumentMapping.getInstance().getDs9Descriptors().getDescriptors().stream().filter(d -> d.getInstrument().equals(instrument)).findFirst().orElse(null);
        if (descriptor != null) {
            fullFovPixels.put(descriptor.getInstrument(), InstrumentPoly(descriptor));

            double[] referencePoint = DS9Utils.getReferencePoint(descriptor);

            BigDecimal[][] vToSkyFrameMatrix = JWSTSIAFUtils.getTelescopeToSkyFrameReferenceMatrix(
                    raDeg, decDeg, rotationDeg, referencePoint[0], referencePoint[1]);

            for (String inst : fullFovPixels.keySet()) {
                fullFovSkyCoords.put(inst,
                        projectPixelsToSkyCoords(fullFovPixels.get(inst), vToSkyFrameMatrix));
            }
        }

        return fullFovSkyCoords;
    }

    @Override
    public Map<String, double[]> computeInstrumentLabels(String instrument, String detector, double angle, double ra, double dec) {
        Map<String, double[]> fullFovSkyCoords = new HashMap<>();

        DS9Descriptor ds9Descriptor = InstrumentMapping.getInstance().getDs9Descriptors().getDescriptors()
                .stream().filter(d -> d.getInstrument().equals(instrument)).findFirst().orElse(null);


        if (ds9Descriptor == null || ds9Descriptor.getTexts().isEmpty()) {
            return fullFovSkyCoords;
        }

        double[] referencePoint = DS9Utils.getReferencePoint(ds9Descriptor);
        BigDecimal[][] vToSkyFrameMatrix = JWSTSIAFUtils.getTelescopeToSkyFrameReferenceMatrix(
                ra, dec, angle, referencePoint[0], referencePoint[1]);

        for (DS9TextDescriptor textDescriptor : ds9Descriptor.getTexts()) {
            double[] coordinates = {textDescriptor.getRa(), textDescriptor.getDec()};
            Vector<double[]> skyCoordinates = projectPixelsToSkyCoords(new Vector<>(Collections.singletonList(coordinates)), vToSkyFrameMatrix);
            skyCoordinates.firstElement()[2] = angle;
            fullFovSkyCoords.put(textDescriptor.getText(), skyCoordinates.firstElement());
        }

        return fullFovSkyCoords;
    }

    private Vector<double[]> projectPixelsToSkyCoords(Vector<double[]> pixels,
                                                      BigDecimal[][] rotationMatrix) {

        Vector<double[]> skycoords = new Vector<>();

        for (double[] pixel : pixels) {

            double[] coords = JWSTSIAFUtils.convertTelescopeToSkyCoords(pixel[0],
                    pixel[1], rotationMatrix);

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
        return sb.toString();
    }

    @Override
    public Map<String, Vector<double[]>> getDetectorsSkyCoordsForInstrument(double raDeg, double decDeg, double rotationDeg, String instrument, String detector) {

        Map<String, Vector<double[]>> detectorMap = new HashMap<String, Vector<double[]>>();

        List<String> listOfDetectors = InstrumentMapping.getInstance().getApertureListForInstrument(instrument);


        double[] referencePosV2V3 = selectReferencePosVFrame(instrument, detector);


        BigDecimal[][] vToSkyFrameMatrix = JWSTSIAFUtils.getTelescopeToSkyFrameReferenceMatrix(
                raDeg, decDeg, rotationDeg, referencePosV2V3[0], referencePosV2V3[1]);

        Vector<double[]> detectorCentersInVFrame;
        Vector<double[]> detectorCentersInSkyFrame;

        for (String d : listOfDetectors) {

            detectorCentersInVFrame = new Vector<>();
            detectorCentersInSkyFrame = new Vector<>();

            DS9Descriptor descriptor = InstrumentMapping.getInstance().getDs9Descriptors().getDescriptors().stream().filter(desc -> desc.getInstrument().equals(instrument)).findFirst().orElse(null);

            detectorCentersInVFrame.add(DS9Utils.getReferencePoint(descriptor));

            detectorCentersInSkyFrame = projectPixelsToSkyCoords(detectorCentersInVFrame,
                    vToSkyFrameMatrix);

            detectorMap.put(d, detectorCentersInSkyFrame);
        }

        return detectorMap;
    }

    @Override
    public double[] selectReferencePosVFrame(String instrument, String detector) {
        DS9Descriptor descriptor = InstrumentMapping.getInstance().getDs9Descriptors().getDescriptors().stream().filter(d -> d.getInstrument().equals(instrument)).findFirst().orElse(null);
        return DS9Utils.getReferencePoint(descriptor);
    }

    @Override
    public Map<String, Vector<double[]>> getDetectorsSkyCoordsForFoV(double raDeg, double decDeg, double rotationDeg, String instrument, String detector) {

        Map<String, Vector<double[]>> detectorMap = new HashMap<>();

        List<String> listOfDetectors = InstrumentMapping.getInstance().getApertureListForInstrument(instrument);
        double[] referencePosV2V3 = selectReferencePosVFrame(instrument, detector);

        BigDecimal[][] vToSkyFrameMatrix = JWSTSIAFUtils.getTelescopeToSkyFrameReferenceMatrix(
                raDeg, decDeg, rotationDeg, referencePosV2V3[0], referencePosV2V3[1]);

        Vector<double[]> detectorCentersInVFrame;
        Vector<double[]> detectorCentersInSkyFrame;

        for (Constants.Instrument inst : Constants.Instrument.getInstrumentsPerMission(Constants.PlanningMission.XMM)) {

            for (String d : listOfDetectors) {

                detectorCentersInVFrame = new Vector<>();
                detectorCentersInSkyFrame = new Vector<>();

                DS9Descriptor descriptor = InstrumentMapping.getInstance().getDs9Descriptors().getDescriptors().stream().filter(desc -> desc.getInstrument().equals(instrument)).findFirst().orElse(null);

                detectorCentersInVFrame.add(DS9Utils.getReferencePoint(descriptor));

                detectorCentersInSkyFrame = projectPixelsToSkyCoords(detectorCentersInVFrame,
                        vToSkyFrameMatrix);

                detectorMap.put(d, detectorCentersInSkyFrame);
            }
        }

        return detectorMap;
    }
}
