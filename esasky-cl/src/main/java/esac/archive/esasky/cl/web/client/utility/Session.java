package esac.archive.esasky.cl.web.client.utility;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.FileUpload;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.hips.HipsAddedEvent;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.presenter.MainPresenter;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.utility.exceptions.SaveStateException;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SkyRow;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;

public class Session {
	
	public void saveState() {
		
		JSONObject stateObj = new JSONObject();

		
		stateObj.put(EsaSkyWebConstants.SESSION_LOCATION, getLocationJson());
		stateObj.put(EsaSkyWebConstants.SESSION_HIPS, getHipsJson());
		
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
			restoreHipsStack(saveStateObj);
		} catch (SaveStateException e) {
			Log.error(e.getMessage(), e);
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
	
	
	private JSONObject getDataJson() {
		for(GeneralEntityInterface ent : EntityRepository.getInstance().getAllEntities()) {
			
		}
		return null;
		
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
