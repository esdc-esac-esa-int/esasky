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