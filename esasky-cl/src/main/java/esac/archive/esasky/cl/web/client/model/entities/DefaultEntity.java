package esac.archive.esasky.cl.web.client.model.entities;

import java.util.List;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.ColorChangeObserver;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.SelectableImage;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.TapMetadata;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.AbstractMetadataService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;

public class DefaultEntity implements GeneralEntityInterface{

    private CountStatus countStatus;
    private SkyViewPosition skyViewPosition;
    private String histoLabel;
    private String esaSkyUniqId;
    private TapRowList metadata;
    private Long lastUpdate;
    private int tabNumber;
    private IDescriptor descriptor;
    private IShapeDrawer drawer;
    private AbstractMetadataService metadataService;
    
    protected EntityContext context;

    public DefaultEntity(IDescriptor descriptor, CountStatus countStatus, SkyViewPosition skyViewPosition,
    		String esaSkyUniqObsId, Long lastUpdate, EntityContext context, IShapeDrawer drawer, AbstractMetadataService metadataService) {
    	this.descriptor = descriptor;
        this.countStatus = countStatus;
        this.skyViewPosition = skyViewPosition;
        this.esaSkyUniqId = esaSkyUniqObsId;
        this.lastUpdate = lastUpdate;
        this.drawer = drawer;
        this.metadataService = metadataService;
        this.context = EntityContext.ASTRO_IMAGING;
        if (context != null) {
            this.context = context;
        }
        setTabNumber(descriptor.getTabCount());
        
		descriptor.registerColorChangeObservers(new ColorChangeObserver() {
			
			@Override
			public void onColorChange(IDescriptor descriptor, String newColor) {
				setColor(newColor);
			}
		});
    }

    public void setTabNumber(int number) {
        this.tabNumber = number;
    }

    public int getTabNumber() {
        return tabNumber;
    }

    public SkyViewPosition getSkyViewPosition() {
        return skyViewPosition;
    }

    public void setSkyViewPosition(SkyViewPosition skyViewPosition) {
        this.skyViewPosition = skyViewPosition;
    }

    public String getHistoLabel() {
        return histoLabel;
    }

    public void setHistoLabel(String histoLabel) {
        this.histoLabel = histoLabel;
    }

    public String getEsaSkyUniqId() {
        return esaSkyUniqId;
    }

    public void setEsaSkyUniqId(String esaSkyUniqId) {
        this.esaSkyUniqId = esaSkyUniqId;
    }

    public TapRowList getMetadata() {
        return metadata;
    }

    public void setMetadata(TapRowList metadata) {
        this.metadata = metadata;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getTabLabel() {
        return  getDescriptor().getGuiShortName();
    }

    public CountStatus getCountStatus() {
        return countStatus;
    }

    public IDescriptor getDescriptor() {
    	return descriptor;
    }

    public EntityContext getContext() {
        return context;
    }

    public void clearAll() {
        if (this.getMetadata() != null) {
            if (this.getMetadata().getMetadata() != null) {
                this.getMetadata().getMetadata().clear();
            }
            if (this.getMetadata().getData() != null) {
                this.getMetadata().getData().clear();
            }
        }
        drawer.removeAllShapes();
    }
    
    public String getColor() {
    	return this.getDescriptor().getHistoColor();
    }
    
    @Override
    public String getMetadataAdql() {
    	return metadataService.getMetadataAdql(getDescriptor());
    }

    public String getHeaderAdql() {
    	return metadataService.getHeaderAdql(getDescriptor());
    }
    
    @Override
    public SelectableImage getTypeIcon() {
    	// TODO Auto-generated method stub
    	return null;
    }
    
	@Override
	public AbstractTablePanel createTablePanel() {
		return null;
	}
    
	@Override
    public Object getTAPDataByTAPName(TapRowList tapRowList, int rowIndex, String tapName) {
		Object data = null;
        for (TapMetadata tapMetadata : tapRowList.getMetadata()) {
            if (tapMetadata.getName().equals(tapName)) {
                int dataIndex = new Integer(tapRowList.getMetadata().indexOf(tapMetadata));
                data = (tapRowList.getData().get(rowIndex)).get(dataIndex);
                break;
            }
        }
        return data;
    }

	@Override
    public Double getDoubleByTAPName(TapRowList tapRowList, int rowIndex, String tapName, Double defaultValue) {
        if (tapName != null) {
            String value = getTAPDataByTAPName(tapRowList, rowIndex, tapName).toString();
            if ((value != null) && (!value.isEmpty()) && (!value.equals("null"))) {
                return Double.parseDouble(value);
            }
        }
        
        return defaultValue;
    }
	
	@Override
    public void fetchData(final ITablePanel tablePanel) {
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
        	
        	@Override
        	public void execute() {
        		if(Modules.useTabulator) {
            		drawer.removeAllShapes();
        			clearAll();
        			tablePanel.insertData(null, TAPUtils.getTAPQuery(URL.encodeQueryString(getMetadataAdql()), EsaSkyConstants.JSON));
        		} else {
        			clearAll();
        			final String debugPrefix = "[fetchData][" + getDescriptor().getGuiShortName() + "]";
        			// Get Query in ADQL format.
        			final String adql = getMetadataAdql();
        			
        			String url = TAPUtils.getTAPQuery(URL.encodeQueryString(adql), EsaSkyConstants.JSON);
        			
        			Log.debug(debugPrefix + "Query [" + url + "]");
        			
        			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        			try {
        				builder.sendRequest(null, new MetadataCallback(tablePanel, adql,
        						TextMgr.getInstance().getText(metadataService.getRetreivingDataTextKey()).replace("$NAME$", getDescriptor().getGuiShortName())));
        				
        			} catch (RequestException e) {
        				Log.error(e.getMessage());
        				Log.error(debugPrefix + "Error fetching JSON data from server");
        			}
        		}
        	}
        });
	}
	
	@Override
	public void coneSearch(final ITablePanel tablePanel, final SkyViewPosition conePos) {
		Scheduler.get().scheduleFinally(new ScheduledCommand() {
			
			@Override
			public void execute() {
				clearAll();
				final String debugPrefix = "[fetchData][" + getDescriptor().getGuiShortName() + "]";
				// Get Query in ADQL format.
				final String adql = metadataService.getMetadataAdqlRadial(descriptor, conePos);
				
				String url = TAPUtils.getTAPQuery(URL.encodeQueryString(adql), "json");
				
				Log.debug(debugPrefix + "Query [" + url + "]");
				
				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
				
//        		new StreamRequest(url, new MetadataStreamCallback(tablePanel, adql, "Test"));
				
				try {
					
					builder.sendRequest(null, new MetadataCallback(tablePanel, adql,
							TextMgr.getInstance().getText(metadataService.getRetreivingDataTextKey()).replace("$NAME$", getDescriptor().getGuiShortName())));
					
				} catch (Exception e) {
					Log.error(e.getMessage());
					Log.error(debugPrefix + "Error fetching JSON data from server");
				}
			}
		});
	}
	
	 public void fetchHeaders(final ITablePanel tablePanel) {
		 if(Modules.useTabulator) {
// 			clearAll();
 			tablePanel.insertData(null, TAPUtils.getTAPQuery(URL.encodeQueryString(getHeaderAdql()), EsaSkyConstants.JSON));
 		}
	 }
	
	
	public void setDrawer(IShapeDrawer drawer) {
		this.drawer = drawer;
	}

	@Override
	public void setSizeRatio(double size) {
		drawer.setSizeRatio(size);
	}
	
	@Override
	public double getSize() {
		return drawer.getSize();
	}

	@Override
	public void removeAllShapes() {
		drawer.removeAllShapes();
	}

	@Override
	public void selectShapes(Set<ShapeId> shapes) {
		drawer.selectShapes(shapes);
	}

	@Override
	public void deselectShapes(Set<ShapeId> shapes) {
		drawer.deselectShapes(shapes);
	}

	@Override
	public void deselectAllShapes() {
		drawer.deselectAllShapes();
	}

	@Override
	public void showShape(int rowId) {
		drawer.showShape(rowId);
	}

	@Override
	public void showShapes(List<Integer> shapeIds) {
		drawer.showShapes(shapeIds);
	}

	@Override
	public void showAndHideShapes(List<Integer> rowIdsToShow, List<Integer> rowIdsToHide) {
		drawer.showAndHideShapes(rowIdsToShow, rowIdsToHide);
	}

	@Override
	public void hideShape(int rowId) {
		drawer.hideShape(rowId);
	}

	@Override
	public void hideShapes(List<Integer> shapeIds) {
		drawer.hideShapes(shapeIds);
	}
	
	@Override
	public void hideAllShapes() {
		drawer.hideAllShapes();
	}

	@Override
	public void hoverStart(int hoveredRowId) {
		drawer.hoverStart(hoveredRowId);
	}

	@Override
	public void hoverStop(int hoveredRowId) {
		drawer.hoverStop(hoveredRowId);
	}

	@Override
	public void setColor(String color) {
		drawer.setColor(color);
	}

	@Override
	public void addShapes(TapRowList rowList, GeneralJavaScriptObject javaScriptObject) {
		drawer.addShapes(rowList, javaScriptObject);
	}

	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		drawer.setShapeBuilder(shapeBuilder);
	}

	@Override
	public boolean isSampEnabled() {
		return true;
	}

	@Override
	public boolean isRefreshable() {
		return true;
	}

	@Override
	public boolean hasDownloadableDataProducts() {
		return true;
	}

	@Override
	public boolean isCustomizable() {
		return true;
	}

	@Override
	public Image getTypeLogo() {
		return null;
	}

	@Override
	public void refreshData(ITablePanel tablePanel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StylePanel createStylePanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fetchDataWithoutMOC(ITablePanel tablePanel) {
		fetchData(tablePanel);
		
	}
}
