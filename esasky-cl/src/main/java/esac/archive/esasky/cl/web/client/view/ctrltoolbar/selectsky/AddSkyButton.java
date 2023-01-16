package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FileUpload;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.event.hips.HipsAddedEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.EsaSkyMenuPopupPanel;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.common.buttons.DisablablePushButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;

public class AddSkyButton extends DisablablePushButton{

	public AddSkyButton() {
		this(Icons.getAddSkyIcon(), Icons.getAddSkyIcon());
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
		EsaSkyMenuPopupPanel<AddSkyMenuItems> menu = new EsaSkyMenuPopupPanel<AddSkyMenuItems>();
        for (final AddSkyMenuItems item : AddSkyMenuItems.values()) {
			// In kiosk mode, we only want to show the "Add new row" option
			String mode = Modules.getMode();
			if (mode != null && Objects.equals(mode.toUpperCase(), EsaSkyWebConstants.MODULE_MODE_KIOSK) && !item.name().equals("ESASKY")) {
				continue;
			}

        	menu.addMenuItem(new MenuItem<>(item, TextMgr.getInstance().getText("addSkyMenuItem_" + item.name()), true));
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
				if(menu.getSelectedObject() == AddSkyMenuItems.ESASKY) {
					CommonEventBus.getEventBus().fireEvent(new HipsAddedEvent(null, HipsWavelength.OTHERS));
				}
				if(menu.getSelectedObject() == AddSkyMenuItems.URL) {
					openUrlPanel(x, y);
				}
				if(menu.getSelectedObject() == AddSkyMenuItems.LOCAL) {
					GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SKIESMENU, GoogleAnalytics.ACT_SKIESMENU_ADDLOCALCLICK, "");
					openLocalHips();
				}
				if(menu.getSelectedObject() == AddSkyMenuItems.BROWSE) {
					browseHips();
				}
			}
		});
        
        menu.setPopupPosition(x, y);
        
        return menu;
	}
	
	private void openUrlPanel(int x, int y) {
		HipsUrlPanel urlPanel = new HipsUrlPanel();
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

	private void browseHips() {
		BrowseHipsPanel browseHipsPanel = new BrowseHipsPanel();
		browseHipsPanel.registerObserver(new BrowseHipsPanelObserver() {
			
			@Override
			public void onHipsAdded(List<String> urls) {
				testParsingHipsList(urls, 0, "");
			}
		});
	}
	
	private void testParsingHipsList(List<String> urls, final int currentIndex, String lastError) {
		if(currentIndex >= urls.size()) {
			String errorMsg = TextMgr.getInstance().getText("addSky_errorParsingProperties");
			errorMsg = errorMsg.replace("$DUE_TO$", lastError);
			DisplayUtils.showMessageDialogBox(errorMsg, TextMgr.getInstance().getText("error").toUpperCase(), UUID.randomUUID().toString(),
					TextMgr.getInstance().getText("error"));
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SKIESMENU, GoogleAnalytics.ACT_SKIESMENU_ADDURL_FAIL, urls.get(0));
			return;
		}
		HipsParser parser = new HipsParser(new HipsParserObserver() {
			
			@Override
			public void onSuccess(HiPS hips) {
				CommonEventBus.getEventBus().fireEvent(new HipsAddedEvent(hips, HipsWavelength.USER));
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SKIESMENU, GoogleAnalytics.ACT_SKIESMENU_ADDURL, hips.getSurveyRootUrl());
			}
			
			@Override
			public void onError(String errorMsg) {
				Log.error(errorMsg);
				testParsingHipsList(urls, currentIndex + 1, errorMsg);
				
			}
		});
		parser.loadProperties(urls.get(currentIndex));
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
			CommonEventBus.getEventBus().fireEvent(new HipsAddedEvent(hips, HipsWavelength.USER));
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SKIESMENU, GoogleAnalytics.ACT_SKIESMENU_ADDLOCAL, hips.getSurveyName());
		} catch (IOException e) {
			DisplayUtils.showMessageDialogBox(e.getMessage(),"Error", UUID.randomUUID().toString(), "");
			Log.error(e.getMessage(), e);
		}
	}
	
	public enum AddSkyMenuItems{
		ESASKY, URL, LOCAL, BROWSE;
	}
	


}
