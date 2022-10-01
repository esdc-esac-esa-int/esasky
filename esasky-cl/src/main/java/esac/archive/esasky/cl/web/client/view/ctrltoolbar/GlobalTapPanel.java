package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.registry.TapRegistrySelectEvent;
import esac.archive.esasky.cl.web.client.model.Size;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.DefaultTabulatorCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class GlobalTapPanel extends MovableResizablePanel<GlobalTapPanel> {

    private FlowPanel container;
    private PopupHeader<GlobalTapPanel> header;
    private final GlobalTapPanel.Resources resources;
    private CssResource style;
    TabulatorWrapper tabulatorTable;
    private FlowPanel tabulatorContainer;
    private AdqlPopupPanel adqlPopupPanel;


    public interface Resources extends ClientBundle {
        @Source("globalTapPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public GlobalTapPanel(String googleEventCategory, boolean isSuggestedPositionCenter) {
        super(googleEventCategory, isSuggestedPositionCenter);
        this.resources = GWT.create(GlobalTapPanel.Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();

        initView();
        setMaxSize();
        MainLayoutPanel.addMainAreaResizeHandler(event -> setDefaultSize());
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        this.addSingleElementAbleToInitiateMoveOperation(header.getElement());
    }

    private void initView() {
        container = new FlowPanel();
        tabulatorContainer = new FlowPanel();
        tabulatorContainer.getElement().setId("browseTap__tabulatorContainer");
        this.addStyleName("globalTapPanel__container");

        header = new PopupHeader<>(this, "header",
                "helptext",
                "help title",
                event -> hide(), "Close panel");


        container.add(header);
        container.add(tabulatorContainer);
        container.getElement().setId("globalTapPanelContainer");

        adqlPopupPanel = new AdqlPopupPanel("test", true);
        adqlPopupPanel.addCloseHandler(event -> setGlassEnabled(false));
        adqlPopupPanel.addOpenHandler(event -> setGlassEnabled(true));
        MainLayoutPanel.addElementToMainArea(adqlPopupPanel);
        adqlPopupPanel.setSuggestedPositionCenter();
        this.add(container);
    }

    public void onJsonLoaded(String jsonString) {
        GeneralJavaScriptObject obj =  GeneralJavaScriptObject.createJsonObject(jsonString);
        TabulatorSettings settings = new TabulatorSettings();
        settings.setAddAdqlColumn(true);
        settings.setIsDownloadable(false);
        tabulatorTable = new TabulatorWrapper("browseTap__tabulatorContainer", new TabulatorCallback(), settings);
        tabulatorTable.groupByColumn("res_title");
        tabulatorTable.insertExternalTapData(obj.getProperty("data"), obj.getProperty("columns"));
        tabulatorTable.restoreRedraw();
        tabulatorTable.redrawAndReinitializeHozVDom();
    }

    private void loadData() {
        JSONUtils.getJSONFromUrl(EsaSkyWebConstants.TAPREGISTRY_URL, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                onJsonLoaded(responseText);
                setDefaultSize();
            }
            @Override
            public void onError(String errorCause) {
                int a = 1+2;
            }
        });
    }

    private class TabulatorCallback extends DefaultTabulatorCallback {

        @Override
        public void onRowSelection(GeneralJavaScriptObject row) {
            GeneralJavaScriptObject rowData =  row.invokeFunction("getData");
            String tableName = rowData.getStringProperty("table_name");
            String accessUrl = rowData.getStringProperty("access_url");
            String url = EsaSkyWebConstants.EXT_TAP_URL  + "?"
                    + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST + "&"
                    + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + "SELECT top 1 * from " + tableName + "&"
                    + EsaSkyConstants.EXT_TAP_URL + "=" + accessUrl;
            JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {

                @Override
                public void onSuccess(String responseText) {
                    GeneralJavaScriptObject obj =  GeneralJavaScriptObject.createJsonObject(responseText);
                    GeneralJavaScriptObject meta = obj.hasProperty("metadata") ? obj.getProperty("metadata") : obj.getProperty("columns");
                    ExtTapDescriptor descriptor = DescriptorRepository.getInstance().getExtTapDescriptors().getDescriptors().get(0);
                    descriptor.setGuiShortName(tableName);
                    descriptor.setGuiLongName(tableName);
                    descriptor.setTapUrl(accessUrl + "/sync");
                    descriptor.setResponseFormat("VOTable");
                    descriptor.setTapTable(tableName);
                    descriptor.setTapTableMetadata(meta);
                    descriptor.setSelectADQL("SELECT *");
                    descriptor.setPrimaryColor(ESASkyColors.getNext());
                    descriptor.setFovLimit(180.0);
                    descriptor.setShapeLimit(10);
                    descriptor.setInBackend(false);
                    descriptor.setUseUcd(true);
                    descriptor.setDateADQL(null);
                    if (descriptor.getTapRaColumn().isEmpty() || descriptor.getTapDecColumn().isEmpty()) {
                        descriptor.setSearchFunction("");
                        descriptor.setSelectADQL("SELECT TOP 50 *");
                    } else {
                        descriptor.setSearchFunction("cointainsPoint");
                    }

                    CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(descriptor));
                }

                @Override
                public void onError(String errorCause) {
                    // TODO: Handle
                }
            });
        }

        @Override
        public void onAdqlButtonPressed(GeneralJavaScriptObject rowData) {
            adqlPopupPanel.setTapServiceUrl(rowData.getStringProperty("access_url"));
            adqlPopupPanel.setTapTable(rowData.getStringProperty("table_name"));
            showAdqlPanel();
        }
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
    public void setMaxSize() {
        Style elementStyle = container.getElement().getStyle();
        int maxWidth = MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15;
        elementStyle.setPropertyPx("maxWidth", maxWidth);
        elementStyle.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15);
        setMaxHeight();
    }

    private void setMaxHeight() {
        int headerSize = header.getOffsetHeight();
        int height = container.getOffsetHeight() - headerSize - 5;

        if (height > MainLayoutPanel.getMainAreaHeight()) {
            height = MainLayoutPanel.getMainAreaHeight() - headerSize - 5;
        }

        tabulatorContainer.getElement().getStyle().setPropertyPx("height", height);
    }



    private void setDefaultSize() {
        Size size = getDefaultSize();
        container.setWidth(size.width + "px");
        container.setHeight(size.height + "px");

        Style containerStyle = container.getElement().getStyle();
        containerStyle.setPropertyPx("minWidth", 350);
        containerStyle.setPropertyPx("minHeight", 300);
    }

    @Override
    protected Element getResizeElement() {
        return container.getElement();
    }

    @Override
    public void show() {
        super.show();
        loadData();
    }

    public void showAdqlPanel() {
        adqlPopupPanel.setSuggestedPositionCenter();
        adqlPopupPanel.show();
        adqlPopupPanel.setFocus(true);
        adqlPopupPanel.updateZIndex();
    }

    public void hideAdqlPanel() {
        adqlPopupPanel.hide();
    }

}


