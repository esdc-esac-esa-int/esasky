package esac.archive.esasky.cl.web.client.model.entities;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SSODescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback.OnComplete;
import esac.archive.esasky.cl.web.client.callback.SSOOrbitMetadataCallback;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.SelectableImage;
import esac.archive.esasky.cl.web.client.query.TAPMetadataSSOService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.SSOObservationsTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;

public class SSOEntity extends ObservationAndSpectraEntity {

	private JavaScriptObject orbitPolyline;
	private String ssoName = null;
	private ESASkySSOObjType ssoType;
	private String ssoOrbitColor = "#FF0000";
	private double ssoOrbitLineWidth = CombinedSourceFootprintDrawer.DEFAULT_LINEWIDTH;

	private final Resources resources = GWT.create(Resources.class);

	public interface Resources extends ClientBundle {

		@Source("saturn_light.png")
		@ImageOptions(flipRtl = true)
		ImageResource tabDefaultSSOIcon();

		@Source("saturn_dark.png")
		@ImageOptions(flipRtl = true)
		ImageResource tabSelectedSSOIcon();
	}

	public SSOEntity(SSODescriptor descriptor, CountStatus countStatus,
			SkyViewPosition skyViewPosition, String esaSkyUniqObsId, Long lastUpdate, EntityContext context) {
		super(descriptor, countStatus, skyViewPosition, esaSkyUniqObsId,
				lastUpdate, context);
		setSsoName(GUISessionStatus.getTrackedSso().name);
		setSsoType(GUISessionStatus.getTrackedSso().type);
	}

	public String getSsoName() {
		return ssoName;
	}

	public void setSsoName(String ssoName) {
		this.ssoName = ssoName;
	}

	public ESASkySSOObjType getSsoType() {
		return ssoType;
	}

	public void setSsoType(ESASkySSOObjType ssoType) {
		this.ssoType = ssoType;
	}

	@Override
	public SelectableImage getTypeIcon() {
		return new SelectableImage(resources.tabDefaultSSOIcon(), resources.tabSelectedSSOIcon());
	}

	@Override
	public String getTabLabel() {
		return super.getTabLabel() + " " + ssoName + " (" + ssoType.getType() + ")";
	}

	@Override
	public SSODescriptor getDescriptor() {
		return (SSODescriptor)super.getDescriptor();
	}

	@Override
	public String getMetadataAdql() {
		return TAPMetadataSSOService.getInstance().getMetadataAdql(getDescriptor());
	}

	@Override
	public void fetchData(final ITablePanel tablePanel) {
		getSSOPolyline();
		String url = TAPUtils.getTAPQuery(URL.encodeQueryString(getMetadataAdql()), EsaSkyConstants.JSON);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			MetadataCallback callback = new MetadataCallback(tablePanel, getMetadataAdql(),
					TextMgr.getInstance().getText("MetadataCallback_retrievingSSOMetadata").replace("$NAME$", getDescriptor().getMission()), new OnComplete() {

				@Override
				public void onComplete() {
					int indexOfFirstSSOMetadata = 2; //0 is selection checkbox, 1 is recenter button
					if(getDescriptor().getSampEnabled()) {
						indexOfFirstSSOMetadata++;
					}
					for(MetadataDescriptor metadataDescriptor : getDescriptor().getMetadata()) {
						if(metadataDescriptor.getVisible()) {
							indexOfFirstSSOMetadata++;
						}
					}
					for(MetadataDescriptor metadataDescriptor : getDescriptor().getSsoXMatchMetadata()) {
						if(metadataDescriptor.getVisible()) {
							indexOfFirstSSOMetadata--;
						}
					}
					tablePanel.setSeparator(indexOfFirstSSOMetadata);
				}
			});
			builder.sendRequest(null, callback);

		} catch (RequestException e) {
			Log.error(e.getMessage());
			Log.error("Error fetching JSON data from server");
		}
	}


	private void getSSOPolyline() {
		final String adql = TAPMetadataSSOService.getInstance().getSSOPolylineAdql(this);

		String url = TAPUtils.getTAPQuery(URL.encodeQueryString(adql), EsaSkyConstants.JSON);

		Log.debug("[getSSOPolyline] Query [" + url + "]");

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new SSOOrbitMetadataCallback(this,
					TextMgr.getInstance().getText("SSOOrbitMetadataCallback_retrievingOrbitPoints").replace("$SSONAME$", GUISessionStatus.getTrackedSso().name), url));
		} catch (RequestException e) {
			Log.error(e.getMessage());
			Log.error("[getSSOPolyline] Error fetching JSON data from server");
		}
	}

	public JavaScriptObject getOrbitPolyline() {
		return orbitPolyline;
	}

	public void setOrbitPolyline(JavaScriptObject orbitPolyline) {
		this.orbitPolyline = orbitPolyline;
	}

	public String getSsoOrbitColor(){
		return ssoOrbitColor;
	}

	public void setSsoOrbitColor(String ssoOrbitColor){
		this.ssoOrbitColor = ssoOrbitColor;
		AladinLiteWrapper.getAladinLite().setOverlayColor(getOrbitPolyline(), ssoOrbitColor);
	}

	private void setSsoOrbitLineWidth(int lineWidth) {
        ssoOrbitLineWidth = Math.max(1, lineWidth);
	}

	public void setSsoOrbitLineRatio(double ratio) {
	    setSsoOrbitLineWidth((int)(CombinedSourceFootprintDrawer.MAX_LINEWIDTH * ratio));
	    AladinLiteWrapper.getAladinLite().setOverlayLineWidth(getOrbitPolyline(), (int) ssoOrbitLineWidth);
	}

	public double getSsoOrbitLineRatio(){
        return (double)ssoOrbitLineWidth / (double)CombinedSourceFootprintDrawer.MAX_LINEWIDTH;
	}

	public int getSsoOrbitLineWidth(){
		return (int)ssoOrbitLineWidth;
	}
	
	public void setStartOfPolyline(double ra, double dec) {
		setSkyViewPosition(new SkyViewPosition(new Coordinate(ra, dec), AladinLiteWrapper.getInstance().getFovDeg()));
	}

	@Override
	public AbstractTablePanel createTablePanel() {
		return new SSOObservationsTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
	}

	@Override
	public boolean isSampEnabled() {
		return false;
	}

	@Override
	public boolean isRefreshable() {
		return false;
	}

	@Override
	public StylePanel createStylePanel() {
		// TODO Auto-generated method stub
		return null;
	}
}