package esac.archive.esasky.cl.web.client.view.common.icons;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;


public class Icons {
	
	private static Resources resources = GWT.create(Resources.class);
	private static HashMap<String, ImageResource> iconMap;
	
	public static interface Resources extends ClientBundle {

		//CtrlToolBar
		
		@Source("selectSky.png")
		ImageResource selectSky();

		@Source("target_list.png")
		ImageResource targetList();
		
		@Source("plan_observation.png")
		ImageResource planObservation();
		
		@Source("galaxy_light_outline.png")
		ImageResource observationIcon();
		
		@Source("catalog_map_outline.png")
		ImageResource catalogIcon();

		@Source("extTap.png")
		ImageResource extTapIcon();
		
		@Source("spectra_light_outline.png")
		ImageResource spectraIcon();
		
		@Source("saturn_light_outline.png")
		ImageResource ssoIcon();

		@Source("publications_outline.png")
        ImageResource publicationsIcon();
		
		@Source("random_dice.png")
		ImageResource exploreIcon();
		
		//PlayerPanel
		
        @Source("previous.png")
        @ImageOptions(flipRtl = true)
        ImageResource previous();

        @Source("next.png")
        @ImageOptions(flipRtl = true)
        ImageResource next();

        @Source("play.png")
        @ImageOptions(flipRtl = true)
        ImageResource play();

        @Source("pause.png")
        @ImageOptions(flipRtl = true)
        ImageResource pause();
        
		//Toggler and Dropdown
        @Source("down-arrow.png")
		@ImageOptions(width = 10, height = 10)
		ImageResource downArrow();
		
        //Menuitem
        @Source("selectedIcon.png")
        @ImageOptions(flipRtl = true)
        ImageResource selectedIcon();

        @Source("fullscreen.png")
        ImageResource fullscreenIcon();

        @Source("no_fullscreen.png")
        ImageResource endFullscreenIcon();
        
        //SkyPanel
		@Source("information.png")
		ImageResource info();

		@Source("plus-sign-light.png")
		ImageResource addSky();
		
        @Source("changePalette.png")
        ImageResource changePalette();
		
	}
	
	private static void initMap() {
		iconMap = new HashMap<>();
	
		iconMap.put("selectSky", getSelectSkyIcon());
		iconMap.put("targetList", getTargetListIcon());
		iconMap.put("planObservation", getPlanObservationIcon());
		iconMap.put("observation", getObservationIcon());
		iconMap.put("catalog", getCatalogIcon());
		iconMap.put("exttap", getExtTapIcon());
		iconMap.put("spectra", getSpectraIcon());
		iconMap.put("sso", getSsoIcon());
		iconMap.put("publications", getPublicationsIcon());
		iconMap.put("dice", getExploreIcon());
		iconMap.put("previous", getPreviousIcon());
		iconMap.put("next", getNextIcon());
		iconMap.put("play", getPlayIcon());
		iconMap.put("pause", getPauseIcon());
		iconMap.put("downArrow", getDownArrowIcon());
		iconMap.put("selected", getSelectedIcon());
		iconMap.put("fullscreen", getFullscreenIcon());
		iconMap.put("endFullscreen", getEndFullscreenIcon());
		iconMap.put("changePalette", getChangePaletteIcon());
		iconMap.put("info", getInfoIcon());
		iconMap.put("addSky", getAddSkyIcon());
		
	}
	
	public static HashMap<String, ImageResource> getIconMap(){
		if(iconMap == null) {
			initMap();
		}
		return iconMap;
	}
	

	public static ImageResource getAddSkyIcon() {
		return resources.addSky();
	}
	
	public static ImageResource getInfoIcon() {
		return resources.info();
	}
	
	public static ImageResource getChangePaletteIcon() {
		return resources.changePalette();
	}

	public static ImageResource getSelectSkyIcon() {
		return resources.selectSky();
	}

	public static ImageResource getTargetListIcon() {
		return resources.targetList();
	}
	
	public static ImageResource getPlanObservationIcon() {
		return resources.planObservation();
	}
	
	public static ImageResource getObservationIcon() {
		return resources.observationIcon();
	}
	
	public static ImageResource getCatalogIcon() {
		return resources.catalogIcon();
	}
	
	public static ImageResource getExtTapIcon() {
		return resources.extTapIcon();
	}
	
	public static ImageResource getSpectraIcon() {
		return resources.spectraIcon();
	}
	
	public static ImageResource getSsoIcon() {
		return resources.ssoIcon();
	}
		
	public static ImageResource getPublicationsIcon() {
		return resources.publicationsIcon();
	}
	
	public static ImageResource getExploreIcon() {
		return resources.exploreIcon();
	}
	
	
	public static ImageResource getPreviousIcon() {
		return resources.previous();
	}
	
	public static ImageResource getNextIcon() {
		return resources.next();
	}
	
	public static ImageResource getPlayIcon() {
		return resources.play();
	}
	
	public static ImageResource getPauseIcon() {
		return resources.pause();
	}
	
	
	public static ImageResource getDownArrowIcon() {
		return resources.downArrow();
	}
	
	public static ImageResource getSelectedIcon() {
		return resources.selectedIcon();
	}

	public static ImageResource getFullscreenIcon() {
		return resources.fullscreenIcon();
	}
	
	public static ImageResource getEndFullscreenIcon() {
		return resources.endFullscreenIcon();
	}
}
