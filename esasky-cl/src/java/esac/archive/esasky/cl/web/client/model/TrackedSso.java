package esac.archive.esasky.cl.web.client.model;

import esac.archive.ammi.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;

public class TrackedSso {
	public final String name;
	public final ESASkySSOObjType type;
	public final int id;
	
	public TrackedSso(String name, ESASkySSOObjType type, int id) {
		this.name = name;
		this.type = type;
		this.id = id;
	}
}