package esac.archive.esasky.cl.web.client.view.resultspanel;


public class TabulatorWrapper{

	public interface TabulatorCallback {
		public void onDataLoaded(GeneralJavaScriptObject javaScriptObject);
		public void onRowSelection(int rowId);
		public void onRowDeselection(int rowId);
		public void onRowMouseEnter(int rowId);
		public void onRowMouseLeave(int rowId);
		public void onFilterChanged(String label, String filter);
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

	private native GeneralJavaScriptObject createColumnTabulator(TabulatorWrapper wrapper, String divId, String url) /*-{
		var visibleTableData = [];
		var visibleTableDataIndex = 0;

		var isInitializing = true;
		var previouslySelectedMap = [];
		var selectionMap = [];
		var columnDef;
		var refinedColumnDef = [];
		function DoubleFilter(headerValue, rowValue, rowData, filterParams){
		    //headerValue - the value of the header filter element
		    //rowValue - the value of the column in this row
		    //rowData - the data for the row being filtered
		    //filterParams - params object passed to the headerFilterFuncParams property
			
			var split = headerValue.split(",");
		
			if(split.length == 2){
				var filter = filterParams.tapName + " BETWEEN  " + split[0] + " AND " + split[1]; 
				wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onFilterChanged(Ljava/lang/String;Ljava/lang/String;)(filterParams.tapName, filter);
		    	return rowValue >= parseFloat(split[0]) && rowValue <= parseFloat(split[1]); //must return a boolean, true if it passes the filter.
			}
			
			wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onFilterChanged(Ljava/lang/String;Ljava/lang/String;)(filterParams.tapName, "");
			return true;
		}
		
		var doubleFilterEmptyCheck = function(value){
			if(value.length == 0){
				
			}
		}
		
		
		var table = new $wnd.Tabulator("#" + divId, {
		 	height:"100%", // set height of table (in CSS or here), this enables the Virtual DOM and improves render speed dramatically (can be any valid css height value)
		 	ajaxURL:url,
		    ajaxResponse:function(url, params, response){
				columnDef = response.metadata;
				
				var data = [];
				for(var i = 0; i < response.data.length; i++){
					var row = {};
					row['id'] = i;
					for(var j = 0; j < columnDef.length; j++){
		    			if(columnDef[j].datatype === "DOUBLE"){
							row[columnDef[j].name] = parseFloat(response.data[i][j]);
			    			if(isNaN(row[columnDef[j].name])){
								row[columnDef[j].name] = undefined;
			    			}
		    			} else {
							row[columnDef[j].name] = response.data[i][j];
		    			}
					}
					data[i] = row;
				}		
				
		        return data;
		    },
		    dataLoaded:function(data){
		    	wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onDataLoaded()();
		    },
		    dataLoading:function(data){

		    	
		    	refinedColumnDef[0] = {formatter:"rowSelection", titleFormatter:"rowSelection"};
		    	for(var i = 0; i < columnDef.length; i++){
		    		var sorter = "string";
		    		if(columnDef[i].datatype === "DOUBLE"){
		    			sorter = "number";
		    		}
		    		refinedColumnDef[i + 1] = {title:columnDef[i].name, 
		    			field:columnDef[i].name,
		    			headerTooltip:columnDef[i].description,
		    			sorter: sorter,
		    			headerFilter: true,
		    			headerFilterFunc:DoubleFilter,
		    			headerFilterFuncParams:{tapName:columnDef[i].name},
		    			headerFilterEmptyCheck:function(value){
		    					//We don't get a filterchange unless this returns false which means we don't remove the filter string in the observsers
		    					//So this will always return negative for now until we can find a better solution
								if(value.length == 0){
									//wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper::onFilterChanged(Ljava/lang/String;Ljava/lang/String;)(this.field, "");
									return false;
								}
								return false;
							}
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

		isInitializing = false;
		return table;
	}-*/;

}
