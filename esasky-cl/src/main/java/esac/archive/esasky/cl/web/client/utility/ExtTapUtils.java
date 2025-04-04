/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.utility;


import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.TapMetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExtTapUtils {

    private ExtTapUtils() {}


    public static CommonTapDescriptor createLevelDescriptor(CommonTapDescriptor parent, String name, String tableName, String whereADQL, double wavelengthStart, double wavelengthEnd) {
        CommonTapDescriptor descriptor = new CommonTapDescriptor();
        descriptor.setGroupColumn1(parent.getGroupColumn1());
        descriptor.setGroupColumn2(parent.getGroupColumn2());
        descriptor.setMission(parent.getMission());
        descriptor.setLongName(name);
        descriptor.setShortName(name);
        descriptor.setCategory(parent.getCategory());
        descriptor.setFovLimit(parent.getFovLimit());
        descriptor.setTableName(tableName);
        descriptor.setWhereADQL(whereADQL);
        descriptor.setSelectADQL("SELECT *");
        descriptor.setFovLimitDisabled(parent.isFovLimitDisabled());
        descriptor.setShapeLimit(parent.getShapeLimit());
        descriptor.setTapUrl(parent.getTapUrl());
        descriptor.setIsExternal(parent.isExternal());
        descriptor.setMetadata(parent.getMetadata());
        descriptor.setUseIntersectsPolygon(parent.useIntersectsPolygon());
        descriptor.setDescription(parent.getDescription());
        descriptor.setBulkDownloadUrl(parent.getBulkDownloadUrl());
        descriptor.setBulkDownloadIdColumn(parent.getBulkDownloadIdColumn());
        descriptor.setCustom(true);
        descriptor.setArchiveBaseURL(parent.getArchiveBaseURL());
        descriptor.setArchiveProductURI(parent.getArchiveProductURI());
        descriptor.setSampBaseURL(parent.getSampBaseURL());
        descriptor.setSampProductURI(parent.getSampProductURI());
        descriptor.setSampEnabled(parent.isSampEnabled());

        if (parent.getLevel() == EsaSkyConstants.TREEMAP_LEVEL_SERVICE && !parent.isCustom()) {
            parent.setColor(parent.getColor());
        }

        parent.addChild(descriptor);

        updateWavelength(descriptor, wavelengthStart, wavelengthEnd);
        updateWavelength(parent, wavelengthStart, wavelengthEnd);

        if(EsaSkyConstants.TABLE_NAME.contentEquals(parent.getGroupColumn2()) && descriptor.getLevel() > 0) {
            String newProductURI = descriptor.getArchiveProductURI().replace("@@@" + EsaSkyConstants.TABLE_NAME+ "@@@", descriptor.getTableName());
            descriptor.setArchiveProductURI(newProductURI);
        }

        return descriptor;
    }

    public static void updateLevelDescriptor(CommonTapDescriptor descriptor, String whereADQL, double wavelengthStart, double wavelengthEnd) {
        descriptor.setWhereADQL(whereADQL);
        descriptor.setWavelengthStart(wavelengthStart);
        descriptor.setWavelengthEnd(wavelengthEnd);
        updateWavelength(descriptor.getParent(), wavelengthStart, wavelengthEnd);
    }

    private static void updateWavelength(CommonTapDescriptor descriptor, Double start, Double end) {
        if (descriptor == null || start == null || start.isNaN() || end == null || end.isNaN()) {
            return;
        }

        if(descriptor.getWavelengthStart() == null || descriptor.getWavelengthEnd() == null) {
        	descriptor.setWavelengthStart(start);
        	descriptor.setWavelengthEnd(end);
        	return;
        }

        if (start > 0 && end > 0) {
            descriptor.setWavelengthStart(descriptor.getWavelengthStart() > 0 ? Math.min(descriptor.getWavelengthStart(), start) : start);
            descriptor.setWavelengthEnd(Math.max(descriptor.getWavelengthEnd(), end));
        } else if (descriptor.getWavelengthStart() <= 0 && descriptor.getWavelengthEnd() <= 0) {
            CommonTapDescriptor parent = descriptor.getParent();
            if (parent != null) {
                descriptor.setWavelengthStart(parent.getWavelengthStart());
                descriptor.setWavelengthEnd(parent.getWavelengthEnd());
            }
        }

    }

    public static void setCount(CommonTapDescriptor parent, CommonTapDescriptor child, int childCount) {
        child.setCount(childCount);
        parent.setCount(parent.getCount() + childCount);
    }

    public static String createLevelDescriptorWhereADQL(String level1Column, String level1ColumnValue, String level2Column, String level2ColumnValue) {
        StringBuilder result = new StringBuilder(level1Column+ "=" + "'" + level1ColumnValue);

        if (level2ColumnValue != null && !level2ColumnValue.equals("")) {
            result.append("' AND ").append(level2Column).append("=").append("'").append(level2ColumnValue).append("'");
        } else {
            result.append("' AND ").append(level2Column).append(" is null");
        }

        return result.toString();
    }

	public static native GeneralJavaScriptObject formatExternalTapMetadata(GeneralJavaScriptObject metadata)/*-{
		var dataResult = []
		for  (var j = 0; j < metadata.length; j++) {
			var dataItemResult = metadata[j];
            if (!dataItemResult.hasOwnProperty("displayName"))
				var displayName = $wnd.esasky.getDefaultLanguageText(metadata[j].name);
				displayName = $wnd.esasky.getColumnDisplayText(displayName);
				dataItemResult["displayName"] = displayName;
			dataItemResult["visible"] = true;
			dataResult.push(dataItemResult);
		}

		return dataResult;
	}-*/;
	public static native GeneralJavaScriptObject formatExternalTapData(GeneralJavaScriptObject data, GeneralJavaScriptObject metadata)/*-{
		var dataResult = []
		for (var i = 0; i < data.length; i++){
			var dataItem = data[i];
			var dataItemResult = {}
			var counter = 0;
			for  (var j = 0; j < metadata.length; j++) {
				dataItemResult[metadata[j].name] = dataItem[j]
				counter++;
			}
			dataResult.push(dataItemResult)
		}
		return dataResult;
	}-*/;


    public static List<TapMetadataDescriptor> getMetadataFromTapDescriptorList(TapDescriptorList descriptorList, boolean isSchemaQuery) {
        // If fetching from tap_schema the data result is the metadata
        if (isSchemaQuery) {
            return descriptorList.getDescriptors().stream()
                    .map(TapMetadataDescriptor::fromTapDescriptor).collect(Collectors.toList());
        } else {
            return descriptorList.getDescriptorMetadata();
        }
    }

    public static String encapsulateTableName(String tableName) {
        if (tableName.matches("^[a-zA-Z0-9_]*$") || tableName.contains(".") || tableName.contains("\"")) {
            return tableName;
        } else {
            return "\"" + tableName + "\"";
        }
    }


    public static CommonTapDescriptor getLevelDescriptor(String levelId) {
        if (levelId == null || levelId.isEmpty()) {
            return null;
        } else if (!levelId.contains("-")) {
            return DescriptorRepository.getInstance().getFirstDescriptor(EsaSkyWebConstants.CATEGORY_EXTERNAL, levelId);
        }

        String[] levelIds = levelId.split("-");
        CommonTapDescriptor parent = DescriptorRepository.getInstance().getFirstDescriptor(EsaSkyWebConstants.CATEGORY_EXTERNAL, levelIds[0]);

        for (CommonTapDescriptor child : parent.getChildren()) {
            String childName = child.getLongName();
            if (Objects.equals(childName, levelIds[1])) {
                if (levelIds.length > 2) {
                    for (CommonTapDescriptor grandChild : child.getChildren()) {
                        String grandChildName = grandChild.getLongName();
                        if (Objects.equals(grandChildName, levelIds[2])) {
                            return grandChild;
                        }

                    }
                } else {
                    return child;
                }
            }
        }

        return null;

    }
}
