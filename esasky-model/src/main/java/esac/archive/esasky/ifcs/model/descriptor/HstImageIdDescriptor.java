package esac.archive.esasky.ifcs.model.descriptor;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HstImageIdDescriptor {
	
	private String id;
	private String title;
   
	@JsonCreator
	public HstImageIdDescriptor(@JsonProperty("id") String id, @JsonProperty("title") String title) {
	   this.id = id;
	   this.title = title;
	}
   
	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
}
