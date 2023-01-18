package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;

public class BaseMovablePopupPanel extends MovablePanel implements Hidable<PopupPanel> {

    protected FlowPanel container;
    private boolean isShowing = false;
    PopupHeader<PopupPanel> header;
    private final Resources resources;
    private final CssResource style;

    public interface Resources extends ClientBundle {
        @Source("basePopupMovablePanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public BaseMovablePopupPanel(String eventCategory, String headerText, String helpText) {
        super(eventCategory, true);
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();

        MainLayoutPanel.addMainAreaResizeHandler(this::onMainAreaResizeEvent);

        initView(headerText, helpText);
    }

    private void initView(String headerText, String helpText) {
        this.addStyleName("basePopupPanel");

        container = new FlowPanel();

        header = new PopupHeader<>(this, headerText, helpText);
        container.add(header);

        this.add(container);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        setMaxSize();
        this.addSingleElementAbleToInitiateMoveOperation(header.getElement());
    }


    public void show() {
        isShowing = true;
        removeStyleName("displayNone");
        updateHandlers();
        setMaxSize();
        ensureDialogFitsInsideWindow();
        OpenEvent.fire(this, null);
        MainLayoutPanel.addElementToMainArea(this);
    }


    public void hide() {
        isShowing = false;
        addStyleName("displayNone");
        this.removeHandlers();
        CloseEvent.fire(this, null);
        MainLayoutPanel.removeElementFromMainArea(this);
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    protected void onMainAreaResizeEvent(ResizeEvent event) {
        setMaxSize();
    }
}
