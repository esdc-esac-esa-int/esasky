package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LayoutDescriptor {
    private String name;
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("layouts_oid")
    public int getId() {
        return id;
    }

    @JsonProperty("layouts_oid")
    public void setId(int id) {
        this.id = id;
    }
}
