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

package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.common.Hidable;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.LabelWithHelpButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton.SignType;
public class PopupHeader<T> extends FlowPanel{

	private final CssResource style;
	private Resources resources = GWT.create(Resources.class);

	private final LabelWithHelpButton labelWithHelpButton;

	private final FlowPanel actionPanel = new FlowPanel();
	private final FlowPanel prefixPanel = new FlowPanel();

	public interface Resources extends ClientBundle {
		@Source("ctrlToolBarPopupHeader.css")
		@CssResource.NotStrict
		CssResource style();
	}
	
	public PopupHeader(final Hidable<? extends T> panel, String headerText, String helpText) {
		this(panel, headerText, helpText, headerText);
	}
	public PopupHeader(final Hidable<? extends T> panel, String headerText, String helpText, String helpHeader) {
		this(panel, headerText, helpText, helpHeader, null, "");
	}
	
	public PopupHeader(final Hidable<? extends T> panel, String headerText, String helpText, String helpHeader, ClickHandler onCloseClick, String closeTooltip) {
		this.style = resources.style();
		this.style.ensureInjected();

		labelWithHelpButton = new LabelWithHelpButton(headerText, helpText, helpHeader);
		labelWithHelpButton.addStyleName("popupHeaderLabelWithHelpButton");


		FlowPanel titlePanel = new FlowPanel();
		titlePanel.addStyleName("titlePanel");
		prefixPanel.addStyleName("popupHeaderPrefixPanel");
		titlePanel.add(prefixPanel);
		titlePanel.add(labelWithHelpButton);
		add(titlePanel);


		actionPanel.addStyleName("popupHeaderActionPanel");
		add(actionPanel);


		if(onCloseClick != null) {
			CloseButton closeButton = new CloseButton();
			closeButton.setTitle(closeTooltip);
			closeButton.addStyleName("popupHeaderCloseButton");
			closeButton.addClickHandler(onCloseClick);
			actionPanel.add(closeButton);
			labelWithHelpButton.addStyleName("popupHeaderLabelWithHelpButtonExtraMargin");
		}
		
		SignButton minimizeButton = new SignButton(SignType.MINUS);
		minimizeButton.addStyleName("popupHeaderMinimizeButton");
		minimizeButton.setTitle(TextMgr.getInstance().getText("popupHeader_minimize"));
		minimizeButton.addClickHandler(event -> panel.hide());
		actionPanel.add(minimizeButton);
		addStyleName("popupHeaderContainer");

	}

	public void setText(String text) {
		labelWithHelpButton.setText(text);
	}
	
	public void setHelpText(String text) {
		labelWithHelpButton.setHelpButtonVisibility(text != null && !text.isEmpty());
		labelWithHelpButton.setDialogMessage(text);
	}

	public void addActionWidget(Widget widget) {
		actionPanel.insert(widget, 0);
	}

	public void removeActionWidget(Widget widget) {
		actionPanel.remove(widget);
	}

	public void addPrefixWidget(Widget widget) {
		prefixPanel.add(widget);
	}

	public void removePrefixWidget(Widget widget) {
		prefixPanel.remove(widget);
	}
}
