package esac.archive.esasky.cl.web.client.view.allskypanel;

import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;

public class MOCTooltip extends Tooltip {

	LinkedList<MOCTooltipObserver> observers = new LinkedList<>();
	
    public MOCTooltip() {
        super(null, false);
        
        EsaSkyStringButton splitButton = new EsaSkyStringButton(TextMgr.getInstance().getText("mocDialog_split"));
        splitButton.setMediumStyle();
        splitButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(MOCTooltipObserver observer : observers) {
					observer.onSplit();
				}
			}
		});
        
        EsaSkyStringButton loadButton = new EsaSkyStringButton(TextMgr.getInstance().getText("mocDialog_loadData"));
        loadButton.setMediumStyle();
        loadButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(MOCTooltipObserver observer : observers) {
					observer.onLoad();
				}
			}
		});
        
        this.add(splitButton);
        this.add(loadButton);
    }
    
    public void registerObserver(MOCTooltipObserver observer) {
    	observers.add(observer);
    }
    
    protected void fillContent(String text) {

        typeSpecificContent.setHTML(text);
    }
}