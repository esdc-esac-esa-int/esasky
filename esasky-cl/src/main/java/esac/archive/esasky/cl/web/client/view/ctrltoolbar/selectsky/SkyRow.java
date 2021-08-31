package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.client.SkiesMenuEntry;
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
	private DropDownMenu<HipsWavelength> wavelengthDropDown;
	private DropDownMenu<HiPS> hipsDropDown;
	private boolean onlyOneSkyActive = true;
	private static List<HiPS> listOfUserHips = new LinkedList<HiPS>();
	private boolean isChosenFromSlider = false;
	private boolean blockNotifications = false;

	private List<SkyObserver> observers = new LinkedList<SkyObserver>();

	private SkiesMenu skiesMenu;

	private Resources resources;
	private Style style;
	
	private boolean isOverlay = false;
	private boolean isMain = false;

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
	    blockNotifications = true;
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
		blockNotifications = false;
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
					fillHiPSMenuBar(listOfHipsByWavelength, true);
				} else {
					listOfHipsByWavelength = skiesMenu.getHiPSListByWavelength(wavelengthDropDown.selectedObject).getHips();
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
				if(!blockNotifications) {
				    if(isOverlay || isMain) {
				        isChosenFromSlider = true;
				        notifySkyChange();
				    }
				    sendConvenienceEvent();
				}
			}
		});

		hipsDropDown.addStyleName("skyDropDown");

		return hipsDropDown;
	}    

	private void fillDropDownMenus(HipsWavelength defaultWavelength, String defaultHips) {
	    blockNotifications = true;
		for (final SkiesMenuEntry menuEntry : skiesMenu.getMenuEntries()) {
			if(menuEntry.getWavelength() == HipsWavelength.USER) {
				for(HiPS hips: menuEntry.getHips()) {
				    if(!listOfUserHips.contains(hips)) {
				        listOfUserHips.add(hips);
				    }
				}
			}
			createWavelengthOption(menuEntry.getWavelength());
		}
		wavelengthDropDown.selectObject(defaultWavelength);
		setSelectHips(defaultHips, false, false);
		blockNotifications = false;
	}

	private void createWavelengthOption(HipsWavelength wavelength) {
		MenuItem<HipsWavelength> dropdownItem = new MenuItem<HipsWavelength>(
				wavelength, TextMgr.getInstance().getText("wavelength_" + wavelength.name()), 
				TextMgr.getInstance().getText("wavelength_" + wavelength.name() + "_Tooltip"), true);
		wavelengthDropDown.addMenuItem(dropdownItem);
	}  

	private void fillHiPSMenuBar(final List<HiPS> hipsList, boolean removable) {
		hipsDropDown.clearItems();

		HiPS defaultHips = null;
		for (final HiPS hips : hipsList) {
			MenuItem<HiPS> dropdownItem = new MenuItem<HiPS>(hips, hips.getSurveyName(), true, removable,
			        new OnRemove<HiPS>() {

                        @Override
                        public void onRemove(MenuItem<HiPS> menuItem) {
                            notifyMenuItemRemoveClicked(menuItem);
                        }
			    
                    });
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
        for(MenuItem<HiPS> menuItemToRemove : hipsDropDown.getMenuItems()) {
            if(menuItemToRemove.getItem().equals(menuItem.getItem())){
                hipsDropDown.removeMenuItem(menuItemToRemove);
                listOfUserHips.remove(menuItemToRemove.getItem());
                List<SkiesMenuEntry> entriesToDelete = new LinkedList<SkiesMenuEntry>();
                for (final SkiesMenuEntry menuEntry : skiesMenu.getMenuEntries()) {
                    if(menuEntry.getWavelength() == HipsWavelength.USER) {
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
                
                if(menuItemToRemove.getIsSelected()) {
                    if(hipsDropDown.getMenuItems().size() > 0) {
                        for (MenuItem<HiPS> menuItemToSelect : hipsDropDown.getMenuItems()) {
                            if(!menuItemToRemove.equals(menuItemToSelect)) {
                                hipsDropDown.selectObject(menuItemToSelect.getItem());
                                if(isSelected()) {
                                    notifySkyChange();
                                    sendConvenienceEvent();
                                }
                                break;
                            }
                        }
                    } else {
                        hipsDropDown.hideMenuBar();
                        notifyClose();
                    }
                } else if (hipsDropDown.getMenuItems().size() == 0) {
                    hipsDropDown.hideMenuBar();
                    notifyClose();
                }
                break;
            }
        }
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
						sendConvenienceEvent();
					}
					return true;
				}
			}
		}
		if(newHips) {
			listOfUserHips.add(lastCreatedUserHiPS);
			MenuItem<HiPS> menuItem = new MenuItem<HiPS>(lastCreatedUserHiPS, hipsName, true, true, new OnRemove<HiPS>() {

                @Override
                public void onRemove(MenuItem<HiPS> menuItem) {
                    notifyMenuItemRemoveClicked(menuItem);
                }
        
            }); 
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
					sendConvenienceEvent();
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
	    if(!blockNotifications) {
			for(SkyObserver observer: observers){
				observer.onUpdateSkyEvent(this);
			}
			sendUpdateSkyName();
			//Notify sky change to Google Analytics
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SKIESMENU, GoogleAnalytics.ACT_SKIESMENU_SELECTEDSKY, getFullId());
	    }
	}

	public void notifyClose(){
		for(SkyObserver observer: observers){
			observer.onCloseEvent(this);
		}
	}
	
	private void sendConvenienceEvent() {
		if(!isSelected()) return;
		if(!blockNotifications) {
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
        return wavelengthDropDown.getSelectedObject().name() + " - " + getNameofSelected() + " - " + getSelectedPalette().name();
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
	    if(wavelengthDropDown.getSelectedObject() == HipsWavelength.USER) {
	        HiPS selectedObject = hipsDropDown.getSelectedObject();
	        fillHiPSMenuBar(listOfUserHips, true);
	        hipsDropDown.selectObject(selectedObject);
	    }
	    blockNotifications = false;
	}
    
    
    
}
