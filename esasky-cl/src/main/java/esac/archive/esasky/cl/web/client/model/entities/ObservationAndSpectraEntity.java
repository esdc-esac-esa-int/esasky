package esac.archive.esasky.cl.web.client.model.entities;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback;
import esac.archive.esasky.cl.web.client.callback.MetadataCallback.OnComplete;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.PolygonShape;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.query.TAPMetadataMOCService;
import esac.archive.esasky.cl.web.client.query.TAPMetadataObservationService;
import esac.archive.esasky.cl.web.client.query.TAPUtils;
import esac.archive.esasky.cl.web.client.status.CountObserver;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.resultspanel.CommonObservationsTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.StylePanelCallback;;

public abstract class ObservationAndSpectraEntity extends CommonObservationEntity {

    private CommonObservationDescriptor descriptor;
	
	public class MocBuilder implements ShapeBuilder{

		@Override
		public Shape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject row) {
			PolygonShape shape = new PolygonShape();
			if(Modules.useTabulator) {
				shape.setStcs(row.invokeFunction("getData", null).getStringProperty(getDescriptor().getMocSTCSColumn()));
			} else {
		    	shape.setStcs((String)getTAPDataByTAPName(rowList, rowId, descriptor.getMocSTCSColumn()));
			}
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
    public void fetchData(final ITablePanel tablePanel) {
        
    	if(!getCountStatus().hasMoved(descriptor.getMission())) {
        	fetchData2(tablePanel);
        	
        }else {
        	
	        getCountStatus().registerObserver(new CountObserver() {
				@Override
				public void onCountUpdate(int newCount) {
					fetchData2(tablePanel);				
					getCountStatus().unregisterObserver(this);
				}
			});
        }
    }
    
    private void fetchData2(ITablePanel tablePanel) {
    	int mocLimit = descriptor.getMocLimit();
    	int count = getCountStatus().getCount(descriptor.getMission());
    	
    	if (DeviceUtils.isMobile()){
    		mocLimit = EsaSkyWebConstants.MAX_SOURCES_FOR_MOBILE;
    	}
    	
    	if (mocLimit > 0 && count > mocLimit) {
    		defaultEntity.setShapeBuilder(new MocBuilder());
    		getMocMetadata(tablePanel);
    	}else {
    		defaultEntity.setShapeBuilder(shapeBuilder);
    		defaultEntity.fetchData(tablePanel);
    	}
    }
    

    
    private void getMocMetadata(final ITablePanel tablePanel) {
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
	public ITablePanel createTablePanel() {
		if(Modules.useTabulator) {
			return new TabulatorTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
		} else {
			return new CommonObservationsTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
		}
	}
	
	@Override
	public StylePanel createStylePanel() {
		return new StylePanel(getEsaSkyUniqId(), getTabLabel(), getColor(), getSize(), 
				null, null, null, null, null, null, null, 
				new StylePanelCallback() {
					
					@Override
					public void onShapeSizeChanged(double value) {
						setSizeRatio(value);
					}
					
					@Override
					public void onShapeColorChanged(String color) {
						getDescriptor().setHistoColor(color);
					}
					
					@Override
					public void onShapeChanged(String shape) {
					}
					
					@Override
					public void onOrbitScaleChanged(double value) {
					}
					
					@Override
					public void onOrbitColorChanged(String color) {
					}
					
					@Override
					public void onArrowScaleChanged(double value) {
					}
					
					@Override
					public void onArrowColorChanged(String color) {
					}
					
					@Override
					public void onArrowAvgCheckChanged(boolean checkedOne, boolean checkedTwo) {
					}
				});
	}
}