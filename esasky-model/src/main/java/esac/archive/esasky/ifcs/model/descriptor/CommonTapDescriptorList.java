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

package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

public class CommonTapDescriptorList  {

    @JsonProperty("descriptors")
    private List<CommonTapDescriptor> descriptors = new LinkedList<>();

    public List<CommonTapDescriptor> getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(List<CommonTapDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    @JsonIgnore
    public int getTotal() {
        return descriptors.size();
    }
}
