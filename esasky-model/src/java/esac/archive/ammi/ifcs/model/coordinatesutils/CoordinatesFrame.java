package esac.archive.ammi.ifcs.model.coordinatesutils;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;

public enum CoordinatesFrame {

    J2000(AladinLiteConstants.FRAME_J2000), GALACTIC(AladinLiteConstants.FRAME_GALACTIC);

    private String cooframe;

    CoordinatesFrame(String cooFrame) {
        this.cooframe = cooFrame;
    }

    public String getValue() {
        return cooframe;
    }

}
