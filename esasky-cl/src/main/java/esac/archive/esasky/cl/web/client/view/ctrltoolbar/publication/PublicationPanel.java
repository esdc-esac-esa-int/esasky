package esac.archive.esasky.cl.web.client.view.ctrltoolbar.publication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.PublicationPanelPresenter;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.EsaSkyNumberBox;
import esac.archive.esasky.cl.web.client.view.common.EsaSkySwitch;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;

public class PublicationPanel extends DialogBox implements PublicationPanelPresenter.View {

	private PopupHeader header;
	private final Resources resources;
	private CssResource style;

	private boolean isShowing;

	private FlowPanel publicationPanel = new FlowPanel();
	
	private EsaSkyButton updatePublicationsButton;
	private Label publicationStatusText;
	private Label maxFoVWarning;
	private EsaSkySwitch updateOnMoveSwitch;
	private LoadingSpinner loadingSpinner = new LoadingSpinner(false);
	private FlowPanel statusContainer = new FlowPanel();
	private DropDownMenu<String> orderByDropdown;
	private final Label warningLabel = new Label();;
	private HTML slider = new HTML();
	private FlowPanel advancedOptions;
	private ClickHandler removeButtonClickHandler;
	private EsaSkyNumberBox numberBox;
	private String updateOnMoveSwitchId = "publications__updateOnMove";
	private final String sliderId = "publications__sourceLimitSlider";

	public static interface Resources extends ClientBundle {

		@Source("publications_outline.png")
		ImageResource publications();
		
		@Source("refresh_outline.png")
		ImageResource refresh();
		
		@Source("publicationPanel.css")
		@CssResource.NotStrict
		CssResource style();
	}

	public PublicationPanel() {
		super(false, false);

		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();

		initView();
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				setMaxSize();
			}
		});
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		setMaxSize();
		addSliderListener(this, sliderId);
	}
	
	private native void addSliderListener(PublicationPanel instance, String sliderId) /*-{
		var slider = $doc.getElementById(sliderId);
		
		slider.oninput = function() {
			instance.@esac.archive.esasky.cl.web.client.view.ctrltoolbar.publication.PublicationPanel::fireSliderChangedEvent(D)(this.value);
		}
		
	}-*/;
	
	private native void setSliderValue(double value, String sliderId) /*-{
		var slider = $doc.getElementById(sliderId);
		slider.value = value;
	}-*/;
	
	private void fireSliderChangedEvent(double newValue) {
		numberBox.setNumber(newValue);
	}

	private void initView() {
		this.removeStyleName("gwt-DialogBox");
		this.getElement().addClassName("publicationPanel");

		header = new PopupHeader(this, TextMgr.getInstance().getText("publicationPanel_title"), 
				TextMgr.getInstance().getText("publicationPanel_helpText"), 
				TextMgr.getInstance().getText("publicationPanel_title"), 
				new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				removeButtonClickHandler.onClick(event);				
			}
		}, TextMgr.getInstance().getText("publicationPanel_removeAndClose"));

		publicationPanel.add(header);
		
		updatePublicationsButton = new EsaSkyButton(this.resources.refresh());
		updatePublicationsButton.addStyleName("publicationPanel__button");
		updatePublicationsButton.setMediumStyle();
		updatePublicationsButton.setTitle(TextMgr.getInstance().getText("publicationPanel_refreshTooltip"));
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.addStyleName("publicationPanel__buttonContainer");
		buttonContainer.add(updatePublicationsButton);
		
		updateOnMoveSwitch = new EsaSkySwitch(updateOnMoveSwitchId, false,
				TextMgr.getInstance().getText("publicationPanel_updateOnMove"), 
				TextMgr.getInstance().getText("publicationPanel_updateOnMoveTooltip"));
		updateOnMoveSwitch.addStyleName("publicationPanel__updateOnMoveSwitchContainer");
		buttonContainer.add(updateOnMoveSwitch);
		
		publicationStatusText = new Label();
		publicationStatusText.addStyleName("publication__statusText");
		loadingSpinner.addStyleName("publicationPanel__loadingSpinner");
		statusContainer.addStyleName("publicationPanel__statusContainer");
		statusContainer.add(loadingSpinner);
		statusContainer.add(publicationStatusText);
		
		maxFoVWarning = new Label();
		maxFoVWarning.addStyleName("publicationPanel__maxFovWarning");
		
		advancedOptions = new FlowPanel();
		warningLabel.setText(TextMgr.getInstance().getText("publicationPanel_sourceLimitWarning"));
		warningLabel.addStyleName("publicationPanel__warningLabel");
		warningLabel.setVisible(false);
		
		Label truncationLabel = new Label(TextMgr.getInstance().getText("publicationPanel_truncationSettings"));
		truncationLabel.addStyleName("publicationPanel__truncationLabel");
		
		numberBox = new EsaSkyNumberBox(NumberFormat.getFormat("#0"), 1);
		numberBox.addStyleName("publicationPanel__limitTextBoxContainer");

		slider.addStyleName("publicationPanel__slideContainer");
		FlowPanel sourceLimitContainer = new FlowPanel();
		sourceLimitContainer.addStyleName("publicationPanel__sourceLimitContainer");
		sourceLimitContainer.add(numberBox);
		sourceLimitContainer.add(slider);
		
		orderByDropdown = new DropDownMenu<String>("", TextMgr.getInstance().getText("publicationPanel_orderByTooltip"), 200, "publicationPanel__orderByDropdown");
		orderByDropdown.addStyleName("publicationPanel__dropdown");
		
		publicationPanel.add(buttonContainer);
		publicationPanel.add(statusContainer);
		publicationPanel.add(maxFoVWarning);
		publicationPanel.add(advancedOptions);
		
		advancedOptions.add(truncationLabel);
		advancedOptions.add(orderByDropdown);
		advancedOptions.add(sourceLimitContainer);
		advancedOptions.add(warningLabel);

		this.add(publicationPanel);
	}
	
	private void setMaxSize() {
		Style style = getElement().getStyle();
		style.setPropertyPx("maxWidth", MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15);
		style.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15);
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
		if(isShowing()) {
			hide();
		} else {
			show();
		}
	}
	
	@Override
	public boolean isShowing() {
		return isShowing;
	}

	@Override
	public EsaSkyButton getUpdateButton() {
		return updatePublicationsButton;
	}

	@Override
	public void addRemoveButtonClickHandler(ClickHandler handler) {
		removeButtonClickHandler = handler;
	}
	
	@Override
	public void setPublicationStatusText(String statusText) {
		publicationStatusText.setText(statusText);
		publicationStatusText.setVisible(true);
	}

	@Override
	public void setMaxFoV(boolean maxFov) {
		maxFoVWarning.setVisible(maxFov);
		updatePublicationsButton.setEnabled(!maxFov);
	}

	@Override
	public void addUpdateOnMoveSwitchClickHandler(ClickHandler handler) {
		updateOnMoveSwitch.addClickHandler(handler);
	}

	@Override
	public void setLoadingSpinnerVisible(boolean visible) {
		loadingSpinner.setVisible(visible);
	}

	@Override
	public int getLimit() {
		return (int)Math.round(numberBox.getNumber());
	}
	
	@Override
	public void addSourceLimitOnValueChangeHandler(ValueChangeHandler<String> handler) {
		numberBox.addValueChangeHandler(handler);
	}

	@Override
	public void onlyShowFovWarning(boolean onlyShowFovWarning) {
		updatePublicationsButton.setVisible(!onlyShowFovWarning);
		updateOnMoveSwitch.setVisible(!onlyShowFovWarning);
		publicationStatusText.setVisible(!onlyShowFovWarning);
		loadingSpinner.setVisible(!onlyShowFovWarning);
		advancedOptions.setVisible(!onlyShowFovWarning);
	}

	@Override
	public String getOrderByValue() {
		return orderByDropdown.getSelectedObject();
	}
	
	@Override
	public String getOrderByDescription() {
		for(MenuItem<String> menuItem : orderByDropdown.getMenuItems()) {
			if(menuItem.getItem() == orderByDropdown.getSelectedObject()) {
				return menuItem.getText();
			}
		}
		return "";
	}

	@Override
	public void addTruncationOption(MenuItem<String> menuItem) {
		orderByDropdown.addMenuItem(menuItem);
		if(orderByDropdown.getMenuItems().size() == 1) {
			orderByDropdown.selectObject(orderByDropdown.getMenuItems().get(0).getItem());
		}
	}

	@Override
	public void setUpdateOnMoveSwitchValue(boolean checked) {
		updateOnMoveSwitch.setChecked(checked);
	}

	@Override
	public void setMaxFovText(String text) {
		maxFoVWarning.setText(text);
	}

	@Override
	public void setSourceLimitValues(final int value, int min, int max) {
		numberBox.setNumber(value);
		numberBox.setMinNumber(min);
		numberBox.setMaxNumber(max);
		
		slider.setHTML("<input type=\"range\" value=\"" + value + "\" min=\"" + min + "\" max=\"" + max + "\" "
				+ "class=\"slider\" id=\"" + sliderId + "\">");
		numberBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setSliderValue(numberBox.getNumber(), sliderId);
				warningLabel.setVisible(numberBox.getNumber() > value);
			}
		});
	}
	
	@Override
	public void addTruncationOptionObserver(MenuObserver observer) {
		orderByDropdown.registerObserver(observer);
	}
}
