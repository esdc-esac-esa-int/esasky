package esac.archive.esasky.cl.web.client.model.entities;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.URL;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.query.AbstractTAPService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;

public class ExtTapEntity extends EsaSkyEntity {


    public ExtTapEntity(IDescriptor descriptor, CountStatus countStatus,
            SkyViewPosition skyViewPosition, String esaSkyUniqId, 
            AbstractTAPService metadataService) {
    	super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService);
    }

    @Override
    public void fetchData() {
    	if(hasReachedFovLimit()) {
	    	Log.debug("Showing fov limit moc. FoVLimit = " + descriptor.getFovLimit());
	    	drawer = new MocDrawer(descriptor.getPrimaryColor());
	        defaultEntity.setDrawer(drawer);
	        getMocMetadata();
	    } else {
	    	drawer = combinedDrawer;
	    	defaultEntity.setDrawer(combinedDrawer);
	        fetchDataWithoutMOC();
	    }
    }
    	
    private void getMocMetadata() {

        tablePanel.clearTable();
        String adql = metadataService.getMocAdql(getDescriptor(), "");

        String url = TAPUtils.getTAPQuery(URL.decodeQueryString(adql), EsaSkyConstants.JSON).replaceAll("#", "%23");

        JSONUtils.getJSONFromUrl(url, new MetadataCallback(tablePanel, adql, TextMgr.getInstance().getText("JsonRequestCallback_retrievingMOC"), new MetadataCallback.OnComplete() {

            @Override
            public void onComplete() {
                tablePanel.setEmptyTable(TextMgr.getInstance().getText("commonObservationTablePanel_showingGlobalSkyCoverage"));
            }
        }));

        tablePanel.setADQLQueryUrl("");
    }
}
