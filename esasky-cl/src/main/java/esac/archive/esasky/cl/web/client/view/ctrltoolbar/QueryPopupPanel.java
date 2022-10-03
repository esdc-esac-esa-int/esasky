package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import esac.archive.esasky.cl.web.client.event.exttap.QueryTapEvent;
import esac.archive.esasky.cl.web.client.event.exttap.QueryTapEventHandler;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;

public class QueryPopupPanel extends MovableResizablePanel<QueryPopupPanel> {

    private FlowPanel container;

    private EsaSkyStringButton searchButton;
    private TextArea queryTextBox;
    private PopupHeader<QueryPopupPanel> header;
    private final QueryPopupPanel.Resources resources;
    private final CssResource style;
    private String queryTextBoxValue;

    private String tapServiceUrl;
    private String tapTableName;


    public interface Resources extends ClientBundle {
        @Source("queryPopupPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }
    public QueryPopupPanel(String googleEventCategory, boolean isSuggestedPositionCenter) {
        super(googleEventCategory, isSuggestedPositionCenter);
        this.resources = GWT.create(QueryPopupPanel.Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        initView();
    }

    public void initView() {
        this.addStyleName("queryPopupPanel__container");
        container = new FlowPanel();
        header = new PopupHeader<>(this, "header", "helptext", "help title");
        MainLayoutPanel.addMainAreaResizeHandler(event -> setDefaultSize());

        FlowPanel textBoxContainer = new FlowPanel();
        textBoxContainer.setStyleName("queryPopupPanel__textBoxContainer");
        queryTextBox = new TextArea();
        queryTextBox.setStyleName("queryPopupPanel__textBox");
        queryTextBox.addValueChangeHandler(event -> {
            queryTextBoxValue = event.getValue();
        });

        textBoxContainer.add(queryTextBox);

        searchButton = new EsaSkyStringButton("Execute");
        searchButton.setMediumStyle();

        searchButton.addClickHandler(event -> {
            this.hide();
            this.fireEvent(new QueryTapEvent(this.tapServiceUrl, this.tapTableName, this.queryTextBoxValue));
        });

        container.add(header);
        container.add(textBoxContainer);
        container.add(searchButton);
        this.addSingleElementAbleToInitiateMoveOperation(header.getElement());
        this.add(container);
    }

    public void setTapServiceUrl(String tapServiceUrl) {
        this.tapServiceUrl = tapServiceUrl;
    }

    public void setTapTable(String tableName) {
        this.tapTableName = tableName;
        setQuery("SELECT TOP 50 * FROM " + tableName); // Default query
    }

    public void setQuery(String query) {
        this.queryTextBox.setText(query);
        this.queryTextBoxValue = query;
    }

    private void setDefaultSize() {
        Style containerStyle = container.getElement().getStyle();
        containerStyle.setPropertyPx("minWidth", 550);
        containerStyle.setPropertyPx("minHeight", 150);
    }

    @Override
    protected Element getMovableElement() {
        return header.getElement();
    }

    @Override
    protected Element getResizeElement() {
        return container.getElement();
    }

    public HandlerRegistration addQueryHandler(QueryTapEventHandler handler) {
        return addHandler(handler, QueryTapEvent.TYPE);
    }

    @Override
    public void show(){
        setDefaultSize();
        super.show();
    }
}
