package esac.archive.esasky.ifcs.model.shared;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;


public class ESASkyResultMOC {
	protected LinkedList<ESASkyResultMOC> children;
	private int count;
	protected LinkedList<Integer> dataList;
	private long ipix;
	private int order;
	private Iterator<Integer> dataIter;
	 
	public ESASkyResultMOC() {
		children = new LinkedList<>();
		dataList = new LinkedList<>();
		count = 0;
	}
	
	public ESASkyResultMOC(int order, long ipix ) {
		count = 0;
		dataList = new LinkedList<>();
		children = new LinkedList<>();
		this.order = order;
		this.ipix = ipix;
	}

	public LinkedList<Integer> getDataList() {
		return dataList;
	}


	public void setDataList(LinkedList<Integer> dataList) {
		this.dataList = dataList;
	}

	public LinkedList<ESASkyResultMOC> getChildren() {
		return children;
	}
	
	public void setChildren(LinkedList<ESASkyResultMOC> children) {
		this.children = children;
	}
	
	public int getOrder() {
		return order;
	}

	public void setOrder(int norder) {
		this.order = norder;
	}

	public void setIpix(long npix) {
		this.ipix = npix;
	}
	
	public long getIpix() {
		return ipix;
	}

	public int getNextData() {
		if(dataIter == null) {
			dataIter = dataList.listIterator(0);
		}
		if(dataIter.hasNext()) {
			return dataIter.next();
		}else {
			return -1;
		}
	}

	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public void increaseCount() {
		count++;
	}
	
	public void addChild(ESASkyResultMOC child) {
		children.add(child);
	}
	
	public void clearData() {
		dataList.clear();
	}
	
	public String getMocString(int maxNorder) {
		Map<Integer, LinkedList<String>> map = new HashMap<>();
		loopNpix4StringMap(maxNorder, count, map);
		
		String line = "{";
		boolean firstNorder = true;
		for(int norder : map.keySet()) {
			if(!firstNorder) {
				line += ", ";
			}else {
				firstNorder = false;
			}
			line += "\"" + Integer.toString(norder) + "\":[";
			boolean first = true;
			for(String s : map.get(norder)) {
				if(!first) {
					line += ",";
				}else {
					first = false;
				}
				line += s;
			}
			line += "]";
		}
		
		line += "}";
		return line;
	}
	
	public void loopNpix4StringMap(int maxNorder, int maxCount, Map<Integer, LinkedList<String>> map) {
		if(getOrder() == maxNorder || children.size() == 0) {
			String ipixString = Long.toString(getIpix()) ;
			
			if(!map.containsKey(getOrder())) {
				
				LinkedList<String> list = new LinkedList<>();
				map.put(order, list);
			}
			map.get(order).add(ipixString);
		}else {
			for(ESASkyResultMOC child : children) {
				child.loopNpix4StringMap(maxNorder, maxCount, map);
			}
		}
	}
	
	public void addData(int newDataIndex, int nOrder, int ipix) {
		
		LinkedList<Long> parents = getParents(3, nOrder, ipix);
		Iterator<Long> i = parents.descendingIterator();
		ESASkyResultMOC currentObject = this;
		
		while(i.hasNext()){
			long parentIpix = i.next();
			
			boolean found = false;
			for(ESASkyResultMOC child : currentObject.getChildren()) {
				if(child.ipix == parentIpix) {
					found = true;
					currentObject.count++;
					currentObject = child;
					break;
				}
			}
			if(!found) {
				ESASkyResultMOC child = new ESASkyResultMOC(currentObject.order + 1, parentIpix);
				currentObject.children.add(child);
				currentObject.count++;
				currentObject = child;
			}
		}
		
		currentObject.count++;
		currentObject.dataList.add(newDataIndex);
		
	}
	
	public void addData(int nOrder, int ipix, int count, boolean whatever) {
		LinkedList<Long> parents = getParents(3, nOrder, ipix);
		Iterator<Long> i = parents.descendingIterator();
		ESASkyResultMOC currentObject = this;
		while(i.hasNext()){
			long parentIpix = i.next();
			
			boolean found = false;
			for(ESASkyResultMOC child : currentObject.getChildren()) {
				if(child.ipix == parentIpix) {
					found = true;
					currentObject.count += count;
					currentObject = child;
					break;
				}
			}
			if(!found) {
				ESASkyResultMOC child = new ESASkyResultMOC(currentObject.order + 1, parentIpix);
				currentObject.children.add(child);
				currentObject.count += count;
				currentObject = child;
			}
		}
		
		currentObject.count += count;
		
	}
	
	public void addData(ESASkyResultMOC newData) {
		if(newData.getOrder() == order) {
			if(ipix == newData.getIpix()) {
				
				
				for(ESASkyResultMOC newChild : newData.getChildren()) {
					addData(newChild);
				}
			}
			else {
				Log.error("[ESASkyResultMOC] Adding data to sibling; NOrder: " + Integer.toString(order)
				+ " Npix: " + Long.toString(ipix) + " newDataNpix: " + Long.toString(newData.getIpix())); 
			}
			return;
		}
		
		if(newData.getOrder() == order + 1) {
			for(ESASkyResultMOC child : children) {
				if(child.getIpix() == newData.getIpix()) {
					child.addData(newData);
					return;
				}
			}
			children.add(newData);
			return;
		}
		
		if(newData.getOrder() > order + 1) {
			long newDataNpix = getParentNpix(order + 1, newData.getOrder(), newData.getIpix());
			for(ESASkyResultMOC child : children) {
				if(child.getIpix() == newDataNpix) {
					child.addData(newData);
					return;
				}
			}
			ESASkyResultMOC newChild = new ESASkyResultMOC(order + 1, newDataNpix);
			newChild.addData(newData);
			children.add(newChild);
			return;
		}
	}
	
	public void updateCount() {
		int newCount = 0;
		for(ESASkyResultMOC child : children) {
			child.updateCount();
			newCount += child.count;
		}
		newCount += dataList.size();
		if(newCount != 0) {
			setCount(newCount);
		}
	}
	
	public static long getParentNpix(int targetNOrder, int currentNOrder, long npix) {
		if(targetNOrder > currentNOrder) {
			return -1;
		}
		while(currentNOrder > targetNOrder) {
			npix = npix >>> 2;
			currentNOrder--;
		}
		return npix;
	}
	
	public void populateCountMap(Map<Integer, Map<Long, Integer>> countMap, int index, int minOrder, int maxOrder) {
		if(order >= minOrder) {
			if(children.size() == 4 || order == maxOrder || children.size() == 0) {
				
				if(countMap.containsKey(order)) {
						countMap.get(order).put(ipix, count);
					
				}else {
					Map<Long, Integer> pixMap = new HashMap<Long, Integer>();
					pixMap.put(ipix, count);
					countMap.put(order, pixMap);
				}
				
			}else if(order < maxOrder){
				for(ESASkyResultMOC child : children) {
					child.populateCountMap(countMap, index, minOrder, maxOrder);
				}
			}
		}
		else if(children.size() == 0) {
			if(countMap.containsKey(order)) {
				countMap.get(order).put(ipix, count);
				
			}else {
				Map<Long, Integer> pixMap = new HashMap<Long, Integer>();
				pixMap.put(ipix, count);
				countMap.put(order, pixMap);
			}
		}
		else {
			for(ESASkyResultMOC child : children) {
				child.populateCountMap(countMap, index, minOrder, maxOrder);
			}
		}
	}
	
	public void fillListWithZerosUntilIndex(LinkedList<Integer> list, int index, int value) {
		for(int i = list.size(); i < index; i++) {
			list.add(0);
		}
		list.add(value);
	}
	
	public ESASkyResultMOC getGrandChild(int norder, long ipix) {
		LinkedList<Long> parents = new LinkedList<Long>();
		parents.add(ipix);
		Log.debug(Long.toString(ipix));
		
		for(int i = norder; i > getOrder() + 1; i--) {
			ipix = ipix >>> 2;
			parents.add(ipix);
		}
		return getGrandChild(parents);
	}
	
	public ESASkyResultMOC getGrandChild(LinkedList<Long> parents) {
		if(parents.isEmpty()) {
			return this;
		}
		
		long ipix = parents.getLast();
		for(ESASkyResultMOC child : children) {
			if(child.getIpix() == ipix) {
				parents.removeLast();
				return child.getGrandChild(parents);
			}
		}
		
		return null;
		
	}
	
	public LinkedList<Integer> getAllData(int norder, long ipix) {
		ESASkyResultMOC child = getGrandChild(norder, ipix);
		return child.getAllData();
	}
	
	public LinkedList<Integer> getAllData() {
		LinkedList<Integer> list = new LinkedList<Integer>();
		list.addAll(getDataList());
		for(ESASkyResultMOC child : children) {
			list.addAll(child.getAllData());
		}
		return list;
	}
	
	public static LinkedList<Long> getParents(int targetNorder, int childNorder, long ipix) {
	
		LinkedList<Long> parents = new LinkedList<Long>();
		parents.add(ipix);
		
		while(childNorder > targetNorder) {
			ipix = ipix >>> 2;
			parents.add(ipix);
			childNorder--;
		}
		
		return parents;
	}
	
	public static int getMinOrderFromFoV(double fov) {
		
		if(fov > 60) {
			return 4;
		}else if(fov > 40) {
			return 5;
		}else if(fov > 20) {
			return 6;
		}else if(fov > 5) {
			return 8;
		}else if(fov > 2) {
			return 9;
		}else if(fov > 1) {
			return 10;
		}else if(fov > 0.5) {
			return 12;
		}else {
			return 14;
		}			
	}
	
	public static int getMaxOrderFromFoV(double fov) {
		
		if(fov > 20) {
			return 8;
		}else if(fov > 5) {
			return 10;
		}else if(fov > 2) {
			return 12;
		}else {
			return 14;
		}					
	}

	public static int getTargetOrderFromFoV(double fov) {
		
		if(fov > 2) {
			return 8;
		}else if(fov > .5) {
			return 10;
		}else if(fov > .1) {
			return 12;
		}else {
			return 14;
		}					
	}
	
}
