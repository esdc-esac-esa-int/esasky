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
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GwPanel extends MovableResizablePanel<GwPanel> {
    private GwDescriptor gwDescriptor;
    private BaseDescriptor iceCubeDescriptor;
    private EsaSkyEntity gwEntity50;
    private EsaSkyEntity gwEntity90;
    private EsaSkyEntity iceCubeEntity;

    private final FlowPanel mainContainer = new FlowPanel();
    private FlowPanel tableHeaderTabRow;
    private PopupHeader<GwPanel> header;
    private TabLayoutPanel tabLayoutPanel;
    EsaSkyToggleButton gridButton;

    private final Map<String, Integer> rowIdHipsMap = new HashMap<>();
    private static final String GRACE_ID = "grace_id";
    private boolean isGridDisabled = false;

    private enum TabIndex {GW, NEUTRINO}

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
                TextMgr.getInstance().getText("gwPanel_helpTitle"));

        mainContainer.add(header);
        mainContainer.getElement().setId("gwPanelContainer");


        tableHeaderTabRow = new FlowPanel();

        gridButton = new EsaSkyToggleButton(Icons.getGridIcon());
        gridButton.setSmallStyle();
        gridButton.setTitle(TextMgr.getInstance().getText("header_gridFull"));
        gridButton.addClickHandler(event -> {
            AladinLiteWrapper.getInstance().toggleGrid();
            isGridDisabled = !gridButton.getToggleStatus();
        });

        header.addActionWidget(gridButton);

        tableHeaderTabRow.addStyleName("gwPanel_headerRow");

        tabLayoutPanel = new TabLayoutPanel(50, Style.Unit.PX);

        tabLayoutPanel.addBeforeSelectionHandler(event -> {
            if (event.getItem() == TabIndex.GW.ordinal()) {
                if (iceCubeEntity != null) {
                    iceCubeEntity.hideAllShapes();
                }
            } else if (event.getItem() == TabIndex.NEUTRINO.ordinal()) {
                loadNeutrinoData();

                int len = iceCubeEntity.getTablePanel().getAllRows().length;
                iceCubeEntity.showShapes(IntStream.rangeClosed(0, len - 1).boxed().collect(Collectors.toList()));

                if (gwEntity50 != null && gwEntity90 != null) {
                    gwEntity50.removeAllShapes();
                    gwEntity90.hideAllShapes();
                    gwEntity90.getTablePanel().deselectAllRows();

                    SelectSkyPanel.getInstance().removeSky(rowIdHipsMap.keySet().toArray(new String[0]));
                }
            }
        });

        tabLayoutPanel.add(new FlowPanel(), TextMgr.getInstance().getText("gwPanel_gwTab"));
        tabLayoutPanel.add(new FlowPanel(), TextMgr.getInstance().getText("gwPanel_neutrinoTab"));
        mainContainer.add(tabLayoutPanel);

        this.add(mainContainer);
    }

    private void loadNeutrinoData() {
        if (iceCubeDescriptor == null) {
            iceCubeDescriptor = DescriptorRepository.getInstance().getIceCubeDescriptors().getDescriptors().get(0);

            if (iceCubeEntity == null) {
                iceCubeDescriptor.setTapSTCSColumn("stc_s");
                iceCubeEntity = EntityRepository.getInstance().createIceCubeEntity(iceCubeDescriptor);
            }

            Widget tabContentContainer = tabLayoutPanel.getWidget(TabIndex.NEUTRINO.ordinal());
            if (tabContentContainer instanceof FlowPanel) {
                ((FlowPanel) tabContentContainer).add(iceCubeEntity.createTablePanel().getWidget());
            }

            iceCubeEntity.fetchDataWithoutMOC();
            setMaxSize();
        }

    }

    private void loadGwData() {
        if (gwDescriptor == null) {
            gwDescriptor = DescriptorRepository.getInstance().getGwDescriptors().getDescriptors().get(0);

            if (gwEntity90 == null) {
                gwDescriptor.setTapSTCSColumn("stcs90");
                String entityId = gwDescriptor.getDescriptorId() + "_90";
                gwEntity90 = EntityRepository.getInstance().createGwEntity(gwDescriptor, entityId, "dashed");
            }

            if (gwEntity50 == null) {
                gwDescriptor.setTapSTCSColumn("stcs50");
                String entityId = gwDescriptor.getDescriptorId() + "_50";
                gwEntity50 = EntityRepository.getInstance().createGwEntity(gwDescriptor, entityId, "solid");
            }

            gwDescriptor.setTapSTCSColumn("stcs90");
            Widget tabContentContainer = tabLayoutPanel.getWidget(TabIndex.GW.ordinal());
            if (tabContentContainer instanceof FlowPanel) {
                ((FlowPanel) tabContentContainer).add(gwEntity90.createTablePanel().getWidget());
            }

            gwEntity90.getTablePanel().registerObserver(new TableObserver() {
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
                    gwEntity90.hideAllShapes();
                }

                @Override
                public void onRowSelected(GeneralJavaScriptObject row) {
                    GeneralJavaScriptObject rowData = row.invokeFunction("getData");
                    String id = rowData.getStringProperty("grace_id");

                    testParsingHipsList("https://skies.esac.esa.int/GW/" + id, GeneralJavaScriptObject.convertToInteger(rowData.getProperty("id")));
                    String ra = rowData.getStringProperty(gwDescriptor.getTapRaColumn());
                    String dec = rowData.getStringProperty(gwDescriptor.getTapDecColumn());
                    AladinLiteWrapper.getInstance().goToTarget(ra, dec, 180, false, CoordinatesFrame.J2000.getValue());
                    GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_GW, GoogleAnalytics.ACT_GW_ROW_SELECTED, id);

                    gwDescriptor.setTapSTCSColumn("stcs90");
                    gwEntity90.showShape(Integer.parseInt(rowData.getProperty("id").toString()));

                    if (!isGridDisabled) {
                        AladinLiteWrapper.getInstance().toggleGrid(true);
                        gridButton.setToggleStatus(true);
                    }


                    gwDescriptor.setTapSTCSColumn("stcs50");
                    gwEntity50.addShapes(rowData.wrapInArray());
                }

                @Override
                public void onRowDeselected(GeneralJavaScriptObject row) {
                    gwEntity90.hideAllShapes();
                    gwEntity50.removeAllShapes();
                }
            });

            setMaxSize();
            gwEntity90.fetchDataWithoutMOC();
        }
    }


    private void setDefaultSize() {
        Size size = getDefaultSize();
        mainContainer.setWidth(size.width + "px");
        mainContainer.setHeight(size.height + "px");

        Style containerStyle = mainContainer.getElement().getStyle();
        containerStyle.setPropertyPx("minWidth", 150);
        containerStyle.setPropertyPx("minHeight", 100);
    }

    @Override
    public void setMaxSize() {
        Style elementStyle = mainContainer.getElement().getStyle();
        int maxWidth = MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15;
        elementStyle.setPropertyPx("maxWidth", maxWidth);
        elementStyle.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15);
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
        loadGwData();
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
        return gwEntity90.getTablePanel().exportAsJSON(false);
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
        JSONObject data = getAllData();
        for (String key : data.keySet()) {
            JSONObject value = data.get(key).isObject();
            if (value.get(GRACE_ID).toString().equals("\"" + id + "\"")) {
                gwEntity90.getTablePanel().selectRow(Integer.parseInt(key));
            }
        }
    }
}
