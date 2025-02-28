/*
ESASky
Copyright (C) 2025 European Space Agency

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

package esac.archive.esasky.ifcs.model.shared;

import java.util.ArrayList;
import java.util.Iterator;
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
		for(Iterator<Interval> it = tree.iterator(); it.hasNext();) {
			Interval b = it.next();
			switch(a.compareTo(b)) {
				case DISJOINT_LOWER:
					int index = tree.indexOf(b);
					tree.add(index, a);
					return;
				case OVERLAP_LOWER:
					b.setStart(a.getStart());
					return;
				case CONTAINED_SMALLER:
					return;
				case CONTAINED_LARGER:
					it.remove();
					break;
				case OVERLAP_HIGHER:
					a.setStart(b.getStart());
					it.remove();
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Interval i : this.tree) {
			sb.append(Long.toString(i.start));
			if(i.start != i.end) {
				sb.append("-");
				sb.append(Long.toString(i.end));
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
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
