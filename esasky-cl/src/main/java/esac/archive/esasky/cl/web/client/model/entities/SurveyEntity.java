package esac.archive.esasky.cl.web.client.model.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ObservationDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.model.SelectableImage;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.SourceShape;
import esac.archive.esasky.cl.web.client.model.SourceShapeType;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPMetadataSurveyService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.SurveyConstant;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.SurveyTablePanel;

public class SurveyEntity implements GeneralEntityInterface{

    private final Resources resources = GWT.create(Resources.class);

    public interface Resources extends ClientBundle {

        @Source("galaxy_light.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabDefaultImagingIcon();

        @Source("galaxy_dark.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabSelectedImagingIcon();

    }
    
    private String shape;
    
    private ShapeBuilder shapeBuilder = new ShapeBuilder() {
    	
    	@Override
    	public Shape buildShape(int rowId, TapRowList rowList) {
            SourceShape mySource = new SourceShape();
            mySource.setShapeId(rowId);
            mySource.setDec((getTAPDataByTAPName(rowList, rowId, EsaSkyConstants.OBS_TAP_DEC))
                    .toString());
            mySource.setRa((getTAPDataByTAPName(rowList, rowId, EsaSkyConstants.OBS_TAP_RA))
                    .toString());

            Map<String, String> details = new HashMap<String, String>();

            details.put(EsaSkyWebConstants.SOURCE_TYPE,
                    EsaSkyWebConstants.SourceType.SURVEY.toString());
            details.put(SurveyConstant.SURVEY_NAME, (getTAPDataByTAPName(rowList, rowId,
                    EsaSkyConstants.OBS_TAP_NAME)).toString());
            details.put(SurveyConstant.CATALOGE_NAME, getEsaSkyUniqId());
            details.put(SurveyConstant.IDX, Integer.toString(rowId));

            mySource.setJsObject(AladinLiteWrapper.getAladinLite().newApi_createSourceJSObj(
                    mySource.getRa(), mySource.getDec(), details, rowId));

            return mySource;
    	}
    };
    
    private final ObservationDescriptor descriptor;
    
    private final DefaultEntity defaultEntity;
    private JavaScriptObject overlay;
    
    public SurveyEntity(ObservationDescriptor obsDescriptor, CountStatus countStatus,
    		SkyViewPosition skyViewPosition, String esaSkyUniqId, Long lastUpdate, EntityContext context) {
    	this.descriptor = obsDescriptor;
		Map<String, String> catDetails = new HashMap<String, String>();
		catDetails.put("shape", SourceShapeType.CROSS.getName());
		overlay = AladinLiteWrapper.getAladinLite().createCatalogWithDetails(
				esaSkyUniqId, 20, descriptor.getHistoColor(), catDetails);
    	IShapeDrawer drawer = new SourceDrawer(overlay, shapeBuilder);
        defaultEntity = new DefaultEntity(obsDescriptor, countStatus, skyViewPosition, esaSkyUniqId, lastUpdate,
                context, drawer, TAPMetadataSurveyService.getInstance());
    }
    
    public String getShape() {
        return (this.shape != null) ? this.shape : SourceShapeType.CROSS.getName();
    }

    public void setShape(String shape) {
        this.shape = shape;
        AladinLiteWrapper.getAladinLite().setCatalogShape(overlay, shape);
    }
    
    @Override
    public String getMetadataAdql() {
        return TAPMetadataSurveyService.getInstance().getMetadataAdql(getDescriptor());
    }

    @Override
    public SelectableImage getTypeIcon() {
        return new SelectableImage(resources.tabDefaultImagingIcon(),
                resources.tabSelectedImagingIcon());
    }

	@Override
	public CommonObservationDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public AbstractTablePanel createTablePanel() {
		return new SurveyTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
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
	public void selectShapes(Set<ShapeId> rowsToSelect) {
		defaultEntity.selectShapes(rowsToSelect);
	}

	@Override
	public void deselectShapes(Set<ShapeId> rowsToDeselect) {
		defaultEntity.deselectShapes(rowsToDeselect);
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
	public void hideShape(int rowId) {
		defaultEntity.hideShape(rowId);
	}

	@Override
	public void hideShapes(List<Integer> shapeIds) {
		defaultEntity.hideShapes(shapeIds);
	}

	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		defaultEntity.setShapeBuilder(shapeBuilder);
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
	public void fetchData(AbstractTablePanel tablePanel) {
		defaultEntity.fetchData(tablePanel);
	}

	@Override
	public boolean isSampEnabled() {
		return defaultEntity.isSampEnabled();
	}

	@Override
	public boolean isRefreshable() {
		return defaultEntity.isRefreshable();
	}

	@Override
	public boolean hasDownloadableDataProducts() {
		return false;
	}

	@Override
	public boolean isCustomizable() {
		return defaultEntity.isCustomizable();
	}

	@Override
	public void refreshData(AbstractTablePanel tablePanel) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void coneSearch(AbstractTablePanel tablePanel, SkyViewPosition conePos) {
		// TODO Auto-generated method stub		
	}
}