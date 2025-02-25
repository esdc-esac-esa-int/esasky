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

package esac.archive.esasky.cl.web.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SingleCount {

    String tableName;
    Integer count;
    Boolean isApprox;
      
	protected SingleCount() {}

	@JsonProperty("res_table_name")
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    @JsonProperty("res_curr_count")
    public Integer getCount() {
        return count;
    }
    
    public void setCount(Integer count) {
        this.count = count;
    }
    
    @JsonProperty("res_count_approx")
    public Boolean IsApprox() {
        return isApprox;
    }
    
    public void setIsApprox(Boolean isApprox) {
        this.isApprox = isApprox;
    }
}
