package esac.archive.esasky.ifcs.model.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContentDescriptorTest {

    private static final UCD UCD_RA = UCD.POS_EQ_RA;
    private static final String UCD_RA_STR = "pos.eq.ra";
    private static final String UCD_RA_MAIN_STR = "pos.eq.ra;meta.main";

    private static final IUType OBSCORE_RA = ObsCore.S_RA;
    private static final String OBSCORE_RA_NO_PREFIX_STR = "Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1";
    private static final String OBSCORE_RA_STR = "obscore:" + OBSCORE_RA_NO_PREFIX_STR;

    @Test
    void testUCDValue() {
        assertEquals(UCD_RA_STR, UCD_RA.getValue());
        assertEquals(UCD_RA_STR, UCD_RA.toString());

    }

    @Test
    void testUCDNegative() {
        assertTrue(UCD_RA.negative().isNegative());
        assertFalse(UCD_RA.positive().isNegative());

    }

    @Test
    void testUCDMatches() {
        assertTrue(UCD_RA.positive().matches(UCD_RA_MAIN_STR));
        assertFalse(UCD_RA.negative().matches(UCD_RA_MAIN_STR));
    }

    @Test
    void testUCDMain() {
        assertTrue(UCD.isMain(UCD_RA_MAIN_STR));
        assertFalse(UCD.isMain(UCD_RA_STR));
    }


    @Test
    void testUTypeGetType() {
        assertEquals("obscore", OBSCORE_RA.getType());
    }

    @Test
    void testUTypeIsType() {
        assertTrue(OBSCORE_RA.isType(OBSCORE_RA_STR));
        assertFalse(OBSCORE_RA.isType(OBSCORE_RA_NO_PREFIX_STR));
    }

    @Test
    void testUTypeMatches() {
        assertFalse(OBSCORE_RA.matches(OBSCORE_RA_NO_PREFIX_STR));
        assertTrue(OBSCORE_RA.matches(OBSCORE_RA_STR));

    }
    @Test
    void testUTypeValue() {
        assertEquals(OBSCORE_RA_NO_PREFIX_STR, OBSCORE_RA.getValue());
    }

}
