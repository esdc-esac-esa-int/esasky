package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowHoverEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ESASkySampEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.model.entities.CommonObservationEntity.DescriptorMapper;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DownloadUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.UncachedRequestBuilder;
import esac.archive.esasky.cl.web.client.utility.SampConstants.SampAction;
import esac.archive.esasky.cl.web.client.utility.samp.SampMessageItem;
import esac.archive.esasky.cl.web.client.utility.samp.SampXmlParser;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper.TabulatorCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;

public class TabulatorTablePanel extends Composite implements ITablePanel, TabulatorCallback {

	private class SelectTimer extends Timer {
		private final int rowId;
		private int numberOfTries = 0;

		public SelectTimer(int rowId) {
			super();
			this.rowId = rowId;
		}

		@Override
		public void run() {
			if(isVisible()) {
				table.selectRow(rowId);
			} else {
				numberOfTries++;
				if(numberOfTries > 30) {
					Log.error("Giving up scrollIntoView");
					return;
				}
				schedule(50);
			}
		}
	}

	public interface IPreviewClickedHandler {
		public void onPreviewClicked(final String id);
	}

	private TabulatorWrapper table;
	/** is the esaSkyUniqID, the same saved into the Entities. */
	private String esaSkyUniqID;
	private String tabulatorContainerId;
	private String tabTitle;
	private String adqlQueryUrl;

	private List<AbstractTableObserver> observers = new LinkedList<AbstractTableObserver>();

	protected Label emptyTableLabel = new Label();
	protected LoadingSpinner loadingSpinner = new LoadingSpinner(false);

	private FlowPanel tableNotShowingContainer = new FlowPanel();
	private FlowPanel tableAndGroupHeader = new FlowPanel();

	private boolean isHidingTable = false;
	private boolean hasBeenClosed = false;
	protected boolean isShowing = false;

	private IPreviewClickedHandler previewClickedHandler;
	private GeneralEntityInterface entity;

	private class TableFocusPanel extends FocusPanel {
		public TableFocusPanel() {
			sinkEvents(Event.ONCONTEXTMENU);
		}

		@Override
		public final void onBrowserEvent(final Event event) {
			if (DOM.eventGetType(event) == Event.ONCONTEXTMENU) {
//				this.openContextMenu(event);
			} else {
				super.onBrowserEvent(event);
			}
		}

//		private void openContextMenu(final Event event) {
////			ColumnSettingInfo[] columnDefinitions = new ColumnSettingInfo[4];
//			ColumnSettingInfo[] columnDefinitions = new ColumnSettingInfo[3];
////	 		{formatter:"rowSelection", titleFormatter:"rowSelection", align:"center", headerSort:false},
////		 	{title:"Name", field:"name", width:150, headerFilter:"input"},
////		 	{title:"Age", field:"age", align:"left", formatter:"progress",headerFilter:"input"},
////		 	{title:"Favourite Color", field:"col", headerFilter:"input"},
////		 	{title:"Date Of Birth", field:"dob", sorter:"date", align:"center",headerFilter:"input"},
//			columnDefinitions[0] = ColumnSettingInfo.createColumnSetting();
//			columnDefinitions[0].setStringProperty("formatter", "rowSelection");
//			columnDefinitions[0].setStringProperty("titleFormatter", "rowSelection");
//			columnDefinitions[0].setStringProperty("align", "center");
//			columnDefinitions[0].setBooleanProperty("headerSort", false);
//			columnDefinitions[1] = ColumnSettingInfo.createColumnSetting();
//			columnDefinitions[1].setStringProperty("formatter", "html");
//			columnDefinitions[1].setStringProperty("title", "Label");
//			columnDefinitions[1].setStringProperty("field", "label");
//			columnDefinitions[1].setStringProperty("align", "left");
//			columnDefinitions[1].setStringProperty("headerFilter", "input");
//			columnDefinitions[3] = ColumnSettingInfo.createColumnSetting();
//			columnDefinitions[3].setStringProperty("title", "TAP name");
//			columnDefinitions[3].setStringProperty("field", "tap_name");
//			columnDefinitions[3].setStringProperty("align", "left");
//			columnDefinitions[3].setStringProperty("headerFilter", "input");
//			columnDefinitions[2] = ColumnSettingInfo.createColumnSetting();
//			columnDefinitions[2].setStringProperty("title", "Description");
//			columnDefinitions[2].setStringProperty("field", "description");
//			columnDefinitions[2].setStringProperty("align", "left");
//			columnDefinitions[2].setStringProperty("headerFilter", "input");
//			new ToggleColumnsDialogBox(entity.getDescriptor().getGuiLongName(), columnInformationList, columnDefinitions,
//					new TabulatorCallback() {
//				
//				@Override
//				public void onAction(ColumnSettingInfo eventObject) {
//					ColumnAndHeader addedColumn = columnMap.get(eventObject.getStringProperty("tap_name"));
//					table.insertColumn(columnMap.get(eventObject.getStringProperty("tap_name")).initialIndex + 1, columnMap.get(eventObject.getStringProperty("tap_name")).column, columnMap.get(eventObject.getStringProperty("tap_name")).header);
//					columnInformationList[addedColumn.initialIndex].setBooleanProperty("is_hidden", false);
//				}
//			}, new TabulatorCallback() {
//				
//				@Override
//				public void onAction(ColumnSettingInfo eventObject) {
//					ColumnAndHeader removedColumn = columnMap.get(eventObject.getStringProperty("tap_name"));
//					table.removeColumn(removedColumn.column);
//					columnInformationList[removedColumn.initialIndex].setBooleanProperty("is_hidden", true);
//				}
//			}).show();
//		}
	}

	public TabulatorTablePanel(final String inputLabel, final String inputEsaSkyUniqID, GeneralEntityInterface entity) {
		this.esaSkyUniqID = inputEsaSkyUniqID;
		this.tabTitle = inputLabel;
		this.entity = entity;
		this.tabulatorContainerId = "tabulatorContainer_" + esaSkyUniqID.replaceAll(" ", "_").replaceAll(".", "_");
		exposeOpenFilterBoxMethodToJs(this);

		FlowPanel container = new FlowPanel();
		container.addStyleName("dataPanelContainer");

		tableNotShowingContainer.addStyleName("tableNotShowingContainer");
		tableNotShowingContainer.add(loadingSpinner);
		emptyTableLabel.setText(TextMgr.getInstance().getText("abstractTablePanel_loadingData"));
		tableNotShowingContainer.add(emptyTableLabel);
		container.add(tableNotShowingContainer);

		tableAndGroupHeader.getElement().setId(tabulatorContainerId);

		FocusPanel tableFocusPanel = new TableFocusPanel();
		tableFocusPanel.addStyleName("dataPanelHoverDetector");
		tableFocusPanel.add(tableAndGroupHeader);
		container.add(tableFocusPanel);

		initWidget(container);

		DOM.sinkEvents(getElement(), Event.ONMOUSEDOWN | Event.ONMOUSEMOVE | Event.ONCLICK | Event.ONMOUSEUP
				| Event.ONMOUSEOVER | Event.ONTOUCHSTART | Event.ONTOUCHMOVE);
		DOM.sinkEvents(RootPanel.get().getElement(),
				Event.ONMOUSEMOVE | Event.ONTOUCHMOVE | Event.ONMOUSEUP | Event.ONTOUCHEND | Event.ONTOUCHCANCEL);
	}

	private int scrollLeft = 0;

	public void setPreviewClickedHandler(IPreviewClickedHandler previewClickedHandler) {
		this.previewClickedHandler = previewClickedHandler;
	}

	public native void exposeOpenFilterBoxMethodToJs(ITablePanel tab) /*-{
		$wnd.openFilterBox = function(columnNumber) {
			tab.@esac.archive.esasky.cl.web.client.view.resultspanel.CommonObservationsTablePanel::openFilterBox(I)(columnNumber);
		}
	}-*/;

	public IDescriptor getDescriptor() {
		return entity.getDescriptor();
	}

	public GeneralEntityInterface getEntity() {
		return entity;
	}

//	public final Set<TableRow> getSelectedRows() {
//		Set<TableRow> visibleSet = new HashSet<TableRow>();
//
//		for (TableRow item : dataProvider.getList()) {
//			if (table.getSelectionModel().isSelected(item)) {
//				visibleSet.add(item);
//			}
//		}
//		return visibleSet;
//	}

	public final void clearTable() {
		tableNotShowingContainer.removeStyleName("displayNone");
		emptyTableLabel.setText(TextMgr.getInstance().getText("abstractTablePanel_loadingData"));
		loadingSpinner.setVisible(true);
	}

	private ColumnSettingInfo[] columnInformationList;

	private void addColumn(Column<TableRow, ?> column, SafeHtml header, int index, String label,
			MetadataDescriptor currentMTD) {
		if (header == null) {
//			table.addColumn(column, "");
//			columnMap.put(currentMTD.getTapName(), new ColumnAndHeader(column, SafeHtmlUtils.fromSafeConstant(table.getHeader(table.getColumnCount() - 1).toString()), index));
		} else {
//			table.addColumn(column, header);
//			columnMap.put(currentMTD.getTapName(), new ColumnAndHeader(column, header, index));
		}
		columnInformationList[index - 1] = ColumnSettingInfo.createColumnSetting(currentMTD.getVisible(), index, label,
				"Placeholder for description of column");
		columnInformationList[index - 1].setIntegerProperty("id", index);
		columnInformationList[index - 1].setStringProperty("label", label);
		columnInformationList[index - 1].setStringProperty("description", "placeholder description");
		columnInformationList[index - 1].setStringProperty("tap_name", currentMTD.getTapName());

//		public boolean isVisible;
//		public int initialIndex;
//		public String label;
//		public String description;
// 		{formatter:"rowSelection", titleFormatter:"rowSelection", align:"center", headerSort:false},
//	 	{title:"Name", field:"name", width:150, headerFilter:"input"},
//	 	{title:"Age", field:"age", align:"left", formatter:"progress",headerFilter:"input"},
	}

	public final String getEsaSkyUniqID() {
		return this.esaSkyUniqID;
	}

	public final String getLabel() {
		return tabTitle;
	}

	public final String getADQLQueryUrl() {
		return adqlQueryUrl;
	}

	public final void setADQLQueryUrl(final String inputADQLQueryUrl) {
		this.adqlQueryUrl = inputADQLQueryUrl;
	}

	public final String getADQLQueryForChosenRows() {
		String uniqueIdentifierField = getEntity().getDescriptor().getUniqueIdentifierField();
		IDescriptor descriptor = getEntity().getDescriptor();

		String adql = "SELECT * FROM " + descriptor.getTapTable() + " WHERE " + uniqueIdentifierField + " IN(";
		List<TableRow> subset = new ArrayList<TableRow>(getSelectedRows());
		if (subset.size() == 0) {
			subset = getFilteredRows();
		}

		for (TableRow row : subset) {
			adql += "'" + row.getElementByTapName(uniqueIdentifierField).getValue() + "',";
		}
		adql = adql.substring(0, adql.length() - 1) + ")";
		return adql;
	}

	public void selectRow(int rowId) {
		if(isVisible()) {
			table.selectRow(rowId);
		} else {
			new SelectTimer(rowId).schedule(50);
		}
	}

	public void deselectRow(int rowId) {
		table.deselectRow(rowId);
	}

	public void hoverStartRow(int rowId) {
		table.hoverStart(rowId);
		entity.hoverStart(rowId);
	}

	public void hoverStopRow(int rowId) {
		table.hoverStop(rowId);
		entity.hoverStop(rowId);
	}

	private boolean addColumnBoolean = false;

	public void openFilterBox(int columnNumber) {
//		((SortableColumn<?>) table.getColumn(columnNumber)).showFilter();
	}

	public void selectTablePanel() {
		isShowing = true;
//		exposeOpenFilterBoxMethodToJs(this);
	}

	private StylePanel stylePanel;

	public void deselectTablePanel() {
		isShowing = false;
		if(stylePanel != null) {
			stylePanel.removeFromParent();
		}
	}

	public void closeTablePanel() {
		isShowing = false;
		hasBeenClosed = true;
		if(stylePanel != null) {
			stylePanel.removeFromParent();
		}
		getEntity().clearAll();
		notifyClosingObservers();
	}

	public boolean hasBeenClosed() {
		return hasBeenClosed;
	}

	public boolean getIsHidingTable() {
		return isHidingTable;
	}

	public void registerObserver(AbstractTableObserver observer) {
		observers.add(observer);
	}

	public void unregisterObserver(AbstractTableObserver observer) {
		if (observers.contains(observer)) {
			observers.remove(observer);
		}
	}

	private void notifyObserversRowsChange(int numberOfVisibleRows) {
		for (AbstractTableObserver observer : observers) {
			observer.numberOfShownRowsChanged(numberOfVisibleRows);
		}
	}

	public String getUnfilteredRow(int rowIndex) {
		TapRowList rowList = getEntity().getMetadata();
		return rowList.getData().get(rowIndex).toString();
	}

	public JSONObject exportAsJSON() {
		JSONObject jsonData = new JSONObject();
		TapRowList rowList = getEntity().getMetadata();
		if (rowList == null) {
			return new JSONObject();
		}

		for (int rowIndex = 0; rowIndex < rowList.getData().size(); rowIndex++) {
			JSONObject rowData = new JSONObject();
			for (int cellIndex = 0; cellIndex < rowList.getMetadata().size(); cellIndex++) {
				Object cellData = rowList.getData().get(rowIndex).get(cellIndex);
				String cellString;
				if (cellData != null) {
					cellString = cellData.toString();
				} else {
					cellString = "";
				}
				rowData.put(rowList.getMetadata().get(cellIndex).getName(), new JSONString(cellString));
			}
			jsonData.put(Integer.toString(rowIndex), rowData);
		}
		return jsonData;
	}

	public void exportAsCsv() {
		table.downloadCsv(DownloadUtils.getValidFilename(getEntity().getEsaSkyUniqId()) + ".csv");
	}

	public void exportAsVot() {
		table.downloadVot(DownloadUtils.getValidFilename(getEntity().getEsaSkyUniqId()) + ".vot", "ESASky " + getDescriptor().getGuiLongName());
	}

	public Widget getWidget() {
		return this;
	}

	public String getFullId() {
		return getEntity().getEsaSkyUniqId() + "-" + getLabel();
	}

	public void setEmptyTable(String emptyTableText) {
		loadingSpinner.setVisible(false);
		emptyTableLabel.setText(emptyTableText);
	}

	public void showStylePanel(int x, int y) {
		if(stylePanel == null) {
			stylePanel = getEntity().createStylePanel();
		}

		stylePanel.toggle();
		stylePanel.setPopupPosition(x, y);
	}

	public void downloadSelected(DDRequestForm ddForm) {
	}

	public void updateData() {
		clearTable();
		getEntity().fetchData(this);
	}

	@Override
	public void toggleVisibilityOfFreeFlowingElements() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPreviewClickedHandler(
			esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel.IPreviewClickedHandler previewClickedHandler) {
		// TODO Auto-generated method stub

	}


	@Override
	public void insertData(List<TableRow> data, String url) {
		if(url != null) {
			table = new TabulatorWrapper(tabulatorContainerId, url, this, getDescriptor().getSampEnabled());
			tableNotShowingContainer.addStyleName("displayNone");
		}

	}

	@Override
	public void createSortableColumn(List<TableRow> list, Column<TableRow, ?> col, int colIdx) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<TableRow> getSelectedRows() {
		// TODO Auto-generated method stub
		return new HashSet<TableRow>();
	}

	@Override
	public List<TableRow> getFilteredRows() {
		// TODO Auto-generated method stub
		return new LinkedList<TableRow>();
	}

	@Override
	public void clearSelectionModel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshHeight() {

	}

	@Override
	public void removeData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resizeColumnGroupHeader() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSeparator(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void activateGroupHeaders() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hoverStartEntity(RowHoverEvent hoverEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hoverStopEntity(RowHoverEvent hoverEvent) {
		// TODO Auto-generated method stub
		
	}
	
	private LinkedList<ClosingObserver> closingObservers = new LinkedList<ClosingObserver>();
    
    public void registerClosingObserver(ClosingObserver observer) {
    	closingObservers.add(observer);
    }
    
    private void notifyClosingObservers() {
 	   for(ClosingObserver observer : closingObservers) {
 		   observer.onClose();
 	   }
    }
    
    private Map<String, String> tapFilters = new HashMap<String, String>();
    
    public Map<String, String> getTapFilters(){
    	return tapFilters;
    };
	
	private void addTapFilter(String label, String tapFilter) {
		boolean shouldNotify = true;
		if(tapFilter.length() > 0) {
			if(tapFilters.containsKey(label) && tapFilter == tapFilters.get(label)){
					shouldNotify = false;
			}
			tapFilters.put(label, tapFilter);
		}else if(tapFilters.containsKey(label)) {
			tapFilters.remove(label);
		}
		
		if(shouldNotify){
			notifyFilterObservers();
		}
	}
	
	public String getFilterString() {
		String filter = "";
		for(String key : getTapFilters().keySet()) {
			filter += " AND ";
			filter += getTapFilters().get(key);
		}
		return filter;
	}
	
	LinkedList<AbstractTableFilterObserver> filterObservers = new LinkedList<>();
	
	public void registerFilterObserver(AbstractTableFilterObserver observer){
		if(!filterObservers.contains(observer)) {
			filterObservers.add(observer);
		}
		
	}
	
	private void notifyFilterObservers() {
		for(AbstractTableFilterObserver obs : filterObservers) {
			obs.filterChanged(tapFilters);
		}
	}

	@Override
	public String getVoTableString() {
		return table.getVot(getDescriptor().getGuiLongName());
	}
	
    @Override
    public void onDataLoaded(GeneralJavaScriptObject javaScriptObject) {
        entity.addShapes(null, javaScriptObject);
    }

    @Override
    public void onRowSelection(final int rowId) {
        HashSet<ShapeId> selectionId = new HashSet<ShapeId>(1);
        selectionId.add(new ShapeId() {
            
            @Override
            public int getShapeId() {
                return rowId;
            }
        });
        entity.selectShapes(selectionId);
        
    }

    @Override
    public void onRowDeselection(final int rowId) {
        HashSet<ShapeId> selectionId = new HashSet<ShapeId>(1);
        selectionId.add(new ShapeId() {
            
            @Override
            public int getShapeId() {
                return rowId;
            }
        });
        entity.deselectShapes(selectionId);
    }
    
    @Override
    public void onDataFiltered(List<Integer> indexArray) {
        entity.hideAllShapes();
        entity.showShapes(indexArray);
    }

    @Override
    public void onRowMouseEnter(int rowId) {
        getEntity().hoverStart(rowId);
    }

    @Override
    public void onRowMouseLeave(int rowId) {
        getEntity().hoverStop(rowId);
    }

    @Override
    public void onFilterChanged(String label, String filter) {
        addTapFilter(label, filter);
    }
    
    @Override
    public void onDatalinkClicked(final GeneralJavaScriptObject row) {
        String datalinkUrl = row.invokeFunction("getData").getStringProperty("access_url");
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_DownloadRow, getFullId(), datalinkUrl);
        String title = row.invokeFunction("getData").getStringProperty("obs_id");

        DatalinkDownloadDialogBox datalinkBox = new DatalinkDownloadDialogBox(datalinkUrl, title);
        
        if(!GeneralJavaScriptObject.convertToBoolean(row.invokeFunction("isSelected"))) {
            row.invokeFunction("select");
            datalinkBox.registerCloseObserver(new ClosingObserver() {
               
                @Override
                public void onClose() {
                    row.invokeFunction("deselect");
                }
            });
        }
    }
    
    @Override
    public void onAccessUrlClicked(String url) {
        Window.open(url, "_blank", "_blank");
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_DownloadRow, getFullId(), url);
    }

    @Override
    public void onCenterClicked(GeneralJavaScriptObject rowData) {
        final String ra = rowData.getStringProperty(getDescriptor().getTapRaColumn());
        final String dec = rowData.getStringProperty(getDescriptor().getTapDecColumn());
        
        double fov = AladinLiteWrapper.getInstance().getFovDeg();
        if(rowData.getStringProperty(EsaSkyConstants.OBSCORE_FOV) != null 
                && rowData.getStringProperty(EsaSkyConstants.OBSCORE_FOV).isEmpty()
                && rowData.getStringProperty(EsaSkyConstants.OBSCORE_SREGION) != null 
                && rowData.getStringProperty(EsaSkyConstants.OBSCORE_SREGION).startsWith("POSITION")) {
            fov = Double.parseDouble(rowData.getStringProperty(EsaSkyConstants.OBSCORE_FOV)) * 4;
        }
        
        AladinLiteWrapper.getInstance().goToTarget(ra, dec, fov, false, AladinLiteWrapper.getInstance().getCooFrame());
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TabRow_Recenter, getFullId(), 
                rowData.getStringProperty(getDescriptor().getUniqueIdentifierField()));
    }

    @Override
    public void onSendToVoApplicaitionClicked(GeneralJavaScriptObject rowData) {
        String uniqueIdentifierField = rowData.getStringProperty(getDescriptor().getUniqueIdentifierField());
        if(getEntity().getDescriptor().getSampUrl() != null){
            executeSampFileList(uniqueIdentifierField);
            return;
        } 

        String tableName = getLabel() + "-" + uniqueIdentifierField;
        HashMap<String, String> sampUrlsPerMissionMap = new HashMap<String, String>();

        // Display top progress bar...
        Log.debug("[sendSelectedProductToSampApp()] About to send 'show top progress bar...' event!!!");
        CommonEventBus.getEventBus().fireEvent(
                new ProgressIndicatorPushEvent(SampAction.SEND_PRODUCT_TO_SAMP_APP.toString(),
                        TextMgr.getInstance().getText("sampConstants_sendingViaSamp")
                        .replace(EsaSkyConstants.REPLACE_PATTERN, tableName)));

        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TabRow_SendToVOTools, getFullId(), uniqueIdentifierField);
        String sampUrl = null;
        if (!getEntity().getDescriptor().getDdBaseURL().isEmpty()) {
            String tapName = getEntity().getDescriptor().getDdProductURI().split("@@@")[1];
            String valueURI = rowData.getStringProperty(tapName);
            sampUrl = getEntity().getDescriptor().getDdBaseURL() + getEntity().getDescriptor().getDdProductURI().replace("@@@" + tapName + "@@@", valueURI);
        }
        
        if (sampUrl == null) {
            sampUrl = rowData.getStringProperty("product_url");
        } 
        if (sampUrl == null) {
            Log.error("[sendSelectedProductToSampApp()] No DD Base URL "
                    + " nor Product URL found for "
                    + getLabel()
                    + " obsId:" + uniqueIdentifierField);
        }

        sampUrlsPerMissionMap.put(tableName, sampUrl);

        // Send all URL to Samp
        ESASkySampEvent sampEvent = new ESASkySampEvent(SampAction.SEND_PRODUCT_TO_SAMP_APP,
                sampUrlsPerMissionMap);
        CommonEventBus.getEventBus().fireEvent(sampEvent);
    }
    
    public void executeSampFileList(String obsId) {

        String completeUrl = EsaSkyWebConstants.TAP_CONTEXT + "/samp-files?";

        StringBuilder data = new StringBuilder();
        DescriptorMapper mapper = GWT.create(DescriptorMapper.class);

        String json = mapper.write(getDescriptor());

        data.append("descriptor=" + URL.encodeQueryString(json));
        data.append("&observation_id=" + obsId);

        Log.debug("[executeSampFileList] URL:" + completeUrl);
        Log.debug("[executeSampFileList] JSON:" + data.toString());
        completeUrl = completeUrl + data.toString();
        Log.debug("[executeSampFileList] CompleteURL:" + completeUrl);

        UncachedRequestBuilder requestBuilder = new UncachedRequestBuilder(
                RequestBuilder.GET, completeUrl);

        try {
            requestBuilder.sendRequest(null, new RequestCallback() {

                @Override
                public void onError(
                        final com.google.gwt.http.client.Request request,
                        final Throwable exception) {
                    Log.debug(
                            "[TabulatorTablePanel/executeSampFileList()] Failed file reading",
                            exception);
                }

                @Override
                public void onResponseReceived(final Request request,
                        final Response response) {
                    String data = "";
                    data = response.getText();
                    List<SampMessageItem> messageItems = SampXmlParser.parse(data);
                    try {

                        int counter = 0;
                        String tableNameTmp="";

                        // Send all URL to Samp
                        HashMap<String, String> sampUrlsPerMissionMap = new HashMap<String, String>();
                        for (SampMessageItem i : messageItems) {
                            // Prepare sending message
                            tableNameTmp = getDescriptor().getTapTable() + "_" + counter;
                            String fullUrl = getDescriptor().getDdBaseURL() + "?retrieval_type=PRODUCT&hcss_urn=" + i.getUrn();
                            if (fullUrl.contains("README")) {
                                continue;
                            }
                            sampUrlsPerMissionMap.put(tableNameTmp, fullUrl);
                            Log.debug("SAMP URL=" + fullUrl);
                            counter++;
                        }
                        ESASkySampEvent sampEvent  = new ESASkySampEvent(SampAction.SEND_PRODUCT_TO_SAMP_APP, sampUrlsPerMissionMap);
                        CommonEventBus.getEventBus().fireEvent(sampEvent);

                    } catch (Exception e) {

                        Log.debug("[TabulatorTablePanel/executeSampFileList()] Exception in ESASkySampEventHandlerImpl.processEvent",e);

                        throw new IllegalStateException(
                                "[TabulatorTablePanel.executeSampFileList] Unexpected SampAction: SEND_VO_TABLE");
                    }
                }

            });
        } catch (RequestException e) {
            Log.debug(
                    "[TabulatorTablePanel/executeSampFileList()] Failed file reading",
                    e);
        }
    }


}
