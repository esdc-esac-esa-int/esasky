package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.hips.HipsAddedEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.Size;
import esac.archive.esasky.cl.web.client.model.entities.EsaSkyEntity;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.ChangeableIconButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TableObserver;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.descriptor.BaseDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.GwDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IceCubeDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GwPanel extends MovableResizablePanel<GwPanel> {

    private TabItem[] tabItems = new TabItem[TabIndex.TAB_END.ordinal()];
    private List<Integer> filteredNeutrinoData;

    private final FlowPanel mainContainer = new FlowPanel();
    private FlowPanel tableHeaderTabRow;
    private PopupHeader<GwPanel> header;
    private TabLayoutPanel tabLayoutPanel;
    private ChangeableIconButton expandButton;

    private final Map<String, Integer> rowIdHipsMap = new HashMap<>();
    public static final String GRACE_ID = "grace_id";

    public static enum TabIndex {GW, NEUTRINO, TAB_END}
    
    private int currentActiveTabIndex;

    private final Resources resources;
    private CssResource style;

    public interface Resources extends ClientBundle {
        @Source("gw.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public GwPanel() {
        super(GoogleAnalytics.CAT_GW, false);
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();

        initView();
        setMaxSize();
        MainLayoutPanel.addMainAreaResizeHandler(event -> setDefaultSize());
    }


    private void initView() {
        this.getElement().addClassName("gwPanel");

        header = new PopupHeader<>(this, TextMgr.getInstance().getText("gwPanel_header"),
                TextMgr.getInstance().getText("gwPanel_helpText"),
                TextMgr.getInstance().getText("gwPanel_helpTitle"),
                event -> close(), "");

        mainContainer.add(header);
        mainContainer.getElement().setId("gwPanelContainer");


        tableHeaderTabRow = new FlowPanel();

        expandButton = new ChangeableIconButton(Icons.getExpandIcon(), Icons.getContractIcon());
        expandButton.setSmallStyle();
        expandButton.setTitle(TextMgr.getInstance().getText("gwPanel_showMoreColumns"));
        expandButton.addClickHandler(event -> {
            TabItem tabItem = getActiveTabItem();
            if (tabItem != null) {
                tabItem.toggleExpand();
                updateExpandedButton(tabItem);
                updatePanelWidth(tabItem);
            }
        });

        EsaSkyButton columnSettings = new EsaSkyButton(Icons.getSettingsIcon());
        columnSettings.setSmallStyle();
        columnSettings.setTitle(TextMgr.getInstance().getText("gwPanel_showMoreColumns"));

        columnSettings.addClickHandler(event -> {
            TabItem tabItem = getActiveTabItem();
            if (tabItem == null || !tabItem.dataLoaded()) {
                return;
            }

            tabItem.getEntity().getTablePanel().openConfigurationPanel();
        });

        header.addActionWidget(columnSettings);
        header.addActionWidget(expandButton);

        tableHeaderTabRow.addStyleName("gwPanel_headerRow");

        tabLayoutPanel = new TabLayoutPanel(50, Style.Unit.PX);

        tabLayoutPanel.addBeforeSelectionHandler(event -> changeTab(event.getItem()));
        tabLayoutPanel.add(new FlowPanel(), TextMgr.getInstance().getText("gwPanel_gwTab"));
        tabLayoutPanel.add(new FlowPanel(), TextMgr.getInstance().getText("gwPanel_neutrinoTab"));
        mainContainer.add(tabLayoutPanel);

        this.add(mainContainer);
    }

    public void changeTab(int tabIndex) {
    	if(tabIndex == currentActiveTabIndex) {
    		return;
    	}
    	currentActiveTabIndex = tabIndex;

        if (tabIndex == TabIndex.GW.ordinal()) {
            onChangeToGwTab();
        } else if (tabIndex == TabIndex.NEUTRINO.ordinal()) {
            onChangeToNeutrinoTab();
        }
        
    }

    private void onChangeToNeutrinoTab() {
        loadNeutrinoData();
        TabItem neutrinoTab = getTabItem(TabIndex.NEUTRINO);
        if (neutrinoTab != null) {
            neutrinoTab.open();
            if (filteredNeutrinoData == null) {
                int len = neutrinoTab.getTablePanel().getAllRows().length;
                neutrinoTab.getEntity().showShapes(IntStream.rangeClosed(0, len - 1).boxed().collect(Collectors.toList()));
            } else {
                neutrinoTab.getEntity().showShapes(filteredNeutrinoData);
            }
            updateExpandedButton(neutrinoTab);
        
        	tabLayoutPanel.selectTab(TabIndex.NEUTRINO.ordinal());
        	neutrinoTab.getTablePanel().selectTablePanel();
            updatePanelWidth(neutrinoTab);
            AladinLiteWrapper.getInstance().toggleGrid(true);
        }
    }

    private void onChangeToGwTab() {
        TabItem gwTab = getTabItem(TabIndex.GW);
        if (gwTab != null) {
            gwTab.open();
            updateExpandedButton(gwTab);
            
        	tabLayoutPanel.selectTab(TabIndex.GW.ordinal());
        	gwTab.getTablePanel().selectTablePanel();
            updatePanelWidth(gwTab);
        }

    }

    private TabItem getTabItem(TabIndex index) {
        if (tabItems.length > index.ordinal()) {
            return tabItems[index.ordinal()];
        } else {
            return null;
        }
    }

    private TabItem getActiveTabItem() {
        int index = tabLayoutPanel.getSelectedIndex();
        return getTabItem(TabIndex.values()[index]);
    }

    private void updateExpandedButton(TabItem tabItem) {
        if (tabItem != null && tabItem.isExpanded()) {
            expandButton.setSecondaryIcon();
        } else {
            expandButton.setPrimaryIcon();
        }
    }

    private void updatePanelWidth(TabItem tabItem) {
        int columnWidth = tabItem.getTablePanel().getVisibleColumnsWidth() + 15;
        if (tabItem.isExpanded && columnWidth > 0) {
            this.mainContainer.setWidth(columnWidth + "px");
        } else {
            setDefaultSize();
        }
    }

    private void setTabItem(TabIndex index, TabItem item) {
        tabItems[index.ordinal()] = item;
    }

    private void loadNeutrinoData() {
        if (getTabItem(TabIndex.NEUTRINO) == null) {
            IceCubeDescriptor descriptor = DescriptorRepository.getInstance().getIceCubeDescriptors().getDescriptors().get(0);

            descriptor.setTapSTCSColumn("stc_s");
            descriptor.setArchiveColumn("event_page");
            EsaSkyEntity entity = EntityRepository.getInstance().createIceCubeEntity(descriptor);

            Widget tabContentContainer = tabLayoutPanel.getWidget(TabIndex.NEUTRINO.ordinal());
            if (tabContentContainer instanceof FlowPanel) {
                ((FlowPanel) tabContentContainer).add(entity.createTablePanel().getWidget());
            }

            List<String> columnsToHide = Arrays.asList("stc_s","title","sun_postn_ra","sun_postn_dec","sun_dist_deg",
                    "stc_error","stc_error50","ra_current","ra_1950","moon_postn_ra","moon_postn_dec","moon_dist",
                    "ecl_coords_lat","ecl_coords_lon","gal_coords_lat","gal_coords_lon","discovery_date","discovery_time");

            TabItem tabItem = new TabItem(descriptor, columnsToHide, entity);

            entity.getTablePanel().registerObserver(new TableObserver() {
                @Override
                public void numberOfShownRowsChanged(int numberOfShownRows) {
                    // Not needed here
                }

                @Override
                public void onSelection(ITablePanel selectedTablePanel) {
                    // Not needed here
                }

                @Override
                public void onUpdateStyle(ITablePanel panel) {
                    // Not needed here
                }

                @Override
                public void onDataLoaded(int numberOfRows) {
                    tabItem.setDataLoaded(true);
                }

                @Override
                public void onRowSelected(GeneralJavaScriptObject row) {
                    // Not needed here
                }

                @Override
                public void onRowDeselected(GeneralJavaScriptObject row) {
                    // Not needed here
                }

                @Override
                public void onDataFilterChanged(List<Integer> filteredIndexList) {
                    filteredNeutrinoData = filteredIndexList;
                }
            });

            entity.fetchDataWithoutMOC();

            setTabItem(TabIndex.NEUTRINO, tabItem);
            setMaxSize();
        }

    }

    private void loadGwData(String idToShow) {
        if (getTabItem(TabIndex.GW) == null) {
            GwDescriptor descriptor = DescriptorRepository.getInstance().getGwDescriptors().getDescriptors().get(0);
            descriptor.setArchiveColumn("event_page");
            descriptor.setTapSTCSColumn("stcs90");
            String entityId = descriptor.getDescriptorId() + "_90";
            EsaSkyEntity entity = EntityRepository.getInstance().createGwEntity(descriptor, entityId, "dashed");

            descriptor.setTapSTCSColumn("stcs50");
            entityId = descriptor.getDescriptorId() + "_50";
            EsaSkyEntity extraEntity = EntityRepository.getInstance().createGwEntity(descriptor, entityId, "solid");

            descriptor.setTapSTCSColumn("stcs90");
            Widget tabContentContainer = tabLayoutPanel.getWidget(TabIndex.GW.ordinal());
            if (tabContentContainer instanceof FlowPanel) {
                ((FlowPanel) tabContentContainer).add(entity.createTablePanel().getWidget());
            }

            List<String> columnsToHide = Arrays.asList("stcs50", "stcs90", "gravitational_waves_oid", "group_id",
                    "hardware_inj", "internal", "open_alert", "pkt_ser_num", "search", "packet_type", "ra", "dec");

            TabItem tabItem = new TabItem(descriptor, columnsToHide, entity, extraEntity);
            entity.getTablePanel().registerObserver(new TableObserver() {
                @Override
                public void numberOfShownRowsChanged(int numberOfShownRows) {
                    entity.hideAllShapes();
                    GeneralJavaScriptObject[] selectedRows = entity.getTablePanel().getSelectedRows();
                    if (selectedRows.length > 0) {
                        String id = selectedRows[0].getStringProperty("id");
                        entity.showShape(Integer.parseInt(id));
                    }
                }

                @Override
                public void onSelection(ITablePanel selectedTablePanel) {
                    // Not needed here
                }

                @Override
                public void onUpdateStyle(ITablePanel panel) {
                    // Not needed here
                }

                @Override
                public void onDataLoaded(int numberOfRows) {
                    tabItem.setDataLoaded(true);
                    entity.hideAllShapes();
                    showEvent(idToShow);
                }

                @Override
                public void onRowSelected(GeneralJavaScriptObject row) {
                    GeneralJavaScriptObject rowData = row.invokeFunction("getData");
                    String id = rowData.getStringProperty("grace_id");

                    testParsingHipsList("https://skies.esac.esa.int/GW/" + id, GeneralJavaScriptObject.convertToInteger(rowData.getProperty("id")));
                    String ra = rowData.getStringProperty(descriptor.getTapRaColumn());
                    String dec = rowData.getStringProperty(descriptor.getTapDecColumn());
                    AladinLiteWrapper.getInstance().goToTarget(ra, dec, 180, false, CoordinatesFrame.J2000.getValue());
                    GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_GW, GoogleAnalytics.ACT_GW_ROW_SELECTED, id);

                    descriptor.setTapSTCSColumn("stcs90");
                    entity.showShape(Integer.parseInt(rowData.getProperty("id").toString()));

                    descriptor.setTapSTCSColumn("stcs50");
                    extraEntity.addShapes(rowData.wrapInArray());
                    AladinLiteWrapper.getInstance().toggleGrid(true);
                }

                @Override
                public void onRowDeselected(GeneralJavaScriptObject row) {
                    entity.hideAllShapes();
                    extraEntity.removeAllShapes();
                    SelectSkyPanel.getInstance().removeSky(rowIdHipsMap.keySet().toArray(new String[0]));
                }
            });

            entity.fetchDataWithoutMOC();
            setTabItem(TabIndex.GW, tabItem);
            setMaxSize();
        }
    }

    private void setDefaultSize() {
        Size size = getDefaultSize();
        mainContainer.setWidth(size.width + "px");
        mainContainer.setHeight(size.height + "px");

        Style containerStyle = mainContainer.getElement().getStyle();
        containerStyle.setPropertyPx("minWidth", 350);
        containerStyle.setPropertyPx("minHeight", 300);
    }

    @Override
    public void setMaxSize() {
        Style elementStyle = mainContainer.getElement().getStyle();
        int maxWidth = MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15;
        int maxHeight = MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15;
        elementStyle.setPropertyPx("maxWidth", maxWidth);
        elementStyle.setPropertyPx("maxHeight", maxHeight);
        setMaxHeight();

    }

    private void setMaxHeight() {
        int headerSize = header.getOffsetHeight() + tableHeaderTabRow.getOffsetHeight();
        int height = mainContainer.getOffsetHeight() - headerSize - 5;

        if (height > MainLayoutPanel.getMainAreaHeight()) {
            height = MainLayoutPanel.getMainAreaHeight() - headerSize - 5;
        }

        tabLayoutPanel.getElement().getStyle().setPropertyPx("height", height);
    }

    @Override
    public void show() {
        super.show();
        loadGwData(null);
        updateExpandedButton(getActiveTabItem());
    }

    private void close() {
        for (TabItem tabItem : tabItems) {
            if (tabItem != null) {
                tabItem.close();
            }
        }

        tabLayoutPanel.selectTab(TabIndex.GW.ordinal());
        AladinLiteWrapper.getInstance().toggleGrid(false);
        SelectSkyPanel.getInstance().removeSky(rowIdHipsMap.keySet().toArray(new String[0]));
        hide();
    }

    @Override
    protected Element getMovableElement() {
        return header.getElement();
    }

    @Override
    protected void onResize() {
        setMaxSize();
    }

    @Override
    protected Element getResizeElement() {
        return mainContainer.getElement();
    }

    private class HipsListParser implements HipsParserObserver {

        private final Integer rowId;
        private final String url;

        private HipsListParser(Integer rowId, String url) {
            this.rowId = rowId;
            this.url = url;
        }

        @Override
        public void onSuccess(HiPS hips) {
            rowIdHipsMap.put(hips.getSurveyName(), rowId);
            hips.setCreator("LIGO Scientifig Collaboration");
            hips.setCreatorURL("https://www.ligo.org/");
            hips.setMission("GraceDB");
            hips.setMissionURL("https://gracedb.ligo.org/superevents/" + hips.getSurveyName() + "/view/");
            hips.setColorPalette(ColorPalette.PLANCK);
            CommonEventBus.getEventBus().fireEvent(new HipsAddedEvent(hips, HipsWavelength.GW, false));
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_GW, GoogleAnalytics.ACT_GW_SHOW_HIPS, url);
        }

        @Override
        public void onError(String errorMsg) {
            String fullErrorText = TextMgr.getInstance().getText("addSky_errorParsingProperties");
            fullErrorText = fullErrorText.replace("$DUE_TO$", errorMsg);

            DisplayUtils.showMessageDialogBox(fullErrorText, TextMgr.getInstance().getText("error").toUpperCase(), UUID.randomUUID().toString(),
                    TextMgr.getInstance().getText("error"));

            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_GW, GoogleAnalytics.ACT_GW_SHOW_HIPS_FAIL, url);
            Log.error(errorMsg);
        }
    }

    private void testParsingHipsList(String url, Integer rowId) {
        HipsParser parser = new HipsParser(new HipsListParser(rowId, url));
        parser.loadProperties(url);
    }

    public JSONArray getIds() {
        JSONArray result = new JSONArray();

        JSONObject data = getAllData();
        for (String key : data.keySet()) {
            JSONObject value = data.get(key).isObject();
            if (value != null && value.containsKey(GRACE_ID)) {
                result.set(result.size(), value.get(GRACE_ID));
            }
        }
        return result;
    }

    public JSONObject getAllData() {
        TabItem tabItem = getTabItem(TabIndex.GW);
        if (tabItem != null) {
            return tabItem.getTablePanel().exportAsJSON(false);
        } else {
            return null;
        }
    }

    public JSONObject getData4Id(String id) {
        JSONObject data = getAllData();
        for (String key : data.keySet()) {
            JSONObject value = data.get(key).isObject();
            if (value.get(GRACE_ID).toString().equals("\"" + id + "\"")) {
                return value;
            }
        }

        throw new IllegalArgumentException();
    }

    public void showEvent(String id) {
        TabItem tabItem = getTabItem(TabIndex.GW);
        if (tabItem != null) {
            JSONObject data = getAllData();
            for (String key : data.keySet()) {
                JSONObject value = data.get(key).isObject();
                if (value.get(GRACE_ID).toString().equals("\"" + id + "\"")) {
                    tabItem.getTablePanel().selectRow(Integer.parseInt(key));
                    break;
                }
            }
        } else {
            loadGwData(id);
        }
    }

    private class TabItem {
        private final BaseDescriptor descriptor;
        private final EsaSkyEntity entity;
        private final EsaSkyEntity extraEntity;
        private boolean dataLoaded = false;
        private final List<String> columnsToHide;

        private boolean isExpanded = false;

        public TabItem(BaseDescriptor descriptor, List<String> columnsToHide, EsaSkyEntity mainEntity) {
            this(descriptor, columnsToHide, mainEntity, null);
        }

        public TabItem(BaseDescriptor descriptor, List<String> columnsToHide, EsaSkyEntity entity, EsaSkyEntity extraEntity) {
            this.descriptor = descriptor;
            this.entity = entity;
            this.extraEntity = extraEntity;
            this.columnsToHide = columnsToHide;
        }

        public BaseDescriptor getDescriptor() {
            return descriptor;
        }

        public EsaSkyEntity getEntity() {
            return entity;
        }

        public EsaSkyEntity getExtraEntity() {
            return extraEntity;
        }

        public ITablePanel getTablePanel() {
            return entity.getTablePanel();
        }

        public void close() {
            entity.hideAllShapes();
            entity.getTablePanel().deselectAllRows();
            EntityRepository.getInstance().removeEntity(entity);
            if (hasExtraEntity()) {
                extraEntity.hideAllShapes();
            }
        }

        public void open() {
            if (!EntityRepository.getInstance().getAllEntities().contains(entity)) {
                EntityRepository.getInstance().addEntity(entity);
            }
        }

        public void toggleExpand() {
            List<String> columns;
            if (isExpanded) {
                columns = this.descriptor.getMetadata().stream()
                        .filter(MetadataDescriptor::getVisible)
                        .map(MetadataDescriptor::getTapName)
                        .collect(Collectors.toList());
            } else {
                columns = this.descriptor.getMetadata().stream()
                        .map(MetadataDescriptor::getTapName)
                        .filter(tapName -> !this.getColumnsToHide().contains(tapName))
                        .collect(Collectors.toList());
            }
            this.entity.getTablePanel().blockRedraw();
            this.entity.getTablePanel().setVisibleColumns(columns);
            this.entity.getTablePanel().restoreRedraw();
            this.entity.getTablePanel().redrawAndReinitializeHozVDom();
            isExpanded = !isExpanded;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public boolean hasExtraEntity() {
            return extraEntity != null;
        }

        public boolean dataLoaded() {
            return dataLoaded;
        }

        public void setDataLoaded(boolean loaded) {
            dataLoaded = loaded;
        }

        public List<String> getColumnsToHide() {
            return this.columnsToHide;
        }

    }
}
