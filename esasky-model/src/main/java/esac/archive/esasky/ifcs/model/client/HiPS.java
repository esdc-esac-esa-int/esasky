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

package esac.archive.esasky.ifcs.model.client;

import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ImageLayer;
import esac.archive.esasky.ifcs.model.shared.ColorPalette;

import java.util.Objects;


/**
 * @author fgiordano Copyright (c) 2015- European Space Agency
 */
public class HiPS {

    public enum HiPSImageFormat {
        jpg, png, fits;
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
    String hipsWavelength;
    /** Category */
    String hipsCategory;
    /** defaultHIPS */
    boolean defaultHIPS = false;
    
    boolean useCredentials = false;
    
    /*
     * hips color default map
     */
    ColorPalette colorPalette;

    boolean isLocal;
    JavaScriptObject files;
    
    boolean isReversedColorMap = false;

    String creatorDid;
    
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
        return this.surveyRootUrl;
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

	public String getHipsWavelength() {
		return hipsWavelength;
	}

	public void setHipsWavelength(String hipsWavelength) {
		this.hipsWavelength = hipsWavelength;
	}

	public boolean isReversedColorMap() {
		return isReversedColorMap;
	}

	public void setReversedColorMap(boolean isReversedColorMap) {
		this.isReversedColorMap = isReversedColorMap;
	}

	public String getHipsCategory() {
		return hipsCategory;
	}

	public void setHipsCategory(String hipsCategory) {
		this.hipsCategory = hipsCategory;
	}

	public boolean isDefaultHIPS() {
		return defaultHIPS;
	}

	public void setDefaultHIPS(boolean defaultHIPS) {
		this.defaultHIPS = defaultHIPS;
	}

    public String getCreatorDid() {
        return creatorDid;
    }
    public void setCreatorDid(String creatorDid) {
        this.creatorDid = creatorDid;
    }

    public static HiPS fromImageLayer(ImageLayer imageLayer, String category) {
        HiPS hips = new HiPS();
        hips.setSurveyId(imageLayer.getName());
        hips.setSurveyName(imageLayer.getName());
        hips.setSurveyRootUrl(imageLayer.getId());
        hips.setImgFormat(HiPSImageFormat.valueOf(imageLayer.getImageFormat()));
        hips.setSurveyFrame(HiPSCoordsFrame.EQUATORIAL);
        hips.setMaximumNorder(8);
        hips.setColorPalette(ColorPalette.NATIVE);
        if(category != null && !category.isEmpty()) {
            hips.setHipsCategory(category);
        }

        return hips;
    }
	
	public boolean shouldUseCredentials() {
		return useCredentials;
	}

	public void setUseCredentials(boolean useCredentials) {
		this.useCredentials = useCredentials;
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HiPS hips = (HiPS) obj;
        return Objects.equals(surveyId, hips.surveyId) && Objects.equals(surveyRootUrl, hips.surveyRootUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(surveyId, surveyRootUrl);
    }
	
}
