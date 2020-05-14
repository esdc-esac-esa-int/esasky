package esac.archive.esasky.cl.web.client.utility;

public final class NumberFormatter {

	public static String formatToNumberWithSpaces(String string) {
        	int currentIndex = string.length() - 3;
        	while(currentIndex > 0){
        		string = string.substring(0, currentIndex) + " " + string.substring(currentIndex);
        		currentIndex -= 3;
        	}
        	return string;
    }
	
	public static String formatToNumberWithSpaces(int number) {
		return formatToNumberWithSpaces(new Integer(number).toString());
	}
	
    public static native boolean isNumber(String text) /*-{
        return !isNaN(text) && text.trim().length > 0;
    }-*/;
}
