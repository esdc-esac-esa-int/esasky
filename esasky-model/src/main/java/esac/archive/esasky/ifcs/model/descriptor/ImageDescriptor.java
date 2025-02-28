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


import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.Objects;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ImageDescriptor extends ObservationDescriptor {
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isHst() {
        return Objects.equals(this.getMission(), EsaSkyConstants.HST_MISSION);
    }
}
