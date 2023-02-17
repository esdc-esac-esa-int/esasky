package esac.archive.esasky.cl.web.client.api;


import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import esac.archive.esasky.cl.web.client.Controller;
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
import esac.archive.esasky.ifcs.model.descriptor.*;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.ifcs.model.shared.contentdescriptors.UCD;

public class ApiOverlay extends ApiBase{
	
	public interface FootprintsSetMapper extends ObjectMapper<FootprintListJSONWrapper> {}
	public interface CatalogueMapper extends ObjectMapper<SourceListJSONWrapper> {}


	
	public ApiOverlay(Controller controller) {
		this.controller = controller;
	}
	
	public void selectShape(String overlayName, String shapeName, JavaScriptObject widget) {
		GeneralEntityInterface ent = EntityRepository.getInstance().getEntityByName(overlayName);
		if (ent != null) {
			ent.selectShapes(shapeName);
		}
	}

	public void deselectShape(String overlayName, String shapeName, JavaScriptObject widget) {
		GeneralEntityInterface ent = EntityRepository.getInstance().getEntityByName(overlayName);
		if (ent != null) {
			ent.deselectShapes(shapeName);
		}
	}

	public void deselectAllShapes(String overlayName, JavaScriptObject widget) {
		
		if(overlayName != null && !"".equals(overlayName)) {
			GeneralEntityInterface ent = EntityRepository.getInstance().getEntityByName(overlayName);
			if (ent != null) {
				ent.deselectAllShapes();
			}
		}else {
			for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
				ent.deselectAllShapes();
			}
		}
	}
	public void overlayFootprints(String footprintsSetJSON, boolean shouldBeInTablePanel) {

		FootprintsSetMapper mapper = GWT.create(FootprintsSetMapper.class);

		try {
			FootprintListJSONWrapper footprintsSet = mapper.read(footprintsSetJSON);

			GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_OVERLAYFOOTPRINTSWITHDETAILS, footprintsSet.getOverlaySet().getOverlayName());

			List<TapMetadataDescriptor> metadata = new LinkedList<>();

			TapMetadataDescriptor mName = new TapMetadataDescriptor();
			mName.setName(ApiConstants.OBS_NAME);
			mName.setDataType(ColumnType.CHAR.getName());
			mName.setUcd(UCD.META_ID.getValue());
			mName.setArraySize("*");
			mName.setPrincipal(true);
			metadata.add(mName);

			TapMetadataDescriptor mStcs = new TapMetadataDescriptor();
			mStcs.setName(ApiConstants.FOOTPRINT_STCS);
			mStcs.setDataType(ColumnType.CHAR.getName());
			mName.setArraySize("*");
			mStcs.setPrincipal(false);
			mStcs.setUcd(UCD.POS_OUTLINE.getValue());
			metadata.add(mStcs);

			TapMetadataDescriptor mRa = new TapMetadataDescriptor();
			mRa.setName(ApiConstants.CENTER_RA_DEG);
			mRa.setDataType(ColumnType.DOUBLE.getName());
			mRa.setUcd(UCD.POS_EQ_RA.getValue());
			mRa.setPrincipal(false);
			metadata.add(mRa);

			TapMetadataDescriptor mDec = new TapMetadataDescriptor();
			mDec.setName(ApiConstants.CENTER_DEC_DEG);
			mDec.setDataType(ColumnType.DOUBLE.getName());
			mDec.setUcd(UCD.POS_EQ_DEC.getValue());
			mDec.setPrincipal(false);
			metadata.add(mDec);

			FootprintListOverlay fooprintList = (FootprintListOverlay) footprintsSet.getOverlaySet();

			GeneralSkyObject generalSkyObject = fooprintList.getSkyObjectList().get(0);

			for (MetadataAPI currMetadata : generalSkyObject.getData()) {
				TapMetadataDescriptor m = new TapMetadataDescriptor();
				m.setName(currMetadata.getName());
				m.setDataType(ColumnType.CHAR.getName());
				m.setArraySize("*");
				m.setPrincipal(true);
				metadata.add(m);
			}

			CommonTapDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository()
					.initUserDescriptor(metadata, footprintsSet, generalSkyObject);

			controller.getRootPresenter().showUserRelatedMetadata(descriptor, GeneralJavaScriptObject.createJsonObject(footprintsSetJSON), shouldBeInTablePanel);

			AladinLiteWrapper.getAladinLite().goToRaDec(
					fooprintList.getSkyObjectList().get(0).getRa_deg(),
					fooprintList.getSkyObjectList().get(0).getDec_deg());
		} catch (Exception ex) {
			Log.error(ex.getMessage(), ex);
		}

	}

	public void overlayCatalogue(String userCatalogueJSON, boolean shouldBeInTablePanel) {


		CatalogueMapper mapper = GWT.create(CatalogueMapper.class);
		try {
			
			userCatalogueJSON = userCatalogueJSON.replace("\"ra\":", "\"ra_deg\":");
			userCatalogueJSON = userCatalogueJSON.replace("\"dec\":", "\"dec_deg\":");
			SourceListJSONWrapper userCatalogue = mapper.read(userCatalogueJSON);
			GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_OVERLAYCATALOGUEWITHDETAILS, userCatalogue.getOverlaySet().getOverlayName());

			List<TapMetadataDescriptor> metadata = new LinkedList<>();

			TapMetadataDescriptor mName = new TapMetadataDescriptor();
			mName.setName(ApiConstants.CAT_NAME);
			mName.setUcd(UCD.META_ID.getValue());
			mName.setDataType(ColumnType.CHAR.getName());
			mName.setArraySize("*");
			mName.setPrincipal(true);
			metadata.add(mName);

			TapMetadataDescriptor mRa = new TapMetadataDescriptor();
			mRa.setName(ApiConstants.CENTER_RA_DEG);
			mRa.setUcd(UCD.POS_EQ_RA.getValue());
			mRa.setDataType(ColumnType.DOUBLE.getName());
			mRa.setPrincipal(true);
			metadata.add(mRa);

			TapMetadataDescriptor mDec = new TapMetadataDescriptor();
			mDec.setName(ApiConstants.CENTER_DEC_DEG);
			mDec.setUcd(UCD.POS_EQ_DEC.getValue());
			mDec.setDataType(ColumnType.DOUBLE.getName());
			mDec.setPrincipal(true);
			metadata.add(mDec);

			SourceListOverlay sourceList = (SourceListOverlay) userCatalogue.getOverlaySet();
			GeneralSkyObject generalSkyObject = sourceList.getSkyObjectList().get(0);

			for (MetadataAPI currMetadata : generalSkyObject.getData()) {
				TapMetadataDescriptor m = new TapMetadataDescriptor();
				m.setName(currMetadata.getName());
				m.setDataType(ColumnType.CHAR.getName());
				m.setArraySize("*");
				m.setPrincipal(true);
				metadata.add(m);
			}

			CommonTapDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository()
					.initUserDescriptor(metadata, userCatalogue, generalSkyObject);
			controller.getRootPresenter().showUserRelatedMetadata(descriptor, GeneralJavaScriptObject.createJsonObject(userCatalogueJSON), shouldBeInTablePanel);

		} catch (Exception ex) {
			Log.error(ex.getMessage(), ex);
		}
	}
	
	public void removeOverlay(String overlayName, JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_CLEARFOOTPRINTSOVERLAY, overlayName);
		GeneralEntityInterface ent = EntityRepository.getInstance().getEntityByName(overlayName);
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
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_CLEARFOOTPRINTSOVERLAY, "all");
		
		for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
			if(ent.getTablePanel() != null) {
				ent.getTablePanel().closeTablePanel();
			}else {
				ent.clearAll();
				EntityRepository.getInstance().removeEntity(ent);
			}
		}
		
	}

	
	public void setOverlayColor(String overlayName, String color,  JavaScriptObject widget) {
		GeneralEntityInterface entity = EntityRepository.getInstance().getEntityByName(overlayName);
		if(entity != null) {
			entity.setPrimaryColor(color);
		}else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("No overlay with name: " + overlayName + " active:\n Check getActiveOverlays() for available overlays"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}

	public void setOverlaySize(String overlayName, double size,  JavaScriptObject widget) {
		GeneralEntityInterface entity = EntityRepository.getInstance().getEntityByName(overlayName);
		if(entity != null) {
			entity.setSizeRatio(size);
		}else {
			JSONObject callbackMessage = new JSONObject();
			callbackMessage.put("message", new JSONString("No overlay with name: " + overlayName + " active:\n Check getActiveOverlays() for available overlays"));
			sendBackToWidget(null, callbackMessage, widget);
		}
	}

	public void setOverlayShape(String overlayName, String shape, JavaScriptObject widget) {
		GeneralEntityInterface entity = EntityRepository.getInstance().getEntityByName(overlayName);
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
			arr.set(arr.size(),new JSONString(ent.getId()));
		}
		JSONObject result = new JSONObject();
		result.put("Overlays", arr);
		sendBackValuesToWidget(result, widget);
	}
}
