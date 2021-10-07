package esac.archive.esasky.cl.web.client.model.entities;

import java.util.List;

import com.google.gwt.user.client.Timer;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesOrFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.model.HstOutreachImage;
import esac.archive.esasky.cl.web.client.query.AbstractTAPService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class ImageListEntity extends EsaSkyEntity {

	private double lastOpacity = 1.0;
	private boolean isHidingShapes = false;
	private HstOutreachImage lastImage = null;
	private List<Integer> visibleRows;
	private String outreachImageIdToBeOpened;
	
	private Timer updateTimer = new Timer() {
		
		@Override
		public void run() {
			if(!isHidingShapes) {
				performFoVFilter();
			}
		}
		
		@Override
		public void schedule(int delayMillis) {
			super.cancel();
			super.schedule(delayMillis);
		}
	};
	
	public ImageListEntity(IDescriptor descriptor, CountStatus countStatus, SkyViewPosition skyViewPosition,
			String esaSkyUniqId, AbstractTAPService metadataService) {
		super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService);
		CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesOrFoVChangedEvent.TYPE, coordinateEvent -> onFoVChanged());
	}
	
	private void performFoVFilter() {
		tablePanel.filterOnFoV("ra_deg", "dec_deg");
	}

	
	private void onFoVChanged() {
		updateTimer.schedule(300);
	}
	
	@Override
	public void fetchData() {
		fetchDataWithoutMOC();
		updateTimer.schedule(5000);
	}
	
	@Override
    public void selectShapes(int shapeId) {
    	drawer.selectShapes(shapeId);
    	GeneralJavaScriptObject[] rows = tablePanel.getSelectedRows();
    	for(GeneralJavaScriptObject row : rows) {
    		if(GeneralJavaScriptObject.convertToInteger(row.getProperty("id")) == shapeId) {
    			if(!isIdAlreadyOpen(row.getStringProperty("identifier"))) {
    				lastImage = new HstOutreachImage(row.getStringProperty("identifier"), lastOpacity);
    				lastImage.loadImage(true);
    			}
    			return;
    		}
    	}
    }
	
	private boolean isIdAlreadyOpen(String newId) {
		return lastImage != null && !lastImage.isRemoved() && lastImage.getId().equals(newId);
	}
	
	@Override
	public void addShapes(GeneralJavaScriptObject rows) {
		super.addShapes(rows);
		if(outreachImageIdToBeOpened != null) {
			GeneralJavaScriptObject [] rowDataArray = GeneralJavaScriptObject.convertToArray(rows);
			for(int i = 0; i < rowDataArray.length; i++) {
				if(rowDataArray[i].getStringProperty(getDescriptor().getUniqueIdentifierField()).equals(outreachImageIdToBeOpened)) {
					selectShapes(i);
					tablePanel.selectRow(i);
					return;
				}
			}
		}
	}
	
	public void setIdToBeOpened(String id) {
		this.outreachImageIdToBeOpened = id;
	}
	
	@Override
	public void deselectShapes(int shapeId) {
		super.deselectShapes(shapeId);
		if(lastImage != null) {
			lastImage.removeOpenSeaDragon();
		}
	}
	
	@Override
	public TabulatorSettings getTabulatorSettings() {
		TabulatorSettings settings = new TabulatorSettings();
		settings.disableGoToColumn = true;
		settings.selectable = 1;
		return settings;
	}

    @Override
    public void onShapeSelection(AladinShape shape) {
    	Integer shapeId =  Integer.parseInt(shape.getId());
    	if(shapeRecentlySelected.contains(shapeId)) {
    		shapeRecentlySelected.remove(shapeId);
    		return;
    	}
    	
    	if(tablePanel != null) {
    		select();
    		tablePanel.deselectAllRows();
    		tablePanel.selectRow(shapeId);
    	}
    	
    	selectShapes(shapeId);
    }
    
    public void setOpacity(double opacity) {
    	if(lastImage != null) {
    		lastImage.setOpacity(opacity);
    	}
    	lastOpacity = opacity;
    }

    public void setIsHidingShapes(boolean isHidingShapes) {
    	if(this.isHidingShapes != isHidingShapes) {
    		this.isHidingShapes = isHidingShapes;
    		if(isHidingShapes) {
    			hideAllShapes();
    		} else {
    			performFoVFilter();
    			if(visibleRows != null) {
    				showShapes(visibleRows);
    			}
    		}
    	}
    }

    @Override
    public void showShapes(List<Integer> shapeIds) {
    	super.showShapes(shapeIds);
    	visibleRows = shapeIds;
    }
}
