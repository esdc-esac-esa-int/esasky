package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.Shape;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.ExternalServices;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.common.AutoHidePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;

public abstract class Tooltip extends AutoHidePanel{

    private final int left;
    private final int top;
    protected HTML typeSpecificContent;
    protected Shape source;
    
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

    public Tooltip(int left, int top, Shape source) {
    	this(left, top, source, true);
    }
    
    public Tooltip(int left, int top, Shape source, boolean addLinks) {
    	style = resources.style();
    	style.ensureInjected();
    	
    	this.left = left;
    	this.top = top;
    	this.source = source;
    	initView(addLinks);
    }
    
    
    private void initView(boolean addLinks) {
        FlowPanel tooltip = new FlowPanel();
        tooltip.getElement().setId("sourceTooltipContent");
        
        typeSpecificContent = new HTML();
        typeSpecificContent.removeStyleName("gwt-HTML");
        tooltip.add(typeSpecificContent);

        FlowPanel links = new FlowPanel();
        EsaSkyButton simbadButton = createLinkButton(resources.simbad());
        simbadButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SourceTooltip, GoogleAnalytics.ACT_SourceTooltip_ViewInSimbad, 
						"RA: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top).getRaDeg() 
						+ " Dec: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg());
				Window.open(
						URL.encode(ExternalServices.buildSimbadURLWithRaDec(AladinLiteWrapper
								.getAladinLite().convertMouseXYToRaDecDeg(left, top).getRaDeg(),
								AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top)
								.getDecDeg(), AladinLiteWrapper.getAladinLite().getCooFrame())),
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
						URL.encode(ExternalServices.buildNedURL(AladinLiteWrapper.getAladinLite()
								.convertMouseXYToRaDecDeg(left, top).getRaDeg(), AladinLiteWrapper
								.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg(),
								AladinLiteWrapper.getAladinLite().getCooFrame())), "_blank", "");
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
        				URL.encode(ExternalServices.buildVizierPhotometryURL(AladinLiteWrapper.getAladinLite()
        						.convertMouseXYToRaDecDeg(left, top).getRaDeg(), AladinLiteWrapper
        						.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg(),
        						AladinLiteWrapper.getAladinLite().getCooFrame())), "_blank", "");
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
        				URL.encode(ExternalServices.buildVizierURL(AladinLiteWrapper.getAladinLite()
        						.convertMouseXYToRaDecDeg(left, top).getRaDeg(), AladinLiteWrapper
        						.getAladinLite().convertMouseXYToRaDecDeg(left, top).getDecDeg(),
        						AladinLiteWrapper.getAladinLite().getCooFrame())), "_blank", "");
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
        DisplayUtils.showInsideMainAreaPointingAtPosition(this, left, top);;
    }
}
