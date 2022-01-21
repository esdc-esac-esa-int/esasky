package esac.archive.esasky.cl.web.client.view.resultspanel.tabulator;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.api.Api;
import esac.archive.esasky.cl.web.client.event.*;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.MultiRetrievalBeanListMapper;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.utility.SampConstants.SampAction;
import esac.archive.esasky.cl.web.client.utility.samp.SampMessageItem;
import esac.archive.esasky.cl.web.client.utility.samp.SampXmlParser;
import esac.archive.esasky.cl.web.client.view.JupyterDownloadDialog;
import esac.archive.esasky.cl.web.client.view.common.AutoHidingMovablePanel;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.resultspanel.*;
import esac.archive.esasky.cl.web.client.view.resultspanel.ToggleColumnsDialogBox.ToggleColumnAction;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataVisibilityObserver;
import esac.archive.esasky.ifcs.model.multiretrievalbean.MultiRetrievalBean;
import esac.archive.esasky.ifcs.model.multiretrievalbean.MultiRetrievalBeanList;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TabulatorTablePanel extends Composite implements ITablePanel, TabulatorCallback {

	JupyterDownloadDialog dialog  = new JupyterDownloadDialog();

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
	public interface DescriptorMapper extends ObjectMapper<IDescriptor> {}
	private TabulatorWrapper table;
	/** is the esaSkyUniqID, the same saved into the Entities. */
	private String esaSkyUniqID;
	private String tabulatorContainerId;
	private String tabTitle;
    protected int numberOfShownRows;

	private List<TableObserver> observers = new LinkedList<TableObserver>();

	private LoadingSpinner loadingSpinner = new LoadingSpinner(false);

	private FlowPanel tableNotShowingContainer = new FlowPanel();
	private FlowPanel tableAndGroupHeader = new FlowPanel();

	private boolean isHidingTable = false;
	private boolean hasBeenClosed = false;
	private boolean isShowing = true;
	private boolean inMOCMode = false;
	private boolean toggleColumnsEnabled = true;

	private MetadataVisibilityObserver metadataVisibilityObserver = new MetadataVisibilityObserver() {

        @Override
        public void onVisibilityChange(String tapName, boolean visible) {
            if(isShowing) {
                setColumnVisibility(tapName, visible);
            } else {
                columnVisibilityChangeToDo.put(tapName, visible);
            }
        }

    };

    private Map<String, Boolean> columnVisibilityChangeToDo = new HashMap<String, Boolean>();

    private void setColumnVisibility(String tapName, boolean visible) {
        if(visible) {
            table.showColumn(tapName);
        } else {
            table.hideColumn(tapName);
        }
    }
	protected GeneralEntityInterface entity;

	private class TableFocusPanel extends FocusPanel {
		public TableFocusPanel() {
			sinkEvents(Event.ONCONTEXTMENU);
		}

		@Override
		public final void onBrowserEvent(final Event event) {
			if (DOM.eventGetType(event) == Event.ONCONTEXTMENU) {
			    if(Modules.getModule(EsaSkyWebConstants.MODULE_TOGGLE_COLUMNS) && toggleColumnsEnabled) {
			        this.openContextMenu(event);
			    }
			} else {
				super.onBrowserEvent(event);
			}
		}

		private void openContextMenu(final Event event) {
		    openToggleColumnDialog();
		}

		public void openToggleColumnDialog() {
		    if(table.getColumns().length > 0) {
		        new ToggleColumnsDialogBox(entity.getDescriptor().getGuiLongName(), table.getColumns(), new ToggleColumnAction() {

		            @Override
		            public void onShow(String field) {
		                getDescriptor().setMetadataVisibility(field, true);
		            }

		            @Override
		            public void onHide(String field) {
		                getDescriptor().setMetadataVisibility(field, false);
		            }

                    @Override
                    public void multiSelectionInProgress() {
                        table.blockRedraw();
                    }

                    @Override
                    public void multiSelectionInFinished() {
                        table.restoreRedraw();
                        table.redrawAndReinitializeHozVDom();
                    }
		        }, esaSkyUniqID);
		    }
		}
	}

	private TableFocusPanel tableFocusPanel;
	public TabulatorTablePanel(final String inputLabel, final String inputEsaSkyUniqID, GeneralEntityInterface entity) {
		this.esaSkyUniqID = inputEsaSkyUniqID;
        this.tabTitle = inputLabel;
        this.entity = entity;
        this.tabulatorContainerId = "tabulatorContainer_" + esaSkyUniqID.replaceAll("[^A-Za-z0-9-_]", "_");

        if(entity.getDescriptor() instanceof ExtTapDescriptor
                || "publications".equals(entity.getDescriptor().getIcon())) {
            disableToggleColumns();
        }

		FlowPanel container = new FlowPanel();
		container.addStyleName("dataPanelContainer");

		tableNotShowingContainer.addStyleName("tableNotShowingContainer");
		tableNotShowingContainer.add(loadingSpinner);
		container.add(tableNotShowingContainer);

		tableAndGroupHeader.getElement().setId(tabulatorContainerId);

		tableFocusPanel = new TableFocusPanel();
		tableFocusPanel.addStyleName("dataPanelHoverDetector");
		tableFocusPanel.add(tableAndGroupHeader);
		container.add(tableFocusPanel);

		initWidget(container);

		DOM.sinkEvents(getElement(), Event.ONMOUSEDOWN | Event.ONMOUSEMOVE | Event.ONCLICK | Event.ONMOUSEUP
				| Event.ONMOUSEOVER | Event.ONTOUCHSTART | Event.ONTOUCHMOVE);
		DOM.sinkEvents(RootPanel.get().getElement(),
				Event.ONMOUSEMOVE | Event.ONTOUCHMOVE | Event.ONMOUSEUP | Event.ONTOUCHEND | Event.ONTOUCHCANCEL);
	}

	@Override
	protected void onAttach() {
	    super.onAttach();
        table = new TabulatorWrapper(tabulatorContainerId, this, entity.getTabulatorSettings());
        getDescriptor().registerMetadataVisibilityObserver(metadataVisibilityObserver);
	}

	@Override
	protected void onDetach() {
	    super.onDetach();
	    getDescriptor().unregisterMetadataVisibilityObserver(metadataVisibilityObserver);
	}

	private void disableToggleColumns() {
	    toggleColumnsEnabled = false;
	}

	public IDescriptor getDescriptor() {
		return entity.getDescriptor();
	}

	public GeneralEntityInterface getEntity() {
		return entity;
	}

	public final void clearTable() {
		tableNotShowingContainer.removeStyleName("displayNone");
		loadingSpinner.setVisible(true);
		table.clearTable();
	}

	public final String getEsaSkyUniqID() {
		return this.esaSkyUniqID;
	}

	public final String getLabel() {
		return tabTitle;
	}

	public void selectRow(int rowId) {
		if(isVisible()) {
			table.selectRow(rowId);
		} else {
			new SelectTimer(rowId).schedule(50);
		}
	}

	public void selectRows(int[] rowIds) {
		table.selectRows(rowIds);
	}

	public void deselectRow(int rowId) {
		table.deselectRow(rowId);
	}

	public void deselectRows(int[] rowIds) {
		table.deselectRows(rowIds);
	}

	public void deselectAllRows() {
		table.deselectAllRows();
	}

	public void hoverStartRow(int rowId) {
		table.hoverStart(rowId);
		entity.hoverStart(rowId);
	}

	public void hoverStopRow(int rowId) {
		table.hoverStop(rowId);
		entity.hoverStop(rowId);
	}

	public void selectTablePanel() {
	    if(isShowing) return;
		isShowing = true;
		for(TableObserver observer : observers) {
		    observer.onSelection(this);
		}

		table.show();
	}

	public void notifyObservers() {
		for(TableObserver observer : observers) {
		    observer.onUpdateStyle(this);
		}
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
		entity.clearAll();
		EntityRepository.getInstance().removeEntity(entity);
		notifyClosingObservers();
	}

	public boolean hasBeenClosed() {
		return hasBeenClosed;
	}

	public boolean getIsHidingTable() {
		return isHidingTable;
	}

	public void registerObserver(TableObserver observer) {
		observers.add(observer);
	}

	public void unregisterObserver(TableObserver observer) {
		if (observers.contains(observer)) {
			observers.remove(observer);
		}
	}

	public JSONObject exportAsJSON() {
	    return new JSONObject(JsonUtils.safeEval(table.exportTableAsJson()));
	}

	public JSONObject exportAsJSON(boolean applyFilters) {
		return new JSONObject(JsonUtils.safeEval(table.exportTableAsJson(applyFilters)));
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
		setPlaceholderText(emptyTableText);
		tableNotShowingContainer.addStyleName("displayNone");
	}

	public void showStylePanel(int x, int y) {
		if(stylePanel == null) {
			stylePanel = getEntity().createStylePanel();
		}

		getEntity().setStylePanelVisibility();

		stylePanel.toggle();
		stylePanel.setPopupPosition(x, y);
	}

	public void downloadSelected(DDRequestForm ddForm) {

        MultiRetrievalBeanList multiRetrievalList = new MultiRetrievalBeanList();

        for (GeneralJavaScriptObject tableRow : getSelectedRows()) {

            String url = GeneralJavaScriptObject.convertToString(tableRow.getProperty("product_url"));
            if (url == null || url.trim().isEmpty()) {
                url = GeneralJavaScriptObject.convertToString(tableRow.getProperty("access_url"));
            }
            if (url == null || "ASTRO_SPECTRA_CHEOPS".equals(entity.getDescriptor().getDescriptorId())) {
            	url = GeneralJavaScriptObject.convertToString(tableRow.getProperty("sci_cor_lc_opt_link"));
            }
            if (url == null || url.trim().isEmpty()) {
                continue;
            } else {
                MultiRetrievalBean multiRetrievalItem = new MultiRetrievalBean(
                        MultiRetrievalBean.TYPE_OBSERVATIONAL, getDescriptor().getMission(), url);
                multiRetrievalList.add(multiRetrievalItem);
                Log.debug("[Download Selected] DD URL: " + url);
            }
        }

        final int files = multiRetrievalList.getMultiRetrievalBeanList().size();
        GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_DOWNLOAD_DD, getFullId(), "Files: " + files);

        if (files < 1) {
            Window.alert("Cannot find any URL to download");
            return;
        }

        MultiRetrievalBeanListMapper mapper = GWT.create(MultiRetrievalBeanListMapper.class);

        String json = mapper.write(multiRetrievalList);

        ddForm.setAction(EsaSkyWebConstants.DATA_REQUEST_URL);
        ddForm.setMethod(FormPanel.METHOD_POST);
        ddForm.setJsonRequest(json);
        ddForm.submit();
	}

	public void updateData() {
		clearTable();
		clearFilters();
		getEntity().fetchData();
	}

	@Override
	public void setPlaceholderText(String text) {
		table.setPlaceholderText(text);
	}

	@Override
	public void insertData(String url) {
	    CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPushEvent("FetchingRealData" + esaSkyUniqID,
	                    TextMgr.getInstance().getText("tabulator_retrievingMissionData").replace("$NAME$", tabTitle),  url));

	    table.setDefaultQueryMode();
		table.setData(url);
		tableNotShowingContainer.addStyleName("displayNone");
	}

	@Override
	public void insertData(GeneralJavaScriptObject data) {
	    table.insertUserData(data);
	    tableNotShowingContainer.addStyleName("displayNone");
	}

	@Override
	public void insertHeader(GeneralJavaScriptObject data, String mode) {
	    table.setHeaderQueryMode(mode);
		table.insertUserHeader(data);
		tableNotShowingContainer.addStyleName("displayNone");
	}

	@Override
	public GeneralJavaScriptObject[] getSelectedRows() {
		return table.getSelectedRows();
	}

	@Override
	public GeneralJavaScriptObject[] getAllRows() {
		return table.getAllRows();
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
			if(tapFilters.containsKey(label) && tapFilter.equals(tapFilters.get(label))){
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
		boolean first = true;

		String filter = "";
		for(String key : getTapFilters().keySet()) {
			if(first) {
				first = false;
			} else {
				filter += " AND ";
			}
			filter += getTapFilters().get(key);
		}
		return filter;
	}

	public void clearFilters() {
		tapFilters = new HashMap<String, String>();
	}

	private LinkedList<TableFilterObserver> filterObservers = new LinkedList<>();

	public void registerFilterObserver(TableFilterObserver observer){
		if(!filterObservers.contains(observer)) {
			filterObservers.add(observer);
		}
	}

	private void notifyFilterObservers() {
		for(TableFilterObserver obs : filterObservers) {
			obs.filterChanged(tapFilters);
		}
	}

	@Override
	public String getVoTableString() {
		return table.getVot(getDescriptor().getGuiLongName());
	}

    @Override
    public void onDataLoaded(GeneralJavaScriptObject javaScriptObject) {
        if(!hasBeenClosed) {
            entity.addShapes(javaScriptObject);
        }
        notifyNumberOfRowsShowingChanged(GeneralJavaScriptObject.convertToArray(javaScriptObject).length);
		notifyOnDataLoaded(GeneralJavaScriptObject.convertToArray(javaScriptObject).length);
    }

    protected void notifyNumberOfRowsShowingChanged(int count) {
        for (TableObserver obs : observers) {
            obs.numberOfShownRowsChanged(count);
        }
    }

	protected void notifyOnDataLoaded(int count) {
		for (TableObserver obs : observers) {
			obs.onDataLoaded(count);
		}
	}

	protected void notifyOnRowSelection(final GeneralJavaScriptObject row) {
		for (TableObserver obs : observers) {
			obs.onRowSelected(row);
		}
	}

	protected void notifyOnRowDeselection(final GeneralJavaScriptObject row) {
		for (TableObserver obs : observers) {
			obs.onRowDeselected(row);
		}
	}

	protected void notifyDataFilterChanged(final List<Integer> indexArray){
		for (TableObserver obs : observers) {
			obs.onDataFilterChanged(indexArray);
		}
	}

    @Override
    public void onRowSelection(final GeneralJavaScriptObject row) {
		notifyOnRowSelection(row);
        entity.selectShapes(GeneralJavaScriptObject.convertToInteger(row.invokeFunction("getIndex")));
        CommonEventBus.getEventBus().fireEvent(new TableRowSelectedEvent(row.invokeFunction("getData")));
    }

    @Override
    public void onRowDeselection(final GeneralJavaScriptObject row) {
        entity.deselectShapes(GeneralJavaScriptObject.convertToInteger(row.invokeFunction("getIndex")));
		notifyOnRowDeselection(row);
    }

    @Override
    public void onDataFiltered(List<Integer> indexArray) {
        entity.hideAllShapes();
        entity.showShapes(indexArray);
		notifyDataFilterChanged(indexArray);
        this.numberOfShownRows = indexArray.size();
        notifyNumberOfRowsShowingChanged(numberOfShownRows);
    }

    @Override
    public int getNumberOfShownRows() {
        return numberOfShownRows;
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
    public void onDatalinkClicked(GeneralJavaScriptObject row) {
        String datalinkUrl = row.invokeFunction("getData").getStringProperty("access_url");
        if(datalinkUrl == null || datalinkUrl.isEmpty()) {
        	datalinkUrl = buildArchiveURL(row.invokeFunction("getData"));
        }
        if("https:".equals(Window.Location.getProtocol()) && datalinkUrl.startsWith("http:")){
            datalinkUrl = datalinkUrl.replaceFirst("http:", "https:");
        }
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_DOWNLOADROW, getFullId(), datalinkUrl);
        String title = row.invokeFunction("getData").getStringProperty(entity.getDescriptor().getUniqueIdentifierField());

        selectRowWhileDialogBoxIsOpen(row, new DatalinkDownloadDialogBox(datalinkUrl, title));
    }

    @Override
    public void onAccessUrlClicked(String url) {

		if (Api.getInstance().isPyesaskyClient()) {
			dialog.setDownloadUrl(url);
			dialog.show();
		} else {
			UrlUtils.openUrl(url);
		}

        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_DOWNLOADROW, getFullId(), url);
    }

    @Override
    public void onPostcardUrlClicked(GeneralJavaScriptObject row, String columnName) {
        String url = row.invokeFunction("getData").getStringProperty(columnName);
        String title = row.invokeFunction("getData").getStringProperty(entity.getDescriptor().getUniqueIdentifierField());
        selectRowWhileDialogBoxIsOpen(row, new PreviewDialogBox(url, title));
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_DOWNLOAD_PREVIEW, getFullId(), title);
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
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TABROW_RECENTER, getFullId(),
                rowData.getStringProperty(getDescriptor().getUniqueIdentifierField()));
    }

    @Override
    public void onSendToVoApplicaitionClicked(GeneralJavaScriptObject rowData) {
        String uniqueIdentifierField = rowData.getStringProperty(getDescriptor().getUniqueIdentifierField());
        if(getEntity().getDescriptor().getSampUrl() != null){
            executeSampFileList(uniqueIdentifierField);
            return;
        }

        String tableName = getLabel() + "-" + uniqueIdentifierField + "-" + GUISessionStatus.getNextUniqueSampNumber();
        HashMap<String, String> sampUrlsPerMissionMap = new HashMap<String, String>();

        // Display top progress bar...
        Log.debug("[sendSelectedProductToSampApp()] About to send 'show top progress bar...' event!!!");
        CommonEventBus.getEventBus().fireEvent(
                new ProgressIndicatorPushEvent(SampAction.SEND_PRODUCT_TO_SAMP_APP.toString(),
                        TextMgr.getInstance().getText("sampConstants_sendingViaSamp")
                        .replace(EsaSkyConstants.REPLACE_PATTERN, tableName)));

        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TABROW_SENDTOVOTOOLS, getFullId(), uniqueIdentifierField);
        String sampUrl = null;
        if (getEntity().getDescriptor().getDdBaseURL() != null && !getEntity().getDescriptor().getDdBaseURL().isEmpty()) {
            String tapName = getEntity().getDescriptor().getDdProductURI().split("@@@")[1];
            String valueURI = rowData.getStringProperty(tapName);
            sampUrl = getEntity().getDescriptor().getDdBaseURL() + getEntity().getDescriptor().getDdProductURI().replace("@@@" + tapName + "@@@", valueURI);
        }

        if (sampUrl == null) {
            sampUrl = rowData.getStringProperty("product_url");
        }
        if (sampUrl == null) {
            sampUrl = rowData.getStringProperty("access_url");
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
                            tableNameTmp = getDescriptor().getTapTable() + "_" + counter + "-" + GUISessionStatus.getNextUniqueSampNumber();
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

    private void selectRowWhileDialogBoxIsOpen(final GeneralJavaScriptObject row, AutoHidingMovablePanel dialogBox) {
        if(!GeneralJavaScriptObject.convertToBoolean(row.invokeFunction("isSelected"))) {
            row.invokeFunction("select");
            dialogBox.registerCloseObserver(new ClosingObserver() {

                @Override
                public void onClose() {
                    row.invokeFunction("deselect");
                }
            });
        }
    }

    @Override
    public void onLink2ArchiveClicked(GeneralJavaScriptObject row) {
        String url = buildArchiveURL(row.invokeFunction("getData"));
        if(url.toLowerCase().contains("datalink")) {
        	onDatalinkClicked(row);
        }else {
        	GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_OUTBOUND, GoogleAnalytics.ACT_OUTBOUND_CLICK, url);
        	Window.open(url, "_blank", "");
        }
    }

    private String buildArchiveURL(GeneralJavaScriptObject rowData) {
        String productURI = getDescriptor().getArchiveProductURI();
        RegExp regularExpression = RegExp.compile("@@@(.*?)@@@", "gm");

        for (MatchResult match = regularExpression.exec(getDescriptor().getArchiveProductURI()); match != null; match = regularExpression
                .exec(getDescriptor().getArchiveProductURI())) {
            String rowColumn = match.getGroup(1); // Group 1 is the match inside @s
            String valueURI = rowData.getStringProperty(rowColumn);
            productURI = productURI.replace("@@@" + rowColumn + "@@@", valueURI);
        }
        String url = getDescriptor().getArchiveURL() + productURI;
        if("https:".equals(Window.Location.getProtocol()) && url.startsWith("http:")){
            url = url.replaceFirst("http:", "https:");
        }
        return url;
    }

    @Override
    public void onSourcesInPublicationClicked(GeneralJavaScriptObject rowData) {
        CommonEventBus.getEventBus().fireEvent(new ShowPublicationSourcesEvent(rowData));
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TABROW_SOURCESINPUBLICATION, getFullId(), rowData.getStringProperty("bibcode"));
    }


	@Override
	public String getLabelFromTapName(String tapName) {
		MetadataDescriptor md = entity.getDescriptor().getMetadataDescriptorByTapName(tapName);
		if(md != null && md.getLabel() != null) {
			return md.getLabel();
		}
		return tapName;
	}

	@Override
	public GeneralJavaScriptObject getDescriptorMetaData() {
		return entity.getDescriptor().getMetaDataJSONObject();
	}

    @Override
    public void goToCoordinateOfFirstRow() {
        table.goToCoordinateOfFirstRow();
    }

	@Override
	public boolean isMOCMode() {
		return inMOCMode;
	}

	public void setMOCMode(boolean input) {
		this.inMOCMode = input;
	}

    @Override
    public void onAjaxResponse() {
        removeStatusMessage();
    }

    @Override
    public void onAjaxResponseError(String error) {
        removeStatusMessage();
        Log.error("Error fetching table data from server. " + " Error message: " + error);
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_REQUESTERROR, this.getClass().getSimpleName(),
                "Error fetching table data from server. " + " Error message: " + error);
    }
    private void removeStatusMessage() {
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("FetchingRealData" + esaSkyUniqID));
    }

    public void disableFilters() {
    	table.disableFilters();
    }

    public void enableFilters() {
    	table.enableFilters();
    }

	@Override
	public String getEsaSkyUniqId() {
		return entity.getEsaSkyUniqId();
	}

    @Override
    public void onTableHeightChanged() {
        for(String tapName : columnVisibilityChangeToDo.keySet()) {
            setColumnVisibility(tapName, columnVisibilityChangeToDo.get(tapName));
        }
        if(columnVisibilityChangeToDo.size() > 0) {
            table.redrawAndReinitializeHozVDom();
            columnVisibilityChangeToDo.clear();
        }
    }

    @Override
    public String getRaColumnName() {
        return getDescriptor().getTapRaColumn();
    }

    @Override
    public String getDecColumnName() {
        return getDescriptor().getTapDecColumn();
    }

    @Override
    public void openConfigurationPanel() {
        tableFocusPanel.openToggleColumnDialog();
    }

    @Override
    public boolean isDataProductDatalink() {
    	if(entity.getDescriptor().getDescriptorId().startsWith("HEASARC")) {
    		return true;
    	}
        return table.isDataProductDatalink();
    }

    @Override
    public void multiSelectionInProgress() {
        //Operation is fast enough for regular tables
    }

    @Override
    public void multiSelectionFinished() {
        //Operation is fast enough for regular tables
    }

	@Override
	public void onAddHipsClicked(GeneralJavaScriptObject rowData) {
		// Not needed
	}

	@Override
	public void filterOnFoV(String raCol, String decCol) {
		table.filterOnFov(raCol, decCol);
	}

	@Override
	public void setMaxHeight(int height) {
		if(tableAndGroupHeader != null && tableAndGroupHeader.getElement() != null) {
			tableAndGroupHeader.getElement().getStyle().setPropertyPx("height", height);
		}

	}

	@Override
	public void showColumn(String field) {
		table.showColumn(field);
	}

	@Override
	public void hideColumn(String field) {
		table.hideColumn(field);
	}

	@Override
	public void blockRedraw() {
		table.blockRedraw();
	}

	@Override
	public void restoreRedraw() {
		table.restoreRedraw();
	}

	@Override
	public void redrawAndReinitializeHozVDom() {
		table.redrawAndReinitializeHozVDom();
	}
}
