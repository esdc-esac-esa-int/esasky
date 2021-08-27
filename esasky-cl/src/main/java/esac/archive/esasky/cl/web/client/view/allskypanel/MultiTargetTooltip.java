package esac.archive.esasky.cl.web.client.view.allskypanel;

import java.util.UUID;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Label;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.banner.CheckForServerMessagesEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.SearchPresenter.ESASkySearchResultMapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.SourceConstant;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.uploadtargetlist.MultiTargetSourceConstants;

public class MultiTargetTooltip extends Tooltip {

    private final String logPrefix = "[MultiTargetTooltip]";
    private LoadingSpinner loadingSpinner = new LoadingSpinner(false);
    private Label closestIdName = new Label();
    private Label closestIdDescription = new Label(TextMgr.getInstance().getText("MultiTargetTooltip_nearestSimbadId") + ":");

    public MultiTargetTooltip(final AladinShape source) {
        super(source);
        this.source = source;
    }

    protected void fillContent(String cooFrame) {
        Log.debug("Into fillContent, cooFrame: " + cooFrame);

        Double raSource = Double.parseDouble(this.source
                .getDataDetailsByKey(MultiTargetSourceConstants.RA_DEG));
        Double decSource = Double.parseDouble(this.source
                .getDataDetailsByKey(MultiTargetSourceConstants.DEC_DEG));
        double[] raDec = { raSource, decSource };
        Log.debug(logPrefix + " source RA: " + raDec[0] + " source Dec: " + raDec[1]);
        Log.debug("SOURCE COOFRAME:" + this.source.getDataDetailsByKey(SourceConstant.COO_FRAME));        
        String tooltipCooFrameLabel = "";
        String sourceCooFrame = this.source.getDataDetailsByKey(SourceConstant.COO_FRAME);
        if (cooFrame.equalsIgnoreCase(sourceCooFrame)) {
            Log.debug("no conversion");
            tooltipCooFrameLabel = cooFrame;
        } else if (sourceCooFrame != null){
            if (sourceCooFrame.equalsIgnoreCase(AladinLiteConstants.FRAME_GALACTIC)) {
                // convert to J2000
                Log.debug("Convert to equatorial");
                raDec = CoordinatesConversion.convertPointGalacticToJ2000(raSource, decSource);
                tooltipCooFrameLabel = AladinLiteConstants.FRAME_J2000;
            } else {
                // convert to Gal
                Log.debug("Convert to galactic");
                raDec = CoordinatesConversion.convertPointEquatorialToGalactic(raSource, decSource);
                tooltipCooFrameLabel = AladinLiteConstants.FRAME_GALACTIC;
            }
        }

        String formattedRa = NumberFormat.getFormat("##0.#####").format(raDec[0]);
        String formattedDec = NumberFormat.getFormat("##0.#####").format(raDec[1]);

        Label userInput = new Label(this.source.getDataDetailsByKey(MultiTargetSourceConstants.USER_INPUT));
        userInput.addStyleName("multiTargetTooltip__userInput");
        typeSpecificFlowPanel.add(userInput);
        
        closestIdDescription.addStyleName("multiTargetTooltip__closestIdDescription");
        typeSpecificFlowPanel.add(closestIdDescription);
        
        if(this.source.getDataDetailsByKey(MultiTargetSourceConstants.SIMBAD_MAIN_ID) == null) {
        	lookForNearbyMainId();
        	typeSpecificFlowPanel.add(loadingSpinner);
        } else {
        	closestIdName.setText(this.source.getDataDetailsByKey(MultiTargetSourceConstants.SIMBAD_MAIN_ID));
        }
        
        closestIdName.addStyleName("multiTargetTooltip__closestIdName");
        typeSpecificFlowPanel.add(closestIdName);
        
        Label raLabel = new Label("RA:");
        raLabel.addStyleName("multiTargetTooltip__raLabel");
        typeSpecificFlowPanel.add(raLabel);
        Label ra = new Label(formattedRa + " [" + tooltipCooFrameLabel+ "]");
        ra.addStyleName("multiTargetTooltip__number");
        typeSpecificFlowPanel.add(ra);
        Label decLabel = new Label("Dec:");
        decLabel.addStyleName("multiTargetTooltip__decLabel");
        typeSpecificFlowPanel.add(decLabel);
        Label dec = new Label(formattedDec + " [" + tooltipCooFrameLabel+ "]");
        dec.addStyleName("multiTargetTooltip__number");
        typeSpecificFlowPanel.add(dec);
    }
    
    private void lookForNearbyMainId() {
        Double raSource = Double.parseDouble(this.source
                .getDataDetailsByKey(MultiTargetSourceConstants.RA_DEG));
        Double decSource = Double.parseDouble(this.source
                .getDataDetailsByKey(MultiTargetSourceConstants.DEC_DEG));
        
        String sourceCooFrame = this.source.getDataDetailsByKey(SourceConstant.COO_FRAME);
        if(sourceCooFrame != null && sourceCooFrame.equalsIgnoreCase(AladinLiteConstants.FRAME_GALACTIC)) {
        	double [] raDec = CoordinatesConversion.convertPointGalacticToJ2000(raSource, decSource);
        	raSource = raDec[0];
        	decSource = raDec[1];
        }
    	
        final String uid = UUID.randomUUID().toString();
        final String url = EsaSkyWebConstants.GENERAL_RESOLVER_URL + "?action=bycoords&RA="
            		+ URL.encodeQueryString(Double.toString(raSource))
            		+ "&DEC=" + URL.encodeQueryString(Double.toString(decSource));
        CommonEventBus.getEventBus().fireEvent(
                new ProgressIndicatorPushEvent(uid, TextMgr.getInstance().getText("MultiTargetTooltip_resolvingNearbyTargets")));

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null, new RequestCallback() {

                @Override
                public void onResponseReceived(final Request request, final Response response) {
                	try {
                		CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(uid));
                		if (200 == response.getStatusCode()) {
                			
                			ESASkySearchResultMapper mapper = GWT
                					.create(ESASkySearchResultMapper.class);
                			ESASkySearchResult result = mapper.read(response.getText());
                			if (result == null) {
                				setNoNearbyTargetFoundInSimbadText();
                			} else {
                				setNearbyTarget(result.getSimbadMainId());
                			}
                			
                		} else {
                			Log.error("Couldn't retrieve data from the " + url
                					+ " StatusText: (" + response.getStatusText() + ")");
                			onError(request, new Throwable());
                		}
                		CommonEventBus.getEventBus().fireEvent(new CheckForServerMessagesEvent());
                	} catch (Exception e) {
                		onError(request, e);
                	}
                }

                @Override
                public void onError(final Request request, final Throwable exception) {
                	CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(uid));
                	setNoNearbyTargetFoundInSimbadText();
                    Log.error(exception.getMessage());
                    Log.error("Error calling " + url);
                }
            });
        } catch (RequestException e) {
        	setNoNearbyTargetFoundInSimbadText();
        	CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(uid));
            Log.error(e.getMessage());
            Log.error("Error calling " + url);
        }
    }
    
    @Override
    public void hide() {
    	super.hide();
    	deselectSource(source);
    }
    
    private native void deselectSource(JavaScriptObject object) /*-{
	    object.deselect();
    	object["catalog"].reportChange();
    }-*/;
    private native void selectSource(JavaScriptObject object) /*-{
	    object.select();
	    object["catalog"].reportChange();
    }-*/;
    
    @Override
    public void show() {
    	super.show();
    	selectSource(source);
    }
    
    private void setNearbyTarget(String targetName) {
    	loadingSpinner.setVisible(false);
    	closestIdName.setText(targetName);
    	if(targetName.toLowerCase().equals(source.getSourceName().toLowerCase())) {
    	    closestIdDescription.setVisible(false);
    	    closestIdName.setVisible(false);
    	}
    }
    
    private void setNoNearbyTargetFoundInSimbadText() {
    	loadingSpinner.setVisible(false);
    	closestIdDescription.setText(TextMgr.getInstance().getText("MultiTargetTooltip_noNearbySimbadIdFound"));
    }
}
