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

package esac.archive.esasky.ifcs.model.client;

import java.util.LinkedList;
import java.util.List;

public class HiPSPlayer {

    private List<HiPSPlayerEntry> hipsEntries = new LinkedList<HiPSPlayerEntry>();
    private int total;

    public List<HiPSPlayerEntry> getHipsEntries() {
        return hipsEntries;
    }

    public void setHipsEntries(List<HiPSPlayerEntry> hipsEntries) {
        this.hipsEntries = hipsEntries;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
