package esac.archive.esasky.cl.web.client.query;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;

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

    /**
     * getMetadata4Sources().
     * @param aladinLite Input AladinLiteWidget.
     * @param catalogue Input CatalogDescriptor
     * @return Query in ADQL format.
     */
    @Override
    public String getMetadataAdql(IDescriptor descriptorInput) {
    	return getMetadataAdql(descriptorInput, "");
    }
    
    @Override
	public String getMetadataAdql(IDescriptor descriptorInput, String filters) {
        CatalogDescriptor descriptor = (CatalogDescriptor) descriptorInput;
        String adql = "";
        if(Modules.toggleColumns) {
            adql = "select top " + getResultsLimit(descriptor.getShapeLimit()) + " * ";
        } else {
            adql = "select top " + getResultsLimit(descriptor.getShapeLimit()) + " ";
            for (MetadataDescriptor currentMetadata : descriptor.getMetadata()) {
                if (descriptor.getTapDecColumn().equals(currentMetadata.getTapName())) {
                    adql += " " + currentMetadata.getTapName() + " as "
                            + descriptor.getTapDecColumn() + ", ";
                } else if (descriptor.getTapRaColumn().equals(currentMetadata.getTapName())) {
                    adql += " " + currentMetadata.getTapName() + " as "
                            + descriptor.getTapRaColumn() + ", ";
                } else if (descriptor.getUniqueIdentifierField().equals(currentMetadata.getTapName())) {
                    adql += " " + currentMetadata.getTapName() + " as "
                            + currentMetadata.getTapName() + ", ";
                } else {
                    adql += " " + currentMetadata.getTapName();
                    adql += ", ";
                }
            }
                
            adql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
            
        }

        adql.replace("\\s*,\\s*$", "");
        adql += " from " + descriptor.getTapTable() + " where " + getGeometricConstraint(descriptor);
        
        if(filters != "") {
        	adql += " AND " + filters;
        }
        
        adql += getOrderBy(descriptor);

        Log.debug("[TAPQueryBuilder/getMetadata4Sources()] ADQL " + adql);

        return adql;
    }

    public String getMetadataAdqlFromIpix(IDescriptor descriptorInput, int order, int ipix) {
    	CatalogDescriptor descriptor = (CatalogDescriptor) descriptorInput;
        String adql = "";
        if(Modules.toggleColumns) {
            adql = "select *";
        } else {
            adql = "select  ";
            for (MetadataDescriptor currentMetadata : descriptor.getMetadata()) {
                if (descriptor.getTapDecColumn().equals(currentMetadata.getTapName())) {
                    adql += " " + currentMetadata.getTapName() + " as "
                            + descriptor.getTapDecColumn() + ", ";
                } else if (descriptor.getTapRaColumn().equals(currentMetadata.getTapName())) {
                    adql += " " + currentMetadata.getTapName() + " as "
                            + descriptor.getTapRaColumn() + ", ";
                } else if (descriptor.getUniqueIdentifierField().equals(currentMetadata.getTapName())) {
                    adql += " " + currentMetadata.getTapName() + " as "
                            + currentMetadata.getTapName() + ", ";
                } else {
                    adql += " " + currentMetadata.getTapName() + ", ";
                }
            }
    
            adql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
        }

        adql.replace("\\s*,\\s*$", "");
        adql += " from " + descriptor.getTapTable() + " WHERE "
    	+ "esasky_q3c_bitshift_left(" + Integer.toString(ipix) + "," + Integer.toString(60 -  2 * order) + " ) <= q3c_ang2ipix(ra,dec)"
    			+ " AND esasky_q3c_bitshift_left(" + Integer.toString(ipix + 1) + "," + Integer.toString(60 -  2 * order) + " ) > q3c_ang2ipix(ra,dec)";

        adql += getOrderBy(descriptor);
        
        return adql;
    }
    
    public String getMetadataAdqlRadial(IDescriptor descriptorInput, SkyViewPosition pos) {
    	return getMetadataAdqlRadial(descriptorInput, pos, "");
    }
    
    public String getMetadataAdqlRadial(IDescriptor descriptorInput, SkyViewPosition pos, String filters) {
    	CatalogDescriptor descriptor = (CatalogDescriptor) descriptorInput;
    	
        int top = getResultsLimit(descriptor.getShapeLimit());

        String adql;
        if (Modules.toggleColumns) {
            adql = "select top " + top + " *";
            
        } else {
            adql = "select top " + getResultsLimit(descriptor.getShapeLimit()) + " ";

            for (MetadataDescriptor currentMetadata : descriptor.getMetadata()) {
                if (descriptor.getTapDecColumn().equals(currentMetadata.getTapName())) {
                    adql += " " + currentMetadata.getTapName() + " as "
                            + descriptor.getTapDecColumn() + ", ";
                } else if (descriptor.getTapRaColumn().equals(currentMetadata.getTapName())) {
                    adql += " " + currentMetadata.getTapName() + " as "
                            + descriptor.getTapRaColumn() + ", ";
                } else if (descriptor.getUniqueIdentifierField().equals(currentMetadata.getTapName())) {
                    adql += " " + currentMetadata.getTapName() + " as "
                            + currentMetadata.getTapName() + ", ";
                } else {
                    adql += " " + currentMetadata.getTapName();
                    adql += ", ";
                }
            }

            adql = adql.substring(0, adql.indexOf(",", adql.length() - 2));
        }

        adql.replace("\\s*,\\s*$", "");
        adql += " from " + descriptor.getTapTable() + " WHERE "
    	+ "'1' = q3c_radial_query(" +  descriptor.getTapRaColumn() + ", "  + descriptor.getTapDecColumn() + ", "
				+ Double.toString(pos.getCoordinate().ra) + ", "  +  Double.toString(pos.getCoordinate().dec) + ", "
				+ Double.toString(pos.getFov()/2) +")";
        
        adql += filters;
        
        adql += getOrderBy(descriptor);
        
        return adql;
    }

}