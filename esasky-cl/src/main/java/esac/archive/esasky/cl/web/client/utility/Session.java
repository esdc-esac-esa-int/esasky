package esac.archive.esasky.cl.web.client.utility;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FileUpload;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.AddTableEvent;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.entities.EsaSkyEntity;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsByAuthorEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsBySourceEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsEntity;
import esac.archive.esasky.cl.web.client.presenter.MainPresenter;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.utility.exceptions.SaveStateException;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SkyRow;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.descriptor.GwDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.GwPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.FutureFootprintRow;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.PlanObservationPanel;

public class Session {
	
	public void saveState() {
		
		JSONObject stateObj = new JSONObject();

		stateObj.put(EsaSkyWebConstants.SESSION_LOCATION, getLocationJson());
		stateObj.put(EsaSkyWebConstants.SESSION_HIPS, getHipsJson());
		stateObj.put(EsaSkyWebConstants.SESSION_DATA, getDataJson());
		JSONObject gw = getGwJson();
		if(gw != null) {
			stateObj.put(EsaSkyWebConstants.SESSION_GW, gw);
		}
		stateObj.put(EsaSkyWebConstants.SESSION_PLANNING, getPlanningJson());
		stateObj.put(EsaSkyWebConstants.SESSION_PUB, getPublicationJson());
		stateObj.put(EsaSkyWebConstants.SESSION_SETTINGS, getSettingsJson());

		writeToFile(stateObj.toString(), "test.json");
		
	}
	
	public void restoreState() {
		FileUpload upload = new FileUpload();
		addFileUploadHandler(this, (JavaScriptObject) upload.getElement());
		upload.click();
	}
	
	public void restoreState(String jsonString) {
		GeneralJavaScriptObject saveStateObj = GeneralJavaScriptObject.createJsonObject(jsonString);
		try {
			restoreLocation(saveStateObj);
			restoreData(saveStateObj);
			restoreHipsStack(saveStateObj);
			restoreGW(saveStateObj);
			restorePublications(saveStateObj);
			restorePlanning(saveStateObj);
			restoreSettings(saveStateObj);
		} catch (SaveStateException e) {
			Log.error(e.getMessage(), e);
		}
	}
	
	private JSONObject getSettingsJson() {
		JSONObject settingsObj = new JSONObject();
		if(MainPresenter.getInstance().getHeaderPresenter().getView().getGridButtonToggled()) {
			settingsObj.put(EsaSkyWebConstants.SESSION_SETTINGS_GRID, new JSONString("True"));
		}
		return settingsObj;
	}
	
	private void restoreSettings(GeneralJavaScriptObject saveStateObj) {
		GeneralJavaScriptObject settingsObj = saveStateObj.getProperty(EsaSkyWebConstants.SESSION_SETTINGS);
		if(settingsObj.hasProperty(EsaSkyWebConstants.SESSION_SETTINGS_GRID)) {
			boolean grid = Boolean.parseBoolean(settingsObj.getStringProperty(EsaSkyWebConstants.SESSION_SETTINGS_GRID));
			AladinLiteWrapper.getInstance().toggleGrid(grid);
		}
	}
	
	private JSONArray getPublicationJson() {
		JSONArray array = new JSONArray();
		for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
			if(ent.getShape(0) == null) {
				continue;
			}
			if(ent instanceof PublicationsEntity) {
				JSONObject pubObj = new JSONObject();
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_TYPE, new JSONString(EsaSkyWebConstants.SESSION_PUB_TYPE_AREA));
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_URL, new JSONString(ent.getAdql()));
				array.set(array.size(), pubObj);
			}else if (ent instanceof PublicationsByAuthorEntity) {
				JSONObject pubObj = new JSONObject();
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_TYPE, new JSONString(EsaSkyWebConstants.SESSION_PUB_TYPE_AUTHOR));
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_AUTHOR, new JSONString(ent.getEsaSkyUniqId()));
				array.set(array.size(), pubObj);
			
			}else if (ent instanceof PublicationsBySourceEntity) {
				PublicationsBySourceEntity pubEnt = (PublicationsBySourceEntity) ent;
				Shape shape = pubEnt.getShape(0);
				JSONObject pubObj = new JSONObject();
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_TYPE, new JSONString(EsaSkyWebConstants.SESSION_PUB_TYPE_SOURCE));
				pubObj.put(EsaSkyWebConstants.SESSION_PUB_SOURCE, new JSONString(ent.getEsaSkyUniqId()));
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
			boolean all = Boolean.parseBoolean(planningObj.getStringProperty(EsaSkyWebConstants.SESSION_PLANNING_ALL));
			PlanObservationPanel planObservationPanel = PlanObservationPanel.getInstance();
			planObservationPanel.addInstrumentRowWithCoordinatesAPI(instrument, aperture, all, ra, dec, rot);
		}
	}
	
	private JSONObject getGwJson() {
		JSONObject obj = null;
		for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
			
			if(ent.getDescriptor() instanceof GwDescriptor) {
				if(ent.getTablePanel() != null) {
					GeneralJavaScriptObject[] rows = ent.getTablePanel().getSelectedRows();
					if(rows.length > 0 ) {
	                    String id = rows[0].getStringProperty(GwPanel.GRACE_ID);
	                    obj = new JSONObject();
	                    obj.put(EsaSkyWebConstants.SESSION_GW_ID, new JSONString(id));
					}
					break;
				}
			}
		}
		return obj;
	}
	
	private void restoreGW(GeneralJavaScriptObject saveStateObj) {
		GeneralJavaScriptObject gwObj = saveStateObj.getProperty(EsaSkyWebConstants.SESSION_GW);
		if(gwObj != null) {
			String id = gwObj.getStringProperty(EsaSkyWebConstants.SESSION_GW_ID);
			MainPresenter.getInstance().getCtrlTBPresenter().showGWEvent(id);
		}
	}
	

	private void restoreLocation(GeneralJavaScriptObject obj) throws SaveStateException {
		try {
			GeneralJavaScriptObject loc = obj.getProperty(EsaSkyWebConstants.SESSION_LOCATION);
			double ra = loc.getDoubleProperty(EsaSkyWebConstants.SESSION_RA);
			double dec = loc.getDoubleProperty(EsaSkyWebConstants.SESSION_DEC);
			double fov = loc.getDoubleProperty(EsaSkyWebConstants.SESSION_FOV);
			String cooFrame = loc.getStringProperty(EsaSkyWebConstants.SESSION_FRAME);
			
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

			
		}catch (Exception e) {
			throw new SaveStateException(e.getMessage(), e);
		}
	}
	
	private void restoreHipsStack(GeneralJavaScriptObject saveStateObj) throws SaveStateException {
		GeneralJavaScriptObject hipsArrayObj = saveStateObj.getProperty(EsaSkyWebConstants.SESSION_HIPS);
		GeneralJavaScriptObject[] array = GeneralJavaScriptObject.convertToArray(hipsArrayObj.getProperty(EsaSkyWebConstants.SESSION_HIPS_ARRAY));
		
		boolean first = true;
		for(GeneralJavaScriptObject hipObj : array) {
			String name = hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_NAME);
			String colorPalette = hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_COLORPALETTE);
			SkyRow skyRow;
			if(!first) {
				skyRow = SelectSkyPanel.getInstance().createSky(true);
			}else {
				skyRow = SelectSkyPanel.getSelectedSky();
				first = false;
			}
			if(!skyRow.setSelectHips(name, true, false)) {
				//Means that we can't find it in the list
				//Setting from url instead
				String url = hipObj.getStringProperty(EsaSkyWebConstants.SESSION_HIPS_URL);
				addUrlHips(url, colorPalette, skyRow);
			}
			skyRow.setColorPalette(ColorPalette.valueOf(colorPalette));
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
	
	private void addUrlHips(String url, String colorPalette, SkyRow skyRow) {
		HipsParser parser = new HipsParser(new HipsParserObserver() {
			
			@Override
			public void onSuccess(HiPS hips) {
				hips.setHipsWavelength(HipsWavelength.USER);
				skyRow.setHiPSFromAPI(hips, false, true);
				skyRow.setColorPalette(ColorPalette.valueOf(colorPalette));
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
	
	
	private JSONArray getDataJson() {
		JSONArray entArray = new JSONArray();
		for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
			
			if(ent.getDescriptor() instanceof GwDescriptor || ent.getDescriptor() instanceof PublicationsDescriptor) {
				continue;
			}
			
			JSONObject entObj = new JSONObject();
			entObj.put(EsaSkyWebConstants.SESSION_DATA_MISSION, new JSONString(ent.getDescriptor().getMission()));
			entObj.put(EsaSkyWebConstants.SESSION_DATA_TABLE,new JSONString(ent.getDescriptor().getTapTable()));
			
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
			
			entObj.put(EsaSkyWebConstants.SESSION_DATA_ADQL,new JSONString(ent.getAdql()));
			entObj.put(EsaSkyWebConstants.SESSION_DATA_FILTERS, new JSONString(ent.getTablePanel().getFilterString()));
			
			entObj.put(EsaSkyWebConstants.SESSION_DATA_COLOR_MAIN,new JSONString(ent.getPrimaryColor()));
			entObj.put(EsaSkyWebConstants.SESSION_DATA_COLOR_SECOND,new JSONString(ent.getSecondaryColor()));
			entObj.put(EsaSkyWebConstants.SESSION_DATA_SIZE,new JSONString(new Double(ent.getSize()).toString()));
			if(ent.getLineStyle() != null) {
				entObj.put(EsaSkyWebConstants.SESSION_DATA_LINESTYLE,new JSONString(ent.getLineStyle()));
			}
			if(ent.getShapeType() != null) {
				entObj.put(EsaSkyWebConstants.SESSION_DATA_SOURCE_STYLE,new JSONString(ent.getShapeType()));
			}
			entArray.set(entArray.size(), entObj);
		}
		return entArray;
		
	}
	
	private void restoreData(GeneralJavaScriptObject saveStateObj) throws SaveStateException {
		try {
			GeneralJavaScriptObject[] dataArray = GeneralJavaScriptObject.convertToArray(saveStateObj.getProperty(EsaSkyWebConstants.SESSION_DATA));
			for(GeneralJavaScriptObject dataObj : dataArray) {
				String mission = dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_MISSION);
				String tableName = dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_TABLE);
				String adql = dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_ADQL);
				boolean isMoc = Boolean.parseBoolean(dataObj.getStringProperty(EsaSkyWebConstants.SESSION_DATA_ISMOC));

				IDescriptor desc = DescriptorRepository.getInstance().getDescriptorFromTable(tableName, mission);
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
		}catch (Exception e) {
			throw new SaveStateException(e.getMessage(), e);
		}
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

		String raDeg = new Double(AladinLiteWrapper.getCenterRaDeg()).toString();
		String decDeg = new Double(AladinLiteWrapper.getCenterDecDeg()).toString();
		String fov = new Double(AladinLiteWrapper.getAladinLite().getFovDeg()).toString();
		String cooFrame = AladinLiteWrapper.getCoordinatesFrame().toString();
		
		obj.put(EsaSkyWebConstants.SESSION_RA, new JSONString(raDeg));
		obj.put(EsaSkyWebConstants.SESSION_DEC, new JSONString(decDeg));
		obj.put(EsaSkyWebConstants.SESSION_FOV, new JSONString(fov));
		obj.put(EsaSkyWebConstants.SESSION_FRAME, new JSONString(cooFrame));
		
		return obj;
	}
	
	private JSONObject getHipsJson() {
		JSONArray hipsArray = new JSONArray();
		SelectSkyPanel panel = SelectSkyPanel.getInstance();
		for(SkyRow row : panel.getHipsList()) {
			JSONObject hipsObj = new JSONObject();
			HiPS hips = row.getSelectedHips();
			hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_NAME, new JSONString(hips.getSurveyName().toString()));
			hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_URL, new JSONString(hips.getSurveyRootUrl().toString()));
			hipsObj.put(EsaSkyWebConstants.SESSION_HIPS_COLORPALETTE, new JSONString(row.getSelectedPalette().toString()));
			hipsArray.set(hipsArray.size(), hipsObj);
		}
		
		JSONObject obj = new JSONObject();
		obj.put(EsaSkyWebConstants.SESSION_HIPS_ARRAY, hipsArray);

		String currentActive =  new Double(panel.getSliderValue()).toString();
		obj.put(EsaSkyWebConstants.SESSION_HIPS_SLIDER, new JSONString(currentActive));
		return obj;
	}
}
