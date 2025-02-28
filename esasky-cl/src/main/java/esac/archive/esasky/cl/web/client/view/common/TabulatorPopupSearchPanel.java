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

package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import esac.archive.esasky.cl.web.client.event.ErrorEvent;
import esac.archive.esasky.cl.web.client.event.ErrorEventHandler;
import esac.archive.esasky.cl.web.client.model.Size;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper;

import java.util.UUID;


public class TabulatorPopupSearchPanel extends MovableResizablePanel<TabulatorPopupSearchPanel>{

    private FlowPanel mainContainer;
    private PopupHeader<TabulatorPopupSearchPanel> header;
    protected TabulatorWrapper tabulatorWrapper;
    private FlowPanel tabulatorContainer;
    private GlassFlowPanel glassTabulatorContainer;
    private LoadingSpinner loadingSpinner;
    private TextBox searchBox;
    private FlowPanel searchRowContainer;
    private CloseButton searchClearBtn;
    private HorizontalPanel actionButtonContainer;
    private final TabulatorCallback tabulatorCallback;
    private final TabulatorSettings tabulatorSettings;
    private final String TABULATOR_TABLE_ID = "tabulatorSearchPanel_tabulatorTablesContainer__" + UUID.randomUUID();

    private String[] searchColumns;

    public interface Resources extends ClientBundle {
        @Source("tabulatorPopupSearchPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public TabulatorPopupSearchPanel(String googleEventCategory, boolean isSuggestedPositionCenter, String title, String helpText, TabulatorCallback tabulatorCallback, TabulatorSettings tabulatorSettings) {
        super(googleEventCategory, isSuggestedPositionCenter);

        Resources resources = GWT.create(Resources.class);
        CssResource style = resources.style();
        style.ensureInjected();

        this.tabulatorCallback = tabulatorCallback;
        this.tabulatorSettings = tabulatorSettings;

        initView(title, helpText);
    }


    private void initView(String title, String helpText) {
        mainContainer = new FlowPanel();
        mainContainer.getElement().setId("tabulatorPopupSearchPanel__" + title + "__" + UUID.randomUUID());

        header = new PopupHeader<>(this, title, helpText);

        glassTabulatorContainer = new GlassFlowPanel();
        tabulatorContainer = new FlowPanel();
        tabulatorContainer.getElement().setId(TABULATOR_TABLE_ID);
        glassTabulatorContainer.add(tabulatorContainer);


        searchRowContainer = new FlowPanel();
        searchRowContainer.addStyleName("tabulatorPopupSearchPanel__searchRowContainer");

        FlowPanel searchBoxContainer = new FlowPanel();
        searchBoxContainer.setStyleName("tabulatorPopupSearchPanel__searchBoxContainer");


        searchBox = new TextBox();
        searchBox.setStyleName("tabulatorPopupSearchPanel__searchBox");

        searchClearBtn = new CloseButton();
        searchClearBtn.addStyleName("tabulatorPopupSearchPanel__searchClearButton");
        searchClearBtn.setDarkStyle();
        searchClearBtn.setSecondaryIcon();

        Timer searchDelayTimer = new Timer() {
            @Override
            public void run() {
                tabulatorWrapper.columnIncludesFilter(searchBox.getText(), searchColumns);
                searchClearBtn.setVisible(!searchBox.getText().isEmpty());
            }
        };
        searchClearBtn.addClickHandler(event -> {
            searchBox.setText("");
            searchBox.setFocus(true);
            searchClearBtn.setVisible(false);
            searchDelayTimer.run();
        });
        searchClearBtn.setVisible(false);

        searchBox.addKeyUpHandler(event -> {
            searchDelayTimer.cancel();
            searchDelayTimer.schedule(500);
        });

        searchBox.addChangeHandler(event -> {
            searchDelayTimer.cancel();
            searchDelayTimer.schedule(500);
        });



        searchBoxContainer.add(searchBox);
        searchBoxContainer.add(searchClearBtn);


        actionButtonContainer = new HorizontalPanel();
        searchRowContainer.add(actionButtonContainer);
        searchRowContainer.add(searchBoxContainer);


        loadingSpinner = new LoadingSpinner(true);
        loadingSpinner.setStyleName("globalTapPanel__loadingSpinner");
        loadingSpinner.setVisible(false);

        mainContainer.add(header);
        mainContainer.add(searchRowContainer);
        mainContainer.add(glassTabulatorContainer);
        mainContainer.add(loadingSpinner);
        this.add(mainContainer);
        this.setStyleName("tabulatorPopupSearchPanel");
        this.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
    }

    @Override
    public void show() {
        super.show();
        setDefaultSize();
        MainLayoutPanel.addElementToMainArea(this);
    }

    @Override
    public void hide() {
        super.hide();
        MainLayoutPanel.removeElementFromMainArea(this);
    }


    @Override
    protected void onLoad() {
        super.onLoad();
        tabulatorWrapper = new TabulatorWrapper(TABULATOR_TABLE_ID, this.tabulatorCallback, this.tabulatorSettings);
        scaleTabulatorHeight();
    }

    @Override
    protected Element getMovableElement() {
        return header.getElement();
    }

    @Override
    protected Element getResizeElement() {
        return mainContainer.getElement();
    }

    @Override
    protected void onResize() {
        super.onResize();
        scaleTabulatorHeight();
    }

    protected void updateSettings(TabulatorSettings settings) {
        tabulatorWrapper = new TabulatorWrapper(TABULATOR_TABLE_ID, this.tabulatorCallback, settings);
        scaleTabulatorHeight();
    }

    public void addActionWidget(EsaSkyButton widget) {
        widget.setBackgroundColor("#aaa");
        widget.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        widget.setRoundStyle();
        actionButtonContainer.insert(widget, 0);
    }

    public void removeActionWidget(EsaSkyButton widget) {
        actionButtonContainer.remove(widget);
    }

    protected void setIsLoading(boolean isLoading) {
        loadingSpinner.setVisible(isLoading);

        if (isLoading) {
            glassTabulatorContainer.showGlass();
        } else {
            glassTabulatorContainer.hideGlass();
        }
    }

    protected void setSearchPlaceholder(String placeholder) {
        searchBox.getElement().setPropertyString("placeholder", placeholder);
    }

    protected void setSearchColumns(String... columns) {
        searchColumns = columns;
    }

    protected void clearSearchText() {
        searchClearBtn.click();
    }

    private void scaleTabulatorHeight() {
        int headerSize = header.getOffsetHeight();
        int searchContainerHeight = searchRowContainer.getOffsetHeight();
        int height = this.getOffsetHeight() - headerSize - searchContainerHeight - 10;

        if (height > MainLayoutPanel.getMainAreaHeight()) {
            height = MainLayoutPanel.getMainAreaHeight() - headerSize - searchContainerHeight - 10;
        }

        this.tabulatorContainer.getElement().getStyle().setPropertyPx("height", height);
    }
    private void setDefaultSize() {
        Size size = super.getDefaultSize();
        mainContainer.setWidth(size.width + "px");
        mainContainer.setHeight(size.height + "px");
    }


    public HandlerRegistration addErrorHandler(ErrorEventHandler handler) {
        return addHandler(handler, ErrorEvent.TYPE);
    }

    protected void fireErrorEvent(String message, Exception exception) {
        fireEvent(new ErrorEvent(message, exception));
    }

    protected void fireErrorEvent(String message, String details) {
        fireEvent(new ErrorEvent(message, details));
    }

    protected  void fireErrorEvent(int status, String message, String details) {
        fireEvent(new ErrorEvent(status, message, details));
    }
}
