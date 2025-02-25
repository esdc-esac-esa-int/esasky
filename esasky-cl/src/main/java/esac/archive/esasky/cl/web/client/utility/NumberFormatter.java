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
	
	public static String formatToNumberWithSpaces(long number) {
		return formatToNumberWithSpaces(Long.toString(number));
	}
	
    public static native boolean isNumber(String text) /*-{
        return !isNaN(text) && text.trim().length > 0;
    }-*/;
}
