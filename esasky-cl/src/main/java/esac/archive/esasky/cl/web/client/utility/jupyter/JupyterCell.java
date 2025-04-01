package esac.archive.esasky.cl.web.client.utility.jupyter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.stream.Collector;

public class JupyterCell {
    public static class JupyterCellMetadata {};

    @JsonProperty("metadata")
    protected JupyterCellMetadata metadata = new JupyterCellMetadata();

    @JsonProperty("cell_type")
    protected String cell_type;

    @JsonProperty("source")
    protected List<String> source;
}
