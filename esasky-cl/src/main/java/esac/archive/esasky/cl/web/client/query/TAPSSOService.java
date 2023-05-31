package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.*;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

import java.util.Objects;

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

    public String getMetadataAdql(CommonTapDescriptor descriptorInput) {
    	return getMetadataAdql(descriptorInput, "");
    }

    public String getMetadataAdql(final CommonTapDescriptor descriptor, String filter) {
        String adql = "SELECT a.*, b.sso_name as sso_name_splitter, b.* FROM " + descriptor.getTableName() + " AS a JOIN "
                + descriptor.getProperties().get("xmatch_table")
                + " AS b on a.observation_oid = b.observation_oid";


        if(descriptor.getTableName().contains("xmm_om")) {
        	adql += " AND a.filter = b.filter";
        }

        adql += " WHERE b.sso_oid=" + GUISessionStatus.getTrackedSso().id;

        return adql;
    }

    public String getPolylineAdql(CommonTapDescriptor descriptor) {
        if(Objects.equals(descriptor.getCategory(), EsaSkyWebConstants.CATEGORY_SSO)) {
            String ssoCardReductionTapTable = descriptor.getProperties().get("card_reduction_table").toString();
            return "select positions from " + ssoCardReductionTapTable + " where "
                    + ssoCardReductionTapTable + ".sso_oid=" + GUISessionStatus.getTrackedSso().id;
        }

        return "";
    }

	@Override
	public String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition conePos) {
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