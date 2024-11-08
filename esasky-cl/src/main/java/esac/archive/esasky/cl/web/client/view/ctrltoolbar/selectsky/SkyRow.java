package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import java.util.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import com.google.gwt.user.client.ui.VerticalPanel;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.HipsLayerChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ImageLayer;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.ImageConfigPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.*;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.client.SkiesMenuEntry;
import esac.archive.esasky.ifcs.model.shared.ColorPalette;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.hips.HipsNameChangeEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.status.ScreenSizeService;
import esac.archive.esasky.cl.web.client.status.ScreenWidth;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.Selectable;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

public class SkyRow extends Composite implements Selectable{

	private FlowPanel skyPanel;

	private int WAVELENGTH_PX_SIZE = 122;   
	private int HIPS_PX_SIZE = 129;

	private EsaSkyButton imageSettingsButton;

	private EsaSkyCheckButton selectCheckButton;
	private EsaSkyImageToggleButton visibilityToggleButton;
	private CloseButton removeSkyBtn;
	private DropDownMenu<String> wavelengthDropDown;
	private DropDownMenu<HiPS> hipsDropDown;
	private boolean onlyOneSkyActive = true;
	private boolean isChosenFromSlider = false;
	private boolean blockNotifications = false;

	private List<SkyObserver> observers = new LinkedList<>();

	private SkiesMenu skiesMenu;

	private Resources resources;
	private Style style;

	private ImageConfigPanel imageConfigPanel;

	private final String rowId;
	private boolean isBase;

	public static interface Resources extends ClientBundle {

		@Source("sky.css")
		@CssResource.NotStrict
		Style style();

		@Source("hide-eye-icon.png")
		ImageResource hideIcon();

		@Source("visible-eye-icon.png")
		ImageResource showIcon();
	}

	public static interface Style extends CssResource {

		String skyPanel();

		String selectedSky();

		String skyInfoBtn();

		String hipsCreatorImage();

		String skyDropDown();

		String skyToggleVisibilityButton();

		String creatorContainer();

		String closeSkyButton();

		String collapse();

		String skySettingButton();
	}

	public SkyRow(SkiesMenu skiesMenu, String hipsName, boolean isBase){
	    this(skiesMenu, hipsName, null, false, isBase, null);
	}

	public SkyRow(SkiesMenu skiesMenu, String hipsName, boolean isBase, String currentId){
		this(skiesMenu, hipsName, null, false, false, currentId);

	}
	
	public SkyRow(SkiesMenu skiesMenu, String hipsName, String category, boolean isDefault, boolean isBase, String currentId){
	    blockNotifications = true;
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();

		this.isBase = isBase;
		this.rowId = isBase ? "base" : "SkyRow_" + UUID.randomUUID();

		if (currentId != null) {
			AladinLiteWrapper.getInstance().renameImageLayer(currentId, rowId);
		}

		this.skiesMenu = skiesMenu;

		String wavelength = category != null ? category : skiesMenu.getWavelengthFromHiPSName(hipsName);
		if(wavelength == null){
			wavelength = EsaSkyConstants.DEFAULT_WAVELENGTH;
			hipsName = EsaSkyConstants.ALADIN_DEFAULT_SURVEY_NAME;
		}
		
		initView(wavelength, hipsName, isDefault);
		blockNotifications = false;
	}

	private void initView(String defaultWavelength, String defaultHips, boolean isDefault) {
		VerticalPanel container = new VerticalPanel();
		skyPanel = new FlowPanel();
		skyPanel.setStyleName(this.style.skyPanel());

		imageConfigPanel = new ImageConfigPanel(true);
		skyPanel.add(createSelectBtn());
		wavelengthDropDown = createWavelengthDropdown();
		skyPanel.add(wavelengthDropDown);
		hipsDropDown = createHipsDropDown();
		skyPanel.add(hipsDropDown);
		imageSettingsButton = createSettingsBtn();
		skyPanel.add(imageSettingsButton);
		skyPanel.add(createToggleRowVisibilityBtn());


		removeSkyBtn = createRemoveSkyBtn();
		skyPanel.add(removeSkyBtn);
		skyPanel.setStyleName(this.style.skyPanel());
		skyPanel.addStyleName(this.style.selectedSky());

		fillDropDownMenus(defaultWavelength, defaultHips);

		container.add(skyPanel);
		container.add(imageConfigPanel);

		initWidget(container);

		ScreenSizeService.getInstance().registerObserver(this::setWidth);
		setWidth();
		
		if (isDefault) {
			removeSkyBtn.addStyleName("collapse");
		}

		String mode = Modules.getMode();
		if (mode != null && Objects.equals(mode.toUpperCase(), EsaSkyWebConstants.MODULE_MODE_KIOSK)) {
			imageSettingsButton.addStyleName("displayNone");
			visibilityToggleButton.addStyleName("displayNone");

		}

		registerObserver(new SkyObserver() {
			@Override
			public void onUpdateSkyEvent(SkyRow sky) {
				imageConfigPanel.collapse();
			}

			@Override
			public void onCloseEvent(SkyRow sky) {
				imageConfigPanel.collapse();
			}

			@Override
			public void onMenuItemRemovalEvent(MenuItem<HiPS> menuItem) {
				imageConfigPanel.collapse();
			}

			@Override
			public void onImageSettingsClicked(SkyRow skyRow) {
				ImageLayer layer = AladinLiteWrapper.getAladinLite().getImageLayer(getRowId());
				HiPS hips = getSelectedHips();
				imageConfigPanel.setLayerAndHiPS(layer, hips);
				imageConfigPanel.toggle();
			}
		});

		CommonEventBus.getEventBus().addHandler(HipsLayerChangedEvent.TYPE, event -> {
			ImageLayer layer = event.getLayer();
			if (this.getRowId().equals(layer.getLayer())) {
				double opacity = layer.getColorCfg().getOpacity();
				if (opacity > 0) {
					visibilityToggleButton.setToggled(true, false);
					visibilityToggleButton.removeStyleName("off");
				} else if (opacity <= 0) {
					visibilityToggleButton.setToggled(false, false);
					visibilityToggleButton.addStyleName("off");
				}
			}
		});
	}

	private void setWidth() {
		if(MainLayoutPanel.getMainAreaWidth() > ScreenWidth.SMALL.getPxSize() && !onlyOneSkyActive) {
			setWidth(410 + "px");
		} else {
			setWidth(380 + "px");
		}
	}

	private DropDownMenu<String> createWavelengthDropdown() {

		final DropDownMenu<String> wavelengthDropDown = new DropDownMenu<>
                (TextMgr.getInstance().getText("sky_wavelength"),
                        TextMgr.getInstance().getText("sky_selectWavelength"), WAVELENGTH_PX_SIZE, "wavelengthDropDown" + getRowId());

		wavelengthDropDown.registerObserver(() -> {
            List<HiPS> listOfHipsByWavelength;
            if(!HipsWavelength.wavelengthList.contains(wavelengthDropDown.getSelectedObject())) {
                listOfHipsByWavelength = HipsWavelength.getListOfUserHips().get(wavelengthDropDown.getSelectedObject());
                fillHiPSMenuBar(listOfHipsByWavelength, true);
			} else {
                listOfHipsByWavelength = skiesMenu.getHiPSListByWavelength(wavelengthDropDown.getSelectedObject()).getHips();
                fillHiPSMenuBar(listOfHipsByWavelength, false);
				if (!Objects.equals(wavelengthDropDown.getSelectedObject(), HipsWavelength.GW)) {
					selectDefaultHips(listOfHipsByWavelength);
				}
            }
        });

		wavelengthDropDown.addStyleName("skyDropDown");

		return wavelengthDropDown;
	}

	private DropDownMenu<HiPS> createHipsDropDown() {

		final DropDownMenu<HiPS> hipsDropDown = new DropDownMenu<>(
                TextMgr.getInstance().getText("sky_skies"),
                TextMgr.getInstance().getText("sky_selectASky"), HIPS_PX_SIZE, "hipsDropdown");

		hipsDropDown.registerObserver(() -> {
            final HiPS hips = hipsDropDown.getSelectedObject();
            final ColorPalette colorPalette = hips.getColorPalette();
            imageConfigPanel.setDefaultColorPallette(colorPalette);
            notifySkyChange();

            if(!blockNotifications) {
                sendEvents();
            }
        });

		hipsDropDown.addStyleName("skyDropDown");

		return hipsDropDown;
	}    

	private void fillDropDownMenus(String defaultWavelength, String defaultHips) {
	    blockNotifications = true;
	    
		for (final SkiesMenuEntry menuEntry : skiesMenu.getMenuEntries()) {
			if((!HipsWavelength.wavelengthList.contains(menuEntry.getWavelength()) )) {
				for(HiPS hips: menuEntry.getHips()) {
                    HipsWavelength.getListOfUserHips().computeIfAbsent(menuEntry.getWavelength(), k -> new LinkedList<>());
					
				    if(!HipsWavelength.getListOfUserHips().get(menuEntry.getWavelength()).contains(hips)) {
				    	HipsWavelength.getListOfUserHips().get(menuEntry.getWavelength()).add(hips);
				    }
				}
			}
			if (!wavelengthDropDown.containsItem(menuEntry.getWavelength())) {
				createWavelengthOption(menuEntry.getWavelength());
			}
		}

		if(!HipsWavelength.wavelengthList.contains(defaultWavelength)) {
            HipsWavelength.getListOfUserHips().computeIfAbsent(defaultWavelength, k -> new LinkedList<>());
		}
		
		wavelengthDropDown.selectObject(defaultWavelength);
		setSelectHips(defaultHips, false, null);
		blockNotifications = false;
	}

	private void createWavelengthOption(String wavelength) {
		MenuItem<String> dropdownItem;
		if(HipsWavelength.wavelengthList.contains(wavelength)) {
			dropdownItem = new MenuItem<>(
                    wavelength, TextMgr.getInstance().getText("wavelength_" + wavelength),
                    TextMgr.getInstance().getText("wavelength_" + wavelength + "_Tooltip"), true);
		}else {
			dropdownItem = new MenuItem<>(
                    wavelength, wavelength,
                    wavelength, true);
		}
		
		wavelengthDropDown.addMenuItem(dropdownItem);
	}  

	private void fillHiPSMenuBar(final List<HiPS> hipsList, boolean removable) {
		hipsDropDown.clearItems();

		String mode = Modules.getMode();

		for (final HiPS hips : hipsList) {
			if (mode != null && Objects.equals(mode.toUpperCase(), EsaSkyWebConstants.MODULE_MODE_KIOSK) && !hips.getIsDefault()) {
				continue;
			}

			MenuItem<HiPS> dropdownItem = new MenuItem<>(hips, hips.getSurveyName(), true, removable, this::notifyMenuItemRemoveClicked);
			hipsDropDown.addMenuItem(dropdownItem);

		}
	}

	private void selectDefaultHips(final List<HiPS> hipsList) {
		HiPS defaultHips = null;
		String mode = Modules.getMode();
		for (final HiPS hips : hipsList) {
			if (mode != null && Objects.equals(mode.toUpperCase(), EsaSkyWebConstants.MODULE_MODE_KIOSK) && !hips.getIsDefault()) {
				continue;
			}

			if(hips.getIsDefault()){
				defaultHips = hips;
				break;
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
            if(!hipsDropDown.getMenuItems().isEmpty()) {
                selectFirstNextEntry(menuItemToRemove);
            } else {
                hipsDropDown.hideMenuBar();
                notifyClose();
            }
        } else if (hipsDropDown.getMenuItems().isEmpty()) {
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
        List<SkiesMenuEntry> entriesToDelete = new LinkedList<>();
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
	public void setHiPSFromAPI(HiPS hips, boolean newHips){
		lastCreatedUserHiPS = hips;
		setSelectHips(hips.getSurveyName(), newHips, hips.getHipsCategory());
	}
	
	public boolean setSelectHips(String hipsName, boolean newHips, String category){
		blockNotifications = true;
		String wavelength = skiesMenu.getWavelengthFromHiPSName(hipsName);
	    if(wavelength == null) {
	    	wavelength = category != null ? category : HipsWavelength.USER;
	    	if(!HipsWavelength.wavelengthList.contains(wavelength) && HipsWavelength.getListOfUserHips().get(wavelength) == null) {
	    		HipsWavelength.getListOfUserHips().put(wavelength, new LinkedList<>());
	    	}
	    	if(HipsWavelength.getListOfUserHips().get(wavelength).isEmpty()) {
	    		createWavelengthOption(wavelength);
	    	}
	    }
	    
		if (!Objects.equals(wavelength, wavelengthDropDown.getSelectedObject())) {
			wavelengthDropDown.selectObject(wavelength);
		}
		for(MenuItem<HiPS> menuItem: hipsDropDown.getMenuItems()){
			if(menuItem.getItem().getSurveyName().equalsIgnoreCase(hipsName)){
				if(newHips) {
					hipsDropDown.removeMenuItem(menuItem);
					HipsWavelength.getListOfUserHips().get(wavelength).remove(menuItem.getItem());
				}else {
					hipsDropDown.selectObject(menuItem.getItem(), false);
					imageConfigPanel.setDefaultColorPallette(menuItem.getItem().getColorPalette());
					notifySkyChange();
					blockNotifications = false;
					setSelected(false);
					sendEvents();
					return true;
				}
			}
		}
		if(newHips) {
			String hipsCategory = category != null ? category : lastCreatedUserHiPS.getHipsWavelength();
			HipsWavelength.getListOfUserHips().get(hipsCategory).add(lastCreatedUserHiPS);
			MenuItem<HiPS> menuItem = new MenuItem<>(lastCreatedUserHiPS, hipsName, true, true, this::notifyMenuItemRemoveClicked);
			hipsDropDown.addMenuItem(menuItem);
			blockNotifications = false;
			hipsDropDown.selectObject(menuItem.getItem());
			return true;
		}
		//No hips with correct name found
		return false;
	}


	private EsaSkyCheckButton createSelectBtn() {
		selectCheckButton = new EsaSkyCheckButton("skyRow");
		selectCheckButton.addStyleName("skyCheckBtn");
		selectCheckButton.registerValueChangeObserver(isSelected -> {
            if (isSelected) {
                AladinLiteWrapper.getInstance().focusImageLayer(getRowId());
				sendEvents();
            } else {
                AladinLiteWrapper.getInstance().restoreImageLayersFocus();
            }
        });
		return selectCheckButton;
	}

	private EsaSkyImageToggleButton createToggleRowVisibilityBtn() {
		visibilityToggleButton = new EsaSkyImageToggleButton(resources.showIcon(), resources.hideIcon(), true);
		visibilityToggleButton.setRoundStyle();
		visibilityToggleButton.addStyleName(this.style.skyToggleVisibilityButton());

		visibilityToggleButton.registerValueChangeObserver(isToggled -> {
			AladinLiteWrapper.getInstance().toggleImageLayer(getRowId());
			selectCheckButton.deselectAll();
			imageConfigPanel.setDefaultValues();
        });

		return visibilityToggleButton;
	}
	
	private void sendUpdateSkyName() {
		CommonEventBus.getEventBus().fireEvent(
				new HipsNameChangeEvent(getNameofSelected()));
	}

	public boolean isSelected(){
		return selectCheckButton.isSelected();
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
		selectCheckButton.setSelected(true, !blockNotifications);
		isChosenFromSlider = false;
	}


	private EsaSkyButton createSettingsBtn() {
		EsaSkyButton settingsBtn = new EsaSkyButton(Icons.getSettingsIcon());
		settingsBtn.addStyleName(this.style.skySettingButton());
		settingsBtn.addClickHandler(event -> notifyImageSettingsClicked());

		return settingsBtn;
	}

	private CloseButton createRemoveSkyBtn(){
		CloseButton removeSkyBtn = new CloseButton();
		removeSkyBtn.setLightStyle();
		removeSkyBtn.setTitle(TextMgr.getInstance().getText("sky_removeSky"));
		removeSkyBtn.addStyleName(this.style.closeSkyButton());

		removeSkyBtn.addClickHandler(event -> notifyClose());
		return removeSkyBtn;
	}

	public HiPS getSelectedHips(){
		if(hipsDropDown!= null) {
			return hipsDropDown.getSelectedObject();
		}
		return null;
	}

	public ColorPalette getSelectedPalette(){
		return imageConfigPanel.getSelectedColorPalette();
	}
	
	public void setColorPalette(ColorPalette colorPalette) {
		if (imageConfigPanel.getLayer() == null) {
			imageConfigPanel.discoverLayer(getRowId());
		}
		imageConfigPanel.setDefaultColorPallette(colorPalette);
	}

	public ImageConfigPanel getImageConfigPanel() {
		return imageConfigPanel;
	}

	public void addOnlyOneSkyActiveStyle(){
		onlyOneSkyActive = true;
		visibilityToggleButton.addStyleName("collapse");
		removeSkyBtn.addStyleName("collapse");
		selectCheckButton.addStyleName("collapse");
		setWidth();
	}
	
	
	public void disableDeleteButton(){
		removeSkyBtn.setEnabled(false);
	}

	public void removeOnlyOneSkyActiveStyle(){
		onlyOneSkyActive = false;
		visibilityToggleButton.removeStyleName("collapse");
		removeSkyBtn.removeStyleName("collapse");
		selectCheckButton.removeStyleName("collapse");

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

	public void notifyImageSettingsClicked() {
		for(SkyObserver observer: observers){
			observer.onImageSettingsClicked(this);
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

	public boolean isBase() {
		return isBase;
	}

	public void setBase(boolean isBase) {
		this.isBase = isBase;
	}

	public boolean isChosenFromSlider() {
		return isChosenFromSlider;
	}

	public void setChosenFromSlider(boolean isChosenFromSlider) {
		this.isChosenFromSlider = isChosenFromSlider;
	}
	
	public void setOpacity(double opacity) {
		AladinLiteWrapper.getInstance().changeImageLayerOpacity(getRowId(), opacity);
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

	public String getRowId() {
		return this.isBase ? "base" : rowId;
	}
    
}
