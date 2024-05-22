package esac.archive.esasky.cl.web.client.view.common;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;

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
        String resizableElementId = getResizeElement().getId();
        if("".equals(resizableElementId)) {
        	Log.error("Resizable Elements needs an Id");
        }
        addResizeHandler(resizableElementId);

        addSingleElementAbleToInitiateMoveOperation(getMovableElement());
        setSnapping(false);
    }

    @Override
    public void show() {
        isShowing = true;
        this.removeStyleName("displayNone");
        updateHandlers();
        setMaxSize();
        ensureDialogFitsInsideWindow();
        OpenEvent.fire(this, null);
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
        return isShowing;
    }

    protected abstract Element getMovableElement();

    protected void onResize() {
        Style elementStyle = this.getElement().getStyle();
        int maxWidth = MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - this.getAbsoluteLeft() - 15;
        int maxHeight = MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - this.getAbsoluteTop() - 15;
        elementStyle.setPropertyPx("maxWidth", maxWidth);
        elementStyle.setPropertyPx("maxHeight", maxHeight);
    }

    protected abstract Element getResizeElement();

    private native void addResizeHandler(String id) /*-{
        var movableResizablePanel = this;
        new $wnd.ResizeSensor($doc.getElementById(id), function() {
            movableResizablePanel.@esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel::onResize()();
        });
    }-*/;
}
