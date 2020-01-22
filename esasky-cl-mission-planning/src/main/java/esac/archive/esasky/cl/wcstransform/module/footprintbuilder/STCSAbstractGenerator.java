package esac.archive.esasky.cl.wcstransform.module.footprintbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author Fabrizio Giordano Copyright (c) 2016 - European Space Agency
 */

public abstract class STCSAbstractGenerator {

    protected String mission;
    protected ArrayList<String> instruments;

    public Map<String, String> doAll(String instrument, String detector, double angle, double ra,
            double dec) {

        Map<String, Vector<double[]>> fullFovSkyCoords = computeInstrumentPolygon(instrument,
                detector, angle, ra, dec);

        Map<String, String> fullFovSTCS = new HashMap<String, String>();

        for (String inst : fullFovSkyCoords.keySet()) {

            fullFovSTCS.put(inst, generateSTCS(fullFovSkyCoords.get(inst)));
        }

        return fullFovSTCS;

    }

    public abstract Map<String, Vector<double[]>> computeInstrumentPolygon(String instrument,
            String detector, double angle, double ra, double dec);

    public abstract String generateSTCS(Vector<double[]> pixels);

    public abstract Map<String, Vector<double[]>> getDetectorsSkyCoordsForInstrument(
            double raDeg, double decDeg, double rotationDeg, String instrument, String detector);

    public abstract double[] selectReferencePosVFrame(String instrument, String detector);

    public abstract Map<String, Vector<double[]>> getDetectorsSkyCoordsForFoV(double raDeg,
            double decDeg, double rotationDeg, String instrument, String detector);

}
