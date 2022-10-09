package esac.archive.esasky.cl.web.client.view.resultspanel.tabulator;

import java.util.List;

import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public interface TabulatorCallback {
    public void onDataLoaded(GeneralJavaScriptObject rowData, GeneralJavaScriptObject metadata);
    public void onTableHeightChanged();
    public void onRowSelection(GeneralJavaScriptObject row);
    public void onRowDeselection(GeneralJavaScriptObject row);
    public void onRowMouseEnter(int rowId);
    public void onRowMouseLeave(int rowId);
    public void onFilterChanged(String label, String filter);
    public void onDataFiltered(List<Integer> filteredRows);
    public void onDatalinkClicked(GeneralJavaScriptObject javaScriptObject);
    public void onAccessUrlClicked(String url);
    public void onPostcardUrlClicked(GeneralJavaScriptObject rowData, String columnName);
    public void onCenterClicked(GeneralJavaScriptObject rowData);
    public void onCenterClicked(String ra, String dec);
    public void onSendToVoApplicaitionClicked(GeneralJavaScriptObject rowData);
    public void onLink2ArchiveClicked(GeneralJavaScriptObject rowData);
    public void onSourcesInPublicationClicked(GeneralJavaScriptObject rowData);
    public void onAddHipsClicked(GeneralJavaScriptObject rowData);
    public void onAjaxResponse();
    public void onAjaxResponseError(String error);
    public String getLabelFromTapName(String tapName);
    public GeneralJavaScriptObject getDescriptorMetaData();
    public String getRaColumnName();
    public String getDecColumnName();
    public String getUniqueIdentifierField();
    public boolean isMOCMode();
    public String getEsaSkyUniqId();
    public void multiSelectionInProgress();
    public void multiSelectionFinished();
    public boolean hasBeenClosed();
    public void onAdqlButtonPressed(GeneralJavaScriptObject rowData);
    public void onMetadataButtonPressed(GeneralJavaScriptObject rowData);

}