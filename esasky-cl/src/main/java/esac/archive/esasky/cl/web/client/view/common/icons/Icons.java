package esac.archive.esasky.cl.web.client.view.common.icons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

import java.util.HashMap;


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

		@Source("gravitational-wave.png")
		ImageResource gwIcon();
		
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
        
        //Header
		@Source("share.png")
		ImageResource shareIcon();

		@Source("question_mark.png")
		ImageResource questionMark();

		@Source("screenshot.png")
		ImageResource screenshotIcon();

		@Source("menu.png")
		ImageResource menuIcon();

		@Source("wwt_logo.png")
		ImageResource wwtLogo();
		
        @Source("warning.png")
        ImageResource warning();

        @Source("grid.png")
        ImageResource gridIcon();
        
		@Source("download.png")
		ImageResource download();
		
		@Source("exclamation.png")
		ImageResource exclamationIcon();

		@Source("expand.png")
		ImageResource expandIcon();

		@Source("contract.png")
		ImageResource contractIcon();
		
		@Source("hubble-hires.png")
		ImageResource hubbleIcon();

		// selection
		@Source("selection-rectangle-dashed.png")
		ImageResource dashedRectIcon();

		@Source("selection-circle-dashed.png")
		ImageResource dashedCircleIcon();

		@Source("selection-poly-dashed.png")
		ImageResource dashedPolyIcon();

		@Source("selection-poly-dashed-dark.png")
		ImageResource dashedPolyDarkIcon();

		@Source("cone.png")
		ImageResource coneIcon();

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
		iconMap.put("share", getShareIcon());
		iconMap.put("questionMark", getQuestionMarkIcon());
		iconMap.put("screenshot", getScreenshotIcon());
		iconMap.put("menuIcon", getMenuIcon());
		iconMap.put("wwtLogo", getWwtLogoIcon());
		iconMap.put("warning", getWarningIcon());
		iconMap.put("grid", getGridIcon());
		iconMap.put("download", getDownloadIcon());
		iconMap.put("exclamation", getExclamationIcon());
		iconMap.put("expand", getExpandIcon());
		iconMap.put("contract", getContractIcon());
		iconMap.put("hubble", getHubbleIcon());
		
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

	public static ImageResource getGwIcon() {
		return resources.gwIcon();
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

	
	public static ImageResource getShareIcon() {
		return resources.shareIcon();
	}
	
	public static ImageResource getQuestionMarkIcon() {
		return resources.questionMark();
	}
	
	public static ImageResource getScreenshotIcon() {
		return resources.screenshotIcon();
	}
	
	
	public static ImageResource getMenuIcon() {
		return resources.menuIcon();
	}
	
	public static ImageResource getWwtLogoIcon() {
		return resources.wwtLogo();
	}

	public static ImageResource getWarningIcon() {
		return resources.warning();
	}
	
	public static ImageResource getGridIcon() {
		return resources.gridIcon();
	}

	public static ImageResource getDownloadIcon() {
		return resources.download();
	}
	
	public static ImageResource getExclamationIcon() {
		return resources.exclamationIcon();
	}

	public static ImageResource getExpandIcon() {
		return resources.expandIcon();
	}
	
	public static ImageResource getContractIcon() {
		return resources.contractIcon();
	}
	
	public static ImageResource getHubbleIcon() {
		return resources.hubbleIcon();
	}

	public static ImageResource getDashedCircleIcon() {
		return resources.dashedCircleIcon();
	}

	public static ImageResource getDashedPolyIcon() {
		return resources.dashedPolyIcon();
	}

	public static ImageResource getDashedPolyDarkIcon() {
		return resources.dashedPolyDarkIcon();
	}

	public static ImageResource getDashedRectangleIcon() {
		return resources.dashedRectIcon();
	}

	public static ImageResource getConeIcon() { return resources.coneIcon(); }
}
