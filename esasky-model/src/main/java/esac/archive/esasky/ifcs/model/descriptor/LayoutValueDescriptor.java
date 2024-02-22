package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LayoutValueDescriptor {

    private int id;
    private String key;
    private boolean isShown;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("is_shown")
    public boolean isShown() {
        return isShown;
    }

    @JsonProperty("is_shown")
    public void setShown(boolean shown) {
        isShown = shown;
    }
}
