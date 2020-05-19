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
import esac.archive.esasky.cl.web.client.repository.MocRepository;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.view.resultspanel.ClosingObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;

public class ExtTapEntity extends EsaSkyEntity {

	private MocDrawer mocDrawer;

    public ExtTapEntity(IDescriptor descriptor, CountStatus countStatus,
            SkyViewPosition skyViewPosition, String esaSkyUniqId, 
            AbstractTAPService metadataService) {
    	super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService);
    	MocRepository.getInstance().addExtTapMocEntity(this);
    	mocDrawer = new MocDrawer(descriptor.getPrimaryColor());
    }

    @Override
    public void fetchData() {
    	if(hasReachedFovLimit()) {
	    	Log.debug("Showing fov limit moc. FoVLimit = " + descriptor.getFovLimit());
	    	drawer = mocDrawer;
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
                onFoVChanged();
            }
        }));

        tablePanel.setADQLQueryUrl("");
		tablePanel.registerClosingObserver(new ClosingObserver() {
			
			@Override
			public void onClose() {
				closingPanel(tablePanel);
				
			}
		});
        
    }
    
    public void closingPanel(ITablePanel tablePanel) {
    	drawer.removeAllShapes();
    	MocRepository.getInstance().removeExtTapEntity(this);
    }
    
    public void onFoVChanged() {
		int minOrder =  Math.min(7, MocRepository.getMinOrderFromFoV());
		int maxOrder = Math.min(7,MocRepository.getMaxOrderFromFoV());
		
    	mocDrawer.getOverlay().invokeFunction("setShowOrders", minOrder, maxOrder);
    }
}
