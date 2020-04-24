package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.CoordinatesObject;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.ExternalServices;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.common.AutoHidePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;

public abstract class Tooltip extends AutoHidePanel{

    private int left;
    private int top;
    protected HTML typeSpecificContent;
    protected FlowPanel typeSpecificFlowPanel = new FlowPanel();
    protected AladinShape source;
    
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
    public static interface Resources extends ClientBundle {
        @Source("tooltip.css")
        @CssResource.NotStrict
        CssResource style();
        
		@Source("NED.png")
		ImageResource ned();

		@Source("vizier-photometry-icon.png")
		ImageResource vizierPhotometry();

		@Source("vizier.png")
		ImageResource vizier();

		@Source("simbad.png")
		ImageResource simbad();
		
		@Source("wwt_logo.png")
		ImageResource wwt();
    }
    
    public Tooltip(AladinShape source) {
    	this(source, true);
    }
    
    public Tooltip(AladinShape source, boolean addLinks) {
    	style = resources.style();
    	style.ensureInjected();
        CoordinatesObject co = AladinLiteWrapper.getAladinLite().convertRaDecDegToMouseXY(
                Double.parseDouble(source.getRa()), Double.parseDouble(source.getDec()));
        if(co == null) {
            // Cannot convert to position
            setVisible(false);
            this.left = 0;
            this.top = 0;
        } else {
            this.left = (int) co.getMouseX();
            this.top = (int) co.getMouseY();
        }
    	this.source = source;
    	initView(addLinks);
    	DOM.sinkEvents(getElement(), Event.ONMOUSEWHEEL);
    }
    
    
    private void initView(boolean addLinks) {
        FlowPanel tooltip = new FlowPanel();
        tooltip.getElement().setId("sourceTooltipContent");
        
        typeSpecificContent = new HTML();
        typeSpecificContent.removeStyleName("gwt-HTML");
        tooltip.add(typeSpecificContent);
        tooltip.add(typeSpecificFlowPanel);
        
        FlowPanel links = new FlowPanel();
        EsaSkyButton simbadButton = createLinkButton(resources.simbad());
        simbadButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SourceTooltip, GoogleAnalytics.ACT_SourceTooltip_ViewInSimbad, 
						"RA: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top).getRaDeg() 
						+ " Dec: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg());
				Window.open(
						ExternalServices.buildSimbadURLWithRaDec(AladinLiteWrapper
								.getAladinLite().convertMouseXYToRaDecDeg(left, top).getRaDeg(),
								AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top)
								.getDecDeg(), AladinLiteWrapper.getAladinLite().getCooFrame()),
						"_blank", "");
			}
		});
        links.add(simbadButton);
        
        EsaSkyButton nedButton = createLinkButton(resources.ned());
        nedButton.addClickHandler(new ClickHandler() {
        	
        	@Override
        	public void onClick(ClickEvent event) {
        		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SourceTooltip, GoogleAnalytics.ACT_SourceTooltip_ViewInNed, 
        				"RA: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top).getRaDeg() 
        				+ " Dec: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg());
				Window.open(
						ExternalServices.buildNedURL(AladinLiteWrapper.getAladinLite()
								.convertMouseXYToRaDecDeg(left, top).getRaDeg(), AladinLiteWrapper
								.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg(),
								AladinLiteWrapper.getAladinLite().getCooFrame()), "_blank", "");
        	}
        });
        links.add(nedButton);
        
        EsaSkyButton vizierPhotometryButton = createLinkButton(resources.vizierPhotometry());
        vizierPhotometryButton.addClickHandler(new ClickHandler() {
        	
        	@Override
        	public void onClick(ClickEvent event) {
        		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SourceTooltip, GoogleAnalytics.ACT_SourceTooltip_ViewInVizierPhotometry, 
        				"RA: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top).getRaDeg() 
        				+ " Dec: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg());
        		Window.open(
        				ExternalServices.buildVizierPhotometryURL(AladinLiteWrapper.getAladinLite()
        						.convertMouseXYToRaDecDeg(left, top).getRaDeg(), AladinLiteWrapper
        						.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg(),
        						AladinLiteWrapper.getAladinLite().getCooFrame()), "_blank", "");
        	}
        });
        links.add(vizierPhotometryButton);
        
        EsaSkyButton vizierButton = createLinkButton(resources.vizier());
        vizierButton.addClickHandler(new ClickHandler() {
        	
        	@Override
        	public void onClick(ClickEvent event) {
        		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SourceTooltip, GoogleAnalytics.ACT_SourceTooltip_ViewInVizier, 
        				"RA: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top).getRaDeg() 
        				+ " Dec: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg());
        		Window.open(
        				ExternalServices.buildVizierURL(AladinLiteWrapper.getAladinLite()
        						.convertMouseXYToRaDecDeg(left, top).getRaDeg(), AladinLiteWrapper
        						.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg(),
        						AladinLiteWrapper.getAladinLite().getCooFrame()), "_blank", "");
        	}
        });
        links.add(vizierButton);
        
        EsaSkyButton wwtButton = createLinkButton(resources.wwt());
        wwtButton.addClickHandler(new ClickHandler() {
        	
        	@Override
        	public void onClick(ClickEvent event) {
        		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SourceTooltip, GoogleAnalytics.ACT_SourceTooltip_ViewInWWT, 
        				"RA: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top).getRaDeg() 
        				+ " Dec: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg());
        		Window.open(ExternalServices.buildWwtURL(AladinLiteWrapper.getAladinLite()
        						.convertMouseXYToRaDecDeg(left, top).getRaDeg(), AladinLiteWrapper
        						.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg(),
        						AladinLiteWrapper.getAladinLite().getCooFrame()), "_blank", "");
        	}
        });
        if(Modules.wwtLink) {
        	links.add(wwtButton);
        }
        
        if(addLinks) {
        	tooltip.add(links);
        }
        
        this.getElement().setId("sourceToolTip");
        this.removeStyleName("gwt-DialogBox");
        super.hide();
        this.add(tooltip);
    }

	private EsaSkyButton createLinkButton(ImageResource image) {
		EsaSkyButton simbadButton = new EsaSkyButton(image);
        simbadButton.addStyleName("tooltipButton");
        simbadButton.keepAspectRatioWidth();
        simbadButton.setVeryBigStyle();
		return simbadButton;
	}

    protected abstract void fillContent(String cooFrame);

    public void show(String cooFrame) {
        fillContent(cooFrame);
        DisplayUtils.showInsideMainAreaPointingAtPosition(this, left, top);
    }
    
	@Override
	public void onBrowserEvent(Event event) {
		if(event.getTypeInt() == Event.ONMOUSEWHEEL) {
			event.stopPropagation();
			AladinLiteWrapper.getAladinLite().triggerMouseWheelEvent(event);
			
		}else {
			super.onBrowserEvent(event);
		}
	}
}
