package esac.archive.esasky.cl.web.client.utility;


import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class IniFileParser {
    
	public static GeneralJavaScriptObject parseIniString(String iniText) {
		GeneralJavaScriptObject props = GeneralJavaScriptObject.createJsonObject("{}");
		
		for(String line : iniText.split("\n")) {
			String[] splitLine = line.split("=");
			if(splitLine.length > 1) {
				String propName = splitLine[0].trim();
				String propValue = splitLine[1].trim();
				props.setProperty(propName, propValue);
			}
		}
		
		return props;
	}
}
