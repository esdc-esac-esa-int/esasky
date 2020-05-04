package esac.archive.esasky.cl.web.client.model.converter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
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
        
        ArrayList<String> labels = new ArrayList<String>();
    	for (TapMetadata tapMetadata : tapRowList.getMetadata()) {
            MetadataDescriptor cmd = descriptor
                    .getMetadataDescriptorByTapName(tapMetadata.getName());
            if(cmd == null) {
            	labels.add("");
            } else if(descriptor instanceof ExtTapDescriptor || !cmd.getVisible()) {
            	labels.add(cmd.getLabel());
            } else {
            	labels.add(TextMgr.getInstance().getText(cmd.getLabel()));
            }
        }

        for (int i = 0; i < tapRowList.getData().size(); i++) {
        	TableRow row = new TableRow(i);
        	int columnNumber = 0;
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
                if(descriptor instanceof ExtTapDescriptor) {
                	elem.setLabel(cmd.getLabel());
                }else {
                	elem.setLabel(labels.get(columnNumber));
                }
                elem.setType(cmd.getType());
                elem.setVisible(cmd.getVisible());
                String data = tapRowList.getDataValue(cmd.getTapName(), i);
                elem.setValue(data);
                elem.setTapName(cmd.getTapName());
                row.getElements().add(elem);
                columnNumber++;
            }
            if(row.getElements().size() > 0) {
            	tableData.add(row);
            }
        }
        return tableData;
    }

}
