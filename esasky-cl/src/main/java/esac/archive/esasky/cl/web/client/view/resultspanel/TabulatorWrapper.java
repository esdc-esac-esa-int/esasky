package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.IsShowingCoordintesInDegreesChangeEvent;
import esac.archive.esasky.cl.web.client.event.IsShowingCoordintesInDegreesChangeEventHandler;
import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.repository.MocRepository;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.animation.OpacityAnimation;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.DateFilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.RangeFilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.ValueFormatter;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.FilterDialogBox;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;


public class TabulatorWrapper{

    public interface TabulatorCallback {
        public void onDataLoaded(GeneralJavaScriptObject rowData);
        public void onTableHeightChanged();
        public void onRowSelection(GeneralJavaScriptObject row);
        public void onRowDeselection(GeneralJavaScriptObject row);
        public void onRowMouseEnter(int rowId);
        public void onRowMouseLeave(int rowId);
        public void onFilterChanged(String label, String filter);
        public void onDataFiltered(List<Integer> filteredRows);
        public void onDatalinkClicked(GeneralJavaScriptObject javaScriptObject);
        public void onAccessUrlClicked(String url);
        public void onPostcardUrlClicked(GeneralJavaScriptObject rowData, String columnName);
        public void onCenterClicked(GeneralJavaScriptObject rowData);
        public void onSendToVoApplicaitionClicked(GeneralJavaScriptObject rowData);
        public void onLink2ArchiveClicked(GeneralJavaScriptObject rowData);
        public void onSourcesInPublicationClicked(GeneralJavaScriptObject rowData);
        public void onAjaxResponse();
        public void onAjaxResponseError(String error);
        public String getLabelFromTapName(String tapName);
        public GeneralJavaScriptObject getDescriptorMetaData();
        public String getRaColumnName();
        public String getDecColumnName();
        public boolean isMOCMode();
        public String getEsaSkyUniqId();
        public void multiSelectionInProgress();
        public void multiSelectionFinished();
        public boolean hasBeenClosed();
    }

    private TabulatorCallback tabulatorCallback;
    private GeneralJavaScriptObject tableJsObject;
    private Map<String, FilterDialogBox> filterDialogs = new HashMap<>();
    private long lastHoverTime = 0;
    private int lastHoveredRow = -1;
    private String rowCountFooterId;
    private final Timer resultInformationAreaTimer;
    private final OpacityAnimation resultInformationAnimation;
    private boolean filtersShouldBeEnabled = true;
    private boolean waitingForMoc = false;

    public TabulatorWrapper(String divId, TabulatorCallback tabulatorCallback, 
            boolean addSendToVOApplicationColumn, boolean addLink2ArchiveColumn, boolean addLink2AdsColumn,  boolean addSourcesInPublicationColumn) {
        this(divId, tabulatorCallback, addSendToVOApplicationColumn, addLink2ArchiveColumn, addLink2AdsColumn, addSourcesInPublicationColumn, null, false, true);
    }
    
    public TabulatorWrapper(String divId, TabulatorCallback tabulatorCallback, 
            boolean addSendToVOApplicationColumn, boolean addLink2ArchiveColumn, boolean addLink2AdsColumn, boolean addSourcesInPublicationColumn, String selectionHeaderTitle, boolean blockRedraw, boolean isEsaskyData) {
        this.tabulatorCallback = tabulatorCallback;
        tableJsObject = createColumnTabulator(this, divId, addSendToVOApplicationColumn, addLink2ArchiveColumn, addLink2AdsColumn, addSourcesInPublicationColumn, selectionHeaderTitle, blockRedraw, isEsaskyData);
        CommonEventBus.getEventBus().addHandler(IsShowingCoordintesInDegreesChangeEvent.TYPE, new IsShowingCoordintesInDegreesChangeEventHandler() {
            
            @Override
            public void onEvent() {
            	reformat(tableJsObject);
            }
        });
        
        
        rowCountFooterId = divId + "_rowCount";
        final Element rowCountFooter = Document.get().getElementById(rowCountFooterId);
        resultInformationAnimation = new OpacityAnimation(rowCountFooter);
        
        resultInformationAreaTimer = new Timer() {
            
            @Override
            public void run() {
                resultInformationAnimation.animateTo(1, 500);
                rowCountFooter.getStyle().setProperty("pointerEvents", "auto");
                cancel();
            }
        };
    }
    
    public void onRowCountFooterMouseOver() {
        Element rowCountFooter = Document.get().getElementById(rowCountFooterId);
        resultInformationAnimation.animateTo(0, 500);
        resultInformationAreaTimer.schedule(4000);
        rowCountFooter.getStyle().setProperty("pointerEvents", "none");
    }
    
    public void selectRow(int rowId) {
        GeneralJavaScriptObject row = tableJsObject.invokeFunction("getRow", "" + rowId);
        row.invokeFunction("select");
        row.invokeFunction("scrollTo");
    }

    public void selectRows(int[] rowIds) {
    	String json = "[";
    	for(int rowId : rowIds) {
    		json += rowId;
    		json += ",";
    	}
    	json = json.substring(0, json.length() - 1 );
    	json += "]";
    	GeneralJavaScriptObject rows = GeneralJavaScriptObject.createJsonObject(json);
    	selectRows(rows);
    }

    public void deselectRows(int[] rowIds) {
    	String json = "[";
    	for(int rowId : rowIds) {
    		json += rowId;
    		json += ",";
    	}
    	json = json.substring(0, json.length() - 1 );
    	json += "]";
    	GeneralJavaScriptObject rows = GeneralJavaScriptObject.createJsonObject(json);
    	deselectRows(rows);
    }

    public void selectRows(GeneralJavaScriptObject rowIdArray) {
        selectRows(tableJsObject, rowIdArray);
    }
    
    private native void selectRows(GeneralJavaScriptObject tableJsObject, GeneralJavaScriptObject rowIdArray)/*-{
        tableJsObject.selectRow(rowIdArray);
    }-*/;

    public void deselectRows(GeneralJavaScriptObject rowIdArray) {
    	deselectRows(tableJsObject, rowIdArray);
    }
    
    private native void deselectRows(GeneralJavaScriptObject tableJsObject, GeneralJavaScriptObject rowIdArray)/*-{
        tableJsObject.deselectRow(rowIdArray);
    }-*/;

    public int getTableHeight() {
        return getTableHeight(tableJsObject);
    }
    
    private native int getTableHeight(GeneralJavaScriptObject tableJsObject)/*-{
        return tableJsObject.rowManager.height;
    }-*/;

    public void deselectRow(int rowId) {
        GeneralJavaScriptObject row = tableJsObject.invokeFunction("getRow", "" + rowId);
        row.invokeFunction("deselect");
    }
    
    public void deselectAllRows() {
    	tableJsObject.invokeFunction("deselectRow");
    }

    public void hoverStart(int rowId) {
        GeneralJavaScriptObject element = tableJsObject.invokeFunction("getRow", "" + rowId).invokeFunction("getElement").getProperty("style");
        element.setProperty("background-color", "rgba(255, 255, 255, 0.15)");
    }

    public void hoverStop(int rowId) {
        GeneralJavaScriptObject element = tableJsObject.invokeFunction("getRow", "" + rowId).invokeFunction("getElement").getProperty("style");
        element.setProperty("background-color", "");
    }

    private native void redraw(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.redraw(true);
    }-*/;
    
    private native void reinitializeVdomHoz(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.vdomHoz.reinitialize();
    }-*/;


    private native void reformat(GeneralJavaScriptObject tableJsObject)/*-{
		tableJsObject.rowManager.rows.forEach(function(row){
			row.reinitialize()
		});
    }-*/;
    
    public void showColumn(String field){
        showColumn(tableJsObject, field);
    }
    
    private native void showColumn(GeneralJavaScriptObject tableJsObject, String field)/*-{
        tableJsObject.getColumn(field).show(true);
    }-*/;
    
    public void hideColumn(String field){
        hideColumn(tableJsObject, field);
    }
    
    private native void hideColumn(GeneralJavaScriptObject tableJsObject, String field)/*-{
        tableJsObject.getColumn(field).hide(true);
    }-*/;
    
    public void downloadCsv(String fileName){
        downloadCsv(tableJsObject, fileName);
    }
    
    private native void downloadCsv(GeneralJavaScriptObject tableJsObject, String fileName)/*-{
        tableJsObject.download(tableJsObject.csvTableFormatter, fileName, {}, tableJsObject.getSelectedData().length > 0 ? "selected" : "active");
    }-*/;

    public void downloadVot(String fileName, String resourceName){
        downloadVot(tableJsObject, fileName, resourceName);
    }

    private native void downloadVot(GeneralJavaScriptObject tableJsObject, String fileName, String resourceName)/*-{
        tableJsObject.download(tableJsObject.voTableFormatter, fileName, {resourceName:resourceName}, tableJsObject.getSelectedData().length > 0 ? "selected" : "active");
    }-*/;

    public String getVot(String resourceName){
        return getVot(tableJsObject, resourceName);
    }

    private native String getVot(GeneralJavaScriptObject tableJsObject, String resourceName)/*-{
        return tableJsObject.getVoTableString(tableJsObject.getSelectedRows().length > 0 
            ? tableJsObject.modules.download.generateExportList("selected") : tableJsObject.modules.download.generateExportList("active"), resourceName);
    }-*/;
    
    public String exportTableAsJson(){
        return exportTableAsJson(tableJsObject);
    }
    
    private native String exportTableAsJson(GeneralJavaScriptObject tableJsObject)/*-{
        var json = "";
        tableJsObject.modules.download.download("json", "json.json", {}, tableJsObject.getSelectedRows().length > 0 
            ? "selected" : "active", function(data){
                json = data;
                return false;
            });
            return json;
    }-*/;

    public GeneralJavaScriptObject[] getSelectedRows(){
        return getSelectedRows(tableJsObject);
    }
    
    private native GeneralJavaScriptObject[] getSelectedRows(GeneralJavaScriptObject tableJsObject)/*-{
        return tableJsObject.getSelectedData();
    }-*/;

    public int getVisibleRowCount(){
        return getVisibleRowCount(tableJsObject);
    }
    
    private native int getVisibleRowCount(GeneralJavaScriptObject tableJsObject)/*-{
        return tableJsObject.getDataCount("active");
    }-*/;
    
    public GeneralJavaScriptObject[] getColumns() {
        return getColumns(tableJsObject);
    }
    
    private native GeneralJavaScriptObject[] getColumns(GeneralJavaScriptObject tableJsObject)/*-{
        return tableJsObject.getColumns();
    }-*/;
    
    public void blockRedraw(){
        blockRedraw(tableJsObject);
    }

    private native void blockRedraw(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.blockRedraw();
    }-*/;
    
    public void restoreRedraw(){
        restoreRedraw(tableJsObject);
    }
    
    private native void restoreRedraw(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.restoreRedraw();
    }-*/;

    public void redrawAndReinitializeHozVDom(){
        redrawAndReinitializeHozVDom(tableJsObject);
    }
    
    private native void redrawAndReinitializeHozVDom(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.vdomHoz.reinitialize(undefined, true);
        tableJsObject.redraw(true);
        tableJsObject.vdomHoz.reinitialize();
    }-*/;
    
    public void setPlaceholderText(String text){
        setPlaceholderText(tableJsObject, text);
    }
    
    public native void setPlaceholderText(GeneralJavaScriptObject tableJsObject, String text)/*-{
        var div = $doc.createElement('div')
        div.innerText = text;
        div.className = "tabulator_emptyTable";
    	tableJsObject.options.placeholder.innerText = "";
    	tableJsObject.options.placeholder.appendChild(div);
	}-*/;


    public void onDataFiltered(String indexes) {
        List<Integer> indexArray = new LinkedList<Integer>();
        for(String s : indexes.split(",")) {
            if(s.length() > 0) {
                indexArray.add(Integer.parseInt(s));
            }
        }
        tabulatorCallback.onDataFiltered(indexArray);
    }
    
    public void toggleNumericFilterDialog(String tapName, String title, String filterButtonId, double minVal, 
            double maxVal, final GeneralJavaScriptObject onChangeFunc, final GeneralJavaScriptObject formatter, 
            GeneralJavaScriptObject formatterParamsIfExisting) {
        final GeneralJavaScriptObject formatterParams = verifyFormatterParams(formatterParamsIfExisting);
        final ValueFormatter valueFormatter = new ValueFormatter() {
            
            @Override
            public double getValueFromFormat(String formattedValue) {
                formatterParams.setProperty("convertBack", true);
                return GeneralJavaScriptObject.convertToDouble(formatter.invokeSelf(createPretendCell(formattedValue), formatterParams));
            }
            
            @Override
            public String formatValue(double value) {
                formatterParams.setProperty("convertBack", false);
                return GeneralJavaScriptObject.convertToString(formatter.invokeSelf(createPretendCell(value), formatterParams));
            }
        };

    	if(!filterDialogs.containsKey(tapName)) {
    		FilterObserver filterObserver = new FilterObserver() {
				
				@Override
				public void onNewFilter(String filter) {
					onChangeFunc.invokeFunction("onChange", filter);
					
				}
			};
    		
    		RangeFilterDialogBox filterDialog = new RangeFilterDialogBox(tapName, title, valueFormatter, filterButtonId, filterObserver);
    		filterDialogs.put(tapName, filterDialog);
    	}
    	RangeFilterDialogBox filterDialogBox = (RangeFilterDialogBox) filterDialogs.get(tapName);
		
		filterDialogBox.setRange(minVal, maxVal, 2);
    	filterDialogBox.toggle();
    }

    private native GeneralJavaScriptObject verifyFormatterParams(GeneralJavaScriptObject formatterParams)/*-{
        return formatterParams || {};
    }-*/;
    
    private native GeneralJavaScriptObject createPretendCell(Object value)/*-{
        return {getValue: function() {return value}};
    }-*/;
    
    public void toggleDateFilterDialog(String tapName, String title, String filterButtonId, String minVal, String maxVal, final GeneralJavaScriptObject onChangeFunc) {
    	
    	if(!filterDialogs.containsKey(tapName)) {
    		FilterObserver filterObserver = new FilterObserver() {
    			
    			@Override
    			public void onNewFilter(String filter) {
    				onChangeFunc.invokeFunction("onChange", filter);
    				
    			}
    		};
    		
    		DateFilterDialogBox filterDialog = new DateFilterDialogBox(tapName, title, filterButtonId, filterObserver);
    		
    		filterDialog.setStartRange(minVal, maxVal);
    		filterDialogs.put(tapName, filterDialog);
    	}
    	
    	FilterDialogBox filterDialogBox = filterDialogs.get(tapName);
    	filterDialogBox.toggle();
    	
    }

    public void showListFilterDialog(String tapName, String title, String filterButtonId, String list, final GeneralJavaScriptObject onChangeFunc) {
    	
    	if(!filterDialogs.containsKey(tapName)) {
    		
    		final DropDownMenu<String> dropDownMenu = new DropDownMenu<String>("", "", 125, filterButtonId + "_DropDownMenu");

    		for(String item : list.split(",")) {
    			MenuItem<String> dropdownItem = new MenuItem<String>(item, item, item, true);
    			dropDownMenu.addMenuItem(dropdownItem);
    		}
    		
    		dropDownMenu.registerObserver(new MenuObserver() {

    			@Override
    			public void onSelectedChange() {
    				String object = dropDownMenu.getSelectedObject();
    				onChangeFunc.invokeFunction("onChange", object);
    			}
    		});
    		
    		dropDownMenu.toggleMenuBar();
    	}
    }
    
    public void goToCoordinateOfFirstRow(){
        tabulatorCallback.onCenterClicked(tableJsObject.invokeFunction("getRow", "0").invokeFunction("getData"));
    }
    public void insertData(GeneralJavaScriptObject data, GeneralJavaScriptObject metadata){
        setMetadata(tableJsObject, metadata);
        setData(tableJsObject, data);
    }
    
    private native String setMetadata(GeneralJavaScriptObject tableJsObject, GeneralJavaScriptObject metadata)/*-{
        return tableJsObject.metadata = metadata;
    }-*/;
    
    public void insertUserData(GeneralJavaScriptObject data){
        setIsUserDataBool(tableJsObject);
        setData(convertDataToTabulatorFormat(tableJsObject, data, AladinLiteWrapper.getCoordinatesFrame().getValue()));
    }
    
    private native void setIsUserDataBool(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.isEsaskyData = false;
    }-*/;

    private native String convertDataToTabulatorFormat(GeneralJavaScriptObject tableJsObject, GeneralJavaScriptObject data, String aladinFrame)/*-{
        return tableJsObject.convertDataToTabulatorFormat(data, aladinFrame);
    }-*/;
    
    public void setData(String dataOrUrl){
    	setData(tableJsObject, dataOrUrl);
    }

    public void clearTable(){
    	clearTable(tableJsObject);
    	for(String key : filterDialogs.keySet()) {
    		onFilterChanged(key, "");
    	}
    	filterDialogs.clear();
    	lastHoveredRow = -1;
    }
    
    private native void clearTable(GeneralJavaScriptObject tableJsObject)/*-{
    	tableJsObject.dataLoaded = false;
    	tableJsObject.showCount = false;
    	tableJsObject.clearData();
    	tableJsObject.filterData = [];
    	tableJsObject.metadata = [];
    	tableJsObject.clearHeaderFilter();
    	previouslySelectedMap = [];
    	tableJsObject.dataLoaded = false;
        tableJsObject.setData([]);
    }-*/;

    private native void setData(GeneralJavaScriptObject tableJsObject, Object dataOrUrl)/*-{
    	tableJsObject.dataLoaded = false;
        tableJsObject.setData(dataOrUrl);
        
        var observer = new MutationObserver(function(mutations){
    		  for (var i = 0; i < mutations.length; i++){
    		    for (var j = 0; j < mutations[i].addedNodes.length; j++){
    		      if(mutations[i].addedNodes[j].classList && 
    		      				(mutations[i].addedNodes[j].classList.contains("tabulator-cell") ||
    		      				mutations[i].addedNodes[j].getAttribute('role') == 'Header'))
      				{
    		      	tableJsObject.vdomHoz.reinitialize(undefined, true);
    		      	tableJsObject.redraw(true);
    		      	tableJsObject.vdomHoz.reinitialize();
    		      	
    		      	this.disconnect();
    		      	return;
    		      }
    		    }
    		  }
    		});

    		observer.observe(tableJsObject.element, {
    		  childList: true,
    		  subtree: true
    		});
    }-*/;
    
    public void setDefaultQueryMode(){
        setDefaultQueryMode(this, tableJsObject);
    }
    
    private native void setDefaultQueryMode(TabulatorWrapper wrapper, GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.options.ajaxResponse = function(url, params, response){
            wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onAjaxResponse()();
			descriptorMetaData = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getDescriptorMetaData()();
			
			var metadata = response.metadata;
			var sortedMetadata = [];
			var indexesMoved = [];
			for(var j = 0; j < metadata.length; j++){
				
				metadata[j]["visible"] = (metadata[j].name !== "s_region");
				metadata[j]["displayName"] = $wnd.esasky.getColumnDisplayText(metadata[j].name);
				
				if(descriptorMetaData.hasOwnProperty(metadata[j].name)){
				
					if(descriptorMetaData[metadata[j].name].hasOwnProperty("visible")){
						metadata[j].visible = descriptorMetaData[metadata[j].name]["visible"];
					}
					if(descriptorMetaData[metadata[j].name].hasOwnProperty("label") && descriptorMetaData[metadata[j].name].label != "" && descriptorMetaData[metadata[j].name].label != "undefined"){
					    if(url.includes("publications-by")){					        
				            metadata[j].displayName = $wnd.esasky.getInternationalizationText(descriptorMetaData[metadata[j].name]["label"]);
					    } else {
				            metadata[j].displayName = $wnd.esasky.getDefaultLanguageText(descriptorMetaData[metadata[j].name]["label"]);
					    }
					}
					if(descriptorMetaData[metadata[j].name].hasOwnProperty("maxDecimalDigits") && descriptorMetaData[metadata[j].name].maxDecimalDigits != null){
				        metadata[j].maxDecimalDigits = descriptorMetaData[metadata[j].name].maxDecimalDigits;
					}
					sortedMetadata[descriptorMetaData[metadata[j].name].index] = metadata[j];
					indexesMoved.push(j);
				} else if (!$wnd.$.isEmptyObject(descriptorMetaData)){
					metadata[j].visible = false;
				}
			}
			
			sortedMetadata = sortedMetadata.filter(function( element ) {
               return element !== undefined;
            });
			
			var data = [];
			for(var i = 0; i < response.data.length; i++){
				var row = {id:i};
				for(var j = 0; j < metadata.length; j++){
	    			if(metadata[j].datatype.toUpperCase() === "DOUBLE" || metadata[j].datatype.toUpperCase() === "REAL"){
						row[metadata[j].name] = parseFloat(response.data[i][j]);
		    			if(isNaN(row[metadata[j].name])){
							row[metadata[j].name] = undefined;
		    			}
	    			} else if(metadata[j].datatype.toUpperCase() === "INTEGER" || metadata[j].datatype.toUpperCase() === "INT"){
						row[metadata[j].name] = parseInt(response.data[i][j]);
		    			if(isNaN(row[metadata[j].name])){
							row[metadata[j].name] = undefined;
		    			}
	    			} else if(metadata[j].datatype.toUpperCase() === "BIGINT"|| metadata[j].datatype.toUpperCase() === "LONG"){
						row[metadata[j].name] = response.data[i][j];
	    			} else {
						row[metadata[j].name] = response.data[i][j];
	    			}
				}
				data[i] = row;
			}		
			
	        var index = indexesMoved.pop();
            while(index !== undefined){
                metadata.splice(index, 1);
                index = indexesMoved.pop();
            }
            metadata = sortedMetadata.concat(metadata);
            
			tableJsObject.metadata = metadata;
			tableJsObject.filterData = []
			tableJsObject.columnDef = [];
			tableJsObject.showCount = true;
			tableJsObject.dataLoaded = true;
	        return data;
	    }
    }-*/;
    
    public void setHeaderQueryMode(String mode){
        setHeaderQueryMode(this, tableJsObject, mode);
    }
    
    private native void setHeaderQueryMode(TabulatorWrapper wrapper, GeneralJavaScriptObject tableJsObject, String mode)/*-{
        tableJsObject.clearData();
        tableJsObject.mocLoaded = false;
        
      	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::registerMocLoadedObserver()();
        
    	tableJsObject.options.ajaxResponse = function(url, params, response){
			var newMeta = [];
			var filterData = {};
			
			var descMetaData = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getDescriptorMetaData()();
			var data = response.data[0][0].split(",");
			
			newMeta = new Array(data.length);
			for(var i = 0;i< data.length; i++){
				var row = data[i].split(";")
				var colName = row[0];
				var val = row[1];
				var metaName = colName.substring(0,colName.length - 4)
				var datatype;

				//If not in descMetaData add to unique spot in end and then we remove all empty slots in end
				var metaDataIndex = data.length + newMeta.length;
				var visible = false;
				
				
				if(descMetaData.hasOwnProperty(metaName)){
					metaDataIndex = parseInt(descMetaData[metaName].index);
					datatype = descMetaData[metaName].type.toUpperCase();
					if(descMetaData[metaName].hasOwnProperty("visible")){
                        visible = descMetaData[metaName]["visible"];
                    }
				}else{
					if(colName.endsWith('_min') || colName.endsWith('_max')){
						datatype = "DOUBLE";
					}else{
						datatype = "STRING";
					}
				}
				
				var label = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getLabelFromTapName(Ljava/lang/String;)(metaName);
				var displayName = $wnd.esasky.getDefaultLanguageText(label);
				
				if(!filterData.hasOwnProperty(metaName)){	
					filterData[metaName] = {};
				}
				
				if(colName.endsWith("_min")){
					if(datatype == "TIMESTAMP" || datatype == "DATETIME"){
						filterData[metaName]["min"] = val;
	    			}else{
						filterData[metaName]["min"] = parseFloat(val);
    				}	    			
					meta = {name:metaName, displayName:displayName, datatype:datatype, visible: visible}
					newMeta.splice(metaDataIndex,1,meta)
				
				}else if(colName.endsWith("_max")){
					
					if(datatype == "TIMESTAMP"|| datatype == "DATETIME"){
						filterData[metaName]["max"] = val;
	    			
					}else{
						filterData[metaName]["max"] = parseFloat(val);
    				}	    			
				
				}else{
					meta = {name:metaName, displayName:displayName, datatype:datatype, visible: visible}
					newMeta.splice(metaDataIndex,1,meta)
				}
			}
			
			newMeta = newMeta.filter(function(e){return e})
			tableJsObject.metadata = newMeta;
			tableJsObject.filterData = filterData;
			tableJsObject.showCount = false;
			tableJsObject.dataLoaded = true;
			
			
			return new Promise(function (resolve, reject) {
				if(tableJsObject.mocLoaded){
			      	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::notifyMocLoadedObserver()();
					resolve([]);
				}else{
					tableJsObject.onMocLoaded = function () {
						this.onMocLoaded = null;
				      	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::notifyMocLoadedObserver()();
						resolve([]);
					}
				}
			});
        }
    }-*/;
    
    public void show() {
        if(isMOCMode()) { //In defaultQueryMode MutationObserver redraws, if necessary
            Scheduler.get().scheduleFinally(new ScheduledCommand() {
                
                @Override
                public void execute() {
                    redraw(tableJsObject); 
                }
            });
        }
    }
    
    

    private native GeneralJavaScriptObject createColumnTabulator(TabulatorWrapper wrapper, String divId, 
            boolean addSendToVOApplicationColumn, boolean addLink2ArchiveColumn, boolean addLink2AdsColumn, boolean addSourcesInPublicationColumn, String selectionHeaderTitle,
            boolean blockRedraw, boolean isEsaskyData) /*-{
		$wnd.esasky.nonDatabaseColumns = ["rowSelection", "centre", "link2archive", "addLink2AdsColumn", "samp", "sourcesInPublication"];
		var visibleTableData = [];
		var visibleTableDataIndex = 0;

		var isInitializing = true;
		var previouslySelectedMap = [];
		var selectionMap = [];
		var columnDef = [];
		
        var createFilterButton = function(filterButtonId){
            var filterButton = $wnd.$( "" + "<div id=\'" + filterButtonId
                        + "\' class=\"filterButton defaultEsaSkyButton darkStyle smallButton squaredButton gwt-PushButton-up\" "
                        + "title=\""  + "\""
                        + "\"" + ">" + "<img src=\"images/filter.png\" class=\"fillParent\" />" + "</div>");
		    
            filterButton.on("mouseenter", function(e){
                filterButton.toggleClass("gwt-PushButton-up-hovering");
            });
            filterButton.on("mouseleave", function(e){
                filterButton.toggleClass("gwt-PushButton-up-hovering");
                filterButton.removeClass("gwt-PushButton-down");
            });
            filterButton.on("mouseover", function(e){e.stopPropagation();});
            filterButton.on("mousedown", function(e){
                filterButton.toggleClass("gwt-PushButton-down");
                e.stopPropagation();
            });
            filterButton.on("mouseup",function(e){
                filterButton.toggleClass("gwt-PushButton-down");
                e.stopPropagation();
            });
            return filterButton;
		}
		
		var numericFilterEditor = function(cell, onRendered, success, cancel, editorParams){
			var filterButtonId = divId + "_" + editorParams["tapName"];
			var filterButton = createFilterButton(filterButtonId);
			
			var functionObject = {};
			functionObject.onChange = function(filter){
				success(filter);
				onFilterChanged(filter);
			}
			filterButton.on("click", function(e){
			    e.stopPropagation();
				var minVal = Infinity;
				var maxVal = -Infinity;
				
				name = cell.getColumn()._column.definition.field;
				if(table.filterData[name]){
					minVal = table.filterData[name].min;
					maxVal = table.filterData[name].max;
				}else{
					cell.getColumn()._column.table.rowManager.rows.forEach(function (row){
						
						if(row.data.hasOwnProperty(name) && row.data[name] != undefined){
							minVal = Math.min(minVal, row.data[name])
							maxVal = Math.max(maxVal, row.data[name])
						}
					});
				}
				
				if(minVal == Infinity){
					minVal = -100;
					maxVal = 100;
				}
				
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::toggleNumericFilterDialog(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DDLesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)
					(editorParams["tapName"], editorParams["title"], filterButtonId, minVal, maxVal, functionObject, cell.getColumn().getDefinition().formatter, cell.getColumn().getDefinition().formatterParams);
			});	
			var container = $wnd.$("<span></span>")
			 
			container.append(filterButton);
			//create and style input
			
			function onFilterChanged(input){
				values = input.split(",");
				var filter = "";
				if(values[0].length > 0 ){
					filter += cell.getField() + " >=  " + values[0]
				}
				if(values.length > 1 && values[1].length > 0 ){
					if(filter.length > 0){
						filter += " AND ";
					}
					filter += cell.getField() + " <=  " + values[1]
				}
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onFilterChanged(Ljava/lang/String;Ljava/lang/String;)(cell.getField(), filter);
			}

			return container[0];

		}
		
		var dateFilterEditor = function(cell, onRendered, success, cancel, editorParams){
			var filterButtonId = divId + "_" + editorParams["tapName"];
            var filterButton = createFilterButton(filterButtonId);
            
			var functionObject = {};
			functionObject.onChange = function(filter){
				success(filter);
				onFilterChanged(filter);
			}
				
			filterButton.on("click", function(e){
			    e.stopPropagation();
				var minVal = "2100-01-01";
				var maxVal = "1800-01-01";
				
				name = cell.getColumn()._column.definition.field;
				if(table.filterData[name]){
					minVal = table.filterData[name].min;
					maxVal = table.filterData[name].max;
				}else{
					cell.getColumn()._column.table.rowManager.rows.forEach(function (row){
						
						if(row.data[name] && row.data[name] != undefined){
							if(minVal > row.data[name]){
								minVal = row.data[name];
							}
							if(maxVal < row.data[name]){
								maxVal = row.data[name];
							}
						}
					});
				}
				
				if(minVal > maxVal){
					tmp = minVal
					minVal = maxVal
					maxVal = tmp;
				}
				
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::toggleDateFilterDialog(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)
					(editorParams["tapName"],editorParams["title"], filterButtonId, minVal, maxVal, functionObject);
			});	
			var container = $wnd.$("<span></span>")
			 
			container.append(filterButton);
			//create and style input
			
			function onFilterChanged(input){
				values = input.split(",");
				var filter = "";
				if(values[0].length > 0 ){
					filter += cell.getField() + " >=  '" + values[0] + "'";
				}
				if(values.length > 1 && values[1].length > 0 ){
					if(filter.length > 0){
						filter += " AND ";
					}
					filter += cell.getField() + " <=  '" + values[1] + "'";
				}
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onFilterChanged(Ljava/lang/String;Ljava/lang/String;)(cell.getField(), filter);
			}

			return container[0];

		}
		
		var listFilterEditor = function(cell, onRendered, success, cancel, editorParams){
			
			var filterButtonId = divId + "_" + editorParams["tapName"];
            var filterButton = createFilterButton(filterButtonId);
            
			var functionObject = {};
			functionObject.onChange = function(filter){
				success(filter);
				onFilterChanged(filter);
			}
				
			filterButton.on("click", function(e){
			    e.stopPropagation();
				if(table.filterData != []){
					name = cell.getColumn()._column.definition.field;
					list = table.filterData[name]["list"];
					wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::showListFilterDialog(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)
						(editorParams["tapName"],editorParams["title"], filterButtonId, minVal, maxVal, functionObject);
				}				
			});	
			var container = $wnd.$("<span></span>")
			 
			container.append(filterButton);
			//create and style input
			
			function onFilterChanged(input){
				values = input.split(",");
				var filter = "";
				if(values[0].length > 0 ){
					filter += cell.getField() + " = ''" + values[0] + "''";
				}
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onFilterChanged(Ljava/lang/String;Ljava/lang/String;)(cell.getField(), filter);
			}

			return container[0];

		}
		
		var raFormatter = function(cell, formatterParams, onRendered){
            var raDeg = cell.getValue();
		    if(formatterParams.convertBack && formatterParams.convertBack === true){
                return @esac.archive.esasky.cl.web.client.model.RaPosition::construct(Ljava/lang/String;)(raDeg)
                .@esac.archive.esasky.cl.web.client.model.RaPosition::getRaDeg()();
		    } else {
    		    if(raDeg === undefined || raDeg === ""){
    		        return "";
    		    }
        	    if(@esac.archive.esasky.cl.web.client.status.GUISessionStatus::isShowingCoordinatesInDegrees()()){
                    return @esac.archive.esasky.cl.web.client.model.RaPosition::construct(D)(raDeg)
                    .@esac.archive.esasky.cl.web.client.model.RaPosition::getDegreeString()();
        	    } else {
                    return @esac.archive.esasky.cl.web.client.model.RaPosition::construct(D)(raDeg)
                    .@esac.archive.esasky.cl.web.client.model.RaPosition::getHmsString()();
                }
		    }
		}
		var decFormatter = function(cell, formatterParams, onRendered){
            var decDeg = cell.getValue();
            if(formatterParams.convertBack && formatterParams.convertBack === true){
                return @esac.archive.esasky.cl.web.client.model.DecPosition::construct(Ljava/lang/String;)(decDeg)
                .@esac.archive.esasky.cl.web.client.model.DecPosition::getDecDeg()();
            } else {
        	    if(decDeg === undefined || decDeg === ""){
        	        return "";
        	    }
        	    if(@esac.archive.esasky.cl.web.client.status.GUISessionStatus::isShowingCoordinatesInDegrees()()){
                    return @esac.archive.esasky.cl.web.client.model.DecPosition::construct(D)(decDeg)
                    .@esac.archive.esasky.cl.web.client.model.DecPosition::getDegreeString()();
        	    } else {
                    return @esac.archive.esasky.cl.web.client.model.DecPosition::construct(D)(decDeg)
                    .@esac.archive.esasky.cl.web.client.model.DecPosition::getSymbolDmsString()();
                }
            }
		}
		var fileSizeFormatter = function(cell, formatterParams, onRendered){
            var value = cell.getValue();
            if(formatterParams.convertBack && formatterParams.convertBack === true){
        	    if(divId.includes("MAST-")){ //MAST has incorrect unit for access_estsize. Should be in kilobytes
                    return @esac.archive.esasky.cl.web.client.utility.SizeFormatter::formatToBytes(Ljava/lang/String;)(value);
        	    } else {
                    return @esac.archive.esasky.cl.web.client.utility.SizeFormatter::formatToBytes(Ljava/lang/String;)(value) / 1024;
        	    }
            } else {
        	    if(value === undefined || value === ""){
        	        return "";
        	    }
        	    if(divId.includes("MAST-")){ //MAST has incorrect unit for access_estsize. Should be in kilobytes
                    return @esac.archive.esasky.cl.web.client.utility.SizeFormatter::formatBytes(II)(value, 0);
        	    } else {
                    return @esac.archive.esasky.cl.web.client.utility.SizeFormatter::formatBytes(II)(value * 1024, 0);
        	    }
            }
		}
		var doubleFormatter = function(cell, formatterParams, onRendered){
            if(formatterParams.convertBack && formatterParams.convertBack === true){
                return @esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.NumberValueFormatter::formatStringToDouble(Ljava/lang/String;I)(cell.getValue(), formatterParams.maxDecimalDigits);
            } else {			
    			if(cell.getValue() == undefined){
    				return "";
    			}
                return @esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.NumberValueFormatter::formatDouble(DI)(cell.getValue(), formatterParams.maxDecimalDigits);
            }
		}

		var hideNonDatabaseColumnFormatter = function(cell, formatterParams, onRendered){
		    if ($wnd.esasky.nonDatabaseColumns.includes(cell.getValue())) {
		        return "";
		    }
		    return cell.getValue();
		}

		function DoubleFilter(headerValue, rowValue, rowData, filterParams){
			
			var split = headerValue.split(",");

			if(split.length == 2){
				
				if(rowValue == null){
					// If any filter is added Null should be removed
					return false;
				}
				
				var startTrue = true;
				var endTrue = true;
				if(split[0].length > 0 && rowValue < parseFloat(split[0]) ){
					startTrue = false;
				}
				if(split[1].length > 0 && rowValue > parseFloat(split[1]) ){

					endTrue = false;
				}
		    	return startTrue && endTrue; 
			}
			return true;
		}
		
		function DateFilter(headerValue, rowValue, rowData, filterParams){
			
			var split = headerValue.split(",");
		   	if(split.length == 2){
		   		if(!rowValue){
					return false;
				}
   				var startTrue = true;
				var endTrue = true;
				if(split[0].length > 0 && rowValue < split[0] ){
					startTrue = false;
				}
				if(split[1].length > 0 && rowValue > split[1] ){
					endTrue = false;
				}
		    	return startTrue && endTrue;
		   	}
		   	return true;
		}
		
		var stringFilterEditor =  function(cell, onRendered, success, cancel, editorParams){
		
			var editor = this.table.modules.edit.editors["input"];
			
			var successFunc = function(filter){
				success(filter);
				var filterString = cell.getField() + " like '%" + filter + "%'";
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onFilterChanged(Ljava/lang/String;Ljava/lang/String;)(cell.getField(), filterString);
			}

			var cancelFunc = function(){
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onFilterChanged(Ljava/lang/String;Ljava/lang/String;)(cell.getField(), "");
				cancel();
			}
			
			return editor(cell, onRendered, successFunc, cancelFunc, editorParams);
		}
		
		var footerCounter = "<div></div><div id=\"" + divId + "_rowCount\" class=\"footerCounter\">0</div>"
		
		var table = new $wnd.Tabulator("#" + divId, {
		 	height:"100%", // set height of table (in CSS or here), this enables the Virtual DOM and improves render speed dramatically (can be any valid css height value)
		 	placeholder:"",
    		footerElement:footerCounter,
    		virtualDomHoz:true,
		    dataFiltered:function(filters, rows){
		    	var returnString = "";
		    	for(var i = 0; i < rows.length; i++){
		    		returnString += rows[i].getIndex() + ",";
		    	}

		  		wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onDataFiltered(Ljava/lang/String;)(returnString);

			   	var footerCounter = this.footerManager.element.getElementsByClassName("footerCounter")[0];
			   	var text = $wnd.esasky.getInternationalizationText("tabulator_rowCount");
			   	text = text.replace("$count$", @esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.NumberValueFormatter::formatDouble(DI)(rows.length, 0));
			   	if(footerCounter){
					footerCounter.innerHTML = text;
			   	}
			   	if(!wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::isMOCMode()()){
				   	if(rows.length == 0 && this.getHeaderFilters().length > 0){
				   	    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::setPlaceholderText(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Ljava/lang/String;)(table, $wnd.esasky.getInternationalizationText("tabulator_filtered_empty"));
				   	}
			   	}
		    },
		    dataLoaded:function(data){
                if((data.length == 0 && !wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::isMOCMode())
                    || wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::hasBeenClosed()()){
                    return;
                }
		    	this.rowManager.adjustTableSize();
			   	if(this.dataLoaded && !wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::isMOCMode()() &&  data.length == 0){
			   	    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::setPlaceholderText(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Ljava/lang/String;)(table, $wnd.esasky.getInternationalizationText("tabulator_no_data"));
			   	} else if(!wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::isMOCMode()() ){
			   		this.options.placeholder.innerText = "";
			   	}
		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onDataLoaded()();
		    	
		    	// Fix for setting the access_url and preview column next to the center button
		    	// until we get some better handling of external tap metadata
		    	// preview column is from ASTRON
		    	var accessUrlColumn = this.getColumn("access_url");
		    	if(accessUrlColumn){
		    	    accessUrlColumn.move("centre", true);
		    	}
		    	var previewColumn = this.getColumn("preview");
		    	if(previewColumn){
		    	    previewColumn.move("centre", true);
		    	}
		    },
		    dataLoading:function(data){
                if((data.length == 0 && !wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::isMOCMode())
                    || wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::hasBeenClosed()()){
                    return;
                }
		        var descriptorMetadata = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getDescriptorMetaData()();
		        var activeColumnGroup = [];
		        var isSSO = false;
		    	activeColumnGroup.push({
		    	    formatter:"rowSelection", 
		    	    titleFormatterParams: {title:selectionHeaderTitle}, 
		    	    field:"rowSelection", 
                    visible: descriptorMetadata && descriptorMetadata.rowSelection ? descriptorMetadata.rowSelection.visible : true,
		    	    title:"Selection", 
		    	    download: false,
		    	    titleFormatter:"rowSelection", 
		    	    sorter:function(a, b, aRow, bRow, column, dir, sorterParams){
					return bRow.isSelected() - aRow.isSelected();
				}});

		    	var imageButtonFormatter = function(cell, formatterParams, onRendered){ 
                    return "<div class='buttonCell' title='" + formatterParams.tooltip + "'><img src='images/" + formatterParams.image + "'/></div>";
                };
		    	var linkListFormatter = function(cell, formatterParams, onRendered){ 
                    return $wnd.esasky.linkListFormatter(cell.getValue(), 100);
                };
                
			    var raFound = false;
			    var decFound = false;
				if(!isInitializing && this.metadata){
				    var raColumnName = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getRaColumnName()();
				    var decColumnName = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getDecColumnName()();
			    	for(var i = 0; i < this.metadata.length; i++){
			    	    if (this.metadata[i].name === raColumnName) {
			    	        raFound = true; 
			    	    } else if (this.metadata[i].name === decColumnName) {
			    	        decFound = true;
			    	    }
			    	}
		    	}
                if(raFound && decFound){
                    activeColumnGroup.push({
                        title:$wnd.esasky.getInternationalizationText("tabulator_centreHeader"),
                        field:"centre",
                        visible: descriptorMetadata && descriptorMetadata.centre ? descriptorMetadata.centre.visible : true,
                        headerSort:false,
                        headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_centreHeaderTooltip"),
                        minWidth: 50,
                        download: false,
                        formatter:imageButtonFormatter, width:40, hozAlign:"center", formatterParams:{image:"recenter.png", 
                            tooltip:$wnd.esasky.getInternationalizationText("tabulator_centreOnCoordinates")},
                            cellClick:function(e, cell){
                                e.stopPropagation();
                		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onCenterClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getData());
                            }
                    });
                }
                if(addSendToVOApplicationColumn){
                    activeColumnGroup.push({
                        title:$wnd.esasky.getInternationalizationText("tabulator_sendToVOApplicationHeader"),
                        field:"samp",
                        visible: descriptorMetadata && descriptorMetadata.samp ? descriptorMetadata.samp.visible : true,
                        headerSort:false, 
                        headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_sendRowToVOApplicationHeaderTooltip"),
                        minWidth: 50,
                        download: false,
                        formatter:imageButtonFormatter, width:40, hozAlign:"center", formatterParams:{image:"send_small.png", 
                            tooltip:$wnd.esasky.getInternationalizationText("tabulator_sendRowToVOA")},
                            cellClick:function(e, cell){
                                e.stopPropagation();
                		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onSendToVoApplicaitionClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getData());
                            }
                    });
                }
                
                if(addLink2ArchiveColumn){
                    activeColumnGroup.push({
                        title:$wnd.esasky.getInternationalizationText("tabulator_link2ArchiveHeader"),
                        field:"link2archive",
                        visible: descriptorMetadata && descriptorMetadata.link2archive ? descriptorMetadata.link2archive.visible : true,
                        headerSort:false, 
                        headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_link2ArchiveHeaderTooltip"),
                        minWidth: 63,
                        download: false,
                        formatter:imageButtonFormatter, width:40, hozAlign:"center", formatterParams:{image:"link2archive.png",
                            tooltip:$wnd.esasky.getInternationalizationText("tabulator_link2ArchiveButtonTooltip")},
                            cellClick:function(e, cell){
                                e.stopPropagation();
                		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onLink2ArchiveClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getRow());
                            }
                    });
                }
                if(addLink2AdsColumn){
                    activeColumnGroup.push({
                        title:$wnd.esasky.getInternationalizationText("tabulator_link2AdsHeader"),
                        field:"link2archive",
                        visible: descriptorMetadata && descriptorMetadata.link2archive ? descriptorMetadata.link2archive.visible : true,
                        headerSort:false, 
                        headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_link2AdsHeaderTooltip"),
                        minWidth: 63,
                        download: false,
                        formatter:imageButtonFormatter, width:40, hozAlign:"center", formatterParams:{image:"link2archive.png",
                            tooltip:$wnd.esasky.getInternationalizationText("tabulator_link2AdsButtonTooltip")},
                            cellClick:function(e, cell){
                                e.stopPropagation();
                                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onLink2ArchiveClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getRow());
                            }
                    });
                }
                if(addSourcesInPublicationColumn){
                    activeColumnGroup.push({
                        title:$wnd.esasky.getInternationalizationText("tabulator_SourcesInPublicationHeader"),
                        field:"sourcesInPublication",
                        visible: descriptorMetadata && descriptorMetadata.sourcesInPublication ? descriptorMetadata.sourcesInPublication.visible : true,
                        headerSort:false, 
                        headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_SourcesInPublicationHeaderTooltip"),
                        minWidth: 67,
                        download: false,
                        formatter:imageButtonFormatter, width:40, hozAlign:"center", formatterParams:{image:"target_list.png",
                            tooltip:$wnd.esasky.getInternationalizationText("tabulator_SourcesInPublication")},
                            cellClick:function(e, cell){
                                e.stopPropagation();
                		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onSourcesInPublicationClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getData());
                            }
                    });
                }
                
				if(!isInitializing && this.metadata){
			    	for(var i = 0; i < this.metadata.length; i++){
			    	    var shouldHideColumn = false;
			    	    for(var j = 0; j < $wnd.esasky.databaseColumnsToHide.length; j++){
			    	        if(this.metadata[i].name.toLowerCase() === $wnd.esasky.databaseColumnsToHide[j]){
			    	            shouldHideColumn = true;
			    	        }
			    	    }
			    	    if(this.metadata[i].name.toLowerCase() === "observation_oid"
			    	        && isSSO){
		    	            shouldHideColumn = true;
			    	    }
			    	    if(shouldHideColumn && table.isEsaskyData){
			    	        activeColumnGroup.push({
                                download: false,
                                field:this.metadata[i].name,
                                visible:false,
                            });
                            continue;
			    	    }
			    		if(this.metadata[i].name.toLowerCase() === "access_url"
			    		    || this.metadata[i].name.toLowerCase() === "product_url"){
	                        activeColumnGroup.push({
	                            title:$wnd.esasky.getInternationalizationText("tabulator_download"),
	                            titleDownload:this.metadata[i].name,
	                            field:this.metadata[i].name,
	                            visible:this.metadata[i].visible,
	                            headerSort:false, 
	                            headerTooltip:this.metadata[i].description,
	                            minWidth: 85,
	                            download: true,
	                            formatter:imageButtonFormatter, width:40, hozAlign:"center", formatterParams:{image:"download_small.png", 
	                                tooltip:$wnd.esasky.getInternationalizationText("tabulator_download")}, 
	                                cellClick:function(e, cell){
	                                    e.stopPropagation();
	                                    if(cell.getData().access_format && cell.getData().access_format.toLowerCase().includes("datalink")){
	                        		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onDatalinkClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getRow());
	                                    } else {
	                        		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onAccessUrlClicked(Ljava/lang/String;)(cell.getValue());
	                                    }
	                                }
	                        });
	                        continue;
			    		}
			    		if(this.metadata[i].name.toLowerCase() === "postcard_url" || this.metadata[i].name.toLowerCase() === "preview"){
	                        activeColumnGroup.push({
	                            title:$wnd.esasky.getInternationalizationText("tabulator_previewHeader"),
	                            titleDownload:this.metadata[i].name,
	                            field:this.metadata[i].name,
	                            visible:this.metadata[i].visible,
	                            headerSort:false, 
	                            headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_previewHeaderTooltip"),
	                            minWidth: 66,
	                            download: true,
	                            formatter:imageButtonFormatter, width:40, hozAlign:"center", formatterParams:{image:"preview.png", 
	                                tooltip:$wnd.esasky.getInternationalizationText("tabulator_preview")}, 
	                                cellClick:function(e, cell){
	                                    e.stopPropagation();
	                    		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onPostcardUrlClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Ljava/lang/String;)(cell.getRow(), cell.getColumn()._column.field);
	                                }
	                        });
	                        continue;
			    		}
			    		if(this.metadata[i].name.toLowerCase() === "author"){
	                        activeColumnGroup.push({
	                            title:$wnd.esasky.getInternationalizationText("Authors"),
	                            titleDownload:this.metadata[i].name,
	                            field:this.metadata[i].name,
	                            visible:this.metadata[i].visible,
	                            download: true,
	    		    			sorter: "string",
	    		    			headerFilter:true,
	    		    			headerFilterFunc:"like",
	    		    			headerFilterFuncParams:{tapName:this.metadata[i].name},
	                            headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_authorHeaderTooltip"),
	                            formatter:linkListFormatter});
	                        continue;
	                    }
			    		if(this.metadata[i].name.toLowerCase() === "sso_name"){
			    			isSSO = true;
			    		    columnDef.push(activeColumnGroup[0]); //Selection column
			    		    columnDef.push({title: $wnd.esasky.getInternationalizationText("tableGroup_Observation"), columns:activeColumnGroup.slice(1)});
			    		    activeColumnGroup = [];
			    		    columnDef.push({title: @esac.archive.esasky.cl.web.client.status.GUISessionStatus::getTrackedSsoName()(), columns:activeColumnGroup});
			    		}
			    		if(this.metadata[i].name.toLowerCase() === "ra" 
			    		    || this.metadata[i].name.toLowerCase() === "ra_deg"
			    		    || this.metadata[i].name.toLowerCase() === "ra_deg_1"
			    		    || this.metadata[i].name.toLowerCase() === "ra_deg_2"
			    		    || this.metadata[i].name.toLowerCase() === "s_ra"){
			    			activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			titleDownload:this.metadata[i].name, 
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			download: true,
				    			formatter:raFormatter,
				    			sorter: "number",
				    			sorterParams: {thousandSeperator: ""},
				    			headerFilter:numericFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:DoubleFilter,
				    			headerFilterFuncParams:{tapName:this.metadata[i].name}
		    				});
			    		}
			    		else if(this.metadata[i].name.toLowerCase() === "dec" 
			    		    || this.metadata[i].name.toLowerCase() === "dec_deg"
			    		    || this.metadata[i].name.toLowerCase() === "dec_deg_1"
			    		    || this.metadata[i].name.toLowerCase() === "dec_deg_2"
			    		    || this.metadata[i].name.toLowerCase() === "s_dec"){
			    			activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			titleDownload:this.metadata[i].name, 
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			download: true,
				    			formatter:decFormatter,
				    			sorter: "number",
				    			sorterParams: {thousandSeperator: ""},
				    			headerFilter:numericFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:DoubleFilter,
				    			headerFilterFuncParams:{tapName:this.metadata[i].name}
		    				});
			    		}
			    		else if(this.metadata[i].name.toLowerCase() === "access_estsize"){
			    			activeColumnGroup.push({
				    			title:$wnd.esasky.getInternationalizationText("tabulator_accessEstSize_header"),
				    			titleDownload:this.metadata[i].name, 
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_accessEstSize_headerTooltip"),
				    			download: true,
				    			formatter:fileSizeFormatter,
				    			sorter: "number",
				    			sorterParams: {thousandSeperator: ""},
				    			headerFilter:numericFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:$wnd.esasky.getInternationalizationText("tabulator_accessEstSize_header")},
				    			headerFilterFunc:DoubleFilter,
				    			headerFilterFuncParams:{tapName:this.metadata[i].name}
		    				});
			    		}
			    		else if(this.metadata[i].datatype.toUpperCase() === "DOUBLE" || this.metadata[i].datatype.toUpperCase() === "REAL"){
			    			activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			titleDownload:this.metadata[i].name, 
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			download: true,
				    			formatter:doubleFormatter,
				    			formatterParams: {maxDecimalDigits: this.metadata[i].maxDecimalDigits || 4},
				    			sorter: "number",
				    			sorterParams: {thousandSeperator: ""},
				    			headerFilter:numericFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:DoubleFilter,
				    			headerFilterFuncParams:{tapName:this.metadata[i].name}
		    				});
		    				
			    		}
			    		else if(this.metadata[i].datatype.toUpperCase() === "TIMESTAMP" || this.metadata[i].datatype.toUpperCase() === "DATETIME"){
			    			activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			titleDownload:this.metadata[i].name, 
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			download: true,
                                formatter: "plaintext",
				    			sorter: "string",
				    			headerFilter:dateFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:DateFilter,
				    			headerFilterFuncParams:{tapName:this.metadata[i].name}
		    				});
			    		}
			    		else if(this.metadata[i].datatype.toUpperCase() === "INTEGER" 
			    		    || this.metadata[i].datatype.toUpperCase() === "INT"){
			    			activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			titleDownload:this.metadata[i].name, 
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			download: true,
				    			formatter:doubleFormatter,
				    			formatterParams: {maxDecimalDigits: 0},
				    			sorter: "number",
				    			sorterParams: {thousandSeperator: ""},
				    			headerFilter:numericFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:DoubleFilter,
				    			headerFilterFuncParams:{tapName:this.metadata[i].name}
		    				});
			    		}
			    		else if(this.metadata[i].datatype.toUpperCase() === "BIGINT" 
			    		    || this.metadata[i].datatype.toUpperCase() === "LONG"){
			    			activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			titleDownload:this.metadata[i].name, 
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			download: true,
				    			formatter:"plaintext",
				    			sorter:  "string",
				    			sorterParams: {thousandSeperator: ""},
				    			headerFilter:stringFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:"like",
				    			headerFilterFuncParams:{tapName:this.metadata[i].name}
			    			});
			    		}
			    		else if(this.metadata[i].datatype.toUpperCase() === "LIST"){
			    			activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			titleDownload:this.metadata[i].name, 
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			download: true,
				    			formatter:doubleFormatter,
				    			sorter: "number",
				    			sorterParams: {thousandSeperator: ""},
				    			formatter:"plaintext",
				    			headerFilter:listFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:"like"
		    				});
			    		}
			    		else if(this.metadata[i].datatype.toUpperCase() === "HTML"){
			    			activeColumnGroup.push({
                                title:this.metadata[i].displayName,
                                titleDownload:this.metadata[i].name, 
                                field:this.metadata[i].name, 
                                visible:this.metadata[i].visible,
                                headerTooltip:this.metadata[i].description,
                                download: true,
                                formatter:"html",
                                sorter:  "string",
                                sorterParams: {thousandSeperator: ""},
                                headerFilter:stringFilterEditor,
                                headerFilterParams:{tapName:this.metadata[i].name,
                                                    title:this.metadata[i].displayName},
                                headerFilterFunc:"like",
                                headerFilterFuncParams:{tapName:this.metadata[i].name}
		    				});
			    		}
			    		else if(this.metadata[i].datatype.toUpperCase() === "STRING_HIDE_NON_DATABASE_VALUES"){
			    			activeColumnGroup.push({
                                title:this.metadata[i].displayName,
                                titleDownload:this.metadata[i].name, 
                                field:this.metadata[i].name, 
                                visible:this.metadata[i].visible,
                                headerTooltip:this.metadata[i].description,
                                download: true,
                                formatter:hideNonDatabaseColumnFormatter,
                                sorter:  "string",
                                sorterParams: {thousandSeperator: ""},
                                headerFilter:stringFilterEditor,
                                headerFilterParams:{tapName:this.metadata[i].name,
                                                    title:this.metadata[i].displayName},
                                headerFilterFunc:"like",
                                headerFilterFuncParams:{tapName:this.metadata[i].name}
		    				});
			    		} else{
				    		activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			titleDownload:this.metadata[i].name, 
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			download: true,
				    			formatter:"plaintext",
				    			sorter:  "string",
				    			sorterParams: {thousandSeperator: ""},
				    			headerFilter:stringFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:"like",
				    			headerFilterFuncParams:{tapName:this.metadata[i].name}
			    			});
			    		}
		    		}
			    	if(!isSSO){
			    	    columnDef = activeColumnGroup;
			    	}
			    	
			    	table.setColumns(columnDef);
	    	        table.getColumns().forEach(function (column){
	    	            if (column.getDefinition().sorter){
	                        column.getElement().onmouseover = function() {
	                            column.getElement().style.backgroundColor = "#d0d0d0";
	                        }
	                        column.getElement().onmouseout = function() {
	                            column.getElement().style.backgroundColor = "";
                            }
        	            } 
                    });
			    }
		    },
		 	selectable:true,
            ajaxError:function(error){
            	error.text().then(function(e){
            		wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onAjaxResponseError(Ljava/lang/String;)(e);
        		});
		 	    
            },
		 	ajaxLoaderLoading: @esac.archive.esasky.cl.web.client.view.common.LoadingSpinner::getLoadingSpinner()(),
		 	ajaxLoaderError:$wnd.esasky.getInternationalizationText("tabulator_loadFailed"),
		    rowSelectionChanged:function(data, rows){
		    	if(isInitializing){
		    		return;
		    	}
			    selectionMap = [];
			    
	    		wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::multiSelectionInProgress()();
		    	rows.forEach(function(item, index, array){
		    		selectionMap[item.getIndex()] = true;
		    		if(!previouslySelectedMap[item.getIndex()]){
			    		wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onRowSelection(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(item);
		    		}
		    	});
		    	Object.keys(previouslySelectedMap).forEach(function(item, index, array){
		    		if(!selectionMap[item] && table.getRow(item)){
			    		wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onRowDeselection(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(table.getRow(item));
		    		}
		    	});
		    	previouslySelectedMap = selectionMap;
	    		wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::multiSelectionFinished()();
		    },

		    rowMouseEnter:function(e, row){
		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onRowEnter(I)(row.getIndex());
		    },

		 	movableColumns: true,
		 	autoColumns: true,
		 	layout: "fitDataFill"
//		 	layout: "fitDataStretch"
		});
		
		if(blockRedraw){
		    table.blockRedraw();
		}
		
		table.isEsaskyData = isEsaskyData;
		//Remove the clearSelection function to make sure that it is possible to select and copy text from the table
		table._clearSelection = function (){};
		
		table.rowManager.adjustTableSize = function () {
    		//Adapted from Tabulator 4.6.2. Change to remove that it changes with the footer height
    		var initialHeight = this.element.clientHeight,
                modExists;
    
            if (this.renderMode === "virtual") {
                var otherHeight = this.columnManager.getElement().offsetHeight;
    
                if (this.fixedHeight) {
                    this.element.style.minHeight = "calc(100% - " + otherHeight + "px)";
                    this.element.style.height = "calc(100% - " + otherHeight + "px)";
                    this.element.style.maxHeight = "calc(100% - " + otherHeight + "px)";
                } else {
                    this.element.style.height = "";
                    this.element.style.height = this.table.element.clientHeight - otherHeight + "px";
                    this.element.scrollTop = this.scrollTop;
                }
                var heightChanged = this.height != this.element.clientHeight;
                this.height = this.element.clientHeight;
                if(heightChanged){
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onTableHeightChanged()();
                }
                this.vDomWindowBuffer = this.table.options.virtualDomBuffer || this.height;
                if(this.table.showCount){
                	footerOffset = (this.table.footerManager && !this.table.footerManager.external ? this.table.footerManager.getElement().offsetHeight : 0);
                }else{
                	footerOffset = 0;
                }
                this.table.footerManager.element.style.marginTop = -  footerOffset + "px";
    
                //check if the table has changed size when dealing with variable height tables
                if (!this.fixedHeight && initialHeight != this.element.clientHeight) {
                    modExists = this.table.modExists("resizeTable");
    
                    if (modExists && !this.table.modules.resizeTable.autoResize || !modExists) {
                        this.redraw();
                        this.vdomHoz.reinitialize();
                    }
                }
            }
        };
	
		table.getVoTableString = function(list, resourceName){
			// Add VOT XML Schema
			var votData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                votData += "<VOTABLE version=\"1.3\" xmlns=\"//www.ivoa.net/xml/VOTable/v1.3\">\n";
				votData += "<RESOURCE name=\"" + $wnd.esasky.escapeXml(resourceName) + "\">\n";
				votData += "<TABLE>\n";
				
			var esaskyToVOStandardType = {};
			esaskyToVOStandardType["DOUBLE"] = "double"
			esaskyToVOStandardType["INTEGER"] = "int"
			esaskyToVOStandardType["BIGINT"] = "long";
			esaskyToVOStandardType["STRING"] = "char";
			esaskyToVOStandardType["VARCHAR"] = "char";
			esaskyToVOStandardType["REAL"] = "float";
			esaskyToVOStandardType["SMALLINT"] = "int";
			esaskyToVOStandardType["TIMESTAMP"] = "char";
			esaskyToVOStandardType["BOOLEAN"] = "boolean";

			// Adds headers to xml
			table.metadata.forEach(function (columnInfo) {
			    if(table.getColumn(columnInfo.name).getDefinition().download){
    				votData += "<FIELD";
    				Object.keys(columnInfo).forEach(function (key) {
    				    var value = columnInfo[key];
    					if(value !== null) {
    					    if(key == "datatype"){
    					    	value = esaskyToVOStandardType[value.toUpperCase()];
    					    	if(value == "char" && !columnInfo.hasOwnProperty("arraysize")){
    					    		votData += " arraysize =\"*\""
    					    	}
    					    }
    						votData += " " + key + "=\"" + $wnd.esasky.escapeXml(value) + "\"";
    					}
    
    				});
    				votData += "/>\n";
			    }
			});

			// Adds data to xml
			votData += "<DATA>\n";
			votData += "<TABLEDATA>\n";

			list.forEach(function (row) {
			    switch(row.type){
			        case "row":
        				votData += "<TR>\n";
        				table.metadata.forEach(function (columnInfo) {
            			    if(table.getColumn(columnInfo.name).getDefinition().download){
            					var value = "";
            					row.columns.some(function(column){
            					    if(column.component.getField() == columnInfo.name){
            					        value = column.value;
            					        return true;
            					    }
            					});
            					votData += "<TD>"
            							+ $wnd.esasky.escapeXml(value)
            							+ "</TD>\n";
            			    }
        				});
        
        				votData += "</TR>\n";
        				break;
			    }
			})

			votData += "</TABLEDATA>\n";
			votData += "</DATA>\n";
			votData += "</TABLE>\n";
			votData += "</RESOURCE>\n";
			votData += "</VOTABLE>\n";

			return votData;
		}
		table.voTableFormatter = function(list, options, setFileContents){
		    setFileContents(table.getVoTableString(list, options.resourceName), "application/x-votable+xml");
		}
		
		table.csvTableFormatter = function csv(list, options, setFileContents) {
            var delimiter = options && options.delimiter ? options.delimiter : ",",
                fileContents = [],
                headers = [];
    
            list.forEach(function (row) {
                var item = [];
    
                switch (row.type) {
                    case "group":
                        console.warn("Download Warning - CSV downloader cannot process row groups");
                        break;
    
                    case "calc":
                        console.warn("Download Warning - CSV downloader cannot process column calculations");
                        break;
    
                    case "header":
                        row.columns.forEach(function (col, i) {
                            if (col && col.depth === 1 && !$wnd.esasky.nonDatabaseColumns.includes(col.component.getField())) {
                                headers.push(typeof col.value == "undefined" || typeof col.value == "null" ? "" : col.value);
                            }
                        });
                        
                        
                        
                        break;
    
                    case "row":
                        row.columns.forEach(function (col) {
    
                            if (col && !$wnd.esasky.nonDatabaseColumns.includes(col.component.getField())) {
    
                                switch (typeof col.value) {
                                    case "object":
                                        col.value = JSON.stringify(col.value);
                                        break;
    
                                    case "undefined":
                                    case "null":
                                        col.value = "";
                                        break;
                                }
    
                                item.push('"' + String(col.value).split('"').join('""') + '"');
                            }
                        });
                        
                        fileContents.push(item.join(delimiter));
                        break;
                }
            });
    
            if (headers.length) {
                fileContents = [headers].concat(fileContents);
            }
    
            fileContents = fileContents.join("\n");
    
            if (options.bom) {
                fileContents = "\uFEFF" + fileContents;
            }
    
            setFileContents(fileContents, "text/csv");
        }
		    
		table.convertDataToTabulatorFormat = function(userData, aladinCoordinateFrame) {
            var metadata = [];
            var skyObjectList = userData.overlaySet.skyObjectList;
            var data = [];
            var cooFrame = userData.overlaySet.cooframe || 'J2000';
            var coordinateConversionFunction = function (ra, dec){
                return [ra, dec];
            }
            
            if (cooFrame.toLowerCase() === 'galactic' || cooFrame.toLowerCase() === 'gal') {
                coordinateConversionFunction = function (ra, dec) {
                    return @esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion::convertPointGalacticToJ2000(DD)(ra, dec);
                }
            }
            var i = 0;
            skyObjectList.forEach(function (skyObject) {
                var row = {id:i};
                var ra, dec = undefined;
                Object.keys(skyObject).forEach(function(key) {
                    if(key === "data"){
                        skyObject[key].forEach(function(extraData){
                        	if(i==0){
                            	metadata.push({name:extraData.name, displayName: $wnd.esasky.getColumnDisplayText(extraData.name), datatype:extraData.type, visible: true});
                        	}
                           if(extraData.type.toUpperCase() === "DOUBLE" || extraData.type.toUpperCase() === "REAL"){
								row[extraData.name] = parseFloat(extraData.value);
				    			if(isNaN(row[extraData.name])){
									row[extraData.name] = undefined;
				    			}
				    			} else if(extraData.type.toUpperCase() === "INTEGER" || extraData.type.toUpperCase() === "INT"){
									row[extraData.name] = parseInt(extraData.value);
					    			if(isNaN(row[extraData.name])){
										row[extraData.name] = undefined;
					    			}
				    			} else if(extraData.type.toUpperCase() === "BIGINT"|| extraData.type.toUpperCase() === "LONG"){
									row[extraData.name] = extraData.value;
				    			} else {
									row[extraData.name] = extraData.value;
				    			}
                        });
                        
                    } else if(key !== 'id'){
                        if(key.toLowerCase() === 'ra' || key.toLowerCase() === 'ra_deg'){
                            ra = skyObject[key];
                            if(dec){
                                setRaDec(ra, dec, row, metadata, i==0);
                            }
                        } else if(key.toLowerCase() === 'dec' || key.toLowerCase() === 'dec_deg'){
                            dec = skyObject[key];
                            if(ra){
                                setRaDec(ra, dec, row, metadata, i==0);
                            }
                        } else {
                            row[key] = skyObject[key];
                            if(i==0){
                            	metadata.push({name:key, displayName: $wnd.esasky.getColumnDisplayText(key), datatype:"STRING", visible: true});
                            }
                        }
                    }
                });
        		i++;
                data.push(row);
            });
    
            function setRaDec(ra, dec, row, metadata, shouldAddMetaData) {
                convertedCoordinate = coordinateConversionFunction(ra, dec);
                row["ra_deg"] = parseFloat(convertedCoordinate[0]);
                row["dec_deg"] = parseFloat(convertedCoordinate[1]);
                if(shouldAddMetaData){
                	metadata.push({name:"ra_deg", displayName: $wnd.esasky.getColumnDisplayText("RA_J2000"), datatype:"DOUBLE", visible: true});
                	metadata.push({name:"dec_deg", displayName: $wnd.esasky.getColumnDisplayText("DEC_J2000"), datatype:"DOUBLE", visible: true});
                }
            }
            
            table.metadata = metadata;
            
            return data;
    
        };
        
        table.filterData = [];
        table.showCount = false;
		isInitializing = false;
		table.dataLoaded = false;
		
		table.element.onmouseleave = function(){wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onTableMouseLeave()()};
        $doc.getElementById(divId + "_rowCount").addEventListener("mouseover", function(){wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onRowCountFooterMouseOver()()});
        
        
//		if(!$wnd.tabulatorTables){$wnd.tabulatorTables = []}
//		$wnd.tabulatorTables.push(table);
		return table;
	}-*/;

    public void onRowEnter(int rowId) {
    	long currentTime = System.currentTimeMillis();
    	if(currentTime - lastHoverTime > 5) {
    		lastHoverTime = currentTime;
    		tabulatorCallback.onRowMouseEnter(rowId);
    		if(lastHoveredRow > -1 && lastHoveredRow != rowId) {
    			tabulatorCallback.onRowMouseLeave(lastHoveredRow);
    		}
    		lastHoveredRow = rowId;
    	}
    }
    
    public void onTableMouseLeave() {
    	if(lastHoveredRow > -1) {
    		tabulatorCallback.onRowMouseLeave(lastHoveredRow);
    	}
    }

    public void onDataLoaded() {
        if(tableJsObject != null) {
        	setCorrectFilterBehaviour();
        	tabulatorCallback.onDataLoaded(tableJsObject.invokeFunction("getData"));
        }
    }

    public void onTableHeightChanged() {
        if(tableJsObject != null) {
            tabulatorCallback.onTableHeightChanged();
        }
    }

    public void onDatalinkClicked(final GeneralJavaScriptObject row) {
        tabulatorCallback.onDatalinkClicked(row);
    }

    public void onAccessUrlClicked(String url){
        tabulatorCallback.onAccessUrlClicked(url);
    }
    
    public void onPostcardUrlClicked(final GeneralJavaScriptObject rowData, String columnName){
        tabulatorCallback.onPostcardUrlClicked(rowData, columnName);
    }
    
    public void onCenterClicked(final GeneralJavaScriptObject rowData) {
        tabulatorCallback.onCenterClicked(rowData);
    }
    
    public void onSendToVoApplicaitionClicked(final GeneralJavaScriptObject rowData) {
        tabulatorCallback.onSendToVoApplicaitionClicked(rowData);
    }
    
    public void onLink2ArchiveClicked(final GeneralJavaScriptObject rowData) {
        tabulatorCallback.onLink2ArchiveClicked(rowData);
    }
    
    public void onSourcesInPublicationClicked(final GeneralJavaScriptObject rowData) {
        tabulatorCallback.onSourcesInPublicationClicked(rowData);
    }

    public void onRowSelection(GeneralJavaScriptObject row) {
        tabulatorCallback.onRowSelection(row);
    }

    public void onRowDeselection(GeneralJavaScriptObject row) {
        tabulatorCallback.onRowDeselection(row);
    }

    public void onFilterChanged(String label, String filter) {
        tabulatorCallback.onFilterChanged(label, filter);
    }
    
    public String getLabelFromTapName(String tapName) {
    	return tabulatorCallback.getLabelFromTapName(tapName);
    }

    public GeneralJavaScriptObject getDescriptorMetaData() {
    	return tabulatorCallback.getDescriptorMetaData();
    }
    
    public String getRaColumnName() {
        return tabulatorCallback.getRaColumnName();
    }
    
    public String getDecColumnName() {
        return tabulatorCallback.getDecColumnName();
    }
    
    public void onAjaxResponse() {
        tabulatorCallback.onAjaxResponse();
    }
    
    public void onAjaxResponseError(String error) {
        tabulatorCallback.onAjaxResponseError(error);
    }
    
    public void multiSelectionInProgress() {
        tabulatorCallback.multiSelectionInProgress();
    }
    
    public void multiSelectionFinished() {
        tabulatorCallback.multiSelectionFinished();
    }
    
    public boolean hasBeenClosed() {
        return tabulatorCallback.hasBeenClosed();
    }
    
    
    public boolean isMOCMode() {
    	return tabulatorCallback.isMOCMode();
    }
    
    public void disableFilters() {
    	disableFilters(tableJsObject);
    	filtersShouldBeEnabled = false;
    }
    
    private native void disableFilters(GeneralJavaScriptObject table) /*-{
    	function loopChildren(el){
    		el.classList.add("tabulator-header-filter-disabled");
    		el.setAttribute("disabled", true);
    		for(var i = 0; i < el.children.length; i++){
    			loopChildren(el.children[i]);
    		};
    	}
    	
    	var list =  table.element.getElementsByClassName("tabulator-header-filter")
    	for(var i = 0; i < list.length; i++){
    		loopChildren(list[i]);
    	};
    }-*/;

    public void enableFilters() {
    	enableFilters(tableJsObject);
    	filtersShouldBeEnabled = true;
    }
    
    private native void enableFilters(GeneralJavaScriptObject table) /*-{
    	var list =  Array.from(table.element.getElementsByClassName("tabulator-header-filter-disabled"))
    	for(var i = 0; i < list.length; i++){
    		list[i].classList.remove("tabulator-header-filter-disabled");
		    list[i].setAttribute("disabled", false);
    	};
    }-*/;
    
    public void setCorrectFilterBehaviour() {
    	if(filtersShouldBeEnabled) {
    		enableFilters();
    	}else {
    		disableFilters();
    	}
    }
    
    public void registerMocLoadedObserver() {
    	MocRepository.getInstance().registerMocLoadedObserver(tabulatorCallback.getEsaSkyUniqId() + "_moc", new MocRepository.MocLoadedObserver() {
			
			@Override
			public void onLoaded() {
				tableJsObject.setProperty("mocLoaded", true);
				if(tableJsObject.hasProperty("onMocLoaded")) {
					tableJsObject.invokeFunction("onMocLoaded");
				}
				MocRepository.getInstance().unRegisterMocLoadedObserver(tabulatorCallback.getEsaSkyUniqId() + "_moc");
			}
		});
    }

    public void notifyMocLoadedObserver() {
    	MocRepository.getInstance().notifyMocLoaded(tabulatorCallback.getEsaSkyUniqId() + "_header");
    }

    public boolean isDataProductDatalink() {
        return isDataProductDatalink(tableJsObject);
    }
    
    private native boolean isDataProductDatalink(GeneralJavaScriptObject tableJsObject)/*-{
        var firstRow = tableJsObject.getRows()[0];
        if(!firstRow) {return false;}
        return firstRow.getData().access_format && firstRow.getData().access_format.toLowerCase().includes("datalink");
    }-*/;
    
}
