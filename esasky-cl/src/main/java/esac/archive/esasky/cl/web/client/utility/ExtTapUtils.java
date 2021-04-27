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
		}else if(level.getColor()!= null) {
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

}
