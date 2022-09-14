package esac.archive.esasky.cl.web.client.model;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.OpenSeaDragonActiveEvent;
import esac.archive.esasky.cl.web.client.event.TargetDescriptionEvent;
import esac.archive.esasky.cl.web.client.query.TAPImageListService;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper.OpenSeaDragonType;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ImageDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.OutreachImageDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.Objects;

public class OutreachImage {
	
	private String id;
	private String title;
	private String description;
	private String credits;
	private String baseUrl;
	private double opacity = 1.0;
	private boolean removed = false;
	private OpenSeaDragonWrapper lastOpenseadragon = null;
	private final String mission;
	
	public OutreachImage(String id, double opacity, ImageDescriptor descriptor) {
		this.id = id;
		this.opacity = opacity;
		this.baseUrl = descriptor.getBaseUrl() + id;
		this.mission = descriptor.getMission();
	}

	public OutreachImage(GeneralJavaScriptObject imageObject, double opacity, String mission) {
		this.opacity = opacity;

		OutreachImageDescriptorMapper mapper = GWT.create(OutreachImageDescriptorMapper.class);
		String newJson = imageObject.jsonStringify().replace("\"[", "[").replace("]\"", "]");
		OutreachImageDescriptor desc = mapper.read(newJson);
		onResponseParsed(desc, mission, true);
		this.mission = mission;
	}
	
	public interface OutreachImageDescriptorMapper extends ObjectMapper<OutreachImageDescriptor> {}

	public void loadImage(IDescriptor descriptor, TAPImageListService metadataService) {
		loadImage(descriptor, metadataService, true);
	}
	
	public void loadImage(IDescriptor descriptor, TAPImageListService metadataService, boolean moveToCenter) {
		String mission = this.mission;
		String query = descriptor.getTapQuery(metadataService.getRequestUrl(), metadataService.getImageMetadata(descriptor, this.id), EsaSkyConstants.JSON);
		JSONUtils.getJSONFromUrl(query , new IJSONRequestCallback() {

			@Override
			public void onSuccess(String responseText) {
				
				GeneralJavaScriptObject rawObject = GeneralJavaScriptObject.createJsonObject(responseText);
				GeneralJavaScriptObject[] metadata = GeneralJavaScriptObject.convertToArray(rawObject.getProperty("metadata"));
				GeneralJavaScriptObject[] data =  GeneralJavaScriptObject.convertToArray(GeneralJavaScriptObject.convertToArray(rawObject.getProperty("data"))[0]);
				
				GeneralJavaScriptObject newObj = GeneralJavaScriptObject.createJsonObject("{}");
				for(int i = 0; i < metadata.length; i++) {
					String metaName = metadata[i].getStringProperty("name");
					newObj.setProperty(metaName, data[i].toString());
				}
				
				OutreachImageDescriptorMapper mapper = GWT.create(OutreachImageDescriptorMapper.class);
				String newJson = newObj.jsonStringify().replace("\"[", "[").replace("]\"", "]");
				OutreachImageDescriptor desc = mapper.read(newJson);
			
				onResponseParsed(desc, mission, moveToCenter);

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

	public void reattachOpenSeaDragon(){
		removed = false;
		CommonEventBus.getEventBus().fireEvent(new OpenSeaDragonActiveEvent(true));
//		loadImage();
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public String getId() {
		return id;
	}
	
	public void onResponseParsed(OutreachImageDescriptor desc, String mission, boolean moveToCenter) {

		
		ImageSize imageSize = new ImageSize(desc.getPixelSize()[0], desc.getPixelSize()[1]);
		Coordinate coor = new Coordinate(desc.getRa(), desc.getDec());
		
		String url = desc.getTilesUrl();
		OpenSeaDragonType type = OpenSeaDragonType.TILED;
		if(url == null || imageSize.getHeight() < 1000 || imageSize.getWidth() < 1000) {
			url = desc.getLargeUrl();
			type = OpenSeaDragonType.SINGLE;
		}
		
		this.id = desc.getId();

		boolean isHst = Objects.equals(mission, EsaSkyConstants.HST_MISSION);
		if (isHst) {
			this.baseUrl = "https://esahubble.org/images/" + id;
		} else {
			this.baseUrl = "https://esawebb.org/images/" + id;
		}
		this.title = desc.getTitle();
		this.description = desc.getDescription();
		this.credits = desc.getCredit();
		
		OpenSeaDragonWrapper openseadragonWrapper = new OpenSeaDragonWrapper(this.id, url, type,
				coor.getRa(), coor.getDec(), desc.getFovSize(), desc.getRotation(), imageSize.getWidth(), imageSize.getHeight());
		lastOpenseadragon = openseadragonWrapper;
		JavaScriptObject openSeaDragonObject = openseadragonWrapper.createOpenSeaDragonObject();
		openseadragonWrapper.addOpenSeaDragonToAladin(openSeaDragonObject);
		
		if(moveToCenter) {
			
			SkyViewPosition curPos = CoordinateUtils.getCenterCoordinateInJ2000();
			
			double dist = curPos.getCoordinate().distance(coor);
			
			if(curPos.getFov() / desc.getFovSize() > 5 || desc.getFovSize() / curPos.getFov() < .2 || dist > curPos.getFov() / 2) {
				AladinLiteWrapper.getAladinLite().goToRaDec(Double.toString(coor.getRa()), Double.toString(coor.getDec()));
				AladinLiteWrapper.getAladinLite().setZoom(desc.getFovSize() * 3);
			}
		}
		
		Timer timer = new Timer() {

			@Override
			public void run() {
				AladinLiteWrapper.getAladinLite().setOpenSeaDragonOpacity(opacity);
				AladinLiteWrapper.getAladinLite().requestRedraw();
			}
			
		};
		timer.schedule(100);
		AladinLiteWrapper.getAladinLite().setOpenSeaDragonOpacity(opacity);

		StringBuilder popupText = new StringBuilder(this.description);
		popupText.append("<br>  Credit: ");
		popupText.append(this.credits);

		popupText.append("<br><br> This image on <a target=\"_blank\" href=\" " + this.baseUrl + (isHst ? "\">ESA Hubble News</a>" : "\">ESA Webb News</a>"));
		
		CommonEventBus.getEventBus().fireEvent(
        		new TargetDescriptionEvent(this.title, popupText.toString(), false));
		CommonEventBus.getEventBus().fireEvent(new OpenSeaDragonActiveEvent(true));
		if (removed) {
			removeOpenSeaDragon();
		}

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
}
