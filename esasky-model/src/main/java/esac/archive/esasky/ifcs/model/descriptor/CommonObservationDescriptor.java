package esac.archive.esasky.ifcs.model.descriptor;


/**
 * @author ESDC team Copyright (c) 2017 - European Space Agency
 */
public abstract class CommonObservationDescriptor extends BaseDescriptor {


    /** Tap DB ObsId column name. */
    private String tapObservationId;

    /** MOC DB table name. */
    private String mocTapTable;
    /** MOC Tap DB STC_S column name. */
    private String mocSTCSColumn;

    /** Count limit below which we are going to display real data querying the tabTable. */
    private int mocLimit;

    private String ddProductURI;

    /** DD product URL. */
    private String ddProductIDParameter;

    /** DD Product ID. */
    private String ddProductIDColumn;
    
	private String ssoCardReductionTapTable;

    private String ssoXMatchTapTable;

    /**
     * getTapObservationId().
     * @return String.
     */
    public final String getTapObservationId() {
        return tapObservationId;
    }

    /**
     * tapObservationId().
     * @param inputTapObservationId Input String
     */
    public final void setTapObservationId(final String inputTapObservationId) {
        this.tapObservationId = inputTapObservationId;
    }

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

    /**
     * getMocLimit().
     * @return int
     */
    public final int getMocLimit() {
        return mocLimit;
    }

    /**
     * setMocLimit().
     * @param inputMocLimit input int
     */
    public final void setMocLimit(final int inputMocLimit) {
        this.mocLimit = inputMocLimit;
    }

    public String getDdProductURI() {
        return ddProductURI;
    }

    public void setDdProductURI(String ddProductURI) {
        this.ddProductURI = ddProductURI;
    }

    /**
     * getDdProductIDParameter().
     * @return String.
     */
    public final String getDdProductIDParameter() {
        return ddProductIDParameter;
    }

    /**
     * setDdProductIDParameter().
     * @param inputDDProductIDParameter Input String
     */
    public final void setDdProductIDParameter(final String inputDDProductIDParameter) {
        this.ddProductIDParameter = inputDDProductIDParameter;
    }

    /**
     * getDdProductIDColumn().
     * @return String.
     */
    public final String getDdProductIDColumn() {
        return ddProductIDColumn;
    }

    /**
     * setDdProductIDColumn().
     * @param inputDDProductIDColumn Input String
     */
    public final void setDdProductIDColumn(final String inputDDProductIDColumn) {
        this.ddProductIDColumn = inputDDProductIDColumn;
    }

    public String getSsoCardReductionTapTable() {
        return ssoCardReductionTapTable;
    }

    public void setSsoCardReductionTapTable(String ssoCardReductionTapTable) {
        this.ssoCardReductionTapTable = ssoCardReductionTapTable;
    }

    public String getSsoXMatchTapTable() {
        return ssoXMatchTapTable;
    }

    public void setSsoXMatchTapTable(String ssoXMatchTapTable) {
        this.ssoXMatchTapTable = ssoXMatchTapTable;
    }
    
    public String getUniqueIdentifierField(){
    	return tapObservationId;
    }
    
    public void setUniqueIdentifierField(String field){
    	tapObservationId = field;
    }
    
    @Override
    public String getTapRaColumn() {
        return "ra_deg";
    }

    @Override
    public String getTapDecColumn() {
        return "dec_deg";
    }

}
