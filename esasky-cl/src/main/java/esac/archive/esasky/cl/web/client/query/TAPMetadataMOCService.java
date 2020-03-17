package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

public class TAPMetadataMOCService extends AbstractMetadataService {

    private static TAPMetadataMOCService instance = null;

    private TAPMetadataMOCService() {
    }

    public static TAPMetadataMOCService getInstance() {
        if (instance == null) {
            instance = new TAPMetadataMOCService();
        }
        return instance;
    }

    @Override
    public String getMetadataAdql(IDescriptor descriptorInput) {
    	return getMetadataAdql(descriptorInput, "");
    }
    
    /**
     * getMOC().
     * @param aladinLite Input AladinLiteWidget.
     * @param obsDescriptor Input ObservationDescriptor
     * @return Query in ADQL format.
     */
    @Override
    public String getMetadataAdql(IDescriptor inputDescriptor, String filter) {
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
}