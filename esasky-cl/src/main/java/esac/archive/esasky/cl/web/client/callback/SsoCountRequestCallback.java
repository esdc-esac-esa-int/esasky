package esac.archive.esasky.cl.web.client.callback;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SSODescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.TreeMapNewDataEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TapMetadata;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.TrackedSso;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.TapRowListMapper;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository.DescriptorListAdapter;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;

public class SsoCountRequestCallback implements RequestCallback {

    private String progressIndicatorId;
    private DescriptorListAdapter<SSODescriptor> descriptorList;
    private String ssoName;
    private ESASkySSOObjType ssoType;

    public SsoCountRequestCallback(DescriptorListAdapter<SSODescriptor> descriptorList, String ssoName, ESASkySSOObjType ssoType) {

        this.progressIndicatorId = UUID.randomUUID().toString();
        this.descriptorList = descriptorList;
        this.ssoName = ssoName;
        this.ssoType = ssoType;
        addProgressIndicator(TextMgr.getInstance().getText("resultsPresenter_computingDataCrossMatch").replace("$SSOTEXT$", ssoType.getType() + " " + ssoName));
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
            String missions = "";
            for(IDescriptor descriptor : descriptorList.getDescriptors()) {
                missions += descriptor.getGuiLongName() + ", ";
            }
            DisplayUtils
            .showMessageDialogBox(TextMgr.getInstance().getText("SsoCountRequestCallback_noCrossMatchResultsMessage").replace("$MISSIONS$", missions.subSequence(0, missions.length() - 2)),
                    TextMgr.getInstance().getText("SsoCountRequestCallback_noCrossMatchResultsTitle"),
                    progressIndicatorId, "NoSSOCrossMatchFoundDialog");
            GUISessionStatus.setIsTrackingSSO(false);
        } else {
        	List<IDescriptor> descriptors = new LinkedList<IDescriptor>();
        	List<Integer> counts = new LinkedList<Integer>();
        	int ssoId = 0;
        	
            for (TapMetadata currMtd : rowList.getMetadata()) {
                if (currMtd.getName().equals("sso_oid")) {
                    ssoId = Integer.parseInt(rowList.getDataValue("sso_oid", 0));
                } else {
                	currMtd.setName(currMtd.getName().replace("_", "-"));
                    SSODescriptor descriptor = descriptorList
                            .getDescriptorByMissionNameCaseInsensitive(currMtd.getName());
                    if (descriptor != null) {
                        Integer count = Integer
                                .parseInt(rowList.getDataValue(currMtd.getName(), 0));
                        descriptorList.getCountStatus().setCount(descriptor, count);
                        descriptors.add(descriptor);
                        counts.add(count);
                    }
                }
            }
            descriptorList.getCountStatus().updateCount();
            GUISessionStatus.setTrackedSSO(new TrackedSso(ssoName, ssoType, ssoId));
            CommonEventBus.getEventBus().fireEvent(new TreeMapNewDataEvent(descriptors, counts));
        }

    }
}