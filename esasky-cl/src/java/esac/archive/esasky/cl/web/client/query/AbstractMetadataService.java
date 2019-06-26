package esac.archive.esasky.cl.web.client.query;

import esac.archive.ammi.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
public abstract class AbstractMetadataService {

    public abstract String getMetadataAdql(IDescriptor descriptor);
    
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