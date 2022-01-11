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
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.ESASkyMultiRangeSlider;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;

import java.util.LinkedList;
import java.util.List;


public class TreeMapContainer extends MovableResizablePanel<TreeMapContainer>{

	private final CssResource style;
	private final Resources resources;
	private boolean firstOpeing = true;
	private final EntityContext context;
	
	private final int DEFAULT_TREEMAP_HEIGHT_DESKTOP = 400;
	private final int DEFAULT_TREEMAP_WIDTH_DESKTOP = 500;
	private final int DEFAULT_TREEMAP_HEIGHT_TABLET = 800;
	private final int DEFAULT_TREEMAP_WIDTH_TABLET = 800;
	private final int DEFAULT_TREEMAP_HEIGHT_MOBILE = 1000;
	private final int DEFAULT_TREEMAP_WIDTH_MOBILE = 1000;

	private TreeMap treeMap;
	private FlowPanel treeMapContainer = new FlowPanel();
	private final PopupHeader<TreeMapContainer> header;
	private ESASkyMultiRangeSlider slider;
	private Element sliderUiHeader = null;
	private FlowPanel sliderContainer;
	boolean haveSlider;
	
	private final List<TreeMapChanged> observers = new LinkedList<>();
	
	
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
		
		if(context.equals(EntityContext.EXT_TAP)) {
			treeMap = new ExtTapTreeMap(context);
			((ExtTapTreeMap) treeMap).registerHeaderObserver(new TreeMapHeaderChanged() {
				
				@Override
				public void onHeaderChanged(String text) {
					header.setText(TextMgr.getInstance().getText("treeMap_" + TreeMapContainer.this.context) + text);
				}
			});
		}else {
			treeMap = new TreeMap(context);
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
		header.setHelpText(TextMgr.getInstance().getText("treeMapContainer_help_" + context));
		
		MainLayoutPanel.addMainAreaResizeHandler(event -> updateMaxSize());

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
		if(sliderUiHeader != null) {
			
			double botPosition = (1 -( low - Math.floor(low)) ) / (high - low);
			double topPosition = (1 - (Math.ceil(high) - high)) / ( high - low );
			
			String styleString =  "background:linear-gradient(to right,";
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
			
			styleString += "); width:100%";
			
			sliderUiHeader.setAttribute("style", styleString);
		}
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
			treeMapContainer.getElement().getStyle().setWidth(DEFAULT_TREEMAP_WIDTH_MOBILE, Unit.PX);
			treeMapContainer.getElement().getStyle().setHeight(DEFAULT_TREEMAP_HEIGHT_MOBILE, Unit.PX);
		} else if(DeviceUtils.isTablet()){
			treeMapContainer.getElement().getStyle().setWidth(DEFAULT_TREEMAP_WIDTH_TABLET, Unit.PX);
			treeMapContainer.getElement().getStyle().setHeight(DEFAULT_TREEMAP_HEIGHT_TABLET, Unit.PX);
		} else {
			treeMapContainer.getElement().getStyle().setWidth(DEFAULT_TREEMAP_WIDTH_DESKTOP , Unit.PX);
			treeMapContainer.getElement().getStyle().setHeight(DEFAULT_TREEMAP_HEIGHT_DESKTOP, Unit.PX);
		}
	}

	public void getSliderUiHeader() {
		Element el = slider.getElement().getFirstChildElement();
		int i = 0;
		while(!el.hasClassName("ui-widget-header")) {
			el = el.getFirstChildElement();
			i++;
			if(i > 5) {
				break;
			}
		}
		
		if(i < 6) {
			sliderUiHeader = el;
		}
	}
	
	public void updateData(List<IDescriptor> descriptors, List<Integer> counts) {
		treeMap.updateData(descriptors, counts);
	}
	
	public void addData(List<IDescriptor> descriptors, List<Integer> counts) {
		treeMap.addData(descriptors, counts);
		if(context == EntityContext.SSO) {
			header.setText(TextMgr.getInstance().getText("treeMap_nameOfSelectedLabel").replace("$SSONAME$", GUISessionStatus.getTrackedSso().name)
					.replace("$SSOTYPE$", GUISessionStatus.getTrackedSso().type.getType()));
		}
	}
	
	public void registerObserver(TreeMapChanged observer){
		observers.add(observer);
	}
	
	private void notifyClosed(){
		for(TreeMapChanged observer : observers){
			observer.onClose();		
		}
	}

	@Override
	public void show() {
		super.show();
		if(firstOpeing){
			firstOpeing = false;
			treeMap.firstTimeOpen();
			setDefaultSize();
			if(haveSlider) {
				slider.firstOpening();
				getSliderUiHeader();
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
}