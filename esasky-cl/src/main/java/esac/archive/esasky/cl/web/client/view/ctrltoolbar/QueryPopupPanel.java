package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import esac.archive.esasky.cl.web.client.event.exttap.QueryTapEvent;
import esac.archive.esasky.cl.web.client.event.exttap.QueryTapEventHandler;
import esac.archive.esasky.cl.web.client.utility.ExtTapUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.Hidable;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MovablePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;

public class QueryPopupPanel extends MovablePanel implements Hidable<QueryPopupPanel> {

    private FlowPanel container;

    private EsaSkyStringButton searchButton;
    private TextArea queryTextBox;
    private PopupHeader<QueryPopupPanel> header;
    private final QueryPopupPanel.Resources resources;
    private final CssResource style;
    private String queryTextBoxValue;

    private String tapServiceUrl;
    private String tapTableName;
    private String tapDescription;
    private boolean isShowing = false;

    private DropDownMenu<PopupMenuItems> dropDownMenu;

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
        header = new PopupHeader<>(this, "Custom ADQL Query", "helptext", "help title");
        MainLayoutPanel.addMainAreaResizeHandler(event -> setDefaultSize());

        FlowPanel textBoxContainer = new FlowPanel();
        textBoxContainer.setStyleName("queryPopupPanel__textBoxContainer");
        queryTextBox = new TextArea();
        queryTextBox.setStyleName("queryPopupPanel__textBox");
        queryTextBox.addValueChangeHandler(event -> {
            queryTextBoxValue = event.getValue();
        });


        textBoxContainer.add(queryTextBox);

        searchButton = new EsaSkyStringButton("Run Query");
        searchButton.setMediumStyle();
        searchButton.setStyleName("queryPopupPanel__queryButton");
        searchButton.addClickHandler(event -> {
            this.hide();
            this.fireEvent(new QueryTapEvent(this.tapServiceUrl, this.tapTableName, this.tapDescription, this.queryTextBoxValue));
        });

        container.add(header);
        container.add(createDropdownMenu());
        container.add(textBoxContainer);
        container.add(searchButton);
        this.addSingleElementAbleToInitiateMoveOperation(header.getElement());
        this.add(container);
    }


    private DropDownMenu<PopupMenuItems> createDropdownMenu() {
        dropDownMenu = new DropDownMenu<>("Examples...", "Query examples", 550, "queryPopupPanelDropdownMenu");
        MenuItem<PopupMenuItems> menuItem1 = new MenuItem<>(PopupMenuItems.METADATA, "Columns in table", true);
        MenuItem<PopupMenuItems> menuItem2 = new MenuItem<>(PopupMenuItems.TABLE, "Full table", true);
        MenuItem<PopupMenuItems> menuItem3 = new MenuItem<>(PopupMenuItems.COUNT, "Count rows", true);

        dropDownMenu.addMenuItem(menuItem1);
        dropDownMenu.addMenuItem(menuItem2);
        dropDownMenu.addMenuItem(menuItem3);



        dropDownMenu.registerObserver(() -> {
            switch (dropDownMenu.getSelectedObject()) {
                case METADATA:
                    setQuery("SELECT * FROM TAP_SCHEMA.columns WHERE table_name = '" + tapTableName + "'");
                    break;
                case TABLE:
                    setQuery("SELECT TOP 1000 * FROM " + tapTableName); // Default query
                    break;
                case COUNT:
                    setQuery("SELECT COUNT(*) FROM " + tapTableName);
                    break;

            }
        });

        return dropDownMenu;
    }


    private enum PopupMenuItems{
        METADATA, TABLE, COUNT;
    }

    public void setTapServiceUrl(String tapServiceUrl) {
        this.tapServiceUrl = tapServiceUrl;
    }

    public void setTapTable(String tableName) {
        this.tapTableName = ExtTapUtils.encapsulateTableName(tableName);
        dropDownMenu.clearSelection();
        dropDownMenu.selectObject(PopupMenuItems.TABLE);
    }

    public void setTapDescription(String description) {
        this.tapDescription = description;
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

    public HandlerRegistration addQueryHandler(QueryTapEventHandler handler) {
        return addHandler(handler, QueryTapEvent.TYPE);
    }

    @Override
    public void show(){
        isShowing = true;
        this.removeStyleName("displayNone");
        updateHandlers();
        setMaxSize();
        ensureDialogFitsInsideWindow();
        OpenEvent.fire(this, null);

        setDefaultSize();
        this.setFocus(true);
        this.updateZIndex();

        MainLayoutPanel.addElementToMainArea(this);
    }

    @Override
    public void hide() {
        isShowing = false;
        this.addStyleName("displayNone");
        this.removeHandlers();
        CloseEvent.fire(this, null);

        MainLayoutPanel.removeElementFromMainArea(this);
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }

}
