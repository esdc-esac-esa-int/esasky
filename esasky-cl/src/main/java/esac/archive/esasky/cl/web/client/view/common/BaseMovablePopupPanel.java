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

    protected void setHeaderText(String headerText) {
        header.setText(headerText);
    }
}
