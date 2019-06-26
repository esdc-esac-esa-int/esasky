package esac.archive.esasky.cl.web.client.api.model;

import java.util.List;

public interface ISkyObject {

	public String getName();

	public void setName(String name);

	public List<MetadataAPI> getData();

	public void setData(List<MetadataAPI> data);

	Integer getId();

	void setId(Integer id);

}
