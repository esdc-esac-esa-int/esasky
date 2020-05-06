package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.i18n.client.NumberFormat;

import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.model.TableColumnHelper;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.DateFilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.DoubleFilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.FilterDialogBox;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;


public class TabulatorWrapper{

    public interface TabulatorCallback {
        public void onDataLoaded(GeneralJavaScriptObject javaScriptObject);
        public void onRowSelection(GeneralJavaScriptObject row);
        public void onRowDeselection(GeneralJavaScriptObject row);
        public void onRowMouseEnter(int rowId);
        public void onRowMouseLeave(int rowId);
        public void onFilterChanged(String label, String filter);
        public void onDataFiltered(List<Integer> filteredRows);
        public void onDatalinkClicked(GeneralJavaScriptObject javaScriptObject);
        public void onAccessUrlClicked(String url);
        public void onPostcardUrlClicked(GeneralJavaScriptObject rowData);
        public void onCenterClicked(GeneralJavaScriptObject rowData);
        public void onSendToVoApplicaitionClicked(GeneralJavaScriptObject rowData);
        public void onLink2ArchiveClicked(GeneralJavaScriptObject rowData);
        public void onSourcesInPublicationClicked(GeneralJavaScriptObject rowData);
        public String getLabelFromTapName(String tapName);
        public GeneralJavaScriptObject getDescriptorMetaData();
    }

    private TabulatorCallback tabulatorCallback;
    private GeneralJavaScriptObject tableJsObject;
    private Map<String, FilterDialogBox> filterDialogs = new HashMap<>();

    public TabulatorWrapper(String divId, TabulatorCallback tabulatorCallback, 
            boolean addSendToVOApplicationColumn, boolean addLink2ArchiveColumn, boolean addCentreColumn, boolean addSourcesInPublicationColumn) {
        this.tabulatorCallback = tabulatorCallback;
        tableJsObject = createColumnTabulator(this, divId, addSendToVOApplicationColumn, addLink2ArchiveColumn, addCentreColumn, addSourcesInPublicationColumn);
    }
    
    public void selectRow(int rowId) {
        GeneralJavaScriptObject row = tableJsObject.invokeFunction("getRow", "" + rowId);
        row.invokeFunction("select");
        row.invokeFunction("scrollTo");
    }

    public void deselectRow(int rowId) {
        GeneralJavaScriptObject row = tableJsObject.invokeFunction("getRow", "" + rowId);
        row.invokeFunction("deselect");
    }

    public void hoverStart(int rowId) {
        GeneralJavaScriptObject element = tableJsObject.invokeFunction("getRow", "" + rowId).invokeFunction("getElement").getProperty("style");
        element.setProperty("background-color", "rgba(255, 255, 255, 0.15)");
    }

    public void hoverStop(int rowId) {
        GeneralJavaScriptObject element = tableJsObject.invokeFunction("getRow", "" + rowId).invokeFunction("getElement").getProperty("style");
        element.setProperty("background-color", "");
    }

    public void downloadCsv(String fileName){
        downloadCsv(tableJsObject, fileName);
    }

    private native void downloadCsv(GeneralJavaScriptObject tableJsObject, String fileName)/*-{
        tableJsObject.download("csv", fileName);
    }-*/;

    public void downloadVot(String fileName, String resourceName){
        downloadVot(tableJsObject, fileName, resourceName);
    }

    private native void downloadVot(GeneralJavaScriptObject tableJsObject, String fileName, String resourceName)/*-{
        tableJsObject.download(tableJsObject.voTableFormatter, fileName, {resourceName:resourceName});
    }-*/;

    public String getVot(String resourceName){
        return getVot(tableJsObject, resourceName);
    }

    private native String getVot(GeneralJavaScriptObject tableJsObject, String resourceName)/*-{
        return tableJsObject.getVoTableString(tableJsObject.getData(), resourceName);
    }-*/;
    
    public void setPlaceholderText(String text){
        setPlaceholderText(tableJsObject, text);
    }
    
    private native void setPlaceholderText(GeneralJavaScriptObject tableJsObject, String text)/*-{
    	tableJsObject.options.placeholder.innerText = text;
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
    
    public void showNumericFilterDialog(String tapName, String title, String filterButtonId, double minVal, double maxVal, final GeneralJavaScriptObject onChangeFunc) {

    	if(!filterDialogs.containsKey(tapName)) {
    		FilterObserver filterObserver = new FilterObserver() {
				
				@Override
				public void onNewFilter(String filter) {
					onChangeFunc.invokeFunction("onChange", filter);
					
				}
			};
    		
    		DoubleFilterDialogBox filterDialog = new DoubleFilterDialogBox(tapName, title, filterButtonId, filterObserver);
    		
    		boolean isInt = false;
    		
    		NumberFormat numberFormat = NumberFormat.getFormat("0.##");
    		
    		if((minVal - Math.floor(minVal)) == 0 && (maxVal - Math.floor(maxVal)) == 0) {
    			numberFormat = NumberFormat.getFormat("0");
    			isInt = true;
    		}
    		
    		filterDialog.setRange(minVal, maxVal, numberFormat, 2);
    		filterDialog.setInt(isInt);
    		filterDialogs.put(tapName, filterDialog);
    	}
    	
    	FilterDialogBox filterDialogBox = filterDialogs.get(tapName);
    	filterDialogBox.show();
    	
    }

    public void showDateFilterDialog(String tapName, String title, String filterButtonId, String minVal, String maxVal, final GeneralJavaScriptObject onChangeFunc) {
    	
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
    	filterDialogBox.show();
    	
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
    public void insertData(GeneralJavaScriptObject data){
        setData(convertDataToTabulatorFormat(tableJsObject, data, AladinLiteWrapper.getCoordinatesFrame().getValue()));
    }
    
    private native String convertDataToTabulatorFormat(GeneralJavaScriptObject tableJsObject, GeneralJavaScriptObject data, String aladinFrame)/*-{
        return tableJsObject.convertDataToTabulatorFormat(data, aladinFrame);
    }-*/;
    
    public void setData(String dataOrUrl){
    	setData(tableJsObject, dataOrUrl);
    }

    private native void setData(GeneralJavaScriptObject tableJsObject, Object dataOrUrl)/*-{
    	console.log(tableJsObject.element);
        tableJsObject.setData(dataOrUrl);
        
        var observer = new MutationObserver(function(mutations){
    		  for (var i=0; i < mutations.length; i++){
    		    for (var j=0; j < mutations[i].addedNodes.length; j++){
    		      if(mutations[i].addedNodes[j].classList && mutations[i].addedNodes[j].classList.contains("tabulator-cell")){
    		      	tableJsObject.redraw(true);
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
			descriptorMetaData = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getDescriptorMetaData()();
			
			metadata = response.metadata;
			for(var j = 0; j < metadata.length; j++){
				
				metadata[j]["visible"] = true;
				metadata[j]["displayName"] = metadata[j].name;
				
				if(descriptorMetaData.hasOwnProperty(metadata[j].name)){
				
					if(descriptorMetaData[metadata[j].name].hasOwnProperty("visible")){
						metadata[j].visible = descriptorMetaData[metadata[j].name]["visible"];
					}
					if(descriptorMetaData[metadata[j].name].hasOwnProperty("label")){
						metadata[j].displayName = descriptorMetaData[metadata[j].name]["label"];
					}
				}
				displayName = $wnd.esasky.getColumnDisplayText(metadata[j].displayName);
				
				metadata[j].displayName = displayName;
			}
			var data = [];
			for(var i = 0; i < response.data.length; i++){
				var row = {id:i};
				for(var j = 0; j < metadata.length; j++){
	    			if(metadata[j].datatype === "DOUBLE" || metadata[j].datatype === "REAL" || metadata[j].datatype === "INTEGER"){
						row[metadata[j].name] = parseFloat(response.data[i][j]);
		    			if(isNaN(row[metadata[j].name])){
							row[metadata[j].name] = undefined;
		    			}
	    			} else {
						row[metadata[j].name] = response.data[i][j];
	    			}
				}
				data[i] = row;
			}		
			tableJsObject.metadata = metadata;
	        return data;
	    }
    }-*/;
    
    public void setHeaderQueryMode(String mode){
        setHeaderQueryMode(this, tableJsObject, mode);
    }
    
    private native void setHeaderQueryMode(TabulatorWrapper wrapper, GeneralJavaScriptObject tableJsObject, String mode)/*-{
        tableJsObject.clearData();
        
        if(mode == 'localMinMax'){
        	tableJsObject.options.ajaxResponse = function(url, params, response){
				var metadata = response.metadata;
				newMeta = [];
				filterData = {};
				
				var descMetaData = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getDescriptorMetaData()();
				
				for(var j = 0; j < metadata.length; j++){
	    			
	    			name = metadata[j].name.substring(0,metadata[j].name.length - 4)
	    			displayName = $wnd.esasky.getColumnDisplayText(name);
	    			
    				//If not in descMetaData add to uniqu spot in end and then we remove all empty slots in end
					var metaDataIndex = metadata.length + newMeta.length;
					if(descMetaData.hasOwnProperty(name)){
						metaDataIndex = parseInt(descMetaData[name].index);
					}
	    			
	    			if(metadata[j].datatype.toUpperCase() == "TIMESTAMP"){
	    				datatype = "TIMESTAMP";
	    			}else{
    					datatype = "DOUBLE";
    				}

	    			if(metadata[j].name.endsWith("_min")){
	    				meta = {name:name, displayName:displayName, datatype:datatype}
	    				newMeta.push(meta)
	    				if(!filterData[name]){
	    					filterData[name] = {};
	    				}
	    				
	    				if(metadata[j].datatype.toUpperCase() == "TIMESTAMP"){
							filterData[name]["min"] = response.data[0][j];
		    			}else{
							filterData[name]["min"] = parseFloat(response.data[0][j]);
	    				}
	    			}
	    			else if(metadata[j].name.endsWith("_max")){
    					if(!filterData[name]){
	    					filterData[name] = {};
	    				}
	    				
						if(metadata[j].datatype.toUpperCase() == "TIMESTAMP"){
							filterData[name]["max"] = response.data[0][j];
		    			}else{
							filterData[name]["max"] = parseFloat(response.data[0][j]);
	    				}	    			
					}
	    			else if(metadata[j].name.endsWith("_lst")){
	    				list = response.data[0][j];
	    				list = list.replace("{","[").replace("}","]");
    					if(!filterData[name]){
    						filterData[name] = {};
	    				}
						filterData[name]["list"] = list;
						meta = {name:name, displayName:displayName, datatype:"LIST"}
						newMeta.push(meta)
	    			}
	    			else{
	    				meta = {name:name, displayName:displayName, datatype:"STRING"}
	    				newMeta.push(meta)
	    			}
	    			
				}
				
				newMeta = newMeta.filter(function(e){return e})
				tableJsObject.metadata = newMeta;
				tableJsObject.filterData = filterData;
		        return [];
		    }
        }else{
        	tableJsObject.options.ajaxResponse = function(url, params, response){
				var md = response.metadata;
				newMeta = [];
				var filterData = {};
				var colNameIndex, minIndex, maxIndex;
				for(var j = 0; j < md.length; j++){
					if(md[j].name.endsWith("min_value")){
						minIndex = j;
					}
					else if(md[j].name.endsWith("max_value")){
						maxIndex = j
					}
					else if(md[j].name == "column_name"){
						colNameIndex = j
					}
				}
				
				var descMetaData = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getDescriptorMetaData()();
				
				for(var i = 0; i < response.data.length; i++){
					
					var name = response.data[i][colNameIndex];
					
					displayName = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getLabelFromTapName(Ljava/lang/String;)(name);
					displayName = $wnd.esasky.getColumnDisplayText(displayName);
					
					
					//If not in descMetaData add to uniqu spot in end and then we remove all empty slots in end
					var metaDataIndex = response.data.length + newMeta.length;
					if(descMetaData.hasOwnProperty(name)){
						metaDataIndex = parseInt(descMetaData[name].index);
					}
					
					minVal = parseFloat(response.data[i][minIndex]);
					maxVal = parseFloat(response.data[i][maxIndex]);
					
					if(maxVal > minVal){
						if(Math.floor(minVal) == minVal && Math.floor(maxVal) == maxVal){
		    				meta = {name:name, displayName:displayName, datatype:"INTEGER", visible:true}
		    				newMeta[metaDataIndex] = meta
						}else{
		    				meta = {name:name, displayName:displayName, datatype:"DOUBLE", visible:true}
		    				newMeta[metaDataIndex] = meta
						}
						
	    				if(!filterData[name]){
	    					filterData[name] = {};
	    				}
						filterData[name]["min"] = minVal;
						filterData[name]["max"] = maxVal;
					}
	    			else{
	    				meta = {name:name, displayName:displayName, datatype:"STRING", visible:true}
	    				newMeta[metaDataIndex] = meta
	    			}
    			}

				//Removes empty slots
				newMeta = newMeta.filter(function(e){return e})
				tableJsObject.metadata = newMeta;
				tableJsObject.filterData = filterData;
		        return [];
			}
        }
    }-*/;

    private native GeneralJavaScriptObject createColumnTabulator(TabulatorWrapper wrapper, String divId, 
            boolean addSendToVOApplicationColumn, boolean addLink2ArchiveColumn,
            boolean addCentreColumn, boolean addSourcesInPublicationColumn) /*-{
		var visibleTableData = [];
		var visibleTableDataIndex = 0;

		var isInitializing = true;
		var previouslySelectedMap = [];
		var selectionMap = [];
		var columnDef = [];
		
        var createFilterButton = function(filterButtonId){
            filterIcon = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getFilterIcon()();

            var filterButton = $wnd.$( "" + "<div id=\'" + filterButtonId
                        + "\' class=\"filterButton defaultEsaSkyButton darkStyle smallButton squaredButton gwt-PushButton-up\" "
                        + "title=\""  + "\""
                        + "\"" + ">" + "<img src=\"" + filterIcon
                        + "\" class=\"fillParent\" />" + "</div>");
		    
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
					cell.getColumn()._column.cells.forEach(function (row){
						if(row.getValue() != undefined){
							minVal = Math.min(minVal, row.getValue())
							maxVal = Math.max(maxVal, row.getValue())
						}
					});
				}
				
				if(minVal == Infinity){
					minVal = -100;
					maxVal = 100;
				}
				
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::showNumericFilterDialog(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DDLesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)
					(editorParams["tapName"],editorParams["title"], filterButtonId, minVal, maxVal, functionObject);
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
					cell.getColumn()._column.cells.forEach(function (row){
						if(row.getValue() != undefined){
							if(minVal > row.getValue()){
								minVal = row.getValue();
							}
							if(maxVal < row.getValue()){
								maxVal = row.getValue();
							}
						}
					});
				}
				
				if(minVal > maxVal){
					tmp = minVal
					minVal = maxVal
					maxVal = tmp;
				}
				
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::showDateFilterDialog(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)
					(editorParams["tapName"],editorParams["title"], filterButtonId, minVal, maxVal, functionObject);
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
		
		var doubleFormatter = function(cell, formatterParams, onRendered){
			
			if(cell.getValue() == undefined){
				return "";
			}
			
			var decimals = 4;

			if(Math.abs(cell.getValue()) > Math.pow(10, -decimals)){
				return cell.getValue().toFixed(decimals)
			}
			
			return cell.getValue().toExponential(decimals - 1);
		}

		function DoubleFilter(headerValue, rowValue, rowData, filterParams){
			
			var split = headerValue.split(",");

			if(split.length == 2){
				
				if(!rowValue){
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
		
		var footerCounter = "<div></div><div class=\"footerCounter\">0</div>"
		

		var table = new $wnd.Tabulator("#" + divId, {
		 	height:"100%", // set height of table (in CSS or here), this enables the Virtual DOM and improves render speed dramatically (can be any valid css height value)
		 	placeholder:"",
    		footerElement:footerCounter,
		    dataFiltered:function(filters, rows){
		    	var returnString = "";
		    	for(var i = 0; i < rows.length; i++){
		    		returnString += rows[i].getIndex() + ",";
		    	}

		  		wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onDataFiltered(Ljava/lang/String;)(returnString);

			   	var footerCounter = this.footerManager.element.getElementsByClassName("footerCounter")[0];
			   	var text = $wnd.esasky.getInternationalizationText("tabulator_rowCount");
			   	text = text.replace("$count$", rows.length);
			   	if(footerCounter){
					footerCounter.innerHTML = text;
			   	}
		    },
		    dataLoaded:function(data){
		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onDataLoaded()();
		    	this.rowManager.adjustTableSize();
		    },
		    dataLoading:function(data){
		        var activeColumnGroup = [];
		    	activeColumnGroup.push({formatter:"rowSelection", titleFormatter:"rowSelection", sorter:function(a, b, aRow, bRow, column, dir, sorterParams){
					return bRow.isSelected() - aRow.isSelected();
					
				}});

		    	var imageButtonFormatter = function(cell, formatterParams, onRendered){ 
                    return "<div class='buttonCell' title='" + formatterParams.tooltip + "'><img src='images/" + formatterParams.image + "'/></div>";
                };
		    	var linkListFormatter = function(cell, formatterParams, onRendered){ 
                    return $wnd.esasky.linkListFormatter(cell.getValue(), 100);
                };
                if(addCentreColumn){
                    activeColumnGroup.push({
                        title:$wnd.esasky.getInternationalizationText("tabulator_centreHeader"),
                        headerSort:false,
                        headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_centreHeaderTooltip"),
                        minWidth: 50,
                        formatter:imageButtonFormatter, width:40, hozAlign:"center", formatterParams:{image:"recenter.png", 
                            tooltip:$wnd.esasky.getInternationalizationText("tabulator_centreOnShape")},
                            cellClick:function(e, cell){
                                e.stopPropagation();
                		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onCenterClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getData());
                            }
                    });
                }
                if(addSendToVOApplicationColumn){
                    activeColumnGroup.push({
                        title:$wnd.esasky.getInternationalizationText("tabulator_sendToVOApplicationHeader"),
                        headerSort:false, 
                        headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_sendRowToVOApplicationHeaderTooltip"),
                        minWidth: 50,
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
                        headerSort:false, 
                        headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_link2ArchiveHeaderTooltip"),
                        minWidth: 62,
                        formatter:imageButtonFormatter, width:40, hozAlign:"center", formatterParams:{image:"link2archive.png",
                            tooltip:$wnd.esasky.getInternationalizationText("tabulator_link2ArchiveButtonTooltip")},
                            cellClick:function(e, cell){
                                e.stopPropagation();
                		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onLink2ArchiveClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getData());
                            }
                    });
                }
                if(addSourcesInPublicationColumn){
                    activeColumnGroup.push({
                        title:$wnd.esasky.getInternationalizationText("tabulator_SourcesInPublicationHeader"),
                        headerSort:false, 
                        headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_SourcesInPublicationHeaderTooltip"),
                        minWidth: 65,
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
			    		if(this.metadata[i].name.toLowerCase() === "access_url"){
	                        activeColumnGroup.push({
	                            title:this.metadata[i].name,
	                            field:this.metadata[i].name,
	                            headerSort:false, 
	                            headerTooltip:this.metadata[i].description,
	                            minWidth: 85,
	                            formatter:imageButtonFormatter, width:40, hozAlign:"center", formatterParams:{image:"download_small.png", 
	                                tooltip:$wnd.esasky.getInternationalizationText("tabulator_download")}, 
	                                cellClick:function(e, cell){
	                                    e.stopPropagation();
	                                    if(cell.getData().access_format && cell.getData().access_format.toLowerCase().includes("datalink")){
	                        		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onDatalinkClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getRow());
	                                    } else {
	                        		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onAccessUrlClicked(Ljava/lang/String;)(cell.getData().access_url);
	                                    }
	                                }
	                        });
	                        continue;
			    		}
			    		if(this.metadata[i].name.toLowerCase() === "postcard_url"){
	                        activeColumnGroup.push({
	                            title:$wnd.esasky.getInternationalizationText("tabulator_preview"),
	                            field:this.metadata[i].name,
	                            headerSort:false, 
	                            headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_previewHeaderTooltip"),
	                            minWidth: 50,
	                            formatter:imageButtonFormatter, width:40, hozAlign:"center", formatterParams:{image:"preview.png", 
	                                tooltip:$wnd.esasky.getInternationalizationText("tabulator_preview")}, 
	                                cellClick:function(e, cell){
	                                    e.stopPropagation();
	                    		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onPostcardUrlClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getRow());
	                                }
	                        });
	                        continue;
			    		}
			    		if(this.metadata[i].name.toLowerCase() === "author"){
	                        activeColumnGroup.push({
	                            title:this.metadata[i].name,
	                            field:this.metadata[i].name,
	    		    			sorter: "string",
	    		    			headerFilter:true,
	    		    			headerFilterFunc:"like",
	    		    			headerFilterFuncParams:{tapName:metadata[i].name},
	                            headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_authorHeaderTooltip"),
	                            formatter:linkListFormatter});
	                        continue;
	                    }
			    		if(this.metadata[i].name.toLowerCase() === "sso_name"){
			    		    columnDef.push(activeColumnGroup[0]); //Selection column
			    		    columnDef.push({title: $wnd.esasky.getInternationalizationText("tableGroup_Observation"), columns:activeColumnGroup.slice(1)});
			    		    activeColumnGroup = [];
			    		    columnDef.push({title: @esac.archive.esasky.cl.web.client.status.GUISessionStatus::getTrackedSsoName()(), columns:activeColumnGroup});
			    		}
			    		if(this.metadata[i].datatype === "DOUBLE" || this.metadata[i].datatype === "REAL"){
			    			activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			formatter:doubleFormatter,
				    			sorter: "number",
				    			headerFilter:numericFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:DoubleFilter,
				    			headerFilterFuncParams:{tapName:this.metadata[i].name}
		    				});
		    				
			    		}
			    		else if(this.metadata[i].datatype === "TIMESTAMP"){
			    			activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			formatter:doubleFormatter,
				    			sorter: "string",
				    			formatter: "plaintext",
				    			headerFilter:dateFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:DateFilter,
				    			headerFilterFuncParams:{tapName:this.metadata[i].name}
		    				});
			    		}
			    		else if(this.metadata[i].datatype === "INTEGER"){
			    			activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			formatter:doubleFormatter,
				    			sorter: "number",
				    			formatter:"plaintext",
				    			headerFilter:numericFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:DoubleFilter,
				    			headerFilterFuncParams:{tapName:this.metadata[i].name}
		    				});
			    		}
			    		else if(this.metadata[i].datatype === "LIST"){
			    			activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			formatter:doubleFormatter,
				    			sorter: "number",
				    			formatter:"plaintext",
				    			headerFilter:listFilterEditor,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:"like",
		    				});
			    		}else{
				    		activeColumnGroup.push({
				    			title:this.metadata[i].displayName,
				    			field:this.metadata[i].name, 
				    			visible:this.metadata[i].visible,
				    			headerTooltip:this.metadata[i].description,
				    			formatter:"plaintext",
				    			sorter:  "string",
				    			headerFilter:true,
				    			headerFilterParams:{tapName:this.metadata[i].name,
				    								title:this.metadata[i].displayName},
				    			headerFilterFunc:"like",
				    			headerFilterFuncParams:{tapName:this.metadata[i].name}
			    			});
			    		}
		    		}
			    	if(columnDef.length == 0){
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
		    rowSelectionChanged:function(data, rows){
		    	if(isInitializing){
		    		return;
		    	}
			    selectionMap = [];
		    	rows.forEach(function(item, index, array){
		    		selectionMap[item.getIndex()] = true;
		    		if(!previouslySelectedMap[item.getIndex()]){
			    		wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onRowSelection(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(item);
		    		}
		    	});
		    	Object.keys(previouslySelectedMap).forEach(function(item, index, array){
		    		if(!selectionMap[item]){
			    		wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onRowDeselection(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(table.getRow(item));
		    		}
		    	});
		    	previouslySelectedMap = selectionMap;
		    },

		    rowMouseEnter:function(e, row){
		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onRowEnter(I)(row.getIndex());
		    },
		    rowMouseLeave:function(e, row){
		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onRowLeave(I)(row.getIndex());
		    },

		 	movableColumns: true,
		 	autoColumns: true,
		 	layout: "fitDataFill"
		});
		
		table.rowManager.adjustTableSize = function () {
		//Change to remove that it changes with the footer height
		if (this.renderMode === "virtual") {

			this.height = this.element.clientHeight;

//			this.vDomWindowBuffer = this.table.options.virtualDomBuffer || this.height;

			var otherHeight = this.columnManager.getElement().offsetHeight ;
			
			this.element.style.minHeight = "calc(100% - " + otherHeight + "px)";

			this.element.style.height = "calc(100% - " + otherHeight + "px)";

			this.element.style.maxHeight = "calc(100% - " + otherHeight + "px)";
			
			footerOffset = (this.table.footerManager && !this.table.footerManager.external ? this.table.footerManager.getElement().offsetHeight : 0);
			
			this.table.footerManager.element.style.marginTop = -  footerOffset + "px";
			
		}
	};
	
		table.getVoTableString = function(data, resourceName){
			// Add VOT XML Schema
			var votData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                votData += "<VOTABLE version=\"1.3\" xmlns=\"//www.ivoa.net/xml/VOTable/v1.3\">\n";
				votData += "<RESOURCE name=\"" + $wnd.esasky.escapeXml(resourceName) + "\">\n";
				votData += "<TABLE>\n";

			// Adds headers to xml
			table.metadata.forEach(function (columnInfo) {
				votData += "<FIELD";
				Object.keys(columnInfo).forEach(function (key) {
				    var value = columnInfo[key];
					if(value !== null) {
					    if(value === 'linklist' || value === 'link2archive') {//ESASky specific types
					        value = 'char'
					    }
						votData += " " + key + "=\"" + $wnd.esasky.escapeXml(value) + "\"";
					}

				});
				votData += "/>\n";
			});

			// Adds data to xml
			votData += "<DATA>\n";
			votData += "<TABLEDATA>\n";

			data.forEach(function (row) {
				votData += "<TR>\n";
				table.metadata.forEach(function (columnInfo) {
					var value = $wnd.esasky.escapeXml(row[columnInfo.name]);
					votData += "<TD>"
							+ value
							+ "</TD>\n";
				});

				votData += "</TR>\n";
			})

			votData += "</TABLEDATA>\n";
			votData += "</DATA>\n";
			votData += "</TABLE>\n";
			votData += "</RESOURCE>\n";
			votData += "</VOTABLE>\n";

			return votData;
		}
		table.voTableFormatter = function(columns, data, options, setFileContents){
		    setFileContents(table.getVoTableString(data.data, options.resourceName), "application/x-votable+xml");
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
                i++;
                var ra, dec = undefined;
                Object.keys(skyObject).forEach(function(key) {
                    if(key === "data"){
                        skyObject[key].forEach(function(extraData){
                            metadata.push({name:extraData.name, displayName: $wnd.esasky.getColumnDisplayText(extraData.name), datatype:extraData.type, visible: true});
                            if(extraData.type === "DOUBLE" || extraData.type === "REAL" || extraData.type === "INTEGER"){
                                row[extraData.name] = parseFloat(extraData.value);
                                if(isNaN(row[extraData.name])){
                                    row[extraData.name] = undefined;
                                }
                            } else {
                                row[extraData.name] = extraData.value;
                            }
                        });
                        
                    } else if(key !== 'id'){
                        if(key.toLowerCase() === 'ra' || key.toLowerCase() === 'ra_deg'){
                            ra = skyObject[key];
                            if(dec){
                                setRaDec(ra, dec, row, metadata);
                            }
                        } else if(key.toLowerCase() === 'dec' || key.toLowerCase() === 'dec_deg'){
                            dec = skyObject[key];
                            if(ra){
                                setRaDec(ra, dec, row, metadata);
                            }
                        } else {
                            row[key] = skyObject[key];
                            metadata.push({name:key, displayName: $wnd.esasky.getColumnDisplayText(key), datatype:"STRING", visible: true});
                        }
                    }
                });
                data.push(row);
            });
    
            function setRaDec(ra, dec, row, metadata) {
                convertedCoordinate = coordinateConversionFunction(ra, dec);
                row["ra_deg"] = parseFloat(convertedCoordinate[0]);
                row["dec_deg"] = parseFloat(convertedCoordinate[1]);
                metadata.push({name:"ra_deg", displayName: $wnd.esasky.getColumnDisplayText("RA_J2000"), datatype:"DOUBLE", visible: true});
                metadata.push({name:"dec_deg", displayName: $wnd.esasky.getColumnDisplayText("DEC_J2000"), datatype:"DOUBLE", visible: true});
            }
            
            table.metadata = metadata;
            
            return data;
    
        };
        
        table.filterData = [];
		isInitializing = false;
		return table;
	}-*/;

    public void onRowEnter(int rowId) {
        tabulatorCallback.onRowMouseEnter(rowId);
    }

    public void onRowLeave(int rowId) {
        tabulatorCallback.onRowMouseLeave(rowId);
    }

    public void onDataLoaded() {
        if(tableJsObject != null) {
            tabulatorCallback.onDataLoaded(tableJsObject.invokeFunction("getData"));
        }
    }

    public void onDatalinkClicked(final GeneralJavaScriptObject row) {
        tabulatorCallback.onDatalinkClicked(row);
    }

    public void onAccessUrlClicked(String url){
        tabulatorCallback.onAccessUrlClicked(url);
    }
    
    public void onPostcardUrlClicked(final GeneralJavaScriptObject rowData){
        tabulatorCallback.onPostcardUrlClicked(rowData);
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
    
    public String getFilterIcon() {
    	return TableColumnHelper.resources.filterIcon().getSafeUri().asString();
    }

}
