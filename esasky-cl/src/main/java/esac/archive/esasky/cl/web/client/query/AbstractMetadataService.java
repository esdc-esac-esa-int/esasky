package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
public abstract class AbstractMetadataService {

    public abstract String getMetadataAdql(IDescriptor descriptor);
    public abstract String getMetadataAdql(IDescriptor descriptor, String filter);
    public abstract String getHeaderAdql(IDescriptor descriptor);
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
}