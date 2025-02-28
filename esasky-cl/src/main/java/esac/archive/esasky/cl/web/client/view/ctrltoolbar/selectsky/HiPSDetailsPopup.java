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

package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.ifcs.model.client.HiPS;

public class HiPSDetailsPopup extends PopupPanel {

    private HiPS hips;
    private final String CSS_ID = "hipsDbox";
    private final VerticalPanel hipsDetailsPanel;
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;

    public static interface Resources extends ClientBundle {
        @Source("hipsDetailsPopup.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public HiPSDetailsPopup(HiPS hips) {
        this.style = this.resources.style();
        this.style.ensureInjected();

        this.hips = hips;
        this.hipsDetailsPanel = new VerticalPanel();
        initView();
    }

    private void initView() {
        this.getElement().setId(CSS_ID);
        this.setAutoHideEnabled(true);

        CloseButton closeBtn = new CloseButton();
        closeBtn.setTitle(TextMgr.getInstance().getText("hiPSDetailsPopup_close"));
        closeBtn.addStyleName("closeHipsDetails");
        closeBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                HiPSDetailsPopup.this.hide();
            }
        });

        hipsDetailsPanel.setWidth("100%");

        hipsDetailsPanel.add(closeBtn);

        hipsDetailsPanel.add(new HTML("<b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_mapName") + "</b>"));
        hipsDetailsPanel.add(new HTML(hips.getSurveyName()));
        hipsDetailsPanel.add(new HTML("<br/>"));

        addHipsMissionDetails(hips.getMissionURL(), hips.getMission());
        addHipsInstrumentDetails(hips.getInstrument());
        addHipsWaveLengthDetails(hips.getWavelengthRange());
        addHipsCreatorDetails(hips.getCreator(), hips.getCreatorURL(), hips.getCreationDate());
        addHipsMoreInfoDetails(hips.getMoreInfoURL());

        this.add(hipsDetailsPanel);

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

    private void addHipsMissionDetails(String missionUrl, String mission) {
        if (missionUrl != null && !"".equals(missionUrl)) {
            hipsDetailsPanel.add(new HTML("<b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_mission") + "</b>"));
            hipsDetailsPanel.add(new HTML("<b><a target='_blank' href='" + missionUrl + "'>"
                    + mission + "</a></b>"));
            hipsDetailsPanel.add(new HTML("<br/>"));
        }
    }

    private void addHipsInstrumentDetails(String instrument) {
        if (instrument != null && !"".equals(instrument)) {
            hipsDetailsPanel.add(new HTML("<b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_instrument") + "</b>"));
            hipsDetailsPanel.add(new HTML(instrument));
            hipsDetailsPanel.add(new HTML("<br/>"));
        }
    }

    private void addHipsWaveLengthDetails(String waveLengthRange) {
        if (waveLengthRange != null && !"".equals(waveLengthRange)) {
            hipsDetailsPanel.add(new HTML("<b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_wavFreq") + "</b>"));
            hipsDetailsPanel.add(new HTML(waveLengthRange + "<br/><br/>"));
        }
    }

    private void addHipsCreatorDetails(String creator, String creatorUrl, String creationDate) {
        if (creator != null && !"".equals(creator)) {
            hipsDetailsPanel.add(new HTML("<b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_mapCreatedBy") + "</b>"));
            String htmlString = "";
            if (creatorUrl != null && !"".equals(creatorUrl)) {
                htmlString += "<b><a target='_blank' href='" + creatorUrl + "'>"
                        + creator + "</a></b>";
            } else {
                htmlString += "<b>" + creator + "</b>";
            }
            if (creationDate != null && !"".equals(creationDate)) {
                htmlString += " on " + creationDate;
            }
            hipsDetailsPanel.add(new HTML(htmlString));
            hipsDetailsPanel.add(new HTML("<br/>"));
        }
    }

    private void addHipsMoreInfoDetails(String moreInfoUrl) {
        if (moreInfoUrl != null && !"".equals(moreInfoUrl)) {
            hipsDetailsPanel.add(new HTML("<b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_forMoreInformation") + "</b><a target='_blank' href='"
                    + moreInfoUrl + "'><b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_here") + "</b></a>"));
            hipsDetailsPanel.add(new HTML("<br/>"));
        }
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
