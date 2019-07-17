package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;

public class TAPMetadataCatalogueService extends AbstractMetadataService {

    private static TAPMetadataCatalogueService instance = null;

    private TAPMetadataCatalogueService() {
    }

    public static TAPMetadataCatalogueService getInstance() {
        if (instance == null) {
            instance = new TAPMetadataCatalogueService();
        }
        return instance;
    }

    /**
     * getMetadata4Sources().
     * @param aladinLite Input AladinLiteWidget.
     * @param catalogue Input CatalogDescriptor
     * @return Query in ADQL format.
     */
    @Override
    public String getMetadataAdql(IDescriptor descriptorInput) {
        CatalogDescriptor descriptor = (CatalogDescriptor) descriptorInput;

        int top = getResultsLimit(descriptor.getSourceLimit());

        String adql = "select top " + top + " ";

        for (MetadataDescriptor currentMetadata : descriptor.getMetadata()) {
            if (descriptor.getPolygonDecTapColumn().equals(currentMetadata.getTapName())) {
                adql += " " + currentMetadata.getTapName() + " as "
                        + descriptor.getPolygonDecTapColumn() + ", ";
            } else if (descriptor.getPolygonRaTapColumn().equals(currentMetadata.getTapName())) {
                adql += " " + currentMetadata.getTapName() + " as "
                        + descriptor.getPolygonRaTapColumn() + ", ";
            } else if (descriptor.getPolygonNameTapColumn().equals(currentMetadata.getTapName())) {
                adql += " " + currentMetadata.getTapName() + " as "
                        + EsaSkyConstants.SOURCE_TAP_NAME + ", ";
            } else {
                adql += " " + currentMetadata.getTapName() + ", ";
            }
        }

        String parsedAdql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
        parsedAdql.replace("\\s*,\\s*$", "");
        parsedAdql += " from " + descriptor.getTapTable() + " where 1=CONTAINS(POINT('ICRS',"
                + EsaSkyConstants.SOURCE_TAP_RA + ", " + EsaSkyConstants.SOURCE_TAP_DEC + "), ";
        // + catalogue.getPosTapColumn() + ",";

        String shape = null;
        double fovDeg = AladinLiteWrapper.getAladinLite().getFovDeg();
        if (AladinLiteWrapper.isCornersInsideHips()) {
            if (fovDeg < 1) {
                Log.debug("[TAPQueryBuilder/getMetadata4Sources()] FoV < 1d");
                shape = "POLYGON('ICRS', "
                        + AladinLiteWrapper.getAladinLite().getFovCorners(1).toString() + ")";
            } else {
                shape = "POLYGON('ICRS', "
                        + AladinLiteWrapper.getAladinLite().getFovCorners(2).toString() + ")";
            }
        } else {

            String cooFrame = AladinLiteWrapper.getAladinLite().getCooFrame();
            if (EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME.equalsIgnoreCase(cooFrame)) {
                // convert to J2000
                Double[] ccInJ2000 = CoordinatesConversion.convertPointGalacticToJ2000(
                        AladinLiteWrapper.getAladinLite().getCenterLongitudeDeg(),
                        AladinLiteWrapper.getAladinLite().getCenterLatitudeDeg());
                shape = "CIRCLE('ICRS', " + ccInJ2000[0] + "," + ccInJ2000[1] + ",90)";
            } else {
                shape = "CIRCLE('ICRS', "
                        + AladinLiteWrapper.getAladinLite().getCenterLongitudeDeg() + ","
                        + AladinLiteWrapper.getAladinLite().getCenterLatitudeDeg() + ",90)";
            }

        }
        parsedAdql += shape + ")";

        if (null != descriptor.getOrderBy() && !"".equals(descriptor.getOrderBy().trim())) {
            parsedAdql += " ORDER BY " + descriptor.getOrderBy();
        }

        Log.debug("[TAPQueryBuilder/getMetadata4Sources()] ADQL " + parsedAdql);

        return parsedAdql;
    }
    
    @Override
    public String getRetreivingDataTextKey() {
    	return "MetadataCallback_retrievingMissionData";
    }

}