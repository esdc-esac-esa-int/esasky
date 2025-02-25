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

import com.allen_sauer.gwt.log.client.Log;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

import java.util.List;

public class DefaultTabulatorCallback implements TabulatorCallback{

	@Override
	public void onDataLoaded(GeneralJavaScriptObject rowData, GeneralJavaScriptObject metadata) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onTableHeightChanged() {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onRowSelection(GeneralJavaScriptObject row) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onRowDeselection(GeneralJavaScriptObject row) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onRowMouseEnter(int rowId) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onRowMouseLeave(int rowId) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onFilterChanged(String label, String filter) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onDataFiltered(List<Integer> filteredRows) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onDatalinkClicked(GeneralJavaScriptObject javaScriptObject, String url) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onAccessUrlClicked(String url) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onPostcardUrlClicked(GeneralJavaScriptObject rowData, String columnName) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onCenterClicked(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onSendToVoApplicaitionClicked(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onLink2ArchiveClicked(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onLink2ArchiveClicked(GeneralJavaScriptObject rowData, String columnName) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public boolean isDatalinkActive(GeneralJavaScriptObject rowData) {
		return false;
	}

	@Override
	public boolean isDatalinkActive(String url) {
		return false;
	}

	@Override
	public void onSourcesInPublicationClicked(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onAddHipsClicked(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onAjaxResponse() {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onAjaxResponseError(String error) {
		Log.error(error);
	}

	@Override
	public String getLabelFromTapName(String tapName) {
		return tapName;
	}

	@Override
	public GeneralJavaScriptObject getDescriptorMetaData() {
		return null;
	}

	@Override
	public CommonTapDescriptor getDescriptor() {
		return null;
	}

	@Override
	public String getRaColumnName() {
		return null;
	}

	@Override
	public String getDecColumnName() {
		return null;
	}

	public String getUniqueIdentifierField() {
		return null;
	}

	@Override
	public boolean isColumnVisible(String column) {
		return false;
	}

	@Override
	public String getColumnUnit(String columnName) {
		return null;
	}

	@Override
	public boolean isMOCMode() {
		return false;
	}

	@Override
	public String getEsaSkyUniqId() {
		return null;
	}

	@Override
	public void multiSelectionInProgress() {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void multiSelectionFinished() {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public boolean hasBeenClosed() {
		return false;
	}

	@Override
	public void onAdqlButtonPressed(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onMetadataButtonPressed(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onAddObscoreTableClicked(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onOpenTableClicked(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onDeleteRowClicked(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onEditRowClicked(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onAddRowClicked(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onRowClicked(GeneralJavaScriptObject rowData) {
		//Do nothing by default - To be overridden if needed.
	}

	@Override
	public void onCreateRowClicked() {
		//Do nothing by default - To be overridden if needed.
	}
	
	@Override
	public void onAddTimeSeriesClicked(GeneralJavaScriptObject row) {
		//Do nothing by default - To be overridden if needed.
	}

	public boolean isRowVisibleInTimeSeriesViewer(GeneralJavaScriptObject row) {
		return false;
	}
}