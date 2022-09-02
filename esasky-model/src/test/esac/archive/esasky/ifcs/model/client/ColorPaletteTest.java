package esac.archive.esasky.ifcs.model.client;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import esac.archive.esasky.ifcs.model.shared.ColorPalette;


public class ColorPaletteTest {

	@Test
    public void convertPointGalacticToJ2000_returnsCorrectCoord() {
		HiPS hips = new HiPS();
		for(ColorPalette color : ColorPalette.values()) {
			hips.setColorPalette(color);
			if(color.equals(ColorPalette.GREYSCALE_INV)) {
				hips.setReversedColorMap(true);
			}
		}
		assertTrue(hips.isReversedColorMap());
    }
	
}
