package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.AutoHidingMovablePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper.TabulatorCallback;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class BrowseHipsPanel extends AutoHidingMovablePanel implements TabulatorCallback{

	private final Resources resources = GWT.create(Resources.class);
	private CssResource style;

	public interface Resources extends ClientBundle {
		@Source("BrowseHipsPanel.css")
		@CssResource.NotStrict
		CssResource style();
	}

	private final FlowPanel contentContainer = new FlowPanel();
	private final FlowPanel tabulatorContainer = new FlowPanel();
	private final CloseButton closeButton;
	private FlowPanel contentAndCloseButton;
	
	private final String ALADIN_GLOBAL_HIPSLIST_URL = "//aladin.u-strasbg.fr/hips/globalhipslist?fmt=json";
	
	private List<BrowseHipsPanelObserver> observers = new LinkedList<BrowseHipsPanelObserver>();
	TabulatorWrapper tabulatorTable;
	
	public BrowseHipsPanel() {
		super(GoogleAnalytics.CAT_BrowseHips);
		this.style = this.resources.style();
		this.style.ensureInjected();

		closeButton = new CloseButton();
		closeButton.addStyleName("browseHips__closeButton");
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				hide();	
			}
		});
		
		contentContainer.addStyleName("browseHips__contentContainer");
		tabulatorContainer.getElement().setId("browseHips__tabulatorContainer");	
		
		Label missionLabel = new Label(TextMgr.getInstance().getText("browseHips_headerLabel"));
		missionLabel.setStyleName("browseHips__missionLabel");

		contentAndCloseButton = new FlowPanel();
		contentAndCloseButton.add(missionLabel);
		contentAndCloseButton.add(closeButton);
		contentContainer.add(tabulatorContainer);
		contentAndCloseButton.add(contentContainer);
		add(contentAndCloseButton);
		addStyleName("browseHips__dialogBox");
		loadData();
		
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SkiesMenu, GoogleAnalytics.ACT_SkiesMenu_BrowseHips, "");
	}
	
	private void loadData() {
		
		JSONUtils.getJSONFromUrl(ALADIN_GLOBAL_HIPSLIST_URL, new IJSONRequestCallback() {

			@Override
			public void onSuccess(String responseText) {
				onJsonloaded(responseText);
			}

			@Override
			public void onError(String errorCause) {
				String errorMsg = TextMgr.getInstance().getText("browseHips_errorLoadingGlobal");
				errorMsg = errorMsg.replace("$URL$", ALADIN_GLOBAL_HIPSLIST_URL);
				DisplayUtils.showMessageDialogBox(errorMsg, TextMgr.getInstance().getText("error").toUpperCase(), UUID.randomUUID().toString(),
						TextMgr.getInstance().getText("error"));
				Log.error(errorCause);
			}
			
		});
			
	}
	
	public native GeneralJavaScriptObject createMetadata() /*-{
		
		var metadata = [];
        metadata.push(
            {
                name: "ID",
                displayName: $wnd.esasky.getInternationalizationText("ID"), 
                datatype:"STRING", 
                visible: true
            });
        metadata.push(
            {
                name: "obs_title",
                displayName: $wnd.esasky.getInternationalizationText("title"), 
                datatype:"STRING", 
                visible: true
            });
        metadata.push(
            {
                name: "hips_service_url",
                displayName: $wnd.esasky.getInternationalizationText("URL"), 
                datatype:"HTML", 
                visible: true
            });
        metadata.push(
            {
                name: "moc_order",
                displayName: $wnd.esasky.getInternationalizationText("order"), 
                datatype:"INTEGER", 
                visible: true
            });
        metadata.push(
            {
                name: "moc_sky_fraction",
                displayName: $wnd.esasky.getInternationalizationText("coverage"), 
                datatype:"DOUBLE", 
                visible: true
            });
        metadata.push(
            {
                name: "em_min",
                displayName: $wnd.esasky.getInternationalizationText("em_min"), 
                datatype:"DOUBLE", 
                visible: true
            });
        metadata.push(
            {
                name: "em_max",
                displayName: $wnd.esasky.getInternationalizationText("em_max"), 
                datatype:"DOUBLE", 
                visible: true
            });
            
           return metadata;
	}-*/;

	public void onJsonloaded(String jsonText) {
		this.show();
		tabulatorTable = new TabulatorWrapper("browseHips__tabulatorContainer", this, false, false, false, false);
		GeneralJavaScriptObject metadata = createMetadata();
		tabulatorTable.setAddHipsColumn(true);
		tabulatorTable.insertData(jsonText, metadata);
		tabulatorTable.restoreRedraw();
        tabulatorTable.redrawAndReinitializeHozVDom();
	}


	@Override
	public void onDataLoaded(GeneralJavaScriptObject rowData) {
		// Not needed
	}

	@Override
	public void onTableHeightChanged() {
		// Not needed
	}

	@Override
	public void onRowSelection(GeneralJavaScriptObject row) {
		// Not needed
	}

	@Override
	public void onRowDeselection(GeneralJavaScriptObject row) {
		// Not needed
	}

	@Override
	public void onRowMouseEnter(int rowId) {
		// Not needed
	}

	@Override
	public void onRowMouseLeave(int rowId) {
		// Not needed
	}

	@Override
	public void onFilterChanged(String label, String filter) {
		// Not needed
	}

	@Override
	public void onDataFiltered(List<Integer> filteredRows) {
		// Not needed
	}

	@Override
	public void onDatalinkClicked(GeneralJavaScriptObject javaScriptObject) {
		// Not needed
	}

	@Override
	public void onAccessUrlClicked(String url) {
		// Not needed
	}

	@Override
	public void onPostcardUrlClicked(GeneralJavaScriptObject rowData, String columnName) {
		// Not needed
	}

	@Override
	public void onCenterClicked(GeneralJavaScriptObject rowData) {
		// Not needed
	}

	@Override
	public void onSendToVoApplicaitionClicked(GeneralJavaScriptObject rowData) {
		// Not needed
	}

	@Override
	public void onLink2ArchiveClicked(GeneralJavaScriptObject rowData) {
		// Not needed
	}

	@Override
	public void onSourcesInPublicationClicked(GeneralJavaScriptObject rowData) {
		// Not needed
	}

	@Override
	public void onAjaxResponse() {
		// Not needed
	}

	@Override
	public void onAjaxResponseError(String error) {
		// Not needed
	}

	@Override
	public String getLabelFromTapName(String tapName) {
		// Not needed
		return null;
	}

	@Override
	public GeneralJavaScriptObject getDescriptorMetaData() {
		// Not needed
		return null;
	}

	@Override
	public String getRaColumnName() {
		// Not needed
		return null;
	}

	@Override
	public String getDecColumnName() {
		// Not needed
		return null;
	}

	@Override
	public boolean isMOCMode() {
		// Not needed
		return false;
	}

	@Override
	public String getEsaSkyUniqId() {
		// Not needed
		return null;
	}

	@Override
	public void multiSelectionInProgress() {
		// Not needed
	}

	@Override
	public void multiSelectionFinished() {
		// Not needed
	}

	@Override
	public boolean hasBeenClosed() {
		// Not needed
		return false;
	}

	@Override
	public void setMaxSize() {
	    super.setMaxSize();
	    int height = MainLayoutPanel.getMainAreaHeight();
		if (height > MainLayoutPanel.getMainAreaHeight() - 30 - 2) {
			height = MainLayoutPanel.getMainAreaHeight() - 30 - 2;
		}
		contentContainer.getElement().getStyle().setPropertyPx("height", height - contentContainer.getAbsoluteTop());
	}

	public void registerObserver(BrowseHipsPanelObserver observer) {
		observers.add(observer);
	}
	
	@Override
	public void onAddHipsClicked(GeneralJavaScriptObject rowData) {
		List<String> urls = new LinkedList<>();
		urls.add(rowData.getStringProperty("hips_service_url"));
		int i = 1;
		while(rowData.hasProperty("hips_service_url_" + i)) {
			urls.add(rowData.getStringProperty("hips_service_url_" + i));
			i++;
		}
		
		for(BrowseHipsPanelObserver observer : observers) {
			observer.onHipsAdded(urls);
		}
	}
}
