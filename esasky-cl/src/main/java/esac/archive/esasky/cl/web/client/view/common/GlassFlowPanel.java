package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

public class GlassFlowPanel extends FlowPanel implements IGlass {

    FlowPanel glassPanel = new FlowPanel();

    public GlassFlowPanel() {
        addGlassStyle(glassPanel);
        this.add(glassPanel);
        this.hideGlass();
    }

    @Override
    public Panel getGlass() {
        return glassPanel;
    }
}
