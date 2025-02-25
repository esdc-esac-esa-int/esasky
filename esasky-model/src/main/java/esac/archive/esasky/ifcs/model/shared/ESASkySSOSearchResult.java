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

import java.util.LinkedList;
import java.util.List;

// TODO this class must be moved into ammi-ifcs-model
public class ESASkySSOSearchResult {

    ESASkySSOObjType ssoObjType;
    String name;
    List<String> aliases = new LinkedList<String>();

    public enum ESASkySSOObjType {
        ASTEROID("Asteroid"), COMET("Comet"), SATELLITE("Satellite"), SPACECRAFT("Spacecraft"), PLANET(
                "Planet"), DWARF_PLANET("Dwarf Planet"), SSO("Sso"), SPACEJUNK("Spacejunk"), EXOPLANET(
                        "Exoplanet"), STAR("Star");

        String type;

        ESASkySSOObjType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static ESASkySSOObjType fromValue(String value) {
            for (ESASkySSOObjType currType : ESASkySSOObjType.values()) {

                if (currType.getType().equals(value)) {
                    return currType;
                }
            }
            return null;
        }

    }

    public ESASkySSOSearchResult() {
        super();
    }

    public ESASkySSOObjType getSsoObjType() {
        return ssoObjType;
    }

    public void setSsoObjType(ESASkySSOObjType ssoObjType) {
        this.ssoObjType = ssoObjType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

}
