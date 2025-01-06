package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;

public class CollapsablePanel extends FlowPanel {

	private int height = 100;
    private boolean isCollapsed;

    private final Resources resources;
    private final CssResource style;
    

    public interface Resources extends ClientBundle {
        @Source("collapsablePanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public CollapsablePanel(boolean isCollapsed, int heightInPx) {
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.height = heightInPx;

        if (isCollapsed) {
            collapse();
        }

        this.isCollapsed = isCollapsed;
        this.getElement().setId("collapsablePanel");

    }

    public void collapse() {
        addStyleName("collapsed");
        removeStyleName("expanded");
        getElement().getStyle().setHeight(0, Unit.PX);
        this.isCollapsed = true;
    }

    public void expand() {
        removeStyleName("collapsed");
        addStyleName("expanded");
        getElement().getStyle().setHeight(this.height, Unit.PX);
        this.isCollapsed = false;
    }


    public void toggle() {
        if (isCollapsed) {
            expand();
        } else {
            collapse();
        }
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }
}
