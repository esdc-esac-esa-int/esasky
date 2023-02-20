package esac.archive.esasky.cl.web.client.presenter;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.callback.ICommand;
import esac.archive.esasky.cl.web.client.callback.Promise;
import esac.archive.esasky.cl.web.client.event.*;
import esac.archive.esasky.cl.web.client.model.DescriptorCountAdapter;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CustomTreeMapDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;

import java.util.*;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class CtrlToolBarPresenter {

    private View view;
    
    private final SelectSkyPanelPresenter selectSkyPresenter;
    private final PublicationPanelPresenter publicationPresenter;
    List<CustomTreeMapDescriptor> treeMapDescriptors = new LinkedList<CustomTreeMapDescriptor>();
    HashMap<String, EsaSkyButton> customButtons = new HashMap<String, EsaSkyButton>();


    public interface SkiesMenuMapper extends ObjectMapper<SkiesMenu> {}
    
    public interface View {

        void updateObservationCount(long newCount);
        void updateCatalogCount(long newCount);
        void updateSpectraCount(long newCount);
        void updateSsoCount(long newCount);
        void onIsTrackingSSOEventChanged();
        void closeTreeMap();
        void closeAllOtherPanels(Widget button);
        void updateModuleVisibility();

        EsaSkyToggleButton getPublicationButton();
        EsaSkyToggleButton getSkyPanelButton();
        
        SelectSkyPanelPresenter.View getSelectSkyView();
        PublicationPanelPresenter.View getPublicationPanelView();

        void addTreeMapData(Collection<DescriptorCountAdapter> descriptorCountAdapters);
        void clearTreeMapData(String category);

        void addCustomTreeMap(CustomTreeMapDescriptor customTreeMapDescriptor);

        void updateCustomTreeMap(CustomTreeMapDescriptor customTreeMapDescriptor);

        EsaSkyButton addCustomButton(ImageResource icon, String iconText, String description);

        void removeCustomButton(EsaSkyButton button);

        void openGWPanel(int tabId);

        void closeGWPanel();
        void minimiseGWPanel();

        void getGWIds(Promise<JSONArray> gwDataPromise);

        void getGWData(String id, Promise<JSONObject> gwDataPromise);

        void getAllGWData(Promise<JSONObject> neutrinoData);
        
        void getNeutrinoEventData(Promise<JSONObject> neutrinoData);

        void showGWEvent(String id);

        void showGWEvent(String id, Promise<Boolean> showPromise);

        void clickExploreButton();
        void openExtTapPanel();
        void closeExtTapPanel();
        boolean isExtTapPanelOpen();

        void openOutreachPanel(String id);

        void closeOutreachPanel();

        void openJwstOutreachPanel(String id);

        void closeJwstOutreachPanel();

        void openExtTapPanelTab(int index);

        void closeExtTapPanelTab();
        JSONArray getOutreachImageIds(ICommand command, String telescope);

        void showOutreachImage(String id, String telescope);
        void hideOutreachImage(String telescope);

        Map<String, Double[]> getSliderValues();
        void setSliderValues(Map<String, Double[]> sliderMap);
    }

    public CtrlToolBarPresenter(final View inputView, DescriptorRepository descriptorRepo, EntityRepository entityRepo) {
        this.view = inputView;
        
		selectSkyPresenter = new SelectSkyPanelPresenter(view.getSelectSkyView());

		publicationPresenter = new PublicationPanelPresenter(view.getPublicationPanelView(), descriptorRepo, entityRepo);
			
        bind();
        updateScienceModeElements();
        view.updateModuleVisibility();
    }

    private void bind() {
        /*
         * Multitarget pointer in the middle of the current sky
         */
        CommonEventBus.getEventBus().addHandler(MultiTargetClickEvent.TYPE, clickEvent -> {
            ESASkySearchResult target = clickEvent.getTarget();
            if (Boolean.TRUE.equals(target.getValidInput())) {

                if (target.getUserInputType() == SearchInputType.TARGET) {
                    AladinLiteWrapper.getInstance().goToTarget(target.getSimbadRaDeg(), target.getSimbadDecDeg(), target.getFoVDeg(), false, target.getCooFrame());
                } else {

                    String[] raDecDeg = { target.getUserRaDeg(), target.getUserDecDeg() };
                    if (target.getCooFrame() != null) {

                        if (target.getCooFrame().equals(AladinLiteConstants.FRAME_GALACTIC)) {
                            // Since Aladin.View.pointTo always does the conversion from GAL
                            // to J2000 even if the coordinates are already in GAL we need to
                            // convert the coordinates back to J2000 and leave AladinLite to do the conversion

                            Log.debug("Clicked on source uploaded with FRAME GALACTIC");
                            Log.debug("mtl source SIMBADinput [" + target.getSimbadRaDeg() + ","
                                    + target.getSimbadDecDeg() + "]");
                            double[] raDecDegJ2000 = CoordinatesConversion
                                    .convertPointGalacticToJ2000(
                                            Double.parseDouble(target.getSimbadRaDeg()),
                                            Double.parseDouble(target.getSimbadDecDeg()));
                            Log.debug("mtl (after conversion) source input ["
                                    + raDecDegJ2000[0] + "," + raDecDegJ2000[1] + "]");
                            raDecDeg[0] = target.getSimbadRaDeg();
                            raDecDeg[1] = target.getSimbadDecDeg();
                        }
                        AladinLiteWrapper.getInstance().goToTarget(raDecDeg[0], raDecDeg[1],
                                target.getFoVDeg(), false, AladinLiteConstants.FRAME_J2000);
                    }
                    AladinLiteWrapper.getInstance().goToTarget(raDecDeg[0], raDecDeg[1],
                            target.getFoVDeg(), false, AladinLiteConstants.FRAME_J2000);
                }
            }
        });
        
        CommonEventBus.getEventBus().addHandler(IsTrackingSSOEvent.TYPE, () -> view.onIsTrackingSSOEventChanged());

        CommonEventBus.getEventBus().addHandler(TreeMapSelectionEvent.TYPE,
                event -> {
                    if(DeviceUtils.isMobile()) {
                        view.closeTreeMap();
                    }
                });
        
        CommonEventBus.getEventBus().addHandler(TreeMapNewDataEvent.TYPE, newDataEvent -> {
            if (newDataEvent.clearData()) {
                view.clearTreeMapData(newDataEvent.getClearCategory());
                DescriptorRepository.getInstance().doCountAll();
            } else {
                view.addTreeMapData(newDataEvent.getCountAdapterList());
            }
        });
         
        view.getPublicationButton().addClickHandler(event -> {
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CTRLTOOLBAR, GoogleAnalytics.ACT_CTRLTOOLBAR_PUBLICATIONS, "");
            publicationPresenter.toggle();
            view.closeAllOtherPanels(view.getPublicationButton());
        });
        
        CommonEventBus.getEventBus().addHandler(IsInScienceModeChangeEvent.TYPE, this::updateScienceModeElements);
        
        CommonEventBus.getEventBus().addHandler(ToggleSkyPanelEvent.TYPE, event -> {
            selectSkyPresenter.toggle();
            view.getSkyPanelButton().setToggleStatus(selectSkyPresenter.isShowing());;
            view.closeAllOtherPanels(view.getSkyPanelButton());
        });
        
        CommonEventBus.getEventBus().addHandler(CloseOtherPanelsEvent.TYPE, event -> 
				view.closeAllOtherPanels(event.getWidgetNotToClose())
		); 
        
        view.getSkyPanelButton().addClickHandler(event -> selectSkyPresenter.toggle());
    }
    
    private void updateScienceModeElements() {
		updateModuleVisibility();
    }
    
    public void updateObservationCount(long newCount){
        view.updateObservationCount(newCount);
    }
    
    public void updateCatalogCount(long newCount){
        view.updateCatalogCount(newCount);
    }
    
    public void updateSpectraCount(long newCount){
        view.updateSpectraCount(newCount);
    }
    
    public void updateSsoCount(long newCount){
        view.updateSsoCount(newCount);
    }
    
    public SelectSkyPanelPresenter getSelectSkyPresenter(){
    	return selectSkyPresenter;
    }
    
    public void addCustomTreeMap(CustomTreeMapDescriptor customTreeMapDescriptor){
    	treeMapDescriptors.add(customTreeMapDescriptor);
    	view.addCustomTreeMap(customTreeMapDescriptor);
    }
    
    public void updateCustomTreeMap(CustomTreeMapDescriptor customTreeMapDescriptor) {
    	view.updateCustomTreeMap(customTreeMapDescriptor);
    }
    
    public EsaSkyButton addCustomButton(String name, ImageResource icon, String iconText, String description) {
    	EsaSkyButton button = view.addCustomButton(icon, iconText, description);
    	removeCustomButton(name);
    	customButtons.put(name, button);
		return button;
	}
	
    public boolean updateCustomButton(String name, ImageResource icon, String iconText, String description) {
    	
    	if(customButtons.containsKey(name)) {
    		EsaSkyButton button = customButtons.get(name);
    		if(icon != null) {
    			button.setButtonImage(icon);
    			button.setImageStyle();
    		}else {
    			button.setButtonText(iconText);
    			button.setTextStyle();
    		}
    		
    		button.setTitle(description);
    		return true;
		}
		
		return false;
    }
    
    public void removeCustomButton(String name) {
    	if(customButtons.containsKey(name)) {
    		view.removeCustomButton(customButtons.get(name));
    		customButtons.remove(name);
    	}
	}
    
    public void customTreeMapClicked(TreeMapSelectionEvent event) {
    	for(CustomTreeMapDescriptor treeMapDescriptor : treeMapDescriptors) {
    		for(CommonTapDescriptor desc : treeMapDescriptor.getMissionDescriptors()) {
    			if(event.getDescriptor() == desc) {
    				treeMapDescriptor.getOnMissionClicked().onMissionClicked(desc.getMission());
    				return;
				}
    		}
    	}
    }
    
    public CustomTreeMapDescriptor getCustomTreeMapDescriptor(String name){
    	for(CustomTreeMapDescriptor treeMapDescriptor : treeMapDescriptors) {
    		if(treeMapDescriptor.getName().equals(name)) {
    			return treeMapDescriptor;
    		}
    	}
    	
    	return null;
    }
    
    public void updateModuleVisibility() {
    	view.updateModuleVisibility();
    }
    
    public PublicationPanelPresenter getPublicationPresenter() {
    	return publicationPresenter;
    }
    
    public void openGWPanel(int tabId) {
        view.openGWPanel(tabId);
    }

    public void closeGWPanel() {
        view.closeGWPanel();
    }

    public void minimiseGWPanel() {
        view.minimiseGWPanel();
    }

    public void getGWIds(Promise<JSONArray> gwDataPromise) {
        view.getGWIds(gwDataPromise);
    }

    public void getGWData(String id, Promise<JSONObject> gwDataPromise) {
        view.getGWData(id, gwDataPromise);
    }

    public void getAllGWData(Promise<JSONObject> gwDataPromise) {
        view.getAllGWData(gwDataPromise);
    }
    
    public void getNeutrinoEventData(Promise<JSONObject> neutrinoDataPromise) {
    	view.getNeutrinoEventData(neutrinoDataPromise);
    }

    public void showGWEvent(String id) {
        view.showGWEvent(id);
    }

    public void showGWEvent(String id, Promise<Boolean> showPromise) {
        view.showGWEvent(id, showPromise);
    }

    public void clickExploreButton() { view.clickExploreButton(); }

    public boolean isExtTapPanelOpen() {
        return view.isExtTapPanelOpen();
    }

    public void openOutreachPanel(String telescope, String id) {
        if("JWST".equals(telescope)) {
            view.openJwstOutreachPanel(id);
        } else {
            view.openOutreachPanel(id);
        }
    }

    public void closeOutreachPanel() {
        view.closeOutreachPanel();
    }

    public void closeJwstOutreachPanel() {
        view.closeJwstOutreachPanel();
    }

    public void openExternalTapPanel(String tab) {
        if("TAP REGISTRY".equals(tab)){
            view.openExtTapPanelTab(1);
        } else if("VIZIER".equals(tab)){
            view.openExtTapPanelTab(2);
        } else if("ESA".equals(tab)){
            view.openExtTapPanelTab(3);
        } else {
            view.openExtTapPanelTab(0);
        }
    }

    public void closeExtTapPanel() {
        view.closeExtTapPanelTab();
    }

    public JSONArray getOutreachImageIds(ICommand command, String telescope) {
        return view.getOutreachImageIds(command, telescope);
    }

    public void showOutreachImage(String id, String telescope) {
        view.showOutreachImage(id, telescope);
    }

    public void hideOutreachImage(String telescope) {
        view.hideOutreachImage(telescope);
    }
    public Map<String, Double[]> getSliderValues(){
    	return view.getSliderValues();
    }
    
    public void setSliderValues(Map<String, Double[]> sliderMap){
    	view.setSliderValues(sliderMap);
    }

}