package esac.archive.esasky.ifcs.model.descriptor;


import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.Objects;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ImageDescriptor extends ObservationDescriptor {
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isHst() {
        return Objects.equals(this.getMission(), EsaSkyConstants.HST_MISSION);
    }
}
