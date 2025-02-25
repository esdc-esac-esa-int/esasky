/*
ESASky
Copyright (C) 2025 Henrik Norman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
		getDescriptor().setSecondaryColor(getDescriptor().getColor());
	    combinedDrawer.addPolylineOverlay(getId(), polylinePoints, getDescriptor().getSecondaryColor());
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