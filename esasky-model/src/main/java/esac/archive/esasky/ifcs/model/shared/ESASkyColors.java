package esac.archive.esasky.ifcs.model.shared;


public class ESASkyColors {
	public static String[] colors = new String[]{"#E8ECFB", "#D9CCE3", "#CAACCB", "#BA8DB2", "#AA6F9E", "#994F88", "#882E72", "#1965B0",
			"#437DBF", "#6195CF", "#7BAFDE", "#4EB265", "#90C987", "#CAE0AB", "#F7F056", "#F7CB45", "#F4A736", "#EE8026", "#E65518",
			"#DC050C", "#A5170E", "#72190E", "#42150A", "#777777"};
	
	//This will extract the starting colorIndex from the floored -Log10 of the wavelength.
	//Ie UV with log(wl) @ ~- 7 would start at wavelengthIndex[7] 
	// Radar 	0-1
	// submm	1-3
	// IR 		3-6
	// OPtical	6-6.5
	// UV 		6.5-8
	// Xrays 	8-11
	// Gamma	11+
	public static int[] wavelengthIndex = new int[] {23,22,22,22,20,17,14,9,8,7,6,5,4};
	public static double[] wavelengthIndex2 = new double[] {14, 13.5, 13, 12, 11, 10, 9, 8, 7, 6.8, 6.5, 6.3, 6.2, 6.0, 5.66, 5.33, 5, 4.5, 4, 3.5, 3, 2};
	
	private static Integer index;
	
	public static String getNext() {
		if(index == null) {
			index = 0;
			return colors[index];
		}
		
		index++;
		if(index == colors.length -1 ) {
			index = 0;
		}
		
		return colors[index];
	}
	
	public static String getColor(int n){
		return colors[n];
	}
	
	public static int maxIndex() {
		return colors.length - 1;
	}
	
	public static String getColorFromWavelength(double wavelength) {
		int index = wavelengthToIndex(wavelength);
		return getColor(index);
	}
	
	public static int wavelengthToIndex(double wavelength) {
		//Should already be in Log10
		if(wavelength < 0) {
			wavelength = - wavelength;
		}
		
		int index = (int) Math.floor(wavelength);
		if(index >= wavelengthIndex.length - 1) {
			return 0;
		}
		
		int start = wavelengthIndex[index];
		int end = wavelengthIndex[index + 1];
		double part = wavelength - index;
		
		return (int) Math.floor(start + (end - start) * part);
	}
	
	public static double indexToWaveLength(int index) {
		return wavelengthIndex2[index];
	}

	public static double valueToWaveLength(double value) {
		int index = (int) Math.floor(value);
		if(index < wavelengthIndex2.length - 1) {
			return (value - index) * (wavelengthIndex2[index + 1] - wavelengthIndex2[index]) + wavelengthIndex2[index];
		}
		return wavelengthIndex2[wavelengthIndex2.length - 1];
	}
	
}
