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

package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.view.animation.NumberAnimation;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;

public class BadgeButton extends FlowPanel{
	
	private EsaSkyToggleButton button;
	private Label numberFound;
	private String baseEnabledTooltip = "";
	private String enabledTooltip = "";
	private String disabledTooltip = "";
	private String target = "";
	
	private NumberAnimation numberAnimation;
	public BadgeButton(EsaSkyToggleButton button) {
		this.button = button;
		this.button.addStyleName("badgeButton");
		this.add(button);
		this.baseEnabledTooltip = button.getTitle();
		this.enabledTooltip = baseEnabledTooltip;
		numberFound = new Label("0");
		numberFound.addStyleName("numberFound");
		this.add(numberFound);
		numberAnimation = new NumberAnimation(numberFound);
		addStyleName("badgeButtonContainer");
	}
	
	public void setDisabledTooltip(String disabledTooltip){
		this.disabledTooltip = disabledTooltip;
	}
	
	public void updateCount(long newCount){
		numberAnimation.animateTo(newCount, 1000);
	}
	
	public void setTargetName(String targetName){
		this.target = targetName;
		updateTooltip();
		enable();
	}
	
	private void updateTooltip(){
		enabledTooltip =  baseEnabledTooltip.replace("$TARGET$", target);
	}
	
	public void disable(){
		button.setEnabled(false);
		numberFound.addStyleName("displayNone");
		button.setTitle(disabledTooltip);
	}
	
	public void enable(){
		button.setEnabled(true);
		numberFound.removeStyleName("displayNone");
		button.setTitle(enabledTooltip);
	}
	
	public boolean isEnabled() {
		return button.isEnabled();
	}
	
	public void setToggleStatus(boolean toggleStatus) {
		button.setToggleStatus(toggleStatus);
	}
	
	public boolean getToggleStatus() {
		return button.getToggleStatus();
	}
	
	public HasClickHandlers getClickableArea() {
		return button;
	}
}