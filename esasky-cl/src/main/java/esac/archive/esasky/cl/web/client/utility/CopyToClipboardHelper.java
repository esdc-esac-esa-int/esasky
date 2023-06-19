package esac.archive.esasky.cl.web.client.utility;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

import esac.archive.esasky.cl.web.client.view.common.AutoHidePanel;


public class CopyToClipboardHelper {
	
	private final CssResource style;
	private Resources resources = GWT.create(Resources.class);
	
	private static CopyToClipboardHelper instance = null;
	
	private AutoHidePanel copyPopupPanel = new AutoHidePanel();
	private AutoHidePanel copyNotification = new AutoHidePanel();
	private TextBox textBoxToCopyFrom = new TextBox();
	private Label copyNotificationLabel = new Label();
	private Timer copyNotificationTimer = new Timer() {

		@Override
		public void run() {
			copyNotification.hide();
		}
	};
	
	public static interface Resources extends ClientBundle {

		@Source("copyToClipboardHelper.css")
		@CssResource.NotStrict
		CssResource style();
	}
	
	private CopyToClipboardHelper() {
		this.style = resources.style();
		this.style.ensureInjected();
		copyNotificationLabel.addStyleName("copyNotificationLabel");
		copyNotification.add(copyNotificationLabel);
		copyNotification.addStyleName("copyNotification");
		
		copyPopupPanel.addStyleName("copyPopupPanel");
		
		textBoxToCopyFrom.addStyleName("textBoxToCopyFrom");
		copyPopupPanel.add(textBoxToCopyFrom);
	}
	
	public static CopyToClipboardHelper getInstance() {
		if(instance == null) {
			instance = new CopyToClipboardHelper();
		}
		return instance;
	}
	
	public void copyToClipBoard(String text, String copyNotificationText) {

		textBoxToCopyFrom.setValue(text);
		copyPopupPanel.show();
		textBoxToCopyFrom.setFocus(true);
		textBoxToCopyFrom.selectAll();
		textBoxToCopyFrom.setWidth(calculateWidth(text) + 12 + "px");
		if(tryAutomaticCopyToClipboardJS() || navigatorCopyToClipboard(text)) {
			copyPopupPanel.hide();
			copyNotificationLabel.setText(copyNotificationText);
			copyNotification.show();
			copyNotificationTimer.schedule(2500);
		}
	}
	
	private int calculateWidth(String text) {
		PopupPanel widthLabelPanel = new PopupPanel();
		Label widthLabel = new Label(text);
		widthLabelPanel.add(widthLabel);
		widthLabelPanel.show();
		int width = widthLabel.getOffsetWidth();
		widthLabelPanel.hide();
		return width;
	}
	
	private native boolean tryAutomaticCopyToClipboardJS() /*-{
		try {
			$doc.execCommand('copy');
			return true;
		} catch (err) {
			return false;
		}

	}-*/;

	private static native boolean navigatorCopyToClipboard(String copyText) /*-{
    	try {
			$wnd.navigator.clipboard.writeText(copyText);
            return true;
		} catch(err) {
            return false;
		}
	}-*/;
}
