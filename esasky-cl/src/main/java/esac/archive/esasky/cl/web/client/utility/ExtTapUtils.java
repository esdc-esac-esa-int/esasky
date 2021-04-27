package esac.archive.esasky.cl.web.client.utility;


import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapTreeMapLevel;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class ExtTapUtils {
	
	public static ExtTapDescriptor createLevelDescriptor(ExtTapDescriptor parent, int levelNumber, String levelName, String columnName, ExtTapTreeMapLevel level) {
		ExtTapDescriptor descriptor = new ExtTapDescriptor();
		descriptor.copyParentValues(parent);
		descriptor.setTreeMapLevel(levelNumber);
		descriptor.setGuiShortName(levelName);
		descriptor.setGuiLongName(descriptor.getGuiLongName() + "-" + levelName);
		descriptor.setMission(descriptor.getMission()+ "-" + levelName);
		descriptor.setDescriptorId(descriptor.getMission());
		descriptor.setSubLevels( parent.getSubLevels().get(levelName).getSubLevels());
		
		
		if(level.getWavelengthRange() != null && level.getWavelengthRange().size() == 2) {
			double minWavelength = Double.parseDouble(level.getWavelengthRange().get(0));
			double maxWavelength = Double.parseDouble(level.getWavelengthRange().get(1));
			
			descriptor.setWavelengths(WavelengthUtils.createWavelengthDescriptor(minWavelength, maxWavelength));
			descriptor.setPrimaryColor(ESASkyColors.getColorFromWavelength((maxWavelength + minWavelength) / 2));
		}else if(level.getColor()!= null && !"".contentEquals(level.getColor())) {
			descriptor.setPrimaryColor(level.getColor());
		}
		else {
			descriptor.setPrimaryColor(ESASkyColors.getNext());
		}
		
		if(EsaSkyConstants.TABLE_NAME.contentEquals(columnName)) {
			if(level.getValues().size() == 1) {
				descriptor.setTapTable(level.getValues().get(0));
				String newProductURI = descriptor.getArchiveProductURI().replace("@@@" + EsaSkyConstants.TABLE_NAME+ "@@@", level.getValues().get(0));
				descriptor.setArchiveProductURI(newProductURI);
			}
			
		}
		
		String whereADQL = descriptor.getWhereADQL();
		
		if(columnName != null && !EsaSkyConstants.TABLE_NAME.contentEquals(columnName)) {
			if(whereADQL != null) {
				whereADQL += " AND ";
			}else {
				whereADQL = "";
			}
			whereADQL +=  columnName + " IN (";
			for(String value : level.getValues()) {
				whereADQL += "\'" + value + "\', ";
			}
			//Remove last "," 
			whereADQL = whereADQL.substring(0, whereADQL.length() - 2);
			whereADQL += ")";
		}
		
		descriptor.setWhereADQL(whereADQL);
		
		return descriptor;
	}

	

//	public static ExtTapDescriptor createLevel1Descriptor(ExtTapDescriptor tapService, ExtTapDescriptor parent, String facilityName) {
//		ExtTapDescriptor collectionDescriptor = new ExtTapDescriptor();
//		collectionDescriptor.copyParentValues((ExtTapDescriptor) parent);
//		collectionDescriptor.setTreeMapLevel(EsaSkyConstants.TREEMAP_LEVEL_1);
//		
//		collectionDescriptor.setGuiShortName(facilityName);
//		collectionDescriptor.setGuiLongName(collectionDescriptor.getGuiLongName() + "-" + facilityName);
//		collectionDescriptor.setMission(collectionDescriptor.getMission() + "-" + facilityName);
//		collectionDescriptor.setDescriptorId(collectionDescriptor.getMission());
//		if(tapService.getCollections().get(facilityName).containsKey(EsaSkyConstants.TABLE_NAME)) {
//			collectionDescriptor.setTapTable(tapService.getCollections().get(facilityName).get(EsaSkyConstants.TABLE_NAME).get(0));
//		}
//		
//		
//	   RegExp regularExpression = RegExp.compile("@@@(.*?)@@@", "gm");
//    	 for (MatchResult match = regularExpression.exec(collectionDescriptor.getArchiveProductURI()); match != null; match = regularExpression
//                 .exec(collectionDescriptor.getArchiveProductURI())) {
//             String rowColumn = match.getGroup(1); // Group 1 is the match inside @s
//             if(tapService.getCollections().get(facilityName).containsKey(rowColumn)) {
//            	 String valueURI = tapService.getCollections().get(facilityName).get(rowColumn).get(0);
//            	 String archiveURI = collectionDescriptor.getArchiveProductURI();
//            	 archiveURI = archiveURI.replace("@@@" + rowColumn + "@@@", valueURI);
//            	 collectionDescriptor.setArchiveProductURI(archiveURI);
//             }
//         }
//    	 
//    	String whereADQL = collectionDescriptor.getWhereADQL();
//		
//		if(tapService.getCollections().get(facilityName).containsKey(EsaSkyConstants.OBSCORE_COLLECTION)) {
//			if(whereADQL != null) {
//				whereADQL += " AND ";
//			}else {
//				whereADQL = "";
//			}
//			whereADQL +=  EsaSkyConstants.OBSCORE_COLLECTION + " IN (";
//			for(String collectionName : tapService.getCollections().get(facilityName).get(EsaSkyConstants.OBSCORE_COLLECTION)) {
//				whereADQL += "\'" + collectionName + "\', ";
//			}
//			//Remove last "," 
//			whereADQL = whereADQL.substring(0, whereADQL.length() - 2);
//			whereADQL += ")";
//		}
//		
//		collectionDescriptor.setWhereADQL(whereADQL);
//		return collectionDescriptor;
//	}
//	
//	public static ExtTapDescriptor createDataproductDescriptor(ExtTapDescriptor parent, String typeName) {
//		ExtTapDescriptor typeDescriptor = new ExtTapDescriptor();
//		typeDescriptor.copyParentValues(parent);
//		typeDescriptor.setTreeMapLevel(EsaSkyConstants.TREEMAP_LEVEL_2);
//
//		
//		String name = ObsCoreCollection.get(typeName);
//		typeDescriptor.setGuiShortName(name);
//		typeDescriptor.setGuiLongName(typeDescriptor.getMission() + "-" + name);
//		
//		typeDescriptor.setMission(typeDescriptor.getMission() + "-" + name);
//		typeDescriptor.setDescriptorId(typeDescriptor.getMission());
//		
//		String whereADQL = typeDescriptor.getWhereADQL();
//		if(typeDescriptor.getIsObsCore()) {
//			if(whereADQL != null) {
//				whereADQL += " AND ";
//			}else {
//				whereADQL = "";
//			}
//			
//			whereADQL += EsaSkyConstants.OBSCORE_DATAPRODUCT + " = \'" + typeName + "\'";
//		}
//		typeDescriptor.setWhereADQL(whereADQL);
//		
//		typeDescriptor.setSelectADQL("SELECT TOP " + Integer.toString(DeviceUtils.getDeviceShapeLimit(typeDescriptor)) + " *");
//		
//		return typeDescriptor;
//	}
	
}
