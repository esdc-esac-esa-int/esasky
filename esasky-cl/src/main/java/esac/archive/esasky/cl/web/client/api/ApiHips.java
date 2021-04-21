package esac.archive.esasky.cl.web.client.api;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.callback.JsonRequestCallback;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.IniFileParser;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HiPSCoordsFrame;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.client.SkiesMenuEntry;
import esac.archive.esasky.ifcs.model.client.HiPS.HiPSImageFormat;

public class ApiHips extends ApiBase{
	
	private long lastGASliderSent = 0;
	
	public ApiHips(Controller controller) {
		this.controller = controller;
	}
	
	
	public void openSkyPanel() {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_openSkyPanel, "");
		if(!controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().isShowing()) {
			controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().toggle();
		}
	}

	public void closeSkyPanel() {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_closeSkyPanel, "");
		if(controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().isShowing()) {
			controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().toggle();
		}
	}
	
	public void addHiPS(String wantedHiPSName, JavaScriptObject widget) {
		SelectSkyPanel.getInstance().createSky();
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_addHips, wantedHiPSName);
		if(!setHiPS(wantedHiPSName, widget)) {
			SelectSkyPanel.getSelectedSky().notifyClose();
		}
	}
	
	public void addHiPSWithParams(String surveyName, String surveyRootUrl, String surveyFrame,
			int maximumNorder, String imgFormat) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_addHips, surveyRootUrl);
		SelectSkyPanel.getInstance().createSky();
		setHiPSWithParams(surveyName, surveyRootUrl, surveyFrame, maximumNorder, imgFormat);
	}
	
	public void removeSkyRow(int index, JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_removeHipsOnIndex, "");
		String msg = "";
		if(index < 0) {
			for(int i = SelectSkyPanel.getInstance().getNumberOfSkyRows() - 1; i > 0 ;i--) {
				SelectSkyPanel.getInstance().removeSky(i);
			}
			sendBackSuccessToWidget(widget);

		}else {
			
			if(SelectSkyPanel.getInstance().removeSky(index)) {
				sendBackSuccessToWidget(widget);
			}
			
			else {
				msg = "Index out of bounds. Max number is: " + Integer.toString(SelectSkyPanel.getInstance().getNumberOfSkyRows());
				sendBackErrorMsgToWidget(msg, widget);
			}
		}
	}
	
	public void getNumberOfSkyRows(JavaScriptObject widget) {
		int count = SelectSkyPanel.getInstance().getNumberOfSkyRows();
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getNumberOfSkyRows, Integer.toString(count));
		sendBackSingleValueToWidget(new JSONNumber(count), widget);
	}
	
	public void setHiPSSliderValue(double value) {
		if(System.currentTimeMillis() - lastGASliderSent > 1000) {
			lastGASliderSent = System.currentTimeMillis();
			GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_setHipsSliderValue, Double.toString(value));
		}
		SelectSkyPanel.getInstance().setSliderValue(value);
	}

	public boolean setHiPS(String wantedHiPSName, JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_changeHiPS, wantedHiPSName);
		if (!SelectSkyPanel.getSelectedSky().setSelectHips(wantedHiPSName, true, false)) {
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString("No HiPS called: " + wantedHiPSName + " found"));

			JSONObject wavelengthMap = getAvailableHiPS("", true);
			error.put(ApiConstants.ERROR_AVAILABLE, wavelengthMap);
			sendBackErrorToWidget(error, widget);
			return false;
		}
		sendBackSuccessToWidget(widget);
		return true;
	}

	public void setHiPSWithParams(String surveyName, String surveyRootUrl, boolean add, final JavaScriptObject widget) {
		
		final String propertiesUrl = surveyRootUrl +  "/" +  ApiConstants.HIPS_PROPERTIES_FILE;
		JSONUtils.getJSONFromUrl(propertiesUrl, new JsonRequestCallback("", propertiesUrl) {

			@Override
			protected void onSuccess(Response response) {
				GeneralJavaScriptObject props = IniFileParser.parseIniString(response.getText());
				
				if(!props.hasProperty(ApiConstants.HIPS_PROP_FRAME )|| "".equals(props.getStringProperty(ApiConstants.HIPS_PROP_FRAME))) {
					sendBackErrorMsgToWidget(ApiConstants.HIPS_PROP_ERROR + ApiConstants.HIPS_PROP_FRAME, widget);
					return;
				}
				if(!props.hasProperty(ApiConstants.HIPS_PROP_FORMAT )|| "".equals(props.getStringProperty(ApiConstants.HIPS_PROP_FORMAT))) {
					sendBackErrorMsgToWidget(ApiConstants.HIPS_PROP_ERROR + ApiConstants.HIPS_PROP_FORMAT, widget);
					return;
				}
				if(!props.hasProperty(ApiConstants.HIPS_PROP_ORDER )|| "".equals(props.getStringProperty(ApiConstants.HIPS_PROP_ORDER))) {
					sendBackErrorMsgToWidget(ApiConstants.HIPS_PROP_ERROR + ApiConstants.HIPS_PROP_ORDER, widget);
					return;
				}
				
				String surveyFrame = props.getStringProperty(ApiConstants.HIPS_PROP_FRAME);
				String imgFormat = props.getStringProperty(ApiConstants.HIPS_PROP_FORMAT);
				int maximumNorder = (int) props.getDoubleProperty(ApiConstants.HIPS_PROP_ORDER);
				
				if(add) {
					addHiPSWithParams(surveyName, surveyRootUrl, surveyFrame, maximumNorder, imgFormat);
				}else {
					setHiPSWithParams(surveyName, surveyRootUrl, surveyFrame, maximumNorder, imgFormat);
				}
				sendBackSuccessToWidget(widget);

			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				sendBackErrorMsgToWidget(ApiConstants.HIPS_PROP_ERROR_LOADING + propertiesUrl, widget);
			}
			
		});
		
	}
	
	public void setHiPSWithParams(String surveyName, String surveyRootUrl, String surveyFrame,
			int maximumNorder, String imgFormat) {
		
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_changeHiPSWithParams, surveyRootUrl);

		HiPS hips = new HiPS();
		hips.setSurveyId(surveyName);
		hips.setSurveyName(surveyName);
		hips.setSurveyRootUrl(surveyRootUrl);
		HiPSCoordsFrame surveyFrameEnum = HiPSCoordsFrame.GALACTIC.name().toLowerCase()
				.contains(surveyFrame.toLowerCase()) ? HiPSCoordsFrame.GALACTIC : HiPSCoordsFrame.EQUATORIAL;
		hips.setSurveyFrame(surveyFrameEnum);
		hips.setMaximumNorder(maximumNorder);
		HiPSImageFormat hipsImageFormatEnum = HiPSImageFormat.png.name().toLowerCase().contains(imgFormat.toLowerCase())
				? HiPSImageFormat.png : HiPSImageFormat.jpg;
		hips.setImgFormat(hipsImageFormatEnum);
		SelectSkyPanel.setHiPSFromAPI(hips, true);
	}

	public void setHiPSColorPalette(String colorPalette, JavaScriptObject widget) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_setHiPSColorPalette, colorPalette);
		
		JSONArray available = new JSONArray();
		int i = 0;
		ColorPalette colorPaletteEnum = null;
		for(ColorPalette c : ColorPalette.values()) {
			if(colorPalette.toUpperCase().equals(c.toString())) {
				colorPaletteEnum = ColorPalette.valueOf(colorPalette.toUpperCase());
				break;
			}
			available.set(i, new JSONString(c.toString()));
			i++;
		}
		
		if(colorPaletteEnum == null) {
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString(ApiConstants.HIPS_ERROR_COLORPALETTE));
			error.put(ApiConstants.ERROR_AVAILABLE, available);
			sendBackErrorToWidget(error, widget);
			return;
		}
		
		SelectSkyPanel.getSelectedSky().setColorPalette(colorPaletteEnum);
		sendBackSuccessToWidget(widget);
	}
	
	
	public JSONObject getHipsAllWavelengths( boolean onlyName) {
		SkiesMenu skiesMenu = controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().getSkiesMenu();

		JSONObject wavelengthMap = new  JSONObject();
		for (SkiesMenuEntry currSkiesMenuEntry : skiesMenu.getMenuEntries()) {
			HipsWavelength currWavelength = currSkiesMenuEntry.getWavelength();
			JSONObject hips = getHiPSByWavelength(currWavelength);
			if(onlyName) {
				JSONArray hipsNames = new JSONArray();
				int i = 0;
				for(String key : hips.keySet()) {
					hipsNames.set(i++, new JSONString(key));
				}
				wavelengthMap.put(currWavelength.name(), hipsNames);
			}else {
				wavelengthMap.put(currWavelength.name(), hips);
			}
		}
		return wavelengthMap;

	}	
	
	public JSONObject getAvailableHiPS(String wavelength, boolean onlyName) {
		//TODO Looks in skiesMenu which doesn't contain user added HiPS
		
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_Pyesasky_getAvailableHiPS, wavelength);
		
		HipsWavelength hipsWavelength;
		if(wavelength == null && "".equals(wavelength)) {
			hipsWavelength = null;
		}else {
			try {
				hipsWavelength = HipsWavelength.valueOf(wavelength);	
			}catch(Exception e) {
				Log.debug("[APIHips]" + e.getMessage(),e);
				hipsWavelength = null;
			}
		}
		if (null == hipsWavelength) {
			return getHipsAllWavelengths(onlyName);
		} else {
			return getHiPSByWavelength(hipsWavelength);
		}
	}
	
	public void getAvailableHiPS(String wavelength, JavaScriptObject widget) {
		JSONObject wavelengthMap = getAvailableHiPS(wavelength, false);
		sendBackToWidget(wavelengthMap, null, widget);
	}

	private JSONObject getHiPSByWavelength(HipsWavelength wavelength) {

		JSONObject hipsMap = new JSONObject();
		SkiesMenu skiesMenu = controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter().getSkiesMenu();
		for (SkiesMenuEntry currSkiesMenuEntry : skiesMenu.getMenuEntries()) {
			if (currSkiesMenuEntry.getWavelength() == wavelength) {
				for (HiPS currHiPS : currSkiesMenuEntry.getHips()) {
					JSONObject currHiPSJSON = new JSONObject();
					currHiPSJSON.put(ApiConstants.HIPS_LABEL, new JSONString(currHiPS.getSurveyId()));
					currHiPSJSON.put(ApiConstants.HIPS_URL, new JSONString(currHiPS.getSurveyRootUrl()));
					currHiPSJSON.put(ApiConstants.HIPS_FRAME, new JSONString(currHiPS.getSurveyFrame().getName()));
					currHiPSJSON.put(ApiConstants.HIPS_MAX_ORDER, new JSONString(Integer.toString(currHiPS.getMaximumNorder())));
					currHiPSJSON.put(ApiConstants.HIPS_FORMAT, new JSONString(currHiPS.getImgFormat().name()));
					hipsMap.put(currHiPS.getSurveyId(),currHiPSJSON);
				}
			}
		}
		return hipsMap;
	}
}
