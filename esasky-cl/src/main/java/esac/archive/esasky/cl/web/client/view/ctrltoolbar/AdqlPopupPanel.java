package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.registry.TapRegistrySelectEvent;
import esac.archive.esasky.cl.web.client.model.Size;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.Hidable;
import esac.archive.esasky.cl.web.client.view.common.MovablePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class AdqlPopupPanel extends MovablePanel implements Hidable<PopupPanel> {

    private FlowPanel container;

    private EsaSkyStringButton searchButton;
    private TextBox adqlTextBox;
    private PopupHeader<PopupPanel> header;
    private final AdqlPopupPanel.Resources resources;
    private final CssResource style;
    private boolean isShowing;
    private String adqlTextInput;

    private String tapServiceUrl;
    private String tapTableName;


    public interface Resources extends ClientBundle {
        @Source("adqlPopupPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }
    public AdqlPopupPanel(String googleEventCategory, boolean isSuggestedPositionCenter) {
        super(googleEventCategory, true);
        this.resources = GWT.create(AdqlPopupPanel.Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        initView();
    }

    public void initView() {
        container = new FlowPanel();
        container.addStyleName("adqlPopupPanel__container");
        header = new PopupHeader<>(this, "header",
                "helptext",
                "help title",
                event -> hide(), "Close panel");
        MainLayoutPanel.addMainAreaResizeHandler(event -> setDefaultSize());

        adqlTextBox = new TextBox();
        adqlTextBox.addValueChangeHandler(event -> {
            adqlTextInput = event.getValue();
        });

        searchButton = new EsaSkyStringButton("OK");
        searchButton.setMediumStyle();

        searchButton.addClickHandler(event -> {
            loadExttapData();
        });

        container.add(header);
        container.add(adqlTextBox);
        container.add(searchButton);
        this.addSingleElementAbleToInitiateMoveOperation(header.getElement());
        this.add(container);
    }


    private void loadExttapData() {
//        String url = EsaSkyWebConstants.EXT_TAP_URL  + "?"
//                + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST + "&"
//                + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + adqlTextInput + "&"
//                + EsaSkyConstants.EXT_TAP_URL + "=" + tapServiceUrl;

        String qry = adqlTextInput.replace("SELECT", "SELECT TOP 1");
        qry += "FROM " + tapTableName;
        String url = EsaSkyWebConstants.EXT_TAP_URL  + "?"
                + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST + "&"
                + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + qry + "&"
                + EsaSkyConstants.EXT_TAP_URL + "=" + tapServiceUrl;

        JSONUtils.getJSONFromUrl(url, new JSONUtils.IJSONRequestCallback() {
            @Override
            public void onSuccess(String responseText) {
                GeneralJavaScriptObject obj =  GeneralJavaScriptObject.createJsonObject(responseText);
                GeneralJavaScriptObject meta = obj.getProperty("metadata");
                ExtTapDescriptor descriptor = DescriptorRepository.getInstance().getExtTapDescriptors().getDescriptors().get(0);
                descriptor.setGuiShortName(tapTableName);
                descriptor.setGuiLongName(tapTableName);
                descriptor.setTapUrl(tapServiceUrl + "/sync");
                descriptor.setTapTable(tapTableName);
                descriptor.setTapTableMetadata(meta);
                descriptor.setSelectADQL(adqlTextInput);
//                descriptor.setResponseFormat("votable");
                descriptor.setPrimaryColor(ESASkyColors.getNext());
                descriptor.setFovLimit(180.0);
                descriptor.setShapeLimit(10);
                descriptor.setInBackend(false);
                descriptor.setUseUcd(true);
                descriptor.setDateADQL(null);
                descriptor.setSearchFunction("cointainsPoint");
                CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(descriptor));
            }

            @Override
            public void onError(String errorCause) {
                int a = 1+2;
            }
        });
    }


    public void setTapServiceUrl(String tapServiceUrl) {
        this.tapServiceUrl = tapServiceUrl;
    }

    public String getTapServiceUrl() {
        return tapServiceUrl;
    }

    public void setTapTable(String tableName) {
        this.tapTableName = tableName;
    }

    public  String getTapTable() {
        return this.tapTableName;
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

        container.getElement().getStyle().setPropertyPx("height", height);
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
    public void show() {
        isShowing = true;
        this.removeStyleName("displayNone");
        setDefaultSize();
        ensureDialogFitsInsideWindow();
        updateHandlers();
    }

    @Override
    public void hide() {
        this.addStyleName("displayNone");
        isShowing = false;
        this.removeHandlers();
        CloseEvent.fire(this, null);
    }

    @Override
    public boolean isShowing() {
        return this.isShowing;
    }
}
