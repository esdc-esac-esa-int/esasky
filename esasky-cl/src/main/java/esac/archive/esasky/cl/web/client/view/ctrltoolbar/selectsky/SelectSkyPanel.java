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

package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.hips.HipsChangeEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.SelectSkyPanelPresenter;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.*;
import esac.archive.esasky.cl.web.client.view.common.buttons.DisablablePushButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.client.SkiesMenuEntry;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SelectSkyPanel extends MovablePanel implements SkyObserver, SelectSkyPanelPresenter.View, Hidable<SelectSkyPanel> {

	private static SelectSkyPanel instance;

	private PopupHeader<SelectSkyPanel> header;
	private DragFlexTable skyTable;
	private final Resources resources;
	private CssResource style;
	private ESASkySlider slider;

	private ESASkyPlayerPanel player;
	private AddSkyButton addSkyButton;

	private String hipsFromUrl;
	private SkiesMenu skiesMenu;

	private boolean isShowing = false;

	private FlowPanel selectSkyPanel = new FlowPanel();
	private FlowPanel sliderContainer = new FlowPanel();


	private static List<SkyRow> skies = new LinkedList<>();

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
		super(GoogleAnalytics.CAT_SELECTSKY, false, false);
		this.hipsFromUrl = defaultHiPS;

		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();

		initView();
		MainLayoutPanel.addMainAreaResizeHandler(event -> setMaxSize());
	}

	public static SelectSkyPanel getInstance() {
		if (instance == null) {
			throw new AssertionError("You have to call init first");
		}
		return instance;
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

	@Override
	protected void onLoad() {
		this.addSingleElementAbleToInitiateMoveOperation(header.getElement());
		this.addResizeHandler(selectSkyPanel.getElement().getId());
	}

	private PopupHeader<SelectSkyPanel> createHeader() {
		return new PopupHeader<>(this, TextMgr.getInstance().getText("sky_loadingSkies"),
				TextMgr.getInstance().getText("sky_selectSky_help"));
	}

	private ESASkySlider createSlider() {
		final ESASkySlider slider = new ESASkySlider(0, 0, 250);
		slider.addStyleName("hipsSlider");
		slider.registerValueChangeObserver(SelectSkyPanel.this::onValueChange);
		return slider;
	}

	private DisablablePushButton createAddSkyBtn() {
		addSkyButton = new AddSkyButton();
		return addSkyButton;
	}

	public void onValueChange(double value) {
		player.setValue(value);
		int numRows = skyTable.getRowCount();

		for (int i = 0; i < numRows; i++) {
			SkyRow curSkyRow = (SkyRow) skyTable.getWidget(i, 0);
			double distanceFromValue = Math.abs(value - i);
			float opacity = 1.0f - Math.min(1.0f, (float) distanceFromValue);
			curSkyRow.setOpacity(opacity);

			if (opacity > 0.5 && !curSkyRow.isSelected()) {
				curSkyRow.setSelected(false);
			}
		}
	}

	public void fillAllSkyPanelEntries(final SkiesMenu skiesMenu) {
		this.skiesMenu = skiesMenu;
		header.setText(TextMgr.getInstance().getText("sky_selectSky"));
		addSkyButton.enableButton();
		onUpdateSkyEvent(createSky(true));
	}


	@Override
	public void show() {
		isShowing = true;
		this.removeStyleName("displayNone");
		setMaxSize();
		ensureDialogFitsInsideWindow();
		updateHandlers();
	}

	@Override
	public void hide() {
		this.addStyleName("displayNone");
		isShowing = false;
		this.removeHandlers();
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
	public void setMaxSize(){
		getElement().getStyle().setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() - this.getAbsoluteTop() - 25);
		getElement().getStyle().setPropertyPx("maxWidth", MainLayoutPanel.getMainAreaWidth() - this.getAbsoluteLeft() - 25);
	}

	@Override
	public boolean isShowing() {
		return isShowing;
	}

	@Override
	public SkyRow createSky(boolean sendConvenienveEvent){
		SkyRow newSky = new SkyRow(skiesMenu, hipsFromUrl, skies.isEmpty() );
		newSky.registerObserver(this);
		skyTable.insertItem(newSky);
		player.addEntryToPlayer(newSky);
		skies.add(newSky);
		ensureCorrectSkyStyle();
		newSky.setSelected(sendConvenienveEvent);
		return newSky;
	}
	
	@Override
	public SkyRow createSky(boolean sendConvenienveEvent, String category, boolean isDefault){
		SkyRow newSky = new SkyRow(skiesMenu, hipsFromUrl, category, isDefault, skies.isEmpty(), null, true);
		newSky.registerObserver(this);
		skyTable.insertItem(newSky);
		player.addEntryToPlayer(newSky);
		skies.add(newSky);
		ensureCorrectSkyStyle();
		newSky.setSelected(sendConvenienveEvent);

		return newSky;
	}

	private void ensureCorrectSkyStyle() {
		slider.setMaxValue(skies.size() - 1);
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
				if(sky.getSelectedHips() != null && sky.getSelectedHips().isDefaultHIPS()) {
					sky.disableDeleteButton();
				}
				
			}
			sliderContainer.setVisible(true);
		}
	}

	private ESASkyPlayerPanel createPlayer() {
		player = new ESASkyPlayerPanel(50, 0.01, "HiPSPlayer");
		player.addStyleName("skyPlayer");
		player.registerValueChangeObserver(value -> SelectSkyPanel.this.slider.setValue(value));
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

	public void removeSky(String ...names) {
        for(SkyRow skyRow : skies) {
            if (Arrays.asList(names).contains(skyRow.getNameofSelected())) {
                removeSky(skyRow);
            }
        }
	}

	// Remove all but the selected sky
	public void removeOtherSkies() {
		for(SkyRow skyRow : skies) {
			int indexToRemove = skies.indexOf(skyRow);
			int indexOfSelected = skies.indexOf(getSelectedSky());

			if (indexToRemove != indexOfSelected) {
				removeSky(skyRow);
			}

		}
	}

	private void removeSky(SkyRow skyToRemove){
		int indexToRemove = skies.indexOf(skyToRemove);
		int indexOfSelected = skies.indexOf(getSelectedSky());
		skyTable.removeSky(skyToRemove);
		player.removeEntry(skyToRemove);
		skies.remove(skyToRemove);
		AladinLiteWrapper.getAladinLite().removeImageLayer(skyToRemove.getRowId());
		if(skyToRemove.isBase()) {
			skies.stream().findFirst().ifPresent(skyRow -> {
				AladinLiteWrapper.getAladinLite().renameImageLayer(skyRow.getRowId(), "base");
				skyRow.setBase(true);
			});
		}
		ensureCorrectSkyStyle();
		if(indexOfSelected > indexToRemove) {
			slider.setValue(indexOfSelected - 1.0);
		} else {
			slider.setValue(indexOfSelected);
		}
	}

	@Override
	public void onUpdateSkyEvent(SkyRow sky) {
		CommonEventBus.getEventBus().fireEvent(
				new HipsChangeEvent(sky.getRowId(), sky.getSelectedHips(), sky.getSelectedPalette(), sky.isBase(), 1));
		sky.setSelected();
	}

	@Override
	public void onCloseEvent(SkyRow sky) {
	    if(ongoingClean) {
	        skiesToRemove.add(sky);
	    } else {
	        removeSky(sky);
	    }
	}

	private void removeClosedSkies() {
	    for (SkyRow sky : skiesToRemove) {
	        removeSky(sky);
	    }
	    skiesToRemove.clear();
	}
	private List<SkyRow> skiesToRemove = new LinkedList<>();

	private boolean ongoingClean = false;

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
			if(hips.getHipsCategory() == null) {
				entry.setWavelength(HipsWavelength.USER);
			}else {
				entry.setWavelength(hips.getHipsCategory());
			}
			
			instance.skiesMenu.getMenuEntries().add(entry);

			skyTmp = new SkyRow(instance.skiesMenu, "Sky", skies.isEmpty());
			skyTmp.registerObserver(instance);
			instance.skyTable.insertItem(skyTmp);
			instance.player.addEntryToPlayer(skyTmp);
			skies.add(skyTmp);
			instance.ensureCorrectSkyStyle();
			
		}
		
		final SkyRow sky = skyTmp;
		sky.setHiPSFromAPI(hips, newHips);
		sky.setSelected();
		instance.refreshUserDropdowns();
		instance.ensureCorrectSkyStyle();
		instance.addSkyButton.enableButton();
	}
	
	public static void updateCustomHiPS () {
		instance.refreshUserDropdowns();
	}

	public static void addFits(HiPS hips) {
		SkiesMenuEntry entry = new SkiesMenuEntry();
		entry.getHips().add(hips);
		entry.setTotal(1);
		entry.setWavelength(HipsWavelength.USER);
		instance.skiesMenu.getMenuEntries().add(entry);

		SkyRow skyRow = new SkyRow(instance.skiesMenu, hips.getSurveyName(), skies.isEmpty(), hips.getSurveyName(), false);
		skyRow.registerObserver(instance);
		instance.skyTable.insertItem(skyRow);
		instance.player.addEntryToPlayer(skyRow);
		skies.add(skyRow);

		instance.refreshUserDropdowns();
		instance.ensureCorrectSkyStyle();
		instance.addSkyButton.enableButton();
	}

	@Override
	public ESASkyPlayerPanel getPlayerPanel() {
		return player;
	}

	public static void setSelectedHipsName (String hipsName) {
		final SkyRow sky = getSelectedSky();
		if (sky != null) {
			if (!hipsName.equals(sky.getNameofSelected())) {
				sky.setSelectHips(hipsName, false, sky.getSelectedHips().getHipsCategory());
			}
		}
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
	public AddSkyButton getAddSkyRowButton() {
		return addSkyButton;
	}

	public SkiesMenu getSkiesMenu() {
		return skiesMenu;
	}

	public void setSkiesMenu(SkiesMenu skiesMenu) {
		this.skiesMenu = skiesMenu;
	}

	@Override
	public void onMenuItemRemovalEvent(MenuItem<HiPS> menuItem) {
	    ongoingClean = true;
	    for (int i = 0; i < skyTable.getRowCount(); i++) {
	        SkyRow skyRow = (SkyRow) skyTable.getWidget(i, 0);
	        skyRow.onMenuItemRemoval(menuItem);
	    }
	    ongoingClean = false;
	    removeClosedSkies();
	}

	@Override
	public void onImageSettingsClicked(SkyRow skyRow) {}

	@Override
	public void refreshUserDropdowns() {
       for (int i = 0; i < skyTable.getRowCount(); i++) {
            SkyRow skyRow = (SkyRow) skyTable.getWidget(i, 0);
            skyRow.refreshUserDropdown();
        }
	}

	@Override
	public boolean select(HiPS hips) {
		for(SkyRow sky : skies) {
			if(sky.getSelectedHips().getSurveyId().equals(hips.getSurveyId())) {
				sky.setSelected();
				return true;
			}
		}
		return false;
	}
	
	public List<SkyRow> getHipsList(){
		return skies;
	}

	private native void addResizeHandler(String id) /*-{
		var movableResizablePanel = this;
		new $wnd.ResizeSensor($doc.getElementById(id), function() {
			movableResizablePanel.@esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel::setMaxSize()();
		});
	}-*/;
}
