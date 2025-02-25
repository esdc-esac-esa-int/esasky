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

public enum ColumnType {
    DATALINK("datalink"), LINK("link"), LINKLIST("linklist"), STRING("string"), CHAR("char"), VARCHAR("char"),  LINK2ARCHIVE("link2archive"), DOWNLOAD("download"), RA(
            "ra"), DEC("dec"), DOUBLE("double"), FLOAT("double"), INTEGER("integer"), LONG("long"), INT("int"), DATETIME("datetime"), TIMESTAMP("timestamp");

    private String type;

    private ColumnType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
