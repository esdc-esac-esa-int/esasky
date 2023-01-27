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
			add(USER);
			add(GW);
		}
	};
	
	public static Map<String, LinkedList<HiPS>> listOfUserHips;

	public static Map<String, LinkedList<HiPS>> getListOfUserHips() {
		if(listOfUserHips==null) {
			listOfUserHips = new HashMap<String, LinkedList<HiPS>>();
		}
		return listOfUserHips;
	}

		
	
}

