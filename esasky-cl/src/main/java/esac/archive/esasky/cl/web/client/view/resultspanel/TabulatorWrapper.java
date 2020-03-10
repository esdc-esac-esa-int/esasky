package esac.archive.esasky.cl.web.client.view.resultspanel;

public class TabulatorWrapper{

	public interface TabulatorCallback {
		public void onDataLoaded(GeneralJavaScriptObject javaScriptObject);
		public void onRowSelection(int rowId);
		public void onRowDeselection(int rowId);
		public void onRowMouseEnter(int rowId);
		public void onRowMouseLeave(int rowId);
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

	private native GeneralJavaScriptObject createColumnTabulator(TabulatorWrapper wrapper, String divId, String url) /*-{
		var visibleTableData = [];
		var visibleTableDataIndex = 0;

		var isInitializing = true;
		var previouslySelectedMap = [];
		var selectionMap = [];
		var columnDef;
		var refinedColumnDef = [];
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
		    			headerFilter: true
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
