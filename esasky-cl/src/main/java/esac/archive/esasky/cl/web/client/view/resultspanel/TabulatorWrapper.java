package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.LinkedList;
import java.util.List;

public class TabulatorWrapper{

	public interface TabulatorCallback {
		public void onDataLoaded(GeneralJavaScriptObject javaScriptObject);
		public void onRowSelection(int rowId);
		public void onRowDeselection(int rowId);
		public void onRowMouseEnter(int rowId);
		public void onRowMouseLeave(int rowId);
		public void onFilterChanged(String label, String filter);
		public void onDataFiltered(List<Integer> filteredRows);
	}

	private TabulatorCallback tabulatorCallback;
	private GeneralJavaScriptObject tableJsObject;

	public TabulatorWrapper(String divId, String url, TabulatorCallback tabulatorCallback) {
		this.tabulatorCallback = tabulatorCallback;
		tableJsObject = createColumnTabulator(this, divId, url);
	}

	public void selectRow(int rowId) {
		GeneralJavaScriptObject row = tableJsObject.invokeFunction("getRow", "" + rowId);
		row.invokeFunction("select", null);
		row.invokeFunction("scrollTo", null);
	}

	public void deselectRow(int rowId) {
		GeneralJavaScriptObject row = tableJsObject.invokeFunction("getRow", "" + rowId);
		row.invokeFunction("deselect", null);
	}

	public void hoverStart(int rowId) {
		GeneralJavaScriptObject element = tableJsObject.invokeFunction("getRow", "" + rowId).invokeFunction("getElement", null).getProperty("style");
		element.setProperty("background-color", "rgba(255, 255, 255, 0.15)");
	}

	public void hoverStop(int rowId) {
		GeneralJavaScriptObject element = tableJsObject.invokeFunction("getRow", "" + rowId).invokeFunction("getElement", null).getProperty("style");
		element.setProperty("background-color", "");
	}

	public void onRowEnter(int rowId) {
		tabulatorCallback.onRowMouseEnter(rowId);
	}

	public void onRowLeave(int rowId) {
		tabulatorCallback.onRowMouseLeave(rowId);
	}

	public void onDataLoaded() {
		tabulatorCallback.onDataLoaded(tableJsObject);
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
        

	public void onDataFiltered(String indexes) {
		List<Integer> indexArray = new LinkedList<Integer>();
		for(String s : indexes.split(",")) {
			if(s.length() > 0) {
				indexArray.add(Integer.parseInt(s));
			}
		}
		tabulatorCallback.onDataFiltered(indexArray);
	}

	private native GeneralJavaScriptObject createColumnTabulator(TabulatorWrapper wrapper, String divId, String url) /*-{
		var visibleTableData = [];
		var visibleTableDataIndex = 0;

		var isInitializing = true;
		var previouslySelectedMap = [];
		var selectionMap = [];
		var metadata;
		var refinedColumnDef = [];
		
		//custom header filter
		var doubleFilterEditor = function(cell, onRendered, success, cancel, editorParams){
		
			 var container = $wnd.$("<span></span>")
			//create and style input
			var start = $wnd.$("<input type='double' placeholder='Start'/>");
			var end = $wnd.$("<input type='double' placeholder='End'/>");
		
			container.append(start).append(end);
		
			var inputs = $wnd.$("input", container);
		
		
			inputs.css({
				"padding":"4px",
				"width":"50%",
				"box-sizing":"border-box",
			})
			.val(cell.getValue());
		
			function buildFilterString(){
				//Adding this value to the cointainer since tabulator has timer which checks the value of the element after 300ms
				container[0].value = start.val() +',' + end.val();
				return start.val() +',' + end.val();
			}
			
			function onFilterChanged(){
				var filter = "";
				if(start.val().length > 0 ){
					filter += cell.getField() + " >=  " + start.val()
				}
				if(end.val().length > 0 ){
					if(filter.length > 0){
						filter += " AND ";
					}
					filter += cell.getField() + " <=  " + end.val();
				}
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onFilterChanged(Ljava/lang/String;Ljava/lang/String;)(cell.getField(), filter);
			}
		
			//submit new value on blur
			inputs.on("change blur", function(e){
				success(buildFilterString());
				onFilterChanged();
			});
		
			//submit new value on enter
			inputs.on("keydown", function(e){
				if(e.keyCode == 13){
					success(buildFilterString());
					onFilterChanged();
				}
		
				if(e.keyCode == 27){
					cancel();
				}
			});

			return container[0];
		
		}
				
		
		function DoubleFilter(headerValue, rowValue, rowData, filterParams){
		    //headerValue - the value of the header filter element
		    //rowValue - the value of the column in this row
		    //rowData - the data for the row being filtered
		    //filterParams - params object passed to the headerFilterFuncParams property
			
			var split = headerValue.split(",");
		
			if(split.length == 2){
				var startTrue = true;
				var endTrue = true;
				if(split[0].length > 0 && rowValue < parseFloat(split[0]) ){
					startTrue = false;
				}
				if(split[1].length > 0 && rowValue > parseFloat(split[1]) ){
					
					endTrue = false;
				}
				console.log(startTrue && endTrue)
		    	return startTrue && endTrue; //must return a boolean, true if it passes the filter.
			}
			return true;
		}
		
		function DoubleFilterEmpty(value){
			if(value.length > 0){
				return false;
			}
			return true;
		}
		
		var table = new $wnd.Tabulator("#" + divId, {
		 	height:"100%", // set height of table (in CSS or here), this enables the Virtual DOM and improves render speed dramatically (can be any valid css height value)
		 	ajaxURL:url,
		    ajaxResponse:function(url, params, response){
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
		    },
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
		    	refinedColumnDef[0] = {formatter:"rowSelection", titleFormatter:"rowSelection"};
		    	for(var i = 0; i < metadata.length; i++){
		    		var sorter = "string";
		    		var headerFilter = true;
		    		var headerFilterFunc = "like";
		    		if(metadata[i].datatype === "DOUBLE"){
		    			sorter = "number";
		    			headerFilter = doubleFilterEditor;
		    			headerFilterFunc = DoubleFilter;
		    			
		    		}
		    		refinedColumnDef[i + 1] = {
		    			title:metadata[i].name,
		    			field:metadata[i].name, 
		    			headerTooltip:metadata[i].name,
		    			headerTooltip:metadata[i].description,
		    			sorter: sorter,
		    			headerFilter:headerFilter,
		    			headerFilterFunc:headerFilterFunc,
		    			headerFilterFuncParams:{tapName:metadata[i].name}
//		    			headerFilterEmptyCheck:headerFilterEmptyCheck
		    			};
		    	}

		    	table.setColumns(refinedColumnDef);
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
		 	autoColumns: true
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

}
