package esac.archive.esasky.cl.web.client.model.converter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.ammi.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.ammi.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.ammi.ifcs.model.descriptor.IDescriptor;
import esac.archive.ammi.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.ammi.ifcs.model.shared.ColumnType;
import esac.archive.esasky.cl.web.client.api.APIMetadataConstants;
import esac.archive.esasky.cl.web.client.api.model.Footprint;
import esac.archive.esasky.cl.web.client.api.model.FootprintListJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.FootprintListOverlay;
import esac.archive.esasky.cl.web.client.api.model.GeneralSkyObject;
import esac.archive.esasky.cl.web.client.api.model.IJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.MetadataAPI;
import esac.archive.esasky.cl.web.client.api.model.Source;
import esac.archive.esasky.cl.web.client.api.model.SourceListJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.SourceListOverlay;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TableElement;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.model.TapMetadata;
import esac.archive.esasky.cl.web.client.model.TapRowList;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class TapToMmiDataConverter {

    private TapToMmiDataConverter() {
        // prevents calls from subclass
    }
    
    public static List<TableRow> convertTapToMMIData(
            TapRowList tapRowList, final IDescriptor descriptor) {
        List<TableRow> tableData = new LinkedList<TableRow>();

        for (int i = 0; i < tapRowList.getData().size(); i++) {
        	TableRow row = new TableRow(i);
            for (TapMetadata tapMetadata : tapRowList.getMetadata()) {
                TableElement elem = new TableElement();
                MetadataDescriptor cmd = descriptor
                        .getMetadataDescriptorByTapName(tapMetadata.getName());
                if(cmd == null) {
                	break;
                }
                
                if (cmd.getMaxDecimalDigits() != null) {
                    elem.setMaxDecimalDigits(cmd.getMaxDecimalDigits());
                }
                elem.setColumnIndex(cmd.getIndex());
                elem.setLabel(TextMgr.getInstance().getText(cmd.getLabel()));
                elem.setType(cmd.getType());
                elem.setVisible(cmd.getVisible());
                String data = tapRowList.getDataValue(cmd.getTapName(), i);
                elem.setValue(data);
                elem.setTapName(cmd.getTapName());
                row.getElements().add(elem);
            }
            if(row.getElements().size() > 0) {
            	tableData.add(row);
            }
        }
        return tableData;
    }

    
    
	public static TapRowList convertCSVToTAPRowList(IJSONWrapper dataset, CoordinatesFrame convertToFrame) {
		if (dataset instanceof FootprintListJSONWrapper) {
			return TapToMmiDataConverter.convertFootprintsCSVToTAPRowList((FootprintListJSONWrapper) dataset);
		} else if (dataset instanceof SourceListJSONWrapper) {
			return TapToMmiDataConverter.convertCatalogueCSVToTAPRowList((SourceListJSONWrapper) dataset, convertToFrame);
		}
		return null;
	}

	private static TapRowList convertCatalogueCSVToTAPRowList(SourceListJSONWrapper dataset, CoordinatesFrame convertToFrame) {
		Log.debug("[TapToMmiDataConverter][convertCatalogueCSVToTAPRowList]");
		TapRowList tapRowList = new TapRowList();

		TapMetadata tmdName = new TapMetadata();
		tmdName.setName(APIMetadataConstants.CAT_NAME);
		tmdName.setDatatype(ColumnType.STRING.getName());
		tmdName.setUcd("null");
		tmdName.setArraysize(Integer.toString(APIMetadataConstants.CAT_NAME.length()));
		tapRowList.getMetadata().add(tmdName);

		TapMetadata tmdRA = new TapMetadata();
		tmdRA.setName(APIMetadataConstants.CENTER_RA_DEG);
		tmdRA.setDatatype(ColumnType.RA.getName());
		tmdRA.setUcd("null");
		tmdRA.setArraysize(Integer.toString(APIMetadataConstants.CENTER_RA_DEG.length()));
		tapRowList.getMetadata().add(tmdRA);

		TapMetadata tmdDec = new TapMetadata();
		tmdDec.setName(APIMetadataConstants.CENTER_DEC_DEG);
		tmdDec.setDatatype(ColumnType.DEC.getName());
		tmdDec.setUcd("null");
		tmdDec.setArraysize(Integer.toString(APIMetadataConstants.CENTER_DEC_DEG.length()));
		tapRowList.getMetadata().add(tmdDec);

		SourceListOverlay sourceList = (SourceListOverlay) dataset.getOverlaySet();
		GeneralSkyObject generalSkyObject = ((GeneralSkyObject) sourceList.getSkyObjectList().get(0));

		for (MetadataAPI currMetaApi : generalSkyObject.getData()) {

			TapMetadata ctmd = new TapMetadata();
			ctmd.setName(currMetaApi.getName());
			ctmd.setDatatype(currMetaApi.getType());
			ctmd.setUcd("null");
			ctmd.setArraysize(Integer.toString(currMetaApi.getName().length()));
			tapRowList.getMetadata().add(ctmd);
		}

		String ra; 
		String dec;
		Double[] raDecConverted;
		for (Object currSkyObj : sourceList.getSkyObjectList()) {

			Source currSource = (Source) currSkyObj;
			ra = currSource.getRa();
			dec = currSource.getDec();
					
			if (convertToFrame == CoordinatesFrame.GALACTIC){
				raDecConverted = CoordinatesConversion.convertPointEquatorialToGalactic(Double.parseDouble(currSource.getRa()), Double.parseDouble(currSource.getDec()));
				ra = String.valueOf(raDecConverted[0]);
				dec = String.valueOf(raDecConverted[1]);
			}else if (convertToFrame == CoordinatesFrame.J2000){
				raDecConverted = CoordinatesConversion.convertPointGalacticToJ2000(Double.parseDouble(currSource.getRa()), Double.parseDouble(currSource.getDec()));
				ra = String.valueOf(raDecConverted[0]);
				dec = String.valueOf(raDecConverted[1]);
			} 
			
			ArrayList<Object> values = new ArrayList<Object>();
			values.add(currSource.getName());
			values.add(ra);
			values.add(dec);

			for (MetadataAPI currMetaApi : currSource.getData()) {
				values.add(currMetaApi.getValue());
			}

			tapRowList.getData().add(values);
		}

		return tapRowList;
	}

	public static TapRowList convertFootprintsCSVToTAPRowList(FootprintListJSONWrapper footprintsSet) {
		Log.debug("[TapToMmiDataConverter][convertFootprintsCSVToTAPRowList]");
		TapRowList tapRowList = new TapRowList();

		TapMetadata tmdName = new TapMetadata();
		tmdName.setName(APIMetadataConstants.OBS_NAME);
		tmdName.setDatatype(ColumnType.STRING.getName());
		tmdName.setUcd("null");
		tmdName.setArraysize(Integer.toString(APIMetadataConstants.OBS_NAME.length()));
		tapRowList.getMetadata().add(tmdName);

		TapMetadata tmdId = new TapMetadata();
		tmdId.setName(APIMetadataConstants.ID);
		tmdId.setDatatype(ColumnType.STRING.getName());
		tmdId.setUcd("null");
		tmdId.setArraysize(Integer.toString(APIMetadataConstants.ID.length()));
		tapRowList.getMetadata().add(tmdId);

		TapMetadata tmdStcs = new TapMetadata();
		tmdStcs.setName(APIMetadataConstants.FOOTPRINT_STCS);
		tmdStcs.setDatatype(ColumnType.STRING.getName());
		tmdStcs.setUcd("null");
		tmdStcs.setArraysize(Integer.toString(APIMetadataConstants.FOOTPRINT_STCS.length()));
		tapRowList.getMetadata().add(tmdStcs);

		TapMetadata tmdRA = new TapMetadata();
		tmdRA.setName(APIMetadataConstants.CENTER_RA_DEG);
		tmdRA.setDatatype(ColumnType.RA.getName());
		tmdRA.setUcd("null");
		tmdRA.setArraysize(Integer.toString(APIMetadataConstants.CENTER_RA_DEG.length()));
		tapRowList.getMetadata().add(tmdRA);

		TapMetadata tmdDec = new TapMetadata();
		tmdDec.setName(APIMetadataConstants.CENTER_DEC_DEG);
		tmdDec.setDatatype(ColumnType.DEC.getName());
		tmdDec.setUcd("null");
		tmdDec.setArraysize(Integer.toString(APIMetadataConstants.CENTER_DEC_DEG.length()));
		tapRowList.getMetadata().add(tmdDec);

		FootprintListOverlay footprintList = (FootprintListOverlay) footprintsSet.getOverlaySet();
		GeneralSkyObject generalSkyObject = ((GeneralSkyObject) footprintList.getSkyObjectList().get(0));

		for (MetadataAPI currMetaApi : generalSkyObject.getData()) {
			TapMetadata ctmd = new TapMetadata();
			ctmd.setName(currMetaApi.getName());
			ctmd.setDatatype(currMetaApi.getType());
			ctmd.setUcd("null");
			ctmd.setArraysize(Integer.toString(currMetaApi.getName().length()));
			tapRowList.getMetadata().add(ctmd);
		}

		for (Object currSkyObj : footprintList.getSkyObjectList()) {

			Footprint currFootprint = (Footprint) currSkyObj;
			ArrayList<Object> values = new ArrayList<Object>();

			values.add(currFootprint.getName());
			values.add(currFootprint.getId());
			values.add(currFootprint.getStcs());

			if (null == currFootprint.getRa_deg() || "".equals(currFootprint.getRa_deg())) {
				// first RA position in the STCS is 2 (0 and 1 are taken by
				// Polygon J2000)
				values.add(currFootprint.getStcs().split("\\s")[2]);
			} else {
				values.add(currFootprint.getRa_deg());
			}

			if (null == currFootprint.getDec_deg() || "".equals(currFootprint.getDec_deg())) {
				// first RA position in the STCS is 3
				values.add(currFootprint.getStcs().split("\\s")[3]);
			} else {
				values.add(currFootprint.getDec_deg());
			}

			for (MetadataAPI currMetaApi : currFootprint.getData()) {
				values.add(currMetaApi.getValue());
			}

			tapRowList.getData().add(values);
		}
		// Log.debug("[TapToMmiDataConverter][convertFootprintsCSVToTAPRowList]
		// exiting");
		return tapRowList;
	}
    
}
