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

package esac.archive.esasky.cl.web.client.query;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class TAPImageListService extends AbstractTAPService {

    private static TAPImageListService instance = null;

    private TAPImageListService() {
    }

    public static TAPImageListService getInstance() {
        if (instance == null) {
            instance = new TAPImageListService();
        }
        return instance;
    }

    
    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptorInput) {
    	return getMetadataAdql(descriptorInput, "");
    }
    
    @Override
    public String getMetadataAdql(CommonTapDescriptor descriptor, String filter) {
    	return "SELECT * from " + descriptor.getTableName() + " order by priority desc";
    }

	@Override
	public String getMetadataAdqlRadial(CommonTapDescriptor descriptor, SkyViewPosition conePos) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public String getImageMetadata(CommonTapDescriptor descriptor, String id) {
    	return "SELECT * from " + descriptor.getTableName() + " WHERE " + EsaSkyConstants.HST_IMAGE_ID_PARAM + " = '" + id + "'";
    }
    
}