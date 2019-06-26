package esac.archive.esasky.cl.web.client.callback;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

import esac.archive.ammi.ifcs.model.descriptor.IDescriptor;
import esac.archive.ammi.ifcs.model.descriptor.SSODescriptor;
import esac.archive.ammi.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.model.TapMetadata;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.TrackedSso;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository.DescriptorListAdapter;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;

public class SsoCountRequestCallback implements RequestCallback {

    private String progressIndicatorId;
    private DescriptorListAdapter<SSODescriptor> observations;
    private String ssoName;
    private ESASkySSOObjType ssoType;
    private ISSOCountRequestHandler countRequestHandler;

    public SsoCountRequestCallback(DescriptorListAdapter<SSODescriptor> observations, String ssoName, ESASkySSOObjType ssoType,
            ISSOCountRequestHandler countRequestHandler) {

        this.progressIndicatorId = UUID.randomUUID().toString();
        this.observations = observations;
        this.ssoName = ssoName;
        this.ssoType = ssoType;
        this.countRequestHandler = countRequestHandler;
        
        addProgressIndicator(countRequestHandler.getSSOProgressIndicatorMessage(ssoName, ssoType));
    }

    @Override
    public void onResponseReceived(final Request request, final Response response) {
        onResponseReceived(response);
    }

    private void onResponseReceived(final Response response) {
        // store start time for logging purposes
        long startTimeMillis = 0L;
        if (Log.isDebugEnabled()) {
            startTimeMillis = System.currentTimeMillis();
        }
        Log.debug(" TIMECALL = " + response.getHeader("timecall"));

        if (200 == response.getStatusCode()) {
            onSuccess(response, startTimeMillis);
        } else {
            Log.error(this.getClass().getSimpleName() + " Couldn't retrieve JSON ("
                    + response.getStatusText() + ")");
        }

        removeProgressIndicator();
    }

    @Override
    public void onError(final Request request, final Throwable exception) {
        Log.error(exception.getMessage());
        Log.error(this.getClass().getSimpleName() + " Error fetching JSON data from server");
        removeProgressIndicator();
    }

    private void addProgressIndicator(String progressIndicatorMessage) {
        CommonEventBus.getEventBus().fireEvent(
                new ProgressIndicatorPushEvent(this.progressIndicatorId, progressIndicatorMessage));
    }

    private void removeProgressIndicator() {
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(progressIndicatorId));
    }

    protected void onSuccess(Response response, long startTimeMillis) {

        TapRowListMapper mapper = GWT.create(TapRowListMapper.class);
        TapRowList rowList = mapper.read(response.getText());
        Log.debug("[SSO] count response received" + response.getText());
        if (rowList.getData().size() == 0) {
            countRequestHandler.showObjectNotAvailableInEsaSkyMsg(progressIndicatorId);
            GUISessionStatus.setIsTrackingSSO(false);
        } else {
        	List<IDescriptor> descriptors = new LinkedList<IDescriptor>();
        	List<Integer> counts = new LinkedList<Integer>();
        	int ssoId = 0;
        	
            for (TapMetadata currMtd : rowList.getMetadata()) {
                if (currMtd.getName().equals("sso_oid")) {
                    ssoId = Integer.parseInt(rowList.getDataValue("sso_oid", 0));
                } else {
                    SSODescriptor descriptor = observations
                            .getDescriptorByMissionNameCaseInsensitive(currMtd.getName());
                    if (descriptor != null) {
                        Integer count = Integer
                                .parseInt(rowList.getDataValue(currMtd.getName(), 0));
                        observations.getCountStatus().setCount(currMtd.getName(), count);
                        descriptors.add(descriptor);
                        counts.add(count);
                    }
                }
            }
            observations.getCountStatus().updateCount();
            GUISessionStatus.setTrackedSSO(new TrackedSso(ssoName, ssoType, ssoId));
            CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptors, counts));
        }

    }
}