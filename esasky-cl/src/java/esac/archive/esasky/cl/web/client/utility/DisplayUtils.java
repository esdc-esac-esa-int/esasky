package esac.archive.esasky.cl.web.client.utility;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.ammi.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.AutoHidePanel;

/**
 * Class whose main purpose is to centralize the place where the action "Window.open()" is invoked.
 * @author mhsarmiento Copyright (c) 2016 - European Space Agency.
 */
public final class DisplayUtils {

    /** default message for launching a VOApp. */
    private static final String LAUNCH_VO_APP_MESSAGE = "[Launch @@ ]";
    
    private static MessageDialogBox lastShownMessageDialogBox;

    public static interface Resources extends ClientBundle {
        @Source("displayUtils.css")
        @CssResource.NotStrict
        CssResource style();
    }

    private DisplayUtils() {}

    /**
     * Creates an ancillary panel with the links to launch the main SAMP-compatible apps.
     * @return Widget
     */
    public static String createSampAppsWidgets() {

        StringBuilder message = new StringBuilder();
        message.append("<div class=\"samp-message-dialog-text-panel\">");
        message.append("<h2>" + TextMgr.getInstance().getText("displayUtils_connectivityProblems") + "</h2>");  
        message.append("<p>" + TextMgr.getInstance().getText("displayUtils_VOnotFound") + ".</p>");
        message.append("<p>" + TextMgr.getInstance().getText("displayUtils_startOneOf") + ":</p>");
        message.append("<ul><li><a  href=\"" + SampConstants.ALADIN_JNLP_URL
                + "\" target=\"_blank\">" + SampConstants.ALADIN_APP + "</a></li>");
        message.append("<li><a  href=\"" + SampConstants.TOPCAT_JNLP_URL + "\" target=\"_blank\">"
                + SampConstants.TOPCAT_APP + "</a></li>");
        message.append("<li><a  href=\""
                + SampConstants.DS9_JNLP_URL
                + "\" target=\"_blank\">"
                + SampConstants.DS9_APP
                + "</a> (" + TextMgr.getInstance().getText("displayUtils_toCommunicateWithDS9") + " <a href=\""
                + SampConstants.SAMP_HUB_URL + "\" target=\"_blank\">" + SampConstants.HUB_APP
                + "</a> hub,");
        message.append(" " + TextMgr.getInstance().getText("displayUtils_seeHelp") + ")</li></ul>");
        message.append("<p>" + TextMgr.getInstance().getText("displayUtils_tryAgain") + "</p>");
        message.append("</div>");

        return message.toString();
    }

    /**
     * Creates an ancillary panel with the links to launch the main SAMP-compatible apps.
     * @param voAppUrl Input String
     * @param voAppName Input String
     * @return Widget
     */
    public static Widget createSampAppsWidgets(final String voAppUrl, final String voAppName) {
        VerticalPanel panel = new VerticalPanel();

        // Create link to the VO App.
        Anchor voAppLink = new Anchor(LAUNCH_VO_APP_MESSAGE.replace(
                EsaSkyConstants.REPLACE_PATTERN, voAppName));
        voAppLink.addClickHandler(createHandlerForSampApps(voAppUrl));
        panel.add(voAppLink);
        return panel;
    }

    /**
     * Creates a click handler to launch in another browser/tab an url.
     * @param url Input String
     * @return ClickHandler
     */
    private static ClickHandler createHandlerForSampApps(final String url) {
        return new ClickHandler() {

            @Override
            public void onClick(final ClickEvent arg0) {
                Window.open(url, "_blanck", "location=0,status=0,toolbar=0,scrollbars=1,menubar=0");
            }
        };
    }

    /**
     * showMessageDialogBox().
     * @param messageText Input String
     * @param headerTitle Input String
     * @param dialogId Input String
     */
    public static MessageDialogBox showMessageDialogBox(final String messageText, final String headerTitle,
            final String dialogId) {
        
        if(lastShownMessageDialogBox != null && lastShownMessageDialogBox.isShowing() 
                && lastShownMessageDialogBox.getId() != null && lastShownMessageDialogBox.getId() == dialogId){
            return lastShownMessageDialogBox;
        }
        
        Resources resources = GWT.create(Resources.class);
        CssResource style = resources.style();
        style.ensureInjected();
        
        lastShownMessageDialogBox = new MessageDialogBox(new HTML(messageText), headerTitle, dialogId);
        
        lastShownMessageDialogBox.show();
        return lastShownMessageDialogBox;
    }
    
    public static MessageDialogBox showAndOverrideLastShownDialogBox(final String messageText, final String headerTitle, final String dialogId) {
    	if(lastShownMessageDialogBox != null) {
    		lastShownMessageDialogBox.hide();
    	}
    	return showMessageDialogBox(messageText, headerTitle, dialogId);
    }

    public static void showInsideMainAreaPointingAtPosition(AutoHidePanel panel, int x, int y) {
    	panel.setPosition(x, y);
		panel.getElement().getStyle().setProperty("borderRadius", "0px 10px 10px 10px");
		panel.getElement().getStyle().setProperty("boxSizing", "border-box");
		panel.show();
		panel.setWidth("auto");
		boolean isWayOffToTheRight = false;
		if(x + panel.getOffsetWidth() > MainLayoutPanel.getMainAreaWidth()) {
			isWayOffToTheRight = true;
			panel.setPosition(0, y);
			panel.setPosition(x - panel.getOffsetWidth(), y);
			if(panel.getOffsetWidth() > x) {
				panel.setWidth(x + "px");
				panel.setPosition(x - panel.getOffsetWidth(), y);
			} 
			panel.getElement().getStyle().setProperty("borderRadius", "10px 0px 10px 10px");
		}

		if(y + panel.getOffsetHeight() > MainLayoutPanel.getMainAreaHeight()) {
			if(isWayOffToTheRight) {
				panel.setPosition(0, 0);
				panel.setPosition(x - panel.getOffsetWidth(), y - panel.getOffsetHeight());
				panel.getElement().getStyle().setProperty("borderRadius", "10px 10px 0px 10px");
			} else {
				panel.setPosition(x, 0); //To make getOffsetHeight return full width
				panel.setPosition(x, y - panel.getOffsetHeight());
				panel.getElement().getStyle().setProperty("borderRadius", "10px 10px 10px 0px");
			}
		}

    }
}
