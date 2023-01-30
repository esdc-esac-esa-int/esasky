package esac.archive.esasky.cl.web.client.model;

import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

public class MOCInfo {
    public MOCInfo(CommonTapDescriptor descriptor, MOCEntity entity, int count, GeneralJavaScriptObject pixels) {
        this.descriptor = descriptor;
        this.entity = entity;
        this.count = count;
        this.pixels = pixels;
    }
    public final int count;
    public final GeneralJavaScriptObject pixels;
    public final CommonTapDescriptor descriptor;
    public final MOCEntity entity;
}
