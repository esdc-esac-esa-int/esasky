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

package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.AutoHidingMovablePanel;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.DefaultTabulatorCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class ToggleColumnsDialogBox extends AutoHidingMovablePanel{
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
		super(GoogleAnalytics.CAT_TOGGLECOLUMNS);
		this.style = this.resources.style();
		this.style.ensureInjected();
		this.columns = columns;
		this.toggleColumnAction = toggleColumnAction;
		this.datasetId = datasetId;
		
        loadingSpinner.addStyleName("toggleColumnsLoadingSpinner");
        MainLayoutPanel.addElementToMainArea(loadingSpinner);
		
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TOGGLECOLUMNS, GoogleAnalytics.ACT_TOGGLECOLUMNSOPEN, datasetId);

		contentContainer.addStyleName("toggleColumns__contentContainer");
		tabulatorContainer.getElement().setId("toggleColumns__tabulatorContainer");

		closeButton = new CloseButton();
		closeButton.addStyleName("toggleColumns__closeButton");
		closeButton.addClickHandler(event -> hide());

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
			TabulatorSettings settings = new TabulatorSettings();
			settings.setSelectionHeaderTitle(TextMgr.getInstance().getText("toggleColumns_rowSelectionTitle"));
			settings.setBlockRedraw(true);
			settings.setAddSelectionColumn(true);
            tabulatorTable = new TabulatorWrapper("toggleColumns__tabulatorContainer", new TabulatorCallback(), settings);
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

	private class TabulatorCallback extends DefaultTabulatorCallback{
		
		@Override
		public void onDataLoaded(GeneralJavaScriptObject javaScriptObject, GeneralJavaScriptObject metadata) {
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
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TOGGLECOLUMNS, GoogleAnalytics.ACT_TOGGLECOLUMNSSHOW,"Column Name: " + tapName + "Dataset: " + datasetId);
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
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TOGGLECOLUMNS, GoogleAnalytics.ACT_TOGGLECOLUMNSHIDE,"Column Name: " + tapName + "Dataset: " + datasetId);
			}
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
		public boolean hasBeenClosed() {
			return hasBeenClosed;
		}
		
	}


	@Override
	public void hide() {
		MainLayoutPanel.removeElementFromMainArea(loadingSpinner);
		this.hasBeenClosed = true;
		super.hide();
	}
}
