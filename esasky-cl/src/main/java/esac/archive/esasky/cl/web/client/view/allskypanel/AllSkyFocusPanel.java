/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.cl.web.client.view.allskypanel;

import java.util.LinkedList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinOpenContextMenuEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.CoordinatesObject;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.model.DecPosition;
import esac.archive.esasky.cl.web.client.model.RaPosition;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.ifcs.model.coordinatesutils.ClientRegexClass;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.AutoHidePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesParser;

public class AllSkyFocusPanel extends FocusPanel {

    public interface AllSkyFocusPanelObserver {
        public void onAladinInteraction();
    }

    private LinkedList<AllSkyFocusPanelObserver> observers = new LinkedList<AllSkyFocusPanelObserver>();
    private AutoHidePanel contextMenu = new AutoHidePanel();
    private int mouseX;
    private int mouseY;
    private EsaSkyButton searchInSimbadButton;
    private EsaSkyButton searchInNedButton;
    private EsaSkyButton searchInVizierPhotometryButton;
    private EsaSkyButton copyCoordinateButton;
    private int lastKnownNumberOfTouches = 0;
    Label raText;
    Label decText;
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;

    public interface Resources extends ClientBundle {

        @Source("NED.png")
        ImageResource ned();

        @Source("vizier-photometry-icon.png")
        ImageResource vizierPhotometry();

        @Source("vizier.png")
        ImageResource vizier();

        @Source("simbad.png")
        ImageResource simbad();

        @Source("wwt_logo.png")
        ImageResource wwtLogo();

        @Source("copy-icon.png")
        ImageResource copyIcon();

        @Source("allSkyFocusPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    private AllSkyFocusPanel() {
        super();
        style = resources.style();
        style.ensureInjected();

        if (Modules.getModule(EsaSkyWebConstants.MODULE_SEARCH_IN_MENU)) {
            createPopupMenu();
        }

        sinkEvents(Event.ONMOUSEUP | Event.ONCONTEXTMENU | Event.ONTOUCHSTART | Event.ONTOUCHMOVE | Event.ONMOUSEDOWN
                | Event.ONTOUCHCANCEL | Event.ONTOUCHEND | Event.ONCLICK | Event.ONFOCUS | Event.TOUCHEVENTS);

        CommonEventBus.getEventBus().addHandler(AladinOpenContextMenuEvent.TYPE, event -> {
            openContextMenu(event.getEvent());
            updateCoordinates();
        });

    }

    private static AllSkyFocusPanel instance = null;

    public static AllSkyFocusPanel getInstance() {
        if (instance == null) {
            instance = new AllSkyFocusPanel();
        }
        return instance;
    }

    @Override
    public final void onBrowserEvent(final Event event) {
        switch (DOM.eventGetType(event)) {

            case Event.ONTOUCHSTART:
            case Event.ONTOUCHEND:
            case Event.ONTOUCHMOVE:
            case Event.ONTOUCHCANCEL:
                lastKnownNumberOfTouches = event.getTouches().length();
                break;

            case Event.ONMOUSEUP:
                break;

            case Event.ONCONTEXTMENU:
                if (lastKnownNumberOfTouches > 1) {
                    break;
                }
                this.openContextMenu(event);
                this.updateCoordinates();
                break;

            default:
                break;

        }
        notifyObservers();
    }

    public final void openContextMenu(final Event event) {
        JsArray<Touch> changedTouches = event.getChangedTouches();

        if (changedTouches != null && changedTouches.length() > 0) {
            mouseX = changedTouches.get(0).getClientX();
            mouseY = changedTouches.get(0).getClientY();
        } else {
            mouseX = event.getClientX();
            mouseY = event.getClientY();
        }

        mouseX -= - MainLayoutPanel.getMainAreaAbsoluteLeft();
        mouseY -= - MainLayoutPanel.getMainAreaAbsoluteTop();



        DisplayUtils.showInsideMainAreaPointingAtPosition(contextMenu, mouseX, mouseY);

        Log.debug(AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getRaDeg() + " dec "
                + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getDecDeg());
    }

    private void createPopupMenu() {

        searchInSimbadButton = new EsaSkyButton(this.resources.simbad());
        searchInSimbadButton.setTitle(TextMgr.getInstance().getText("AllSkyFocusPanel_searchInSimbad"));
        searchInSimbadButton.setVeryBigStyle();
        searchInSimbadButton.addStyleName("allSkyButton");
        searchInSimbadButton.addClickHandler(event -> {
            contextMenu.hide();
            Coordinate j2000Coordinate = getJ2000Coordinate();
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CONTEXT_MENU, GoogleAnalytics.ACT_CONTEXTMENU_SEARCHINSIMBAD,
                    "RA: " + j2000Coordinate.getRa()
                            + " Dec: " + j2000Coordinate.getDec()
                            + " CooFrame: " + AladinLiteWrapper.getCoordinatesFrame().getValue());
            Window.open(
                    ExternalServices.buildSimbadURLWithRaDec(j2000Coordinate.getRa(),
                            j2000Coordinate.getDec(), CoordinatesFrame.J2000.getValue()),
                    "_blank", "");
        });

        searchInNedButton = new EsaSkyButton(this.resources.ned());
        searchInNedButton.setTitle(TextMgr.getInstance().getText("AllSkyFocusPanel_searchInNed"));
        searchInNedButton.addStyleName("allSkyButton");
        searchInNedButton.setVeryBigStyle();
        searchInNedButton.addClickHandler(event -> {
            contextMenu.hide();
            Coordinate j2000Coordinate = getJ2000Coordinate();
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CONTEXT_MENU, GoogleAnalytics.ACT_CONTEXTMENU_SEARCHINNED,
                    "RA: " + j2000Coordinate.getRa()
                            + " Dec: " + j2000Coordinate.getDec()
                            + " CooFrame: " + AladinLiteWrapper.getCoordinatesFrame().getValue());
            Window.open(
                    ExternalServices.buildNedURL(j2000Coordinate.getRa(), j2000Coordinate.getDec(),
                            CoordinatesFrame.J2000.getValue()), "_blank", "");
        });

        searchInVizierPhotometryButton = new EsaSkyButton(this.resources.vizierPhotometry());
        searchInVizierPhotometryButton
                .setTitle(TextMgr.getInstance().getText("AllSkyFocusPanel_searchInVizierPhotometry"));
        searchInVizierPhotometryButton.setVeryBigStyle();
        searchInVizierPhotometryButton.addStyleName("allSkyButton");
        searchInVizierPhotometryButton.addClickHandler(event -> {
            contextMenu.hide();
            Coordinate j2000Coordinate = getJ2000Coordinate();
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CONTEXT_MENU, GoogleAnalytics.ACT_CONTEXTMENU_SEARCHINVIZIERPHOTOMETRY,
                    "RA: " + j2000Coordinate.getRa()
                            + " Dec: " + j2000Coordinate.getDec());
            Window.open(ExternalServices.buildVizierPhotometryURLJ2000(j2000Coordinate.getRa(),
                    j2000Coordinate.getDec()), "_blank", "");

        });

        EsaSkyButton searchInVizierButton = new EsaSkyButton(this.resources.vizier());
        searchInVizierButton.setTitle(TextMgr.getInstance().getText("AllSkyFocusPanel_searchInVizier"));
        searchInVizierButton.setVeryBigStyle();
        searchInVizierButton.addStyleName("allSkyButton");
        searchInVizierButton.addClickHandler(event -> {
            contextMenu.hide();
            Coordinate j2000Coordinate = getJ2000Coordinate();
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CONTEXT_MENU,
                    GoogleAnalytics.ACT_CONTEXTMENU_SEARCHINVIZIER,
                    "RA: " + j2000Coordinate.getRa()
                            + " Dec: " + j2000Coordinate.getDec());
            Window.open(
                    ExternalServices.buildVizierURLJ2000(j2000Coordinate.getRa(), j2000Coordinate.getDec()), "_blank", "");
        });

        EsaSkyButton wwtButton = new EsaSkyButton(this.resources.wwtLogo());
        wwtButton.setVeryBigStyle();
        wwtButton.addStyleName("allSkyButton");

        wwtButton.setTitle(TextMgr.getInstance().getText("header_viewInWWTFull"));
        wwtButton.addClickHandler(event -> {
            Coordinate j2000Coordinate = getJ2000Coordinate();
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_CONTEXT_MENU, GoogleAnalytics.ACT_CONTEXTMENU_VIEWINWWT,
                    "RA: " + j2000Coordinate.getRa() + " Dec: " + j2000Coordinate.getDec());
            Window.open(ExternalServices.buildWwtURLJ2000(j2000Coordinate.getRa(), j2000Coordinate.getDec()), "_blank", "");
        });
        if (!Modules.getModule(EsaSkyWebConstants.MODULE_WWT_LINK)) {
            wwtButton.getElement().getStyle().setDisplay(Display.NONE);
        }

        Style popupStyle = contextMenu.getElement().getStyle();
        popupStyle.setZIndex(30);

        FlowPanel searchButtons = new FlowPanel();
        searchButtons.add(searchInSimbadButton);
        searchButtons.add(searchInNedButton);
        searchButtons.add(searchInVizierPhotometryButton);
        searchButtons.add(searchInVizierButton);
        if (Modules.getModule(EsaSkyWebConstants.MODULE_WWT_LINK)) {
            searchButtons.add(wwtButton);
        }

        FlowPanel positionContainer = new FlowPanel();
        positionContainer.addStyleName("allSkyCoordinateContainer");
        copyCoordinateButton = new EsaSkyButton(this.resources.copyIcon());
        copyCoordinateButton.addClickHandler(event -> CopyToClipboardHelper.getInstance().copyToClipBoard(raText.getText() + " " + decText.getText(), TextMgr.getInstance().getText("AllSkyFocusPanel_copyCoordinateURL")));

        FlowPanel labelContainer = new FlowPanel();
        labelContainer.addStyleName("labelContainer");
        FlowPanel raContainer = new FlowPanel();
        FlowPanel decContainer = new FlowPanel();
        raContainer.addStyleName("labelRow");
        decContainer.addStyleName("labelRow");

        Label raLabel = new Label("RA: ");
        Label decLabel = new Label("Dec: ");
        raLabel.addStyleName("labelTitle");
        decLabel.addStyleName("labelTitle");
        raText = new Label();
        decText = new Label();

        raContainer.add(raLabel);
        raContainer.add(raText);

        decContainer.add(decLabel);
        decContainer.add(decText);

        labelContainer.add(raContainer);
        labelContainer.add(decContainer);
        positionContainer.add(labelContainer);
        positionContainer.add(copyCoordinateButton);
        contextMenu.add(positionContainer);

        Label searchLabel = new Label(TextMgr.getInstance().getText("AllSkyFocusPanel_searchIn"));
        FlowPanel container = new FlowPanel();
        container.add(searchLabel);
        container.add(searchButtons);
        contextMenu.add(container);
        contextMenu.addStyleName("AllSkyRightClickPopup");

    }

    protected Coordinate getJ2000Coordinate() {
        CoordinatesObject coordinateOfPress = AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX,
                mouseY);
        return CoordinateUtils.getCoordinateInJ2000(coordinateOfPress.getRaDeg(),
                coordinateOfPress.getDecDeg());
    }

    public void registerObserver(AllSkyFocusPanelObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AllSkyFocusPanelObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (AllSkyFocusPanelObserver observer : observers) {
            observer.onAladinInteraction();
        }
    }

    private void updateCoordinates() {
        Coordinate cords = getJ2000Coordinate();
        CoordinatesFrame cooFrame = CoordinatesFrame.valueOf(AladinLiteWrapper.getAladinLite().getCooFrame().toUpperCase());
        double[] coords = CoordinatesParser.convertCoordsToDegrees(new ClientRegexClass(), cords.getRa() + " "
                + (cords.getDec() >= 0 ? "+" : "") + cords.getDec(), CoordinatesFrame.J2000, cooFrame);

        if (coords != null && coords.length > 1) {
            RaPosition raPos = new RaPosition(coords[0]);
            DecPosition decPos = new DecPosition(coords[1]);

            String[] raDecStr;
            if (GUISessionStatus.isShowingCoordinatesInDegrees()) {
                raDecStr = new String[]{Double.toString(raPos.getRaDeg()), Double.toString(decPos.getDecDegFix())};
            } else {
                raDecStr = new String[]{raPos.getSpacedHmsString(), decPos.getSpacedDmsStringFix()};
            }

            raText.setText(raDecStr[0]);
            decText.setText(raDecStr[1]);
        }
    }

    public void updateModuleVisibility() {
        contextMenu.setVisible(Modules.getModule(EsaSkyWebConstants.MODULE_SEARCH_IN_MENU));
    }
}
