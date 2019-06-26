package esac.archive.esasky.ifcs.model.shared;

public class ESASkyTarget {

    String name;
    String cooFrame;
    String ra;
    String dec;
    String fovDeg;
    String hipsName;
    String title;
    String description;
    
    /**
     * Defaults constructor.
     */
    public ESASkyTarget() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCooFrame() {
        return cooFrame;
    }

    public void setCooFrame(String cooFrame) {
        this.cooFrame = cooFrame;
    }
    
    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }
    
    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }
    
    public String getFovDeg() {
        return fovDeg;
    }

    public void setFovDeg(String fovDeg) {
        this.fovDeg = fovDeg;
    }
    
    public String getHipsName() {
        return hipsName;
    }

    public void setHipsName(String hipsName) {
        this.hipsName = hipsName;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return (title != null && !title.isEmpty()) ? title : name;
    }
}
