package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowHoverEvent;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel.IPreviewClickedHandler;

public interface ITablePanel {

	public void toggleVisibilityOfFreeFlowingElements();

	public void setPreviewClickedHandler(IPreviewClickedHandler previewClickedHandler);

	public void exposeOpenFilterBoxMethodToJs(ITablePanel tab);

	public void insertData(List<TableRow> data, String url);

	public void createSortableColumn(List<TableRow> list, Column<TableRow, ?> col, final int colIdx);

	public IDescriptor getDescriptor();

	public GeneralEntityInterface getEntity();

	public Set<TableRow> getSelectedRows();

	public List<TableRow> getFilteredRows();

	public void clearSelectionModel();

	public void clearTable();
	
	public void refreshHeight();
	public String getEsaSkyUniqID();

	public String getLabel();

	public String getADQLQueryUrl();

	public void setADQLQueryUrl(final String inputADQLQueryUrl);

	public String getADQLQueryForChosenRows();

	public void selectRow(int rowId);

	public void deselectRow(int rowId);

	public void hoverStartRow(int rowId);

	public void hoverStopRow(int rowId);

	public void hoverStartEntity(RowHoverEvent hoverEvent);
	public void hoverStopEntity(RowHoverEvent hoverEvent);


	public void openFilterBox(int columnNumber);
	public void selectTablePanel();
	
	public void deselectTablePanel();

	public void closeTablePanel();
	public boolean hasBeenClosed();

	public void removeData();

	public boolean getIsHidingTable();

	public void registerObserver(AbstractTableObserver observer);

	public void unregisterObserver(AbstractTableObserver observer);
	
	public String getUnfilteredRow(int rowIndex);	
	public JSONObject exportAsJSON();
	public void exportAsCsv();
	public void exportAsVot();
	public String getFullId();	
	
	public void setEmptyTable(String emptyTableText);
	
    public void resizeColumnGroupHeader();    
	public void setSeparator(int index);
	
	public void activateGroupHeaders();	
	public abstract void showStylePanel(int x, int y);
	
	public void downloadSelected(DDRequestForm ddForm);
	
	public void updateData();
	
	public Widget getWidget();
	public void registerClosingObserver(ClosingObserver closingObserver);
	
	public void registerFilterObserver(AbstractTableFilterObserver observer);
	public Map<String, String> getTapFilters();
	
	public String getVoTableString();
}
