package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.ammi.ifcs.model.descriptor.IDescriptor;
import esac.archive.ammi.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.ammi.ifcs.model.descriptor.SSODescriptor;
import esac.archive.esasky.cl.web.client.model.entities.SSOEntity;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;

public class TAPMetadataSSOService extends AbstractMetadataService {

    private static TAPMetadataSSOService instance = null;

    private TAPMetadataSSOService() {
    }

    public static TAPMetadataSSOService getInstance() {
        if (instance == null) {
            instance = new TAPMetadataSSOService();
        }
        return instance;
    }

    public String getMetadataAdql(final IDescriptor inputDescriptor) {
        final String debugPrefix = "[TAPMetadataSSOService.getMetadataAdql]";
        SSODescriptor descriptor = (SSODescriptor) inputDescriptor;

        String adql = "SELECT ";
        for (MetadataDescriptor currMetadata : descriptor.getMetadata()) {
        	boolean found = false;
        	for(MetadataDescriptor ssoMetadata : descriptor.getSsoXMatchMetadata()) {
        		if(ssoMetadata.getTapName().equals(currMetadata.getTapName())) {
        			adql += " b." + currMetadata.getTapName() + ", ";
        			found = true;
        			break;
        		}
        	}
        	if(!found) {
        		adql += " a." + currMetadata.getTapName() + ", ";
        	}
        }

        String parsedAdql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
        parsedAdql.replace("\\s*,\\s*$", "");
        parsedAdql += " FROM " + descriptor.getTapTable() + " AS a JOIN "
                + descriptor.getSsoXMatchTapTable()
                + " AS b on a.observation_oid = b.observation_oid WHERE b.sso_oid=" + GUISessionStatus.getTrackedSso().id;

        Log.debug(debugPrefix + " ADQL " + parsedAdql);

        return parsedAdql;
    }

    public String getSSOPolylineAdql(SSOEntity entity) {
        SSODescriptor descriptor = entity.getDescriptor();
        String ssoCardReductionTapTable = descriptor.getSsoCardReductionTapTable();

        String adql = "select positions from " + ssoCardReductionTapTable + " where "
                + ssoCardReductionTapTable + ".sso_oid=" + GUISessionStatus.getTrackedSso().id;

        return adql;
    }

}