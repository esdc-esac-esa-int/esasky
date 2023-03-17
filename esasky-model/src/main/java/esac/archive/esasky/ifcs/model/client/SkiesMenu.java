package esac.archive.esasky.ifcs.model.client;

import java.util.LinkedList;
import java.util.List;

public class SkiesMenu {

    private List<SkiesMenuEntry> menuEntries = new LinkedList<SkiesMenuEntry>();
    private Integer total;

    public List<SkiesMenuEntry> getMenuEntries() {
        return menuEntries;
    }

    public void setMenuEntries(List<SkiesMenuEntry> menuEntries) {
        this.menuEntries = menuEntries;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public HiPS getHiPS(String surveyId) {
        for (SkiesMenuEntry currentEntry : menuEntries) {
            for (HiPS currentHiPS : currentEntry.getHips()) {
                if (currentHiPS.getSurveyId().equals(surveyId)) {
                    return currentHiPS;
                }
            }
        }
        return null;
    }

    public String getWavelengthFromHiPS(HiPS hips) {
        for (SkiesMenuEntry currentEntry : menuEntries) {
            for (HiPS currentHiPS : currentEntry.getHips()) {
                if (currentHiPS.getSurveyId().equals(hips.getSurveyId())) {
                    return currentEntry.getWavelength();
                }
            }
        }
        return null;
    }
    
    public String getWavelengthFromHiPSName(String hipsName) {
        for (SkiesMenuEntry currentEntry : menuEntries) {
            for (HiPS currentHiPS : currentEntry.getHips()) {
                if (currentHiPS.getSurveyName().equalsIgnoreCase(hipsName)) {
                    return currentEntry.getWavelength();
                }
            }
        }
        return null;
    }
    

    public HiPS getHiPS(String wavelength, String surveyId) {
        for (SkiesMenuEntry currentEntry : menuEntries) {

            if (currentEntry.getWavelength() == wavelength) {
                for (HiPS currentHiPS : currentEntry.getHips()) {
                    if (currentHiPS.getSurveyId().equalsIgnoreCase(surveyId)) {
                        return currentHiPS;
                    }
                }
            }
        }
        return null;
    }

    public HiPS getHiPS(String wavelength, String mission, String surveyId) {
        for (SkiesMenuEntry currentEntry : menuEntries) {

            if (currentEntry.getWavelength() == wavelength) {
                for (HiPS currentHiPS : currentEntry.getHips()) {
                    if (currentHiPS.getMission().equals(mission)
                            && currentHiPS.getSurveyId().equals(surveyId)) {
                        return currentHiPS;
                    }
                }
            }
        }
        return null;
    }

    public SkiesMenuEntry getHiPSListByWavelength(String wavelength) {
        for (SkiesMenuEntry currentEntry : menuEntries) {

            if (currentEntry.getWavelength() == wavelength) {
                return currentEntry;
            }
        }
        return null;
    }

}
