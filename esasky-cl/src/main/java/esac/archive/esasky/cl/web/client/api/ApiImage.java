package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.esasky.cl.web.client.model.HstOutreachImage;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper.OpenSeaDragonType;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class ApiImage extends ApiBase{
	
	
	public ApiImage() {
	}
	
	public void parseHstImageData(final String name) {
		HstOutreachImage image = new HstOutreachImage(name);
		image.parseHstPageForProperties();
	}
	
	public void addTiledImage(JavaScriptObject input) {
		GeneralJavaScriptObject parameters = (GeneralJavaScriptObject) input;
		
		String name;
		String url;
		double ra;
		double dec;
		double fov;
		double rot;
		int width;
		int height;
		
		if(parameters.hasProperty("name")) {
			name = parameters.getStringProperty("name");
		}else {
			return;
		}
		
		if(parameters.hasProperty("url")) {
			url = parameters.getStringProperty("url");
		}else {
			return;
		}
		
		if(parameters.hasProperty("ra")) {
			ra = parameters.getDoubleProperty("ra");
		}else {
			return;
		}
		
		if(parameters.hasProperty("dec")) {
			dec = parameters.getDoubleProperty("dec");
		}else {
			return;
		}
		
		if(parameters.hasProperty("fov")) {
			fov = parameters.getDoubleProperty("fov");
		}else {
			return;
		}
		
		if(parameters.hasProperty("rot")) {
			rot = parameters.getDoubleProperty("rot");
		}else {
			return;
		}
		
		if(parameters.hasProperty("width")) {
			width = Integer.parseInt(parameters.getStringProperty("width"));
		}else {
			return;
		}
		
		if(parameters.hasProperty("height")) {
			height = Integer.parseInt(parameters.getStringProperty("height"));
		}else {
			return;
		}
		
		addTiledImage(name, url, ra, dec, fov, rot, width, height);
	}
	
	private void addTiledImage(String name, String url, double ra, double dec, double fov, double rotation, int width, int height) {
		
		OpenSeaDragonWrapper openSeaDragonWrapper = new OpenSeaDragonWrapper(name, url, OpenSeaDragonType.TILED, ra, dec, fov,
				rotation, width, height);
		openSeaDragonWrapper.addOpenSeaDragonToAladin(openSeaDragonWrapper.createOpenSeaDragonObject());
		
	}
	
}
