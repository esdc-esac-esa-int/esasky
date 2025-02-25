/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.cl.web.client.utility.samp;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class SampXmlParser {

	public static List<SampMessageItem> parse(String data){
		Document dom = XMLParser.parse(data);
		List<SampMessageItem> results = new ArrayList<SampMessageItem>();
		NodeList list = dom.getElementsByTagName("item");
		int size = list.getLength();
		String urn;
		NodeList nl;
		int childSize;
		Node n;
		String nodeName;
		String tmp;
		for(int i = 0; i < size; i++){
			Element e = (Element)list.item(i);
			urn = getAttributeIgnoreCase(e, "id");
			SampMessageItem msg = new SampMessageItem();
			msg.setUrn(urn);
			msg.setMsgid(urn + "_" + System.currentTimeMillis());
			
			nl = e.getChildNodes();
			if(nl != null){
				childSize = nl.getLength();
				for(int index = 0; index < childSize; index++){
					n = nl.item(index);
					nodeName = n.getNodeName();
					if(nodeName.equalsIgnoreCase("info")){
						tmp = extractNodeValue(n);
						tmp = escapeCdata(tmp);
						msg.setInfo(tmp);
					}else if(nodeName.equalsIgnoreCase("distribution_path")){
						tmp = extractNodeValue(n);
						tmp = escapeCdata(tmp);
						msg.setDistributionPath(tmp);
					}
				}
			}
			
			results.add(msg);
		}
		return results;
	}
	private static String getAttributeIgnoreCase(Element e, String attrId){
		if(e.hasAttribute(attrId)){
			return e.getAttribute(attrId);
		}
		if(e.hasAttribute(attrId.toLowerCase())){
			return e.getAttribute(attrId.toLowerCase());
		}
		return e.getAttribute(attrId.toUpperCase());
	}
	
	
	private static String extractNodeValue(Node n){
		if(n == null){
			return null;
		}
		Node c = n.getFirstChild();
		if(c == null){
			return null;
		}
		NodeList nl = n.getChildNodes();
		if(nl == null || nl.getLength() == 0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		String nodeValue;
		for(int i = 0 ; i < nl.getLength(); i++){
			c = nl.item(i);
			if (c == null){
				continue;
			}
			nodeValue = c.getNodeValue();
			if(nodeValue == null){
				return null;
			} else {
				sb.append(nodeValue);
			}
		}
		return sb.toString();
	}

	private static String escapeCdata(String data){
		if(data == null){
			return null;
		}
		if(data.startsWith("<![CDATA[")){
			int p = data.lastIndexOf("]]");
			if(p >= 0){
				return data.substring(10, p);
			}
		}
		return data;
	}
}
