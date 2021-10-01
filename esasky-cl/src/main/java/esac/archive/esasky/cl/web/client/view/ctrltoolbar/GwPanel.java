package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.GridToggledEvent;
import esac.archive.esasky.cl.web.client.event.hips.HipsAddedEvent;
import esac.archive.esasky.cl.web.client.event.hips.HipsNameChangeEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
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
import esac.archive.esasky.cl.web.client.view.common.buttons.ChangeableIconButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.descriptor.BaseDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.GwDescriptorList;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;

public class GwPanel extends PopupPanel implements TabulatorWrapper.TabulatorCallback{

	public interface GwDescriptorListMapper extends ObjectMapper<GwDescriptorList> {}
	private BaseDescriptor gwDescriptor;
	
	private List<String> defaultVisibleColumns = new LinkedList<>();
	
	private final Resources resources;
	private CssResource style;

	private boolean isShowing;
	private boolean isExpanded = false;

	private FlowPanel gwPanel = new FlowPanel();
	private Tab neutrinoTab;
	private EsaSkyToggleButton gridButton;
	private TabulatorWrapper gwTable;
	private final FlowPanel tabulatorContainer = new FlowPanel();
	private LoadingSpinner loadingSpinner = new LoadingSpinner(true);
	private Map<String, Integer> rowIdHipsMap = new HashMap<>();
	private boolean blockOpenHipsTrigger = false;
	private boolean gridHasBeenDeactivatedByUserThroughGwPanel = false;
	private boolean dataHasLoaded = false;
	private List<String> columnsToAlwaysHide = Arrays.asList(
			"stcs50",
			"stcs90",
			"gravitational_waves_oid",
			"group_id",
			"hardware_inj",
			"internal",
			"open_alert",
			"pkt_ser_num",
			"search",
			"packet_type",
			"ra",
			"dec");
	
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
		
		initGwDescriptor();
		
		initView();
		MainLayoutPanel.addMainAreaResizeHandler(event -> setMaxSize());
		CommonEventBus.getEventBus().addHandler(HipsNameChangeEvent.TYPE, changeEvent -> {
			Integer rowId = rowIdHipsMap.get(changeEvent.getHiPSName());
			if(rowId != null) {
				if(!gwTable.isSelected(rowId)) {
					gwTable.deselectAllRows();
					blockOpenHipsTrigger = true;
					gwTable.selectRow(rowId, false);
					blockOpenHipsTrigger = false;
				}
				if(!gridHasBeenDeactivatedByUserThroughGwPanel) {
					AladinLiteWrapper.getInstance().toggleGrid(true);
				}
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
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		setMaxSize();
	}
	
	private void loadData() {
        gwTable = new TabulatorWrapper("gwPanel__tabulatorContainer", GwPanel.this);
        gwTable.setDefaultQueryMode();

        for(MetadataDescriptor md : gwDescriptor.getMetadata()) {
        	if(md.getVisible()) {
        		defaultVisibleColumns.add(md.getTapName());
        	}
        }

		gwTable.setData(EsaSkyWebConstants.TAP_CONTEXT 
				+ "/tap/sync?request=doQuery&lang=ADQL&format=json&query=select+*+from+" 
				+ gwDescriptor.getTapTable());
	}

	private class Tab extends FocusPanel{
		private Label label;
		public Tab(String name) {
			FlowPanel tabContainer = new FlowPanel();
			//TODO internationalization
			label = new Label(name);
			label.addStyleName("gwPanel__tabLabel");
			tabContainer.add(label);
			this.add(tabContainer);
			addStyleName("gwPanel__tab");
		}
		
		public void setSelectedStyle() {
			this.removeStyleName("gwPanel__tabDeselected");
			this.addStyleName("gwPanel__tabSelected");
		}
		
		public void setDeselectedStyle() {
			this.removeStyleName("gwPanel__tabSelected");
			this.addStyleName("gwPanel__tabDeselected");
		}
	}
	
	private Widget createTabBar() {
		FlowPanel tabs = new FlowPanel();
		tabs.addStyleName("gwPanel__tabs");
		//TODO texts
		Tab gwTab = new Tab("Gravitational Waves");
		gwTab.addClickHandler(event ->{
			gwTab.setSelectedStyle();
			neutrinoTab.setDeselectedStyle();
		});
		gwTab.setSelectedStyle();
		tabs.add(gwTab);
		
		//TODO texts
		neutrinoTab = new Tab("Neutrinos");
		neutrinoTab.addClickHandler(event ->{
			neutrinoTab.setSelectedStyle();
			gwTab.setDeselectedStyle();
		});
		neutrinoTab.setDeselectedStyle();
		tabs.add(neutrinoTab);
		
		return tabs;
	}
	
	private Widget createButtons() {
		FlowPanel buttonContainer = new FlowPanel();
		gridButton = new EsaSkyToggleButton(Icons.getGridIcon());
		gridButton.setMediumStyle();
		//TODO add tooltip
		buttonContainer.add(gridButton);
		gridButton.addClickHandler(event->{
			AladinLiteWrapper.getInstance().toggleGrid();
			gridHasBeenDeactivatedByUserThroughGwPanel = true;
		});

		
		ChangeableIconButton expandOrCollapseColumnsButton = new ChangeableIconButton(Icons.getExpandIcon(), Icons.getContractIcon());
		expandOrCollapseColumnsButton.setMediumStyle();
		buttonContainer.add(expandOrCollapseColumnsButton);
		//TODO add tooltip
		expandOrCollapseColumnsButton.addClickHandler(event -> {
			if(!dataHasLoaded) {
				return;
			}
			if(isExpanded) {
				showOnlyBaseColumns();
				expandOrCollapseColumnsButton.setPrimaryIcon();
			} else {
				showAllColumns();
				expandOrCollapseColumnsButton.setSecondaryIcon();
			}
			isExpanded = !isExpanded;
			
			//gwt button bug - Button moved, so hover style is not always removed
			Scheduler.get().scheduleFinally(() -> expandOrCollapseColumnsButton.removeGwtHoverCssClass());
		});
		expandOrCollapseColumnsButton.addStyleName("gwPanel_showMoreButton");
		buttonContainer.addStyleName("gwPanel_buttonContainer");
		return buttonContainer;
	}
	
	private void initView() {
		this.getElement().addClassName("gwPanel");

		//TODO texts
		PopupHeader header = new PopupHeader(this, "Astronomical Events", 
				TextMgr.getInstance().getText("publicationPanel_helpText"), 
				TextMgr.getInstance().getText("publicationPanel_title"));

		gwPanel.add(header);
		
		FlowPanel rowAboveTable = new FlowPanel();
		rowAboveTable.add(createTabBar());
		rowAboveTable.add(createButtons());
		rowAboveTable.addStyleName("gwPanel_headerRow");
		gwPanel.add(rowAboveTable);
		
		tabulatorContainer.getElement().setId("gwPanel__tabulatorContainer");
		gwPanel.add(tabulatorContainer);
		
		CommonEventBus.getEventBus().addHandler(GridToggledEvent.TYPE, event -> {
				gridButton.setToggleStatus(event.isGridActive());
				if(isShowing() && !event.isGridActive()) {
					gridHasBeenDeactivatedByUserThroughGwPanel = true;
				}
			}
		);
		loadingSpinner.setVisible(false);
		loadingSpinner.addStyleName("gwPanel_loadingHipsProperties");
		gwPanel.add(loadingSpinner);
		
		this.add(gwPanel);
	}
	
	private void showAllColumns() {
		gwTable.blockRedraw();
		for(MetadataDescriptor md : gwDescriptor.getMetadata()) {
			if(!md.getVisible() && !columnsToAlwaysHide.contains(md.getTapName())) {
				gwTable.showColumn(md.getTapName());
			}
		}
		gwTable.restoreRedraw();
		gwTable.redrawAndReinitializeHozVDom();
	}
	
	private void showOnlyBaseColumns() {
		gwTable.blockRedraw();
		for(MetadataDescriptor md : gwDescriptor.getMetadata()) {
			if(!defaultVisibleColumns.contains(md.getTapName())) {
				gwTable.hideColumn(md.getTapName());
			} else {
				gwTable.showColumn(md.getTapName());
			}
		}
		gwTable.restoreRedraw();
		gwTable.redrawAndReinitializeHozVDom();
	}
	
	
	private void setMaxSize() {
		Style elementStyle = getElement().getStyle();
		elementStyle.setPropertyPx("maxWidth", MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15);
		elementStyle.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15);
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
		setMaxSize();
	}

	@Override
	public void onTableHeightChanged() {
		//No reason to do anything
	}

	@Override
	public void onRowSelection(GeneralJavaScriptObject row) {
		GeneralJavaScriptObject rowData = row.invokeFunction("getData");
		String id = rowData.getStringProperty("grace_id");
		
		if(!blockOpenHipsTrigger) {
			//TODO do not use skiesdev
			testParsingHipsList("http://skiesdev.esac.esa.int/GW/" + id, GeneralJavaScriptObject.convertToInteger(rowData.getProperty("id")));
			String ra = rowData.getStringProperty(gwDescriptor.getTapRaColumn());
			String dec = rowData.getStringProperty(gwDescriptor.getTapDecColumn());
			AladinLiteWrapper.getInstance().goToTarget(ra, dec, 180, false, CoordinatesFrame.J2000.getValue());
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_GW, GoogleAnalytics.ACT_GW_ROW_SELECTED, id);
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
		//No reason to do anything
	}

	@Override
	public void onRowMouseLeave(int rowId) {
		//No reason to do anything
	}

	@Override
	public void onFilterChanged(String label, String filter) {
		//No reason to do anything
	}

	@Override
	public void onDataFiltered(List<Integer> filteredRows) {
		//No reason to do anything
	}

	@Override
	public void onDatalinkClicked(GeneralJavaScriptObject javaScriptObject) {
		//Column not visible
	}

	@Override
	public void onAccessUrlClicked(String url) {
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_DOWNLOADROW, "GravitationalWaves", url);
		UrlUtils.openUrl(url);
	}

	@Override
	public void onPostcardUrlClicked(GeneralJavaScriptObject rowData, String columnName) {
		//Column not visible
	}

	@Override
	public void onCenterClicked(GeneralJavaScriptObject rowData) {
		//Column not visible
	}

	@Override
	public void onSendToVoApplicaitionClicked(GeneralJavaScriptObject rowData) {
		//Column not visible
	}

	@Override
	public void onLink2ArchiveClicked(GeneralJavaScriptObject row) {
		String url = row.invokeFunction("getData").getStringProperty("event_page");
		GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_OUTBOUND, GoogleAnalytics.ACT_OUTBOUND_CLICK, url);
        UrlUtils.openUrl(url);
	}

	@Override
	public void onSourcesInPublicationClicked(GeneralJavaScriptObject rowData) {
		//Column not visible
	}

	@Override
	public void onAddHipsClicked(GeneralJavaScriptObject rowData) {
		//Column not visible
	}

	@Override
	public void onAjaxResponse() {
		dataHasLoaded = true;
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
		return gwDescriptor.getMetaDataJSONObject();
	}
	
	@Override
	public String getRaColumnName() {
		return gwDescriptor.getTapRaColumn();
	}

	@Override
	public String getDecColumnName() {
		return gwDescriptor.getTapDecColumn();
	}

	@Override
	public boolean isMOCMode() {
		return false;
	}

	@Override
	public String getEsaSkyUniqId() {
		return gwDescriptor.getDescriptorId();
	}

	@Override
	public void multiSelectionInProgress() {
		//No reason to do anything
	}

	@Override
	public void multiSelectionFinished() {
		//No reason to do anything
	}

	@Override
	public boolean hasBeenClosed() {
		return false;
	}
}
