package esac.archive.esasky.cl.web.client.model;

import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class MOCInfo {
    public MOCInfo(IDescriptor descriptor,MOCEntity entity, int count, GeneralJavaScriptObject pixels) {
        this.descriptor = descriptor;
        this.entity = entity;
        this.count = count;
        this.pixels = pixels;
    }
    public final int count;
    public final GeneralJavaScriptObject pixels;
    public final IDescriptor descriptor;
    public final MOCEntity entity;
}
