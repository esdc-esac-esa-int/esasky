package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
public abstract class AbstractTAPService {

    public abstract String getMetadataAdql(IDescriptor descriptor);
    public abstract String getMetadataAdql(IDescriptor descriptor, String filter);
    public abstract String getHeaderAdql(IDescriptor descriptor);
    public abstract String getMetadataAdqlRadial(IDescriptor descriptor, SkyViewPosition conePos);
    
    public String getRetreivingDataTextKey() {
    	return "MetadataCallback_retrievingMissionMetadata";
    }

    protected int getResultsLimit(int descriptorLimit){
        
        if (DeviceUtils.isMobile()){
            return EsaSkyWebConstants.MAX_SHAPES_FOR_MOBILE;
        }
         return descriptorLimit;  
        
    }
    
    public String getRequestUrl() {
        return EsaSkyWebConstants.TAP_CONTEXT;
    }
    
    public String getMocAdql(IDescriptor inputDescriptor, String filter) {
        CommonObservationDescriptor descriptor = (CommonObservationDescriptor) inputDescriptor;
        Log.debug("[TAPQueryBuilder/getMOC()] Cooframe "
                + AladinLiteWrapper.getAladinLite().getCooFrame());
        String adql = "select " + descriptor.getMocSTCSColumn() + " from "
                + descriptor.getMocTapTable() + " where 1=INTERSECTS(fov,";

        String shape = null;

        String cooFrame = AladinLiteWrapper.getAladinLite().getCooFrame();
        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();

        if (EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME.equalsIgnoreCase(cooFrame)) {
            if (AladinLiteWrapper.isCornersInsideHips()) {
                if (fovDeg < 1) {
                    Log.debug("[TAPQueryBuilder/getMOC()] FoV < 1d");
                    shape = "POLYGON('ICRS', "
                            + CoordinatesConversion
                                    .convertPointListGalacticToJ2000(AladinLiteWrapper
                                            .getAladinLite().getFovCorners(1).toString()) + ")";
                } else {
                    shape = "POLYGON('ICRS', "
                            + CoordinatesConversion
                                    .convertPointListGalacticToJ2000(AladinLiteWrapper
                                            .getAladinLite().getFovCorners(2).toString()) + ")";
                }
            } else {
                // convert to J2000
                Double[] ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(
                        AladinLiteWrapper.getAladinLite().getCenterLongitudeDeg(),
                        AladinLiteWrapper.getAladinLite().getCenterLatitudeDeg());
                shape = "CIRCLE('ICRS', " + ccInJ2000[0] + "," + ccInJ2000[1] + ",90)";
            }
        } else {
            if (AladinLiteWrapper.isCornersInsideHips()) {
                if (fovDeg < 1) {
                    Log.debug("[TAPQueryBuilder/getMOC()] FoV < 1d");
                    shape = "POLYGON('ICRS', "
                            + AladinLiteWrapper.getAladinLite().getFovCorners(1).toString() + ")";
                } else {
                    shape = "POLYGON('ICRS', "
                            + AladinLiteWrapper.getAladinLite().getFovCorners(2).toString() + ")";
                }
            } else {
                shape = "CIRCLE('ICRS', "
                        + AladinLiteWrapper.getAladinLite().getCenterLongitudeDeg() + ","
                        + AladinLiteWrapper.getAladinLite().getCenterLatitudeDeg() + ",90)";
            }
        }

        adql += shape + ")";
        
        adql += filter;

        Log.debug("[TAPQueryBuilder/getMOC()] ADQL " + adql);
        return adql;
    }
    
    public String getCount(final AladinLiteWidget aladinLite, IDescriptor descriptor) {
        final String tapTable = descriptor.getTapTable();
        String url = null;
        String shape = null;
        String adqlQuery = "";

        if (AladinLiteWrapper.isCornersInsideHips()) {
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

        Log.debug("[TAPQueryBuilder/FastCountQuery()] Fast count ADQL " + adqlQuery);
        url = TAPUtils.getTAPQuery(URL.encodeQueryString(adqlQuery), EsaSkyConstants.JSON);
        return url;
    }
}