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

package esac.archive.esasky.ifcs.model.coordinatesutils;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;

public enum CoordinatesFrame {

    J2000(AladinLiteConstants.FRAME_J2000), GALACTIC(AladinLiteConstants.FRAME_GALACTIC);

    private String cooframe;

    CoordinatesFrame(String cooFrame) {
        this.cooframe = cooFrame;
    }

    public String getValue() {
        return cooframe;
    }

}
