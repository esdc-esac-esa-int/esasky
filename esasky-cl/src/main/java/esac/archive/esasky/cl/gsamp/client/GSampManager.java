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

package esac.archive.esasky.cl.gsamp.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.gsamp.client.jsni.SampJsniConnector;
import esac.archive.esasky.cl.gsamp.client.model.SampClient;

/**
 * Class to expose java access to SAMP javascript methods.
 * @author ileon
 */
public class GSampManager {

    /** SampJsniConnector. */
    SampJsniConnector sampJsniConnector;

    /**
     * Constructor.
     * @param widget Widget to receive SAMP events.
     */
    public GSampManager(final Widget widget) {
        this.sampJsniConnector = new SampJsniConnector(widget);
    }

    /**
     * Register to SAMP hub.
     */
    public final void register() {
        this.sampJsniConnector.registerToSampHub();
    }

    /**
     * Unregister from SAMP hub.
     */
    public final void unregister() {
        this.sampJsniConnector.unregisterFromSampHub();
    }

    /**
     * Are we already registered to SAMP hub.
     * @return Whether we are registered or not.
     */
    public final boolean isApplicationRegistered() {
        return this.sampJsniConnector.isApplicationRegistered();
    }

    // /**
    // * Is there a SAMP hub running?.
    // * @return If a SAMP hub is running in the default port or not.
    // */
    // public boolean isHubRunning() {
    // return this.sampJsniConnector.isHubRunning();
    // }

    /**
     * Is there a SAMP hub running?.
     * @return If a SAMP hub is running in the default port or not.
     */
    public final boolean getHubStatus() {
        return this.sampJsniConnector.getHubStatus();
    }

    /**
     * Launch an asynchronous RPC ping on the hub. Needed to know its status.
     */
    public final void launchRpcPingOnHub() {
        this.sampJsniConnector.launchRpcPingOnHub();
    }

    /**
     * Broadcast VoTable url to SAMP hub.
     * @param voTableUrl Input vo table url.
     * @param tableId Id to identify the table (used for reference)
     * @param tableName Table name to be displayed
     * @throws Exception Basic Java Exception.
     */
    public final void loadVoTable(final String voTableUrl, final String tableId,
            final String tableName) throws Exception {
        try {
            this.sampJsniConnector.loadVoTable(voTableUrl, tableId, tableName);
        } catch (JavaScriptException jse) {
            Log.error("JavascriptException in GSampUtils.broadcastVoTableUrl()", jse);
            throw new Exception(jse);
        }
    }

    /**
     * Send votable url to a specific client.
     * @param voTableUrl Input vo table url.
     * @param tableId Id to identify the table (used for reference)
     * @param tableName Table name to be displayed
     * @param clientId Client identifier to whom the message is sent.
     * @throws Exception If any error is found.
     */
    public final void loadVoTable(final String voTableUrl, final String tableId,
            final String tableName, final String clientId) throws Exception {
        try {
            this.sampJsniConnector.loadVoTable(voTableUrl, tableId, tableName, clientId);
        } catch (JavaScriptException jse) {
            Log.error("JavascriptException in GSampUtils.sendVoTableToClient()", jse);
            throw new Exception(jse);
        }
    }

    /**
     * Send fits Image url to a specific client.
     * @param fitsImageUrl Input fits image url.
     * @param fitsImageId Id to identify the fits image (used for reference)
     * @param fitsImageName Fits image name to be displayed
     * @param clientId Client identifier to whom the message is sent.
     * @throws Exception If any error is found.
     */
    public final void loadFitsImage(final String fitsImageUrl, final String fitsImageId,
            final String fitsImageName, final String clientId) throws Exception {
        try {
            this.sampJsniConnector
            .loadFitsImage(fitsImageUrl, fitsImageId, fitsImageName, clientId);
        } catch (JavaScriptException jse) {
            Log.error("JavascriptException in GSampUtils.sendFitsImageToClient()", jse);
            throw new Exception(jse);
        }
    }

    /**
     * Broadcast Fits Image url to SAMP hub.
     * @param fitsImageUrl Input fits image url.
     * @param fitsImageId Id to identify the fits image (used for reference)
     * @param fitsImageName Fits image name to be displayed
     * @throws Exception If any error is found.
     */
    public final void loadFitsImage(final String fitsImageUrl, final String fitsImageId,
            final String fitsImageName) throws Exception {
        try {
        	Log.debug("FITSURL=" + fitsImageUrl + ",FITSID=" + fitsImageId + ",FITSNAME=" + fitsImageName);
            this.sampJsniConnector.loadFitsImage(fitsImageUrl, fitsImageId, fitsImageName);
        } catch (JavaScriptException jse) {
            Log.error("JavascriptException in GSampUtils.broadcastFitsImageUrl()", jse);
            throw new Exception(jse);
        }
    }

    /**
     * Send fits Table url to a specific client.
     * @param fitsTableUrl Input fits table url.
     * @param fitsTableId Id to identify the fits table (used for reference)
     * @param fitsTableName Fits table name to be displayed
     * @param clientId Client identifier to whom the message is sent.
     * @throws Exception If any error is found.
     */
    public final void loadFitsTable(final String fitsTableUrl, final String fitsTableId,
            final String fitsTableName, final String clientId) throws Exception {
        try {
            this.sampJsniConnector
            .loadFitsTable(fitsTableUrl, fitsTableId, fitsTableName, clientId);
        } catch (JavaScriptException jse) {
            Log.error("JavascriptException in GSampUtils.sendFitsTableToClient()", jse);
            throw new Exception(jse);
        }
    }

    /**
     * Broadcast Fits Table url to SAMP hub.
     * @param fitsTableUrl Input fits image url.
     * @param fitsTableId Id to identify the fits table (used for reference)
     * @param fitsTableName Fits table name to be displayed
     * @throws Exception If any error is found.
     */
    public final void loadFitsTable(final String fitsTableUrl, final String fitsTableId,
            final String fitsTableName) throws Exception {
        try {
            this.sampJsniConnector.loadFitsTable(fitsTableUrl, fitsTableId, fitsTableName);
        } catch (JavaScriptException jse) {
            Log.error("JavascriptException in GSampUtils.broadcastFitsTableUrl()", jse);
            throw new Exception(jse);
        }
    }

    /**
     * Broadcast a script to Aladin.
     * @param script Input String script.
     * @throws Exception which is a basic Java exception.
     */
    public final void sendScriptToAladin(final String script) throws Exception {
        try {
            this.sampJsniConnector.sendScriptToAladin(script);
        } catch (JavaScriptException jse) {
            Log.error("JavascriptException in GSampUtils.broadcastFitsTableUrl()", jse);
            throw new Exception(jse);
        }
    }

    /**
     * Send highlight table row samp message to all clients (broadcast).
     * @param voTableUrl Input vo table url.
     * @param tableId Id to identify the table (used for reference)
     * @param rowNumber Number of row to highlight.
     * @throws Exception If any error is found.
     */
    public final void highlightTableRow(final String voTableUrl, final String tableId,
            final int rowNumber) throws Exception {
        try {
            this.sampJsniConnector.highlightTableRow(voTableUrl, tableId, rowNumber);
        } catch (JavaScriptException jse) {
            Log.error("JavascriptException in GSampUtils.highlightTableRow()", jse);
            throw new Exception(jse);
        }
    }

    /**
     * Send highlight table row samp message to input client.
     * @param voTableUrl Input vo table url.
     * @param tableId Id to identify the table (used for reference)
     * @param rowNumber Number of row to highlight.
     * @param clientId Client identifier to whom the message is sent.
     * @throws Exception If any error is found.
     */
    public final void highlightTableRow(final String voTableUrl, final String tableId,
            final int rowNumber, final String clientId) throws Exception {
        try {
            this.sampJsniConnector.highlightTableRow(voTableUrl, tableId, rowNumber, clientId);
        } catch (JavaScriptException jse) {
            Log.error("JavascriptException in GSampUtils.highlightTableRow()", jse);
            throw new Exception(jse);
        }
    }

    /**
     * Selects a list of rows of an identified table by row index.
     * @param voTableUrl Input vo table url.
     * @param tableId Id to identify the table (used for reference)
     * @param rowList List of rows to select.
     * @throws Exception If any error is found.
     */
    public final void selectTableRowList(final String voTableUrl, final String tableId,
            final int[] rowList) throws Exception {
        try {
            JsArrayString javascriptRowList = (JsArrayString) JsArrayString.createArray();
            if (rowList != null) {
                for (int i = 0; i < rowList.length; i++) {
                    javascriptRowList.push("" + rowList[i]);
                }
            }
            this.sampJsniConnector.selectTableRowList(voTableUrl, tableId, javascriptRowList);
        } catch (JavaScriptException jse) {
            Log.error("JavascriptException in GSampUtils.selectTableRowList()", jse);
            throw new Exception(jse);
        }
    }

    /**
     * Selects a list of rows of an identified table by row index.
     * @param voTableUrl Input vo table url.
     * @param tableId Id to identify the table (used for reference)
     * @param rowList List of rows to select.
     * @param clientId Client identifier to whom the message is sent.
     * @throws Exception If any error is found.
     */
    public final void selectTableRowList(final String voTableUrl, final String tableId,
            final int[] rowList, final String clientId) throws Exception {
        try {
            JsArrayString javascriptRowList = (JsArrayString) JsArrayString.createArray();
            if (rowList != null) {
                for (int i = 0; i < rowList.length; i++) {
                    javascriptRowList.push("" + rowList[i]);
                }
            }
            this.sampJsniConnector.selectTableRowList(voTableUrl, tableId, javascriptRowList,
                    clientId);
        } catch (JavaScriptException jse) {
            Log.error("JavascriptException in GSampUtils.selectTableRowList()", jse);
            throw new Exception(jse);
        }
    }

    /**
     * Retrieve hub registered clients.
     * @return JsArray of SampClient objects.
     */
    public final JsArray<SampClient> getRegisteredClients() {
        JsArray<SampClient> sampClients = this.sampJsniConnector.getRegisteredClients();
        return sampClients;
    }

    /**
     * Given an input samp client name, return its samp id string.
     * @param sampClientName Input samp client name.
     * @return Client id or null if not found.
     */
    public final String getSampClientId(final String sampClientName) {
        if (sampClientName != null) {
            if (this.isApplicationRegistered()) {
                JsArray<SampClient> sampClients = this.getRegisteredClients();
                if (sampClients != null) {
                    for (int i = 0; i < sampClients.length(); i++) {
                        SampClient sampClient = sampClients.get(i);
                        if (sampClientName.equals(sampClient.getName())) {
                            return sampClient.getId();
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * set DS9 Cmd.
     * @param commandId: Command Identifier. identifier
     * @throws Exception If any error is found.
     */
    public String setDS9Cmd(String cmd, String data) throws Exception {
        String result = "";
        try {
        	String id = getSampDS9Id();
        	System.out.println("DS9 ID=" + id);
            sampJsniConnector.setDS9Cmd(id, cmd, data);
            
            return result;
        } catch (JavaScriptException jse) {
            Log.error("JavascriptException in GSampUtils.setDS9Cmd units", jse);
            throw new Exception(jse);
        }
    }

    /**
     * Given an input samp client name, return its samp id string.
     * @param sampClientName Input samp client name.
     * @return Client id or null if not found.
     */
    public String getSampDS9Id() {
        String sampClientName = "DS9";
        if (this.isApplicationRegistered()) {
            JsArray<SampClient> sampClients = this.getRegisteredClients();
            if (sampClients != null) {
                for (int i = 0; i < sampClients.length(); i++) {
                    SampClient sampClient = sampClients.get(i);
                    if (sampClient.getName().contains(sampClientName)) {
                        return sampClient.getId();
                    }
                }
            }
        }
        return null;
    }
}
