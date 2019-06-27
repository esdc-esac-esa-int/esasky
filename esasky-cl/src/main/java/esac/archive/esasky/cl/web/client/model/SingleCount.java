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
