package esac.archive.esasky.cl.web.client.view.allskypanel;

import java.util.LinkedList;


public class MOCTooltip extends Tooltip {

	LinkedList<MOCTooltipObserver> observers = new LinkedList<>();
	String text = "";
	
    public MOCTooltip(String text, int x, int y) {
    	super(x,y);
    	this.text = text;
    	this.getElement().getStyle().setZIndex(40);
//        EsaSkyStringButton splitButton = new EsaSkyStringButton(TextMgr.getInstance().getText("mocDialog_split"));
//        splitButton.setMediumStyle();
//        splitButton.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				for(MOCTooltipObserver observer : observers) {
//					observer.onSplit();
//				}
//			}
//		});
//        
//        EsaSkyStringButton loadButton = new EsaSkyStringButton(TextMgr.getInstance().getText("mocDialog_loadData"));
//        loadButton.setMediumStyle();
//        loadButton.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				for(MOCTooltipObserver observer : observers) {
//					observer.onLoad();
//				}
//			}
//		});
//        
//        this.add(splitButton);
//        this.add(loadButton);
    }
    
    public void registerObserver(MOCTooltipObserver observer) {
    	observers.add(observer);
    }

	@Override
	protected void fillContent(String cooFrame) {
		typeSpecificContent.setHTML(text);
	}
    
}