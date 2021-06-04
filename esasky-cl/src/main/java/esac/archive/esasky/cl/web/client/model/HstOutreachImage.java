package esac.archive.esasky.cl.web.client.model;

import java.io.IOException;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.TargetDescriptionEvent;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper.OpenSeaDragonType;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.ClientRegexClass;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesParser;

public class HstOutreachImage {
	
	private String id;
	private String title;
	private String description;
	private String credits;

	private String baseUrl;
	private String tilesUrl;
	
	private double opacity = 1.0;
	
	public HstOutreachImage(String id) {
		this.id = id;
		this.baseUrl = "https://esahubble.org/images/" + id;
		this.tilesUrl = "https://cdn.spacetelescope.org/archives/images/zoomable/" + id +"/";
	}
	
	public void parseHstPageForProperties() {
		RequestCallback callback = new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {
				String text = response.getText();
				try {
					
					Coordinate coor = parseCoords(text);
					ImageSize imageSize = parseSizes(text);
					double fov = parseFov(text);
					double rotation = parseRotation(text);
					
					String title = parseTitle(text);
					String description = parseDescription(text);
					String credits = parseCredits(text);
					
					onResponseParsed(coor, fov, rotation, imageSize, title, description, credits);
				}catch (IOException e) {
					Log.error(e.getMessage(), e);
					return;
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				Log.error(exception.getMessage(), exception);
			}
		};
		
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, this.baseUrl);
        try {
            builder.sendRequest(null, callback);
        } catch (RequestException e) {
            Log.error(e.getMessage(), e);
            Log.error("[getJSONFromUrl] Error fetching JSON data from server");
        }
	}
	
	
	public void onResponseParsed(Coordinate coor, double fov, double rotation, ImageSize imageSize,
			String title, String description, String credits) {
		this.title = title;
		this.description = description;
		this.credits = credits;
		
		OpenSeaDragonWrapper openseadragon = new OpenSeaDragonWrapper(this.id, this.tilesUrl, OpenSeaDragonType.TILED,
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
	
	public Coordinate parseCoords(String text) throws IOException {
		Coordinate coor = null;
		try {
			ClientRegexClass regex = new ClientRegexClass();
			GeneralJavaScriptObject[] raArray = GeneralJavaScriptObject.convertToArray(regex.match("Position \\(RA\\):<\\/td><td>(\\d+\\s+\\d+\\s+\\d+\\.*\\d*)",text));
			String raString = raArray[1].toString();
			
			GeneralJavaScriptObject[] decArray = GeneralJavaScriptObject.convertToArray(regex.match("Position \\(Dec\\):<\\/td><td>(\\-?\\d+&deg \\d+\' \\d+\\.*\\d+\")",text));
			String decString = decArray[1].toString().replace("&deg", "").replace("'", "").replace("\"", "");
			if (!decString.startsWith("-")) {
				decString = "+" + decString;
			}
			double[] raDec = CoordinatesParser.parseCoords(regex, raString + " " + decString, CoordinatesFrame.J2000);
			coor = new Coordinate(raDec[0], raDec[1]);
			
		}catch(Exception e ) {
			String errorMsg = "Error parsing coordinates from HST, due to: " + e.getMessage();
			throw new IOException(errorMsg, e);
		}
		return coor;
	}
	
	public ImageSize parseSizes(String text) throws IOException {
		ImageSize imageSize = null;
		try {
			ClientRegexClass regex = new ClientRegexClass();
			GeneralJavaScriptObject[] sizes = GeneralJavaScriptObject.convertToArray(regex.match("<td>(\\d+)\\sx\\s(\\d+)\\s+px",text));
			int width = Integer.parseInt(sizes[1].toString());
			int height = Integer.parseInt(sizes[2].toString());
			
			imageSize = new ImageSize(width, height);
			
		}catch(Exception e ) {
			String errorMsg = "Error parsing images sizes from HST, due to: " + e.getMessage();
			throw new IOException(errorMsg, e);
		}
		return imageSize;
	}

	public double parseFov(String text) throws IOException{
		double fov = 0.0;
		try {
			ClientRegexClass regex = new ClientRegexClass();
			GeneralJavaScriptObject[] fovArray = GeneralJavaScriptObject.convertToArray(regex.match("Field of view:</td><td>(\\d+\\.*\\d+) x (\\d+\\.*\\d+) (arcminutes|arcseconds|degrees)",text));
			fov = Double.parseDouble(fovArray[1].toString());
			String type = fovArray[3].toString();
			if("arcseconds".equalsIgnoreCase(type)) {
				fov = fov / 3600.0;
			}
			else if("arcminutes".equalsIgnoreCase(type)) {
				fov = fov / 60.0;
			}
			
		}catch(Exception e ) {
			String errorMsg = "Error parsing fov from HST, due to: " + e.getMessage();
			throw new IOException(errorMsg, e);
		}
		return fov;
	}

	public double parseRotation(String text) throws IOException{
		double rotation = 0.0;
		try {
			ClientRegexClass regex = new ClientRegexClass();
			GeneralJavaScriptObject[] rotArray = GeneralJavaScriptObject.convertToArray(regex.match("Orientation:</td><td>North is (\\d+\\.?\\d+)&deg; (right|left)",text));
			rotation = Double.parseDouble(rotArray[1].toString());
			String dir = rotArray[2].toString();
			if("right".equalsIgnoreCase(dir)) {
				rotation = - rotation;
			}
			
			
		}catch(Exception e ) {
			String errorMsg = "Error parsing rotation from HST, due to: " + e.getMessage();
			throw new IOException(errorMsg, e);
		}
		return rotation;
	}
	
	public String parseTitle(String text) throws IOException{
		String title = "";
		try {
			ClientRegexClass regex = new ClientRegexClass();
			title = GeneralJavaScriptObject.convertToArray(regex.match("<h1>(.*)</h1>", text))[1].toString();

		}catch(Exception e ) {
			String errorMsg = "Error parsing title from HST, due to: " + e.getMessage();
			throw new IOException(errorMsg, e);
		}
		return title;
	}
	
	public String parseDescription(String text) throws IOException{
		String description = "";
		try {
			ClientRegexClass regex = new ClientRegexClass();
			description = GeneralJavaScriptObject.convertToArray(regex.match("</div>\\n+<p>(.*)", text))[1].toString();
			description = description.replace("<p>", "");
			description = description.replace("</p>", "");
			description = description.replace("<a ", "<a target=\"_blank\" ");
			
		}catch(Exception e ) {
			String errorMsg = "Error parsing description from HST, due to: " + e.getMessage();
			throw new IOException(errorMsg, e);
		}
		return description;
	}

	public String parseCredits(String text) throws IOException{
		String credits = "";
		try {
			ClientRegexClass regex = new ClientRegexClass();
			credits = GeneralJavaScriptObject.convertToArray(regex.match("<div class=\\\"credit\\\"><p>(.*)</p>", text))[1].toString();
			credits = credits.replace("<a ", "<a target=\"_blank\" ");

		}catch(Exception e ) {
			String errorMsg = "Error parsing credits from HST, due to: " + e.getMessage();
			throw new IOException(errorMsg, e);
		}
		return credits;
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
