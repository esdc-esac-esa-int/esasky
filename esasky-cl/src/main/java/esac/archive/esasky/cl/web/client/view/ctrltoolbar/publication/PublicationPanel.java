package esac.archive.esasky.cl.web.client.view.ctrltoolbar.publication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.presenter.PublicationPanelPresenter;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.EsaSkyNumberBox;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.Toggler;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;

public class PublicationPanel extends DialogBox implements PublicationPanelPresenter.View {

	private PopupHeader header;
	private final Resources resources;
	private CssResource style;

	private boolean isShowing;

	private FlowPanel publicationPanel = new FlowPanel();
	
	private CloseButton removePublicationsButton = new CloseButton();
	private EsaSkyButton recentreButton;
	private EsaSkyButton doInitialPublicationQueryButton;
	private EsaSkyButton updatePublicationsButton;
	private Label publicationStatusText;
	private Label maxFoVWarningVisible;
	private CheckBox updateOnMoveCheckBox;
	private LoadingSpinner loadingSpinner = new LoadingSpinner(false);
	private FlowPanel statusContainer = new FlowPanel();
	
	private EsaSkyNumberBox numberBox;

	public static interface Resources extends ClientBundle {

		@Source("publications_outline.png")
		ImageResource publications();
		
		@Source("refresh_outline.png")
		ImageResource refresh();

		@Source("recenter_outline.png")
		ImageResource recentre();
		
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
		addSliderListener(this);
	}
	
	private native void addSliderListener(PublicationPanel instance) /*-{
		var slider = $doc.getElementById("myRange");
		
		slider.oninput = function() {
			instance.@esac.archive.esasky.cl.web.client.view.ctrltoolbar.publication.PublicationPanel::fireSliderChangedEvent(D)(this.value);
		}
		
	}-*/;
	
	private native void setSliderValue(double value) /*-{
		var slider = $doc.getElementById("myRange");
		slider.value = value;
	}-*/;
	
	private void fireSliderChangedEvent(double newValue) {
		numberBox.setNumber(newValue);
	}
	
	private void initView() {
		this.removeStyleName("gwt-DialogBox");
		this.getElement().addClassName("publicationPanel");

		header = new PopupHeader(this, "Publications",
//				TextMgr.getInstance().getText("sky_selectSky_help"));
				"Not written yet :)");
		publicationPanel.add(header);
		
		
		removePublicationsButton.addStyleName("publicationPanel__button");
		removePublicationsButton.setMediumStyle();
		
		recentreButton = new EsaSkyButton(this.resources.recentre());
		recentreButton.addStyleName("publicationPanel__button");
		recentreButton.setMediumStyle();
		
		doInitialPublicationQueryButton = new EsaSkyButton(this.resources.publications());
		doInitialPublicationQueryButton.addStyleName("publicationPanel__button");
		doInitialPublicationQueryButton.setMediumStyle();
		
		updatePublicationsButton = new EsaSkyButton(this.resources.refresh());
		updatePublicationsButton.addStyleName("publicationPanel__button");
		updatePublicationsButton.setMediumStyle();
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.addStyleName("publicationPanel__buttonContainer");
		buttonContainer.add(doInitialPublicationQueryButton);
		buttonContainer.add(updatePublicationsButton);
		buttonContainer.add(recentreButton);
		buttonContainer.add(removePublicationsButton);
		
		updateOnMoveCheckBox = new CheckBox("Update on move");
		updateOnMoveCheckBox.addStyleName("publicationPanel__checkBox");
		
		publicationStatusText = new Label("Updating... xx sources with publications in the FoV");
		publicationStatusText.addStyleName("publication__statusText");
		loadingSpinner.addStyleName("publicationPanel__loadingSpinner");
		statusContainer.addStyleName("publicationPanel__statusContainer");
		statusContainer.add(loadingSpinner);
		statusContainer.add(publicationStatusText);
		
		maxFoVWarningVisible = new Label("Must be closer than 25 \u00B0");
		maxFoVWarningVisible.addStyleName("publicationPanel__maxFovWarning");
		
		FlowPanel advancedOptions = new FlowPanel();
		Toggler advancedOptionsToggler = new Toggler(advancedOptions);
		advancedOptionsToggler.setText("Advanced");
		final Label warningLabel = new Label("Warning: A high source limit may degrade performance");
		warningLabel.addStyleName("publicationPanel__warningLabel");
		warningLabel.setVisible(false);
		
		Label truncationLabel = new Label("Truncation settings");
		truncationLabel.addStyleName("publicationPanel__truncationLabel");
		
		numberBox = new EsaSkyNumberBox(NumberFormat.getFormat("#0"), 1);
		numberBox.addStyleName("publicationPanel__limitTextBoxContainer");
		numberBox.setNumber(3000); //TODO get default from presenter
		numberBox.setMaxNumber(50000); //TODO add simbad hard limit to help description
		numberBox.setMinNumber(1);
		numberBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setSliderValue(numberBox.getNumber());
				warningLabel.setVisible(numberBox.getNumber() > 3000);
			}
		});
		HTML slider = new HTML(//TODO use default numbers from presenter
				"<input type=\"range\" min=\"1\" max=\"50000\" value=\"3000\" class=\"slider\" id=\"myRange\">");
		slider.addStyleName("publicationPanel__slideContainer");
		FlowPanel sourceLimitContainer = new FlowPanel();
		sourceLimitContainer.addStyleName("publicationPanel__sourceLimitContainer");
		sourceLimitContainer.add(numberBox);
		sourceLimitContainer.add(slider);
		
		
		DropDownMenu<String> orderByDropdown = new DropDownMenu<>("Publications (Most)", "Select 'based on' value", 200, "publicationPanel__orderByDropdown");
		orderByDropdown.addMenuItem(new MenuItem<String> ("bibcount DESC", "Publications (Most)", true));
		orderByDropdown.addMenuItem(new MenuItem<String> ("bibcount ASC", "Publications (Least)", true));
		orderByDropdown.addMenuItem(new MenuItem<String> ("name ASC", "Source name (A-Z)", true));
		orderByDropdown.addMenuItem(new MenuItem<String> ("name DESC", "Source name (Z-A)", true));
		orderByDropdown.addMenuItem(new MenuItem<String> ("ra DESC", "RA (High)", true));
		orderByDropdown.addMenuItem(new MenuItem<String> ("ra ASC", "RA (Low)", true));
		orderByDropdown.addMenuItem(new MenuItem<String> ("dec DESC", "Dec (High)", true));
		orderByDropdown.addMenuItem(new MenuItem<String> ("dec ASC", "Dec (Low)", true));
		
		publicationPanel.add(buttonContainer);
		publicationPanel.add(updateOnMoveCheckBox);
		publicationPanel.add(statusContainer);
		publicationPanel.add(maxFoVWarningVisible);
		publicationPanel.add(advancedOptionsToggler);
		publicationPanel.add(advancedOptions);
		
		advancedOptions.add(truncationLabel);
		advancedOptions.add(sourceLimitContainer);
		advancedOptions.add(warningLabel);
		advancedOptions.add(orderByDropdown);

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
	public EsaSkyButton getDoPublicationsQueryButton() {
		return doInitialPublicationQueryButton;
	}

	@Override
	public EsaSkyButton getUpdateButton() {
		return updatePublicationsButton;
	}

	@Override
	public EsaSkyButton getRecentreButton() {
		return recentreButton;
	}

	@Override
	public HasClickHandlers getRemoveButton() {
		return removePublicationsButton;
	}
	
	@Override
	public void setPublicationStatusText(String statusText) {
		publicationStatusText.setText(statusText);
		publicationStatusText.setVisible(true);
	}

	@Override
	public void setInitialLayout() {
		doInitialPublicationQueryButton.setVisible(true);
		removePublicationsButton.setVisible(false);
		updatePublicationsButton.setVisible(false);
		recentreButton.setVisible(false);
		publicationStatusText.setVisible(false);
		loadingSpinner.setVisible(false);
	}
	
	@Override
	public void setPublicationResultsAvailableLayout() {
		doInitialPublicationQueryButton.setVisible(false);
		removePublicationsButton.setVisible(true);
		updatePublicationsButton.setVisible(true);
		recentreButton.setVisible(true);
		publicationStatusText.setVisible(true);
	}

	@Override
	public void setMaxFoV(boolean maxFov) {
		maxFoVWarningVisible.setVisible(maxFov);
		doInitialPublicationQueryButton.setEnabled(!maxFov);
		updateOnMoveCheckBox.setEnabled(!maxFov);
		updatePublicationsButton.setEnabled(!maxFov);
	}

	@Override
	public boolean getUpdateOnMoveValue() {
		return updateOnMoveCheckBox.getValue();
	}
	
	@Override
	public void addUpdateOnMoveCheckboxOnValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		updateOnMoveCheckBox.addValueChangeHandler(handler);
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
}
