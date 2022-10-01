package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.registry.TapRegistrySelectEvent;
import esac.archive.esasky.cl.web.client.model.Size;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class AdqlPopupPanel extends MovableResizablePanel<AdqlPopupPanel> {

    private FlowPanel container;

    private EsaSkyStringButton searchButton;
    private TextArea adqlTextBox;
    private PopupHeader<AdqlPopupPanel> header;
    private final AdqlPopupPanel.Resources resources;
    private final CssResource style;
    private String adqlTextInput;

    private String tapServiceUrl;
    private String tapTableName;


    public interface Resources extends ClientBundle {
        @Source("adqlPopupPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }
    public AdqlPopupPanel(String googleEventCategory, boolean isSuggestedPositionCenter) {
        super(googleEventCategory, isSuggestedPositionCenter);
        this.resources = GWT.create(AdqlPopupPanel.Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        initView();
        this.hide();
    }

    public void initView() {
        this.addStyleName("adqlPopupPanel__container");
        container = new FlowPanel();
        header = new PopupHeader<>(this, "header",
                "helptext",
                "help title",
                event -> hide(), "Close panel");
        MainLayoutPanel.addMainAreaResizeHandler(event -> setDefaultSize());

        adqlTextBox = new TextArea();
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
        String url = EsaSkyWebConstants.EXT_TAP_URL  + "?"
                + EsaSkyConstants.EXT_TAP_ACTION_FLAG + "=" + EsaSkyConstants.EXT_TAP_ACTION_REQUEST + "&"
                + EsaSkyConstants.EXT_TAP_ADQL_FLAG + "=" + adqlTextInput + "&"
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
                descriptor.setResponseFormat("VOTable");
                descriptor.setPrimaryColor(ESASkyColors.getNext());
                descriptor.setFovLimit(180.0);
                descriptor.setShapeLimit(10);
                descriptor.setInBackend(false);
                descriptor.setUseUcd(true);
                descriptor.setDateADQL(null);
                descriptor.setSearchFunction("");
                CommonEventBus.getEventBus().fireEvent(new TapRegistrySelectEvent(descriptor, obj));
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
        setAdqlQuery("SELECT TOP 50 * FROM " + tableName);
    }

    public void setAdqlQuery(String query) {
        this.adqlTextBox.setText(query);
        adqlTextInput = query;
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
    protected Element getMovableElement() {
        return header.getElement();
    }

    @Override
    protected void onResize() {

    }

    @Override
    protected Element getResizeElement() {
        return container.getElement();
    }
}
