package esac.archive.esasky.ifcs.model.client;

public class HiPSPlayerEntry {

    private HipsWavelength wavelength;
    private String mission;
    private String surveyName;

    public HipsWavelength getWavelength() {
        return wavelength;
    }

    public void setWavelength(HipsWavelength wavelength) {
        this.wavelength = wavelength;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

}
