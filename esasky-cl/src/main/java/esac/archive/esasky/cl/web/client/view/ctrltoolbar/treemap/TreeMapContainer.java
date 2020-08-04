package esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.ESASkyMultiRangeSlider;
import esac.archive.esasky.cl.web.client.view.common.EsaSkyMultiRangeSliderObserver;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;

public class TreeMapContainer extends DialogBox {

	private final CssResource style;
	private final Resources resources;
	private boolean firstOpeing = true;
	private boolean isOpen = false;
	private final EntityContext context;
	
	private final int DEFAULT_TREEMAP_HEIGHT_DESKTOP = 400;
	private final int DEFAULT_TREEMAP_WIDTH_DESKTOP = 500;
	private final int DEFAULT_TREEMAP_HEIGHT_TABLET = 800;
	private final int DEFAULT_TREEMAP_WIDTH_TABLET = 800;
	private final int DEFAULT_TREEMAP_HEIGHT_MOBILE = 1000;
	private final int DEFAULT_TREEMAP_WIDTH_MOBILE = 1000;

	private TreeMap treeMap;
	private FlowPanel allContent = new FlowPanel();
	private final PopupHeader header;
	private ESASkyMultiRangeSlider slider;
	private Element sliderUiHeader = null;
	private FlowPanel sliderContainer;
	
	private List<TreeMapChanged> observers = new LinkedList<TreeMapChanged>();
	
	
	public interface Resources extends ClientBundle {
		@Source("treeMapContainer.css")
		@CssResource.NotStrict
		CssResource style();
		
		@Source("logo_IMCCE_web_ssodnet.svg")
		@MimeType("image/svg+xml")
		DataResource ssoDNetLogo();
	}
	
	public TreeMapContainer(EntityContext context){
		super(false, false);
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
        
		this.removeStyleName("gwt-DialogBox");
		this.addStyleName("treeMapContainer");
		if(!DeviceUtils.isMobileOrTablet()) {
			this.addStyleName("treeMapContainerDesktop");
		}
		getElement().setId("treeMapContainer_" + context);

		header = new PopupHeader(this, "", "");
		Image ssoDnetLogo = new Image(resources.ssoDNetLogo().getSafeUri());
		ssoDnetLogo.addStyleName("treeMap__ssoLogo");
		header.add(ssoDnetLogo);
		ssoDnetLogo.setVisible(context == EntityContext.SSO);

		allContent.add(header);
		allContent.add(treeMap);
		
		sliderContainer = initSliderContainer();
		allContent.add(sliderContainer);
		add(allContent);
		
		header.setText(TextMgr.getInstance().getText("treeMap_" + context));
		header.setHelpText(TextMgr.getInstance().getText("treeMapContainer_help_" + context));
		
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				updateMaxSize();
			}
		});
		
		//Due to rendering problems in highcharts, this element is always in the DOM. Open() and Close() is used to show/hide
		show();
		close();
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
		slider.registerValueChangeObserver(new EsaSkyMultiRangeSliderObserver() {

			@Override
			public void onValueChange(double low, double high) {
				updateSliderColor(low, high);
			}
		});
		
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
					styleString += " " + Double.toString(botPosition*100) + "%";
				}else if(nShown == Math.ceil(high) - Math.floor(low) - 1) {
					styleString += " " + Double.toString(100 - topPosition * 100) + "%";
				}
				nShown++;
				styleString += ",";
			}
			styleString = styleString.substring(0,styleString.length() - 1);
			
			styleString += "); width:100%";
			
			sliderUiHeader.setAttribute("style", styleString);
		}
	}
	
	public void onLayoutChange() {
		updateMaxSize();
	}
	
	private void updateMaxSize() {
		int MIN_MARGIN_RIGHT = 30;
		int MIN_MARGIN_BOTTOM = 30;
		int maxWidth = MainLayoutPanel.getMainAreaAbsoluteLeft() + MainLayoutPanel.getMainAreaWidth() - MIN_MARGIN_RIGHT - getAbsoluteLeft();
		int maxHeight = MainLayoutPanel.getMainAreaAbsoluteTop() + MainLayoutPanel.getMainAreaHeight() - MIN_MARGIN_BOTTOM - getAbsoluteTop();
		if(DeviceUtils.isTablet()) {
			maxHeight = MainLayoutPanel.getMainAreaAbsoluteTop() + MainLayoutPanel.getMainAreaHeight() - MIN_MARGIN_BOTTOM - getAbsoluteTop() - 350;
		}
		
		getElement().getStyle().setProperty("maxWidth", maxWidth + "px");
		getElement().getStyle().setProperty("maxHeight", maxHeight + "px");
	}
	
	private native void addResizeHandler(String context) /*-{
		var treeMapContainer = this;
		new $wnd.ResizeSensor($doc.getElementById('treeMapContainer_' + context), function() {
			treeMapContainer.@esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.TreeMapContainer::updateTreeMapSizeOnTimer()();
		});
	}-*/; 
	
	private native void addResizeCursorToBottomRightCorner(String context) /*-{
    	$wnd.$('#treeMapContainer_' + context).on('mousemove', function(e) {
			var y = $wnd.$('#treeMapContainer_' + context).offset().top + $wnd.$('#treeMapContainer_' + context).outerHeight() - 15,	//	top border of bottom-right-corner-box area
			x = $wnd.$('#treeMapContainer_' + context).offset().left + $wnd.$('#treeMapContainer_' + context).outerWidth() - 15;	//	left border of bottom-right-corner-box area
			$wnd.$('#treeMapContainer_' + context).css({
				cursor: e.pageY > y && e.pageY < y + 13 && e.pageX > x && e.pageX < x + 13 ? 'nw-resize' : ''
			});
    	})
	}-*/; 
	
	public void updateTreeMapSizeOnTimer() {
		if(!resizeTimer.isRunning()) {
			resizeTimer.schedule(200);
		}
	}
	
	private Timer resizeTimer = new Timer() {
		
		@Override
		public void run() {
			updateTreeMapSize();
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TreeMap_Resize, GoogleAnalytics.ACT_TreeMap_Resize, "");
		}
	};
	
	private void updateTreeMapSize() {
		treeMap.setSize(TreeMapContainer.this.getOffsetWidth() - 22, TreeMapContainer.this.getOffsetHeight() - header.getOffsetHeight() - sliderContainer.getOffsetHeight() - 34);
		slider.updateSize(TreeMapContainer.this.getOffsetWidth() - 30);
	}

	
    public void toggleTreeMap(){
		if(!isOpen){
			open();
    	} else {
    		close();
    	}
    }
	
	public void close(){
		addStyleName("displayNone");
		isOpen = false;
		notifyClosed();
	}
	
	public void open(){
		removeStyleName("displayNone");
		if(firstOpeing){
			firstOpeing = false;
			treeMap.firstTimeOpen();
			if(DeviceUtils.isMobile()) {
				getElement().getStyle().setWidth(DEFAULT_TREEMAP_WIDTH_MOBILE, Unit.PX);
				getElement().getStyle().setHeight(DEFAULT_TREEMAP_HEIGHT_MOBILE, Unit.PX);
			} else if(DeviceUtils.isTablet()){
				getElement().getStyle().setWidth(DEFAULT_TREEMAP_WIDTH_TABLET, Unit.PX);
				getElement().getStyle().setHeight(DEFAULT_TREEMAP_HEIGHT_TABLET, Unit.PX);
			} else {
				getElement().getStyle().setWidth(DEFAULT_TREEMAP_WIDTH_DESKTOP , Unit.PX);
				getElement().getStyle().setHeight(DEFAULT_TREEMAP_HEIGHT_DESKTOP, Unit.PX);
			}
			addResizeHandler(context.toString());
			addResizeCursorToBottomRightCorner(context.toString());
			slider.firstOpening();
			getSliderUiHeader();
			updateSliderColor(0, ESASkyColors.maxIndex());
			updateTreeMapSize();
		}
		isOpen = true;
		updateMaxSize();
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
	
	public void addData(List<IDescriptor> descriptors, List<Integer> counts) {
		treeMap.addData(descriptors, counts);
		if(context == EntityContext.SSO) {
			header.setText(TextMgr.getInstance().getText("treeMap_nameOfSelectedLabel").replace("$SSONAME$", GUISessionStatus.getTrackedSso().name)
					.replace("$SSOTYPE$", GUISessionStatus.getTrackedSso().type.getType()));
		}
	}
	
	public boolean isOpen() {
		return isOpen;
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
	public void hide() {
		close();
	}
	
	public void setHeaderText(String text) {
		header.setText(text);
	}
}