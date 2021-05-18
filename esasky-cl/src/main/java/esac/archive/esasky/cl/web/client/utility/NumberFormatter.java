package esac.archive.esasky.cl.web.client.utility;

public final class NumberFormatter {

	public static String formatToNumberWithSpaces(String string) {
	    if(new Long(string) < 10000) {
	        return string;
	    }
    	int currentIndex = string.length() - 3;
    	while(currentIndex > 0){
    		string = string.substring(0, currentIndex) + "\u2009" + string.substring(currentIndex);
    		currentIndex -= 3;
    	}
    	return string;
    }
	
	public static String formatToNumberWithSpaces(int number) {
		return formatToNumberWithSpaces(Long.toString(number));
	}
	
    public static native boolean isNumber(String text) /*-{
        return !isNaN(text) && text.trim().length > 0;
    }-*/;
}
