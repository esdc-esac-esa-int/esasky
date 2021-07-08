package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.EsaSkyMenuPopupPanel;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

public class ChangePaletteBtn extends Composite{

    private Resources resources;
    private Style style;  
	private final int POPUP_SIZE = 90;
    private EsaSkyMenuPopupPanel<ColorPalette> changePaletteMenu;
    private List<PaletteObserver> observers = new LinkedList<PaletteObserver>();
    
	public static interface Resources extends ClientBundle {
        
        @Source("changePalette.css")
        @CssResource.NotStrict
        Style style();
    }
	    
    public static interface Style extends CssResource {
        String changePaletteBtn();
    }
	        
	public ChangePaletteBtn() {
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        initWidget(createImageBtn());
    }
	
	public ColorPalette getSelectedColorPalette(){
		return changePaletteMenu.getSelectedObject();
	}
	
    private Image createImageBtn() {
        	final Image changePaletteBtn = new Image(Icons.getChangePaletteIcon());
        	changePaletteBtn.setStyleName(style.changePaletteBtn());
        changePaletteBtn.setTitle(TextMgr.getInstance().getText("changePaletteBtn_changePaletteTooltip"));
        initChangePaletteMenu();
        changePaletteBtn.addMouseDownHandler((MouseDownEvent event) -> event.preventDefault());
        changePaletteBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                changePaletteMenu.show();
                int defaultLeft = changePaletteBtn.getAbsoluteLeft() + changePaletteBtn.getOffsetWidth() / 2;
                if (defaultLeft + changePaletteMenu.getOffsetWidth() > MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft()) {
                	defaultLeft -= changePaletteMenu.getOffsetWidth(); 
                }
                changePaletteMenu.setPopupPosition(defaultLeft, 
                		changePaletteBtn.getAbsoluteTop() + changePaletteBtn.getOffsetHeight() / 2);
            }
        });
        return changePaletteBtn;
    }
	
    private void initChangePaletteMenu() {
        changePaletteMenu = new EsaSkyMenuPopupPanel<ColorPalette>(POPUP_SIZE);
        for (final ColorPalette colorPalette : ColorPalette.values()) {
            changePaletteMenu.addMenuItem(new MenuItem<ColorPalette>(colorPalette, TextMgr.getInstance().getText("colorPalette_" + colorPalette.name()), true));
        }
        changePaletteMenu.selectObject(ColorPalette.NATIVE);
        changePaletteMenu.registerObserver(new MenuObserver() {
			
			@Override
			public void onSelectedChange() {
                notifyObservers();
			}
		});
    }
    
    public void setDefaultColorPallette(ColorPalette colorPalette){
    	    changePaletteMenu.selectObject(colorPalette);
    }

    public void registerObserver(PaletteObserver observer){
    	    observers.add(observer);
    }
    
    private void notifyObservers(){
        	for(PaletteObserver observer: observers){
        		observer.onPaletteChange();
        	}
    }
}