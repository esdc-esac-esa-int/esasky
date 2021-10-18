package esac.archive.esasky.ifcs.model.descriptor;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HstImageIdDescriptor {
	
	private String id;
	private String title;
	private String stcs;
	private String objectName;
	private double ra;
	private double dec;
	private int prio;
   
	@JsonCreator
	public HstImageIdDescriptor(@JsonProperty("id") String id, @JsonProperty("title") String title,
			 @JsonProperty("stcs") String stcs,  @JsonProperty("object_name") String objectName,
			 @JsonProperty("ra") double ra,  @JsonProperty("dec") double dec, @JsonProperty("prio") int prio) {
	   this.id = id;
	   this.title = title;
	   this.stcs = stcs;
	   this.objectName = objectName;
	   this.ra = ra;
	   this.dec = dec;
	   this.prio = prio;
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

	public String getStcs() {
		return stcs;
	}

	public void setStcs(String stcs) {
		this.stcs = stcs;
	}

	public String getObjectName() {
		return objectName;
	}

	public double getRa() {
		return ra;
	}

	public double getDec() {
		return dec;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public void setRa(double ra) {
		this.ra = ra;
	}

	public void setDec(double dec) {
		this.dec = dec;
	}

	public int getPrio() {
		return prio;
	}

	public void setPrio(int prio) {
		this.prio = prio;
	}
	
	
}
