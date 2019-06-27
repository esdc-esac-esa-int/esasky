package esac.archive.esasky.cl.gsamp.client.jsni;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.gsamp.client.event.SampEvent;
import esac.archive.esasky.cl.gsamp.client.model.SampClient;
import esac.archive.esasky.cl.gsamp.client.model.SampMessage;

/**
 * Class to expose SAMP javascript methods to be used by GWT code.
 * @author ileon
 */
public class SampJsniConnector {

    // Static variable, otherwise it cannot be accessed from static method, and
    // if it is not a static method it cannot be called by javascript listener...
    /** associateWidget. local widget. */
    private static Widget associatedWidget;

    @SuppressWarnings("static-access")
    /**
     * Class constructor.
     * @param widget Input Widget.
     */
    public SampJsniConnector(Widget widget) {
        // Really bad practice, but see comment above...
        this.associatedWidget = widget;
        initJsSampVars();
    }

    /**
     * initJsSampVars().
     */
    private native void initJsSampVars() /*-{

		$wnd.esasky_metadata = new $wnd.sampEsaSkyMetadata();

		$wnd.samp_clienttracker = new $wnd.samp.ClientTracker();
		$wnd.samp_ct_callHandler = $wnd.samp_clienttracker.callHandler;
		$wnd.samp_ct_callHandler["samp.app.ping"] = function(senderId, message,
				isCall) {
			if (isCall) {
				return {
					text : "Hello, " + samp_clienttracker.getName(senderId)
							+ ", greetings from ESASky"
				};
			}
		};
		$wnd.samp_ct_callHandler["samp.app.status"] = function(senderId,
				message, isCall) {
			if (isCall) {
				return {
					text : "samp.ok"
				};
			}
		};
		$wnd.logCallableClient = {
			receiveNotification : function(senderId, message) {
				var handled = $wnd.samp_clienttracker.receiveNotification(
						senderId, message);
				@esac.archive.esasky.cl.gsamp.client.jsni.SampJsniConnector::fireSampEvent(Lesac/archive/esasky/cl/gsamp/client/model/SampMessage;)(message);
				// alert("[Notification from ["
				//	+ $wnd.samp_clienttracker.getName(senderId) + "]] -> "
				//	+ JSON.stringify(message, null, 4));
			},
			receiveCall : function(senderId, msgId, message) {
				var handled = $wnd.samp_clienttracker.receiveCall(senderId,
						msgId, message);
				// alert("[Call from ["
				//  + $wnd.samp_clienttracker.getName(senderId) + "]] -> "
				//  + JSON.stringify(message, null, 4));
			},
			receiveResponse : function(responderId, msgTag, response) {
				var handled = $wnd.samp_clienttracker.receiveResponse(
						responderId, msgTag, response);
				// alert("[Responsee from ["
				//  + $wnd.samp_clienttracker.getName(senderId) + "]] -> "
				//  + JSON.stringify(message, null, 4));
			},
			init : function(connection) {
				$wnd.samp_clienttracker.init(connection);
			}
		};

		$wnd.esasky_subscriptions = new $wnd.sampEsaSkySubscriptions();

		$wnd.samp_connector = new $wnd.samp.Connector("ESASky",
				$wnd.esasky_metadata, $wnd.logCallableClient,
				$wnd.esasky_subscriptions);
    }-*/;

    /**
     * registerToSampHub().
     */
    public native void registerToSampHub() /*-{
		$wnd.samp_connector.register();
    }-*/;

    /**
     * unregisterFromSampHub().
     */
    public native void unregisterFromSampHub() /*-{
		$wnd.samp_connector.unregister();
    }-*/;

    /**
     * isApplicationRegistered().
     * @return boolean value.
     */
    public native boolean isApplicationRegistered() /*-{
		return (!!$wnd.samp_connector.connection);
    }-*/;

    public native void loadVoTable(String voTableUrl, String tableId, String tableName) /*-{
		var msg = new $wnd.samp.Message("table.load.votable",
				new $wnd.sampVoTableParams(tableId, voTableUrl, tableName));
		$wnd.samp_connector.connection.notifyAll([ msg ]);
    }-*/;

    public native void loadVoTable(String voTableUrl, String tableId, String tableName,
            String clientId) /*-{
		var msg = new $wnd.samp.Message("table.load.votable",
				new $wnd.sampVoTableParams(tableId, voTableUrl, tableName));
		$wnd.samp_connector.connection.notify([ clientId, msg ]);
    }-*/;

    public native void loadFitsImage(String fitsImageUrl, String fitsImageId, String fitsImageName) /*-{
		var msg = new $wnd.samp.Message("image.load.fits",
				new $wnd.sampFitsImageParams(fitsImageId, fitsImageUrl,
						fitsImageName));
		$wnd.samp_connector.connection.notifyAll([ msg ]);
    }-*/;

    public native void loadFitsImage(String fitsImageUrl, String fitsImageId, String fitsImageName,
            String clientId) /*-{
		var msg = new $wnd.samp.Message("image.load.fits",
				new $wnd.sampFitsImageParams(fitsImageId, fitsImageUrl,
						fitsImageName));
		$wnd.samp_connector.connection.notify([ clientId, msg ]);
    }-*/;

    public native void loadFitsTable(String fitsTableUrl, String fitsTableId, String fitsTableName) /*-{
		var msg = new $wnd.samp.Message("table.load.fits",
				new $wnd.sampFitsTableParams(fitsTableId, fitsTableUrl,
						fitsTableName));
		$wnd.samp_connector.connection.notifyAll([ msg ]);
    }-*/;

    public native void loadFitsTable(String fitsTableUrl, String fitsTableId, String fitsTableName,
            String clientId) /*-{
		var msg = new $wnd.samp.Message("table.load.fits",
				new $wnd.sampFitsTableParams(fitsTableId, fitsTableUrl,
						fitsTableName));
		$wnd.samp_connector.connection.notify([ clientId, msg ]);
    }-*/;

    public native void sendScriptToAladin(String script) /*-{
		var msg = new $wnd.samp.Message("script.aladin.send",
				new $wnd.sampAladinScriptParams(script));
		$wnd.samp_connector.connection.notifyAll([ msg ]);
    }-*/;

    public native void highlightTableRow(String voTableUrl, String tableId, int rowNumber,
            String clientId) /*-{
		var msg = new $wnd.samp.Message("table.highlight.row",
				new $wnd.sampHighlightRowParams(tableId, voTableUrl, rowNumber));
		$wnd.samp_connector.connection.notify([ clientId, msg ]);
    }-*/;

    public native void highlightTableRow(String voTableUrl, String tableId, int rowNumber) /*-{
		var msg = new $wnd.samp.Message("table.highlight.row",
				new $wnd.sampHighlightRowParams(tableId, voTableUrl, rowNumber));
		$wnd.samp_connector.connection.notifyAll([ msg ]);
    }-*/;

    public native void selectTableRowList(String voTableUrl, String tableId, JsArrayString rowList,
            String clientId) /*-{
		var msg = new $wnd.samp.Message("table.select.rowList",
				new $wnd.sampSelectRowListParams(tableId, voTableUrl, rowList));
		$wnd.samp_connector.connection.notify([ clientId, msg ]);
    }-*/;

    public native void selectTableRowList(String voTableUrl, String tableId, JsArrayString rowList) /*-{
		var msg = new $wnd.samp.Message("table.select.rowList",
				new $wnd.sampSelectRowListParams(tableId, voTableUrl, rowList));
		$wnd.samp_connector.connection.notifyAll([ msg ]);
    }-*/;

    public native JsArray<SampClient> getRegisteredClients() /*-{
		if (!!$wnd.samp_connector.connection) {
			var client_ids = (!!$wnd.samp_clienttracker.connection) ? $wnd.samp_clienttracker.ids
					: [];
			var sampClientArray = [];
			for (client_id in client_ids) {
				var clientDetails = new $wnd.sampClientDetails();
				clientDetails.id = client_id;
				clientDetails.name = $wnd.samp_clienttracker.getName(client_id);
				var client_metadata = $wnd.samp_clienttracker.metas[client_id];
				for (i in client_metadata) {
					clientDetails.metadata[i] = JSON
							.stringify(client_metadata[i]);
				}
				var client_subscriptions = $wnd.samp_clienttracker.subs[client_id];
				for (i in client_subscriptions) {
					clientDetails.subscriptions[i] = JSON
							.stringify(client_subscriptions[i]);
				}
				sampClientArray.push(clientDetails);
			}
			return sampClientArray;
			// alert(JSON.stringify(sampClientArray, null, 4));
		} else {
			return [];
		}
    }-*/;

    // public boolean isHubRunning() {
    // Hub runs by default at http://localhost:21012/ (see samp.js)

    // Option 1 (homemade): There seems to be problems in some cases
    // return $wnd.isThere("http://localhost:21012/");

    // Option 2: Call defaults samp implementation (with a timeout!)
    // launchRpcPingOnHub();
    // return getHubStatus();
    // };

    public native void launchRpcPingOnHub() /*-{
		// alert('External variable before = ' + hub_status.active);
		$wnd.samp.ping($wnd.isHubActive);
		//alert('External variable after = ' + hub_status.active);
    }-*/;

    public native boolean getHubStatus() /*-{
		return $wnd.hub_status.active;
    }-*/;

    public static void fireSampEvent(SampMessage sampMessage) {
        associatedWidget.fireEvent(new SampEvent(sampMessage));
    }
    
//    public native void setDS9Cmd(String clientId, String cmd, String data) /*-{
//    var msg = new $wnd.samp.Message("ds9.set", new $wnd.sampDs9MethodParams(cmd));
//    $wnd.samp_connector.connection.notify([ clientId, msg ]);
//    }-*/;
    
    public native void setDS9Cmd(String clientId, String msgId, String commandId) /*-{
    var msg = new $wnd.samp.Message("ds9.set",
    new $wnd.sampDs9MethodParams(msgId));
    $wnd.samp_connector.connection.call([ clientId, commandId, msg ]);
    }-*/;

    public native void setDS9CmdRestricted(String clientId, String msgId, String commandId) /*-{
    var msg = new $wnd.samp.Message("ds9.restricted-set",
    new $wnd.sampDs9MethodParams(msgId));
    $wnd.samp_connector.connection.call([ clientId, commandId, msg ]);
    }-*/;

}
