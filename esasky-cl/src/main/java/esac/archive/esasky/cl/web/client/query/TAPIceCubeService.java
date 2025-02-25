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

package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

public class TAPIceCubeService extends AbstractTAPService {

    private static TAPIceCubeService instance = null;

    private TAPIceCubeService() {
    }

    public static TAPIceCubeService getInstance() {
        if (instance == null) {
            instance = new TAPIceCubeService();
        }
        return instance;
    }


    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptorInput) {
        return getMetadataAdql(descriptorInput, "");
    }

    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptor, String filter) {
        return "SELECT * from " + descriptor.getTableName() + " order by discovery_timestamp desc";
    }

    @Override
    public String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition conePos) {
        return null;
    }

}