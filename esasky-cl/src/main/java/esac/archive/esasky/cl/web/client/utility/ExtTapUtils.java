package esac.archive.esasky.cl.web.client.utility;


import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.TapMetadataDescriptor;

import java.util.List;
import java.util.stream.Collectors;

public class ExtTapUtils {

    private ExtTapUtils() {}


    public static CommonTapDescriptor createLevelDescriptor(CommonTapDescriptor parent, String name, String whereADQL, double wavelengthStart, double wavelengthEnd) {
        CommonTapDescriptor descriptor = new CommonTapDescriptor();
        descriptor.setGroupColumn1(parent.getGroupColumn1());
        descriptor.setGroupColumn2(parent.getGroupColumn2());
        descriptor.setMission(parent.getMission());
        descriptor.setLongName(name);
        descriptor.setShortName(name);
        descriptor.setWavelengthStart(wavelengthStart);
        descriptor.setWavelengthEnd(wavelengthEnd);
        descriptor.setCategory(parent.getCategory());
        descriptor.setFovLimit(parent.getFovLimit());
        descriptor.setTableName(parent.getTableName());
        descriptor.setWhereADQL(whereADQL);
        descriptor.setSelectADQL("SELECT *");
        descriptor.setFovLimitDisabled(parent.isFovLimitDisabled());
        descriptor.setShapeLimit(parent.getShapeLimit());
        descriptor.setTapUrl(parent.getTapUrl());
        descriptor.setIsExternal(parent.isExternal());
        descriptor.setMetadata(parent.getMetadata());
        descriptor.setUseIntersectsPolygon(parent.useIntersectsPolygon());
        parent.addChild(descriptor);

        return descriptor;
    }

    public static void updateLevelDescriptor(CommonTapDescriptor descriptor, String whereADQL, double wavelengthStart, double wavelengthEnd) {
        descriptor.setWhereADQL(whereADQL);
        descriptor.setWavelengthStart(wavelengthStart);
        descriptor.setWavelengthEnd(wavelengthEnd);
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
}
