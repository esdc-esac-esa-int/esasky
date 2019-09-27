package esac.archive.esasky.cl.web.client.model.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.SelectableImage;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.SourceShape;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPMetadataCatalogueService;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.ProperMotionUtils;
import esac.archive.esasky.cl.web.client.utility.SourceConstant;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.SourcesTablePanel;

public class CatalogEntity implements GeneralEntityInterface{


    private static final double MAX_ARROW_SCALE = 10.0;

    private JavaScriptObject catalogue;
    protected final CatalogDescriptor descriptor;
    
    private final DefaultEntity defaultEntity;

    private final Resources resources = GWT.create(Resources.class);

    private String shape;
    private double arrowScale = 1.0;
    
    private boolean showAvgPM = false;
    private boolean useMedianOnAvgPM;
    private ShapeBuilder shapeBuilder = new ShapeBuilder() {
    	
    	@Override
    	public SourceShape buildShape(int shapeId, TapRowList sourceList) {
    		return CatalogEntity.this.buildShape(shapeId, sourceList);
    	}
    };
    
	private Timer sourceLimitNotificationTimer = new Timer() {

		@Override
		public void run() {
			CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(getEsaSkyUniqId() + "SourceLimit"));
		}
	};
    
    public interface Resources extends ClientBundle {

        @Source("catalog_map_outline.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabDefaultCatalogueIcon();

        @Source("catalog_map_outline_dark.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabSelectedCatalogueIcon();
    }

    public CatalogEntity(CatalogDescriptor catDescriptor, CountStatus countStatus,
            JavaScriptObject catalogue, SkyViewPosition skyViewPosition,
            String esaSkyUniqId, Long lastUpdate, EntityContext context) {
		this.catalogue = catalogue;
    	IShapeDrawer drawer = new SourceDrawer(catalogue, shapeBuilder);
        defaultEntity = new DefaultEntity(catDescriptor, countStatus, skyViewPosition, esaSkyUniqId, lastUpdate,
                context, drawer, TAPMetadataCatalogueService.getInstance());
        this.descriptor = catDescriptor;
    }

    @Override
    public CatalogDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public String getMetadataAdql() {
       return defaultEntity.getMetadataAdql();
    }

    @Override
    public SelectableImage getTypeIcon() {
        return new SelectableImage(resources.tabDefaultCatalogueIcon(),
                resources.tabSelectedCatalogueIcon());
    }

    public String getShape() {
        return (this.shape != null) ? this.shape : "square";
    }

    public void setShape(String shape) {
        this.shape = shape;
        AladinLiteWrapper.getAladinLite().setCatalogShape(this.catalogue, shape);
    }

    public String getArrowColor() {
        return this.getDescriptor().getPmArrowColor();
    }

    public void setArrowColor(String color) {
        this.getDescriptor().setPmArrowColor(color);
        AladinLiteWrapper.getAladinLite().setCatalogArrowColor(this.catalogue, color);
    }

    // Must return a ratio between 0.01 and 1.0;
    public double getArrowScale() {
        return arrowScale / MAX_ARROW_SCALE;
    }

    // Must receive a ratio between 0.01 and 1.0;
    public void setArrowScale(double scale) {
        arrowScale = scale * MAX_ARROW_SCALE;
        AladinLiteWrapper.getAladinLite().setCatalogArrowScale(this.catalogue, arrowScale);
    }
    
    public void setShowAvgProperMotion(boolean showAvgPM, boolean useMedianOnAvgPM) {
        this.showAvgPM = showAvgPM;
        this.useMedianOnAvgPM = useMedianOnAvgPM;
        AladinLiteWrapper.getAladinLite().setCatalogAvgProperMotion(this.catalogue, this.showAvgPM, this.useMedianOnAvgPM, true);
    }
    
    public boolean getShowAvgProperMotion() {
        return this.showAvgPM;
    }
    
    public boolean getUseMedianOnAvgProperMotion() {
        return this.useMedianOnAvgPM;
    }

    @Override
	public void setColor(String color) {
		defaultEntity.setColor(color);
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
		removeSourceLimitNotificationNow();
	}
	
	@Override
	public void addShapes(TapRowList rowList) {
		defaultEntity.addShapes(rowList);
		if(rowList.getData().size() >= getSourceLimit()) {
			if(sourceLimitNotificationTimer.isRunning()) {
				sourceLimitNotificationTimer.run();
			}
			String sourceLimitDescription = descriptor.getSourceLimitDescription();
			String orderBy = getOrderByDescription();
			if (sourceLimitDescription.contains("|")) {
				String[] sourceLimitArr = sourceLimitDescription.split("\\|");
				orderBy = sourceLimitArr[1];
				sourceLimitDescription = sourceLimitArr[0];
			} 
			sourceLimitDescription = TextMgr.getInstance().getText(sourceLimitDescription).replace("$sourceLimit$", getSourceLimit() + "").replace("$orderBy$", orderBy).replace("$mostOrLeast$", orderBy.toLowerCase());
			CommonEventBus.getEventBus().fireEvent( 
					new ProgressIndicatorPushEvent(getEsaSkyUniqId() + "SourceLimit", sourceLimitDescription, true));
			sourceLimitNotificationTimer.schedule(6000);
		}
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
	public void hoverStart(int hoveredRowId) {
		defaultEntity.hoverStart(hoveredRowId);
	}

	@Override
	public void hoverStop(int hoveredRowId) {
		defaultEntity.hoverStop(hoveredRowId);
	}
	
	public SourceShape buildShape(int shapeId, TapRowList sourceList) {
        SourceShape mySource = new SourceShape();
        mySource.setShapeId(shapeId);

        Double dec = Double.parseDouble(getTAPDataByTAPName(sourceList, shapeId,
                getDescriptor().getPolygonDecTapColumn()).toString());
        Double ra = Double.parseDouble(getTAPDataByTAPName(sourceList, shapeId,
        		getDescriptor().getPolygonRaTapColumn()).toString());
        mySource.setDec(dec.toString());
        mySource.setRa(ra.toString());
        mySource.setSourceName(((String) getTAPDataByTAPName(sourceList, shapeId,
                descriptor.getUniqueIdentifierField())).toString());

        Map<String, String> details = new HashMap<String, String>();

        details.put(SourceConstant.SOURCE_NAME, mySource.getSourceName());

        details.put(EsaSkyWebConstants.SOURCE_TYPE,
                EsaSkyWebConstants.SourceType.CATALOGUE.toString());
        details.put(SourceConstant.CATALOGE_NAME, getEsaSkyUniqId());
        details.put(SourceConstant.IDX, Integer.toString(shapeId));

        if (this.getDescriptor().getExtraPopupDetailsByTapName() == null) {
            details.put(SourceConstant.EXTRA_PARAMS, null);
        } else {
            details.put(SourceConstant.EXTRA_PARAMS,
                    this.getDescriptor().getExtraPopupDetailsByTapName());
            String[] extraDetailsTapName = this.getDescriptor().getExtraPopupDetailsByTapName()
                    .split(",");

            for (String currTapName : extraDetailsTapName) {

                MetadataDescriptor cmd = this.getDescriptor()
                        .getMetadataDescriptorByTapName(currTapName);
                Integer precision = null;
                String value = (String) getTAPDataByTAPName(sourceList, shapeId, currTapName);
                if (cmd.getMaxDecimalDigits() != null
                        && (cmd.getType() == ColumnType.RA || cmd.getType() == ColumnType.DEC || cmd
                                .getType() == ColumnType.DOUBLE)) {
                    StringBuilder sb = new StringBuilder();
                    precision = cmd.getMaxDecimalDigits();
                    sb.append("#0.");
                    if (precision != null) {
                        for (int i = 0; i < precision; i++) {
                            sb.append("0");
                        }
                    } else {
                        sb.append("00");
                    }
                    value = NumberFormat.getFormat(sb.toString()).format(Double.parseDouble(value));
                }
                details.put(currTapName, value);
            }
        }

        if (this.getDescriptor().getDrawSourcesFunction() != null) {

            // Adds the details for drawing the proper motion arrows
            try {

                Double finalRa = null;
                Double finalDec = null;

                if ((this.getDescriptor().getFinalRaTapColumn() != null)
                        && this.getDescriptor().getFinalDecTapColumn() != null) {

                    // Proper motion ra, dec is coming from descriptor data, just use it
                    finalRa = getDoubleByTAPName(sourceList, shapeId, this.getDescriptor().getFinalRaTapColumn(), null);
                    finalDec = getDoubleByTAPName(sourceList, shapeId, this.getDescriptor().getFinalDecTapColumn(), null);

                } else {

                    // Proper motion ra, dec not coming from descriptor data, so we need to
                    // calculate it
                    
                    final Double pm_ra = getDoubleByTAPName(sourceList, shapeId, this.getDescriptor().getPmRaTapColumn(), null);
                    final Double pm_dec = getDoubleByTAPName(sourceList, shapeId, this.getDescriptor().getPmDecTapColumn(), null);
                    
                    if ((pm_ra != null) && (pm_dec != null)
                        && (this.getDescriptor().getPmOrigEpoch() != null)
                        && (this.getDescriptor().getPmFinalEpoch() != null)) {
                        
                        double[] inputA = new double[6];
                        inputA[0] = ra;
                        inputA[1] = dec;
                        inputA[2] = getDoubleByTAPName(sourceList, shapeId, this.getDescriptor().getPmPlxTapColumn(), 0.0); // Consider the parallax as 0.0 by default
                        inputA[3] = pm_ra;
                        inputA[4] = pm_dec;
                        inputA[5] = getDoubleByTAPName(sourceList, shapeId, this.getDescriptor().getPmNormRadVelTapColumn(), 0.0);// normalised radial velocity at t0 [mas/yr] - see Note 2
    
                        double[] outputA = new double[6];
    
                        ProperMotionUtils.pos_prop(this.getDescriptor().getPmOrigEpoch(), inputA,
                                this.getDescriptor().getPmFinalEpoch(), outputA);
    
                        finalRa = outputA[0];
                        finalDec = outputA[1];
                        
                        if (this.getDescriptor().getPmOrigEpoch() > this.getDescriptor().getPmFinalEpoch()) {
                            // For catalogs in J2015 put the source in J2015 but draw the arrow flipped... from J2000 to J2015
                            details.put("arrowFlipped", "true");
                        }
                    }
                }

                if ((finalRa != null) && (finalDec != null)) {
                    details.put("arrowRa", finalRa + "");
                    details.put("arrowDec", finalDec + "");
    
                    // Creates the ra dec normalized vector of 10 degrees
                    final double raInc = finalRa - ra;
                    final double decInc = finalDec - dec;
                    final double m = Math.sqrt((raInc * raInc) + (decInc * decInc));
                    final double mRatio = 2 / m; // 2 degrees
                    final double raNorm = ra + (raInc * mRatio);
                    final double decNorm = dec + (decInc * mRatio);
                    
                    //Calculates the scale factor, this function is also in AladinESDC.js (modify there also)
                    final double arrowRatio = 4.0 - (3.0 * Math.pow(Math.log((m * 1000) + 2.72), -2.0)); // See: //rechneronline.de/function-graphs/  , using function: 4 - (3*(log((x* 1000)+2.72)^(-2)))
                    
                    details.put("arrowRaNorm", raNorm + "");
                    details.put("arrowDecNorm", decNorm + "");
                    details.put("arrowRatio", arrowRatio + "");
                }
                
            } catch (Exception ex) {
                Log.error(this.getClass().getSimpleName() + ", Calculate proper motion error: ", ex);
            }
        }

        mySource.setJsObject(AladinLiteWrapper.getAladinLite().newApi_createSourceJSObj(
                mySource.getRa(), mySource.getDec(), details, shapeId));
        return mySource;
    }
	
	@Override
    public void fetchData(final AbstractTablePanel tablePanel) {
		defaultEntity.fetchData(tablePanel);
	}

	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		defaultEntity.setShapeBuilder(shapeBuilder);
	}

	@Override
	public AbstractTablePanel createTablePanel() {
		return new SourcesTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
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
	public Image getTypeLogo() {
		return defaultEntity.getTypeLogo();
	}
	
	protected int getSourceLimit() {
		return descriptor.getSourceLimit();
	}
	
	protected String getOrderByDescription() {
		return "";
	}
	
	public void removeSourceLimitNotificationNow() {
		sourceLimitNotificationTimer.run();
	}
}