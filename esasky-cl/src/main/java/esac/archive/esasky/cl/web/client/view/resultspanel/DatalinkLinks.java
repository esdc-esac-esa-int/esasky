package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.LinkedList;
import java.util.List;

import esac.archive.esasky.cl.web.client.utility.SizeFormatter;

public class DatalinkLinks {
	public String ID = "";
	public String access_url = "";
	public String service_def = "";
	public String description = "";
	public String error_message = "";
	public String semantics = "";
	public String content_type = "";
	public String content_length = "";
	public List<String> others = new LinkedList<String>();
	
	public String getTypeAndSizeDisplayText() {
		String typeAndSizeDisplayText = "";
		if(!content_type.isEmpty()) {
			if(content_type.contains("/")) {
				String [] contentType = content_type.split("/");
				typeAndSizeDisplayText = contentType[contentType.length - 1];
			} else {
				typeAndSizeDisplayText = content_type;
			}
			typeAndSizeDisplayText = " (" + typeAndSizeDisplayText.toUpperCase() + ")";
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
	
    public static DatalinkLinks parseDatalinkLinks(String [] data, DatalinkMetadata[] metadata) {
    	DatalinkLinks datalinkLinks = new DatalinkLinks();
    	for(int i = 0; i < metadata.length; i++) {
			if(data[i].isEmpty()) {
				continue;
			}
			if(metadata[i].getName().equalsIgnoreCase("ID")) {
				datalinkLinks.ID = data[i];
			} else if(metadata[i].getName().equalsIgnoreCase("access_url")) {
				datalinkLinks.access_url = data[i];
			} else if(metadata[i].getName().equals("service_def")) {
				datalinkLinks.service_def = data[i];
			} else if(metadata[i].getName().equals("error_message")) {
				datalinkLinks.error_message = data[i];
			} else if(metadata[i].getName().equals("description")) {
				datalinkLinks.description = data[i];
			} else if(metadata[i].getName().equals("semantics")) {
				datalinkLinks.semantics = data[i];
			} else if(metadata[i].getName().equals("content_type")) {
				datalinkLinks.content_type = data[i];
			} else if(metadata[i].getName().equals("content_length")) {
				datalinkLinks.content_length = data[i];
			} else {
				datalinkLinks.others.add(metadata[i].getName() + ": " + data[i]);
			}
		}
    	return datalinkLinks;
    }
}