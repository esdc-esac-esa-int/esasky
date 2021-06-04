package esac.archive.esasky.ifcs.model.coordinatesutils;

import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.RegexClass;

public class ClientRegexClass implements RegexClass{
	
	public boolean test(String pattern, String stringToTest) {
		return regex(pattern, stringToTest);
	}
	
    private native boolean regex(String pattern, String stringToTest)/*-{ 
		return $wnd.XRegExp(pattern).test(stringToTest);
	}-*/;

    public native GeneralJavaScriptObject match(String pattern, String stringToTest)/*-{ 
		return stringToTest.match(pattern);
	}-*/;

}
