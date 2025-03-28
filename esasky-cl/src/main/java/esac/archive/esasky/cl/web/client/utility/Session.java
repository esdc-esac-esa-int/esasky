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

package esac.archive.esasky.cl.web.client.utility;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FileUpload;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.CoordinatesObject;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.AddTableEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.login.UserTablePanel;
import esac.archive.esasky.cl.web.client.model.DescriptorCountAdapter;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.entities.*;
import esac.archive.esasky.cl.web.client.presenter.MainPresenter;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.utility.exceptions.SaveStateException;
import esac.archive.esasky.cl.web.client.view.ImageConfigPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.GwPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.GwPanel.TabIndex;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.FutureFootprintRow;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.PlanObservationPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SkyRow;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TimeSeriesPanel;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.TapMetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColorPalette;

import java.util.*;

public class Session {
	
	public void saveState() {
		
		JSONObject stateObj = saveStateAsObj();

		StringBuilder fileNameBuilder = new StringBuilder("esasky_session_");
		Date date = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMdd_HHmmss");
		fileNameBuilder.append(dtf.format(date, TimeZone.createTimeZone(0)));

		fileNameBuilder.append(".json");
		writeToFile(stateObj.toString(), fileNameBuilder.toString());
		
	}
	
	public JSONObject saveStateAsObj() {
		JSONObject stateObj = new JSONObject();

		stateObj.put(EsaSkyWebConstants.SESSION_LOCATION, getLocationJson());
		stateObj.put(EsaSkyWebConstants.SESSION_HIPS, getHipsJson());
		stateObj.put(EsaSkyWebConstants.SESSION_DATA, getDataJson());
		JSONObject mme = getMmeJson();
		if(mme != null) {
			stateObj.put(EsaSkyWebConstants.SESSION_MME, mme);
		}
		JSONObject outreachObj = getOutreachImageJson();
		if(outreachObj != null) {
			stateObj.put(EsaSkyWebConstants.SESSION_OUTREACH, outreachObj);
		}
		
		stateObj.put(EsaSkyWebConstants.SESSION_PLANNING, getPlanningJson());
		stateObj.put(EsaSkyWebConstants.SESSION_PUB, getPublicationJson());
		stateObj.put(EsaSkyWebConstants.SESSION_SETTINGS, getSettingsJson());
		stateObj.put(EsaSkyWebConstants.SESSION_TREEMAP, getTreemapJson());
		stateObj.put(EsaSkyWebConstants.SESSION_EXTERNAL_DATA_CENTERS, getExternalDatacentersJson());
		JSONObject timeViewerObj = getTimeViewerJson();
		if (timeViewerObj != null) {
			stateObj.put(EsaSkyWebConstants.SESSION_TIME_VIEWER, timeViewerObj);
		}
		
		return stateObj;
	}

	public void restoreState() {
		FileUpload upload = new FileUpload();
		addFileUploadHandler(this, upload.getElement());
		upload.click();
	}
	
	public void restoreState(String jsonString) {
		GeneralJavaScriptObject saveStateObj = GeneralJavaScriptObject.createJsonObject(jsonString);
		restoreState(saveStateObj);
	}
	
	public void restoreState(GeneralJavaScriptObject saveStateObj) {
		try {
			restoreSettings(saveStateObj);
			restoreLocation(saveStateObj);
			restoreData(saveStateObj);
			restoreHipsStack(saveStateObj);
			restoreMme(saveStateObj);
			restorOutreach(saveStateObj);
			restorePublications(saveStateObj);
			restorePlanning(saveStateObj);
			restoreSettings(saveStateObj);
			restoreTreemap(saveStateObj);
			restoreExternalDataCenters(saveStateObj);
			restoreTimeViewer(saveStateObj);
		} catch (SaveStateException e) {
			Log.error(e.getMessage(), e);
		}
	}
	
	private JSONObject getTreemapJson() {
		JSONObject treemapObj = new JSONObject();
		Map<String, Double[]> sliderMap = MainPresenter.getInstance().getCtrlTBPresenter().getSliderValues();
		for(String key : sliderMap.keySet()) {
			JSONObject obj = new JSONObject();
			Double[] values = sliderMap.get(key);
			obj.put(EsaSkyWebConstants.SESSION_TREEMAP_LOW, new JSONString(values[0].toString()));
			obj.put(EsaSkyWebConstants.SESSION_TREEMAP_HIGH, new JSONString(values[1].toString()));
			treemapObj.put(key, obj);
		}
		
		return treemapObj;

	}
	
	
	private void restoreTreemap(GeneralJavaScriptObject saveStateObj) {
		if(saveStateObj.hasProperty(EsaSkyWebConstants.SESSION_TREEMAP)) {
			GeneralJavaScriptObject treemapObj = saveStateObj.getProperty(EsaSkyWebConstants.SESSION_TREEMAP);
			Map<String, Double[]> map = new HashMap<>();
			for(String key : treemapObj.getPropertiesArray()) {
				double low = Double.parseDouble(treemapObj.getProperty(key).getStringProperty(EsaSkyWebConstants.SESSION_TREEMAP_LOW));
				double high = Double.parseDouble(treemapObj.getProperty(key).getStringProperty(EsaSkyWebConstants.SESSION_TREEMAP_HIGH));
				map.put(key, new Double[] {low, high});
			}
			MainPresenter.getInstance().getCtrlTBPresenter().setSliderValues(map);
		}
	}

	private void restoreExternalDataCenters(GeneralJavaScriptObject saveStateObj) {
		if (saveStateObj.hasProperty(EsaSkyWebConstants.SESSION_EXTERNAL_DATA_CENTERS)) {
			GeneralJavaScriptObject edcObject = saveStateObj.getProperty(EsaSkyWebConstants.SESSION_EXTERNAL_DATA_CENTERS);
			DescriptorRepository.CommonTapDescriptorListMapper mapper = GWT.create(DescriptorRepository.CommonTapDescriptorListMapper.class);
			CommonTapDescriptorList commonTapDescriptorList = mapper.read(edcObject.toJSONString());
			DescriptorCountAdapter dca = new DescriptorCountAdapter(commonTapDescriptorList, EsaSkyWebConstants.CATEGORY_EXTERNAL, null);
			DescriptorRepository.getInstance().setDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_EXTERNAL, dca);
			CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(null, true, EsaSkyWebConstants.CATEGORY_EXTERNAL));
		}
	}

	private void restoreTimeViewer(GeneralJavaScriptObject saveStateObj) {
		if (saveStateObj.hasProperty(EsaSkyWebConstants.SESSION_TIME_VIEWER)) {
			GeneralJavaScriptObject object = saveStateObj.getProperty(EsaSkyWebConstants.SESSION_TIME_VIEWER);
			for (String key : object.getPropertiesArray()) {
				GeneralJavaScriptObject panel = object.getProperty(key);
				for (GeneralJavaScriptObject dataEntry : GeneralJavaScriptObject.convertToArray(panel.getProperty("data"))) {
					String mission = dataEntry.getProperty("mission").toString();
					String id = dataEntry.getProperty("id").toString();
					String secondIdentifier = dataEntry.getProperty("secondIdentifier").toString();
					String ra = dataEntry.getStringProperty("ra");
					String dec = dataEntry.getStringProperty("dec");
					TimeSeriesPanel.toggleTimeSeriesData(mission, id, secondIdentifier, ra, dec);
				}
			}
		}
	}
	
	private JSONObject getSettingsJson() {
		JSONObject settingsObj = new JSONObject();
		boolean gridOn = MainPresenter.getInstance().getHeaderPresenter().getView().getGridButtonToggled();
		settingsObj.put(EsaSkyWebConstants.SESSION_SETTINGS_GRID, new JSONString(Boolean.toString(gridOn)));
		
		if(DescriptorRepository.getInstance().hasSearchArea()) {
			settingsObj.put(EsaSkyWebConstants.SESSION_SETTINGS_SEARCH, new JSONString(getSearchAreaStcs()));
		}
		return settingsObj;
	}
	
	private String getSearchAreaStcs() {
		SearchArea area = DescriptorRepository.getInstance().getSearchArea();
		String stcs = "";
		if(area.isCircle()){
			CoordinatesObject[] coors = area.getCoordinates();
			stcs = "CIRCLE ICRS " + coors[0].getRaDeg() + " " + coors[0].getDecDeg() + " " + area.getRadius();
		}else {
			StringBuilder sb = new StringBuilder("POLYGON ICRS ");
			for(CoordinatesObject cooObj : area.getCoordinates()) {
				sb.append(cooObj.getRaDeg());
				sb.append(" ");
				sb.append(cooObj.getDecDeg());
				sb.append(" ");
			}
			stcs = sb.toString().trim();
		}
		return stcs;
	}

	private JSONObject getExternalDatacentersJson() {
		List<CommonTapDescriptor> descriptors = DescriptorRepository.getInstance().getDescriptors(EsaSkyWebConstants.CATEGORY_EXTERNAL);
		DescriptorRepository.CommonTapDescriptorListMapper mapper = GWT.create(DescriptorRepository.CommonTapDescriptorListMapper.class);
		CommonTapDescriptorList commonTapDescriptorList = new CommonTapDescriptorList();
		commonTapDescriptorList.setDescriptors(descriptors);
		String jsonString = mapper.write(commonTapDescriptorList);
		return new JSONObject(JsonUtils.safeEval(jsonString));
	}

	private JSONObject getTimeViewerJson() {
		JSONObject obj = new JSONObject();
		TimeSeriesPanel cheops = TimeSeriesPanel.getTimeSeriesPanelOrNull("CHEOPS");
		if (cheops != null && cheops.isShowing()) {
			storeTimeSeriesPanelData(obj, "cheops", cheops);
		}
		TimeSeriesPanel allOther = TimeSeriesPanel.getTimeSeriesPanelOrNull("GAIA");
		if (allOther != null && allOther.isShowing()) {
			storeTimeSeriesPanelData(obj, "others", allOther);
		}
		if (!obj.keySet().isEmpty()) {
			return obj;
		}
		return null;
	}

	private void storeTimeSeriesPanelData(JSONObject obj, String key, TimeSeriesPanel panel) {
		Set<String[]> data = panel.getCurrentData();
		JSONArray dataArray = new JSONArray();
		for (String[] entry : data) {
			JSONObject jsonEntry = new JSONObject();
			jsonEntry.put("mission", new JSONString(entry[0]));
			jsonEntry.put("id", new JSONString(entry[1]));
			jsonEntry.put("secondIdentifier", new JSONString(entry[2]));
			dataArray.set(dataArray.size(), jsonEntry);
		}
		JSONObject panelSettings = new JSONObject();
		panelSettings.put("data", dataArray);
		obj.put(key, panelSettings);
	}

	private void restoreSettings(GeneralJavaScriptObject saveStateObj) {
		GeneralJavaScriptObject settingsObj = saveStateObj.getProperty(EsaSkyWebConstants.SESSION_SETTINGS);
		if(settingsObj.hasProperty(EsaSkyWebConstants.SESSION_SETTINGS_GRID)) {
			boolean grid = Boolean.parseBoolean(settingsObj.getStringProperty(EsaSkyWebConstants.SESSION_SETTINGS_GRID));
			AladinLiteWrapper.getInstance().toggleGrid(grid);
		}
		if(settingsObj.hasProperty(EsaSkyWebConstants.SESSION_SETTINGS_SEARCH)) {
			String searchStcs =settingsObj.getStringProperty(EsaSkyWebConstants.SESSION_SETTINGS_SEARCH);
			AladinLiteWrapper.getAladinLite().createSearchArea(searchStcs);
		} else {
			AladinLiteWrapper.getAladinLite().clearSearchArea();
		}
	}
	
	private JSONArray getPublicationJson() {
		JSONArray array = new JSONArray();
		for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
			if(ent.getNumberOfShapes() < 1 || ent.getShape(0) == null) {
				continue;
			}
			if(ent instanceof PublicationsEntity) {
				JSONObject pubObj = new JSONObject();
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_TYPE, new JSONString(EsaSkyWebConstants.SESSION_PUB_TYPE_AREA));
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_URL, new JSONString(ent.getQuery()));
				array.set(array.size(), pubObj);
			}else if (ent instanceof PublicationsByAuthorEntity) {
				JSONObject pubObj = new JSONObject();
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_TYPE, new JSONString(EsaSkyWebConstants.SESSION_PUB_TYPE_AUTHOR));
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_AUTHOR, new JSONString(ent.getId()));
				array.set(array.size(), pubObj);
			
			}else if (ent instanceof PublicationsBySourceEntity) {
				PublicationsBySourceEntity pubEnt = (PublicationsBySourceEntity) ent;
				Shape shape = pubEnt.getShape(0);
				JSONObject pubObj = new JSONObject();
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_TYPE, new JSONString(EsaSkyWebConstants.SESSION_PUB_TYPE_SOURCE));
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_SOURCE, new JSONString(ent.getId()));
				pubObj.put(EsaSkyWebConstants.SESSION_RA, new JSONString(shape.getRa()));
				pubObj.put(EsaSkyWebConstants.SESSION_DEC, new JSONString(shape.getDec()));
				String bibcount = shape.getJsObject().getProperty("data").getStringProperty(EsaSkyWebConstants.SESSION_PUB_BIBCOUNT);
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_BIBCOUNT, new JSONString(bibcount));
				array.set(array.size(), pubObj);
				
			}
		}
		return array;
	}
	
	private void restorePublications(GeneralJavaScriptObject saveStateObj) {
		GeneralJavaScriptObject[] array = GeneralJavaScriptObject.convertToArray(saveStateObj.getProperty(EsaSkyWebConstants.SESSION_PUB));
		for(GeneralJavaScriptObject pubObj : array) {
			if(EsaSkyWebConstants.SESSION_PUB_TYPE_AREA.equals(pubObj.getStringProperty(EsaSkyWebConstants.SESSION_PUB_TYPE))) {
				String url = pubObj.getStringProperty(EsaSkyWebConstants.SESSION_PUB_URL);
				MainPresenter.getInstance().getCtrlTBPresenter().getPublicationPresenter().getPublications(url);

			}else if(EsaSkyWebConstants.SESSION_PUB_TYPE_AUTHOR.equals(pubObj.getStringProperty(EsaSkyWebConstants.SESSION_PUB_TYPE))) {
				String author = pubObj.getStringProperty(EsaSkyWebConstants.SESSION_PUB_AUTHOR);
				MainPresenter.getInstance().loadOrQueueAuthorInformationFromSimbad(author);
			
			}else if(EsaSkyWebConstants.SESSION_PUB_TYPE_SOURCE.equals(pubObj.getStringProperty(EsaSkyWebConstants.SESSION_PUB_TYPE))) {
				String source = pubObj.getStringProperty(EsaSkyWebConstants.SESSION_PUB_SOURCE);
				double ra = Double.parseDouble(pubObj.getStringProperty(EsaSkyWebConstants.SESSION_RA));
				double dec = Double.parseDouble(pubObj.getStringProperty(EsaSkyWebConstants.SESSION_DEC));
				String bibcount = pubObj.getStringProperty(EsaSkyWebConstants.SESSION_PUB_BIBCOUNT);
	            GeneralEntityInterface entity = EntityRepository.getInstance().createPublicationsBySourceEntity(source, ra, dec, bibcount);
	            CommonEventBus.getEventBus().fireEvent(new AddTableEvent(entity));
			}
		}
	}
	
	private JSONArray getPlanningJson() {
		JSONArray array = new JSONArray();
		for(FutureFootprintRow row :  PlanObservationPanel.getInstance().getAllRows()) {
			JSONObject rowObj = new JSONObject();
			rowObj.put(EsaSkyWebConstants.SESSION_RA, new JSONString(new Double(row.getCenterRaDeg()).toString()));
			rowObj.put(EsaSkyWebConstants.SESSION_DEC, new JSONString(new Double(row.getCenterDecDeg()).toString()));
			rowObj.put(EsaSkyWebConstants.SESSION_ROT, new JSONString(row.getRotationDeg().toString()));
			rowObj.put(EsaSkyWebConstants.SESSION_PLANNING_APERTURE, new JSONString(row.getAperture()));
			rowObj.put(EsaSkyWebConstants.SESSION_PLANNING_INSTRUMENT, new JSONString(row.getInstrument().getInstrumentName()));
			rowObj.put(EsaSkyWebConstants.SESSION_PLANNING_ALL, new JSONString(new Boolean(row.getIsAllInstrumentsSelected()).toString()));
			rowObj.put(EsaSkyWebConstants.SESSION_PLANNING_MISSION, new JSONString(row.getInstrument().getMission().getMissionName()));
			array.set(array.size(), rowObj);
		}
		return array;
	}
	
	private void restorePlanning(GeneralJavaScriptObject saveStateObj) {
		GeneralJavaScriptObject[] array = GeneralJavaScriptObject.convertToArray(saveStateObj.getProperty(EsaSkyWebConstants.SESSION_PLANNING));
		
		for(GeneralJavaScriptObject planningObj : array) {
			String ra = planningObj.getStringProperty(EsaSkyWebConstants.SESSION_RA);
			String dec = planningObj.getStringProperty(EsaSkyWebConstants.SESSION_DEC);
			String rot = planningObj.getStringProperty(EsaSkyWebConstants.SESSION_ROT);
			String aperture = planningObj.getStringProperty(EsaSkyWebConstants.SESSION_PLANNING_APERTURE);
			String instrument = planningObj.getStringProperty(EsaSkyWebConstants.SESSION_PLANNING_INSTRUMENT);
			String mission = planningObj.getStringProperty(EsaSkyWebConstants.SESSION_PLANNING_MISSION);
			boolean all = Boolean.parseBoolean(planningObj.getStringProperty(EsaSkyWebConstants.SESSION_PLANNING_ALL));
			PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
			planObservationPanel.addInstrumentRowWithCoordinatesAPI(mission, instrument, aperture, all, ra, dec, rot);
		}
	}
	
	private JSONObject getOutreachImageJson() {
		JSONObject outreachObj = null;
		for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
			if(ent instanceof ImageListEntity) {
				ImageListEntity imageEnt = (ImageListEntity) ent;
				if(ent.getTablePanel() != null) {
					GeneralJavaScriptObject[] rows = imageEnt.getTablePanel().getSelectedRows();
					if(rows.length > 0 ) {
	                    String id = rows[0].getStringProperty(ImageListEntity.IDENTIFIER_KEY);
	                    String opacity = new Double(imageEnt.getOpacity()).toString();
	                    String footprintsShowing = new Boolean(!imageEnt.isHidingShapes()).toString();
	                    String panelOpen = new Boolean(!imageEnt.getIsPanelClosed()).toString();

	                    outreachObj = new JSONObject();
	                    outreachObj.put(EsaSkyWebConstants.SESSION_OUTREACH_IMAGE_ID, new JSONString(id));
	                    outreachObj.put(EsaSkyWebConstants.SESSION_OUTREACH_IMAGE_OPACITY, new JSONString(opacity));
	                    outreachObj.put(EsaSkyWebConstants.SESSION_OUTREACH_IMAGE_FOOTPRINT_SHOWING, new JSONString(footprintsShowing));
	                    outreachObj.put(EsaSkyWebConstants.SESSION_OUTREACH_IMAGE_PANEL_OPEN, new JSONString(panelOpen));
						outreachObj.put(EsaSkyWebConstants.SESSION_OUTREACH_IMAGE_TELESCOPE, new JSONString(imageEnt.getDescriptor().getMission()));
						break;
					}
				}
			}
		}
		
		return outreachObj;
	}
	

	private void restorOutreach(GeneralJavaScriptObject saveStateObj) {
		GeneralJavaScriptObject outreachObj = saveStateObj.getProperty(EsaSkyWebConstants.SESSION_OUTREACH);
		if(outreachObj == null) {
			return;
		}
		
		if(outreachObj.hasProperty(EsaSkyWebConstants.SESSION_OUTREACH_IMAGE_ID)) {
			String id = outreachObj.getStringProperty(EsaSkyWebConstants.SESSION_OUTREACH_IMAGE_ID);
			String telescope = outreachObj.getStringProperty(EsaSkyWebConstants.SESSION_OUTREACH_IMAGE_TELESCOPE);
			double opacity = Double.parseDouble(outreachObj.getStringProperty(EsaSkyWebConstants.SESSION_OUTREACH_IMAGE_OPACITY));
			boolean showFootPrints = Boolean.parseBoolean(outreachObj.getStringProperty(EsaSkyWebConstants.SESSION_OUTREACH_IMAGE_FOOTPRINT_SHOWING));
			boolean showPanel = Boolean.parseBoolean(outreachObj.getStringProperty(EsaSkyWebConstants.SESSION_OUTREACH_IMAGE_PANEL_OPEN));
			MainPresenter.getInstance().getCtrlTBPresenter().showOutreachImage(id, telescope);
			for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
				if(ent instanceof ImageListEntity) {
					ImageListEntity imageEnt = (ImageListEntity) ent;
					imageEnt.setIsHidingShapes(!showFootPrints);
					imageEnt.setOpacity(opacity);
					if(!showPanel) {
						MainPresenter.getInstance().getCtrlTBPresenter().closeOutreachPanel();
					}
					break;
				}
			}
		}
	}
	
	
	private JSONObject getMmeJson() {
		JSONObject mmeObj = new JSONObject();
		for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
			
			if(Objects.equals(ent.getDescriptor().getTableName(), "alerts.mv_v_gravitational_waves_fdw")) {
				if(ent.getTablePanel() != null) {
					GeneralJavaScriptObject[] rows = ent.getTablePanel().getSelectedRows();
					if(rows.length > 0 ) {
	                    String id = rows[0].getStringProperty(GwPanel.GRACE_ID);
	                    mmeObj.put(EsaSkyWebConstants.SESSION_GW_ID, new JSONString(id));
					}
				}
			}
			else if(Objects.equals(ent.getDescriptor().getTableName(), "alerts.mv_v_icecube_event_fdw")) {
				 int size = ent.getNumberOfShapes();
				 if(size > 0) {
					JSONObject icecubeObj = new JSONObject();
					icecubeObj.put(EsaSkyWebConstants.SESSION_SHOWING, new JSONString("true"));
					icecubeObj.put(EsaSkyWebConstants.SESSION_DATA_FILTERS, new JSONString(ent.getTablePanel().getFilterString()));
					mmeObj.put(EsaSkyWebConstants.SESSION_ICECUBE, icecubeObj);
				 }
			}
		}
		if(!mmeObj.keySet().isEmpty()) {
			return mmeObj;
		}
		return null;
	}
	
	private void restoreMme(GeneralJavaScriptObject saveStateObj) {
		GeneralJavaScriptObject mmeObj = saveStateObj.getProperty(EsaSkyWebConstants.SESSION_MME);
		if(mmeObj == null) {
			return;
		}
		
		if(mmeObj.hasProperty(EsaSkyWebConstants.SESSION_GW_ID)) {
			String id = mmeObj.getStringProperty(EsaSkyWebConstants.SESSION_GW_ID);
			MainPresenter.getInstance().getCtrlTBPresenter().showGWEvent(id);
		}
		if(mmeObj.hasProperty(EsaSkyWebConstants.SESSION_ICECUBE)) {
			GeneralJavaScriptObject icecubeObj = mmeObj.getProperty(EsaSkyWebConstants.SESSION_ICECUBE);
			if(Boolean.parseBoolean(icecubeObj.getStringProperty(EsaSkyWebConstants.SESSION_SHOWING))) {
				MainPresenter.getInstance().getCtrlTBPresenter().openGWPanel(TabIndex.NEUTRINO.ordinal());
			}
		}
	}
	

	private void restoreLocation(GeneralJavaScriptObject obj) throws SaveStateException {
		try {
			GeneralJavaScriptObject loc = obj.getProperty(EsaSkyWebConstants.SESSION_LOCATION);
			double ra = loc.getDoubleProperty(EsaSkyWebConstants.SESSION_RA);
			double dec = loc.getDoubleProperty(EsaSkyWebConstants.SESSION_DEC);
			double fov = loc.getDoubleProperty(EsaSkyWebConstants.SESSION_FOV);
			String cooFrame = loc.getStringProperty(EsaSkyWebConstants.SESSION_FRAME);
			String projection = loc.getStringProperty(EsaSkyWebConstants.SESSION_PROJECTION);
			
			if(cooFrame.equalsIgnoreCase(EsaSkyWebConstants.ALADIN_J2000_COOFRAME)) {
				AladinLiteWrapper.getInstance().setCooFrame(AladinLiteConstants.CoordinateFrame.J2000);
				MainPresenter.getInstance().getHeaderPresenter().getView().selectCoordinateFrame(0);
			}else if (cooFrame.equalsIgnoreCase(EsaSkyWebConstants.ALADIN_GALACTIC_COOFRAME)){
				AladinLiteWrapper.getInstance().setCooFrame(AladinLiteConstants.CoordinateFrame.GALACTIC);
				MainPresenter.getInstance().getHeaderPresenter().getView().selectCoordinateFrame(1);
			}else {
				throw new SaveStateException("Wrong coordinateFrame: " + cooFrame);
			}
			
			AladinLiteWrapper.getInstance().goToObject(ra + " " + dec, false);
			AladinLiteWrapper.getAladinLite().setZoom(fov);

			if (projection != null) {
				AladinLiteWrapper.getAladinLite().setProjection(projection);
			}

			
		}catch (Exception e) {
			throw new SaveStateException(e.getMessage(), e);
		}
	}
	
	private void restoreHipsStack(GeneralJavaScriptObject saveStateObj) throws SaveStateException {

		// Clear stack
		SelectSkyPanel.getInstance().removeOtherSkies();

		GeneralJavaScriptObject hipsArrayObj = saveStateObj.getProperty(EsaSkyWebConstants.SESSION_HIPS);
		GeneralJavaScriptObject[] array = GeneralJavaScriptObject.convertToArray(hipsArrayObj.getProperty(EsaSkyWebConstants.SESSION_HIPS_ARRAY));

		boolean first = true;
		for(GeneralJavaScriptObject hipObj : array) {
			final String name = hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_NAME);
			final String colorPalette = hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_COLORPALETTE);
			final boolean reverse =  Boolean.parseBoolean(hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_REVERSE));
			final String stretch = hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_STRETCH);
			final double cutsLow = hipObj.getDoubleProperty(EsaSkyWebConstants.SESSION_HIPS_CUTS_LOW);
			final double cutsHigh = hipObj.getDoubleProperty(EsaSkyWebConstants.SESSION_HIPS_CUTS_HIGH);
			final double cutLimitLow = hipObj.getDoubleProperty(EsaSkyWebConstants.SESSION_HIPS_CUT_LIMIT_LOW);
			final double cutLimitHigh = hipObj.getDoubleProperty(EsaSkyWebConstants.SESSION_HIPS_CUT_LIMIT_HIGH);
			final boolean blending = Boolean.parseBoolean(hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_BLENDING));
			final double opacity = hipObj.getDoubleProperty(EsaSkyWebConstants.SESSION_HIPS_OPACITY);
			final String tileFormat = hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_FORMAT);




			String category = null;
			if (hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_CATEGORY) != null) {
				category = hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_CATEGORY);
			}
			
			boolean isDefaultHiPS = false;
			if (hipObj.getProperty(EsaSkyWebConstants.SESSION_HIPS_DEFAULT) != null) {
				isDefaultHiPS = Boolean.valueOf(hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_DEFAULT));
			}
			SkyRow skyRow;
			if(!first) {
				skyRow = SelectSkyPanel.getInstance().createSky(true);
			}else {
				skyRow = SelectSkyPanel.getSelectedSky();
				first = false;
			}
			if(!skyRow.setSelectHips(name,  false, category)) {
				//Means that we can't find it in the list
				//Setting from url instead
				String url = hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_URL);
				addUrlHips(url, colorPalette, skyRow, category, isDefaultHiPS);
			}

			// We need to wait for aladin to change the layer. It would be good to have an event for this
			// but there is no event that would work for multiple layers atm...
			final SkyRow skyRow1 = skyRow;
			Timer timer = new Timer() {

				@Override
				public void run() {
					ImageConfigPanel configPanel = skyRow1.getImageConfigPanel();
					if (configPanel != null) {
						configPanel.discoverLayer(skyRow1.getRowId());
						configPanel.setTileFormat(tileFormat);
						configPanel.setDefaultColorPallette(ColorPalette.valueOf(colorPalette));
						configPanel.setReversed(reverse);
						configPanel.setStretch(stretch);
						configPanel.setBlending(blending);
						configPanel.setOpacity(opacity);
					}
				}

			};

			timer.schedule(2000);


		}
		double sliderValue = hipsArrayObj.getDoubleProperty(EsaSkyWebConstants.SESSION_HIPS_SLIDER);
		SelectSkyPanel.getInstance().setSliderValue(sliderValue);
		
		Timer timer = new Timer() {
			
			@Override
			public void run() {
				SelectSkyPanel.getInstance().setSliderValue(sliderValue);
			}
			
		};
		//Adds setting the Hips with a timer to allow the url hips to load
		timer.schedule(5000);

	}

	private void addUrlHips(String url, String colorPalette, final SkyRow skyRow, String category, boolean isDefault) {
		HipsParser parser = new HipsParser(new HipsParserObserver() {
			
			@Override
			public void onSuccess(HiPS hips) {
				hips.setHipsWavelength(category != null ? category : HipsWavelength.USER);
				hips.setHipsCategory(category != null ? category : HipsWavelength.USER);
				skyRow.setHiPSFromAPI(hips, true);
				skyRow.setColorPalette(ColorPalette.valueOf(colorPalette));

				if(isDefault) {
					skyRow.disableDeleteButton();
				}
				
			}
			
			@Override
			public void onError(String errorMsg) {
				Log.error(errorMsg);
			}
		});
		parser.loadProperties(url);
	}
	
	public native void addFileUploadHandler(Session instance, JavaScriptObject element)/*-{
		element.addEventListener("change", function(event) {
			var file = event.target.files[0];
			file.text().then(function(text){ 
				instance.@esac.archive.esasky.cl.web.client.utility.Session::restoreState(Ljava/lang/String;)(text);
			})
		}, false);
	}-*/;
	
	private native void writeToFile(String data, String filename) /*-{
		var element = document.createElement('a')
		var blob = new Blob([data], { type: "json" })
		var filename = filename;

		if (blob) {

			element.setAttribute('href', window.URL.createObjectURL(blob));

			//set file title
			element.setAttribute('download', filename);

			//trigger download
			element.style.display = 'none';
			document.body.appendChild(element);
			element.click();

			//remove temporary link element
			document.body.removeChild(element);
		}
	}-*/;
	
	
	private boolean checkIfSpecialEntity(GeneralEntityInterface ent) {
		return Objects.equals(ent.getDescriptor().getSchemaName(), "alerts")
				|| Objects.equals(ent.getDescriptor().getSchemaName(), "public")
				|| ent instanceof ImageListEntity;
	}
	
	private JSONArray getDataJson() {
		JSONArray entArray = new JSONArray();
		for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
			
			if(checkIfSpecialEntity(ent)) {
				continue;
			}
			
			JSONObject entObj = new JSONObject();
			entObj.put(EsaSkyWebConstants.SESSION_DATA_MISSION, new JSONString(ent.getDescriptor().getMission()));
			entObj.put(EsaSkyWebConstants.SESSION_DATA_TABLE,new JSONString(ent.getDescriptor().getTableName()));
			entObj.put(EsaSkyWebConstants.SESSION_DATA_CATEGORY,new JSONString(ent.getDescriptor().getCategory()));

			String isMoc = "False";
			if(ent instanceof EsaSkyEntity && ((EsaSkyEntity) ent).getMocEntity() != null
					&& ((EsaSkyEntity) ent).getMocEntity().isShouldBeShown()){
				isMoc = "True";
			}
			
			entObj.put(EsaSkyWebConstants.SESSION_DATA_ISMOC,new JSONString(isMoc));
	
			if(ent.getTablePanel() != null) {
				entObj.put(EsaSkyWebConstants.SESSION_DATA_HAS_PANEL,new JSONString("True"));
			}else {
				entObj.put(EsaSkyWebConstants.SESSION_DATA_HAS_PANEL,new JSONString("False"));
			}

			String query = ent.getDescriptor().getUnprocessedADQL() != null ? ent.getDescriptor().getUnprocessedADQL() : ent.getQuery();
			entObj.put(EsaSkyWebConstants.SESSION_DATA_ADQL,new JSONString(query));
			entObj.put(EsaSkyWebConstants.SESSION_DATA_FILTERS, new JSONString(ent.getTablePanel().getFilterString()));

			putIfNonNull(entObj, EsaSkyWebConstants.SESSION_DATA_BULK_DOWNLOAD_URL, ent.getDescriptor().getBulkDownloadUrl());
			putIfNonNull(entObj, EsaSkyWebConstants.SESSION_DATA_BULK_DOWNLOAD_ID_COLUMN, ent.getDescriptor().getBulkDownloadIdColumn());

			entObj.put(EsaSkyWebConstants.SESSION_DATA_COLOR_MAIN, new JSONString(ent.getPrimaryColor()));
			entObj.put(EsaSkyWebConstants.SESSION_DATA_COLOR_SECOND, new JSONString(ent.getSecondaryColor()));
			entObj.put(EsaSkyWebConstants.SESSION_DATA_SIZE, new JSONString(new Double(ent.getSize()).toString()));
			putIfNonNull(entObj, EsaSkyWebConstants.SESSION_DATA_LINESTYLE, ent.getLineStyle());
			putIfNonNull(entObj, EsaSkyWebConstants.SESSION_DATA_SOURCE_STYLE, ent.getShapeType());

			entArray.set(entArray.size(), entObj);
		}
		return entArray;
	}

	private void putIfNonNull(JSONObject obj, String key, String value) {
		if (value != null) {
			obj.put(key, new JSONString(value));
		}
	}
	
	private void restoreData(GeneralJavaScriptObject saveStateObj) throws SaveStateException {

		// Clear current data
		Iterator<GeneralEntityInterface> entityIterator = EntityRepository.getInstance().getAllEntities().iterator();
		while (entityIterator.hasNext()) {
			GeneralEntityInterface ent = entityIterator.next();
			ITablePanel tablePanel = ent.getTablePanel();
			if (tablePanel != null) {
				tablePanel.closeTablePanel();
			}
		}
		MainPresenter.getInstance().getCtrlTBPresenter().closeAllPanels();

		// Load session data
		try {
			GeneralJavaScriptObject[] dataArray = GeneralJavaScriptObject.convertToArray(saveStateObj.getProperty(EsaSkyWebConstants.SESSION_DATA));
			for(GeneralJavaScriptObject dataObj : dataArray) {
				String mission = dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_MISSION);
				String category = dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_CATEGORY);

				if (category.equals(EsaSkyWebConstants.CATEGORY_EXTERNAL)) {
					restoreExternalDescriptor(dataObj);
				} else {
					String adql = dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_ADQL);
					boolean isMoc = Boolean.parseBoolean(dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_ISMOC));

					CommonTapDescriptor desc = DescriptorRepository.getInstance().getDescriptorFromMission(category, mission);

					GeneralEntityInterface ent = EntityRepository.getInstance().createEntity(desc);
					MainPresenter.getInstance().getResultsPresenter().addResultsTab(ent);
					String filterString = dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_FILTERS);
					restoreFilters(ent, filterString);
					if(isMoc) {
						EsaSkyEntity esaSkyEntity = (EsaSkyEntity) ent;
						MOCEntity mocEntity = esaSkyEntity.createMocEntity();
						mocEntity.loadMOC(adql);
					}else {
						ent.fetchData(adql);
					}

					ent.setPrimaryColor(dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_COLOR_MAIN));
					ent.setSecondaryColor(dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_COLOR_SECOND));
					ent.setSizeRatio(dataObj.getDoubleProperty(EsaSkyWebConstants.SESSION_DATA_SIZE));
					if(dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_LINESTYLE) != null) {
						ent.setLineStyle(dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_LINESTYLE));
					}
					if(dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_SOURCE_STYLE) != null) {
						ent.setShapeType(dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_SOURCE_STYLE));
					}
				}
			}
		}catch (Exception e) {
			throw new SaveStateException(e.getMessage(), e);
		}
	}

	private void restoreExternalDescriptor(GeneralJavaScriptObject sessionData) {
		String table = sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_TABLE);
		String adql = sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_ADQL);
		JSONUtils.getJSONFromUrl(TAPUtils.getTAPQuery(adql, "json"), new JSONUtils.IJSONRequestCallback() {
			@Override
			public void onSuccess(String responseText) {
				GeneralJavaScriptObject obj = GeneralJavaScriptObject.createJsonObject(responseText);
				UserTablePanel.TapDescriptorListMapper mapper = GWT.create(UserTablePanel.TapDescriptorListMapper.class);
				TapDescriptorList descriptorList = mapper.read(responseText);
				if (descriptorList != null) {
					List<TapMetadataDescriptor> metadataDescriptorList = ExtTapUtils.getMetadataFromTapDescriptorList(descriptorList, false);
					CommonTapDescriptor commonTapDescriptor = DescriptorRepository.getInstance().createExternalDescriptor(metadataDescriptorList, EsaSkyWebConstants.TAP_CONTEXT + "/tap/sync",
							table, table, "user_description", adql, false, true);
					commonTapDescriptor.setColor(sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_COLOR_MAIN));
					commonTapDescriptor.setIsUserTable(true);
					if (sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_BULK_DOWNLOAD_URL) != null) {
						commonTapDescriptor.setBulkDownloadUrl(sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_BULK_DOWNLOAD_URL));
						commonTapDescriptor.setBulkDownloadIdColumn(sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_BULK_DOWNLOAD_ID_COLUMN));
					}

					GeneralEntityInterface entity = EntityRepository.getInstance().createEntity(commonTapDescriptor);
					MainPresenter.getInstance().getResultsPresenter().addResultsTab(entity);
					entity.insertExternalData(obj);
					entity.setQuery(adql);
					String filterString = sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_FILTERS);
					restoreFilters(entity, filterString);

					entity.setPrimaryColor(sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_COLOR_MAIN));
					entity.setSecondaryColor(sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_COLOR_SECOND));
					entity.setSizeRatio(sessionData.getDoubleProperty(EsaSkyWebConstants.SESSION_DATA_SIZE));
					if(sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_LINESTYLE) != null) {
						entity.setLineStyle(sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_LINESTYLE));
					}
					if(sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_SOURCE_STYLE) != null) {
						entity.setShapeType(sessionData.getStringProperty(EsaSkyWebConstants.SESSION_DATA_SOURCE_STYLE));
					}
				}

			}

			@Override
			public void onError(String errorCause) {

			}

		});


	}
	
	private void restoreFilters(GeneralEntityInterface ent, String filterString) {
		String[] andSplit = filterString.split("AND");
		Map<String, List<String>> filterMap = new HashMap<>(); 
		for(String s : andSplit) {
			String key = s.trim().split("\\s")[0];
			List<String> list = filterMap.get(key);
			if(list == null) {
				list = new LinkedList<>();
			}
			list.add(s);
			filterMap.put(key, list);
		}
		
		for(String key : filterMap.keySet()) {
			List<String> list = filterMap.get(key);
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i <list.size(); i++) {
				String s = list.get(i);
				sb.append(s);
				if(i < list.size() - 1) {
					sb.append(" AND ");
				}
				
			}
			ent.getTablePanel().addTapFilter(key, sb.toString());
		}

	}
	
	private JSONObject getLocationJson() {
		JSONObject obj = new JSONObject();

		String raDeg = Double.toString(AladinLiteWrapper.getCenterRaDeg());
		String decDeg = Double.toString(AladinLiteWrapper.getCenterDecDeg());
		String fov = Double.toString(AladinLiteWrapper.getAladinLite().getFovDeg());
		String cooFrame = AladinLiteWrapper.getCoordinatesFrame().toString();
		String projection = AladinLiteWrapper.getCurrentProjection();
		
		obj.put(EsaSkyWebConstants.SESSION_RA, new JSONString(raDeg));
		obj.put(EsaSkyWebConstants.SESSION_DEC, new JSONString(decDeg));
		obj.put(EsaSkyWebConstants.SESSION_FOV, new JSONString(fov));
		obj.put(EsaSkyWebConstants.SESSION_FRAME, new JSONString(cooFrame));
		obj.put(EsaSkyWebConstants.SESSION_PROJECTION, new JSONString(projection));
		
		return obj;
	}
	
	private JSONObject getHipsJson() {
		JSONArray hipsArray = new JSONArray();
		SelectSkyPanel panel = SelectSkyPanel.getInstance();
		for(SkyRow row : panel.getHipsList()) {
			JSONObject hipsObj = new JSONObject();
			HiPS hips = row.getSelectedHips();
			hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_NAME, new JSONString(hips.getSurveyName()));
			hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_URL, new JSONString(hips.getSurveyRootUrl()));

			try {
				ImageConfigPanel imgConfig = row.getImageConfigPanel();

				if (imgConfig != null) {
					hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_COLORPALETTE, new JSONString(imgConfig.getSelectedColorPalette().toString()));
					hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_REVERSE, new JSONString(Boolean.toString(imgConfig.getReversed())));
					hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_STRETCH, new JSONString(imgConfig.getStretch()));

					double[] cuts = imgConfig.getCuts();
					hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_CUTS_LOW, new JSONNumber(cuts[0]));
					hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_CUTS_HIGH, new JSONNumber(cuts[1]));

					double[] cutLimits = imgConfig.getCutLimits();
					hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_CUT_LIMIT_LOW, new JSONNumber(cutLimits[0]));
					hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_CUT_LIMIT_HIGH, new JSONNumber(cutLimits[1]));

					hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_BLENDING, new JSONString(Boolean.toString(imgConfig.getBlending())));
					hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_OPACITY, new JSONNumber(imgConfig.getOpacity()));
					hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_FORMAT, new JSONString(imgConfig.getTileFormat()));
				}
			} catch (Exception e) {
				Log.error(e.getMessage(), e);
			}


			if(hips.getHipsCategory() != null) {
				hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_CATEGORY, new JSONString(hips.getHipsCategory()));
			}
			if(hips.isDefaultHIPS()) {
				hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_DEFAULT, new JSONString(String.valueOf(hips.isDefaultHIPS())));
			}
			
			hipsArray.set(hipsArray.size(), hipsObj);
		}
		
		JSONObject obj = new JSONObject();
		obj.put(EsaSkyWebConstants.SESSION_HIPS_ARRAY, hipsArray);

		String currentActive = Double.toString(panel.getSliderValue());
		obj.put(EsaSkyWebConstants.SESSION_HIPS_SLIDER, new JSONString(currentActive));
		return obj;
	}
}
