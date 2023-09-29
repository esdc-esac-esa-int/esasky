package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import java.util.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.client.SkiesMenuEntry;
import esac.archive.esasky.ifcs.model.shared.ColorPalette;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.hips.HipsNameChangeEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.status.ScreenSizeObserver;
import esac.archive.esasky.cl.web.client.status.ScreenSizeService;
import esac.archive.esasky.cl.web.client.status.ScreenWidth;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuItem.OnRemove;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.common.Selectable;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyRadioButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyRadioButtonObserver;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

public class SkyRow extends Composite implements Selectable{

	private FlowPanel skyPanel;

	private int WAVELENGTH_PX_SIZE = 122;   
	private int HIPS_PX_SIZE = 129;

	private ChangePaletteBtn changePaletteBtn;
 
	private EsaSkyRadioButton isSelectedBtn;
	private CloseButton removeSkyBtn;
	private DropDownMenu<String> wavelengthDropDown;
	private DropDownMenu<HiPS> hipsDropDown;
	private boolean onlyOneSkyActive = true;
	private boolean isChosenFromSlider = false;
	private boolean blockNotifications = false;

	private List<SkyObserver> observers = new LinkedList<SkyObserver>();

	private SkiesMenu skiesMenu;

	private Resources resources;
	private Style style;
	
	private boolean isOverlay = false;
	private boolean isMain = false;
	private boolean isReversedActivated = false;

	public static interface Resources extends ClientBundle {

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
	    this(skiesMenu, hipsName, null, false);
	}
	
	public SkyRow(SkiesMenu skiesMenu, String hipsName, String category, boolean isDefault){
	    blockNotifications = true;
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();
		this.skiesMenu = skiesMenu;

		String wavelength = category != null ? category : skiesMenu.getWavelengthFromHiPSName(hipsName);
		if(wavelength == null){
			wavelength = EsaSkyConstants.DEFAULT_WAVELENGTH.toString();
			hipsName = EsaSkyConstants.ALADIN_DEFAULT_SURVEY_NAME;
		}
		
		initView(wavelength, hipsName, isDefault);
		blockNotifications = false;
	}

	private void initView(String defaultWavelength, String defaultHips, boolean isDefault) {
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
		
		if (isDefault) {
			removeSkyBtn.addStyleName("collapse");
		}
	}

	private void setWidth() {
		if(MainLayoutPanel.getMainAreaWidth() > ScreenWidth.SMALL.getPxSize() && !onlyOneSkyActive) {
			setWidth(410 + "px");
		} else {
			setWidth(380 + "px");
		}
	}

	private DropDownMenu<String> createWavelengthDropdown() {

		final DropDownMenu<String> wavelengthDropDown = new DropDownMenu<String>
		(TextMgr.getInstance().getText("sky_wavelength"), 
				TextMgr.getInstance().getText("sky_selectWavelength"), WAVELENGTH_PX_SIZE, "wavelengthDropDown");

		wavelengthDropDown.registerObserver(new MenuObserver() {

			@Override
			public void onSelectedChange() {
				List<HiPS> listOfHipsByWavelength;
				if(!HipsWavelength.wavelengthList.contains(wavelengthDropDown.getSelectedObject())) {
					listOfHipsByWavelength = HipsWavelength.getListOfUserHips().get(wavelengthDropDown.getSelectedObject().toString());
					fillHiPSMenuBar(listOfHipsByWavelength, true);
				} else {
					listOfHipsByWavelength = skiesMenu.getHiPSListByWavelength(wavelengthDropDown.getSelectedObject()).getHips();
					fillHiPSMenuBar(listOfHipsByWavelength, false);
				}
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
			    if(isOverlay || isMain) {
			        isChosenFromSlider = true;
			        notifySkyChange();
			    }
			    if(!blockNotifications) {
				    sendEvents();
				}
			}
		});

		hipsDropDown.addStyleName("skyDropDown");

		return hipsDropDown;
	}    

	private void fillDropDownMenus(String defaultWavelength, String defaultHips) {
	    blockNotifications = true;
	    for(String wave:HipsWavelength.wavelengthList) {
	    }
	    
		for (final SkiesMenuEntry menuEntry : skiesMenu.getMenuEntries()) {
			if((!HipsWavelength.wavelengthList.contains(menuEntry.getWavelength()) )) {
				for(HiPS hips: menuEntry.getHips()) {
					
					if(HipsWavelength.getListOfUserHips().get(menuEntry.getWavelength()) == null) {
						HipsWavelength.getListOfUserHips().put(menuEntry.getWavelength(), new LinkedList<HiPS>());
					}
					
				    if(!HipsWavelength.getListOfUserHips().get(menuEntry.getWavelength()).contains(hips)) {
				    	HipsWavelength.getListOfUserHips().get(menuEntry.getWavelength()).add(hips);
				    }
				}
			}
			createWavelengthOption(menuEntry.getWavelength());
		}
		
		for(String category: HipsWavelength.getListOfUserHips().keySet()) {
				createWavelengthOption(category);
			
		}
		
		if(!HipsWavelength.wavelengthList.contains(defaultWavelength)) {
			
			if(HipsWavelength.getListOfUserHips().get(defaultWavelength) == null) {
				HipsWavelength.getListOfUserHips().put(defaultWavelength, new LinkedList<HiPS>());
			}
		}
		
		wavelengthDropDown.selectObject(defaultWavelength);
		setSelectHips(defaultHips, false, false, null);
		blockNotifications = false;
	}

	private void createWavelengthOption(String wavelength) {
		MenuItem<String> dropdownItem = null;
		if(HipsWavelength.wavelengthList.contains(wavelength)) {
			dropdownItem = new MenuItem<String>(
					wavelength, TextMgr.getInstance().getText("wavelength_" + wavelength), 
					TextMgr.getInstance().getText("wavelength_" + wavelength + "_Tooltip"), true);
		}else {
			dropdownItem = new MenuItem<String>(
					wavelength, wavelength, 
					wavelength, true);
		}
		
		wavelengthDropDown.addMenuItem(dropdownItem);
	}  

	private void fillHiPSMenuBar(final List<HiPS> hipsList, boolean removable) {
		hipsDropDown.clearItems();

		String mode = Modules.getMode();
		HiPS defaultHips = null;
		for (final HiPS hips : hipsList) {
			if (mode != null && Objects.equals(mode.toUpperCase(), EsaSkyWebConstants.MODULE_MODE_KIOSK) && !hips.getIsDefault()) {
				continue;
			}

			MenuItem<HiPS> dropdownItem = new MenuItem<>(hips, hips.getSurveyName(), true, removable, this::notifyMenuItemRemoveClicked);
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
	
	private void notifyMenuItemRemoveClicked(MenuItem<HiPS> menuItem) {
	       for(SkyObserver observer: observers){
	            observer.onMenuItemRemovalEvent(menuItem);
	        }
	}

	public void onMenuItemRemoval(MenuItem<HiPS> menuItem) {
        MenuItem<HiPS> menuItemToRemove = findCorrespondingMenuItem(menuItem);
        if(menuItemToRemove == null) {
            return;
        }
        removeEntries(menuItemToRemove);
        
        if(menuItemToRemove.getIsSelected()) {
            if(hipsDropDown.getMenuItems().size() > 0) {
                selectFirstNextEntry(menuItemToRemove);
            } else {
                hipsDropDown.hideMenuBar();
                notifyClose();
            }
        } else if (hipsDropDown.getMenuItems().size() == 0) {
            hipsDropDown.hideMenuBar();
            notifyClose();
        }
        
        //Remove custom HiPS
        if(HipsWavelength.getListOfUserHips().get(wavelengthDropDown.getSelectedObject()) != null) {
        	int indexToRemove = -1;
        	for(HiPS hips : HipsWavelength.getListOfUserHips().get(wavelengthDropDown.getSelectedObject())) {
        		if(hips.getSurveyName().equals(menuItemToRemove.getText())) {
        			indexToRemove= HipsWavelength.getListOfUserHips().get(wavelengthDropDown.getSelectedObject()).indexOf(hips);
        		}
        	}
        	if(indexToRemove >=0) {
        		HipsWavelength.getListOfUserHips().get(wavelengthDropDown.getSelectedObject()).remove(indexToRemove);
        	}
        }
	}

    private void selectFirstNextEntry(MenuItem<HiPS> menuItemToRemove) {
        for (MenuItem<HiPS> menuItemToSelect : hipsDropDown.getMenuItems()) {
            if(!menuItemToRemove.equals(menuItemToSelect)) {
                hipsDropDown.selectObject(menuItemToSelect.getItem());
                if(isSelected()) {
                    notifySkyChange();
                    sendEvents();
                }
                break;
            }
        }
    }

    private void removeEntries(MenuItem<HiPS> menuItemToRemove) {
        hipsDropDown.removeMenuItem(menuItemToRemove);
        List<SkiesMenuEntry> entriesToDelete = new LinkedList<SkiesMenuEntry>();
        for (final SkiesMenuEntry menuEntry : skiesMenu.getMenuEntries()) {
            if(!HipsWavelength.wavelengthList.contains(menuEntry.getWavelength())) {
                for(HiPS hips: menuEntry.getHips()) {
                    if(menuItemToRemove.getItem().equals(hips)) {
                        entriesToDelete.add(menuEntry);
                    }
                }
            }
        }
        for (SkiesMenuEntry entry : entriesToDelete) {
            skiesMenu.getMenuEntries().remove(entry);
        }
    }
	
	private MenuItem<HiPS> findCorrespondingMenuItem(MenuItem<HiPS> menuItemFromOtherMenu){
	    for(MenuItem<HiPS> menuItem : hipsDropDown.getMenuItems()) {
            if(menuItem.getItem().equals(menuItemFromOtherMenu.getItem())){
                return menuItem;
            }
	    }
	    return null;
	}
	
	private HiPS lastCreatedUserHiPS;
	public void setHiPSFromAPI(HiPS hips, boolean notifiyObservers, boolean newHips){
		lastCreatedUserHiPS = hips;
		setSelectHips(hips.getSurveyName(), notifiyObservers, newHips, hips.getHipsCategory());
	}
	
	public boolean setSelectHips(String hipsName, boolean notifiyObservers, boolean newHips, String category){
		blockNotifications = true;
		String wavelength = skiesMenu.getWavelengthFromHiPSName(hipsName);
	    if(wavelength == null) {
	    	wavelength = category != null ? category : HipsWavelength.USER;
	    	if(!HipsWavelength.wavelengthList.contains(wavelength) && HipsWavelength.getListOfUserHips().get(wavelength) == null) {
	    		HipsWavelength.getListOfUserHips().put(wavelength, new LinkedList<HiPS>());
	    	}
	    	if(HipsWavelength.getListOfUserHips().get(wavelength).size() == 0) {
	    		createWavelengthOption(wavelength);
	    	}
	    }
	    
		if (wavelength != wavelengthDropDown.getSelectedObject()) {
			wavelengthDropDown.selectObject(wavelength);
		}
		for(MenuItem<HiPS> menuItem: hipsDropDown.getMenuItems()){
			if(menuItem.getItem().getSurveyName().equalsIgnoreCase(hipsName)){
				if(newHips) {
					hipsDropDown.removeMenuItem(menuItem);
					HipsWavelength.getListOfUserHips().get(wavelength).remove(menuItem.getItem());
				}else {
					hipsDropDown.selectObject(menuItem.getItem());
					if (notifiyObservers) {
						notifySkyChange();
					}
					blockNotifications = false;
					sendEvents();
					return true;
				}
			}
		}
		if(newHips) {
			String hipsCategory = category != null ? category : lastCreatedUserHiPS.getHipsWavelength();
			HipsWavelength.getListOfUserHips().get(hipsCategory).add(lastCreatedUserHiPS);
			MenuItem<HiPS> menuItem = new MenuItem<HiPS>(lastCreatedUserHiPS, hipsName, true, true, new OnRemove<HiPS>() {

                @Override
                public void onRemove(MenuItem<HiPS> menuItem) {
                    notifyMenuItemRemoveClicked(menuItem);
                }
        
            }); 
			hipsDropDown.addMenuItem(menuItem);
			blockNotifications = false;
			hipsDropDown.selectObject(menuItem.getItem());
			return true;
		}
		//No hips with correct name found
		return false;
	}

	private EsaSkyButton createSkyDetailsBtn() {
		final EsaSkyButton skyDetailsBtn = new EsaSkyButton(Icons.getInfoIcon());
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
				
	            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SKIESMENU, GoogleAnalytics.ACT_SKIESMENU_SKYINFOSHOWN, getFullId());
			}
		});

		return skyDetailsBtn;
	}

	private EsaSkyRadioButton createIsSelectedBtn() {
		isSelectedBtn = new EsaSkyRadioButton("sky");
		isSelectedBtn.addStyleName("skyRadioBtn");
		
		isSelectedBtn.registerValueChangeObserver(new EsaSkyRadioButtonObserver() {

			@Override
			public void onValueChange(boolean isSelected) {
				if(isSelected && !isChosenFromSlider){
					SelectSkyPanel.getInstance().clearAllMainStatus();
					SelectSkyPanel.getInstance().clearAllOverlayStatus();
					setMain(true);
					notifySkyChange();
					sendEvents();
				}
			}
		});

		return isSelectedBtn;
	}
	
	private void sendUpdateSkyName() {
		CommonEventBus.getEventBus().fireEvent(
				new HipsNameChangeEvent(getNameofSelected()));
	}

	public boolean isSelected(){
		return isSelectedBtn.isSelected();
	}
	
	public void setSelected(){
		setSelected(true);
	}
	
	public void setSelected(boolean sendConvenienceEvent) {
		if(!sendConvenienceEvent && !blockNotifications) {
			blockNotifications = true;
			select();
			blockNotifications = false;
		} else {
			select();
		}
	}
	
	private void select() {
		if(!isChosenFromSlider) {
			setMain(true);
		}
		isSelectedBtn.setSelected(true); 
		isChosenFromSlider = false;
	}

	private void addSkyPaletteChangeListener() {
		changePaletteBtn.registerObserver(new PaletteObserver() {

			@Override
			public void onPaletteChange() {
				if(isOverlay || isMain) {
					isChosenFromSlider = true;
					notifySkyChange();
				}
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
		if(hipsDropDown!= null) {
			return hipsDropDown.getSelectedObject();
		}
		return null;
	}

	public ColorPalette getSelectedPalette(){
		return changePaletteBtn.getSelectedColorPalette();
	}
	
	public void setColorPalette(ColorPalette colorPalette) {
		changePaletteBtn.setDefaultColorPallette(colorPalette);
	}

	public void addOnlyOneSkyActiveStyle(){
		onlyOneSkyActive = true;
		isSelectedBtn.addStyleName("collapse");
		removeSkyBtn.addStyleName("collapse");
		setWidth();
	}
	
	
	public void disableDeleteButton(){
		removeSkyBtn.setEnabled(false);
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
		for(SkyObserver observer: observers){
			observer.onUpdateSkyEvent(this);
		}
	}

	public void notifyClose(){
		for(SkyObserver observer: observers){
			observer.onCloseEvent(this);
		}
	}
	
	private void sendEvents() {
		if(isSelected() && !blockNotifications) {
			sendUpdateSkyName();
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SKIESMENU, GoogleAnalytics.ACT_SKIESMENU_SELECTEDSKY, getFullId());
    		//Convenience event for easy statistics gathering
    		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CONVENIENCE, GoogleAnalytics.ACT_SKIESMENU_SELECTEDSKY, getNameofSelected());
		}
	}

	@Override
	public String getNameofSelected() {
		HiPS hips = getSelectedHips();
		if(hips != null) {
			return hips.getSurveyId();
		}
		return EsaSkyConstants.ALADIN_DEFAULT_SURVEY_NAME;
	}

	@Override
	public boolean isValid() {
		return true;
	}
	
    public String getFullId() {
        return wavelengthDropDown.getSelectedObject() + " - " + getNameofSelected() + " - " + getSelectedPalette().name();
    }
    
    public void setOverlayStatus(boolean status) {
    	isOverlay = status;
    }
    
    public boolean isOverlay() {
    	return isOverlay;
    }
    
	public boolean isMain() {
		return isMain;
	}

	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}

	public boolean isChosenFromSlider() {
		return isChosenFromSlider;
	}

	public void setChosenFromSlider(boolean isChosenFromSlider) {
		this.isChosenFromSlider = isChosenFromSlider;
	}
	
	public void setOpacity(double opacity) {
		if(isMain()) {
			AladinLiteWrapper.getInstance().changeHiPSOpacity(Math.pow(opacity,0.25));
		}else if(isOverlay) {
			AladinLiteWrapper.getInstance().changeOverlayOpacity(Math.pow(opacity,2));
		}
	}
	
	public void refreshUserDropdown() {
	    blockNotifications = true;
	    refreshWavelengthDropdown();
	    if(!HipsWavelength.wavelengthList.contains(wavelengthDropDown.getSelectedObject())) {
	    	HiPS selectedObject = hipsDropDown.getSelectedObject();
	        fillHiPSMenuBar(HipsWavelength.getListOfUserHips().get(wavelengthDropDown.getSelectedObject()), true);
	        hipsDropDown.selectObject(selectedObject);
	    }
	    blockNotifications = false;
	}
	
	private void refreshWavelengthDropdown() {
		Set<String> customWavelengths = HipsWavelength.getListOfUserHips().keySet();
		List<String> wavelengthsAdded = new ArrayList<String>();
		for(MenuItem<String> item: wavelengthDropDown.getMenuItems()) {
			if(!HipsWavelength.wavelengthList.contains(item.getText()) && customWavelengths.contains(item.getText())) {
				wavelengthsAdded.add(item.getText());
			}
		}
		for(String wavelength: customWavelengths) {
			if(!wavelengthsAdded.contains(wavelength)) {
				MenuItem<String> dropdownItem = new MenuItem<String>(
						wavelength, wavelength, 
						wavelength, true);
				wavelengthDropDown.addMenuItem(dropdownItem);
			}
			
	
		
		}
	}    
    
}
