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

package esac.archive.esasky.cl.web.client.event;

import java.util.Iterator;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.gsamp.client.GSampManager;
import esac.archive.esasky.cl.gsamp.client.model.SampClient;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.SampConstants.SampAction;

/**
 * @author Maria Henar Sarmiento Carrion Copyright (c) 2015 - European Space Agency
 */
public class ESASkySampEventHandlerImpl implements ESASkySampEventHandler {

    /**
     * Samp manager.
     */
    private GSampManager gsampManager;
    /**
     * isFirstTime.
     */
    private boolean isFirstTime = true;

    /**
     * Default constructor.
     */
    public ESASkySampEventHandlerImpl() {
        gsampManager = new GSampManager(new Label());
    }

    /**
     * Method invoked when the event is fired.
     *
     * @param event
     */
    @Override
    public final void onEvent(final ESASkySampEvent event) {
        // Preventing double clicks
        if (!isFirstTime) {
            Log.info("[ESASkySampEventHandlerImpl/onEvent()] Samp is currently attending other click event.");
            return;
        }

        // Unregister action
        if (SampAction.UNREGISTER.equals(event.getAction())) {
            if (gsampManager.isApplicationRegistered()) {
                gsampManager.unregister();
            }
            isFirstTime = true;
            return;
        }

        // Registering ESASky in case it is not registered yet
        int count = 0;
        if (!gsampManager.isApplicationRegistered()) {
            Log.debug("[ESASkySampEventHandlerImpl] ESASky is not registered yet into the hub !!!");
            gsampManager.launchRpcPingOnHub();
            Log.debug("[ESASkySampEventHandlerImpl] Rpc Ping on hub launched !!!");
            gsampManager.register();
            Log.debug("[ESASkySampEventHandlerImpl] ESASky registered !!!");
        }

        // Send data in another thread using Timer object.
        CheckHubStatusTimer checkHubStatusTimer = new CheckHubStatusTimer(event, count);
        checkHubStatusTimer.schedule(1000);
    }

    /**
     * Send the URL to the awake applications.
     *
     * @param event Input SampEvent
     * @throws Exception basic Java exception
     */
    public final void processEvent(final ESASkySampEvent event) throws Exception {

        Log.info("[ESASkySampEventHandlerImpl.processEvent] " + event.getAction() + " Processing URLs  ");
        JsArray<SampClient> list;
        Object o = gsampManager.getRegisteredClients();
        if (o != null) {
            list = gsampManager.getRegisteredClients();
            for (int i = 0; i < list.length(); i++) {
                Log.debug("list" + list.get(i).getId() + "-" + list.get(i).getName());
            }
        } else {
            Log.debug("List is null");
        }


        switch (event.getAction()) {
            case SEND_REGION_TABLE_TO_SAMP_APP:
                String regionFileUrl = null;
                for (String key : event.getSampUrlsPerMissionMap().keySet()) {
                    if (key.contains("region")) {
                        regionFileUrl = event.getSampUrlsPerMissionMap().get(key);
                    }
                }

                sendSampFits(event, event.getSampUrlsPerMissionMap().keySet().iterator(), regionFileUrl);
                break;

            case SEND_PRODUCT_TO_SAMP_APP:
                sendSampFits(event, event.getSampUrlsPerMissionMap().keySet().iterator(), null);
                break;

            case SEND_FITS_TABLE_TO_SAMP_APP:
                for (String tableName : event.getSampUrlsPerMissionMap().keySet()) {
                    // Get URL
                    String url = event.getSampUrlsPerMissionMap().get(tableName);
                    // Prepare sending message
                    String sendingMessage = EsaSkyConstants.APP_NAME + "-" + tableName;
                    gsampManager.loadFitsTable(url, tableName, sendingMessage);
                }
                break;

            case SEND_VOTABLE:

                for (String tableName : event.getSampUrlsPerMissionMap().keySet()) {

                    // Get URL
                    String voTable = event.getSampUrlsPerMissionMap().get(tableName);
                    final String tableId = tableName;

                    final String sendingMessage = EsaSkyConstants.APP_NAME + "-" + tableName;

                    final String votableBufferUrl = Window.Location.getProtocol() + "//"
                            + Window.Location.getHost() + EsaSkyWebConstants.BACKEND_CONTEXT
                            + EsaSkyConstants.HttpServlet.VOTABLE_BUFFER_SERVLET;

                    Log.debug("[ESASkySampEventHandlerImpl/processEvent()] Votable buffer servlet"
                            + votableBufferUrl);

                    RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, votableBufferUrl);

                    try {
                        final String data = "vot=" + voTable;
                        requestBuilder.sendRequest(data, new RequestCallback() {

                            @Override
                            public void onError(final com.google.gwt.http.client.Request request,
                                                final Throwable exception) {
                                sendGoogleAnalyticsErrorEvent("Failed to send data to " + data + ", Exception: " + exception.toString());
                                Log.debug(
                                        "[ESASkySampEventHandlerImpl/processEvent()] Failed file reading",
                                        exception);
                            }

                            @Override
                            public void onResponseReceived(
                                    final com.google.gwt.http.client.Request request,
                                    final Response response) {
                                String id = "";
                                id = response.getText();
                                String resourceUrl = votableBufferUrl + "id=" + id;
                                Log.debug(resourceUrl);
                                try {
                                    gsampManager.loadVoTable(resourceUrl, tableId, sendingMessage);
                                } catch (Exception e) {
                                    sendGoogleAnalyticsErrorEvent("Failed to load VO Table: " + votableBufferUrl + ", Exception: " + e.toString());
                                    Log.debug(
                                            "[ESASkySampEventHandlerImpl/processEvent()] Exception in ESASkySampEventHandlerImpl.processEvent",
                                            e);

                                    CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(event.getAction().toString()));
                                    throw new IllegalStateException(
                                            "[ESASkySampEventHandlerImpl.processEvent] Unexpected SampAction: SEND_VO_TABLE");
                                }
                            }

                        });
                    } catch (RequestException e) {
                        Log.debug(
                                "[ESASkySampEventHandlerImpl/processEvent()] Failed file reading",
                                e);
                    }
                } // end 1st for
                break;

            default:
                // Enable again the button
                isFirstTime = true;

                sendGoogleAnalyticsErrorEvent("Unknown action type");
                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(event.getAction().toString()));
                throw new IllegalStateException(
                        "[ESASkySampEventHandlerImpl.processEvent] Unexpected SampAction "
                                + event.getAction());

        }

        // Restart boolean value.
        isFirstTime = true;
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(event.getAction().toString()));
    }

    private void sendGoogleAnalyticsErrorEvent(String details) {
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SAMP, GoogleAnalytics.ACT_SAMP_ERROR, details);
    }

    /**
     * Timer responsible for attempt the SampAction after ESASky has been registered.
     *
     * @author mhsarmiento
     */
    private class CheckHubStatusTimer extends Timer {

        /**
         * Max number of registering tries.
         */
        private final Integer MAX_TRIES_REGISTER = 20;
        /**
         * Max number of tries checking clients.
         */
        private final Integer MIN_TRIES_CHECKING_CLIENTS = 1;

        /**
         * local count.
         */
        private int count;
        /**
         * local sampEvent.
         */
        private ESASkySampEvent event;

        /**
         * Default constructor.
         *
         * @param inputEvent Input Samp Event
         * @param inputCount Input integer.
         */
        public CheckHubStatusTimer(final ESASkySampEvent inputEvent, final int inputCount) {
            Log.debug("[ESASkySampEventHandlerImpl.CheckHubStatusTimer] Into Check hub Status Timer!!!");
            this.count = inputCount;
            this.event = inputEvent;
            this.count++;
        }

        /**
         * Where job is done.
         */
        @Override
        public void run() {

            if (this.count > MAX_TRIES_REGISTER) {
                Log.debug("[ESASkySampEventHandlerImpl.CheckHubStatusTimer] Maximum number of attemps exceeded !!!");
                CommonEventBus.getEventBus().fireEvent(
                        new ProgressIndicatorPopEvent(event.getAction().toString()));
                return;
            }

            try {
                if (gsampManager.isApplicationRegistered()) {
                    isFirstTime = false;
                    processEvent(event);
                } else {
                    if (count > MIN_TRIES_CHECKING_CLIENTS && notAvailableClients()) {

                        // Create an ancillary widget to show the links to Aladin/Topcat/DS9
                        DisplayUtils.showMessageDialogBox(DisplayUtils.createSampAppsWidgets(),
                                TextMgr.getInstance().getText("sampConstants_help"),
                                event.getAction().toString(), "FailedToFindSAMPHubDialog");

                        // Enable again the button
                        isFirstTime = true;
                        return;
                    }

                    CheckHubStatusTimer checkHubStatusTimer = new CheckHubStatusTimer(event,
                            this.count);
                    checkHubStatusTimer.schedule(1000);

                }
            } catch (Exception e) {
                CheckHubStatusTimer checkHubStatusTimer = new CheckHubStatusTimer(event, this.count);
                checkHubStatusTimer.schedule(1000);
            }
        }

        /**
         * Returns true in case there are not any client available to send the data via SAMP: status
         * == false and not registered.
         *
         * @return boolean value.
         */
        private Boolean notAvailableClients() {
            Boolean hubStatus = null;
            try {
                hubStatus = gsampManager.getHubStatus();
            } catch (Exception e) {
                Log.warn("[ESASkySampEventHandler.notAvailableClients] STATUS exception" + e);
                hubStatus = null;
            }
            return Boolean.FALSE.equals(hubStatus) && !gsampManager.isApplicationRegistered();
        }
    }


    private void sendSampFits(final ESASkySampEvent event,
                              final Iterator<String> item, final String regionMap) throws Exception {

        if (!item.hasNext()) {
            if (event.getAction().equals(SampAction.SEND_REGION_TABLE_TO_SAMP_APP)) {
                //sendSampDS9Region(regionMap);
            }
            return;
        }

        // Get URL
        String tableName = item.next();
        if (!tableName.contains("_region")) {
            String url = event.getSampUrlsPerMissionMap().get(tableName);
            // Prepare sending message
            String sendingMessage = EsaSkyConstants.APP_NAME + "-" + tableName;
            gsampManager.loadFitsImage(url, tableName, sendingMessage);

        }

        // wait and continue loop
        Timer timer = new Timer() {
            public void run() {
                try {
                    sendSampFits(event, item, regionMap);
                } catch (Exception e) {
                    Log.debug("Send of samp FITS failed. " + e);
                }
            }
        };
        timer.schedule(2000);
    }

//	private void sendSampDS9Region(String regionMap) throws Exception{
//		// Get URL
//		if(regionMap == null){
//			return;
//		}
//		Log.debug("[sendSampDS9Region]Region Params:" + regionMap);
//		String completeUrl = EsaSkyWebConstants.TAP_CONTEXT + "/region-file?" + URL.encode(regionMap);
//		Log.debug("[sendSampDS9Region]CompleteURL:"	+ completeUrl);
//		gsampManager.setDS9Cmd("regions sky fk5", "");
//		gsampManager.setDS9Cmd("regions load all ", completeUrl);
//		UncachedRequestBuilder requestBuilder = new UncachedRequestBuilder(
//				RequestBuilder.GET, completeUrl);
//
//		try {
//			requestBuilder.sendRequest(null, new RequestCallback() {
//
//				@Override
//				public void onError(
//						final com.google.gwt.http.client.Request request,
//						final Throwable exception) {
//					Log.debug(
//							"[sendSampDS9Region] Failed file reading", exception);
//				}
//
//				@Override
//				public void onResponseReceived(final Request request,
//						final Response response) {
//					
//					try {
//						int index;
//						if((index = response.getHeader("Content-Disposition").indexOf("filename")) >0){
//							String disposition = response.getHeader("Content-Disposition");
//							String fileName = disposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
//							Log.debug("filename=" + fileName);
//							gsampManager.setDS9Cmd("regions sky fk5", "");
//							gsampManager.setDS9Cmd("regions load all " + fileName, "");
//							gsampManager.setDS9Cmd("regions command \"circle 100 100 10\"", "");
//
//						}
//						
//				       
//					
//
//					} catch (Exception e) {
//
//						Log.debug("[ESASkySampEventHandlerImpl/processEvent()] Exception in ESASkySampEventHandlerImpl.processEvent",e);
//
//						throw new IllegalStateException(
//								"[ESASkySampEventHandlerImpl.processEvent] Unexpected SampAction: SEND_VO_TABLE");
//					}
//				}
//
//			});
//		} catch (RequestException e) {
//			Log.debug(
//					"[ESASkySampEventHandlerImpl/processEvent()] Failed file reading",
//					e);
//		}


//        //String text = "circle(259.999369,-19.955069,0.00371218)";
//		
//       String text ="regions-command \"circle 259.999369 -19.955069 0.00371218\" # color=green\"";// label='Hermione'";
//        // Prepare sending message
//        //String sendingMessage = EsaSkyConstants.APP_NAME + "-" + tableName;
//        gsampManager.setDS9Cmd(text,"");
//        gsampManager.setDS9Cmd("regions","circle 259.999369 -19.955069 0.00371218");
//        gsampManager.setDS9Cmd("regions","\"circle 259.997 -19.955 0.0371218\"");
//        
////        gsampManager.setDS9Cmd("regions command \"circle " + region + "# color=red\"","");
//        gsampManager.setDS9Cmd("regions command \"circle 259.999369 -19.9550690.00371218 # color=red label='Ceres'\"","");
//        gsampManager.setDS9Cmd("regions load all /home/eracero/trash/ceres.reg", "");
////        gsampManager.setDS9Cmd("frame refresh", "");
////        gsampManager.setDS9Cmd("circle","");
////        gsampManager.setDS9Cmd("202.5189583","");
////        gsampManager.setDS9Cmd("47.16969440","");
////        gsampManager.setDS9Cmd("0.00371218","");
//        //gsampManager.setDS9Cmd(text,text);
////        gsampManager.setDS9Cmd("command", text);
//        gsampManager.setDS9Cmd("regions sky fk5", "");
//
//        gsampManager.setDS9Cmd("regions command \"circle 259.99 -19.96 0.03 # color=red\"","");
//        
//        gsampManager.setDS9Cmd("command \"circle 259.99 -19.96 0.03 # color=red\"","regions");
//        gsampManager.setDS9Cmd("regions show yes","");
//	}
}

