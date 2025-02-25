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

package esac.archive.esasky.cl.web.client.api;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.model.DescriptorCountAdapter;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.WavelengthUtils;
import esac.archive.esasky.cl.web.client.utility.WavelengthUtils.WavelengthName;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

import java.util.List;
import java.util.stream.Collectors;

public class ApiCounts extends ApiBase {

    public ApiCounts(Controller controller) {
        this.controller = controller;
    }

    public void getAvailableObservationMissions(JavaScriptObject widget) {
        List<CommonTapDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getDescriptors(EsaSkyWebConstants.CATEGORY_OBSERVATIONS);
        getAvailableMissions(descriptors, widget);
    }

    public void getAvailableSpectraMissions(JavaScriptObject widget) {
        List<CommonTapDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getDescriptors(EsaSkyWebConstants.CATEGORY_SPECTRA);
        ;
        getAvailableMissions(descriptors, widget);
    }

    public void getAvailableCatalogueMissions(JavaScriptObject widget) {
        List<CommonTapDescriptor> descriptors = controller.getRootPresenter().getDescriptorRepository().getDescriptors(EsaSkyWebConstants.CATEGORY_CATALOGUES);
        getAvailableMissions(descriptors, widget);
    }

    public void getAvailableMissions(final List<CommonTapDescriptor> descriptors, JavaScriptObject widget) {
        JSONObject obsObj = new JSONObject();

        for (CommonTapDescriptor currDesc : descriptors) {
            Double meanWavelength = currDesc.getWavelengthCenter();
            if(meanWavelength == null) {
            	obsObj.put(currDesc.getMission(), new JSONObject());
            	continue;
            }

            JSONObject descObj = new JSONObject();
            WavelengthName name = WavelengthUtils.getWavelengthNameFromValue(meanWavelength);
            descObj.put(ApiConstants.WAVELENGTH, new JSONString(name.longName));

            double min = currDesc.getWavelengthStart();
            double max = currDesc.getWavelengthEnd();
            List<JSONString> names = WavelengthUtils.getWavelengthsNameFromRange(min, max).stream()
                    .map(x -> new JSONString(x.longName)).collect(Collectors.toList());

            JSONArray namesArr = new JSONArray();
            names.forEach(x -> namesArr.set(namesArr.size(), x));
            descObj.put(ApiConstants.WAVELENGTHS, namesArr);
            obsObj.put(currDesc.getMission(), descObj);
        }
        sendBackValuesToWidget(obsObj, widget);

    }

    public void getObservationsCount(JavaScriptObject widget) {
        GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_GETOBSERVATIONSCOUNT);
        DescriptorCountAdapter descriptors = controller.getRootPresenter().getDescriptorRepository().getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_OBSERVATIONS);
        getCounts(descriptors, widget);
    }

    public void getCataloguesCount(JavaScriptObject widget) {
        GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_GETCATALOGUESCOUNT);
        DescriptorCountAdapter descriptors = controller.getRootPresenter().getDescriptorRepository().getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_CATALOGUES);
        getCounts(descriptors, widget);
    }

    public void getSpectraCount(JavaScriptObject widget) {
        GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_GETSPECTRACOUNT);
        DescriptorCountAdapter descriptors = controller.getRootPresenter().getDescriptorRepository().getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_SPECTRA);
        getCounts(descriptors, widget);
    }

    private void getCounts(final DescriptorCountAdapter descriptorCountAdapter, final JavaScriptObject widget) {
        final CountStatus countStatus = descriptorCountAdapter.getCountStatus();
        if (Boolean.TRUE.equals(checkCountUpdated(descriptorCountAdapter))) {
            onCountUpdated(descriptorCountAdapter, countStatus, widget);

        } else {
            countStatus.registerObserver(new CountObserver() {
                @Override
                public void onCountUpdate(long newCount) {
                    onCountUpdated(descriptorCountAdapter, countStatus, widget);
                    countStatus.unregisterObserver(this);
                }
            });
        }
    }

    private void onCountUpdated(final DescriptorCountAdapter descriptorCountAdapter, final CountStatus countStatus, final JavaScriptObject widget) {
        JSONObject obsCount = new JSONObject();

        for (CommonTapDescriptor currObs : descriptorCountAdapter.getDescriptors()) {
            obsCount.put(currObs.getMission(), new JSONNumber(countStatus.getCount(currObs)));
        }

        obsCount.put(ApiConstants.COUNT_TOTAL, new JSONNumber(countStatus.getTotalCount()));
        GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_COUNT, obsCount.toString());
        sendBackValuesToWidget(obsCount, widget);
    }

    private Boolean checkCountUpdated(DescriptorCountAdapter descriptorCountAdapter) {
        if (descriptorCountAdapter != null) {
            CountStatus countStatus = descriptorCountAdapter.getCountStatus();
            return countStatus.countStillValid(descriptorCountAdapter.getDescriptors().get(0));
        }
        return false;
    }


}
