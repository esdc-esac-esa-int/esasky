package esac.archive.esasky.ifcs.model.descriptor;


/**
 * @author ESDC team Copyright (c) 2017 - European Space Agency
 */
public abstract class CommonObservationDescriptor extends BaseDescriptor {


    /** MOC DB table name. */
    private String mocTapTable;
    /** MOC Tap DB STC_S column name. */
    private String mocSTCSColumn;

    /**
     * getMocTapTable().
     * @return String
     */
    public final String getMocTapTable() {
        return mocTapTable;
    }

    /**
     * setMocTapTable().
     * @param inputMocTapTable Input String
     */
    public final void setMocTapTable(final String inputMocTapTable) {
        this.mocTapTable = inputMocTapTable;
    }

    /**
     * getMocSTCSColumn().
     * @return String.
     */
    public final String getMocSTCSColumn() {
        return mocSTCSColumn;
    }

    /**
     * setMocSTCSColumn().
     * @param inputMocSTCSColumn Input String.
     */
    public final void setMocSTCSColumn(final String inputMocSTCSColumn) {
        this.mocSTCSColumn = inputMocSTCSColumn;
    }

    @Override
    public String getTapRaColumn() {
        return tapRaColumn == null ? "ra_deg": tapRaColumn;
    }

    @Override
    public String getTapDecColumn() {
        return tapDecColumn == null ? "dec_deg": tapDecColumn;
    }
    
    @Override
    public String getTapSTCSColumn() {
        return tapSTCSColumn == null ? "stc_s": tapSTCSColumn;
    }
    
    @Override
    public Boolean getUseIntersectPolygonInsteadOfContainsPoint() {
        return useIntersectPolygonInsteadOfContainsPoint == null ? true: useIntersectPolygonInsteadOfContainsPoint;
    }
}
