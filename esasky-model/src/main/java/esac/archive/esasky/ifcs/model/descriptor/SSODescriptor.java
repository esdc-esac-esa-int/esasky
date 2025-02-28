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

import java.util.LinkedList;
import java.util.List;

public class SSODescriptor extends CommonObservationDescriptor {

	private List<MetadataDescriptor> ssoXMatchMetadata = new LinkedList<>();
    private String ssoCardReductionTapTable;

    private String ssoXMatchTapTable;
	
	@Override
	public final void setMetadata(final List<MetadataDescriptor> inputMetadata) {
		metadata = inputMetadata;
		metadata.addAll(ssoXMatchMetadata);
	}
	
    public final List<MetadataDescriptor> getSsoXMatchMetadata() {
        return ssoXMatchMetadata;
    }

    public final void setSsoXMatchMetadata(final List<MetadataDescriptor> inputMetadata) {
        this.ssoXMatchMetadata = inputMetadata;
    }

    @Override
    public String getIcon() {
        return "sso";
    }
    
    @Override
    public String getDescriptorId() {
        if(descriptorId == null || descriptorId.isEmpty()) {
            return "SSO_" + getMission();
        }
        return descriptorId;
    }
    
    public String getSsoCardReductionTapTable() {
        return ssoCardReductionTapTable;
    }

    public void setSsoCardReductionTapTable(String ssoCardReductionTapTable) {
        this.ssoCardReductionTapTable = ssoCardReductionTapTable;
    }

    public String getSsoXMatchTapTable() {
        return ssoXMatchTapTable;
    }

    public void setSsoXMatchTapTable(String ssoXMatchTapTable) {
        this.ssoXMatchTapTable = ssoXMatchTapTable;
    }
}
