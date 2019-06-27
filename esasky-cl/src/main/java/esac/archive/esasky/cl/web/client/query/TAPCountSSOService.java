package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;

public class TAPCountSSOService {

    private static TAPCountSSOService instance = null;

    private TAPCountSSOService() {
    }

    public static TAPCountSSOService getInstance() {
        if (instance == null) {
            instance = new TAPCountSSOService();
        }
        return instance;
    }

    /**
     * getCount4CatalogueURL().
     * @param descriptor Input CatalogDescriptor.
     * @param aladinLite Input instance to AladinLiteWidget.
     * @return String
     */

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
