package esac.archive.esasky.cl.web.client.model;

import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class MOCInfo {
    public MOCInfo(IDescriptor descriptor,MOCEntity entity, int count, int order, int ipix) {
        this.descriptor = descriptor;
        this.entity = entity;
        this.count = count;
        this.order = order;
        this.ipix = ipix;
    }
    public final int count;
    public final int order;
    public final int ipix;
    public final IDescriptor descriptor;
    public final MOCEntity entity;
}
