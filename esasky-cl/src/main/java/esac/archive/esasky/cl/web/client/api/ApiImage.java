package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.esasky.cl.web.client.model.HstOutreachImage;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper.OpenSeaDragonType;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class ApiImage extends ApiBase{
	
	private static String errorMsg = "Error parsing image properties. Property: @@@prop@@@ missing";
	
	public void parseHstImageData(final String name) {
		HstOutreachImage image = new HstOutreachImage(name);
		image.parseHstPageForProperties();
	}
	
	public void addTiledImage(JavaScriptObject input, JavaScriptObject widget) {
		GeneralJavaScriptObject parameters = (GeneralJavaScriptObject) input;
		parameters.setProperty("type", OpenSeaDragonType.TILED.getType());
		addImage(parameters, widget);
	}
	
	public void addSingleImage(JavaScriptObject input, JavaScriptObject widget) {
		GeneralJavaScriptObject parameters = (GeneralJavaScriptObject) input;
		parameters.setProperty("type", OpenSeaDragonType.SINGLE.getType());
		addImage(parameters, widget);
	}
	
	public void addImage(JavaScriptObject input, JavaScriptObject widget) {
		GeneralJavaScriptObject parameters = (GeneralJavaScriptObject) input;
		
		String name;
		String url;
		double ra;
		double dec;
		double fov;
		double rot;
		int width = 0;
		int height = 0;
		OpenSeaDragonType type = null;
		
		if(parameters.hasProperty("name")) {
			name = parameters.getStringProperty("name");
		}else {
			sendBackErrorMsgToWidget(errorMsg.replace("@@@prop@@@", "name"), widget);
			return;
		}
		
		if(parameters.hasProperty("url")) {
			url = parameters.getStringProperty("url");
		}else {
			sendBackErrorMsgToWidget(errorMsg.replace("@@@prop@@@", "url"), widget);
			return;
		}
		
		if(parameters.hasProperty("ra")) {
			ra = parameters.getDoubleProperty("ra");
		}else {
			sendBackErrorMsgToWidget(errorMsg.replace("@@@prop@@@", "ra"), widget);

			return;
		}
		
		if(parameters.hasProperty("dec")) {
			dec = parameters.getDoubleProperty("dec");
		}else {
			sendBackErrorMsgToWidget(errorMsg.replace("@@@prop@@@", "dec"), widget);
			return;
		}
		
		if(parameters.hasProperty("fov")) {
			fov = parameters.getDoubleProperty("fov");
		}else {
			sendBackErrorMsgToWidget(errorMsg.replace("@@@prop@@@", "fov"), widget);
			return;
		}
		
		if(parameters.hasProperty("rot")) {
			rot = parameters.getDoubleProperty("rot");
		}else {
			sendBackErrorMsgToWidget(errorMsg.replace("@@@prop@@@", "rot"), widget);
			return;
		}
		
	
		String typeName = parameters.getStringProperty("type");
		type = OpenSeaDragonType.getImageType(typeName);
		
		if(type == OpenSeaDragonType.TILED) {
			if(parameters.hasProperty("width")) {
				width = Integer.parseInt(parameters.getStringProperty("width"));
			}else {
				sendBackErrorMsgToWidget(errorMsg.replace("@@@prop@@@", "width"), widget);
				return;
			}
			
			if(parameters.hasProperty("height")) {
				height = Integer.parseInt(parameters.getStringProperty("height"));
			}else {
				sendBackErrorMsgToWidget(errorMsg.replace("@@@prop@@@", "height"), widget);
				return;
			}
		}
		
		
		addTiledImage(name, url, ra, dec, fov, rot, width, height, type);
	}
	
	private void addTiledImage(String name, String url, double ra, double dec, double fov,
			double rotation, int width, int height, OpenSeaDragonType type) {
		
		OpenSeaDragonWrapper openSeaDragonWrapper = new OpenSeaDragonWrapper(name, url, type, ra, dec, fov,
				rotation, width, height);
		openSeaDragonWrapper.addOpenSeaDragonToAladin(openSeaDragonWrapper.createOpenSeaDragonObject());
		
	}
	
}
