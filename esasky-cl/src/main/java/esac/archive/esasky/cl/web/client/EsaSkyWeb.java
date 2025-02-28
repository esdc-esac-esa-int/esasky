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
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
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
						int loadingTime = getTimeSinceStart();
						Log.debug("Load time: " + Integer.toString(loadingTime));
						GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_INITIALISATION, GoogleAnalytics.ACT_INITIALISATION_TIME, Integer.toString(loadingTime));
					}
				});
            }
        });
    }
    
    public static native int getTimeSinceStart()/*-{
    	return new Date().valueOf() - $wnd.esasky_starttime;
    }-*/;

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
