package esac.archive.esasky.cl.web.client.utility;


public class EsaSkyColors {
	public static String[] colors = new String[]{"#E8ECFB", "#D9CCE3", "#CAACCB", "#BA8DB2", "#AA6F9E", "#994F88", "#882E72", "#1965B0",
			"#437DBF", "#6195CF", "#7BAFDE", "#4EB265", "#90C987", "#CAE0AB", "#F7F056", "#F7CB45", "#F4A736", "#EE8026", "#E665518",
			"#DC050C", "#A5170E", "#72190E", "#42150A"};
	
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
	
}
