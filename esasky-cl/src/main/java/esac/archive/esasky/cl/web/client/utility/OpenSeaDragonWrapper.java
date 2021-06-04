package esac.archive.esasky.cl.web.client.utility;

import com.google.gwt.core.client.JavaScriptObject;

public class OpenSeaDragonWrapper {

	private double ra;
	private double dec;
	private double fov;
	private double rotation;
	
	private int width;
	private int height;
	
	private String id;
	private String url;
	private OpenSeaDragonType type;
	
	public OpenSeaDragonWrapper(String id, String url, OpenSeaDragonType type, double ra, double dec,
			double fov, double rotation, int width, int height) {
		this.ra = ra;
		this.dec = dec;
		this.fov = fov;
		this.rotation = rotation;
		
		this.width = width;
		this.height = height;
		
		this.id = id;
		this.url = url;
		this.type = type;
	}
	
	public  JavaScriptObject createOpenSeaDragonObject() {
		AladinLiteWrapper.getAladinLite().removeOpenSeaDragon("");
		return createOpenSeaDragonObject(id, url, type.getName(), ra, dec, fov, rotation, width, height);
	}
	
	public void addOpenSeaDragonToAladin(JavaScriptObject openSeaDragonObject) {
		AladinLiteWrapper.getAladinLite().removeOpenSeaDragon("");
		AladinLiteWrapper.getAladinLite().addOpenSeaDragon(openSeaDragonObject);
	}
	
	private native JavaScriptObject createOpenSeaDragonObject(String name, String url, String type, double ra, double dec,
			double fov, double rot, int width, int height)/*-{
		var openseadragon = $wnd.OpenSeadragon({
		    id: "openseadragonCanvas",
			maxZoomPixelRatio: 3,
		    animationTime: .01,
		    showFullPageControl: false,
		    showHomeControl: false,
		    showZoomControl: false,
		    tileSources: [{
		        type: type,
		        width: width,
		        height: height,
		        tilesUrl: url,
		    }]
		});
	    openseadragon.name = name;
	    openseadragon.fov = fov;
	    openseadragon.ra = ra;
	    openseadragon.dec = dec;
	    openseadragon.rot = rot;
	    openseadragon.whScale = width / height;
	    openseadragon.viewport.setRotation(rot); 
	    return openseadragon;
	}-*/;
	
	
	public enum OpenSeaDragonType {
	    
		TILED("zoomifytileservice"), SINGLE("image");

	    private String name;

	    OpenSeaDragonType(String name) {
	        this.name = name;
	    }

	    public String getName() {
	        return this.name;
	    }
	}
}
