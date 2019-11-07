package esac.archive.esasky.ifcs.model.shared;

public enum ObsCoreCollection {
    image("Images"), spectrum("Spectra"), cube("Cubes"), timeseries("Time series");

    private String type;

    private ObsCoreCollection(String type) {
        this.type = type;
    }

    public String getName() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
