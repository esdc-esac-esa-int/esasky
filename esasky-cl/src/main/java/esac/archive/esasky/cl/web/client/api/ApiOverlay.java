package esac.archive.esasky.cl.web.client.api;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.api.model.Footprint;
import esac.archive.esasky.cl.web.client.api.model.FootprintListJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.FootprintListOverlay;
import esac.archive.esasky.cl.web.client.api.model.GeneralSkyObject;
import esac.archive.esasky.cl.web.client.api.model.MetadataAPI;
import esac.archive.esasky.cl.web.client.api.model.SourceListJSONWrapper;
import esac.archive.esasky.cl.web.client.api.model.SourceListOverlay;
import esac.archive.esasky.cl.web.client.model.SourceShapeType;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;

public class ApiOverlay extends ApiBase{
	
	public interface FootprintsSetMapper extends ObjectMapper<FootprintListJSONWrapper> {}
	public interface CatalogDescriptorMapper extends ObjectMapper<CatalogDescriptor> {}
	public interface CatalogueMapper extends ObjectMapper<SourceListJSONWrapper> {}

	Map<String, JavaScriptObject> userCatalogues = new HashMap<String, JavaScriptObject>();
	Map<String, JavaScriptObject> setOfFootprints = new HashMap<String, JavaScriptObject>();
	
	public ApiOverlay(Controller controller) {
		this.controller = controller;
	}
	
	public void overlayFootprints(String footprintsSetJSON, boolean shouldBeInTablePanel) {

		FootprintsSetMapper mapper = GWT.create(FootprintsSetMapper.class);

		try {
			FootprintListJSONWrapper footprintsSet = (FootprintListJSONWrapper) mapper.read(footprintsSetJSON);

			GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_overlayFootprintsWithDetails, footprintsSet.getOverlaySet().getOverlayName());

			List<MetadataDescriptor> metadata = new LinkedList<MetadataDescriptor>();

			MetadataDescriptor mName = new MetadataDescriptor();
			mName.setIndex(0);
			mName.setLabel(ApiConstants.OBS_NAME);
			mName.setMaxDecimalDigits(null);
			mName.setTapName(ApiConstants.OBS_NAME);
			mName.setType(ColumnType.STRING);
			mName.setVisible(true);
			metadata.add(mName);

			MetadataDescriptor mStcs = new MetadataDescriptor();
			mStcs.setIndex(0);
			mStcs.setLabel(ApiConstants.FOOTPRINT_STCS);
			mStcs.setMaxDecimalDigits(null);
			mStcs.setTapName(ApiConstants.FOOTPRINT_STCS);
			mStcs.setType(ColumnType.STRING);
			mStcs.setVisible(false);
			metadata.add(mStcs);

			MetadataDescriptor mRa = new MetadataDescriptor();
			mRa.setIndex(0);
			mRa.setLabel(ApiConstants.CENTER_RA_DEG);
			mRa.setMaxDecimalDigits(null);
			mRa.setTapName(ApiConstants.CENTER_RA_DEG);
			mRa.setType(ColumnType.RA);
			mRa.setVisible(false);
			metadata.add(mRa);

			MetadataDescriptor mDec = new MetadataDescriptor();
			mDec.setIndex(0);
			mDec.setLabel(ApiConstants.CENTER_DEC_DEG);
			mDec.setMaxDecimalDigits(null);
			mDec.setTapName(ApiConstants.CENTER_DEC_DEG);
			mDec.setType(ColumnType.DEC);
			mDec.setVisible(false);
			metadata.add(mDec);

			FootprintListOverlay fooprintList = (FootprintListOverlay) footprintsSet.getOverlaySet();

			GeneralSkyObject generalSkyObject = (GeneralSkyObject) fooprintList.getSkyObjectList().get(0);

			for (MetadataAPI currMetadata : generalSkyObject.getData()) {
				MetadataDescriptor m = new MetadataDescriptor();
				m.setIndex(0);
				m.setLabel(currMetadata.getName());
				m.setMaxDecimalDigits(null);
				m.setTapName(currMetadata.getName());
				m.setType(ColumnType.STRING);
				m.setVisible(true);
				metadata.add(m);
			}

			IDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository()
					.initUserDescriptor(metadata, footprintsSet);

			controller.getRootPresenter().showUserRelatedMetadata(descriptor, GeneralJavaScriptObject.createJsonObject(footprintsSetJSON), shouldBeInTablePanel);

			AladinLiteWrapper.getAladinLite().goToRaDec(
					((Footprint) fooprintList.getSkyObjectList().get(0)).getRa_deg(),
					((Footprint) fooprintList.getSkyObjectList().get(0)).getDec_deg());
		} catch (Exception ex) {
			Log.error(ex.getMessage(), ex);
		}

	}

	public void overlayCatalogue(String userCatalogueJSON, boolean shouldBeInTablePanel) {


		CatalogueMapper mapper = GWT.create(CatalogueMapper.class);
		try {
			
			userCatalogueJSON = userCatalogueJSON.replace("\"ra\":", "\"ra_deg\":");
			userCatalogueJSON = userCatalogueJSON.replace("\"dec\":", "\"dec_deg\":");
			SourceListJSONWrapper userCatalogue = (SourceListJSONWrapper) mapper.read(userCatalogueJSON);
			GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_overlayCatalogueWithDetails, userCatalogue.getOverlaySet().getOverlayName());

			List<MetadataDescriptor> metadata = new LinkedList<MetadataDescriptor>();
			
			MetadataDescriptor mName = new MetadataDescriptor();
			mName.setIndex(0);
			mName.setLabel(ApiConstants.CAT_NAME);
			mName.setMaxDecimalDigits(null);
			mName.setTapName(ApiConstants.CAT_NAME);
			mName.setType(ColumnType.STRING);
			mName.setVisible(true);
			metadata.add(mName);

			MetadataDescriptor mRa = new MetadataDescriptor();
			mRa.setIndex(0);
			mRa.setLabel(ApiConstants.CENTER_RA_DEG);
			mRa.setMaxDecimalDigits(null);
			mRa.setTapName(ApiConstants.CENTER_RA_DEG);
			mRa.setType(ColumnType.RA);
			mRa.setVisible(true);
			metadata.add(mRa);

			MetadataDescriptor mDec = new MetadataDescriptor();
			mDec.setIndex(0);
			mDec.setLabel(ApiConstants.CENTER_DEC_DEG);
			mDec.setMaxDecimalDigits(null);
			mDec.setTapName(ApiConstants.CENTER_DEC_DEG);
			mDec.setType(ColumnType.DEC);
			mDec.setVisible(true);
			metadata.add(mDec);

			SourceListOverlay sourceList = (SourceListOverlay) userCatalogue.getOverlaySet();
			GeneralSkyObject generalSkyObject = (GeneralSkyObject) sourceList.getSkyObjectList().get(0);

			for (MetadataAPI currMetadata : generalSkyObject.getData()) {
				MetadataDescriptor m = new MetadataDescriptor();
				m.setIndex(0);
				m.setLabel(currMetadata.getName());
				m.setMaxDecimalDigits(null);
				m.setTapName(currMetadata.getName());
				m.setType(ColumnType.STRING);
				m.setVisible(true);
				metadata.add(m);
			}

			IDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository()
					.initUserDescriptor(metadata, userCatalogue);
			controller.getRootPresenter().showUserRelatedMetadata(descriptor, GeneralJavaScriptObject.createJsonObject(userCatalogueJSON), shouldBeInTablePanel);

		} catch (Exception ex) {
			Log.error(ex.getMessage(), ex);
		}
	}
	
	public void removeOverlay(String overlayName, JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_clearFootprintsOverlay, overlayName);
		GeneralEntityInterface ent = EntityRepository.getInstance().getEntity(overlayName);
		if (ent != null) {
			ent.clearAll();
			EntityRepository.getInstance().removeEntity(ent);
		}
		else{
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("No overlay with name: " + overlayName + " active:\n Check getActiveOverlays() for available overlays"));
			sendBackToWidget(null, callbackMessage, widget);
			
		}
	}
	
	public void removeAllOverlays(JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_clearFootprintsOverlay, "all");
		
		for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
			ent.getTablePanel().closeTablePanel();
		}
	}

	
	public void setOverlayColor(String overlayName, String color,  JavaScriptObject widget) {
		GeneralEntityInterface entity = EntityRepository.getInstance().getEntity(overlayName);
		if(entity != null) {
			entity.setPrimaryColor(color);
		}else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("No overlay with name: " + overlayName + " active:\n Check getActiveOverlays() for available overlays"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}

	public void setOverlaySize(String overlayName, double size,  JavaScriptObject widget) {
		GeneralEntityInterface entity = EntityRepository.getInstance().getEntity(overlayName);
		if(entity != null) {
			entity.setSizeRatio(size);
		}else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("No overlay with name: " + overlayName + " active:\n Check getActiveOverlays() for available overlays"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}

	public void setOverlayShape(String overlayName, String shape, JavaScriptObject widget) {
		GeneralEntityInterface entity = EntityRepository.getInstance().getEntity(overlayName);
		if(entity != null) {
			
			LinkedList<String> sourceTypes = new LinkedList<String>();
			boolean found = false;
			for(SourceShapeType s : SourceShapeType.values()) {
				if(shape.equalsIgnoreCase(s.getName())) {
					found = true;
				}else {
					sourceTypes.add(s.getName());
				}
			}
			if(found) {
				entity.setShapeType(shape);
			}else {
				JSONObject callbackMessage = new JSONObject();
				callbackMessage.put("message", new JSONString("No such shape possible. \n Available shapes are " + String.join(",", sourceTypes.toArray(new String[0]))));
				sendBackToWidget(null, callbackMessage, widget);
			}
		}else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("No overlay with name: " + overlayName + " active:\n Check getActiveOverlays() for available overlays"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}
	
	public void getActiveOverlays(JavaScriptObject widget) {
		List<GeneralEntityInterface> list = EntityRepository.getInstance().getAllEntities();
	
		JSONArray arr = new JSONArray();
		for(GeneralEntityInterface ent : list) {
			arr.set(arr.size(),new JSONString(ent.getEsaSkyUniqId()));
		}
		JSONObject result = new JSONObject();
		result.put("Overlays", arr);
		sendBackToWidget(result, widget);
	}
}
