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

package esac.archive.esasky.ifcs.model.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HipsWavelength {
	public static String GAMMA_RAY = "GAMMA_RAY";
	public static String HARD_X_RAY = "HARD_X_RAY";
	public static String SOFT_X_RAY = "SOFT_X_RAY";
	public static String UV = "UV";
	public static String OPTICAL = "OPTICAL";
	public static String NEAR_IR = "NEAR_IR";
	public static String MID_IR = "MID_IR";
	public static String FAR_IR = "FAR_IR";
	public static String SUBMM = "SUBMM";
	public static String RADIO = "RADIO";
	public static String OTHERS = "OTHERS";
	public static String USER = "USER";
	public static String GW = "GW";


	public static LinkedList<String> wavelengthList = new LinkedList<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 726294782000193621L;

		{
			add(GAMMA_RAY);
			add(HARD_X_RAY);
			add(SOFT_X_RAY);
			add(UV);
			add(OPTICAL);
			add(NEAR_IR);
			add(MID_IR);
			add(FAR_IR);
			add(SUBMM);
			add(RADIO);
			add(OTHERS);
			add(GW);
		}
	};
	
	public static Map<String, LinkedList<HiPS>> listOfUserHips;

	public static Map<String, LinkedList<HiPS>> getListOfUserHips() {
		if(listOfUserHips==null) {
			listOfUserHips = new HashMap<>();
		}
		return listOfUserHips;
	}

		
	
}

