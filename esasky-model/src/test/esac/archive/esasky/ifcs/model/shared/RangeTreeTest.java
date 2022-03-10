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
