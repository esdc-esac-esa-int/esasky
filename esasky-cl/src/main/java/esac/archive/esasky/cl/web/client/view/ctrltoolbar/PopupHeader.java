package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.LabelWithHelpButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton.SignType;
public class PopupHeader extends FlowPanel{

	private final CssResource style;
	private Resources resources = GWT.create(Resources.class);

	private LabelWithHelpButton labelWithHelpButton;

	public static interface Resources extends ClientBundle {
		@Source("ctrlToolBarPopupHeader.css")
		@CssResource.NotStrict
		CssResource style();
	}
	
	public PopupHeader(final PopupPanel panel, String headerText, String helpText) {
		this(panel, headerText, helpText, headerText);
	}
	public PopupHeader(final PopupPanel panel, String headerText, String helpText, String helpHeader) {
		this(panel, headerText, helpText, helpHeader, null, "");
	}
	
	public PopupHeader(final PopupPanel panel, String headerText, String helpText, String helpHeader, ClickHandler onCloseClick, String closeTooltip) {
		this.style = resources.style();
		this.style.ensureInjected();

		labelWithHelpButton = new LabelWithHelpButton(headerText, helpText, helpHeader);
		labelWithHelpButton.addStyleName("popupHeaderLabelWithHelpButton");

		add(labelWithHelpButton);

		if(onCloseClick != null) {
			CloseButton closeButton = new CloseButton();
			closeButton.setTitle(closeTooltip);
			closeButton.addStyleName("popupHeaderCloseButton");
			closeButton.addClickHandler(onCloseClick);
			add(closeButton);
			labelWithHelpButton.addStyleName("popupHeaderLabelWithHelpButtonExtraMargin");
		}
		
		SignButton minimizeButton = new SignButton(SignType.MINUS);
		minimizeButton.addStyleName("popupHeaderMinimizeButton");
		minimizeButton.setTitle(TextMgr.getInstance().getText("popupHeader_minimize"));
		minimizeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				panel.hide();
			}
		});
		add(minimizeButton);
		addStyleName("popupHeaderContainer");
	}

	public void setText(String text) {
		labelWithHelpButton.setText(text);
	}
	
	public void setHelpText(String text) {
		labelWithHelpButton.setDialogMessage(text);
	}
}
