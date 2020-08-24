package esac.archive.esasky.cl.web.client.view.allskypanel;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.MOCInfo;
import esac.archive.esasky.cl.web.client.utility.NumberFormatter;
import esac.archive.esasky.cl.web.client.utility.WavelengthUtils;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;


public class MOCTooltip extends Tooltip {

	private LinkedList<MOCTooltipObserver> observers = new LinkedList<>();

	private List<MOCInfo> mocInfos;
	
    public MOCTooltip(List<MOCInfo> mocInfos, int x, int y) {
    	super(x,y);
    	this.mocInfos = mocInfos;
    }
    
    public void registerObserver(MOCTooltipObserver observer) {
    	observers.add(observer);
    }

	@Override
	protected void fillContent(String cooFrame) {
	    FlowPanel container = new FlowPanel();
	    for(final MOCInfo mocInfo : mocInfos) {
    	    HTML wavelengthInfo = new HTML("(" + WavelengthUtils.getShortName(mocInfo.descriptor) + ")");
    	    wavelengthInfo.addStyleName("mocTooltip__wavelength");
    	    container.add(new HTML("<h2>" + mocInfo.descriptor.getGuiShortName() + "</h2>" + wavelengthInfo));
    	    container.add(new HTML("<b>" + TextMgr.getInstance().getText("MocTooltip_count") + ":</b> " + NumberFormatter.formatToNumberWithSpaces(mocInfo.count)));
    	    
    	    if(mocInfo.count < mocInfo.descriptor.getShapeLimit()) {
				EsaSkyStringButton loadButton = new EsaSkyStringButton(TextMgr.getInstance().getText("mocDialog_loadData"));
				loadButton.setMediumStyle();
				loadButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						for(MOCTooltipObserver observer : observers) {
							observer.onLoad(mocInfo);
						}
						hide();
					}
				});
				container.add(loadButton);
		    }
    	    
            if(mocInfos.indexOf(mocInfo) + 1 < mocInfos.size()) {
                FlowPanel separator = new FlowPanel();
                separator.addStyleName("mocTooltip__separator");
                container.add(separator);
            }
	    }
        typeSpecificFlowPanel.add(container);
	}
    
}