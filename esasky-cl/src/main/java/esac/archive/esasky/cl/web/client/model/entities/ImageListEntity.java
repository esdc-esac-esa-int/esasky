package esac.archive.esasky.cl.web.client.model.entities;

import com.google.gwt.user.client.Timer;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEventHandler;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.model.HstOutreachImage;
import esac.archive.esasky.cl.web.client.query.AbstractTAPService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class ImageListEntity extends EsaSkyEntity {

	public ImageListEntity(IDescriptor descriptor, CountStatus countStatus, SkyViewPosition skyViewPosition,
			String esaSkyUniqId, AbstractTAPService metadataService) {
		super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService);
		CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesChangedEvent.TYPE, new AladinLiteCoordinatesChangedEventHandler () {

			@Override
			public void onCoordsChanged(AladinLiteCoordinatesChangedEvent coordinateEvent) {
				onFoVChanged(); 
			}
		});
	}

	Timer updateTimer = new Timer() {
		
		@Override
		public void run() {
			tablePanel.filterOnFoV("ra_deg", "dec_deg");
		}
		
		@Override
		public void schedule(int delayMillis) {
			super.cancel();
			super.schedule(delayMillis);
		}
	};
	
	private void onFoVChanged() {
		updateTimer.schedule(300);
	}
	
	@Override
	public void fetchData() {
		fetchDataWithoutMOC();
	}
	
	@Override
    public void selectShapes(int shapeId) {
    	drawer.selectShapes(shapeId);
    	GeneralJavaScriptObject[] rows = tablePanel.getSelectedRows();
    	for(GeneralJavaScriptObject row : rows) {
    		if(row.getStringProperty("id").equals(Integer.toString(shapeId))) {
    			HstOutreachImage image = new HstOutreachImage(row.getStringProperty("image_id"));
    			image.loadImage(false);
    		}
    	}
    }

    
}
