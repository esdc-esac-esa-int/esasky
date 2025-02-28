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

package esac.archive.esasky.cl.web.client.login;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

import java.util.HashMap;
import java.util.Map;

public class UserDetails {
    private final String id;
    private final String name;
    private final long quotaDb;
    private final long sizeDb;
    private final long syncMaxExecTime;
    private final Map<String,String> properties = new HashMap<String,String>();

    public UserDetails(String id, Long dbQuota, Long sizeDb, Long syncMaxExecTime, String name) {
        this.id = id;
        this.quotaDb = dbQuota;
        this.sizeDb = sizeDb;
        this.syncMaxExecTime = syncMaxExecTime;
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public long getQuotaDb() {
        return quotaDb;
    }

    public long getCurrentSizeDb() {
        return sizeDb;
    }

    public String getName() {
        return name;
    }

    public long getSyncMaxExecTime() {
        return syncMaxExecTime;
    }

    public String getProperty(String name) {
        return properties.get(name);
    }

    public static UserDetails parseFromXml(String xml) {
        Document doc = XMLParser.parse(xml);

        String username = getNodeValue(doc, "username");
        Long dbQuota = getNodeValueAsLong(doc, "db_quota");
        Long dbCurrentSize = getNodeValueAsLong(doc, "db_current_size");
        Long syncMaxExecTime = getNodeValueAsLong(doc, "sync_max_exec_time");
        String userNameDetails = getNodeValue(doc, "user_name_details");

        return new UserDetails(username, dbQuota, dbCurrentSize, syncMaxExecTime, userNameDetails);
    }

    public static String getNodeValue(Document doc, String tagValue) {
        NodeList nl = doc.getElementsByTagName(tagValue);
        if (nl == null || nl.getLength() < 1) {
            return "";
        }
        Text text = (Text)nl.item(0).getFirstChild();
        if (text == null) {
            return "";
        } else {
            return text.getData();
        }
    }

    public static long getNodeValueAsLong(Document doc, String tagValue) {
        String tmp = getNodeValue(doc, tagValue);
        if (tmp == null) {
            return -1;
        }
        try {
            return Long.parseLong(tmp);
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
            return -1;
        }
    }
}
