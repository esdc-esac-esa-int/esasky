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

package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.http.client.URL;

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
	private List<String> others = new LinkedList<>();
	
    public DatalinkLinks (String [] data, DatalinkMetadata[] metadata) {
    	for(int i = 0; i < metadata.length; i++) {
			parseRow(data, metadata, i);
		}
    }

    private void parseRow(String[] data, DatalinkMetadata[] metadata, int rowNumber) {
        if(data[rowNumber].isEmpty()) {
        	return;
        }
        if("access_url".equalsIgnoreCase(metadata[rowNumber].getName())) {
        	accessUrl = extractEncodedAccessUrl(data[rowNumber]);
        } else if("service_def".equalsIgnoreCase(metadata[rowNumber].getName())) {
        	serviceDef = data[rowNumber];
        } else if("error_message".equalsIgnoreCase(metadata[rowNumber].getName())) {
        	errorMessage = data[rowNumber];
        } else if("description".equalsIgnoreCase(metadata[rowNumber].getName())) {
        	description = data[rowNumber];
        } else if("semantics".equalsIgnoreCase(metadata[rowNumber].getName())) {
        	semantics = data[rowNumber];
        } else if("content_type".equalsIgnoreCase(metadata[rowNumber].getName())) {
        	contentType = data[rowNumber];
        } else if("content_length".equalsIgnoreCase(metadata[rowNumber].getName())) {
        	contentLength = data[rowNumber];
        } else if(!"ID".equalsIgnoreCase(metadata[rowNumber].getName())){
        	others.add(metadata[rowNumber].getName() + ": " + data[rowNumber]);
        }
    }

	private String extractEncodedAccessUrl(String encodedUrl) {
		if(encodedUrl.contains("?")) {
			String [] baseUrlAndQueryString = encodedUrl.split("\\?");
			return baseUrlAndQueryString[0] + "?" + URL.decodeQueryString(baseUrlAndQueryString[1]);
		} else {
			return encodedUrl;
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
    
    public String getContentLength() {
        return contentLength;
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