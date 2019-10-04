package esac.archive.esasky.cl.web.client.model.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback;
import esac.archive.esasky.cl.web.client.model.PolygonShape;
import esac.archive.esasky.cl.web.client.model.SelectableImage;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.TapMetadata;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPExtTapService;
import esac.archive.esasky.cl.web.client.query.TAPMetadataObservationService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ExtTapTablePanel;

public class ExtTapEntity implements GeneralEntityInterface {

    private final Resources resources = GWT.create(Resources.class);

    public interface Resources extends ClientBundle {

        @Source("galaxy_light.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabDefaultImagingIcon();

        @Source("galaxy_dark.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabSelectedImagingIcon();

    }
    
    protected DefaultEntity defaultEntity;
    protected IShapeDrawer drawer;
    private ExtTapDescriptor descriptor;
    protected ShapeBuilder shapeBuilder = new ShapeBuilder() {
    	
    	@Override
    	public Shape buildShape(int rowId, TapRowList rowList) {
    		PolygonShape polygon = new PolygonShape();
    		polygon.setShapeId(rowId);
    		String stcs = (String) getTAPDataByTAPName(rowList, rowId, descriptor.getTapSTCSColumn());
    		stcs = makeSureSTCSHasFrame(stcs);
    		polygon.setStcs(stcs);
    		polygon.setJsObject(AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(
    				polygon.getStcs(), rowId));
    		return polygon;
    	}
    };
    
    private String makeSureSTCSHasFrame(String input) {
    	String stcs = input.toUpperCase();
    	if(stcs.contains("J2000")  || stcs.contains("ICRS")) {
    		return stcs;
    	}
    	if(stcs.contains("POLYGON")){
    		stcs = stcs.replaceAll("POLYGON", "POLYGON J2000");
    	}
    	if(stcs.contains("CIRCLE")){
    		stcs = stcs.replaceAll("CIRCLE", "CIRCLE J2000");
    	}
    	return stcs;
    	
    }

    public ExtTapEntity(ExtTapDescriptor descriptor, CountStatus countStatus,
    		SkyViewPosition skyViewPosition, String esaSkyUniqId, Long lastUpdate, EntityContext context) {
    	JavaScriptObject overlay = AladinLiteWrapper.getAladinLite().createOverlay(esaSkyUniqId,
				descriptor.getHistoColor());
		drawer = new FootprintDrawer(overlay, shapeBuilder);
        defaultEntity = new DefaultEntity(descriptor, countStatus, skyViewPosition, esaSkyUniqId, lastUpdate,
                context, drawer, TAPMetadataObservationService.getInstance());
		this.descriptor = descriptor;
    }
    
    @Override
    public SelectableImage getTypeIcon() {
        return new SelectableImage(resources.tabDefaultImagingIcon(), resources.tabSelectedImagingIcon());
    }
    
    @Override
    public String getMetadataAdql() {
        return TAPExtTapService.getInstance().getMetadataAdql(getDescriptor());
    }
    
    @Override
    public void fetchData(final AbstractTablePanel tablePanel) {
    	Scheduler.get().scheduleFinally(new ScheduledCommand() {
        	
        	@Override
        	public void execute() {
        		clearAll();
        		final String debugPrefix = "[fetchData][" + getDescriptor().getGuiShortName() + "]";
        		// Get Query in ADQL format.
        		final String adql = getMetadataAdql();
        		
        		String url = TAPUtils.getExtTAPQuery(URL.encode(adql), getDescriptor().getResponseFormat(), getDescriptor().getMission());
        		
        		Log.debug(debugPrefix + "Query [" + url + "]");
        		
        		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        		try {
        			builder.sendRequest(null, new MetadataCallback(tablePanel, adql, getDescriptor().getGuiShortName()));
        			
        		} catch (RequestException e) {
        			Log.error(e.getMessage());
        			Log.error(debugPrefix + "Error fetching JSON data from server");
        		}
        	}
        });
    }
    
    public void setDescriptorMetaData() {
    	List<MetadataDescriptor> metaList = new LinkedList<>();
    	int i = 0;
    	for(TapMetadata tmd : getMetadata().getMetadata()) {
    		MetadataDescriptor metaDatadescriptor = new MetadataDescriptor();
    		metaDatadescriptor.setTapName(tmd.getName());
    		if(tmd.getName().equals("access_url")) {
    			metaDatadescriptor.setType(ColumnType.DATALINK);
    		}else {
    			metaDatadescriptor.setType(ColumnType.valueOf(tmd.getDatatype().toUpperCase()));
    		}
    		metaDatadescriptor.setIndex(i++);
    		metaDatadescriptor.setLabel(tmd.getName());
    		metaDatadescriptor.setVisible(true);
    		metaList.add(metaDatadescriptor);
    	}
    	descriptor.setMetadata(metaList);
    }

	@Override
	public void setSizeRatio(double size) {
		defaultEntity.setSizeRatio(size);
	}
	
	@Override
	public double getSize() {
		return defaultEntity.getSize();
	}

	@Override
	public void removeAllShapes() {
		defaultEntity.removeAllShapes();
	}

	@Override
	public void addShapes(TapRowList rowList) {
		defaultEntity.addShapes(rowList);
	}
	
	@Override
	public void selectShapes(Set<ShapeId> shapes) {
		defaultEntity.selectShapes(shapes);
	}

	@Override
	public void deselectShapes(Set<ShapeId> shapes) {
		defaultEntity.deselectShapes(shapes);
	}

	@Override
	public void deselectAllShapes() {
		defaultEntity.deselectAllShapes();
	}

	@Override
	public void showShape(int rowId) {
		defaultEntity.showShape(rowId);
	}

	@Override
	public void showShapes(List<Integer> shapeIds) {
		defaultEntity.showShapes(shapeIds);
	}

	@Override
	public void showAndHideShapes(List<Integer> rowIdsToShow, List<Integer> rowIdsToHide) {
		defaultEntity.showAndHideShapes(rowIdsToShow, rowIdsToHide);
	}
	
	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		defaultEntity.setShapeBuilder(shapeBuilder);
	}

	@Override
	public void hideShape(int rowId) {
		defaultEntity.hideShape(rowId);
	}

	@Override
	public void hideShapes(List<Integer> shapeIds) {
		defaultEntity.hideShapes(shapeIds);
	}

	@Override
	public void hoverStart(int hoveredRowId) {
		defaultEntity.hoverStart(hoveredRowId);
	}

	@Override
	public void hoverStop(int hoveredRowId) {
		defaultEntity.hoverStop(hoveredRowId);
	}

	@Override
	public SkyViewPosition getSkyViewPosition() {
		return defaultEntity.getSkyViewPosition();
	}

	@Override
	public void setSkyViewPosition(SkyViewPosition skyViewPosition) {
		defaultEntity.setSkyViewPosition(skyViewPosition);
	}

	@Override
	public String getHistoLabel() {
		return defaultEntity.getHistoLabel();
	}

	@Override
	public void setHistoLabel(String histoLabel) {
		defaultEntity.setHistoLabel(histoLabel);
	}

	@Override
	public String getEsaSkyUniqId() {
		return defaultEntity.getEsaSkyUniqId();
	}

	@Override
	public void setEsaSkyUniqId(String esaSkyUniqId) {
		defaultEntity.setEsaSkyUniqId(esaSkyUniqId);
	}

	@Override
	public TapRowList getMetadata() {
		return defaultEntity.getMetadata();
	}

	@Override
	public void setMetadata(TapRowList metadata) {
		defaultEntity.setMetadata(metadata);
	}

	@Override
	public Long getLastUpdate() {
		return defaultEntity.getLastUpdate();
	}

	@Override
	public void setLastUpdate(Long lastUpdate) {
		defaultEntity.setLastUpdate(lastUpdate);
	}

	@Override
	public String getTabLabel() {
		return defaultEntity.getTabLabel();
	}
	
	@Override
	public int getTabNumber() {
		return defaultEntity.getTabNumber();
	}

	@Override
	public void setTabNumber(int number) {
		defaultEntity.setTabNumber(number);
	}
	
	@Override
	public Image getTypeLogo() {
		return defaultEntity.getTypeLogo();
	}

	@Override
	public Object getTAPDataByTAPName(TapRowList tapRowList, int rowIndex, String tapName) {
		return defaultEntity.getTAPDataByTAPName(tapRowList, rowIndex, tapName);
	}

	@Override
	public Double getDoubleByTAPName(TapRowList tapRowList, int rowIndex, String tapName, Double defaultValue) {
		return defaultEntity.getDoubleByTAPName(tapRowList, rowIndex, tapName, defaultValue);
	}

	@Override
	public ExtTapDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public CountStatus getCountStatus() {
		return defaultEntity.getCountStatus();
	}

	@Override
	public EntityContext getContext() {
		return defaultEntity.getContext();
	}

	@Override
	public void clearAll() {
		defaultEntity.clearAll();
	}

	@Override
	public String getColor() {
		return defaultEntity.getColor();
	}

	@Override
	public void setColor(String color) {
		defaultEntity.setColor(color);
	}

	@Override
	public AbstractTablePanel createTablePanel() {
		return new ExtTapTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
	}

	@Override
	public boolean isSampEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRefreshable() {
		return defaultEntity.isRefreshable();
	}

	@Override
	public boolean hasDownloadableDataProducts() {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public boolean isCustomizable() {
    	return defaultEntity.isCustomizable();
    }


}
