package esac.archive.esasky.cl.web.client.utility;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.ifcs.model.shared.ObsCoreCollection;

public class ExtTapUtils {

	public static ExtTapDescriptor createCollectionDescriptor(ExtTapDescriptor tapService, ExtTapDescriptor parent, String facilityName) {
		ExtTapDescriptor collectionDescriptor = new ExtTapDescriptor();
		collectionDescriptor.copyParentValues((ExtTapDescriptor) parent);
		collectionDescriptor.setTreeMapType(EsaSkyConstants.TREEMAP_TYPE_SUBCOLLECTION);
		
		collectionDescriptor.setGuiShortName(facilityName);
		collectionDescriptor.setGuiLongName(collectionDescriptor.getGuiLongName() + "-" + facilityName);
		collectionDescriptor.setMission(collectionDescriptor.getMission() + "-" + facilityName);
		collectionDescriptor.setDescriptorId(collectionDescriptor.getMission());
		if(tapService.getCollections().get(facilityName).containsKey(EsaSkyConstants.TABLE_NAME)) {
			collectionDescriptor.setTapTable(tapService.getCollections().get(facilityName).get(EsaSkyConstants.TABLE_NAME).get(0));
		}
		
		String whereADQL = collectionDescriptor.getWhereADQL();
		
	   RegExp regularExpression = RegExp.compile("@@@(.*?)@@@", "gm");
    	 for (MatchResult match = regularExpression.exec(collectionDescriptor.getArchiveProductURI()); match != null; match = regularExpression
                 .exec(collectionDescriptor.getArchiveProductURI())) {
             String rowColumn = match.getGroup(1); // Group 1 is the match inside @s
             if(tapService.getCollections().get(facilityName).containsKey(rowColumn)) {
            	 String valueURI = tapService.getCollections().get(facilityName).get(rowColumn).get(0);
            	 String archiveURI = collectionDescriptor.getArchiveProductURI();
            	 archiveURI = archiveURI.replace("@@@" + rowColumn + "@@@", valueURI);
            	 collectionDescriptor.setArchiveProductURI(archiveURI);
             }
         }
		
		if(tapService.getCollections().get(facilityName).containsKey(EsaSkyConstants.OBSCORE_COLLECTION)) {
			if(whereADQL != null) {
				whereADQL += " AND ";
			}else {
				whereADQL = "";
			}
			whereADQL +=  EsaSkyConstants.OBSCORE_COLLECTION + " IN (";
			for(String collectionName : tapService.getCollections().get(facilityName).get(EsaSkyConstants.OBSCORE_COLLECTION)) {
				whereADQL += "\'" + collectionName + "\', ";
			}
			//Remove last "," 
			whereADQL = whereADQL.substring(0, whereADQL.length() - 2);
			whereADQL += ")";
		}
		
		collectionDescriptor.setWhereADQL(whereADQL);
		return collectionDescriptor;
	}
	
	public static ExtTapDescriptor createDataproductDescriptor(ExtTapDescriptor parent, String typeName) {
		ExtTapDescriptor typeDescriptor = new ExtTapDescriptor();
		typeDescriptor.copyParentValues(parent);
		typeDescriptor.setTreeMapType(EsaSkyConstants.TREEMAP_TYPE_DATAPRODUCT);
		
		String name = ObsCoreCollection.get(typeName);
		typeDescriptor.setGuiShortName(name);
		typeDescriptor.setGuiLongName(typeDescriptor.getMission() + "-" + name);
		
		typeDescriptor.setMission(typeDescriptor.getMission() + "-" + name);
		typeDescriptor.setDescriptorId(typeDescriptor.getMission());
		
		String whereADQL = typeDescriptor.getWhereADQL();
		if(typeDescriptor.getIsObsCore()) {
			if(whereADQL != null) {
				whereADQL += " AND ";
			}else {
				whereADQL = "";
			}
			
			whereADQL += EsaSkyConstants.OBSCORE_DATAPRODUCT + " = \'" + typeName + "\'";
		}
		typeDescriptor.setWhereADQL(whereADQL);
		
		typeDescriptor.setSelectADQL("SELECT TOP " + Integer.toString(DeviceUtils.getDeviceShapeLimit(typeDescriptor)) + " *");
		
		return typeDescriptor;
	}
	
}
