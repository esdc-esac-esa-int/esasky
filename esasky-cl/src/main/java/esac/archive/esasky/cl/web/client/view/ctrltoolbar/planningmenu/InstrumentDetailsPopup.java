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

package esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
public class InstrumentDetailsPopup extends PopupPanel {

    private final String CSS_ID = "instrumentDbox";
    private final VerticalPanel instrumentDetailsPanel;
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;
    private double offsetAngle;
    private double instrumentAngle;
    private double totalAngle;
    private final NumberFormat angleFormat = NumberFormat.getFormat("#0.000");
    private final String DEGREE = "\u00B0\n";
    private final String BOLD_OPEN = "<b>";
    private final String BOLD_CLOSED = "</b>";
    private final String LINE_BREAK = "<br/>";
    private final String mission;
    public static interface Resources extends ClientBundle {
        @Source("instrumentDetailsPopup.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public InstrumentDetailsPopup(String mission, double offsetAngle, double instrumentAngle, double totalAngle) {
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.mission = mission;
        this.offsetAngle=offsetAngle;
        this.instrumentAngle=instrumentAngle;
        this.totalAngle=totalAngle;
        
        this.instrumentDetailsPanel = new VerticalPanel();
        initView();
    }

    private void initView() {
        this.getElement().setId(CSS_ID);
        this.setAutoHideEnabled(true);

        CloseButton closeBtn = new CloseButton();
        closeBtn.setTitle(TextMgr.getInstance().getText("instrumentDetailsPopup_close"));
        closeBtn.addStyleName("closeinstrumentDetails");
        closeBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                InstrumentDetailsPopup.this.hide();
            }
        });

        instrumentDetailsPanel.setWidth("100%");

        instrumentDetailsPanel.add(closeBtn);

        instrumentDetailsPanel.add(new HTML(BOLD_OPEN + TextMgr.getInstance().getText("futureFootprintRow_tooltip").replace("$MISSION$", mission) + BOLD_CLOSED));
        instrumentDetailsPanel.add(new HTML(LINE_BREAK));
        
        addInstrumentAngle(instrumentAngle);
        addTotalRotation(totalAngle);
        instrumentDetailsPanel.add(new HTML(LINE_BREAK));
        addInfoLink();

        this.add(instrumentDetailsPanel);

        MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {
                setMaxHeight();
            }
        });
        getContainerElement().getStyle().setOverflow(Overflow.AUTO);

        setWidth(170 + "px");
    }

    private void setMaxHeight() {
        getContainerElement().getStyle().setProperty("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getContainerElement().getAbsoluteTop() - 10 + "px");
    }
    
    private void addInstrumentAngle(double instrumentAngle) {
    	instrumentDetailsPanel.add(new HTML(BOLD_OPEN + TextMgr.getInstance().getText("futureFootprintRow_instrAngle") + BOLD_CLOSED + angleFormat.format(instrumentAngle) + DEGREE));
    }
    
    private void addTotalRotation(double totalAngle) {
    	instrumentDetailsPanel.add(new HTML(BOLD_OPEN + TextMgr.getInstance().getText("futureFootprintRow_totalRotation") + BOLD_CLOSED + angleFormat.format(totalAngle) + DEGREE));
    }

    private void addInfoLink() {
    	instrumentDetailsPanel.add(new HTML(TextMgr.getInstance().getText("futureFootprintRow_instrAngleInfo_" + mission)));
    }


    @Override
    public void show() {
        super.show();
        setMaxHeight();
    }

    @Override
    public void setPopupPosition(int left, int top) {
        super.setPopupPosition(left, top);
        setMaxHeight();
    }

}
