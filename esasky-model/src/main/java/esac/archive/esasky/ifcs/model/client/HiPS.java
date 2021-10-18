package esac.archive.esasky.ifcs.model.client;

import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;

/**
 * @author fgiordano Copyright (c) 2015- European Space Agency
 */
public class HiPS {

    public enum HiPSImageFormat {
        jpg, png;
    }

    /** hips mission */
    String mission;
    /** hips mission URL */
    String missionURL;
    /** hips specific wavelength range */
    String wavelengthRange;
    /** hips mission instrument */
    String instrument;
    /** hips creator */
    String creator;
    /** hips creator URL */
    String creatorURL;
    /** hips creation date */
    String creationDate;
    /** more info URL */
    String moreInfoURL;
    /** hips id */
    String surveyId;
    /** hips name */
    String surveyName;
    /** hips URL */
    String surveyRootUrl;
    /** hips frame J2000 or equatorial */
    HiPSCoordsFrame surveyFrame;
    /** hips norder prop */
    Integer maximumNorder;
    /** hips image format */
    HiPSImageFormat imgFormat;
    /** is the default maps */
    boolean isDefault;
    /** icon */
    String icon;
    /** Wavelength */
    HipsWavelength hipsWavelength;
    /*
     * hips color default map
     */
    ColorPalette colorPalette;

    boolean isLocal;
    JavaScriptObject files;
    
    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getMissionURL() {
        return missionURL;
    }

    public void setMissionURL(String missionURL) {
        this.missionURL = missionURL;
    }

    public String getWavelengthRange() {
        return wavelengthRange;
    }

    public void setWavelengthRange(String wavelengthRange) {
        this.wavelengthRange = wavelengthRange;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getSurveyRootUrl() {
        return surveyRootUrl;
    }

    public void setSurveyRootUrl(String surveyRootUrl) {
        this.surveyRootUrl = surveyRootUrl;
    }

    public HiPSCoordsFrame getSurveyFrame() {
        return surveyFrame;
    }

    public void setSurveyFrame(HiPSCoordsFrame surveyFrame) {
        this.surveyFrame = surveyFrame;
    }

    public Integer getMaximumNorder() {
        return maximumNorder;
    }

    public void setMaximumNorder(Integer maximumNorder) {
        this.maximumNorder = maximumNorder;
    }

    public HiPSImageFormat getImgFormat() {
        return imgFormat;
    }

    public void setImgFormat(HiPSImageFormat imgFormat) {
        this.imgFormat = imgFormat;
    }

    public ColorPalette getColorPalette() {
        return colorPalette;
    }

    public void setColorPalette(ColorPalette inputColorPalette) {
        this.colorPalette = inputColorPalette;
    }

    public String getCreatorURL() {
        return creatorURL;
    }

    public void setCreatorURL(String creatorURL) {
        this.creatorURL = creatorURL;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getMoreInfoURL() {
        return moreInfoURL;
    }

    public void setMoreInfoURL(String moreInfoURL) {
        this.moreInfoURL = moreInfoURL;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

	public boolean isLocal() {
		return isLocal;
	}

	public JavaScriptObject getFiles() {
		return files;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	public void setFiles(JavaScriptObject files) {
		this.files = files;
	}

	public HipsWavelength getHipsWavelength() {
		return hipsWavelength;
	}

	public void setHipsWavelength(HipsWavelength hipsWavelength) {
		this.hipsWavelength = hipsWavelength;
	}
	
}
