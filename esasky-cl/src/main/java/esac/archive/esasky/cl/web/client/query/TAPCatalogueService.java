package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.TapMetadataDescriptor;

public class TAPCatalogueService extends AbstractTAPService {
    private static TAPCatalogueService instance = null;

    private TAPCatalogueService() {
    }

    public static TAPCatalogueService getInstance() {
        if (instance == null) {
            instance = new TAPCatalogueService();
        }
        return instance;
    }


    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptorInput) {
        return getMetadataAdql(descriptorInput, "");
    }

    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptor, String filter) {
        final String debugPrefix = "[TAPCatalogueService.getMetadata]";

        Log.debug(debugPrefix);
        StringBuilder adql;

        if (Modules.getModule(EsaSkyWebConstants.MODULE_TOGGLE_COLUMNS)) {
            adql = new StringBuilder("SELECT TOP " + DeviceUtils.getDeviceShapeLimit(descriptor) + " *");
        } else {
            adql = new StringBuilder("SELECT TOP " + DeviceUtils.getDeviceShapeLimit(descriptor) + " ");
            for (TapMetadataDescriptor currMetadata : descriptor.getMetadata()) {;
                adql.append(" ").append(currMetadata.getName()).append(", ");
            }

            adql = new StringBuilder(adql.substring(0, adql.indexOf(",", adql.length() - 2)));
        }
        adql.append(" FROM ").append(descriptor.getTableName()).append(" WHERE ");

        adql.append(getGeometricConstraint(descriptor));

        if (!"".equals(filter)) {
            adql.append(" AND ").append(filter);
        }

        Log.debug(debugPrefix + " ADQL " + adql);
        return adql.toString();
    }

    @Override
    public String getMetadataAdqlRadial(CommonTapDescriptor descriptorInput, SkyViewPosition pos) {
        return getMetadataAdqlRadial(descriptorInput, pos, "");
    }

    public String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition pos, String filters) {
        int top = DeviceUtils.getDeviceShapeLimit(descriptor);

        StringBuilder adql;
        if (Modules.getModule(EsaSkyWebConstants.MODULE_TOGGLE_COLUMNS)) {
            adql = new StringBuilder("select top " + top + " *");

        } else {
            adql = new StringBuilder("select top " + top + " ");

            for (TapMetadataDescriptor currMetadata : descriptor.getMetadata()) {
                if (descriptor.getDecColumn().equals(currMetadata.getName())) {
                    adql.append(" ").append(currMetadata.getName()).append(" as ").append(descriptor.getDecColumn()).append(", ");
                } else if (descriptor.getRaColumn().equals(currMetadata.getName())) {
                    adql.append(" ").append(currMetadata.getName()).append(" as ").append(descriptor.getRaColumn()).append(", ");
                } else if (descriptor.getIdColumn().equals(currMetadata.getName())) {
                    adql.append(" ").append(currMetadata.getName()).append(" as ").append(currMetadata.getName()).append(", ");
                } else {
                    adql.append(" ").append(currMetadata.getName());
                    adql.append(", ");
                }
            }

            adql = new StringBuilder(adql.substring(0, adql.indexOf(",", adql.length() - 2)));
        }

        adql.append(" from ").append(descriptor.getTableName())
                .append(" WHERE ")
                .append("'1' = q3c_radial_query(")
                .append(descriptor.getRaColumn())
                .append(", ")
                .append(descriptor.getDecColumn())
                .append(", ")
                .append(pos.getCoordinate().getRa())
                .append(", ")
                .append(pos.getCoordinate().getDec())
                .append(", ")
                .append(pos.getFov() / 2)
                .append(")");

        adql.append(filters);

        if (descriptor.getOrderByADQL() != null) {
            adql.append(descriptor.getOrderByADQL());
        }


        return adql.toString();
    }

}