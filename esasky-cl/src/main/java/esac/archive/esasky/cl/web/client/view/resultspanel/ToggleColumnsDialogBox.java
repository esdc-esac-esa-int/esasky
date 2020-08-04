package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.AutoHidingMovablePanel;
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

	private final CloseButton closeButton;
	private GeneralJavaScriptObject[] columns;
	private GeneralJavaScriptObject columnData;
	private Label missionLabel;
	private String datasetId;
	private boolean isInitialized = false;
	
	private final FlowPanel contentContainer = new FlowPanel();
	
	private TabulatorWrapper tabulatorTable;
	
	
	public interface ToggleColumnAction{
	    void onShow(String field);
	    void onHide(String field);
	}
	
	private final ToggleColumnAction toggleColumnAction;
	
	
	public ToggleColumnsDialogBox(String mission, GeneralJavaScriptObject[] columns, ToggleColumnAction toggleColumnAction, String datasetId){
		super(GoogleAnalytics.CAT_ToggleColumns);
		this.style = this.resources.style();
		this.style.ensureInjected();
		this.columns = columns;
		this.toggleColumnAction = toggleColumnAction;
		this.datasetId = datasetId;
		
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ToggleColumns, GoogleAnalytics.ACT_ToggleColumnsOpen, datasetId);

		contentContainer.addStyleName("toggleColumns__contentContainer");
		contentContainer.getElement().setId("toggleColumns__contentContainer");

		closeButton = new CloseButton();
		closeButton.addStyleName("toggleColumns__closeButton");
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				hide();
			}
		});

		missionLabel = new Label(mission);
		missionLabel.setStyleName("toggleColumns__missionLabel");

		FlowPanel contentAndCloseButton = new FlowPanel();
		contentAndCloseButton.setHeight("100%");
		contentAndCloseButton.add(missionLabel);
		contentAndCloseButton.add(closeButton);
		contentAndCloseButton.add(contentContainer);
		add(contentAndCloseButton);

		addStyleName("toggleColumns__dialogBox");

		addElementNotAbleToInitiateMoveOperation(contentContainer.getElement());
		show();
	}

    private native GeneralJavaScriptObject[] extractData(GeneralJavaScriptObject[] columns)/*-{
        var tableData = [];
        var selectedRows = [];
        var i = 0;
        columns.forEach(function (column){
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
		tabulatorTable = new TabulatorWrapper("toggleColumns__contentContainer", this, false, false, false, TextMgr.getInstance().getText("toggleColumns_rowSelectionTitle"));
		GeneralJavaScriptObject data[] = extractData(columns);
		tabulatorTable.insertData(data[0], data[1]);
		numberOfRowsToSelectAtStartup = GeneralJavaScriptObject.convertToArray(data[2]).length;
		tabulatorTable.selectRows(data[2]);
		setMaxSize();
		isInitialized = true;
	}
	private int numberOfRowsToSelectAtStartup;
	private long timeAtLastResize;
	
	@Override
	public void setMaxSize() {
	    super.setMaxSize();
	    int height = MainLayoutPanel.getMainAreaHeight();
		if (height > MainLayoutPanel.getMainAreaHeight() - 30 - 2 - missionLabel.getOffsetHeight()) {
			height = MainLayoutPanel.getMainAreaHeight() - 30 - 2 - missionLabel.getOffsetHeight();
		}
		if(System.currentTimeMillis() - timeAtLastResize > 500 && tabulatorTable.getTableHeight() > 0) {
		    timeAtLastResize = System.currentTimeMillis();
		    if(tabulatorTable.getTableHeight() + 100 > height) {
		        contentContainer.getElement().getStyle().setPropertyPx("height", height);
		    } else {
		        contentContainer.getElement().getStyle().setProperty("height", "auto");
		    }
		    setSuggestedPositionCenter();
		}
	}

    @Override
    public void onDataLoaded(GeneralJavaScriptObject javaScriptObject) {
        setMaxSize();
    }

    @Override
    public void onRowSelection(GeneralJavaScriptObject row) {
        numberOfRowsToSelectAtStartup --;
        if(isInitialized && numberOfRowsToSelectAtStartup < 0) {
            String tapName = row.invokeFunction("getData").getStringProperty("tap_name");
            toggleColumnAction.onShow(tapName);
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ToggleColumns, GoogleAnalytics.ACT_ToggleColumnsShow,"Column Name: " + tapName + "Dataset: " + datasetId);
        }
    }

    @Override
    public void onRowDeselection(GeneralJavaScriptObject row) {
        if(isInitialized) {
            String tapName = row.invokeFunction("getData").getStringProperty("tap_name");
            toggleColumnAction.onHide(tapName);
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ToggleColumns, GoogleAnalytics.ACT_ToggleColumnsHide,"Column Name: " + tapName + "Dataset: " + datasetId);
        }
    }
    
    @Override
    public void onTableHeightChanged() {
        setMaxSize();
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
    public void onPostcardUrlClicked(GeneralJavaScriptObject rowData) {
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
	
}
