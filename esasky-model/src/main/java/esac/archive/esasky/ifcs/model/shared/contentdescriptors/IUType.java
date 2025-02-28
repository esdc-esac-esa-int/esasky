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

package esac.archive.esasky.ifcs.model.shared.contentdescriptors;

public interface IUType extends IContentDescriptor{
    String getValue();

    String getType();

    default String getTypeString() {
        return getType() + ":";

    }
    default boolean isType(String ucd) {
        return ucd.toLowerCase().startsWith(getType().toLowerCase());
    }

    default boolean matches(String str) {
        return isType(str) && str.replace(getTypeString(), "").equalsIgnoreCase(getValue());
    }

}
