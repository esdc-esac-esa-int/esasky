package esac.archive.esasky.cl.web.client.utility.jupyter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


public class Jupyter {
    public static class JupyterMetadata {};

    @JsonProperty("nbformat")
    private final int nb_format;

    @JsonProperty("nbformat_minor")
    private final int nb_format_minor;

    @JsonProperty("metadata")
    protected JupyterMetadata metadata = new JupyterMetadata();

    @JsonProperty("cells")
    private final List<JupyterCell> cells;

    public Jupyter() {
        nb_format = 4;
        nb_format_minor = 0;
        this.cells = new ArrayList<>();
    }

    public void addCell(final JupyterCell cell) {
        cells.add(cell);
    }
}
