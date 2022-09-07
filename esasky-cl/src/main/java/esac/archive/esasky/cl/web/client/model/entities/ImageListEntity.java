package esac.archive.esasky.cl.web.client.model.entities;

import java.util.List;
import java.util.UUID;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Timer;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ImageListSelectedEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.OutreachImage;
import esac.archive.esasky.cl.web.client.query.TAPImageListService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

public class ImageListEntity extends EsaSkyEntity {

	private double lastOpacity = 1.0;
	private boolean isHidingShapes = false;
	private boolean isClosed = false;
	private boolean firstLoad = true;
	private OutreachImage lastImage = null;
	private List<Integer> visibleRows;
	private String outreachImageIdToBeOpened;
	private long timeAtLastFoVFilter = 0L;

	public static final String IDENTIFIER_KEY = "identifier";
	
	private Timer updateTimer = new Timer() {
		
		@Override
		public void run() {
			performFoVFilter();
		}
		
		@Override
		public void schedule(int delayMillis) {
			super.cancel();
			super.schedule(delayMillis);
		}
	};
	
	public ImageListEntity(IDescriptor descriptor, CountStatus countStatus, SkyViewPosition skyViewPosition,
						   String esaSkyUniqId, TAPImageListService metadataService) {
		super(descriptor, countStatus, skyViewPosition, esaSkyUniqId, metadataService);
		this.metadataService = metadataService;
		CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesChangedEvent.TYPE, coordinateEvent -> {
			if(coordinateEvent.getIsViewCenterPosition()) {
				onFoVChanged();	
			}
		});
		CommonEventBus.getEventBus().addHandler(AladinLiteFoVChangedEvent.TYPE, coordinateEvent -> onFoVChanged());
	}

	private void performFoVFilter() {
		timeAtLastFoVFilter = System.currentTimeMillis();
		tablePanel.filterOnFoV("ra_deg", "dec_deg");
	}

	private void onFoVChanged() {
		if(System.currentTimeMillis() - timeAtLastFoVFilter > 1000) {
			performFoVFilter();
		} else {
			updateTimer.schedule(300);
		}
	}

	@Override
	public void fetchData() {
		fetchDataWithoutMOC();
		updateTimer.schedule(5000);
	}
	
	@Override
    public void selectShapes(int shapeId) {
		CommonEventBus.getEventBus().fireEvent(new ImageListSelectedEvent(this));
    	drawer.selectShapes(shapeId);
    	GeneralJavaScriptObject[] rows = tablePanel.getSelectedRows();
    	for(GeneralJavaScriptObject row : rows) {
    		if(GeneralJavaScriptObject.convertToInteger(row.getProperty("id")) == shapeId) {
				lastImage = new OutreachImage(row, lastOpacity, descriptor.getMission());
				UrlUtils.setSelectedOutreachImageId(lastImage.getId(), getDescriptor());
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
		if(firstLoad) {
			firstLoad = false;
			performFoVFilter();
			setSizeRatio(0.3);
			if(isHidingShapes) {
				toggleFootprints();
			}
		}
		if(outreachImageIdToBeOpened != null) {
			GeneralJavaScriptObject [] rowDataArray = GeneralJavaScriptObject.convertToArray(rows);
			for(int i = 0; i < rowDataArray.length; i++) {
				if(rowDataArray[i].getStringProperty(getDescriptor().getUniqueIdentifierField()).equals(outreachImageIdToBeOpened)) {
					selectShapes(i);
					tablePanel.selectRow(i);
					return;
				}
			}
    		String errorMsg = TextMgr.getInstance().getText("imageListEntity_imageNotFound").replace("$ID$", outreachImageIdToBeOpened);
			DisplayUtils.showMessageDialogBox(errorMsg, TextMgr.getInstance().getText("error").toUpperCase(), UUID.randomUUID().toString(),
					TextMgr.getInstance().getText("error"));
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
			UrlUtils.setSelectedOutreachImageId(null, getDescriptor());
		}
	}

	@Override
	public void deselectAllShapes() {
		super.deselectAllShapes();
		if(lastImage != null) {
			lastImage.removeOpenSeaDragon();
			UrlUtils.setSelectedOutreachImageId(null, getDescriptor());
			lastImage = null;
		}
		tablePanel.deselectAllRows();
	}

	@Override
	public TabulatorSettings getTabulatorSettings() {
		TabulatorSettings settings = new TabulatorSettings();
		settings.setDisableGoToColumn(true);
		settings.setSelectable(1);
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
    
    public double getOpacity() {
    	return lastOpacity;
    }

    public void setIsHidingShapes(boolean isHidingShapes) {
    	if(this.isHidingShapes != isHidingShapes) {
    		this.isHidingShapes = isHidingShapes;
    		toggleFootprints();
    	}
    }
    
    public boolean isHidingShapes() {
    	return isHidingShapes;
    }

	private void toggleFootprints() {
		if(this.isHidingShapes || this.isClosed) {
			hideAllShapes();
		} else {
			performFoVFilter();
			if(visibleRows != null) {
				showShapes(visibleRows);
			}
		}
	}

    @Override
    public void showShapes(List<Integer> shapeIds) {
    	if(!isHidingShapes && !this.isClosed) {
    		super.showShapes(shapeIds);
    	}
    	visibleRows = shapeIds;
    }
    
    public void setIsPanelClosed(boolean isClosed) {
    	this.isClosed = isClosed;
    	toggleFootprints();
    	if(lastImage != null){
    		if(isClosed) {
				UrlUtils.setSelectedOutreachImageId(null, getDescriptor());
				lastImage.removeOpenSeaDragon();
				tablePanel.deselectAllRows();
    		} else {
				UrlUtils.setSelectedOutreachImageId(lastImage.getId(), getDescriptor());
    		}
    	}
    }

	public boolean getIsPanelClosed() {
		return isClosed;
	}

	public JSONArray getIds() {
		JSONObject data = getAllData();
		JSONArray result = new JSONArray();

		int i = 0;
		for (String key : data.keySet()) {
			JSONObject value = data.get(key).isObject();
			if (value != null && value.containsKey(IDENTIFIER_KEY)) {
				result.set(i, value.get(IDENTIFIER_KEY));
			}
			i++;
		}

		return result;
	}

	public JSONObject getAllData() {
		return tablePanel.exportAsJSON(false);
	}

	public void selectShape(String identifier) {
		if (this.isClosed) {
			setIsPanelClosed(false);
		}
		CommonEventBus.getEventBus().fireEvent(new ImageListSelectedEvent(this));
		GeneralJavaScriptObject[] rowDataArray = tablePanel.getAllRows();
		for(int i = 0; i < rowDataArray.length; i++) {
			if(rowDataArray[i].getStringProperty(getDescriptor().getUniqueIdentifierField()).equals(identifier)) {
				select();
				tablePanel.deselectAllRows();
				selectShapes(i);
				tablePanel.selectRow(i);
				return;
			}
		}
	}
}
