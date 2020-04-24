package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.i18n.client.NumberFormat;

import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.model.TableColumnHelper;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.DateFilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.DoubleFilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.FilterDialogBox;


public class TabulatorWrapper{

    public interface TabulatorCallback {
        public void onDataLoaded(GeneralJavaScriptObject javaScriptObject);
        public void onRowSelection(int rowId);
        public void onRowDeselection(int rowId);
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
    }

    private TabulatorCallback tabulatorCallback;
    private GeneralJavaScriptObject tableJsObject;
    private Map<String, FilterDialogBox> filterDialogs = new HashMap<>();

    public TabulatorWrapper(String divId, String url, TabulatorCallback tabulatorCallback, 
            boolean addSendToVOApplicationColumn, boolean addLink2ArchiveColumn, boolean addCentreColumn, boolean addSourcesInPublicationColumn, boolean isHeaderQuery) {
        this.tabulatorCallback = tabulatorCallback;
        tableJsObject = createColumnTabulator(this, divId, url, addSendToVOApplicationColumn, addLink2ArchiveColumn, addCentreColumn, addSourcesInPublicationColumn, isHeaderQuery);
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
        tableJsObject.download("csv", fileName, {bom:true});
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
    
    public void showNumericFilterDialog(String tapName, String filterButtonId, double minVal, double maxVal, final GeneralJavaScriptObject onChangeFunc) {

    	if(!filterDialogs.containsKey(tapName)) {
    		FilterObserver filterObserver = new FilterObserver() {
				
				@Override
				public void onNewFilter(String filter) {
					onChangeFunc.invokeFunction("onChange", filter);
					
				}
			};
    		
    		DoubleFilterDialogBox filterDialog = new DoubleFilterDialogBox(tapName, tapName, filterButtonId, filterObserver);
    		
    		NumberFormat numberFormat = NumberFormat.getFormat("0.##");
    		
    		if((minVal - Math.floor(minVal)) == 0 && (maxVal - Math.floor(maxVal)) == 0) {
    			numberFormat = NumberFormat.getFormat("0");
    		}
    		
    		filterDialog.setRange(minVal, maxVal, numberFormat, 2);
    		filterDialogs.put(tapName, filterDialog);
    	}
    	
    	FilterDialogBox filterDialogBox = filterDialogs.get(tapName);
    	filterDialogBox.show();
    	
    }

    public void showDateFilterDialog(String tapName, String filterButtonId, String minVal, String maxVal, final GeneralJavaScriptObject onChangeFunc) {
    	
    	if(!filterDialogs.containsKey(tapName)) {
    		FilterObserver filterObserver = new FilterObserver() {
    			
    			@Override
    			public void onNewFilter(String filter) {
    				onChangeFunc.invokeFunction("onChange", filter);
    				
    			}
    		};
    		
    		DateFilterDialogBox filterDialog = new DateFilterDialogBox(tapName, tapName, filterButtonId, filterObserver);
    		
    		filterDialog.setStartRange(minVal, maxVal);
    		filterDialogs.put(tapName, filterDialog);
    	}
    	
    	FilterDialogBox filterDialogBox = filterDialogs.get(tapName);
    	filterDialogBox.show();
    	
    }

    public void showListFilterDialog(String tapName, String filterButtonId, String list, final GeneralJavaScriptObject onChangeFunc) {
    	
    	if(!filterDialogs.containsKey(tapName)) {
    		
    		final DropDownMenu<String> dropDownMenu = new DropDownMenu<String>("", "", 125, filterButtonId + "_DropDownMenu");

    		for(String s : list.split(",")) {
    			MenuItem<String> dropdownItem = new MenuItem<String>(s, s, s, true);
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

    private native GeneralJavaScriptObject createColumnTabulator(TabulatorWrapper wrapper, String divId, 
            String url, boolean addSendToVOApplicationColumn, boolean addLink2ArchiveColumn,
            boolean addCentreColumn, boolean addSourcesInPublicationColumn, boolean isHeaderQuery) /*-{
		var visibleTableData = [];
		var visibleTableDataIndex = 0;

		var isInitializing = true;
		var previouslySelectedMap = [];
		var selectionMap = [];
		var metadata;
		var columnDef = [];
		
		var ajaxResponseFunc = {}
		var filterData = [];
		if(isHeaderQuery){
			ajaxResponseFunc = function(url, params, response){
				metadata = response.metadata;
				newMeta = [];
				filterData[0] = {};
				filterData[1] = {};
				filterData[0].id = 0;
				filterData[1].id = 1;
				for(var j = 0; j < metadata.length; j++){
	    			name = metadata[j].name.substring(0,metadata[j].name.length - 4)

	    			if(metadata[j].name.endsWith("_min")){
	    				meta = {name:name, datatype:"DOUBLE"}
	    				newMeta.push(meta)
						filterData[0][name] = parseFloat(response.data[0][j]);
	    			}
	    			else if(metadata[j].name.endsWith("_max")){
						filterData[1][name] = parseFloat(response.data[0][j]);
	    			}
	    			else if(metadata[j].name.endsWith("_lst")){
	    				list = response.data[0][j];
	    				list = list.replace("{","[").replace("}","]");
						filterData[0][name] = list;
						meta = {name:name, datatype:"LIST"}
						newMeta.push(meta)
	    			}
	    			else{
	    				meta = {name:name, datatype:"STRING"}
	    				newMeta.push(meta)
	    			}
	    			
				}

				metadata = newMeta;
		        return [];
		    }
		}else{
			ajaxResponseFunc = function(url, params, response){
				metadata = response.metadata;

				var data = [];
				for(var i = 0; i < response.data.length; i++){
					var row = {};
					row['id'] = i;
					for(var j = 0; j < metadata.length; j++){
		    			if(metadata[j].datatype === "DOUBLE"){
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

		        return data;
		    }
		}
		

		var numericFilterEditor = function(cell, onRendered, success, cancel, editorParams){
			
			var tapName = editorParams["tapName"];
			var filterButtonId = divId + "_" + tapName;

			filterIcon = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getFilterIcon()();

			var filterButton = $wnd.$( "" + "<div id=\'" + filterButtonId
						+ "\' class=\"filterButton defaultEsaSkyButton darkStyle smallButton squaredButton gwt-PushButton-up\" "
						+ "title=\""  + "\""
						+ "\"" + ">" + "<img src=\"" + filterIcon
						+ "\" class=\"fillParent\" />" + "</div>");
			
			var functionObject = {};
			functionObject.onChange = function(filter){
				success(filter);
				onFilterChanged(filter);
			}
				
			filterButton.on("click", function(){
				var minVal = Infinity;
				var maxVal = -Infinity;
				
				if(filterData.length > 0){
					name = cell.getColumn()._column.definition.field;
					minVal = filterData[0][name];
					maxVal = filterData[1][name];
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
				
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::showNumericFilterDialog(Ljava/lang/String;Ljava/lang/String;DDLesac/archive/esasky/cl/web/client/view/resultspanel/GeneralJavaScriptObject;)
					(tapName, filterButtonId, minVal, maxVal, functionObject);
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
			
			var tapName = editorParams["tapName"];
			var filterButtonId = divId + "_" + tapName;

			filterIcon = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getFilterIcon()();

			var filterButton = $wnd.$( "" + "<div id=\'" + filterButtonId
						+ "\' class=\"filterButton defaultEsaSkyButton darkStyle smallButton squaredButton gwt-PushButton-up\" "
						+ "title=\""  + "\""
						+ "\"" + ">" + "<img src=\"" + filterIcon
						+ "\" class=\"fillParent\" />" + "</div>");
			
			var functionObject = {};
			functionObject.onChange = function(filter){
				success(filter);
				onFilterChanged(filter);
			}
				
			filterButton.on("click", function(){
				var minVal = "2100-01-01";
				var maxVal = "1800-01-01";
				
				if(filterData.length > 0){
					name = cell.getColumn()._column.definition.field;
					minVal = filterData[0][name];
					maxVal = filterData[1][name];
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
				
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::showDateFilterDialog(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lesac/archive/esasky/cl/web/client/view/resultspanel/GeneralJavaScriptObject;)
					(tapName, filterButtonId, minVal, maxVal, functionObject);
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
			
			var tapName = editorParams["tapName"];
			var filterButtonId = divId + "_" + tapName;

			filterIcon = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::getFilterIcon()();

			var filterButton = $wnd.$( "" + "<div id=\'" + filterButtonId
						+ "\' class=\"filterButton defaultEsaSkyButton darkStyle smallButton squaredButton gwt-PushButton-up\" "
						+ "title=\""  + "\""
						+ "\"" + ">" + "<img src=\"" + filterIcon
						+ "\" class=\"fillParent\" />" + "</div>");
			
			var functionObject = {};
			functionObject.onChange = function(filter){
				success(filter);
				onFilterChanged(filter);
			}
				
			filterButton.on("click", function(){
				if(filterData != []){
					name = cell.getColumn()._column.definition.field;
					list = filterData[0][name];
					wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::showListFilterDialog(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lesac/archive/esasky/cl/web/client/view/resultspanel/GeneralJavaScriptObject;)
						(tapName, filterButtonId, list, functionObject);
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
			
			decimals = 4;

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
		

		var table = new $wnd.Tabulator("#" + divId, {
		 	height:"100%", // set height of table (in CSS or here), this enables the Virtual DOM and improves render speed dramatically (can be any valid css height value)
		 	ajaxURL:url,
		 	placeholder:"",
		    ajaxResponse:ajaxResponseFunc,
		    dataFiltered:function(filters, rows){
		    	var returnString = "";
		    	for(i = 0; i < rows.length; i++){
		    		returnString += rows[i].getIndex() + ",";
		    	}

		  		wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onDataFiltered(Ljava/lang/String;)(returnString);
		    },
		    dataLoaded:function(data){
		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onDataLoaded()();
		    },
		    dataLoading:function(data){
		        var activeColumnGroup = [];
		    	activeColumnGroup.push({formatter:"rowSelection", titleFormatter:"rowSelection"});

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
                        formatter:imageButtonFormatter, width:40, align:"center", formatterParams:{image:"recenter.png", 
                            tooltip:$wnd.esasky.getInternationalizationText("tabulator_centreOnShape")},
                            cellClick:function(e, cell){
                                e.stopPropagation();
                		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onCenterClicked(Lesac/archive/esasky/cl/web/client/view/resultspanel/GeneralJavaScriptObject;)(cell.getData());
                            }
                    });
                }
                if(addSendToVOApplicationColumn){
                    activeColumnGroup.push({
                        title:$wnd.esasky.getInternationalizationText("tabulator_sendToVOApplicationHeader"),
                        headerSort:false, 
                        headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_sendRowToVOApplicationHeaderTooltip"),
                        minWidth: 50,
                        formatter:imageButtonFormatter, width:40, align:"center", formatterParams:{image:"send_small.png", 
                            tooltip:$wnd.esasky.getInternationalizationText("tabulator_sendRowToVOA")},
                            cellClick:function(e, cell){
                                e.stopPropagation();
                		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onSendToVoApplicaitionClicked(Lesac/archive/esasky/cl/web/client/view/resultspanel/GeneralJavaScriptObject;)(cell.getData());
                            }
                    });
                }
                
                if(addLink2ArchiveColumn){
                    activeColumnGroup.push({
                        title:$wnd.esasky.getInternationalizationText("tabulator_link2ArchiveHeader"),
                        headerSort:false, 
                        headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_link2ArchiveHeaderTooltip"),
                        minWidth: 62,
                        formatter:imageButtonFormatter, width:40, align:"center", formatterParams:{image:"link2archive.png",
                            tooltip:$wnd.esasky.getInternationalizationText("tabulator_link2ArchiveButtonTooltip")},
                            cellClick:function(e, cell){
                                e.stopPropagation();
                		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onLink2ArchiveClicked(Lesac/archive/esasky/cl/web/client/view/resultspanel/GeneralJavaScriptObject;)(cell.getData());
                            }
                    });
                }
                if(addSourcesInPublicationColumn){
                    activeColumnGroup.push({
                        title:$wnd.esasky.getInternationalizationText("tabulator_SourcesInPublicationHeader"),
                        headerSort:false, 
                        headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_SourcesInPublicationHeaderTooltip"),
                        minWidth: 65,
                        formatter:imageButtonFormatter, width:40, align:"center", formatterParams:{image:"target_list.png",
                            tooltip:$wnd.esasky.getInternationalizationText("tabulator_SourcesInPublication")},
                            cellClick:function(e, cell){
                                e.stopPropagation();
                		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onSourcesInPublicationClicked(Lesac/archive/esasky/cl/web/client/view/resultspanel/GeneralJavaScriptObject;)(cell.getData());
                            }
                    });
                }


		    	for(var i = 0; i < metadata.length; i++){
		    		if(metadata[i].name.toLowerCase() === "access_url"){
                        activeColumnGroup.push({
                            title:metadata[i].name,
                            field:metadata[i].name,
                            headerSort:false, 
                            headerTooltip:metadata[i].description,
                            minWidth: 85,
                            formatter:imageButtonFormatter, width:40, align:"center", formatterParams:{image:"download_small.png", 
                                tooltip:$wnd.esasky.getInternationalizationText("tabulator_download")}, 
                                cellClick:function(e, cell){
                                    e.stopPropagation();
                                    if(cell.getData().access_format && cell.getData().access_format.toLowerCase().includes("datalink")){
                        		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onDatalinkClicked(Lesac/archive/esasky/cl/web/client/view/resultspanel/GeneralJavaScriptObject;)(cell.getRow());
                                    } else {
                        		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onAccessUrlClicked(Ljava/lang/String;)(cell.getData().access_url);
                                    }

                                }
                        });
                        continue;
		    		}
		    		if(metadata[i].name.toLowerCase() === "postcard_url"){
                        activeColumnGroup.push({
                            title:$wnd.esasky.getInternationalizationText("tabulator_preview"),
                            field:metadata[i].name,
                            headerSort:false, 
                            headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_previewHeaderTooltip"),
                            minWidth: 50,
                            formatter:imageButtonFormatter, width:40, align:"center", formatterParams:{image:"preview.png", 
                                tooltip:$wnd.esasky.getInternationalizationText("tabulator_preview")}, 
                                cellClick:function(e, cell){
                                    e.stopPropagation();
                    		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onPostcardUrlClicked(Lesac/archive/esasky/cl/web/client/view/resultspanel/GeneralJavaScriptObject;)(cell.getRow());
                                }
                        });
                        continue;
		    		}
		    		if(metadata[i].name.toLowerCase() === "author"){
                        activeColumnGroup.push({
                            title:metadata[i].name,
                            field:metadata[i].name,
    		    			sorter: "string",
    		    			headerFilter:true,
    		    			headerFilterFunc:"like",
    		    			headerFilterFuncParams:{tapName:metadata[i].name},
                            headerTooltip:$wnd.esasky.getInternationalizationText("tabulator_authorHeaderTooltip"),
                            formatter:linkListFormatter});
                        continue;
                    }
		    		if(metadata[i].name.toLowerCase() === "sso_name"){
		    		    columnDef.push(activeColumnGroup[0]); //Selection column
		    		    columnDef.push({title: $wnd.esasky.getInternationalizationText("tableGroup_Observation"), columns:activeColumnGroup.slice(1)});
		    		    activeColumnGroup = [];
		    		    columnDef.push({title: @esac.archive.esasky.cl.web.client.status.GUISessionStatus::getTrackedSsoName()(), columns:activeColumnGroup});
		    		}
		    		
		    		if(metadata[i].datatype === "DOUBLE"){
		    			activeColumnGroup.push({
			    			title:metadata[i].name,
			    			field:metadata[i].name, 
			    			headerTooltip:metadata[i].description,
			    			formatter:doubleFormatter,
			    			sorter: "number",
			    			headerFilter:numericFilterEditor,
			    			headerFilterParams:{tapName:metadata[i].name},
			    			headerFilterFunc:DoubleFilter,
			    			headerFilterFuncParams:{tapName:metadata[i].name}
	    				});
		    		}
		    		else if(metadata[i].datatype === "TIMESTAMP"){
		    			activeColumnGroup.push({
			    			title:metadata[i].name,
			    			field:metadata[i].name, 
			    			headerTooltip:metadata[i].description,
			    			formatter:doubleFormatter,
			    			sorter: "string",
			    			formatter: "plaintext",
			    			headerFilter:dateFilterEditor,
			    			headerFilterParams:{tapName:metadata[i].name},
			    			headerFilterFunc:DateFilter,
			    			headerFilterFuncParams:{tapName:metadata[i].name}
	    				});
		    		}
		    		else if(metadata[i].datatype === "INTEGER"){
		    			activeColumnGroup.push({
			    			title:metadata[i].name,
			    			field:metadata[i].name, 
			    			headerTooltip:metadata[i].description,
			    			formatter:doubleFormatter,
			    			sorter: "number",
			    			formatter:"plaintext",
			    			headerFilter:numericFilterEditor,
			    			headerFilterParams:{tapName:metadata[i].name},
			    			headerFilterFunc:DoubleFilter,
			    			headerFilterFuncParams:{tapName:metadata[i].name}
	    				});
		    		}
		    		else if(metadata[i].datatype === "LIST"){
		    			activeColumnGroup.push({
			    			title:metadata[i].name,
			    			field:metadata[i].name, 
			    			headerTooltip:metadata[i].description,
			    			formatter:doubleFormatter,
			    			sorter: "number",
			    			formatter:"plaintext",
			    			headerFilter:listFilterEditor,
			    			headerFilterParams:{tapName:metadata[i].name},
			    			headerFilterFunc:"like",
	    				});
		    		}else{
			    		activeColumnGroup.push({
			    			title:metadata[i].name,
			    			field:metadata[i].name, 
			    			headerTooltip:metadata[i].description,
			    			formatter:"plaintext",
			    			sorter:  "string",
			    			headerFilter:true,
			    			headerFilterParams:{tapName:metadata[i].name},
			    			headerFilterFunc:"like",
			    			headerFilterFuncParams:{tapName:metadata[i].name}
		    			});
		    		}
	    		}
		    	if(columnDef.length == 0){
		    	    columnDef = activeColumnGroup;
		    	}
		    	
		    	table.setColumns(columnDef);
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
			    		wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onRowSelection(I)(item.getIndex());
		    		}
		    	});
		    	Object.keys(previouslySelectedMap).forEach(function(item, index, array){
		    		if(!selectionMap[item]){
			    		wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onRowDeselection(I)(item);
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



		table.getVoTableString = function(data, resourceName){
			// Add VOT XML Schema
			var votData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                votData += "<VOTABLE version=\"1.3\" xmlns=\"//www.ivoa.net/xml/VOTable/v1.3\">\n";
				votData += "<RESOURCE name=\"" + resourceName + "\">\n";
				votData += "<TABLE>\n";

			// Adds headers to xml
			metadata.forEach(function (columnInfo) {
				votData += "<FIELD";
				Object.keys(columnInfo).forEach(function (key) {
					if(columnInfo[key] !== null) {
						votData += " " + key + "=\"" + columnInfo[key] + "\"";
					}

				});
				votData += "/>\n";
			});//TODO TEST with Publications

			// Adds data to xml
			votData += "<DATA>\n";
			votData += "<TABLEDATA>\n";

			data.forEach(function (row) {
				votData += "<TR>\n";
				metadata.forEach(function (columnInfo) {
					var value = row[columnInfo.name] || "";
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
        tabulatorCallback.onDataLoaded(tableJsObject.invokeFunction("getData"));
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

    public void onRowSelection(int rowId) {
        tabulatorCallback.onRowSelection(rowId);
    }

    public void onRowDeselection(int rowId) {
        tabulatorCallback.onRowDeselection(rowId);
    }

    public void onFilterChanged(String label, String filter) {
        tabulatorCallback.onFilterChanged(label, filter);
    }
    
    public String getFilterIcon() {
    	return TableColumnHelper.resources.filterIcon().getSafeUri().asString();
    }

}
