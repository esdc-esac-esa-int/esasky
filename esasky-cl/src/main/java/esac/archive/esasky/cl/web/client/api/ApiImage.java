package esac.archive.esasky.cl.web.client.api;


import java.io.IOException;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.model.HstOutreachImage;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper.OpenSeaDragonType;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class ApiImage extends ApiBase{
	
	private static String errorMsg = "Error parsing image properties. Property: @@@prop@@@ missing";

	public ApiImage(Controller controller) {
		this.controller = controller;
	}

	public void parseHstImageData(final String name) {
		HstOutreachImage image = new HstOutreachImage(name, 1.0);
		image.loadImage();
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
	
	public String parseParameter(GeneralJavaScriptObject parameters, String parameter) throws IOException {
		
		if(parameters.hasProperty(parameter)) {
			return parameters.getStringProperty(parameter);
		}else {
			throw new IOException(errorMsg.replace("@@@prop@@@", parameter));
		}
	}

	public double parseDoubleParameter(GeneralJavaScriptObject parameters, String parameter) throws IOException {
		
		if(parameters.hasProperty(parameter)) {
			return parameters.getDoubleProperty(parameter);
		}else {
			throw new IOException(errorMsg.replace("@@@prop@@@", parameter));
		}
	}
	
	public void addImage(JavaScriptObject input, JavaScriptObject widget) {
		GeneralJavaScriptObject parameters = (GeneralJavaScriptObject) input;
	
		OpenSeaDragonType type = null;
		
		
		try {
			String name = parseParameter(parameters, "name");
			String url = parseParameter(parameters, "url");
			double ra =  parseDoubleParameter(parameters, "ra");
			double dec = parseDoubleParameter(parameters, "dec");
			double fov = parseDoubleParameter(parameters, "fov");
			double rot = parseDoubleParameter(parameters, "rot");

			String typeName = parseParameter(parameters, "type");
			type = OpenSeaDragonType.getImageType(typeName);
			int width = 1;
			int height = 1;
			
			if(type == OpenSeaDragonType.TILED) {
				height = Integer.parseInt(parseParameter(parameters, "height"));
				width = Integer.parseInt(parseParameter(parameters, "width"));
			}
			
			addTiledImage(name, url, ra, dec, fov, rot, width, height, type);

		}catch(IOException e) {
			sendBackErrorMsgToWidget(e.getMessage(), widget);
			Log.error(e.getMessage(), e);
		}
		
	}

	public void openOutreachPanel() {
		controller.getRootPresenter().getCtrlTBPresenter().openOutreachPanel();
	}

	public void closeOutreachPanel() {
		controller.getRootPresenter().getCtrlTBPresenter().closeOutreachPanel();
	}

	public void getAllOutreachImageIds(JavaScriptObject widget) {
		JSONArray ids = controller.getRootPresenter().getCtrlTBPresenter().getOutreachImageIds();
		JSONObject obj = new JSONObject();
		obj.put("Available_ids", ids);
		sendBackToWidget(obj, null, widget);
	}

	public void showOutreachImage(String id) {
		controller.getRootPresenter().getCtrlTBPresenter().showOutreachImage(id);
	}

	private void addTiledImage(String name, String url, double ra, double dec, double fov,
			double rotation, int width, int height, OpenSeaDragonType type) {
		
		OpenSeaDragonWrapper openSeaDragonWrapper = new OpenSeaDragonWrapper(name, url, type, ra, dec, fov,
				rotation, width, height);
		openSeaDragonWrapper.addOpenSeaDragonToAladin(openSeaDragonWrapper.createOpenSeaDragonObject());
		
	}
	
}
