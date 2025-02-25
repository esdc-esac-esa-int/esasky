package esac.archive.esasky.cl.web.client.login;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.common.BaseMovablePopupPanel;
import esac.archive.esasky.cl.web.client.view.common.RemainingSpaceBar;

public class UserInfoPanel extends BaseMovablePopupPanel {

    private final Label usernameLabel = new Label();
    private final RemainingSpaceBar spaceBar = new RemainingSpaceBar();
    private final Label spaceLabel = new Label();
    private final Label timeoutLabel = new Label();

    public UserInfoPanel() {
        super(GoogleAnalytics.CAT_USERAREA, TextMgr.getInstance().getText("userArea__userInfoPopup_title"), TextMgr.getInstance().getText("userArea__userInfoPopup_help"));
        initView();
    }

    public void initView() {
        VerticalPanel container = new VerticalPanel();
        Label usernameHeader = new Label(TextMgr.getInstance().getText("userArea__userInfoPopup_username"));
        usernameHeader.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        Label tableQuotaHeader = new Label(TextMgr.getInstance().getText("userArea__userInfoPopup_db_quota"));
        tableQuotaHeader.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        Label timeoutHeader = new Label(TextMgr.getInstance().getText("userArea__userInfoPopup_req_timeout"));
        timeoutHeader.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);

        VerticalPanel usernameContainer = new VerticalPanel();
        VerticalPanel tableQuotaContainer = new VerticalPanel();
        VerticalPanel timeoutContainer = new VerticalPanel();

        usernameContainer.setSpacing(10);
        tableQuotaContainer.setSpacing(10);
        timeoutContainer.setSpacing(10);

        usernameContainer.add(usernameHeader);
        usernameContainer.add(usernameLabel);
        tableQuotaContainer.add(tableQuotaHeader);
        tableQuotaContainer.add(spaceBar);
        tableQuotaContainer.add(spaceLabel);
        timeoutContainer.add(timeoutHeader);
        timeoutContainer.add(timeoutLabel);


        spaceBar.hideLabel(true);

        container.add(usernameContainer);
        container.add(tableQuotaContainer);
        container.add(timeoutContainer);

        container.setSpacing(10);
        this.add(container);
    }


    @Override
    public void show() {
        UserDetails details = GUISessionStatus.getUserDetails();
        if (details != null) {
            usernameLabel.setText(details.getName() + " (" + details.getId() + ")");
            spaceBar.setSpace(details.getQuotaDb(), details.getCurrentSizeDb());
            spaceLabel.setText(spaceBar.getTitle());
            timeoutLabel.setText(details.getSyncMaxExecTime() + " s");
        }

        super.show();
    }

}
