package esac.archive.esasky.cl.web.client.model.entities;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback.OnComplete;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.PolygonShape;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPMetadataMOCService;
import esac.archive.esasky.cl.web.client.query.TAPMetadataObservationService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.CommonObservationsTablePanel;

public abstract class ObservationAndSpectraEntity extends CommonObservationEntity {

    private CommonObservationDescriptor descriptor;
	
	public class MocBuilder implements ShapeBuilder{

		@Override
		public Shape buildShape(int rowId, TapRowList rowList) {
			PolygonShape shape = new PolygonShape();
	    	shape.setStcs((String)getTAPDataByTAPName(rowList, rowId, descriptor.getMocSTCSColumn()));
			shape.setJsObject(AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(shape.getStcs()));
			return shape;
		}
	}
    
    public ObservationAndSpectraEntity(CommonObservationDescriptor obsDescriptor,
            CountStatus countStatus, SkyViewPosition skyViewPosition, String esaSkyUniqObsId,
            Long lastUpdate, EntityContext context) {
        super(obsDescriptor, countStatus, skyViewPosition, esaSkyUniqObsId,
                lastUpdate, context);
        this.descriptor = obsDescriptor;
    }

    public CommonObservationDescriptor getDescriptor() {
    	return descriptor;
    }

    @Override
    public String getMetadataAdql() {
        return TAPMetadataObservationService.getInstance().getMetadataAdql(getDescriptor());
    }
    
    @Override
    public void fetchData(AbstractTablePanel tablePanel) {
    	if(showMocData()) {
    		defaultEntity.setShapeBuilder(new MocBuilder());
    		getMocMetadata(tablePanel);
    	} else {
    		defaultEntity.setShapeBuilder(shapeBuilder);
    		super.fetchData(tablePanel);
    	}
    }
    
    private boolean showMocData() {
        int mocLimit = descriptor.getMocLimit();
        int count = getCountStatus().getCount(descriptor.getMission());
        
        if (DeviceUtils.isMobile()){
            mocLimit = EsaSkyWebConstants.MAX_SOURCES_FOR_MOBILE;
        }
        
        if (mocLimit > 0 && count > mocLimit) {
            return true;
        }
        return false;
    }

    
    private void getMocMetadata(final AbstractTablePanel tablePanel) {
        Log.debug("[getMocMetadata][" + descriptor.toString() + "]");

        tablePanel.clearTable();
        String adql = TAPMetadataMOCService.getInstance().getMetadataAdql(getDescriptor());
        
        String url = TAPUtils.getTAPQuery(URL.encodeQueryString(adql), EsaSkyConstants.JSON);

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null,
                new MetadataCallback(tablePanel, adql, TextMgr.getInstance().getText("JsonRequestCallback_retrievingMOC"), new OnComplete() {
                	
                	@Override
                	public void onComplete() {
                		tablePanel.setEmptyTable(TextMgr.getInstance().getText("commonObservationTablePanel_showingGlobalSkyCoverage"));
                	}
                }));
        } catch (RequestException e) {
            Log.error(e.getMessage());
            Log.error("[getMocMetadata] Error fetching JSON data from server");
        }

        tablePanel.setADQLQueryUrl("");
    }

	@Override
	public AbstractTablePanel createTablePanel() {
		return new CommonObservationsTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
	}
}