package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.LinkedList;
import java.util.List;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.SizeFormatter;

public class DatalinkLinks {
	private String accessUrl = "";
	private String serviceDef = "";
	private String description = "";
	private String errorMessage = "";
	private String semantics = "";
	private String contentType = "";
	private String contentLength = "";
	private List<String> others = new LinkedList<String>();
	
    public DatalinkLinks (String [] data, DatalinkMetadata[] metadata) {
    	for(int i = 0; i < metadata.length; i++) {
			parseRow(data, metadata, i);
		}
    }

    private void parseRow(String[] data, DatalinkMetadata[] metadata, int rowNumber) {
        if(data[rowNumber].isEmpty()) {
        	return;
        }
        if(metadata[rowNumber].getName().equalsIgnoreCase("access_url")) {
        	accessUrl = data[rowNumber];
        } else if(metadata[rowNumber].getName().equals("service_def")) {
        	serviceDef = data[rowNumber];
        } else if(metadata[rowNumber].getName().equals("error_message")) {
        	errorMessage = data[rowNumber];
        } else if(metadata[rowNumber].getName().equals("description")) {
        	description = data[rowNumber];
        } else if(metadata[rowNumber].getName().equals("semantics")) {
        	semantics = data[rowNumber];
        } else if(metadata[rowNumber].getName().equals("content_type")) {
        	contentType = data[rowNumber];
        } else if(metadata[rowNumber].getName().equals("content_length")) {
        	contentLength = data[rowNumber];
        } else if(!metadata[rowNumber].getName().equalsIgnoreCase("ID")){
        	others.add(metadata[rowNumber].getName() + ": " + data[rowNumber]);
        }
    }
    
    public String getTypeAndSizeDisplayText() {
        String typeAndSizeDisplayText = "";
        if(!contentType.isEmpty()) {
            if(contentType.contains("/")) {
                String [] contentTypeSplit = contentType.split("/");
                typeAndSizeDisplayText = contentTypeSplit[contentTypeSplit.length - 1];
            } else {
                typeAndSizeDisplayText = contentType;
            }
            if(typeAndSizeDisplayText.toLowerCase().contains("datalink")) {
                typeAndSizeDisplayText = " (" + TextMgr.getInstance().getText("datalink_linkedProducts") + ")";
            } else {
                typeAndSizeDisplayText = " (" + typeAndSizeDisplayText.toUpperCase() + ")";
            }
        }
        if(!contentLength.isEmpty()) {
            if(contentType.isEmpty()) {
                typeAndSizeDisplayText = " (";
            } else {
                typeAndSizeDisplayText = typeAndSizeDisplayText.substring(0, typeAndSizeDisplayText.length() - 1) + ", ";
            }
            try {
                typeAndSizeDisplayText += SizeFormatter.formatBytes(new Integer(contentLength), 0) + ")";
            } catch (NumberFormatException e) {
                typeAndSizeDisplayText += contentLength + ")";
            }
        }
        return typeAndSizeDisplayText;
    }
    
    public String getAccessUrl() {
        return accessUrl;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSemantics() {
        return semantics;
    }
    public String getServiceDef() {
        return serviceDef;
    }
    public List<String> getOthers() {
        return others;
    }
}