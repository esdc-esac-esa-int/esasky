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

package esac.archive.esasky.cl.web.client.status;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;

public class CountDetails {

    private Integer count;
    private Long updateTime;
    private SkyViewPosition skyViewPosition;
    private SearchArea searchArea;
    private boolean markedForRemoval;

    public CountDetails(Integer count) {
        super();
        this.count = count;
        this.markedForRemoval = false;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

	public SkyViewPosition getSkyViewPosition() {
		return skyViewPosition;
	}

	public void setSkyViewPosition(SkyViewPosition skyViewPosition) {
		this.skyViewPosition = skyViewPosition;
	}

    public void setSearchArea(SearchArea searchArea) {
        this.searchArea = searchArea;
    }

    public SearchArea getSearchArea() {
        return this.searchArea;
    }

    public void markForRemoval() {
        this.markedForRemoval = true;
    }

    public boolean isMarkedForRemoval() {
        return this.markedForRemoval;
    }
}
