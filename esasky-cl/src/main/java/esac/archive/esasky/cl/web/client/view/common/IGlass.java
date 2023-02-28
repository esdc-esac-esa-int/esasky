package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public interface IGlass{
    default void addGlassStyle(Widget glassPanel) {
        glassPanel.getElement().setClassName("gwt-PopupPanelGlass");
        glassPanel.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        glassPanel.getElement().getStyle().setLeft(0, Style.Unit.PX);
        glassPanel.getElement().getStyle().setTop(0, Style.Unit.PX);
        glassPanel.getElement().getStyle().setWidth(100, Style.Unit.PCT);
        glassPanel.getElement().getStyle().setHeight(100, Style.Unit.PCT);
        glassPanel.getElement().getStyle().setZIndex(1199);
    }

    default void showGlass() {
        getGlass().removeStyleName("displayNone");

    }

    default void hideGlass() {
        getGlass().addStyleName("displayNone");
    }

    Panel getGlass();

}
