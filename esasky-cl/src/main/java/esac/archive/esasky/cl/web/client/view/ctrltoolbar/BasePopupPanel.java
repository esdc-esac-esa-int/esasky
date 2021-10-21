package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.Hidable;

public class BasePopupPanel extends PopupPanel implements Hidable<PopupPanel> {

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

	@Override
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
}
