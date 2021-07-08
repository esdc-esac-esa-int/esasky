package esac.archive.esasky.cl.web.client.utility;

import java.io.IOException;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.cl.web.client.api.ApiConstants;
import esac.archive.esasky.cl.web.client.callback.JsonRequestCallback;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HiPSCoordsFrame;
import esac.archive.esasky.ifcs.model.client.HiPS.HiPSImageFormat;

public class HipsParser {

	HipsParserObserver observer;
	
	public HipsParser(HipsParserObserver observer) {
		this.observer = observer;
	}
	
	public HipsParser() {
		this(null);
	}
	
	public void loadProperties(String surveyRootUrl) {
		
	
		final String propertiesUrl = surveyRootUrl +  "/" +  ApiConstants.HIPS_PROPERTIES_FILE;
		JSONUtils.getJSONFromUrl(propertiesUrl, new JsonRequestCallback("", propertiesUrl) {
	
			@Override
			protected void onSuccess(Response response) {
				try {
					
					HiPS hips = parseHipsProperties(response.getText(), surveyRootUrl);
					observer.onSuccess(hips);
					
				}catch (IOException e) {
					Log.error(e.getMessage(), e);
					observer.onError(e.getMessage());
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				observer.onError(ApiConstants.HIPS_PROP_ERROR_LOADING + propertiesUrl);
			}
			
		});
	}
	
	public HiPS parseHipsProperties(String propertiesText, String surveyRootUrl) throws IOException {
		GeneralJavaScriptObject props = IniFileParser.parseIniString(propertiesText);
		
		detectHipSPropertyErrors(props);
		String imgFormat = parseImgFormat(props);
		String surveyFrame = props.getStringProperty(ApiConstants.HIPS_PROP_FRAME);
		int maximumNorder = (int) props.getDoubleProperty(ApiConstants.HIPS_PROP_ORDER);
		String title =  props.getStringProperty(ApiConstants.HIPS_PROP_TITLE);
		
		HiPS hips = new HiPS();
		hips.setSurveyId(title);
		hips.setSurveyName(title);
		hips.setSurveyRootUrl(surveyRootUrl);
		HiPSCoordsFrame surveyFrameEnum = HiPSCoordsFrame.GALACTIC.name().toLowerCase()
				.contains(surveyFrame.toLowerCase()) ? HiPSCoordsFrame.GALACTIC : HiPSCoordsFrame.EQUATORIAL;
		hips.setSurveyFrame(surveyFrameEnum);
		hips.setMaximumNorder(maximumNorder);
		HiPSImageFormat hipsImageFormatEnum = HiPSImageFormat.png.name().toLowerCase().contains(imgFormat.toLowerCase())
				? HiPSImageFormat.png : HiPSImageFormat.jpg;
		hips.setImgFormat(hipsImageFormatEnum);
		
		return hips;
	}
	
	public void detectHipSPropertyErrors(GeneralJavaScriptObject props) throws IOException {
		if(!props.hasProperty(ApiConstants.HIPS_PROP_FRAME )|| "".equals(props.getStringProperty(ApiConstants.HIPS_PROP_FRAME))) {
			throw new IOException(ApiConstants.HIPS_PROP_ERROR + ApiConstants.HIPS_PROP_FRAME);
		}
		if(!props.hasProperty(ApiConstants.HIPS_PROP_FORMAT )|| "".equals(props.getStringProperty(ApiConstants.HIPS_PROP_FORMAT))) {
			throw new IOException(ApiConstants.HIPS_PROP_ERROR + ApiConstants.HIPS_PROP_FORMAT);
		}
		if(!props.hasProperty(ApiConstants.HIPS_PROP_ORDER )|| "".equals(props.getStringProperty(ApiConstants.HIPS_PROP_ORDER))) {
			throw new IOException(ApiConstants.HIPS_PROP_ERROR + ApiConstants.HIPS_PROP_ORDER);
		}
		if(!props.hasProperty(ApiConstants.HIPS_PROP_TITLE )|| "".equals(props.getStringProperty(ApiConstants.HIPS_PROP_TITLE))) {
			throw new IOException(ApiConstants.HIPS_PROP_ERROR + ApiConstants.HIPS_PROP_TITLE);
		}
	}
	
	public String parseImgFormat(GeneralJavaScriptObject props) {
		String[] imgFormats = props.getStringProperty(ApiConstants.HIPS_PROP_FORMAT).split(" ");

		String imgFormat = "jpg";
		if(imgFormats.length > 1) {
			for(String format : imgFormats) {
				if(format.equalsIgnoreCase("png") || format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
					imgFormat = format;
					break;
				}
			}
		}else {
			imgFormat = imgFormats[0];
		}
		
		return imgFormat;
	}

}
