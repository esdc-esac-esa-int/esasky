package esac.archive.esasky.cl.web.client.model.entities;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.cl.web.client.status.CountStatus;

public class ObservationEntity extends ObservationAndSpectraEntity {

    public ObservationEntity(CommonObservationDescriptor obsDescriptor, CountStatus countStatus,
    		SkyViewPosition skyViewPosition, String esaSkyUniqObsId) {
        super(obsDescriptor, countStatus, skyViewPosition, esaSkyUniqObsId);
    }
}
