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

package esac.archive.esasky.cl.gsamp.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Class to use in gwt the samp message objects returned from javascript methods. Examples of a
 * SampMessage Javascript object printed in JSON format:
 *
 * <pre>
 * [message] -> {
 *     "samp.mtype": "samp.hub.event.subscriptions",
 *     "samp.params": {
 *         "id": "c31",
 *         "subscriptions": {
 *             "samp.hub.event.subscriptions": {},
 *             "samp.hub.event.metadata": {},
 *             "samp.hub.disconnect": {},
 *             "samp.hub.event.unregister": {},
 *             "samp.hub.event.shutdown": {},
 *             "samp.app.status": {},
 *             "samp.app.ping": {},
 *             "samp.hub.event.register": {}
 *         }
 *     }
 * }
 * [message] -> {
 *     "samp.mtype": "samp.hub.event.register",
 *     "samp.params": {
 *         "id": "c32"
 *     }
 * }
 * [message] -> {
 *     "samp.mtype": "samp.hub.event.metadata",
 *     "samp.params": {
 *         "id": "c32",
 *         "metadata": {
 *             "samp.icon.url": "http://www.star.bristol.ac.uk/~mbt/websamp/clientIcon.gif",
 *             "samp.name": "Monitor",
 *             "samp.description": "JavaScript-based Web Profile monitor for SAMP clients "
 *         }
 *     }
 * }
 * [message] -> {
 *     "samp.mtype": "samp.hub.event.subscriptions",
 *     "samp.params": {
 *         "id": "c32",
 *         "subscriptions": {
 *             "*": {}
 *         }
 *     }
 * }
 * [message] -> {
 *     "samp.mtype": "samp.hub.event.unregister",
 *     "samp.params": {
 *         "id": "c32"
 *     }
 * }
 * [message] -> {
 *     "samp.mtype": "samp.hub.disconnect",
 *     "samp.params": {
 *         "reason": "GUI hub user requested ejection"
 *     }
 * }
 * [message] -> {
 *     "samp.mtype": "samp.hub.event.shutdown",
 *     "samp.params": {}
 * }
 * </pre>
 * @author ileon
 */
public class SampMessage extends JavaScriptObject {

    public final static String MESSAGE_TYPE_SUBSCRIPTIONS = "samp.hub.event.subscriptions";

    public final static String MESSAGE_TYPE_METADATA = "samp.hub.event.metadata";

    public final static String MESSAGE_TYPE_REGISTER = "samp.hub.event.register";

    public final static String MESSAGE_TYPE_UNREGISTER = "samp.hub.event.unregister";

    public final static String MESSAGE_TYPE_DISCONNECT = "samp.hub.disconnect";

    public final static String MESSAGE_TYPE_SHUTDOWN = "samp.hub.event.shutdown";

    protected SampMessage() {
    }

    /**
     * Retrieve message type.
     * @return Value associated, null if not found.
     */
    public final native String getMessageType() /*-{
		return this["samp.mtype"].toString();
    }-*/;

    /**
     * Retrieve all keys inside the "samp.params" map.
     * @return Javascript array of strings.
     */
    public final native JsArrayString getParametersKeys() /*-{
		if (this["samp.params"] != undefined) {
			var parametesKeys = [];
			for ( var parametesKey in this["samp.params"]) {
				parametesKeys.push(parametesKey);
			}
			return parametesKeys;
		} else {
			return null;
		}
    }-*/;

    /**
     * Retrieve value for ["samp.params"].["id"] or null if it does not exist.
     * @return String value or null.
     */
    public final native String getMessageId() /*-{
		if (this["samp.params"] != undefined) {
			if (this["samp.params"]["id"] != undefined) {
				return this["samp.params"]["id"].toString();
			}
		}
		return null;
    }-*/;

    /**
     * Retrieve value for ["samp.params"].["reason"] or null if it does not exist.
     * @return String value or null.
     */
    public final native String getMessageReason() /*-{
		if (this["samp.params"] != undefined) {
			if (this["samp.params"]["reason"] != undefined) {
				return this["samp.params"]["reason"].toString();
			}
		}
		return null;
    }-*/;

    /**
     * Retrieve the value associated with this metadata array key.
     * @param key Input key.
     * @return Value associated, null if not found.
     */
    public final native String getMetadataValue(final String key) /*-{
		if (this["samp.params"] != undefined) {
			if (this["samp.params"]["metadata"] != undefined) {
				if (this["samp.params"]["metadata"][key] != undefined) {
					return this["samp.params"]["metadata"][key].toString();
				}
			}
		}
		return null;

    }-*/;

    /**
     * Retrieve all keys inside the "metadata" map.
     * @return Javascript array of strings.
     */
    public final native JsArrayString getMetadataKeys() /*-{
		if (this["samp.params"] != undefined) {
			if (this["samp.params"]["metadata"] != undefined) {
				var metadataKeys = [];
				for ( var metadataKey in this["samp.params"]["metadata"]) {
					metadataKeys.push(metadataKey);
				}
				return metadataKeys;
			}
		}
		return [];
    }-*/;

    /**
     * Retrieve the value associated with this subscription array key.
     * @param key Input key.
     * @return Value associated, null if not found.
     */
    public final native String getSubscriptionsValue(final String key) /*-{
		if (this["samp.params"] != undefined) {
			if (this["samp.params"]["subscriptions"] != undefined) {
				if (this["samp.params"]["subscriptions"][key] != undefined) {
					return this["samp.params"]["subscriptions"][key].toString();
				}
			}
		}
		return null;
    }-*/;

    /**
     * Retrieve all keys inside the "subscriptions" map.
     * @return Javascript array of strings.
     */
    public final native JsArrayString getSubscriptionsKeys() /*-{
		if (this["samp.params"] != undefined) {
			if (this["samp.params"]["subscriptions"] != undefined) {
				var subscriptionsKeys = [];
				for ( var subscriptionsKey in this["samp.params"]["subscriptions"]) {
					subscriptionsKeys.push(subscriptionsKey);
				}
				return subscriptionsKeys;
			}
		}
		return [];
    }-*/;

    /**
     * Print object contents in JSON format.
     * @return Object contents in JSON format
     */
    public final native String toJsonString() /*-{
		return JSON.stringify(this, null, 4);
    }-*/;

    /**
     * Print object contents.
     * @return Object contents
     */
    public final String toFormattedString() {
        String output = new String();
        output = "Message Type [" + getMessageType() + "], ";
        output = output + "Id [" + getMessageId() + "], ";
        output = output + "Reason [" + getMessageReason() + "]";
        for (int i = 0; i < getMetadataKeys().length(); i++) {
            output = output + "-> Metadata: [" + getMetadataKeys().get(i) + "]->["
                    + getMetadataValue(getMetadataKeys().get(i)) + "]";
        }
        for (int i = 0; i < getSubscriptionsKeys().length(); i++) {
            output = output + "-> Subscribed to: [" + getSubscriptionsKeys().get(i) + "]";
        }
        return output;
    }
}
