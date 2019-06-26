package esac.archive.ammi.ifcs.model.client;

public enum HiPSCoordsFrame {
    GALACTIC("galactic"), EQUATORIAL("equatorial");

    /** class attribute. */
    private String cooFrame;

    /**
     * Class Constructor.
     * @param value Input String
     */
    private HiPSCoordsFrame(final String cooFrame) {
        this.cooFrame = cooFrame;
    }

    public String getName() {
        return this.cooFrame;
    }

    @Override
    public String toString() {
        return this.cooFrame;
    }
}
