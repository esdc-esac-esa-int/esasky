package esac.archive.esasky.cl.web.client.model.entities;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.callback.SSOOrbitMetadataCallback;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TrackedSso;
import esac.archive.esasky.cl.web.client.query.TAPSSOService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;

public class SSOEntity extends EsaSkyEntity {
    
    private final TrackedSso sso;
	public SSOEntity(CommonTapDescriptor descriptor) {

		super(descriptor, DescriptorRepository.getInstance().getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_SSO).getCountStatus(),
                CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.getId(), TAPSSOService.getInstance());
		this.sso = GUISessionStatus.getTrackedSso();
	}

	@Override
	public void fetchData() {
	    super.fetchDataWithoutMOC();
		getSSOPolyline();
	}

	private void getSSOPolyline() {
		final String adql = TAPSSOService.getInstance().getPolylineAdql(getDescriptor());
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

	public void setOrbitPolyline(double[] polylinePoints) {
	    combinedDrawer.addPolylineOverlay(getId(), polylinePoints, getDescriptor().getColor());
	}
	
    @Override
    public String getTabLabel() {
        return super.getTabLabel() + " " + sso.name + " (" + sso.type.getType() + ")";
    }
	
	@Override
	public boolean isRefreshable() {
		return false;
	}
}