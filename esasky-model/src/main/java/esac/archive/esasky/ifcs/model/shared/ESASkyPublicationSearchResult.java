package esac.archive.esasky.ifcs.model.shared;

public class ESASkyPublicationSearchResult {

    private String name;

    public ESASkyPublicationSearchResult() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SimbadResult [name=" + this.name + "]";
    }
}
