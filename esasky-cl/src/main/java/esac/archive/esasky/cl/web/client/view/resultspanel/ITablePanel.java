package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;

import java.util.List;
import java.util.Map;

public interface ITablePanel {

	public void insertData(String url);
	public void insertData(GeneralJavaScriptObject data);

	public IDescriptor getDescriptor();

	public GeneralEntityInterface getEntity();

	public GeneralJavaScriptObject[] getSelectedRows();

	public GeneralJavaScriptObject[] getAllRows();

	public void clearTable();
	
	public String getEsaSkyUniqID();

	public String getLabel();

	public void selectRow(int rowId);
	
	public void selectRows(int[] rowIds);

	public void deselectRow(int rowId);
	
	public void deselectRows(int[] rowIds);

	public void deselectAllRows();

	public void hoverStartRow(int rowId);

	public void hoverStopRow(int rowId);

	public void selectTablePanel();
	
	public void deselectTablePanel();

	public void closeTablePanel();
	public boolean hasBeenClosed();

	public boolean getIsHidingTable();

	public void registerObserver(TableObserver observer);

	public void unregisterObserver(TableObserver observer);
	
	public JSONObject exportAsJSON();
	public JSONObject exportAsJSON(boolean applyFilters);
	public void exportAsCsv();
	public void exportAsVot();
	public String getFullId();	
	
	public void setEmptyTable(String emptyTableText);
	
	public abstract void showStylePanel(int x, int y);
	
	public void downloadSelected(DDRequestForm ddForm);
	
	public void updateData();
	public void openConfigurationPanel();
	
	public Widget getWidget();
	public void registerClosingObserver(ClosingObserver closingObserver);
	
	public void registerFilterObserver(TableFilterObserver observer);
	public Map<String, String> getTapFilters();
	public String getFilterString();
	public void clearFilters();
	
	public String getVoTableString();
	
	public void setPlaceholderText(String text);

	public void insertHeader(GeneralJavaScriptObject data, String mode);
	public void goToCoordinateOfFirstRow();
	
	public boolean isMOCMode();
	public void setMOCMode(boolean input);
	public void notifyObservers();
	public void disableFilters();
	public void enableFilters();
	
	public boolean isDataProductDatalink();
    public int getNumberOfShownRows();
	void filterOnFoV(String raCol, String decCol);
	
	void setMaxHeight(int height);
	void setVisibleColumns(List<String> columns);

	void showColumn(String field);
	void hideColumn(String field);

	void blockRedraw();
	void restoreRedraw();
	void redrawAndReinitializeHozVDom();
	void addTapFilter(String label, String tapFilter);
}
