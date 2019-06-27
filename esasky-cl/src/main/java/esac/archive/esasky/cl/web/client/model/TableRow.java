package esac.archive.esasky.cl.web.client.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author ESDC team Copyright (c) 2016- European Space Agency
 */
public class TableRow implements ShapeId{

    private int rowId = -1;

    /** List of tableElements. */
    private List<TableElement> elements = new LinkedList<TableElement>();

    /**
     * Class constructor().
     */
    public TableRow() {
    }

    public TableRow(final int rowId) {
        this.rowId = rowId;
    }

    @Override
    public final int getShapeId() {
        return rowId;
    }

    /**
     * getElements().
     * @return List<TableElement>
     */
    public final List<TableElement> getElements() {
        return elements;
    }

    /**
     * setElements().
     * @param inputElements Input List<TableElement>
     */
    public final void setElements(final List<TableElement> inputElements) {
        this.elements = inputElements;
    }

    /**
     * getElementByLabel().
     * @param label Input String
     * @return TableElement
     */
    public final TableElement getElementByLabel(final String label) {
        for (TableElement currElem : elements) {
            if (currElem.getLabel().equals(label)) {
                return currElem;
            }
        }
        return null;
    }
    
    public final TableElement getElementContainingLabel(final String label) {
        for (TableElement currElem : elements) {
            if (currElem.getLabel().toLowerCase().contains(label.toLowerCase())) {
                return currElem;
            }
        }
        return null;
    }
    
    /**
     * getElementByTapName().
     * @param tapName Input String
     * @return TableElement
     */
    public final TableElement getElementByTapName(final String tapName) {
        for (TableElement currElem : elements) {
            if (currElem.getTapName().equals(tapName)) {
                return currElem;
            }
        }
        return null;
    }
    
}
