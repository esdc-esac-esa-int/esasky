package esac.archive.esasky.cl.web.client.model;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;

import esac.archive.esasky.cl.web.client.CommonEventBus;
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
	
	public HstOutreachImage(String id) {
		this.id = id;
		this.baseUrl = "https://esahubble.org/images/" + id;
	}
	
	public interface HstImageDescriptorMapper extends ObjectMapper<HstImageDescriptor> {
	}

	
	public void getPropertiesFromBackend() {
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
						url);

		        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_IMAGES, GoogleAnalytics.ACT_Images_hstImage_Success, desc.getId());
			}

			@Override
			public void onError(String errorCause) {
				Log.error(errorCause);
		        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_IMAGES, GoogleAnalytics.ACT_Images_hstImage_Fail, id);
			}
		});
	}
	
	public void onResponseParsed(Coordinate coor, double fov, double rotation, ImageSize imageSize,
			String title, String description, String credits, OpenSeaDragonType type, String url) {
		this.title = title;
		this.description = description;
		this.credits = credits;
		
		OpenSeaDragonWrapper openseadragon = new OpenSeaDragonWrapper(this.id, url, type,
				coor.ra, coor.dec, fov, rotation, imageSize.getWidth(), imageSize.getHeight());
		JavaScriptObject openSeaDragonObject = openseadragon.createOpenSeaDragonObject();
		openseadragon.addOpenSeaDragonToAladin(openSeaDragonObject);
		
		AladinLiteWrapper.getAladinLite().goToRaDec(Double.toString(coor.ra), Double.toString(coor.dec));
		AladinLiteWrapper.getAladinLite().setZoom(fov * 3);
		
		Timer timer = new Timer() {

			@Override
			public void run() {
				AladinLiteWrapper.getAladinLite().setOpenSeaDragonOpacity(opacity);
			}
			
		};
		timer.schedule(200);

		StringBuilder popupText = new StringBuilder(this.description);
		popupText.append("<br>  Credit: ");
		popupText.append(this.credits);			

		popupText.append("<br><br> This image on <a target=\"_blank\" href=\" " + this.baseUrl + "\">ESA Hubble News</a>");
		
		CommonEventBus.getEventBus().fireEvent(
        		new TargetDescriptionEvent(this.title, popupText.toString()));
	}
	
	public double getOpacity() {
		return opacity;
	}

	public void setOpacity(double opacity) {
		this.opacity = opacity;
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
