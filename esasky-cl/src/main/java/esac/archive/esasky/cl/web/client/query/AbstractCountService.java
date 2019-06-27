package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

public abstract class AbstractCountService<T extends IDescriptor> {

    public abstract String getCount(final AladinLiteWidget aladinLite, T descriptor);

    /**
     * getDynamicCountQuery().
     * @param aladinLite Input AladinLiteWidget instance
     * @param tapTable Input String
     * @return String
     */
    protected static String getDynamicCountQuery(final AladinLiteWidget aladinLite,
            final String tapTable) {

        String url = null;
        String shape = null;
        double fovDeg = aladinLite.getFovDeg();
        String adqlQuery = "";

        if (fovDeg < 90) {
            // shape = "POLYGON('ICRS', " + aladinLite.getFovCorners(2).toString() + ")";
            shape = "POLYGON('ICRS'," + aladinLite.getFovCorners(2).toString() + ")";
            adqlQuery = "SELECT esasky_general_dynamic_count_q3c_poly_singletable('" + tapTable + "', " + shape
                    + ",   '{" + aladinLite.getFovCorners(2).toString()
                    + "}') as esasky_dynamic_count from dual";
        } else {// not accurate search based on a circle
            String cooFrame = aladinLite.getCooFrame();
            Double[] ccInJ2000 = { aladinLite.getCenterLongitudeDeg(),
                    aladinLite.getCenterLatitudeDeg() };
            if (EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME.equalsIgnoreCase(cooFrame)) {
                // convert to J2000
                ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(
                        aladinLite.getCenterLongitudeDeg(), aladinLite.getCenterLatitudeDeg());
            }
            adqlQuery = "SELECT esasky_general_dynamic_count_q3c_circle_singletable("
                    // TAP table name
                    + "'" + tapTable + "', "
                    // centre RA in degrees [J2000]
                    + "'" + ccInJ2000[0] + "', "
                    // centre DEC in degrees [J2000]
                    + "'" + ccInJ2000[1] + "',"
                    // radius in degrees
                    + "'90') " + "as esasky_dynamic_count from dual";
        }

        // String adql = "SELECT esasky_dynamic_count_q3c('" + descriptor.getTapTable() + "', "
        // + shape + ",   '{" + aladinLite.getFovCorners(2).toString()
        // + "}') as esasky_dynamic_count from dual";
        Log.debug("[TAPQueryBuilder/FastCountQuery()] Fast count ADQL " + adqlQuery);
        // String adqlQuery = getDynamicCountQuery(aladinLite, descriptor.getTapTable());
        url = TAPUtils.getTAPQuery(adqlQuery, EsaSkyConstants.JSON);
        return url;
    }
}
