package esac.archive.esasky.cl.web.client.utility;

import java.util.ArrayList;
import java.util.List;

public class RangeTree {

	private final static int DISJOINT_LOWER = -3;
	private final static int OVERLAP_LOWER = -2;
	private final static int CONTAINED_SMALLER = 0;
	private final static int CONTAINED_LARGER = 1;
	private final static int OVERLAP_HIGHER = 2;
	private final static int DISJOINT_HIGHER = 3;
	
	private List<Interval> tree = new ArrayList<>();
	
	public void add(long start, long end) {
		Interval a = new Interval(start, end);
		insertInTree(a);
	}
	
	private void insertInTree(Interval a) {
		for(Interval b : tree) {
			switch(a.compareTo(b)) {
				case DISJOINT_LOWER:
					int index = tree.indexOf(b);
					tree.add(index, a);
					return;
				case OVERLAP_LOWER:
					b.setStart(a.start);
					return;
				case CONTAINED_SMALLER:
					return;
				case CONTAINED_LARGER:
					tree.remove(b);
					break;
				case OVERLAP_HIGHER:
					tree.remove(b);
					a.setStart(b.end);
					break;
				default:
					break;
			}
		}
		tree.add(a);
	}
	
	public List<Interval> getTree(){
		return tree;
	}
	
	
	public static class Interval{
		private long start;
		private long end;
		
		Interval(long start, long end){
			this.start = start;
			this.end = end;
		}

		public long getStart() {
			return start;
		}

		public long getEnd() {
			return end;
		}
		
		public void setStart(long start) {
			this.start = start;
		}

		public void setEnd(long end) {
			this.end = end;
		}

		public int compareTo(Interval b) {
			if(this.start < b.start) {
				if(this.end < b.start) {
					return DISJOINT_LOWER;
				}
				if(this.end >= b.end ) {
					return CONTAINED_LARGER;
				}
				return OVERLAP_LOWER;
			}
			
			if(this.start > b.end) {
				return DISJOINT_HIGHER;
			}
			if(this.end <= b.end) {
				return CONTAINED_SMALLER;
			}
			return OVERLAP_HIGHER;
			
		}
		
		
	}
}
