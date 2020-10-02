package esac.archive.esasky.cl.web.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

import esac.archive.esasky.cl.web.client.api.Api;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.common.ESASkyJavaScriptLibrary;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class EsaSkyWeb implements EntryPoint {
	
    private static boolean loadControllersCalled = false;
    private Controller controller;
//    public static final String HIGHCHARTS_EXPORT_URL = Dictionary.getDictionary("serverProperties")
//            .get("highchartsExportURL");

    public static native String getLocaleLanguage() /*-{
		return navigator.language || navigator.userLanguage;
	}-*/;
    
    @Override
    public final void onModuleLoad() {
        // install an uncaught exception handler to produce FATAL log messages
        Log.setUncaughtExceptionHandler();
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                	if (TextMgr.isInitialized()){
                		loadControllers();
                	} else {
                		Log.debug("scheduleDeferred.execute: TextMgr not initialized yet.");
                	}
            }
        });
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
            	
                String langCode = UrlUtils.getUrlLangCode();
                if (langCode.isEmpty()) {
                    langCode = getLocaleLanguage();
                }
                Log.debug("User language " + langCode);
                
            	    //Fetch translations file
                TextMgr.Init(langCode, new TextMgr.InitCallback() {
					@Override
					public void onInitialized(boolean success) {
						if (!success) {
							Log.error("Translation fail! Setting default translations");
						}
						loadControllers();
						Log.debug("Loading ESASkyAPI");
						Api.init(EsaSkyWeb.this);
					}
				});
            }
        });
    }

    public Controller getController(){
    	return this.controller;
    }
    
    public final void loadControllers() {
    	
        //TODO: add locks to this 
        if (loadControllersCalled) {
        	Log.warn("loadControllers called twice");
        	return;
        }
        loadControllersCalled = true;
	
        EventBus eventBus = new SimpleEventBus();
        CommonEventBus.setEventBus(eventBus);
        
        Modules.Init();
        
        controller = new Controller();

        // Set GWT container invisible
        RootLayoutPanel.get().getElement().getStyle().setDisplay(Display.NONE);

        // Load the application
        RootLayoutPanel.get().getElement().setId("RootLayoutPanel");
        
        controller.go(RootPanel.get());

        Element body = RootPanel.getBodyElement();

        Element loading = Document.get().getElementById("esa-logo");

        if (body.isOrHasChild(loading)) {
            loading.removeFromParent();
        }
        
        // Set GWT container visible
        RootLayoutPanel.get().getElement().getStyle().setDisplay(Display.BLOCK);
        
        ESASkyJavaScriptLibrary.initialize();
    }

}
