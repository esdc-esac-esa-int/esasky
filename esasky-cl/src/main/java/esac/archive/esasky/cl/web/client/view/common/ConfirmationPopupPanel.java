/*
ESASky
Copyright (C) 2025 Henrik Norman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
        containerStyle.setPropertyPx("minWidth", 500);
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
