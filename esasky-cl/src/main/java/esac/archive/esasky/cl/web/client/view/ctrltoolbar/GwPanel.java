package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import java.util.LinkedList;
import java.util.List;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;

import esac.archive.esasky.cl.web.client.api.ApiOverlay;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.HipsParser;
import esac.archive.esasky.cl.web.client.utility.HipsParserObserver;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.AddSkyObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class GwPanel extends PopupPanel implements TabulatorWrapper.TabulatorCallback{

	private PopupHeader header;
	private final Resources resources;
	private CssResource style;

	private boolean isShowing;
	private boolean isShowingMore = false;

	private FlowPanel gwPanel = new FlowPanel();
	//TODO Ugly button - If button stays fix internationalization
	private EsaSkyStringButton showMoreButton = new EsaSkyStringButton("Show more/less");
	private TabulatorWrapper tabulatorTable;
	private final FlowPanel tabulatorContainer = new FlowPanel();
	
	public static interface Resources extends ClientBundle {

		@Source("gw.css")
		@CssResource.NotStrict
		CssResource style();
	}
	
	public static GwPanel instance = null;

	public GwPanel() {
		super(false, false);

		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();

		initView();
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				setMaxSize();
			}
		});
		
		//TODO fix ugliness
		instance = this;
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		Scheduler.get().scheduleDeferred(() -> {
//            tabulatorTable = new TabulatorWrapper("gwPanel__tabulatorContainer", GwPanel.this);
//            tabulatorTable.setDefaultQueryMode();

    });
		setMaxSize();
	}
	public void loadData() {
		//TODO race condition between tabulatorTable initialization and call of this method?
        tabulatorTable = new TabulatorWrapper("gwPanel__tabulatorContainer", GwPanel.this);
        tabulatorTable.setDefaultQueryMode();

        for(MetadataDescriptor md : DescriptorRepository.gwDescriptors.getMetadata()) {
        	if(md.getVisible()) {
        		defaultVisibleColumns.add(md.getTapName());
        	}
        }

        //TODO fix url
		tabulatorTable.setData("/esasky-tap/tap/sync?request=doQuery&lang=ADQL&format=json&query=select+*+from+alerts.mv_gravitational_waves_fdw");
	}
	private void initView() {
		this.getElement().addClassName("gwPanel");

		//TODO texts
		header = new PopupHeader(this, "Gravitational Waves", 
				TextMgr.getInstance().getText("publicationPanel_helpText"), 
				TextMgr.getInstance().getText("publicationPanel_title"));

		gwPanel.add(header);
		tabulatorContainer.getElement().setId("gwPanel__tabulatorContainer");
		gwPanel.add(tabulatorContainer);
		gwPanel.add(showMoreButton);
		showMoreButton.addClickHandler((event)->{
			if(isShowingMore) {
				showOnlyBaseColumns();
			} else {
				showAllColumns();
			}
			isShowingMore = !isShowingMore;
		});
		
		this.add(gwPanel);
	}
	
	private List<String> defaultVisibleColumns = new LinkedList<>();
	
	private void showAllColumns() {
		tabulatorTable.blockRedraw();
		for(MetadataDescriptor md : DescriptorRepository.gwDescriptors.getMetadata()) {
			if(!md.getVisible()) {
				tabulatorTable.showColumn(md.getTapName());
			}
		}
		tabulatorTable.hideColumn("stcs50");
		tabulatorTable.hideColumn("stcs90");
		tabulatorTable.restoreRedraw();
		tabulatorTable.redrawAndReinitializeHozVDom();
	}
	private void showOnlyBaseColumns() {
		tabulatorTable.blockRedraw();
		for(MetadataDescriptor md : DescriptorRepository.gwDescriptors.getMetadata()) {
			if(!defaultVisibleColumns.contains(md.getTapName())) {
				tabulatorTable.hideColumn(md.getTapName());
			} else {
				tabulatorTable.showColumn(md.getTapName());
			}
		}
		tabulatorTable.restoreRedraw();
		tabulatorTable.redrawAndReinitializeHozVDom();
		
	}
	
	
	private void setMaxSize() {
		Style style = getElement().getStyle();
		style.setPropertyPx("maxWidth", MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15);
		style.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15);
	    int height = MainLayoutPanel.getMainAreaHeight();
	    if(height > 600) {
	    	height = 600;
	    }
		if (height > MainLayoutPanel.getMainAreaHeight() - 30 - 2) {
			height = MainLayoutPanel.getMainAreaHeight() - 30 - 2;
		}
		if(tabulatorContainer != null && tabulatorContainer.getElement() != null) {
			tabulatorContainer.getElement().getStyle().setPropertyPx("height", height - tabulatorContainer.getAbsoluteTop());
		}
	}
	
	@Override
	public void setPopupPosition(int left, int top) {
		setMaxSize();
	}

	@Override
	public void show() {
		isShowing = true;
		this.removeStyleName("displayNone");
		setMaxSize();
	}

	@Override
	public void hide(boolean autohide) {
		this.addStyleName("displayNone");
		isShowing = false;
		CloseEvent.fire(this, null);
	}

	public void toggle() {
		if(isShowing()) {
			hide();
		} else {
			show();
		}
	}
	
	@Override
	public boolean isShowing() {
		return isShowing;
	}

	@Override
	public void onDataLoaded(GeneralJavaScriptObject rowData) {

	}

	@Override
	public void onTableHeightChanged() {
	}

	@Override
	public void onRowSelection(GeneralJavaScriptObject row) {
		GeneralJavaScriptObject rowData = row.invokeFunction("getData");
		String id = rowData.getStringProperty("grace_id");
		//TODO do not use skiesdev
		testParsingHipsList("http://skiesdev.esac.esa.int/GW/" + id, 0, "");
		GeneralJavaScriptObject footprint = createFootprint(rowData.getStringProperty("stcs50"), "gw50", "blue");
		ApiOverlay.instance.overlayFootprints(footprint.jsonStringify(), false);
		GeneralJavaScriptObject footprint90 = createFootprint(rowData.getStringProperty("stcs90"), "gw90", "red");
		ApiOverlay.instance.overlayFootprints(footprint90.jsonStringify(), false);
		
		
	}
	private native GeneralJavaScriptObject createFootprint(String stcs, String id, String color) /*-{
		//TODO lineWidth does not have any effect?
		return {'overlaySet': {'type': 'FootprintListOverlay', 'overlayName': id, 'cooframe': 'J2000', 'color': color, 'lineWidth': 50, 'skyObjectList': [{'name': 'test footprint1', 'id': 1, 'stcs': stcs, 'ra_deg': '0.0', 'dec_deg': '0.0'}]}};
	}-*/;
	
	//TODO remove static ugliness
	public static AddSkyObserver addSkyObserver;
	
	private void testParsingHipsList(String url, final int currentIndex, String lastError) {
		//TODO deal with race conditions if multiple rows are clicked in rapid succession
		//TODO cleanup
		HipsParser parser = new HipsParser(new HipsParserObserver() {
			
			@Override
			public void onSuccess(HiPS hips) {
				addSkyObserver.onSkyAddedWithUrl(hips);
//				errorLabel.setVisible(false);
				hide();
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SKIESMENU, GoogleAnalytics.ACT_SKIESMENU_ADDURL, url);
			}
			
			@Override
			public void onError(String errorMsg) {
//				loadingSpinner.setVisible(false);
//				errorLabel.setVisible(true);
//				String fullErrorText = TextMgr.getInstance().getText("addSky_errorParsingProperties");
//				fullErrorText = fullErrorText.replace("$DUE_TO$", errorMsg);
//				errorLabel.setText(fullErrorText);
//				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SKIESMENU, GoogleAnalytics.ACT_SKIESMENU_ADDURL_FAIL, url);

				Log.error(errorMsg);
				
			}
		});
		parser.loadProperties(url);
	}

	@Override
	public void onRowDeselection(GeneralJavaScriptObject row) {
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
		//TODO duplicated https guard from TabulatorTablePanel
        if("https:".equals(Window.Location.getProtocol()) && url.startsWith("http:")){
            url = url.replaceFirst("http:", "https:");
        }
        Window.open(url, "_blank", "_blank");
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_DOWNLOADROW, "GravitationalWaves", url);
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
	public void onLink2ArchiveClicked(GeneralJavaScriptObject row) {
		//TODO duplicated https guard
        String url = row.invokeFunction("getData").getStringProperty("event_page");
        if("https:".equals(Window.Location.getProtocol()) && url.startsWith("http:")){
        	url = url.replaceFirst("http:", "https:");
        }
    	GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_OUTBOUND, GoogleAnalytics.ACT_OUTBOUND_CLICK, url);
    	Window.open(url, "_blank", "");
	}

	@Override
	public void onSourcesInPublicationClicked(GeneralJavaScriptObject rowData) {
	}

	@Override
	public void onAddHipsClicked(GeneralJavaScriptObject rowData) {
	}

	@Override
	public void onAjaxResponse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAjaxResponseError(String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLabelFromTapName(String tapName) {
		return null;
	}

	@Override
	public GeneralJavaScriptObject getDescriptorMetaData() {
		return DescriptorRepository.gwDescriptors.getMetaDataJSONObject();
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
	}

	@Override
	public void multiSelectionFinished() {
	}

	@Override
	public boolean hasBeenClosed() {
		// TODO Auto-generated method stub
		return false;
	}
}
