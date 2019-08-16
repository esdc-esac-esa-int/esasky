package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.client.SkiesMenuEntry;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.status.ScreenSizeObserver;
import esac.archive.esasky.cl.web.client.status.ScreenSizeService;
import esac.archive.esasky.cl.web.client.status.ScreenWidth;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.common.Selectable;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyRadioButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyRadioButtonObserver;

public class SkyRow extends Composite implements Selectable{

	private FlowPanel skyPanel;

	private int WAVELENGTH_PX_SIZE = 122;   
	private int HIPS_PX_SIZE = 129;

	private ChangePaletteBtn changePaletteBtn;

	private EsaSkyRadioButton isSelectedBtn;
	private CloseButton removeSkyBtn;
	private DropDownMenu<HipsWavelength> wavelengthDropDown;
	private DropDownMenu<HiPS> hipsDropDown;
	private boolean onlyOneSkyActive = true;
	private List<HiPS> listOfUserHips = new LinkedList<HiPS>();

	private List<SkyObserver> observers = new LinkedList<SkyObserver>();

	private SkiesMenu skiesMenu;

	private Resources resources;
	private Style style;
	
	private boolean isOverlay = false;

	public static interface Resources extends ClientBundle {

		@Source("information.png")
		ImageResource info();

		@Source("changePalette.png")
		ImageResource changePalette();

		@Source("sky.css")
		@CssResource.NotStrict
		Style style();
	}

	public static interface Style extends CssResource {

		String skyPanel();

		String selectedSky();

		String skyInfoBtn();

		String hipsCreatorImage();
	}

	public SkyRow(SkiesMenu skiesMenu, String hipsName){
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();
		this.skiesMenu = skiesMenu;

		HipsWavelength wavelength = skiesMenu.getWavelengthFromHiPSName(hipsName);
		if(wavelength == null){
			wavelength = EsaSkyConstants.DEFAULT_WAVELENGTH;
			hipsName = EsaSkyConstants.ALADIN_DEFAULT_SURVEY_NAME;
		}
		initView(wavelength, hipsName);
	}

	private void initView(HipsWavelength defaultWavelength, String defaultHips) {
		skyPanel = new FlowPanel();
		skyPanel.setStyleName(this.style.skyPanel());

		changePaletteBtn = new ChangePaletteBtn();
		skyPanel.add(createIsSelectedBtn());
		wavelengthDropDown = createWavelengthDropdown();
		skyPanel.add(wavelengthDropDown);
		hipsDropDown = createHipsDropDown();
		skyPanel.add(hipsDropDown);
		skyPanel.add(changePaletteBtn);
		skyPanel.add(createSkyDetailsBtn());
		fillDropDownMenus(defaultWavelength, defaultHips);
		addSkyPaletteChangeListener();

		removeSkyBtn = createRemoveSkyBtn();
		skyPanel.add(removeSkyBtn);
		skyPanel.setStyleName(this.style.skyPanel());
		skyPanel.addStyleName(this.style.selectedSky());

		initWidget(skyPanel);

		ScreenSizeService.getInstance().registerObserver(new ScreenSizeObserver() {

			@Override
			public void onScreenSizeChange() {
				setWidth();
			}
		});
		setWidth();
	}

	private void setWidth() {
		if(MainLayoutPanel.getMainAreaWidth() > ScreenWidth.SMALL.getPxSize() && !onlyOneSkyActive) {
			setWidth(410 + "px");
		} else {
			setWidth(380 + "px");
		}
	}

	private DropDownMenu<HipsWavelength> createWavelengthDropdown() {

		final DropDownMenu<HipsWavelength> wavelengthDropDown = new DropDownMenu<HipsWavelength>
		(TextMgr.getInstance().getText("sky_wavelength"), 
				TextMgr.getInstance().getText("sky_selectWavelength"), WAVELENGTH_PX_SIZE, "wavelengthDropDown");

		wavelengthDropDown.registerObserver(new MenuObserver() {

			@Override
			public void onSelectedChange() {
				List<HiPS> listOfHipsByWavelength;
				if(wavelengthDropDown.getSelectedObject() == HipsWavelength.USER) {
					listOfHipsByWavelength = listOfUserHips;
				} else {
					listOfHipsByWavelength = skiesMenu.getHiPSListByWavelength(wavelengthDropDown.selectedObject).getHips();
				}
				fillHiPSMenuBar(listOfHipsByWavelength);
			}
		});

		wavelengthDropDown.addStyleName("skyDropDown");

		return wavelengthDropDown;
	}

	private DropDownMenu<HiPS> createHipsDropDown() {

		final DropDownMenu<HiPS> hipsDropDown = new DropDownMenu<HiPS>(
				TextMgr.getInstance().getText("sky_skies"), 
				TextMgr.getInstance().getText("sky_selectASky"), HIPS_PX_SIZE, "hipsDropdown");

		hipsDropDown.registerObserver(new MenuObserver() {

			@Override
			public void onSelectedChange() {
				final HiPS hips = hipsDropDown.getSelectedObject();
				final ColorPalette colorPalette = hips.getColorPalette();
				changePaletteBtn.setDefaultColorPallette(colorPalette);
				notifySkyChange();
			}
		});

		hipsDropDown.addStyleName("skyDropDown");

		return hipsDropDown;
	}    

	private void fillDropDownMenus(HipsWavelength defaultWavelength, String defaultHips) {
		for (final SkiesMenuEntry menuEntry : skiesMenu.getMenuEntries()) {
			createWavelengthOption(menuEntry.getWavelength());
		}
		wavelengthDropDown.selectObject(defaultWavelength);
		setSelectHips(defaultHips, false, false);
	}

	private void createWavelengthOption(HipsWavelength wavelength) {
		MenuItem<HipsWavelength> dropdownItem = new MenuItem<HipsWavelength>(
				wavelength, TextMgr.getInstance().getText("wavelength_" + wavelength.name()), 
				TextMgr.getInstance().getText("wavelength_" + wavelength.name() + "_Tooltip"), true);
		wavelengthDropDown.addMenuItem(dropdownItem);
	}  

	private void fillHiPSMenuBar(final List<HiPS> hipsList) {
		hipsDropDown.clearItems();

		HiPS defaultHips = null;
		for (final HiPS hips : hipsList) {
			MenuItem<HiPS> dropdownItem = new MenuItem<HiPS>(hips, hips.getSurveyName(), true);
			hipsDropDown.addMenuItem(dropdownItem);
			if(hips.getIsDefault()){
				defaultHips = hips;
			}
		}
		if(defaultHips == null && !hipsList.isEmpty()) {
			defaultHips = hipsList.get(0);
		}
		hipsDropDown.selectObject(defaultHips);
	}

	private HiPS lastCreatedUserHiPS;
	public void setHiPSFromAPI(HiPS hips, boolean notifiyObservers, boolean newHips){
		lastCreatedUserHiPS = hips;
		setSelectHips(hips.getSurveyName(), notifiyObservers, newHips);
	}
	
	public boolean setSelectHips(String hipsName, boolean notifiyObservers, boolean newHips){
	    HipsWavelength wavelength = skiesMenu.getWavelengthFromHiPSName(hipsName);
	    if(wavelength == null) {
	    	wavelength = HipsWavelength.USER;
	    	if(listOfUserHips.size() == 0) {
	    		createWavelengthOption(HipsWavelength.USER);
	    	}
	    }
	    
		if (wavelength != wavelengthDropDown.getSelectedObject()) {
			wavelengthDropDown.selectObject(wavelength);
		}
		
		for(MenuItem<HiPS> menuItem: hipsDropDown.getMenuItems()){
			if(menuItem.getItem().getSurveyName().equalsIgnoreCase(hipsName)){

				if(newHips) {
					hipsDropDown.removeMenuItem(menuItem);
					listOfUserHips.remove(menuItem.getItem());
				}else {
					hipsDropDown.selectObject(menuItem.getItem());
					if (notifiyObservers) {
						notifySkyChange();
					}
					return true;
				}
			}
		}
		if(newHips) {
			listOfUserHips.add(lastCreatedUserHiPS);
			MenuItem<HiPS> menuItem = new MenuItem<HiPS>(lastCreatedUserHiPS, hipsName, true); 
			hipsDropDown.addMenuItem(menuItem);
			hipsDropDown.selectObject(menuItem.getItem());
			if (notifiyObservers) {
				notifySkyChange();
			}
			return true;
		}
		//No hips with correct name found
		return false;
	}

	private EsaSkyButton createSkyDetailsBtn() {
		final EsaSkyButton skyDetailsBtn = new EsaSkyButton(this.resources.info());
		skyDetailsBtn.addStyleName(this.style.skyInfoBtn());
		skyDetailsBtn.setTitle(TextMgr.getInstance().getText("sky_skyDetails"));
		skyDetailsBtn.setRoundStyle();
		skyDetailsBtn.setSmallStyle();
		skyDetailsBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				HiPSDetailsPopup skyDetailsInfo = new HiPSDetailsPopup(hipsDropDown.getSelectedObject());
				skyDetailsInfo.show();
				int defaultLeft = skyDetailsBtn.getAbsoluteLeft() + skyDetailsBtn.getOffsetWidth() / 2;
				if (defaultLeft + skyDetailsInfo.getOffsetWidth() > MainLayoutPanel.getMainAreaAbsoluteLeft() + MainLayoutPanel.getMainAreaWidth()) {
					defaultLeft -= skyDetailsInfo.getOffsetWidth(); 
				}
				skyDetailsInfo.setPopupPosition(defaultLeft, 
						skyDetailsBtn.getAbsoluteTop() + skyDetailsBtn.getOffsetHeight() / 2);
				
	            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SkiesMenu, GoogleAnalytics.ACT_SkiesMenu_SkyInfoShown, getFullId());
			}
		});

		return skyDetailsBtn;
	}

	private EsaSkyRadioButton createIsSelectedBtn() {
		isSelectedBtn = new EsaSkyRadioButton("sky");
		isSelectedBtn.addStyleName("skyRadioBtn");
		
		final SkyRow skyRow = this;

		isSelectedBtn.registerValueChangeObserver(new EsaSkyRadioButtonObserver() {

			@Override
			public void onValueChange(boolean isSelected) {
				if(isSelected){
					notifySkyChange();
					SelectSkyPanel skyPanel = SelectSkyPanel.getInstance();
					if(SelectSkyPanel.skies.size() > 1){
						for (int i = 0; i < skyPanel.skyTable.getRowCount(); i++) {
			                Widget widget = skyPanel.skyTable.getWidget(i, 0);
			                if (widget.equals(skyRow)) {
			                	skyPanel.slider.setValue(i);
			                	continue;
				            }
						}
					}
				}
			}
		});

		return isSelectedBtn;
	}

	public boolean isSelected(){
		return isSelectedBtn.isSelected();
	}

	public void setSelected(){
		isSelectedBtn.setSelected(true); 
	}

	private void addSkyPaletteChangeListener() {
		changePaletteBtn.registerObserver(new PaletteObserver() {

			@Override
			public void onPaletteChange() {
				notifySkyChange();
			}
		});
	}

	private CloseButton createRemoveSkyBtn(){
		CloseButton removeSkyBtn = new CloseButton();
		removeSkyBtn.setLightStyle();
		removeSkyBtn.setTitle(TextMgr.getInstance().getText("sky_removeSky"));
		removeSkyBtn.addStyleName("closeSkyButton");
		removeSkyBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				notifyClose();
			}
		});
		return removeSkyBtn;
	}

	public HiPS getSelectedHips(){
		return hipsDropDown.getSelectedObject();
	}

	public ColorPalette getSelectedPalette(){
		return changePaletteBtn.getSelectedColorPalette();
	}

	public void addOnlyOneSkyActiveStyle(){
		onlyOneSkyActive = true;
		isSelectedBtn.addStyleName("collapse");
		removeSkyBtn.addStyleName("collapse");
		setWidth();
	}

	public void removeOnlyOneSkyActiveStyle(){
		onlyOneSkyActive = false;
		isSelectedBtn.removeStyleName("collapse");
		removeSkyBtn.removeStyleName("collapse");
		setWidth();
	}

	public void registerObserver(SkyObserver observer){
		observers.add(observer);
	}

	public void notifySkyChange(){
		if(isSelected()){
			for(SkyObserver observer: observers){
				observer.onUpdateSkyEvent(this);
			}
			
			//Notify sky change to Google Analytics
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SkiesMenu, GoogleAnalytics.ACT_SkiesMenu_SelectedSky, getFullId());
		}else if(isOverlay()) {
			double value = SelectSkyPanel.getInstance().slider.getCurrentValue();
			double opacity = value - Math.floor(value);
			AladinLiteWrapper.getInstance().createOverlayMap(getSelectedHips(), opacity, getSelectedPalette());
		}
	}

	private void notifyClose(){
		for(SkyObserver observer: observers){
			observer.onCloseEvent(this);
		}
	}

	@Override
	public String getNameofSelected() {
		return getSelectedHips().getSurveyName();
	}

	@Override
	public boolean isValid() {
		return true;
	}

    public String getFullId() {
        return wavelengthDropDown.getSelectedObject().name() + " - " + getSelectedHips().getSurveyName() + " - " + getSelectedPalette().name();
    }
    
    public void setOverlayStatus(boolean status) {
    	isOverlay = status;
    }
    
    public boolean isOverlay() {
    	return isOverlay;
    }
    
}
