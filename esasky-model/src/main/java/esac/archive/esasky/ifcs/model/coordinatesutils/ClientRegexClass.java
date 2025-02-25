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
