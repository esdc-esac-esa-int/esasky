package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;

import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.client.SkiesMenuEntry;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.hips.HipsChangeEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.SelectSkyPanelPresenter;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.ESASkyPlayerPanel;
import esac.archive.esasky.cl.web.client.view.common.ESASkySlider;
import esac.archive.esasky.cl.web.client.view.common.EsaSkyPlayerObserver;
import esac.archive.esasky.cl.web.client.view.common.EsaSkySliderObserver;
import esac.archive.esasky.cl.web.client.view.common.buttons.DisablablePushButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;

public class SelectSkyPanel extends DialogBox implements SkyObserver, SelectSkyPanelPresenter.View {

	private static SelectSkyPanel instance;

	private PopupHeader header;
	private DragFlexTable skyTable;
	private final Resources resources;
	private CssResource style;
	private ESASkySlider slider;

	private boolean isShowing;

	private ESASkyPlayerPanel player;
	private AddSkyButton addSkyButton;

	private String hipsFromUrl = null;
	private SkiesMenu skiesMenu;

	private FlowPanel selectSkyPanel = new FlowPanel();
	private FlowPanel sliderContainer = new FlowPanel();

	private static List<SkyRow> skies = new LinkedList<SkyRow>();

	public static interface Resources extends ClientBundle {

		@Source("selectSkyPanel.css")
		@CssResource.NotStrict
		CssResource style();
	}

	public static SelectSkyPanel init(String defaultHiPS) {
		instance = new SelectSkyPanel(defaultHiPS);
		return instance;
	}

	private SelectSkyPanel(String defaultHiPS) {
		super(false, false);
		this.hipsFromUrl = defaultHiPS;

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
	
	public static SelectSkyPanel getInstance() {
		if (instance == null) {
			throw new AssertionError("You have to call init first");
		}
		return instance;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		setMaxSize();
	}

	private void initView() {
		this.removeStyleName("gwt-DialogBox");
		this.getElement().setId("allSkiesMenu");

		selectSkyPanel.getElement().setId("allSkiesMenuContainer");

		header = createHeader();
		selectSkyPanel.add(header);

		skyTable = new DragFlexTable();
		selectSkyPanel.add(skyTable);
		
		FlowPanel hipsControllerContainer = new FlowPanel();
		hipsControllerContainer.addStyleName("hipsControllerContainer");
		selectSkyPanel.add(hipsControllerContainer);
		
		hipsControllerContainer.add(createAddSkyBtn());
		slider = createSlider();
		sliderContainer.addStyleName("sliderContainer");
		sliderContainer.add(slider);
		
		hipsControllerContainer.add(sliderContainer);
		hipsControllerContainer.add(createPlayer());

		this.add(selectSkyPanel);
		
	}
	
	private PopupHeader createHeader() {
		return new PopupHeader(this, TextMgr.getInstance().getText("sky_loadingSkies"),
				TextMgr.getInstance().getText("sky_selectSky_help"));
	}

	private ESASkySlider createSlider() {
		final ESASkySlider slider = new ESASkySlider(0, 0, 250);
		slider.addStyleName("hipsSlider");
		slider.registerValueChangeObserver(new EsaSkySliderObserver() {

			@Override
			public void onValueChange(double value) {
				SelectSkyPanel.this.onValueChange(value);
			}
		});
		return slider;
	}

	private DisablablePushButton createAddSkyBtn() {
		addSkyButton = new AddSkyButton();
		return addSkyButton;
	}
	
	public void onValueChange(double value) {
		player.setValue(value);
		int nRows = skyTable.getRowCount();
		int rowNumber = Math.min((int) Math.floor(value + 0.001), nRows - 1); // 0.001 extra because of float number precision errors. 
		double opacity = value - rowNumber;
		
		if(opacity < 0 ) { opacity = 0.0;} // Could happen with the added precision from rowNumbers

		SkyRow secondSky;
		SkyRow skyRow = (SkyRow) skyTable.getWidget(rowNumber, 0);
		if(rowNumber + 1 < nRows) {
			secondSky = (SkyRow) skyTable.getWidget(rowNumber + 1, 0);
		}else {
			secondSky = null;
		}
		
		if(Math.abs(slider.getOldValue() - value) >= 1) {
			skyRow.setSelected();
		}

		if(opacity > 0.5) {
			if(secondSky != null && !secondSky.isSelected()) {
				secondSky.setChosenFromSlider(true);
				secondSky.setSelected();
			}
		}else {
			if(!skyRow.isSelected()) {
				skyRow.setChosenFromSlider(true);
				skyRow.setSelected();
			}
		}
		
		if(!skyRow.isMain() && !skyRow.isOverlay()) {
			if(secondSky != null  && secondSky.isMain()) {
				clearAllOverlayStatus();
				skyRow.setOverlayStatus(true);
				
			}else {
				clearAllMainStatus();
				skyRow.setMain(true);
			}
			skyRow.setChosenFromSlider(true);
			skyRow.notifySkyChange();
			skyRow.setChosenFromSlider(false);
		}
		skyRow.setOpacity(1-opacity);

		if(secondSky != null && opacity > 0) {
			
			if(skyRow.isMain() && secondSky.isMain()) {
				clearAllMainStatus();
				skyRow.isMain();
			}
			
			if(!secondSky.isMain() && !secondSky.isOverlay()) {
				if(skyRow.isMain()) {
					clearAllOverlayStatus();
					secondSky.setOverlayStatus(true);
					
				}else {
					clearAllMainStatus();
					secondSky.setMain(true);
				}
				secondSky.setChosenFromSlider(true);
				secondSky.notifySkyChange();
				secondSky.setChosenFromSlider(false);
			}
			secondSky.setOpacity(opacity);
		}else {
			if(skyRow.isMain()) {
				clearAllOverlayStatus();
				AladinLiteWrapper.getInstance().setOverlayImageLayerToNull();
			}else {
				clearAllMainStatus();
				AladinLiteWrapper.getInstance().changeHiPSOpacity(0);
			}
		}
	}
	
	public void clearAllOverlayStatus() {
		for (int i = 0; i < skyTable.getRowCount(); i++) {
			SkyRow skyRow = (SkyRow) skyTable.getWidget(i, 0);
			skyRow.setOverlayStatus(false);
		}
	}
	
	public void clearAllMainStatus() {
		for (int i = 0; i < skyTable.getRowCount(); i++) {
			SkyRow skyRow = (SkyRow) skyTable.getWidget(i, 0);
			skyRow.setMain(false);
		}
	}

	public void fillAllSkyPanelEntries(final SkiesMenu skiesMenu) {
		this.skiesMenu = skiesMenu;
		header.setText(TextMgr.getInstance().getText("sky_selectSky"));
		createSky();
		addSkyButton.enableButton();
	}

	@Override
	public SkyRow createSky(){
		SkyRow newSky = new SkyRow(skiesMenu, hipsFromUrl);
		newSky.registerObserver(this);
		skyTable.insertItem(newSky);
		player.addEntryToPlayer(newSky);
		skies.add(newSky);
		ensureCorrectSkyStyle();
		newSky.setSelected();
		return newSky;
	}

	private void ensureCorrectSkyStyle() {
		slider.setMaxValue(skies.size() - 1.0);
		if(skies.size() <= 1){
			for(SkyRow sky: skies){
				sky.addOnlyOneSkyActiveStyle();
				slider.addStyleName("collapse");
			}
			sliderContainer.setVisible(false);
		} else { 
			for(SkyRow sky: skies){
				sky.removeOnlyOneSkyActiveStyle();
				slider.removeStyleName("collapse");
			}
			sliderContainer.setVisible(true);
		}
	}

	private ESASkyPlayerPanel createPlayer() {
		player = new ESASkyPlayerPanel(50, 0.01, "HiPSPlayer");
		player.addStyleName("skyPlayer");
		player.registerValueChangeObserver(new EsaSkyPlayerObserver() {
			
			@Override
			public void onValueChange(double value) {
				SelectSkyPanel.this.slider.setValue(value);
			}
		});
		return player;
	}
	
	public boolean removeSky(int index) {
		try {
			SkyRow skyRow = skies.get(index);
			removeSky(skyRow);
			return true;
		}catch(IndexOutOfBoundsException e) {
			return false;
		}
	}

	private void removeSky(SkyRow skyToRemove){
		int indexToRemove = skies.indexOf(skyToRemove);
		int indexOfSelected = skies.indexOf(getSelectedSky());
		skyTable.removeSky(skyToRemove);
		player.removeEntry(skyToRemove);
		skies.remove(skyToRemove);
		if(skyToRemove.isOverlay()) {
			AladinLiteWrapper.getInstance().setOverlayImageLayerToNull();
		}
		ensureCorrectSkyStyle();
		if(indexOfSelected > indexToRemove) {
			slider.setValue(indexOfSelected - 1);
		} else {
			slider.setValue(indexOfSelected);
		}
	}

	@Override
	public void onUpdateSkyEvent(SkyRow sky) {
		
		double value = SelectSkyPanel.getInstance().getSliderValue();
		double opacity = value - Math.floor(value);
		if(sky.isMain()) { opacity = 1 - opacity;}
		CommonEventBus.getEventBus().fireEvent(
				new HipsChangeEvent(sky.getSelectedHips(), sky.getSelectedPalette(), sky.isMain(), opacity));

		if(skies.size() > 1){
			for (int i = 0; i < skyTable.getRowCount(); i++) {
				SkyRow skyRow = (SkyRow) skyTable.getWidget(i, 0);
				if (skyRow.equals(sky)) {
					if(!sky.isChosenFromSlider()) {
						clearAllMainStatus();
						clearAllOverlayStatus();
						AladinLiteWrapper.getInstance().setOverlayImageLayerToNull();
						sky.setMain(true);
						slider.setValue(i);
					}
					sky.setChosenFromSlider(false);
					break;
				}
			}
		}
	}

	@Override
	public void onCloseEvent(SkyRow sky) {
		removeSky(sky);
	}

	public static String getNameOfSelectedHips(){
		final SkyRow sky = getSelectedSky();
		return (sky != null) ? sky.getNameofSelected() : EsaSkyConstants.ALADIN_DEFAULT_SURVEY_NAME;
	}

	public static SkyRow getSelectedSky(){
		for(SkyRow sky : skies){
			if (sky.isSelected()){
				return sky;
			}
		}

		Log.debug("No sky yet selected");
		return null;
	}

	public static void setHiPSFromAPI (HiPS hips, boolean newHips) {
		SkyRow skyTmp = getSelectedSky();
		if(skyTmp == null) {
			SkiesMenuEntry entry = new SkiesMenuEntry();
			entry.getHips().add(hips);
			entry.setTotal(1);
			entry.setWavelength(HipsWavelength.USER);
			instance.skiesMenu.getMenuEntries().add(entry);

			skyTmp = new SkyRow(instance.skiesMenu, "Sky");
			skyTmp.registerObserver(instance);
			instance.skyTable.insertItem(skyTmp);
			instance.player.addEntryToPlayer(skyTmp);
			skies.add(skyTmp);
			instance.ensureCorrectSkyStyle();
		}
		final SkyRow sky = skyTmp;
		sky.setHiPSFromAPI(hips, true, newHips);
		sky.setSelected();
		instance.addSkyButton.enableButton();
	}

	public static void setSelectedHipsName (String hipsName) {
		final SkyRow sky = getSelectedSky();
		if (sky != null) {
			if (!hipsName.equals(sky.getNameofSelected())) {
				// TODO: Review this code... what happens if selected sky hasn't the hipsName passed, probably we need to change the selected Sky...
				sky.setSelectHips(hipsName, true, false);
			}
		}
	}

	private void setMaxSize() {
		Style style = getElement().getStyle();
		style.setPropertyPx("maxWidth", MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15);
		style.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15);
	}
	
	public void setSliderValue(double value) {
		slider.setValue(value);
	}
	
	public double getSliderValue() {
		return slider.getCurrentValue();
	}
	
	public int getNumberOfSkyRows() {
		return skies.size();
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
	public AddSkyButton getAddSkyRowButton() {
		return addSkyButton;
	}

	public SkiesMenu getSkiesMenu() {
		return skiesMenu;
	}

	public void setSkiesMenu(SkiesMenu skiesMenu) {
		this.skiesMenu = skiesMenu;
	}

}
