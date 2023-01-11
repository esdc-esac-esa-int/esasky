package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import esac.archive.esasky.cl.web.client.event.DialogActionEvent;
import esac.archive.esasky.cl.web.client.event.DialogActionEventHandler;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;

public class ConfirmationPopupPanel extends BaseMovablePopupPanel {

    private final Resources resources;
    private final CssResource style;
    public interface Resources extends ClientBundle {
        @Source("confirmationPopupPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public ConfirmationPopupPanel(String eventCategory, String title, String body, String helpText) {
        super(eventCategory, title, helpText);
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();

        MainLayoutPanel.addMainAreaResizeHandler(event -> setDefaultSize());

        initView(body);
    }

    public void initView(String body) {
        FlowPanel bodyContainer = new FlowPanel();
        bodyContainer.addStyleName("confirmationPopupPanel_bodyContainer");
        bodyContainer.add(new HTML(body));

        FlowPanel buttonContainer = new FlowPanel();
        buttonContainer.addStyleName("confirmationPopupPanel_buttonContainer");

        EsaSkyStringButton noButton = new EsaSkyStringButton("No");
        noButton.addStyleName("confirmationPopupPanel_button");
        noButton.setMediumStyle();
        noButton.addClickHandler(event -> fireDialogEvent(DialogActionEvent.DialogAction.NO));

        EsaSkyStringButton yesButton = new EsaSkyStringButton("Yes");
        yesButton.addStyleName("confirmationPopupPanel_button");
        yesButton.setMediumStyle();
        yesButton.addClickHandler(event -> fireDialogEvent(DialogActionEvent.DialogAction.YES));

        buttonContainer.add(noButton);
        buttonContainer.add(yesButton);

        container.add(bodyContainer);
        container.add(buttonContainer);

    }

    @Override
    public void onLoad() {
        super.onLoad();
        setDefaultSize();
    }

    private void setDefaultSize() {
        Style containerStyle = container.getElement().getStyle();
        containerStyle.setPropertyPx("minWidth", 550);
        containerStyle.setPropertyPx("minHeight", 150);
    }

    private void fireDialogEvent(DialogActionEvent.DialogAction action) {
        hide();
        fireEvent(new DialogActionEvent(action));
    }

    public HandlerRegistration addDialogEventHandler(DialogActionEventHandler handler) {
        return addHandler(handler, DialogActionEvent.TYPE);
    }


}
