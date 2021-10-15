package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import esac.archive.esasky.cl.web.client.model.Size;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;

public class BasePopupPanel extends PopupPanel {

	private boolean isShowing = false;
	public BasePopupPanel() {
		super(false, false);
		MainLayoutPanel.addMainAreaResizeHandler(event -> setMaxSize());
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		setMaxSize();
	}
	
	protected void setMaxSize() {
		Style elementStyle = getElement().getStyle();
		elementStyle.setPropertyPx("maxWidth", MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15);
		elementStyle.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15);
	}
	
	@Override
	public void setPopupPosition(int left, int top) {
		setMaxSize();
	}

	@Override
	public void show() {
		isShowing = true;
		this.removeStyleName("displayNone");
		setMaxSize();
	}

	@Override
	public void hide(boolean autohide) {
		this.addStyleName("displayNone");
		isShowing = false;
		CloseEvent.fire(this, null);
	}

	public void toggle() {
		if (isShowing()) {
			hide();
		} else {
			show();
		}
	}

	@Override
	public boolean isShowing() {
		return isShowing;
	}

	protected Size getDefaultSize() {
		int width = 610;
		int height = MainLayoutPanel.getMainAreaHeight();

		if (MainLayoutPanel.getMainAreaWidth() < 1500) {
			width = 500;
		}
		if (MainLayoutPanel.getMainAreaWidth() < 1100) {
			width = 350;
		}
		if (MainLayoutPanel.getMainAreaWidth() < 450) {
			height = 300;
		}
		if (height > 400) {
			height = 400;
		}
		if (!DeviceUtils.isMobileOrTablet() && height > MainLayoutPanel.getMainAreaHeight() / 2) {
			height = MainLayoutPanel.getMainAreaHeight() / 2;
		}
		if (height > MainLayoutPanel.getMainAreaHeight() - 30 - 2) {
			height = MainLayoutPanel.getMainAreaHeight() - 30 - 2;
		}
		if (width > MainLayoutPanel.getMainAreaWidth()) {
			width = MainLayoutPanel.getMainAreaWidth() - 2;
		}

		return new Size(width, height);
	}
}
