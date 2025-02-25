/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.*;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.ESASkyMultiRangeSlider;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Arrays;


public class TreeMapContainer extends MovableResizablePanel<TreeMapContainer>{

    private static final Set<EntityContext> TREE_MAPS_WITH_LOADING_SPINNERS = new HashSet<>(Arrays.asList(EntityContext.ASTRO_CATALOGUE, EntityContext.ASTRO_SPECTRA, EntityContext.ASTRO_IMAGING));

	private final CssResource style;
	private final Resources resources;
	private boolean firstOpening = true;
	private final EntityContext context;

	private final TreeMap treeMap;
	private final FlowPanel treeMapContainer = new FlowPanel();
	private final PopupHeader<TreeMapContainer> header;
	private ESASkyMultiRangeSlider slider;
	private FlowPanel sliderContainer;
	boolean haveSlider;

	private final Set<String> currentlyLoadingDataIds = new HashSet<>();

	private final List<TreeMapChanged> treemapObservers = new LinkedList<>();

	public interface Resources extends ClientBundle {
		@Source("treeMapContainer.css")
		@CssResource.NotStrict
		CssResource style();
		
		@Source("logo_IMCCE_web_ssodnet.svg")
		@MimeType("image/svg+xml")
		DataResource ssoDNetLogo();
	}
	public TreeMapContainer(EntityContext context){
		this(context, true);
	}
	
	public TreeMapContainer(EntityContext context, boolean shouldHaveSlider){
		super(GoogleAnalytics.CAT_TREEMAP + "_" + context, false);
		this.haveSlider = shouldHaveSlider;
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();
		this.context = context;

		treeMap = new TreeMap(context);

		if (TREE_MAPS_WITH_LOADING_SPINNERS.contains(context)) {
			setupLoadingSpinner();
		}

		this.addStyleName("treeMapContainer");
		if(!DeviceUtils.isMobileOrTablet()) {
			this.addStyleName("treeMapContainerDesktop");
		}
		treeMapContainer.getElement().setId("treeMapContainer_" + context);

		header = new PopupHeader<>(this, "", "");

		Image ssoDnetLogo = new Image(resources.ssoDNetLogo().getSafeUri());
		ssoDnetLogo.addStyleName("treeMap__ssoLogo");
		header.add(ssoDnetLogo);
		ssoDnetLogo.setVisible(context == EntityContext.SSO);

		treeMapContainer.add(header);
		treeMapContainer.add(treeMap);

		if(shouldHaveSlider) {
			sliderContainer = initSliderContainer();
			treeMapContainer.add(sliderContainer);
		}

		treeMap.setHasSlider(shouldHaveSlider);
		this.add(treeMapContainer);

		header.setText(TextMgr.getInstance().getText("treeMap_" + context));
		String helpText = TextMgr.getInstance().hasText("treeMapContainer_help_" + context) ? TextMgr.getInstance().getText("treeMapContainer_help_" + context): null;
		header.setHelpText(helpText);

		MainLayoutPanel.addMainAreaResizeHandler(event -> updateMaxSize());

	}

    private void setupLoadingSpinner() {
        final LoadingSpinner loadingSpinner = new LoadingSpinner(false);
        CommonEventBus.getEventBus().addHandler(ProgressIndicatorPushEvent.TYPE,
                new ProgressIndicatorPushEventHandler() {
                    @Override
                    public void onPushEvent(ProgressIndicatorPushEvent pushEvent) {
                        if (pushEvent instanceof CountProgressIndicatorPushEvent) {
                            currentlyLoadingDataIds.add(pushEvent.getId());
                            treeMap.addStyleName("loadingOverlay");
                            loadingSpinner.removeStyleName("treeMapContainer__invisibleSpinner");
                        }
                    }
                });

        CommonEventBus.getEventBus().addHandler(ProgressIndicatorPopEvent.TYPE,
                new ProgressIndicatorPopEventHandler() {
                    @Override
                    public void onPopEvent(ProgressIndicatorPopEvent popEvent) {
                        currentlyLoadingDataIds.remove(popEvent.getId());
                        if (currentlyLoadingDataIds.isEmpty()) {
                            treeMap.removeStyleName("loadingOverlay");
                            loadingSpinner.addStyleName("treeMapContainer__invisibleSpinner");
                        }
                    }
                });

        loadingSpinner.addStyleName("treeMapContainer__spinnerContainer");
        loadingSpinner.addStyleName("treeMapContainer__invisibleSpinner");
        treeMapContainer.add(loadingSpinner);
    }

    @Override
	protected void onLoad() {
		super.onLoad();
		show();
		hide();
	}
	
	private FlowPanel initSliderContainer() {
		FlowPanel sliderContainer = new FlowPanel();
		
		FlowPanel textPanel = new FlowPanel();
		textPanel.addStyleName("treeMap__filter__text__container");
		
        Label leftLabel = new Label();
        leftLabel.setText(TextMgr.getInstance().getText("wavelength_GAMMA_RAY"));
        leftLabel.addStyleName("treeMap__filter__text__left");
        
        Label centerLabel = new Label();
        centerLabel.setText(TextMgr.getInstance().getText("wavelength_OPTICAL"));
        centerLabel.addStyleName("treeMap__filter__text__center");
        
        Label rightLabel = new Label();
        rightLabel.setText(TextMgr.getInstance().getText("wavelength_RADIO"));
        rightLabel.addStyleName("treeMap__filter__text__right");
		
		textPanel.add(leftLabel);
		textPanel.add(centerLabel);
		textPanel.add(rightLabel);
		
		sliderContainer.add(textPanel);
		
		slider = new ESASkyMultiRangeSlider(0, ESASkyColors.maxIndex() , 300);
		slider.addStyleName("treeMap__slider");
		
		sliderContainer.add(slider);
		treeMap.addSliderObserver(slider);
		slider.registerValueChangeObserver(this::updateSliderColor);
		
		return sliderContainer;
	}
	
	public void updateSliderColor(double low, double high) {
			
			double botPosition = (1 -( low - Math.floor(low)) ) / (high - low);
			double topPosition = (1 - (Math.ceil(high) - high)) / ( high - low );
			
			String styleString =  "linear-gradient(to right,";
			int nShown = 0;
			
			for(int i = (int) Math.floor(low); i <=  Math.ceil(high); i++) {
				styleString += ESASkyColors.getColor(i);
				if(nShown == 1) {
					styleString += " " + botPosition * 100 + "%";
				}else if(nShown == (int) Math.ceil(high) - (int) Math.floor(low) - 1) {
					styleString += " " + (100 - topPosition * 100) + "%";
				}
				nShown++;
				styleString += ",";
			}
			styleString = styleString.substring(0,styleString.length() - 1);
			
			styleString += ")";
			slider.setSliderColor(styleString);
	}
	
	private void updateMaxSize() {
		int MIN_MARGIN_RIGHT = 30;
		int MIN_MARGIN_BOTTOM = 30;
		int maxWidth = MainLayoutPanel.getMainAreaAbsoluteLeft() + MainLayoutPanel.getMainAreaWidth() - MIN_MARGIN_RIGHT - getAbsoluteLeft();
		int maxHeight = MainLayoutPanel.getMainAreaAbsoluteTop() + MainLayoutPanel.getMainAreaHeight() - MIN_MARGIN_BOTTOM - getAbsoluteTop();
		if(DeviceUtils.isTablet()) {
			maxHeight = MainLayoutPanel.getMainAreaAbsoluteTop() + MainLayoutPanel.getMainAreaHeight() - MIN_MARGIN_BOTTOM - getAbsoluteTop() - 350;
		}

		treeMapContainer.getElement().getStyle().setProperty("maxWidth", maxWidth + "px");
		treeMapContainer.getElement().getStyle().setProperty("maxHeight", maxHeight + "px");
	}
	
	public void updateTreeMapSizeOnTimer() {
		if(!resizeTimer.isRunning()) {
			resizeTimer.schedule(200);
		}
	}
	
	private final Timer resizeTimer = new Timer() {
		
		@Override
		public void run() {
			updateTreeMapSize();
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TREEMAP_RESIZE, GoogleAnalytics.ACT_TREEMAP_RESIZE, "");
		}
	};
	
	private void updateTreeMapSize() {
		if(haveSlider) {
			treeMap.setSize(treeMapContainer.getOffsetWidth(), treeMapContainer.getOffsetHeight() - header.getOffsetHeight() - sliderContainer.getOffsetHeight() - 34);
			slider.updateSize(treeMapContainer.getOffsetWidth() - 30);
		}else {
			treeMap.setSize(treeMapContainer.getOffsetWidth(), treeMapContainer.getOffsetHeight() - header.getOffsetHeight() - 34);
		}
	}

	private void setDefaultSize() {
		if(DeviceUtils.isMobile()) {
			treeMapContainer.getElement().getStyle().setWidth(EsaSkyConstants.DEFAULT_TREEMAP_WIDTH_MOBILE, Unit.PX);
			treeMapContainer.getElement().getStyle().setHeight(EsaSkyConstants.DEFAULT_TREEMAP_HEIGHT_MOBILE, Unit.PX);
		} else if(DeviceUtils.isTablet()){
			treeMapContainer.getElement().getStyle().setWidth(EsaSkyConstants.DEFAULT_TREEMAP_WIDTH_TABLET, Unit.PX);
			treeMapContainer.getElement().getStyle().setHeight(EsaSkyConstants.DEFAULT_TREEMAP_HEIGHT_TABLET, Unit.PX);
		} else {
			treeMapContainer.getElement().getStyle().setWidth(EsaSkyConstants.DEFAULT_TREEMAP_WIDTH_DESKTOP , Unit.PX);
			treeMapContainer.getElement().getStyle().setHeight(EsaSkyConstants.DEFAULT_TREEMAP_HEIGHT_DESKTOP, Unit.PX);
		}
	}

	public void updateData(List<CommonTapDescriptor> descriptors, List<Integer> counts) {
		treeMap.updateData(descriptors, counts);
	}
	
	public void addData(List<CommonTapDescriptor> descriptors, List<Integer> counts) {
		treeMap.addData(descriptors, counts);
		if(context == EntityContext.SSO && GUISessionStatus.getTrackedSso() != null) {
			header.setText(TextMgr.getInstance().getText("treeMap_nameOfSelectedLabel").replace("$SSONAME$", GUISessionStatus.getTrackedSso().name)
					.replace("$SSOTYPE$", GUISessionStatus.getTrackedSso().type.getType()));
		}
	}

	public void clearData() {
		treeMap.clearData();
	}
	
	public void registerObserver(TreeMapChanged observer){
		treemapObservers.add(observer);
	}
	
	private void notifyClosed(){
		for(TreeMapChanged observer : treemapObservers){
			observer.onClose();		
		}
	}

	@Override
	public void show() {
		super.show();
		if(firstOpening){
			firstOpening = false;
			treeMap.firstTimeOpen();
			setDefaultSize();
			if(haveSlider) {
				slider.firstOpening();
				updateSliderColor(0, ESASkyColors.maxIndex());
			}
		}

		updateMaxSize();
		updateTreeMapSize();
	}

	@Override
	public void hide() {
		super.hide();
		notifyClosed();
	}

	@Override
	protected Element getMovableElement() {
		return header.getElement();
	}

	@Override
	protected void onResize() {
		updateTreeMapSizeOnTimer();
	}

	@Override
	protected Element getResizeElement() {
		return treeMapContainer.getElement();
	}

	public void setHeaderText(String text) {
		header.setText(text);
	}
	
	public Double[] getSliderValues() {
		return treeMap.getSliderValues();
	}
	
	public void setSliderValues(double low, double high) {
		if(haveSlider) {
			slider.setSliderValue(low, high);
		}
	}
}