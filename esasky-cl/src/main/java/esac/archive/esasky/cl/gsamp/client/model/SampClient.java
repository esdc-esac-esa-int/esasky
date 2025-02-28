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
 * Class to use in gwt the objects returned from javascript methods. Example of a SampClient
 * Javascript object printed in JSON format is:
 *
 * <pre>
 * {
 *     "metadata": {
 *         "samp.name": "\"ESASky\"",
 *         "samp.icon.url": "\"http://127.0.0.1:8888/images/favicon.png\"",
 *         "samp.description": "\"ESASky\"",
 *         "author.affiliation": "\"European Space Agency\"",
 *         "author.name": "\"ESAC Science Data Center (ESDC)\""
 *     },
 *     "subscriptions": {
 *         "samp.app.event.shutdown": "{}",
 *         "samp.app.ping": "{}",
 *         "samp.app.status": "{}",
 *         "samp.hub.disconnect": "{}",
 *         "samp.hub.event.metadata": "{}",
 *         "samp.hub.event.register": "{}",
 *         "samp.hub.event.shutdown": "{}",
 *         "samp.hub.event.subscriptions": "{}",
 *         "samp.hub.event.unregister": "{}"
 *     },
 *     "id": "c11",
 *     "name": "ESASky"
 * }
 * </pre>
 * @author ileon
 */
public class SampClient extends JavaScriptObject {

    /**
     * Class constructor().
     */
    protected SampClient() {
    }

    /**
     * Retrieve client name.
     * @return Value associated, null if not found.
     */
    public final native String getName() /*-{
		return this["name"].toString();
    }-*/;

    /**
     * Retrieve client id.
     * @return Value associated, null if not found.
     */
    public final native String getId() /*-{
		return this["id"].toString();
    }-*/;

    /**
     * Retrieve the value associated with this metadata array key.
     * @param key Input key.
     * @return Value associated, null if not found.
     */
    public final native String getMetadataValue(final String key) /*-{
		if (this.metadata != undefined) {
			return this.metadata[key].toString();
		} else {
			return null;
		}
    }-*/;

    /**
     * Retrieve all keys inside the "metadata" map.
     * @return Javascript array of strings.
     */
    public final native JsArrayString getMetadataKeys() /*-{
		if (this.metadata != undefined) {
			var metadataKeys = [];
			for ( var metadataKey in this.metadata) {
				metadataKeys.push(metadataKey);
			}
			return metadataKeys;
		} else {
			return null;
		}
    }-*/;

    /**
     * Retrieve the value associated with this subscription array key.
     * @param key Input key.
     * @return Value associated, null if not found.
     */
    public final native String getSubscriptionsValue(final String key) /*-{
		if (this.subscriptions != undefined) {
			return this.subscriptions[key].toString();
		} else {
			return null;
		}
    }-*/;

    /**
     * Retrieve all keys inside the "subscriptions" map.
     * @return Javascript array of strings.
     */
    public final native JsArrayString getSubscriptionsKeys() /*-{
		if (this.subscriptions != undefined) {
			var subscriptionsKeys = [];
			for ( var subscriptionsKey in this.subscriptions) {
				subscriptionsKeys.push(subscriptionsKey);
			}
			return subscriptionsKeys;
		} else {
			return null;
		}
    }-*/;

    /**
     * Print object contents in JSON format.
     * @return Object contents in JSON format
     */
    public final native String toJsonString() /*-{
		return JSON.stringify(this, null, 4);
    }-*/;

}
