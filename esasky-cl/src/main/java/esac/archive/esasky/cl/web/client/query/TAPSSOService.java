package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SSODescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;

public class TAPSSOService extends AbstractTAPService {

    private static TAPSSOService instance = null;

    private TAPSSOService() {
    }

    public static TAPSSOService getInstance() {
        if (instance == null) {
            instance = new TAPSSOService();
        }
        return instance;
    }

    @Override
    public String getMetadataAdql(IDescriptor descriptorInput) {
    	return getMetadataAdql(descriptorInput, "");
    }
    
    public String getMetadataAdql(final IDescriptor inputDescriptor, String filter) {
    	//TODO
    	//Include filters if needed
        final String debugPrefix = "[TAPSSOService.getMetadataAdql]";
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

    public String getPolylineAdql(IDescriptor descriptor) {
        if(descriptor instanceof SSODescriptor) {
            String ssoCardReductionTapTable = ((SSODescriptor)descriptor).getSsoCardReductionTapTable();
            
            return "select positions from " + ssoCardReductionTapTable + " where "
                    + ssoCardReductionTapTable + ".sso_oid=" + GUISessionStatus.getTrackedSso().id;
        }

        return "";
    }

	@Override
	public String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeaderAdql(IDescriptor descriptor) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public String getCount(String ssoName, ESASkySSOObjType ssoType) {

        String dbSSOType = "aster";
        // TODO this must be changed! SSODnet and the ephemeris script uses different identifiers

        String adql = "select b.* from sso.ssoid as a join sso.count_table as b on a.sso_oid = b.sso_oid where a.sso_name = '"
                + ssoName + "' and a.sso_type = '" + dbSSOType + "'";

        if (ssoType == ESASkySSOObjType.ASTEROID) {
            dbSSOType = "aster";
            adql = "select b.* from sso.ssoid as a join sso.count_table as b on a.sso_oid = b.sso_oid where a.sso_name = '"
                    + ssoName + "' and a.sso_type = '" + dbSSOType + "'";
        } else if (ssoType == ESASkySSOObjType.COMET) {
            dbSSOType = "comet";
            adql = "select b.* from sso.ssoid as a join sso.count_table as b on a.sso_oid = b.sso_oid where a.sso_id = '"
                    + ssoName + "' and a.sso_type = '" + dbSSOType + "'";
        } else if (ssoType == ESASkySSOObjType.PLANET) {
            dbSSOType = "planet";
            adql = "select b.* from sso.ssoid as a join sso.count_table as b on a.sso_oid = b.sso_oid where a.sso_name = '"
                    + ssoName + "' and a.sso_type = '" + dbSSOType + "'";
        } else if (ssoType == ESASkySSOObjType.SATELLITE) {
            dbSSOType = "satellite";
            adql = "select b.* from sso.ssoid as a join sso.count_table as b on a.sso_oid = b.sso_oid where a.sso_name = '"
                    + ssoName + "' and a.sso_type = '" + dbSSOType + "'";
        } else if (ssoType == ESASkySSOObjType.SPACECRAFT) {
            dbSSOType = "spacecraft";
            adql = "select b.* from sso.ssoid as a join sso.count_table as b on a.sso_oid = b.sso_oid where a.sso_id = '"
                    + ssoName + "' and a.sso_type = '" + dbSSOType + "'";
        } else if (ssoType == ESASkySSOObjType.DWARF_PLANET) {
            dbSSOType = "aster";
            adql = "select b.* from sso.ssoid as a join sso.count_table as b on a.sso_oid = b.sso_oid where a.sso_name = '"
                    + ssoName + "' and a.sso_type = '" + dbSSOType + "'";
        } else if (ssoType == ESASkySSOObjType.SSO) {
            dbSSOType = "sso";
            adql = "select b.* from sso.ssoid as a join sso.count_table as b on a.sso_oid = b.sso_oid where a.sso_id = '"
                    + ssoName + "' and a.sso_type = '" + dbSSOType + "'";
        }

        Log.debug("SSO COUNT ADQL " + adql);
        return adql;
    }

}