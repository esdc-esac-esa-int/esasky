package esac.archive.esasky.cl.web.client.model.entities;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.cl.web.client.model.SelectableImage;
import esac.archive.esasky.cl.web.client.status.CountStatus;

public class ObservationEntity extends ObservationAndSpectraEntity {

    private final Resources resources = GWT.create(Resources.class);

    public interface Resources extends ClientBundle {

        @Source("galaxy_light.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabDefaultImagingIcon();

        @Source("galaxy_dark.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabSelectedImagingIcon();

    }

    public ObservationEntity(CommonObservationDescriptor obsDescriptor, CountStatus countStatus,
    		SkyViewPosition skyViewPosition, String esaSkyUniqObsId, Long lastUpdate, EntityContext context) {
        super(obsDescriptor, countStatus, skyViewPosition, esaSkyUniqObsId, lastUpdate, context);
    }
    
    @Override
    public SelectableImage getTypeIcon() {
        return new SelectableImage(resources.tabDefaultImagingIcon(), resources.tabSelectedImagingIcon());
    }

}
