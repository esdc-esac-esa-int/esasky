package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.dom.client.Element;

public abstract class MovableResizablePanel<T> extends MovablePanel implements Hidable<T> {

    public MovableResizablePanel(String googleEventCategory, boolean isSuggestedPositionCenter) {
        super(googleEventCategory, isSuggestedPositionCenter);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        getResizeElement().getStyle().setProperty("resize", "both");
        getResizeElement().getStyle().setProperty("overflow", "auto");
        addResizeHandler(getResizeElement().getId());
    }

    protected abstract void onResize();

    protected abstract Element getResizeElement();

    private native void addResizeHandler(String id) /*-{
        var movableResizablePanel = this;
        new $wnd.ResizeSensor($doc.getElementById(id), function() {
            movableResizablePanel.@esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel::onResize()();
        });
    }-*/;
}
