package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.hips.HipsAddedEvent;
import esac.archive.esasky.cl.web.client.event.hips.HipsNameChangeEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.HipsParser;
import esac.archive.esasky.cl.web.client.utility.HipsParserObserver;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.descriptor.BaseDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.GwDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;

public class GwPanel extends PopupPanel implements TabulatorWrapper.TabulatorCallback{

	public interface GwDescriptorListMapper extends ObjectMapper<GwDescriptorList> {}
	private BaseDescriptor gwDescriptor;
	private boolean loadingDescriptor = false;
	
	private List<String> defaultVisibleColumns = new LinkedList<>();
	
	private PopupHeader header;
	private final Resources resources;
	private CssResource style;

	private boolean isShowing;
	private boolean isShowingMore = false;

	private FlowPanel gwPanel = new FlowPanel();
	private EsaSkyStringButton showMoreButton = new EsaSkyStringButton("Show more/less");
	private TabulatorWrapper tabulatorTable;
	private final FlowPanel tabulatorContainer = new FlowPanel();
	private LoadingSpinner loadingSpinner = new LoadingSpinner(true);
	private Map<String, Integer> rowIdHipsMap = new HashMap<>();
	private boolean blockOpenHipsTrigger = false;
	
	public static interface Resources extends ClientBundle {

		@Source("gw.css")
		@CssResource.NotStrict
		CssResource style();
	}
	
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
		CommonEventBus.getEventBus().addHandler(HipsNameChangeEvent.TYPE, changeEvent -> {
			Integer rowId = rowIdHipsMap.get(changeEvent.getHiPSName());
			if(rowId != null && !tabulatorTable.isSelected(rowId)) {
				tabulatorTable.deselectAllRows();
				blockOpenHipsTrigger = true;
				tabulatorTable.selectRow(rowId, false);
				blockOpenHipsTrigger = false;
			}
		});
	}
	
	public void initGwDescriptor() {
		JSONUtils.getJSONFromUrl(EsaSkyWebConstants.GW_URL, new IJSONRequestCallback() {

			@Override
			public void onSuccess(String responseText) {
				GwDescriptorListMapper mapper = GWT.create(GwDescriptorListMapper.class);
				GwDescriptorList mappedDescriptorList = mapper.read(responseText);

				gwDescriptor = mappedDescriptorList.getDescriptors().get(0);
				loadData();
			}

			@Override
			public void onError(String errorCause) {
				Log.error("[GravitationalWaves] initGwDescriptor ERROR: " + errorCause);
			}

		});
		loadingDescriptor = true;
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		setMaxSize();
	}
	
	private void loadData() {
		//TODO race condition between tabulatorTable initialization and call of this method?
        tabulatorTable = new TabulatorWrapper("gwPanel__tabulatorContainer", GwPanel.this);
        tabulatorTable.setDefaultQueryMode();

        for(MetadataDescriptor md : gwDescriptor.getMetadata()) {
        	if(md.getVisible()) {
        		defaultVisibleColumns.add(md.getTapName());
        	}
        }

		tabulatorTable.setData(EsaSkyWebConstants.TAP_CONTEXT + "/tap/sync?request=doQuery&lang=ADQL&format=json&query=select+*+from+alerts.mv_gravitational_waves_fdw");
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
		loadingSpinner.setVisible(false);
		loadingSpinner.addStyleName("gwPanel_loadingHipsProperties");
		gwPanel.add(loadingSpinner);
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
	
	private void showAllColumns() {
		tabulatorTable.blockRedraw();
		for(MetadataDescriptor md : gwDescriptor.getMetadata()) {
			if(!md.getVisible()) {
				tabulatorTable.showColumn(md.getTapName());
			}
		}
		tabulatorTable.hideColumn("stcs50");
		tabulatorTable.hideColumn("stcs90");
		tabulatorTable.hideColumn("gravitational_waves_oid");
		tabulatorTable.hideColumn("group_id");
		tabulatorTable.hideColumn("hardware_inj");
		tabulatorTable.hideColumn("internal");
		tabulatorTable.hideColumn("open_alert");
		tabulatorTable.hideColumn("pkt_ser_num");
		tabulatorTable.hideColumn("search");
		tabulatorTable.hideColumn("packet_type");
 
		tabulatorTable.restoreRedraw();
		tabulatorTable.redrawAndReinitializeHozVDom();
	}

	private void showOnlyBaseColumns() {
		tabulatorTable.blockRedraw();
		for(MetadataDescriptor md : gwDescriptor.getMetadata()) {
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
		if(gwDescriptor == null && !loadingDescriptor) {
			initGwDescriptor();
		}
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
		setMaxSize();
	}

	@Override
	public void onTableHeightChanged() {
	}

	@Override
	public void onRowSelection(GeneralJavaScriptObject row) {
		GeneralJavaScriptObject rowData = row.invokeFunction("getData");
		String id = rowData.getStringProperty("grace_id");
		
		if(!blockOpenHipsTrigger) {
			//TODO do not use skiesdev
			testParsingHipsList("http://skiesdev.esac.esa.int/GW/" + id, GeneralJavaScriptObject.convertToInteger(rowData.getProperty("id")));
		}

		String stcs90EntityId = gwDescriptor.getDescriptorId() + "_90";
        GeneralEntityInterface stcs90entity = EntityRepository.getInstance().getEntity(stcs90EntityId);
        if(stcs90entity == null) {
        	stcs90entity = EntityRepository.getInstance().createGwEntity(gwDescriptor, stcs90EntityId, "dashed");
        }
        stcs90entity.addShapes(row.invokeFunction("getData").wrapInArray());
        
        
        gwDescriptor.setTapSTCSColumn("stcs50");
        String stcs50EntityId = gwDescriptor.getDescriptorId() + "_50";
        GeneralEntityInterface stcs50entity = EntityRepository.getInstance().getEntity(stcs50EntityId);
        if(stcs50entity == null) {
        	stcs50entity = EntityRepository.getInstance().createGwEntity(gwDescriptor, stcs50EntityId, "solid");
        }
        stcs50entity.addShapes(row.invokeFunction("getData").wrapInArray());
        gwDescriptor.setTapSTCSColumn("stcs90");
	}

	private void testParsingHipsList(String url, Integer rowId) {
		loadingSpinner.setVisible(true);
		HipsParser parser = new HipsParser(new HipsParserObserver() {
			
			@Override
			public void onSuccess(HiPS hips) {
				rowIdHipsMap.put(hips.getSurveyName(), rowId);
				loadingSpinner.setVisible(false);
				CommonEventBus.getEventBus().fireEvent(new HipsAddedEvent(hips, true, false));
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_GW, GoogleAnalytics.ACT_GW_SHOW_HIPS, url);
			}
			
			@Override
			public void onError(String errorMsg) {
				loadingSpinner.setVisible(false);
				String fullErrorText = TextMgr.getInstance().getText("addSky_errorParsingProperties");
				fullErrorText = fullErrorText.replace("$DUE_TO$", errorMsg);
			
				DisplayUtils.showMessageDialogBox(fullErrorText, TextMgr.getInstance().getText("error").toUpperCase(), UUID.randomUUID().toString(),
						TextMgr.getInstance().getText("error"));
				
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_GW, GoogleAnalytics.ACT_GW_SHOW_HIPS_FAIL, url);
				Log.error(errorMsg);
			}
		});
		parser.loadProperties(url);
	}

	@Override
	public void onRowDeselection(GeneralJavaScriptObject row) {
		EntityRepository.getInstance().getEntity(gwDescriptor.getDescriptorId() + "_90").removeAllShapes();
		EntityRepository.getInstance().getEntity(gwDescriptor.getDescriptorId() + "_50").removeAllShapes();
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
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_DOWNLOADROW, "GravitationalWaves", url);
		UrlUtils.openUrl(url);
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
		String url = row.invokeFunction("getData").getStringProperty("event_page");
		GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_OUTBOUND, GoogleAnalytics.ACT_OUTBOUND_CLICK, url);
        UrlUtils.openUrl(url);
	}

	@Override
	public void onSourcesInPublicationClicked(GeneralJavaScriptObject rowData) {
	}

	@Override
	public void onAddHipsClicked(GeneralJavaScriptObject rowData) {
	}

	@Override
	public void onAjaxResponse() {
	}

	@Override
	public void onAjaxResponseError(String error) {
		Log.error(error);
	}

	@Override
	public String getLabelFromTapName(String tapName) {
		return null;
	}

	@Override
	public GeneralJavaScriptObject getDescriptorMetaData() {
		return gwDescriptor.getMetaDataJSONObject();
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
		return false;
	}
}
