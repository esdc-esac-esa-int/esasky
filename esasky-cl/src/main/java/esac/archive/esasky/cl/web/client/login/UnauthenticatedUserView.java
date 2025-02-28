/*
ESASky
Copyright (C) 2025 European Space Agency

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

package esac.archive.esasky.cl.web.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import esac.archive.esasky.cl.web.client.event.ErrorEvent;
import esac.archive.esasky.cl.web.client.event.ErrorEventHandler;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.login.UserAreaPresenter;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.AutoHidePanel;

public class UnauthenticatedUserView extends AutoHidePanel implements UserAreaPresenter.View {

    Anchor loginAnchor;
    Anchor registerAnchor;
    private final Resources resources;
    private CssResource style;


    public interface Resources extends ClientBundle {
        @Source("unauthenticatedUserView.css")
        @CssResource.NotStrict
        CssResource style();
    }


    public UnauthenticatedUserView() {
        super();
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();

        initView();
    }

    private void initView() {
        this.addStyleName("loginPopupPanel");

        FlowPanel mainContainer = new FlowPanel();
        mainContainer.setStyleName("header__dropdown__content");
        mainContainer.addStyleName("loginPopupPanel__header_dropdown_content");

        loginAnchor = new Anchor(TextMgr.getInstance().getText("login"));
        registerAnchor = new Anchor(TextMgr.getInstance().getText("register_account"));

        mainContainer.add(loginAnchor);
        mainContainer.add(registerAnchor);
        add(mainContainer);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        setExactPopUpPosition();
    }

    private void setExactPopUpPosition() {
        int windowWidth = MainLayoutPanel.getMainAreaWidth();
        int panelWidth = getOffsetWidth();
        int leftPosition = windowWidth - panelWidth;
        int topPosition = 30;

        setPosition(leftPosition, topPosition);
    }


    @Override
    public HandlerRegistration addErrorHandler(ErrorEventHandler handler) {
        return addHandler(handler, ErrorEvent.TYPE);
    }


    @Override
    public void addCasRegisterClickHandler(ClickHandler handler) {
        registerAnchor.addClickHandler(handler);
    }

    @Override
    public void addCasLoginClickHandler(ClickHandler handler) {
        loginAnchor.addClickHandler(handler);
    }

    public void onErrorEvent(ErrorEvent event) {
        fireEvent(event);
    }

    @Override
    public void hideAll() {
        super.hide();
    }

}
