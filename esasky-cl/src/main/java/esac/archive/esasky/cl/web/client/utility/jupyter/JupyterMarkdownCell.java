package esac.archive.esasky.cl.web.client.utility.jupyter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class JupyterMarkdownCell extends JupyterCell {
    public JupyterMarkdownCell(String... lines) {
        this.cell_type = "markdown";
        source = new ArrayList<>();
        for(String line : lines) {
            this.addLine(line);
        }
    }

    public void addLine(final String line) {
        if (!source.isEmpty()) {
            String lastLine = source.remove(source.size() - 1);
            source.add(lastLine + "<br>\n");
        }
        source.add(line);
    }
}
