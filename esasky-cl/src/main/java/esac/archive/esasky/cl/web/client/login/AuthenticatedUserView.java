package esac.archive.esasky.cl.web.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import esac.archive.esasky.cl.web.client.event.ErrorEvent;
import esac.archive.esasky.cl.web.client.event.ErrorEventHandler;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.login.UserAreaPresenter;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.view.common.AutoHidePanel;
import esac.archive.esasky.cl.web.client.view.common.Hidable;
import esac.archive.esasky.cl.web.client.view.common.TabulatorPopupSearchPanel;

import java.util.ArrayList;
import java.util.List;

public class AuthenticatedUserView extends AutoHidePanel implements UserAreaPresenter.View {

    private Label title;
    private Label signoutLabel;
    private final Resources resources;
    private CssResource style;

    List<Hidable<TabulatorPopupSearchPanel>> dropdownPanels;

    public interface Resources extends ClientBundle {
        @Source("authenticatedUserView.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public AuthenticatedUserView() {
        super();
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        initView();
    }

    private void initView() {
        setStyleName("statusPopupPanel");

        dropdownPanels = new ArrayList<>();

        FlowPanel mainContainer = new FlowPanel();
        mainContainer.setStyleName("header__dropdown__content");
        mainContainer.addStyleName("statusPopupPanel__header_dropdown_content");

        UserInfoPanel userInfoPanel = new UserInfoPanel();

        title = new Label("-");
        title.addStyleName("statusPopupPanel__header_dropdown_content_title");
        title.addClickHandler(event -> userInfoPanel.show());

        Anchor tablesAnchor = new Anchor(TextMgr.getInstance().getText("userArea__dropdown__tables"));
        tablesAnchor.addClickHandler(event -> {
            UserTablePanel userTablePanel = new UserTablePanel();
            userTablePanel.addErrorHandler(this::onErrorEvent);
            showPanel(userTablePanel);
            dropdownPanels.clear();
            dropdownPanels.add(userTablePanel);
        });

        Anchor sessionAnchor = new Anchor(TextMgr.getInstance().getText("userArea__dropdown__sessions"));
        sessionAnchor.addClickHandler(event -> {
            UserSessionPanel userSessionPanel = new UserSessionPanel();
            userSessionPanel.addErrorHandler(this::onErrorEvent);
            showPanel(userSessionPanel);
            dropdownPanels.clear();
            dropdownPanels.add(userSessionPanel);
        });

        Anchor accountAnchor = new Anchor(TextMgr.getInstance().getText("userArea__dropdown__layouts"));
        accountAnchor.addClickHandler(event -> {
            UserLayoutPanel userLayoutPanel = new UserLayoutPanel();
            userLayoutPanel.addErrorHandler(this::onErrorEvent);
            showPanel(userLayoutPanel);
            dropdownPanels.clear();
            dropdownPanels.add(userLayoutPanel);
        });

        signoutLabel = new Label(TextMgr.getInstance().getText(TextMgr.getInstance().getText("logout")));
        signoutLabel.addStyleName("statusPopupPanel__header_dropdown_content_footer");


        mainContainer.add(title);
        mainContainer.add(tablesAnchor);
        mainContainer.add(sessionAnchor);
        mainContainer.add(accountAnchor);
        mainContainer.add(signoutLabel);

        addResizeHandler();
        add(mainContainer);
    }


    private void showPanel(Hidable<TabulatorPopupSearchPanel> panel) {
        for (Hidable<TabulatorPopupSearchPanel> hidable : dropdownPanels) {
            if (hidable != panel) {
                hidable.hide();
            }
        }
        panel.show();
        hide();
    }

    @Override
    public void show() {
        UserDetails details = GUISessionStatus.getUserDetails();
        if (details != null) {
            title.setText(details.getId());
        }
        super.show();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        setExactPopUpPosition();
    }

    private void setExactPopUpPosition() {
        int windowWidth = Window.getClientWidth();
        int panelWidth = getOffsetWidth();
        int leftPosition = windowWidth - panelWidth;
        int topPosition = 30;

        setPosition(leftPosition, topPosition);
    }

    private void addResizeHandler() {
        Window.addResizeHandler(event -> Scheduler.get().scheduleDeferred(this::setExactPopUpPosition));
    }

    @Override
    public void addCasLogoutClickHandler(ClickHandler handler) {
        this.signoutLabel.addClickHandler(handler);
    }

    @Override
    public HandlerRegistration addErrorHandler(ErrorEventHandler handler) {
        return addHandler(handler, ErrorEvent.TYPE);
    }

    public void onErrorEvent(ErrorEvent event) {
        fireEvent(event);
    }
    @Override
    public void hideAll() {
        for (Hidable<TabulatorPopupSearchPanel> hidable : dropdownPanels) {
            hidable.hide();
        }

        super.hide();
    }
}
