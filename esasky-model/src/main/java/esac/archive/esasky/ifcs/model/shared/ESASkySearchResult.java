package esac.archive.esasky.ifcs.model.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;

public class ESASkySearchResult {

    // SIMBAD part
    // TODO instead of setting all these parameters for SIMBAD, it should be better to include the
    // SIMBADResult object here.
    String simbadMainId;
    String simbadNbref;
    String simbadRaDeg;
    String simbadDecDeg;
    String simbadGaldimMajAxis;
    String simbadGaldimMinAxis;
    String simbadGaldimAngle;
    String simbadCooFrame;

    // ESASky part
    String serverMessage = "";
    String userInput;
    SearchInputType userInputType;
    String userRaDeg;
    String userDecDeg;
    Boolean validInput = false;
    String cooFrame;
    String description;
    String outreachImage;

    @JsonIgnoreProperties(ignoreUnknown = true)
    double foVDeg;

    /**
     * Defaults constructor.
     */
    public ESASkySearchResult() {
        super();
    }

    public String getSimbadMainId() {
        return simbadMainId;
    }

    public void setSimbadMainId(String simbadMainId) {
        this.simbadMainId = simbadMainId;
    }

    public String getSimbadNbref() {
        return simbadNbref;
    }

    public void setSimbadNbref(String simbadNbref) {
        this.simbadNbref = simbadNbref;
    }

    public String getSimbadRaDeg() {
        return simbadRaDeg;
    }

    public void setSimbadRaDeg(String simbadRaDeg) {
        this.simbadRaDeg = simbadRaDeg;
    }

    public String getSimbadDecDeg() {
        return simbadDecDeg;
    }

    public void setSimbadDecDeg(String simbadDecDeg) {
        this.simbadDecDeg = simbadDecDeg;
    }

    public String getSimbadGaldimMajAxis() {
        return simbadGaldimMajAxis;
    }

    public void setSimbadGaldimMajAxis(String simbadGaldimMajAxis) {
        this.simbadGaldimMajAxis = simbadGaldimMajAxis;
    }

    public String getSimbadGaldimMinAxis() {
        return simbadGaldimMinAxis;
    }

    public void setSimbadGaldimMinAxis(String simbadGaldimMinAxis) {
        this.simbadGaldimMinAxis = simbadGaldimMinAxis;
    }

    public String getSimbadGaldimAngle() {
        return simbadGaldimAngle;
    }

    public void setSimbadGaldimAngle(String simbadGaldimAngle) {
        this.simbadGaldimAngle = simbadGaldimAngle;
    }

    public String getSimbadCooFrame() {
        return simbadCooFrame;
    }

    public void setSimbadCooFrame(String simbadCooFrame) {
        this.simbadCooFrame = simbadCooFrame;
    }

    public String getServerMessage() {
        return serverMessage;
    }

    public void setServerMessage(String serverMessage) {
        this.serverMessage = serverMessage;
    }

    public SearchInputType getUserInputType() {
        return userInputType;
    }

    public void setUserInputType(SearchInputType userInputType) {
        this.userInputType = userInputType;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getUserRaDeg() {
        return userRaDeg;
    }

    public void setUserRaDeg(String userRaDeg) {
        this.userRaDeg = userRaDeg;
    }

    public String getUserDecDeg() {
        return userDecDeg;
    }

    public void setUserDecDeg(String userDecDeg) {
        this.userDecDeg = userDecDeg;
    }

    public Boolean getValidInput() {
        return validInput;
    }

    public void setValidInput(Boolean validInput) {
        this.validInput = validInput;
    }

    public String getCooFrame() {
        return cooFrame;
    }

    public void setCooFrame(String cooFrame) {
        this.cooFrame = cooFrame;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getFoVDeg() {
        foVDeg = EsaSkyConstants.DEFAULT_FOV;
        String majorAxisArcmin = getSimbadGaldimMajAxis();
        if (majorAxisArcmin != null && majorAxisArcmin.trim().length() > 0) {
            foVDeg = Double.parseDouble(majorAxisArcmin) / 60.;
        }

        return foVDeg;
    }

    public void setFoVDeg(double foVDeg) {
        this.foVDeg = foVDeg;
    }


    public String getOutreachImage() {
        return outreachImage;
    }

    public String setOutreachImage(String outreachImage) {
        return this.outreachImage = outreachImage;
    }

    /**
     * To string method.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SimbadResult [name=" + this.simbadMainId + ", simbadraDeg=" + this.simbadRaDeg
                + ", simbaddecDeg=" + this.simbadDecDeg + ", galdimMajAxis="
                + this.simbadGaldimMajAxis + ", galdimMinAxis=" + this.simbadGaldimMinAxis
                + ", galdimAngle=" + this.simbadGaldimAngle + ", userRa=" + this.userRaDeg
                + ", userDec=" + this.userDecDeg + "]";
    }
}
