/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ITapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptor;

import java.util.List;
import java.util.Map;

public interface ITablePanel {

	void insertData(String url);
	void insertData(GeneralJavaScriptObject data);
	public void insertExternalTapData(GeneralJavaScriptObject data);

	public CommonTapDescriptor getDescriptor();

	GeneralEntityInterface getEntity();

	GeneralJavaScriptObject[] getSelectedRows();

	GeneralJavaScriptObject[] getAllRows();

	void clearTable();
	
	String getEsaSkyUniqID();

	String getLabel();

	void selectRow(int rowId);
	void selectRow(int rowId, boolean delay);
	
	void selectRows(int[] rowIds);

	void deselectRow(int rowId);
	
	void deselectRows(int[] rowIds);

	void deselectAllRows();

	void hoverStartRow(int rowId);

	void hoverStopRow(int rowId);

	void selectTablePanel();
	
	void deselectTablePanel();

	void closeTablePanel();
	boolean hasBeenClosed();

	boolean getIsHidingTable();

	void registerObserver(TableObserver observer);

	void unregisterObserver(TableObserver observer);
	
	JSONObject exportAsJSON();
	JSONObject exportAsJSON(boolean applyFilters);
	void exportAsCsv();
	void exportAsVot();
	String getFullId();
	
	void setEmptyTable(String emptyTableText);
	
	void showStylePanel(int x, int y);
	
	void downloadSelected(DDRequestForm ddForm);
	
	void updateData();
	void openConfigurationPanel();
	
	Widget getWidget();
	void registerClosingObserver(ClosingObserver closingObserver);
	
	void registerFilterObserver(TableFilterObserver observer);
	Map<String, String> getTapFilters();
	String getFilterString();
	void clearFilters();
	
	String getVoTableString();
	
	void setPlaceholderText(String text);

	void insertHeader(GeneralJavaScriptObject data, String mode);
	void goToCoordinateOfFirstRow();
	
	boolean isMOCMode();
	void setMOCMode(boolean input);
	void notifyObservers();
	void disableFilters();
	void enableFilters();
	
	boolean isDataProductDatalink();
    int getNumberOfShownRows();
	void filterOnFoV(String raCol, String decCol);

	void groupOnColumn(String columnName);

	void setMaxHeight(int height);
	void setVisibleColumns(List<String> columns);

	int getVisibleColumnsWidth();

	void showColumn(String field);
	void hideColumn(String field);

	void blockRedraw();
	void restoreRedraw();
	void redrawAndReinitializeHozVDom();
	void addTapFilter(String label, String tapFilter);
	GeneralJavaScriptObject getTableMetadata();
	void openQueryPanel();
}
