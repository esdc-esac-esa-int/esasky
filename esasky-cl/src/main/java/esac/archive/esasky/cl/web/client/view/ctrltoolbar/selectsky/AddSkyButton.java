package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import java.io.IOException;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FileUpload;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.HipsParser;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.EsaSkyMenuPopupPanel;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.common.buttons.DisablablePushButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.ifcs.model.client.HiPS;

public class AddSkyButton extends DisablablePushButton{

	AddSkyObserver addSkyObserver;
	private final int POPUP_SIZE = 90;
	
	public AddSkyButton() {
		this(Icons.getAddSkyIcon(), Icons.getAddSkyIcon());
	}
	
	public void setObserver(AddSkyObserver addSkyObserver) {
		this.addSkyObserver = addSkyObserver;
	}
	
	private AddSkyButton(ImageResource enabledImage, ImageResource disabledImage) {
		super(enabledImage, disabledImage);
	
		this.setRoundStyle();
		this.addStyleName("addSkyBtn");
		this.setMediumStyle();
		this.disableButton();
		this.setTitle(TextMgr.getInstance().getText("selectSkyPanel_addSky"));
		this.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				EsaSkyMenuPopupPanel<AddSkyMenuItems> menu = createPopupMenu();
		        menu.show();
			}
		});
	}
	
	private EsaSkyMenuPopupPanel<AddSkyMenuItems> createPopupMenu() {
		EsaSkyMenuPopupPanel<AddSkyMenuItems> menu = new EsaSkyMenuPopupPanel<AddSkyMenuItems>(POPUP_SIZE);
        for (final AddSkyMenuItems item : AddSkyMenuItems.values()) {
        	menu.addMenuItem(new MenuItem<AddSkyMenuItems>(item, TextMgr.getInstance().getText("addSkyMenuItem_" + item.name()), true));
        }
        int defaultLeft = this.getAbsoluteLeft() + this.getOffsetWidth() / 2;
        if (defaultLeft + menu.getOffsetWidth() > MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft()) {
        	defaultLeft -= menu.getOffsetWidth(); 
        }
        
        final int x = defaultLeft;
        final int y = this.getAbsoluteTop() + this.getOffsetHeight() / 2;
        menu.registerObserver(new MenuObserver() {
			
			@Override
			public void onSelectedChange() {
				if(menu.getSelectedObject() == AddSkyMenuItems.ESASky) {
					addSkyObserver.onSkyAdded();
				}
				if(menu.getSelectedObject() == AddSkyMenuItems.URL) {
					openUrlPanel(x, y);
				}
				if(menu.getSelectedObject() == AddSkyMenuItems.LOCAL) {
					openLocalHips();
				}
			}
		});
        
        menu.setPopupPosition(x, y);
        
        return menu;
	}
	
	private void openUrlPanel(int x, int y) {
		HipsUrlPanel urlPanel = new HipsUrlPanel(addSkyObserver);
		urlPanel.setPopupPosition(x, y);
		urlPanel.show();
		urlPanel.focus();
	}

	private void openLocalHips() {
		FileUpload upload = new FileUpload();
		upload.getElement().setPropertyBoolean("webkitdirectory", true);
		upload.getElement().setPropertyBoolean("multiple", true);
		addFileUploadHandler(this, (JavaScriptObject) upload.getElement());
		upload.click();
	}
	
	public native void addFileUploadHandler(AddSkyButton instance, JavaScriptObject element)/*-{
		element.addEventListener("change", function(event) {
  			var files = event.target.files;
  			for(var i = 0; i < files.length; i++){
  				if(files[i].webkitRelativePath.endsWith("properties")){
  					files[i].text().then(function(text){ 
							instance.@esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.AddSkyButton::onFilesUploaded(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(files, text);
  					});
  					break;
  				}
  			}
		}, false);
	}-*/;
	
	public void onFilesUploaded(JavaScriptObject files, String propertiesText) {
		HipsParser parser = new HipsParser();
		try {
			HiPS hips = parser.parseHipsProperties(propertiesText, "");
			hips.setFiles(files);
			hips.setLocal(true);
			addSkyObserver.onSkyAddedWithUrl(hips);
		} catch (IOException e) {
			Log.error(e.getMessage(), e);
		}
	}
	
	public enum AddSkyMenuItems{
		ESASky, URL, LOCAL;
	}
	


}
