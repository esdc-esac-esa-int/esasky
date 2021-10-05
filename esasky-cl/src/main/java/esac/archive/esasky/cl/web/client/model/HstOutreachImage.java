package esac.archive.esasky.cl.web.client.model;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.OpenSeaDragonActiveEvent;
import esac.archive.esasky.cl.web.client.event.TargetDescriptionEvent;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper.OpenSeaDragonType;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.descriptor.HstImageDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class HstOutreachImage {
	
	private String id;
	private String title;
	private String description;
	private String credits;
	private String baseUrl;
	private double opacity = 1.0;
	private boolean removed = false;
	private OpenSeaDragonWrapper lastOpenseadragon = null;
	
	public HstOutreachImage(String id, double opacity) {
		this.id = id;
		this.opacity = opacity;
		this.baseUrl = "https://esahubble.org/images/" + id;
	}
	
	public interface HstImageDescriptorMapper extends ObjectMapper<HstImageDescriptor> {
	}

	
	public void loadImage() {
		loadImage(true);
	}
	public void loadImage(boolean moveToCenter) {
		String query = EsaSkyWebConstants.HST_IMAGE_URL + "?" + EsaSkyConstants.HST_IMAGE_ID_PARAM + "=" + this.id;
		JSONUtils.getJSONFromUrl(query , new IJSONRequestCallback() {

			@Override
			public void onSuccess(String responseText) {
				
				HstImageDescriptorMapper mapper = GWT.create(HstImageDescriptorMapper.class);

				HstImageDescriptor desc = mapper.read(responseText);
				
				ImageSize imageSize = new ImageSize(desc.getPixelSize().get(0),
						desc.getPixelSize().get(1));
				
				String url = desc.getTilesUrl();
				OpenSeaDragonType type = OpenSeaDragonType.TILED;
				if(imageSize.height < 1000 || imageSize.width < 1000) {
					url = desc.getLargeUrl();
					type = OpenSeaDragonType.SINGLE;
				}
				
				onResponseParsed(desc.getCoordinateMetadata().getCoordinate(),
						desc.getCoordinateMetadata().getFov(),
						desc.getCoordinateMetadata().getRotation(),
						imageSize,
						desc.getTitle(),
						desc.getDescription(),
						desc.getCredit(),
						type,
						url, moveToCenter);

		        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_IMAGES, GoogleAnalytics.ACT_IMAGES_HSTIMAGE_SUCCESS, desc.getId());
			}

			@Override
			public void onError(String errorCause) {
				Log.error(errorCause);
		        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_IMAGES, GoogleAnalytics.ACT_IMAGES_HSTIMAGE_FAIL, id);
			}
		});
	}
	
	public void removeOpenSeaDragon(){
		removed = true;
		CommonEventBus.getEventBus().fireEvent(new OpenSeaDragonActiveEvent(false));
		if(lastOpenseadragon != null) {
			lastOpenseadragon.removeOpenSeaDragonFromAladin();
		}
	}
	
	public void onResponseParsed(Coordinate coor, double fov, double rotation, ImageSize imageSize,
			String title, String description, String credits, OpenSeaDragonType type, String url, boolean moveToCenter) {
		if (removed) {
			return;
		}
		this.title = title;
		this.description = description;
		this.credits = credits;
		
		OpenSeaDragonWrapper openseadragon = new OpenSeaDragonWrapper(this.id, url, type,
				coor.getRa(), coor.getDec(), fov, rotation, imageSize.getWidth(), imageSize.getHeight());
		lastOpenseadragon = openseadragon;
		JavaScriptObject openSeaDragonObject = openseadragon.createOpenSeaDragonObject();
		openseadragon.addOpenSeaDragonToAladin(openSeaDragonObject);
		
		if(moveToCenter) {
			AladinLiteWrapper.getAladinLite().goToRaDec(Double.toString(coor.getRa()), Double.toString(coor.getDec()));
			AladinLiteWrapper.getAladinLite().setZoom(fov * 3);
		}
		
		Timer timer = new Timer() {

			@Override
			public void run() {
				AladinLiteWrapper.getAladinLite().setOpenSeaDragonOpacity(opacity);
			}
			
		};
		timer.schedule(200);
		AladinLiteWrapper.getAladinLite().setOpenSeaDragonOpacity(opacity);

		StringBuilder popupText = new StringBuilder(this.description);
		popupText.append("<br>  Credit: ");
		popupText.append(this.credits);			

		popupText.append("<br><br> This image on <a target=\"_blank\" href=\" " + this.baseUrl + "\">ESA Hubble News</a>");
		
		CommonEventBus.getEventBus().fireEvent(
        		new TargetDescriptionEvent(this.title, popupText.toString(), false));
		CommonEventBus.getEventBus().fireEvent(new OpenSeaDragonActiveEvent(true));

	}
	
	public double getOpacity() {
		return opacity;
	}

	public void setOpacity(double opacity) {
		if(removed) {
			return;
		}
		this.opacity = opacity;
		AladinLiteWrapper.getAladinLite().setOpenSeaDragonOpacity(opacity);
	}

	public class ImageSize{
		private int width;
		private int height;
		
		public ImageSize(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public void setHeight(int height) {
			this.height = height;
		}
	}
}
