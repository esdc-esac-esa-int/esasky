package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
public abstract class AbstractMetadataService {

    public abstract String getMetadataAdql(IDescriptor descriptor);
    public abstract String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos);
    
    public String getRetreivingDataTextKey() {
    	return "MetadataCallback_retrievingMissionMetadata";
    }

    protected int getResultsLimit(int descriptorLimit){
        
        if (DeviceUtils.isMobile()){
            return EsaSkyWebConstants.MAX_SOURCES_FOR_MOBILE;
        }
         return descriptorLimit;  
        
    }
}