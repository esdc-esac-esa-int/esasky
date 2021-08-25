package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.AutoHidingMovablePanel;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper.TabulatorCallback;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;

public class ToggleColumnsDialogBox extends AutoHidingMovablePanel implements TabulatorCallback{
	private final Resources resources = GWT.create(Resources.class);
	private CssResource style;

	public interface Resources extends ClientBundle {
		@Source("toggleColumnsDialogBox.css")
		@CssResource.NotStrict
		CssResource style();
	}

	private final LoadingSpinner loadingSpinner = new LoadingSpinner(true);
	private final CloseButton closeButton;
	private GeneralJavaScriptObject[] columns;
	private GeneralJavaScriptObject columnData;
	private Label missionLabel;
	private String datasetId;
	private boolean isInitialized = false;
	private boolean hasBeenClosed = false;
	private FlowPanel contentAndCloseButton;
	
	private final FlowPanel contentContainer = new FlowPanel();
	private final FlowPanel tabulatorContainer = new FlowPanel();
	
	private TabulatorWrapper tabulatorTable;
	
	
	public interface ToggleColumnAction{
	    void onShow(String field);
	    void onHide(String field);
	    void multiSelectionInProgress();
	    void multiSelectionInFinished();
	}
	
	private final ToggleColumnAction toggleColumnAction;
	
	
	public ToggleColumnsDialogBox(String mission, GeneralJavaScriptObject[] columns, ToggleColumnAction toggleColumnAction, String datasetId){
		super(GoogleAnalytics.CAT_ToggleColumns);
		this.style = this.resources.style();
		this.style.ensureInjected();
		this.columns = columns;
		this.toggleColumnAction = toggleColumnAction;
		this.datasetId = datasetId;
		
        loadingSpinner.addStyleName("toggleColumnsLoadingSpinner");
        MainLayoutPanel.addElementToMainArea(loadingSpinner);
		
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ToggleColumns, GoogleAnalytics.ACT_ToggleColumnsOpen, datasetId);

		contentContainer.addStyleName("toggleColumns__contentContainer");
		tabulatorContainer.getElement().setId("toggleColumns__tabulatorContainer");

		closeButton = new CloseButton();
		closeButton.addStyleName("toggleColumns__closeButton");
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				hide();
			}
		});

		missionLabel = new Label(mission);
		missionLabel.setStyleName("toggleColumns__missionLabel");

		contentAndCloseButton = new FlowPanel();
		contentAndCloseButton.add(missionLabel);
		contentAndCloseButton.add(closeButton);
		contentContainer.add(tabulatorContainer);
		contentAndCloseButton.add(contentContainer);
		add(contentAndCloseButton);
		setMaxSize();

		addStyleName("toggleColumns__dialogBox");

		addElementNotAbleToInitiateMoveOperation(contentContainer.getElement());
		show();
	}

    private native GeneralJavaScriptObject[] extractData(GeneralJavaScriptObject[] columns)/*-{
        var tableData = [];
        var selectedRows = [];
        var i = 0;
        var numberOfObservationOidFound = 0;
        columns.forEach(function (column){
            var shouldHideColumn = false;
            for(var j = 0; j < $wnd.esasky.databaseColumnsToHide.length; j++){
                if(column.getField().toLowerCase() === $wnd.esasky.databaseColumnsToHide[j]){
                    shouldHideColumn = true;
                }
            }
            if(column.getField().toLowerCase() === "observation_oid"){
                numberOfObservationOidFound++;
                if(numberOfObservationOidFound > 1){
                    shouldHideColumn = true;
                }
            }
            if(!shouldHideColumn && column.getField() !== "rowSelection"){
                tableData.push(
                    {   
                        id: i,
                        name: column.getDefinition().title, 
                        tap_name: column.getField(), 
                        description: column.getDefinition().headerTooltip
                    });
                if (column.isVisible()) {
                    selectedRows.push(i);
                }
                i++;
            }
        });
        var metadata = [];
        metadata.push(
            {
                name: "name",
                displayName: $wnd.esasky.getInternationalizationText("toggleColumns_headerTitleColumn"), 
                datatype:"HTML", 
                visible: true
            });
        metadata.push(
            {
                name: "tap_name",
                displayName: $wnd.esasky.getInternationalizationText("toggleColumns_headerTitleDatabaseName"), 
                datatype:"STRING_HIDE_NON_DATABASE_VALUES", 
                visible: true
            });
        metadata.push(
            {
                name: "description",
                displayName: $wnd.esasky.getInternationalizationText("toggleColumns_headerTitleDescription"), 
                datatype:"STRING", 
                visible: true
            });
        return [tableData, metadata, selectedRows];
    }-*/;
	
	@Override
	protected void onLoad() {
		super.onLoad();   
		Scheduler.get().scheduleDeferred(() -> {
                tabulatorTable = new TabulatorWrapper("toggleColumns__tabulatorContainer", ToggleColumnsDialogBox.this, false, false, false, false, TextMgr.getInstance().getText("toggleColumns_rowSelectionTitle"), true, false, true);
                GeneralJavaScriptObject data[] = extractData(columns);
                tabulatorTable.insertData(data[0], data[1]);
                tabulatorTable.selectRows(data[2]);
                tabulatorTable.restoreRedraw();
                tabulatorTable.redrawAndReinitializeHozVDom();
                isInitialized = true;
        });
	}
	private long timeAtLastResize;
	
	@Override
	public void setMaxSize() {
	    super.setMaxSize();
	    int height = MainLayoutPanel.getMainAreaHeight();
		if (height > MainLayoutPanel.getMainAreaHeight() - 30 - 2) {
			height = MainLayoutPanel.getMainAreaHeight() - 30 - 2;
		}
		contentContainer.getElement().getStyle().setPropertyPx("height", height - contentContainer.getAbsoluteTop());
	}

    @Override
    public void onDataLoaded(GeneralJavaScriptObject javaScriptObject) {
        MainLayoutPanel.removeElementFromMainArea(loadingSpinner);
        setSuggestedPositionCenter();
    }

    @Override
    public void onRowSelection(GeneralJavaScriptObject row) {
        if(isInitialized) {
            String tapName = row.invokeFunction("getData").getStringProperty("tap_name");
            if(tapName.equals("rowSelection")) {
                return;
            }
            toggleColumnAction.onShow(tapName);
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ToggleColumns, GoogleAnalytics.ACT_ToggleColumnsShow,"Column Name: " + tapName + "Dataset: " + datasetId);
        }
    }

    @Override
    public void onRowDeselection(GeneralJavaScriptObject row) {
        if(isInitialized) {
            String tapName = row.invokeFunction("getData").getStringProperty("tap_name");
            if("rowSelection".equals(tapName)) {
                return;
            }
            toggleColumnAction.onHide(tapName);
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ToggleColumns, GoogleAnalytics.ACT_ToggleColumnsHide,"Column Name: " + tapName + "Dataset: " + datasetId);
        }
    }
    
    @Override
    public void onTableHeightChanged() {
        //No need to do anything
    }

    @Override
    public void onRowMouseEnter(int rowId) {
    }

    @Override
    public void onRowMouseLeave(int rowId) {
    }

    @Override
    public void onFilterChanged(String label, String filter) {
    }

    @Override
    public void onDataFiltered(List<Integer> filteredRows) {
    }

    @Override
    public void onDatalinkClicked(GeneralJavaScriptObject javaScriptObject) {
    }

    @Override
    public void onAccessUrlClicked(String url) {
    }

    @Override
    public void onPostcardUrlClicked(GeneralJavaScriptObject rowData, String columnName) {
    }

    @Override
    public void onCenterClicked(GeneralJavaScriptObject rowData) {
    }

    @Override
    public void onSendToVoApplicaitionClicked(GeneralJavaScriptObject rowData) {
    }

    @Override
    public void onLink2ArchiveClicked(GeneralJavaScriptObject rowData) {
    }

    @Override
    public void onSourcesInPublicationClicked(GeneralJavaScriptObject rowData) {
    }

    @Override
    public void onAjaxResponse() {
    }

    @Override
    public void onAjaxResponseError(String error) {
    }

    @Override
    public String getLabelFromTapName(String tapName) {
        return null;
    }

    @Override
    public GeneralJavaScriptObject getDescriptorMetaData() {
        return null;
    }

    @Override
    public boolean isMOCMode() {
        return false;
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
    public String getEsaSkyUniqId() {
        return null;
    }

    @Override
    public void multiSelectionInProgress() {
        MainLayoutPanel.addElementToMainArea(loadingSpinner);
        toggleColumnAction.multiSelectionInProgress();
    }

    @Override
    public void multiSelectionFinished() {
        toggleColumnAction.multiSelectionInFinished();
        MainLayoutPanel.removeElementFromMainArea(loadingSpinner);
    }
	
    
    @Override
    public void hide() {
        MainLayoutPanel.removeElementFromMainArea(loadingSpinner);
        this.hasBeenClosed = true;
        super.hide();
    }

    @Override
    public boolean hasBeenClosed() {
        return this.hasBeenClosed;
    }

	@Override
	public void onAddHipsClicked(GeneralJavaScriptObject rowData) {
		// Not needed
	}
}
