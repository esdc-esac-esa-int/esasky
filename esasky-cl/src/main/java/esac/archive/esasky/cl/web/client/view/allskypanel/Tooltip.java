/*
ESASky
Copyright (C) 2025 European Space Agency

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
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.ExternalServices;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.common.AutoHidePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;

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
    
    public Tooltip(int left, int top) {
    	style = resources.style();
    	style.ensureInjected();
    	this.left = left;
    	this.top = top;
    	initView(false);
    	DOM.sinkEvents(getElement(), Event.ONMOUSEWHEEL);
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
        simbadButton.addClickHandler(event -> {
			Coordinate j2000Coordinate = getJ2000Coordinate();
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SOURCE_TOOLTIP, GoogleAnalytics.ACT_SOURCETOOLTIP_VIEWINSIMBAD,
					"RA: " + j2000Coordinate.getRa()
					+ " Dec: " + j2000Coordinate.getDec());
			Window.open(
					ExternalServices.buildSimbadURLWithRaDec(j2000Coordinate.getRa(),
							j2000Coordinate.getDec(), CoordinatesFrame.J2000.getValue()),
					"_blank", "");

		});
        links.add(simbadButton);
        
        EsaSkyButton nedButton = createLinkButton(resources.ned());
        nedButton.addClickHandler(event -> {
			Coordinate j2000Coordinate = getJ2000Coordinate();
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SOURCE_TOOLTIP, GoogleAnalytics.ACT_SOURCETOOLTIP_VIEWINNED,
					"RA: " + j2000Coordinate.getRa()
					+ " Dec: " + j2000Coordinate.getDec());
			Window.open(
					ExternalServices.buildNedURL(j2000Coordinate.getRa(), j2000Coordinate.getDec(),
							CoordinatesFrame.J2000.getValue()), "_blank", "");
		});
        links.add(nedButton);
        
        EsaSkyButton vizierPhotometryButton = createLinkButton(resources.vizierPhotometry());
        vizierPhotometryButton.addClickHandler(event -> {
			Coordinate j2000Coordinate = getJ2000Coordinate();
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SOURCE_TOOLTIP, GoogleAnalytics.ACT_SOURCETOOLTIP_VIEWINVIZIERPHOTOMETRY,
					"RA: " + j2000Coordinate.getRa()
					+ " Dec: " + j2000Coordinate.getDec());
			Window.open(ExternalServices.buildVizierPhotometryURLJ2000(j2000Coordinate.getRa(),
					j2000Coordinate.getDec()), "_blank", "");
		});
        links.add(vizierPhotometryButton);
        
        EsaSkyButton vizierButton = createLinkButton(resources.vizier());
        vizierButton.addClickHandler(event -> {
			Coordinate j2000Coordinate = getJ2000Coordinate();
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SOURCE_TOOLTIP, GoogleAnalytics.ACT_SOURCETOOLTIP_VIEWINVIZIER,
					"RA: " + j2000Coordinate.getRa()
					+ " Dec: " + j2000Coordinate.getDec());
			Window.open(
					ExternalServices.buildVizierURLJ2000(j2000Coordinate.getRa(), j2000Coordinate.getDec()), "_blank", "");
		});
        links.add(vizierButton);
        
        EsaSkyButton wwtButton = createLinkButton(resources.wwt());
        wwtButton.addClickHandler(event -> {
			Coordinate j2000Coordinate = getJ2000Coordinate();
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SOURCE_TOOLTIP, GoogleAnalytics.ACT_SOURCETOOLTIP_VIEWINWWT,
					"RA: " + j2000Coordinate.getRa()
					+ " Dec: " + j2000Coordinate.getDec());
			Window.open(ExternalServices.buildWwtURLJ2000(j2000Coordinate.getRa(), j2000Coordinate.getDec()), "_blank", "");
		});
        if(Modules.getModule(EsaSkyWebConstants.MODULE_WWT_LINK)) {
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
	
	protected Coordinate getJ2000Coordinate() {
		CoordinatesObject coordinateOfPress = AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(left, top);
		return CoordinateUtils.getCoordinateInJ2000(coordinateOfPress.getRaDeg(),
				coordinateOfPress.getDecDeg());
	}

}
