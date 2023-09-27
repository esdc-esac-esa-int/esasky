package esac.archive.esasky.cl.wcstransform.module.footprintbuilder;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.wcstransform.module.utility.Constants.PlanningMission;

/**
 * @author Fabrizio Giordano Copyright (c) 2016 - European Space Agency
 */

public class STCSGeneratorFactory {

    public static STCSAbstractGenerator getSTCSGenerator(String mission) {
        Log.debug("FACTORY " + mission);
        if (PlanningMission.JWST.getMissionName().equals(mission)) {
            Log.debug("FACTORY " + PlanningMission.JWST.getMissionName());
            return new JWSTSiafToSTCSGenerator(mission);
        }
        return null;
    }
}
