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

package esac.archive.esasky.ifcs.model.shared;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RangeTreeTest {

	
	@Test
    public void testInner() {
	 	RangeTree tree = new RangeTree();
	 	tree.add(0,10);
	 	tree.add(2, 5);
	 	assertEquals("0-10", tree.toString());
	 }

	@Test
	public void testLeft() {
		RangeTree tree = new RangeTree();
		tree.add(0,10);
		tree.add(-2, 1);
		assertEquals("-2-10", tree.toString());
	}
	@Test
	public void testRight() {
		RangeTree tree = new RangeTree();
		tree.add(0,10);
		tree.add(8, 14);
		assertEquals("0-14", tree.toString());
	}
	@Test
	public void testOuter() {
		RangeTree tree = new RangeTree();
		tree.add(0,10);
		tree.add(-2, 12);
		assertEquals("-2-12", tree.toString());
	}
	@Test
	public void testDisjoint() {
		RangeTree tree = new RangeTree();
		tree.add(0,10);
		tree.add(-12, -5);
		tree.add(12, 20);
		assertEquals("-12--5,0-10,12-20", tree.toString());
	}
	@Test
	public void testOnBorder() {
		RangeTree tree = new RangeTree();
		tree.add(0,10);
		tree.add(10, 11);
		assertEquals("0-11", tree.toString());
	}
	 
}
