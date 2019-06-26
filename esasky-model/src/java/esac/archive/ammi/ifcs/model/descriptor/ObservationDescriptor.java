package esac.archive.ammi.ifcs.model.descriptor;


/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ObservationDescriptor extends CommonObservationDescriptor {

    /** for mission with a very large fov like INTEGRAL */
    private Boolean isSurveyMission;

    public Boolean getIsSurveyMission() {
        return isSurveyMission;
    }

    public void setIsSurveyMission(Boolean isSurveyMission) {
        this.isSurveyMission = isSurveyMission;
    }

	@Override
	public String generateId() {
		return getMission() + " Observation " + generateNextTabCount();
	}
}
