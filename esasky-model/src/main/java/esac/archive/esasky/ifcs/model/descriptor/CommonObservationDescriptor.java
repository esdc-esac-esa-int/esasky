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
