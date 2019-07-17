package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

public class TAPCountPublicationsService extends
AbstractCountService<PublicationsDescriptor> {

    private static TAPCountPublicationsService instance = null;

    private TAPCountPublicationsService() {
    }

    public static TAPCountPublicationsService getInstance() {
        if (instance == null) {
            instance = new TAPCountPublicationsService();
        }
        return instance;
    }

    /**
     * getCount4().
     * @param pubDescriptor Input PublicationsDescriptor.
     * @param aladinLite Input instance to AladinLiteWidget.
     * @return String
     */
    @Override
    public String getCount(AladinLiteWidget aladinLite, PublicationsDescriptor descriptor) {
        return super.getDynamicCountQuery(aladinLite, descriptor.getTapTable());
    }
    
    /**
     * getCountQueryForSIMBAD().
     * @param aladinLite Input AladinLiteWidget instance
     * @return String
     */
    public String getCountQueryForSIMBAD(final AladinLiteWidget aladinLite) {

        String url = null;
        String shape = null;
        double fovDeg = aladinLite.getFovDeg();
        
        String adqlQuery = "select sum(nbref) as \"esasky_dynamic_count\" from basic"
            + " where 1=CONTAINS(POINT('ICRS'," + EsaSkyConstants.SOURCE_TAP_RA + ", "
            + EsaSkyConstants.SOURCE_TAP_DEC + "), ";

        if (AladinLiteWrapper.isCornersInsideHips()) {
            if (fovDeg < 1) {
                Log.debug("[TAPCountPublicationsService/getCountQueryForSIMBAD()] FoV < 1d");
                shape = "POLYGON('ICRS', "
                        + aladinLite.getFovCorners(1).toString() + ")";
            } else {
                shape = "POLYGON('ICRS', "
                        + aladinLite.getFovCorners(2).toString() + ")";
            }
        } else {
        
            String cooFrame = aladinLite.getCooFrame();
            if (EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME.equalsIgnoreCase(cooFrame)) {
                // convert to J2000
                Double[] ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(
                        aladinLite.getCenterLongitudeDeg(),
                        aladinLite.getCenterLatitudeDeg());
                shape = "CIRCLE('ICRS', " + ccInJ2000[0] + "," + ccInJ2000[1] + ",90)";
            } else {
                shape = "CIRCLE('ICRS', "
                        + aladinLite.getCenterLongitudeDeg() + ","
                        + aladinLite.getCenterLatitudeDeg() + ",90)";
            }
        
        }
        adqlQuery += shape + ")";
        
        Log.debug("[TAPCountPublicationsService/getCountQueryForSIMBAD()] Count ADQL " + adqlQuery);
        
        url = TAPUtils.getSIMBADTAPQuery("pub_count", adqlQuery, "{\"count\":$DATA$,\"aprox\":true}");
        return url;
    }

}
