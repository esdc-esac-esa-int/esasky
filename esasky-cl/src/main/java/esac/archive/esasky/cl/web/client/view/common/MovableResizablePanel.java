package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.CloseEvent;

public abstract class MovableResizablePanel<T> extends MovablePanel implements Hidable<T> {

    private boolean isShowing = false;

    public MovableResizablePanel(String googleEventCategory, boolean isSuggestedPositionCenter) {
        super(googleEventCategory, isSuggestedPositionCenter);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        getResizeElement().getStyle().setProperty("resize", "both");
        getResizeElement().getStyle().setProperty("overflow", "auto");
        addResizeHandler(getResizeElement().getId());

        this.setSnapping(false);
    }

    @Override
    public void show() {
        this.addSingleElementAbleToInitiateMoveOperation(getMovableElement());
        this.removeStyleName("displayNone");
        updateHandlers();
    }

    @Override
    public void hide() {
        isShowing = false;
        this.addStyleName("displayNone");
        this.removeHandlers();
        CloseEvent.fire(this, null);
    }

    @Override
    public void toggle() {
        if (isShowing) {
            hide();
        } else {
            show();
        }
    }

    @Override
    public boolean isShowing() {
        return false;
    }

    protected abstract Element getMovableElement();

    protected abstract void onResize();

    protected abstract Element getResizeElement();

    private native void addResizeHandler(String id) /*-{
        var movableResizablePanel = this;
        new $wnd.ResizeSensor($doc.getElementById(id), function() {
            movableResizablePanel.@esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel::onResize()();
        });
    }-*/;
}
