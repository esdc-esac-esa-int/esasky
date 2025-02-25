/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.cl.web.client.view.resultspanel.tabulator;

import java.util.List;

import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

public interface TabulatorCallback {
    void onDataLoaded(GeneralJavaScriptObject rowData, GeneralJavaScriptObject metadata);
    void onTableHeightChanged();
    void onRowSelection(GeneralJavaScriptObject row);
    void onRowDeselection(GeneralJavaScriptObject row);
    void onRowMouseEnter(int rowId);
    void onRowMouseLeave(int rowId);
    void onFilterChanged(String label, String filter);
    void onDataFiltered(List<Integer> filteredRows);
    void onDatalinkClicked(GeneralJavaScriptObject javaScriptObject, String url);
    void onAccessUrlClicked(String url);
    void onPostcardUrlClicked(GeneralJavaScriptObject rowData, String columnName);
    void onCenterClicked(GeneralJavaScriptObject rowData);
    void onSendToVoApplicaitionClicked(GeneralJavaScriptObject rowData);
    void onLink2ArchiveClicked(GeneralJavaScriptObject row);
    void onLink2ArchiveClicked(GeneralJavaScriptObject row, String columnName);
    boolean isDatalinkActive(GeneralJavaScriptObject row);
    boolean isDatalinkActive(String url);
    void onSourcesInPublicationClicked(GeneralJavaScriptObject rowData);
    void onAddHipsClicked(GeneralJavaScriptObject rowData);
    void onAjaxResponse();
    void onAjaxResponseError(String error);
    String getLabelFromTapName(String tapName);
    GeneralJavaScriptObject getDescriptorMetaData();
    CommonTapDescriptor getDescriptor();
    String getRaColumnName();
    String getDecColumnName();
    String getUniqueIdentifierField();
    boolean isColumnVisible(String column);
    String getColumnUnit(String columnName);
    boolean isMOCMode();
    String getEsaSkyUniqId();
    void multiSelectionInProgress();
    void multiSelectionFinished();
    boolean hasBeenClosed();
    void onAdqlButtonPressed(GeneralJavaScriptObject rowData);
    void onMetadataButtonPressed(GeneralJavaScriptObject rowData);
    void onAddObscoreTableClicked(GeneralJavaScriptObject rowData);
    void onOpenTableClicked(GeneralJavaScriptObject rowData);
    void onDeleteRowClicked(GeneralJavaScriptObject rowData);
    void onEditRowClicked(GeneralJavaScriptObject rowData);
    void onAddRowClicked(GeneralJavaScriptObject rowData);
    void onRowClicked(GeneralJavaScriptObject rowData);
    void onCreateRowClicked();
	void onAddTimeSeriesClicked(GeneralJavaScriptObject rowData);
    boolean isRowVisibleInTimeSeriesViewer(GeneralJavaScriptObject rowData);
}