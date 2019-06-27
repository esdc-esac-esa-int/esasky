package esac.archive.esasky.cl.web.client.model.entities;

import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.cl.web.client.status.CountStatus;

public class ESASkyAPICatalogueEntity extends CatalogEntity implements GeneralEntityInterface{

	public ESASkyAPICatalogueEntity(CatalogDescriptor catDescriptor, CountStatus countStatus,
			JavaScriptObject catalogue, SkyViewPosition skyViewPosition, String esaSkyUniqObsId,
			Long lastUpdate, EntityContext context) {
		super(catDescriptor, countStatus, catalogue, skyViewPosition, esaSkyUniqObsId, lastUpdate, context);
	}

}
