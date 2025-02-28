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

public abstract class DescriptorList<T extends IDescriptor> implements IDescriptorList<T> {

    protected List<T> descriptors = new LinkedList<T>();
    private int total;

    @Override
    public List<T> getDescriptors() {
        return descriptors;
    }

    @Override
    public void setDescriptors(List<T> descriptors) {
        this.descriptors = descriptors;
    }

    @Override
    public int getTotal() {
        return total;
    }

    @Override
    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public T getDescriptorByMissionNameCaseInsensitive(String missionName) {
        for (T currentDesc : this.descriptors) {
            if (currentDesc.getMission().equalsIgnoreCase(missionName)) {
                return currentDesc;
            }
        }
        return null;
    }
}
