package esac.archive.esasky.ifcs.model.shared;

import java.util.Arrays;

import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.shared.RangeTree.Interval;

public class EsaSkyMocUtility {
	
	private EsaSkyMocUtility() {
		
	}
	
	
	public static String asciiStringToJsonString(String pixelString) {
		// On the IVOA format order/p1-p2 p3 p4 order2/p1-p2
		String jsonString = "{";
		
		String[] orderSplit = pixelString.split("/");
		int nOrders = orderSplit.length - 1;
		
		if(nOrders < 2) {
			return null;
		}
		
		String[] list = orderSplit[0].split(" ");
		String order = list[list.length - 1];
		for(int i = 1; i <= nOrders; i++) {
			list = orderSplit[i].split(" ");
			String[] list2;
			if(i < nOrders) {
				list2 = Arrays.copyOfRange(list, 0, list.length - 1);
			}else {
				list2 = Arrays.copyOfRange(list, 0, list.length);
			}
			jsonString += order + ":" + pixelInOrderListToJsonString(list2) + ",";
			if(i < nOrders) {
				order = list[list.length - 1];
			}
		}
		jsonString = jsonString.substring(0, jsonString.length() - 1 ) + "}";
		return jsonString;
		
	}
	
	public static RangeTree asciiStringToRangeTree(String pixelString, int maxOrder) {
		// On the IVOA format order/p1-p2 p3 p4 order2/p1-p2
		RangeTree tree = new RangeTree();
		
		String[] orderSplit = pixelString.split("/");
		int nOrders = orderSplit.length - 1;
		
		if(nOrders < 1) {
			return null;
		}
		
		String[] list = orderSplit[0].split(" ");
		int order = Integer.parseInt(list[list.length - 1]);
		for(int i = 1; i <= nOrders; i++) {
			list = orderSplit[i].split(" ");
			String[] list2;
			if(i < nOrders) {
				list2 = Arrays.copyOfRange(list, 0, list.length - 1);
			}else {
				list2 = Arrays.copyOfRange(list, 0, list.length);
			}
			pixelInOrderListToRangeTree(list2 ,order, maxOrder, tree);
			if(i < nOrders) {
				order = Integer.parseInt(list[list.length - 1]);
			}

		}
		return tree;
	}

	private static String pixelInOrderListToJsonString(String[] list) {
		
		String orderString = "[";
		for(int j = 0; j < list.length; j++) {
			String val = list[j];
			if(val.contains("-")) {
				String[] split = val.split("-");
				int start = Integer.parseInt(split[0]);
				int end = Integer.parseInt(split[1]);
				for(int k = start; k<= end; k++) {
					orderString += Integer.toString(k) + ",";
				}
			}else {
				orderString += val + ",";
			}
		}
		orderString = orderString.substring(0, orderString.length() - 1 ) + "]";
		return orderString;
	}
	
	private static RangeTree pixelInOrderListToRangeTree(String[] list, int order, int maxOrder, RangeTree tree) {
		
		for(int j = 0; j < list.length; j++) {
			String val = list[j];
			if(val.contains("-")) {
				String[] split = val.split("-");
				long start = Long.parseLong(split[0]) << (maxOrder - order) * 2;
				long end = Long.parseLong(split[1]) << (maxOrder - order) * 2;
				tree.add(start, end);
			}else {
				long start = Long.parseLong(val) << (maxOrder - order) * 2;
				long end = start;
				tree.add(start, end);
			}
		}
		return tree;
	}
	
	public static String objectToAsciiString(GeneralJavaScriptObject obj) {
		String[] orderArray = obj.getProperties().split(",");
    	String mocString = "";
    	
    	for(String orderString : orderArray) {
    		RangeTree rangeTree = new RangeTree();
        	String[] pixelArray = GeneralJavaScriptObject.convertToString(obj.getProperty(orderString)
        			.invokeFunction("toString")).split(",");
        	for(String pixelString : pixelArray){
        		long pixel = Long.parseLong(pixelString);
        		long start = pixel;
        		long end = pixel + 1;
        		rangeTree.add(start, end);
        	}
        	mocString += orderString +"/";
        	for(Interval i : rangeTree.getTree()) {
        		if(i.getStart() == i.getEnd() - 1) {
        			mocString += i.getStart();
        		}else {
        			mocString += i.getStart() + "-" + (i.getEnd() - 1);
        		}
        		mocString += " ";
        	}
        	mocString = mocString.substring(0,mocString.length() -1) + " ";
    	}
    	
    	return mocString;
	}

}
