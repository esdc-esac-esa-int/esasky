package esac.archive.esasky.ifcs.model.client;

import java.util.LinkedList;
import java.util.List;

public class SkiesMenuEntry {

    private Integer total;
    private String wavelength;
    private List<HiPS> hips = new LinkedList<HiPS>();

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getWavelength() {
        return wavelength;
    }

    public void setWavelength(String wavelength) {
        this.wavelength = wavelength;
    }


    public void setHips(List<HiPS> hips) {
        this.hips = hips;
    }

    public List<HiPS> getHips() {
        return hips;
    }

}
