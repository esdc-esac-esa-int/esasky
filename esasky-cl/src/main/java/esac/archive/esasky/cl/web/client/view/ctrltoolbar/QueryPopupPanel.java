/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import esac.archive.esasky.cl.web.client.event.exttap.QueryTapEvent;
import esac.archive.esasky.cl.web.client.event.exttap.QueryTapEventHandler;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.ExtTapUtils;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.BaseMovablePopupPanel;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;

public class QueryPopupPanel extends BaseMovablePopupPanel {

    private TextArea queryTextBox;
    private final Resources resources;
    private final CssResource style;
    private String queryTextBoxValue;
    private String tapServiceUrl;
    private String tapTableName;
    private String tapDescription;
    private String publisher;

    private DropDownMenu<PopupMenuItems> dropDownMenu;

    public interface Resources extends ClientBundle {
        @Source("queryPopupPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }
    public QueryPopupPanel() {
        this(true);
    }

    public QueryPopupPanel(boolean showExamples) {
        super(GoogleAnalytics.CAT_GLOBALTAP_ADQLPANEL,
                TextMgr.getInstance().getText("global_tap_panel_custom_query_title"),
                TextMgr.getInstance().getText("global_tap_panel_custom_query_help"));
        this.resources = GWT.create(QueryPopupPanel.Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        initView(showExamples);
        setDefaultSize();
    }

    public void initView(boolean showExamples) {
        MainLayoutPanel.addMainAreaResizeHandler(event -> setDefaultSize());

        FlowPanel textBoxContainer = new FlowPanel();
        textBoxContainer.setStyleName("queryPopupPanel__textBoxContainer");
        queryTextBox = new TextArea();
        queryTextBox.setStyleName("queryPopupPanel__textBox");
        queryTextBox.addValueChangeHandler(event -> {
            queryTextBoxValue = event.getValue();
        });


        textBoxContainer.add(queryTextBox);

        EsaSkyStringButton searchButton = new EsaSkyStringButton(TextMgr.getInstance().getText("global_tap_panel_custom_query_run_query"));
        searchButton.setMediumStyle();
        searchButton.setStyleName("queryPopupPanel__queryButton");
        searchButton.addClickHandler(event -> {
            this.hide();
            this.fireEvent(new QueryTapEvent(this.tapServiceUrl, this.tapTableName, this.tapDescription, this.publisher, this.queryTextBoxValue));
        });

        if (showExamples) {
            container.add(createDropdownMenu());
        }

        container.add(textBoxContainer);
        container.add(searchButton);

    }


    private DropDownMenu<PopupMenuItems> createDropdownMenu() {
        dropDownMenu = new DropDownMenu<>("Examples...",
                TextMgr.getInstance().getText("global_tap_panel_custom_query_dropdown_tooltip"),
                500, "queryPopupPanel__dropdownMenu");

        MenuItem<PopupMenuItems> menuItem1 = new MenuItem<>(PopupMenuItems.METADATA,
                TextMgr.getInstance().getText("global_tap_panel_custom_query_dropdown_option_columns"), true);

        MenuItem<PopupMenuItems> menuItem2 = new MenuItem<>(PopupMenuItems.TABLE,
                TextMgr.getInstance().getText("global_tap_panel_custom_query_dropdown_option_table"), true);

        MenuItem<PopupMenuItems> menuItem3 = new MenuItem<>(PopupMenuItems.COUNT,
                TextMgr.getInstance().getText("global_tap_panel_custom_query_dropdown_option_row_count"), true);

        dropDownMenu.addMenuItem(menuItem1);
        dropDownMenu.addMenuItem(menuItem2);
        dropDownMenu.addMenuItem(menuItem3);

        dropDownMenu.registerObserver(() -> {
            switch (dropDownMenu.getSelectedObject()) {
                case METADATA:
                    setQuery("SELECT * FROM TAP_SCHEMA.columns WHERE table_name = '" + tapTableName.replace("\"", "") + "'");
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


    public enum PopupMenuItems{
        METADATA, TABLE, COUNT;
    }

    public void setTapServiceUrl(String tapServiceUrl) {
        this.tapServiceUrl = tapServiceUrl;
    }

    public void disableExample(PopupMenuItems menuItem) {
        dropDownMenu.getMenuItems().stream().filter(item -> item.getItem() == menuItem).findFirst().ifPresent(item -> item.setDisabled(true));
    }

    public void setTapTable(String tableName) {
        this.tapTableName = ExtTapUtils.encapsulateTableName(tableName);
        dropDownMenu.clearSelection();
        dropDownMenu.selectObject(PopupMenuItems.TABLE);
    }

    public void setTapDescription(String description) {
        this.tapDescription = description;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setQuery(String query) {
        this.queryTextBox.setText(query);
        this.queryTextBoxValue = query;
    }

    private void setDefaultSize() {
        Style containerStyle = container.getElement().getStyle();
        containerStyle.setPropertyPx("minWidth", 500);
        containerStyle.setPropertyPx("minHeight", 150);
    }

    public HandlerRegistration addQueryHandler(QueryTapEventHandler handler) {
        return addHandler(handler, QueryTapEvent.TYPE);
    }


}
