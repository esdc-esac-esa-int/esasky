package esac.archive.esasky.cl.web.client.api;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.model.OutreachImage;
import esac.archive.esasky.cl.web.client.query.TAPImageListService;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper.OpenSeaDragonType;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.io.IOException;

public class ApiImage extends ApiBase{

	private static String errorMsg = "Error parsing image properties. Property: @@@prop@@@ missing";

	public ApiImage(Controller controller) {
		this.controller = controller;
	}

	public void parseHstImageData(final String name) {

		CommonTapDescriptor descriptor = DescriptorRepository.getInstance()
				.getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_IMAGES)
				.getDescriptorByMission(EsaSkyConstants.HST_MISSION);

		if (descriptor != null) {
			OutreachImage image = new OutreachImage(name, 1.0, descriptor);
			TAPImageListService metadataService = TAPImageListService.getInstance();
			image.loadImage(descriptor, metadataService, true);
		}
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

	public void openOutreachPanel(String telescope) {
		controller.getRootPresenter().getCtrlTBPresenter().openOutreachPanel(telescope);
	}

//	public void openTelescopeOutreachPanel() {
//		controller.getRootPresenter().getCtrlTBPresenter().openTelescopeOutreachPanel();
//	}

	public void closeOutreachPanel(String telescope) {
		controller.getRootPresenter().getCtrlTBPresenter().closeOutreachPanel(telescope);
	}

	public void getAllOutreachImageIds(JavaScriptObject widget) {
		JSONObject obj = new JSONObject();
		JSONArray ids = controller.getRootPresenter().getCtrlTBPresenter().getOutreachImageIds(result -> {
			obj.put("Available_ids", result);
			sendBackToWidget(obj, null, widget);
		});

		if (ids.size() > 0) {
			obj.put("Available_ids", ids);
			sendBackToWidget(obj, null, widget);
		}

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
