package esac.archive.esasky.cl.web.client.view.resultspanel.tabulator;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

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
	public void onDatalinkClicked(GeneralJavaScriptObject javaScriptObject) {
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
	public void onCenterClicked(String ra, String dec) {
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
	public String getRaColumnName() {
		return null;
	}

	@Override
	public String getDecColumnName() {
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
}