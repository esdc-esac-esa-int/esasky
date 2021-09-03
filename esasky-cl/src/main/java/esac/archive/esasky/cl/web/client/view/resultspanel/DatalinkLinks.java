package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.LinkedList;
import java.util.List;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.SizeFormatter;

public class DatalinkLinks {
	private String access_url = "";
	private String service_def = "";
	private String description = "";
	private String error_message = "";
	private String semantics = "";
	private String content_type = "";
	private String content_length = "";
	private List<String> others = new LinkedList<String>();
	
	public String getTypeAndSizeDisplayText() {
		String typeAndSizeDisplayText = "";
		if(!content_type.isEmpty()) {
			if(content_type.contains("/")) {
				String [] contentType = content_type.split("/");
				typeAndSizeDisplayText = contentType[contentType.length - 1];
			} else {
				typeAndSizeDisplayText = content_type;
			}
			if(typeAndSizeDisplayText.toLowerCase().contains("datalink")) {
			    typeAndSizeDisplayText = " (" + TextMgr.getInstance().getText("datalink_linkedProducts") + ")";
			} else {
			    typeAndSizeDisplayText = " (" + typeAndSizeDisplayText.toUpperCase() + ")";
			}
		}
		if(!content_length.isEmpty()) {
			if(content_type.isEmpty()) {
				typeAndSizeDisplayText = " (";
			} else {
				typeAndSizeDisplayText = typeAndSizeDisplayText.substring(0, typeAndSizeDisplayText.length() - 1) + ", ";
			}
			try {
				typeAndSizeDisplayText += SizeFormatter.formatBytes(new Integer(content_length), 0) + ")";
			} catch (NumberFormatException e) {
				typeAndSizeDisplayText += content_length + ")";
			}
		}
		return typeAndSizeDisplayText;
	}
	
    public DatalinkLinks (String [] data, DatalinkMetadata[] metadata) {
    	for(int i = 0; i < metadata.length; i++) {
			if(data[i].isEmpty()) {
				continue;
			}
			if(metadata[i].getName().equalsIgnoreCase("access_url")) {
				access_url = data[i];
			} else if(metadata[i].getName().equals("service_def")) {
				service_def = data[i];
			} else if(metadata[i].getName().equals("error_message")) {
				error_message = data[i];
			} else if(metadata[i].getName().equals("description")) {
				description = data[i];
			} else if(metadata[i].getName().equals("semantics")) {
				semantics = data[i];
			} else if(metadata[i].getName().equals("content_type")) {
				content_type = data[i];
			} else if(metadata[i].getName().equals("content_length")) {
				content_length = data[i];
			} else {
				others.add(metadata[i].getName() + ": " + data[i]);
			}
		}
    }
    
    public String getAccessUrl() {
        return access_url;
    }
    
    public String getContentType() {
        return content_type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getErrorMessage() {
        return error_message;
    }

    public String getSemantics() {
        return semantics;
    }
    public String getServiceDef() {
        return service_def;
    }
    public List<String> getOthers() {
        return others;
    }
}