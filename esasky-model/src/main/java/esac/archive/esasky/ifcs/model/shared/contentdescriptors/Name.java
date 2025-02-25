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

package esac.archive.esasky.ifcs.model.shared.contentdescriptors;

public enum Name implements IContentDescriptor {

    RA("ra"),
    S_RA("s_ra"),
    DEC("dec"),
    S_DEC("s_dec"),
    REGION("region"),
    S_REGION("s_region");

    private final String value;

    Name(String value) {
        this.value = value;
    }

    @Override
    public boolean matches(String str) {
        return str.equalsIgnoreCase(value);
    }

    @Override
    public String getValue() {
        return value;
    }
}
