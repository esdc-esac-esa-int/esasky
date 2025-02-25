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

package esac.archive.esasky.cl.web.client.presenter;


import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants.CoordinateFrame;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.*;
import esac.archive.esasky.cl.web.client.event.*;
import esac.archive.esasky.cl.web.client.presenter.login.UserAreaPresenter;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.banner.ServerProblemFoundEvent;
import esac.archive.esasky.cl.web.client.event.banner.ServerProblemSolvedEvent;
import esac.archive.esasky.cl.web.client.event.banner.ToggleServerProblemBannerEvent;
import esac.archive.esasky.cl.web.client.event.hips.HipsNameChangeEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.DecPosition;
import esac.archive.esasky.cl.web.client.model.RaPosition;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.CopyToClipboardHelper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.ExternalServices;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.Session;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.allskypanel.AllSkyFocusPanel;
import esac.archive.esasky.cl.web.client.view.header.HeaderPanel;
import esac.archive.esasky.cl.web.client.view.header.ScreenshotDialogBox;

public class HeaderPresenter {

    private View view;

    public interface View {
        void setFov(String fov);

        void setCoordinate(String coordinate);

        void setIsCenterCoordinateStyle();

        void setIsNotCenterCoordinateStyle();

        void setAvailableCoordinateFrames(List<SelectionEntry> coordinateFrames);

        void setAvailableProjections(Map<String, String> projections);

        void setProjection(String projection);

        void selectCoordinateFrame(int index);

        void selectCoordinateFrameNoEvent(int index);

        void setHipsName(String hipsName);

        void setIsInScienceMode(boolean isInScienceMode);

        void setAvailableLanguages(List<SimpleEntry<String, String>> languages);

        void setSelectedLanguage(int index);

        void addCoordinateFrameChangeHandler(ChangeHandler changeHandler);

        void addCoordinateClickHandler(ClickHandler handler);

        String getSelectedCoordinateFrame();

        void addMenuClickHandler(ClickHandler handler);

        void addDropdownContainerBlurHandler(BlurHandler handler);

        void addDropdownContainerMouseDownHandler(MouseDownHandler handler);

        void addHipsNameClickHandler(ClickHandler handler);

        void addShareClickHandler(ClickHandler handler);

        void addHelpClickHandler(ClickHandler handler);

        void addEvaClickHandler(ClickHandler handler);

        void addFeedbackClickHandler(ClickHandler handler);

        void addVideoTutorialsClickHandler(ClickHandler handler);

        void addReleaseNotesClickHandler(ClickHandler handler);

        void addNewsletterClickHandler(ClickHandler handler);

        void addApiClickHandler(ClickHandler handler);

        void addAboutUsClickHandler(ClickHandler handler);

        void addAcknowledgeClickHandler(ClickHandler handler);

        void addViewInWwtClickHandler(ClickHandler handler);

        void addScienceModeSwitchClickHandler(ClickHandler handler);

        void addScreenshotClickHandler(ClickHandler handler);

        void addLanguageSelectionChangeHandler(StringValueSelectionChangedHandler handler);

        void addWarningButtonClickHandler(ClickHandler handler);

        void addSessionSaveClickHandler(ClickHandler handler);

        void addSessionRestoreClickHandler(ClickHandler handler);

        void addGridButtonClickHandler(ClickHandler handler);

        void setGridButtonToggled(boolean toggled);

        void addUserAreaClickHandler(ClickHandler handler);

        void showWarningButton();

        void hideWarningButton();

        void toggleDropdownMenu();

        void closeDropdownMenu();

        StatusPresenter.View getStatusView();

        UserAreaPresenter getUserAreaPresenter();

        void updateModuleVisibility();
    }


    private String coordinateFrameFromUrl;
    private long timeSinceLastMouseDownInsideDropdownContainer;
    private boolean isSciModeChecked = GUISessionStatus.getIsInScienceMode();

    public HeaderPresenter(final View inputView, String coordinateFrameFromUrl) {
        this.view = inputView;
        this.coordinateFrameFromUrl = coordinateFrameFromUrl;
        bind();
        new StatusPresenter(inputView.getStatusView());
        view.updateModuleVisibility();
    }

    private void bind() {

        CommonEventBus.getEventBus().addHandler(AladinLiteProjectionChangedEvent.TYPE, event -> {
            view.setProjection(event.getProjection());
            CommonEventBus.getEventBus().fireEvent(new UrlChangedEvent());
        });

        CommonEventBus.getEventBus().addHandler(AladinLiteFoVChangedEvent.TYPE, changeEvent -> {
                view.setFov("FoV: " + formatFov(changeEvent.getFov()) + " X " + formatFov(changeEvent.getFovDec()));
            });

        CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesChangedEvent.TYPE, changeEvent -> {
            setCoordinates(changeEvent.getRa(), changeEvent.getDec());
            if (changeEvent.getIsViewCenterPosition()) {
                view.setIsCenterCoordinateStyle();
            } else {
                view.setIsNotCenterCoordinateStyle();
            }
        });

        CommonEventBus.getEventBus().addHandler(HipsNameChangeEvent.TYPE, changeEvent -> {
            view.setHipsName(changeEvent.getHiPSName());
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_HIPSNAME, changeEvent.getHiPSName());
        });

        CommonEventBus.getEventBus().addHandler(IsInScienceModeChangeEvent.TYPE, () -> {
            if (GUISessionStatus.getIsInScienceMode() != isSciModeChecked) {
                toggleSciMode();
            }
        });

        CommonEventBus.getEventBus().addHandler(ServerProblemFoundEvent.TYPE, event -> view.showWarningButton());

        CommonEventBus.getEventBus().addHandler(ServerProblemSolvedEvent.TYPE, event -> view.hideWarningButton());
        CommonEventBus.getEventBus().addHandler(GridToggledEvent.TYPE, event -> view.setGridButtonToggled(event.isGridActive()));

        setInitialValues();

        view.addCoordinateFrameChangeHandler(changeEvent -> {
            if (view.getSelectedCoordinateFrame().equals(CoordinateFrame.J2000.toString())) {
                AladinLiteWrapper.getInstance().setCooFrame(CoordinateFrame.J2000);
                GUISessionStatus.setShowCoordinatesInDegrees(false);
            } else {
                AladinLiteWrapper.getInstance().setCooFrame(CoordinateFrame.GALACTIC);
                GUISessionStatus.setShowCoordinatesInDegrees(true);
            }
        });

        view.addHipsNameClickHandler(event -> CommonEventBus.getEventBus().fireEvent(new ToggleSkyPanelEvent()));

        view.addCoordinateClickHandler(event -> {
            if (AladinLiteWrapper.getCoordinatesFrame() == CoordinatesFrame.GALACTIC) {
                return;
            }
            GUISessionStatus.toggleShowCoordinatesInDegrees();
            Coordinate coordinate = CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
            setCoordinates(coordinate.getRa(), coordinate.getDec());
        });

        view.addShareClickHandler(event -> {
            String bookmarkUrl = UrlUtils.getUrlForCurrentState();
            UrlUtils.updateURLWithoutReloadingJS(bookmarkUrl);
            String hostName = Window.Location.getHost();
            CopyToClipboardHelper.getInstance().copyToClipBoard(hostName + bookmarkUrl, TextMgr.getInstance().getText("ctrlToolBar_URLClipboard"));
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_SHARE, "");
            view.closeDropdownMenu();
        });

        view.addHelpClickHandler(event -> {
            Window.open(TextMgr.getInstance().getText("headerPresenter_helpLink"), "_blank", "");
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_HELP, "");
            view.closeDropdownMenu();
        });

        view.addEvaClickHandler(event -> {
            CommonEventBus.getEventBus().fireEvent(new ShowEvaEvent());
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_EVA, "");
            view.closeDropdownMenu();
        });


        view.addDropdownContainerMouseDownHandler(arg0 -> timeSinceLastMouseDownInsideDropdownContainer = System.currentTimeMillis());

        view.addDropdownContainerBlurHandler(arg0 -> {
            if (System.currentTimeMillis() - timeSinceLastMouseDownInsideDropdownContainer > 100) {
                view.closeDropdownMenu();
            }
        });

        AllSkyFocusPanel.getInstance().registerObserver(() -> view.closeDropdownMenu());

        view.addMenuClickHandler(event -> {
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_MENU, "");
            view.toggleDropdownMenu();
        });

        view.addFeedbackClickHandler(event -> {
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_FEEDBACK, "");
            Window.open(EsaSkyWebConstants.ESA_SKY_USER_ECHO, "_blank", "");
            view.closeDropdownMenu();
        });


        view.addVideoTutorialsClickHandler(event -> {
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_VIDEOTUTORIALS, "");
            Window.open(EsaSkyWebConstants.ESA_SKY_HELP_PAGES_URL, "_blank", "");
            view.closeDropdownMenu();
        });

        view.addGridButtonClickHandler(event -> AladinLiteWrapper.getInstance().toggleGrid());

        view.addReleaseNotesClickHandler(event -> {
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_RELEASENOTES, "");
            Window.open(EsaSkyWebConstants.ESA_SKY_RELEASE_NOTES_URL, "_blank", "");
            view.closeDropdownMenu();
        });

        view.addNewsletterClickHandler(event -> {
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_NEWSLETTER, "");
            Window.open(EsaSkyWebConstants.ESA_SKY_NEWSLETTER_URL, "_blank", "");
            view.closeDropdownMenu();
        });

        view.addApiClickHandler(event -> {
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_API, "");
            Window.open(EsaSkyWebConstants.ESA_SKY_API_URL, "_blank", "");
            view.closeDropdownMenu();
        });

        view.addAboutUsClickHandler(event -> {
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_ABOUTUS, "");
            Window.open(EsaSkyWebConstants.ESA_SKY_ABOUTUS_URL, "_blank", "");
            view.closeDropdownMenu();
        });

        view.addAcknowledgeClickHandler(event -> {
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_ACKNOWLEDGE, "");
            Window.open(EsaSkyWebConstants.ESA_SKY_ACKNOWLEDGE_URL, "_blank", "");
            view.closeDropdownMenu();
        });

        view.addViewInWwtClickHandler(event -> {
            Coordinate j2000Coordinate = CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
            Window.open(ExternalServices.buildWwtURLJ2000(j2000Coordinate.getRa(), j2000Coordinate.getDec()), "_blank", "");
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_VIEWINWWT, "");
            view.closeDropdownMenu();
        });


        view.addSessionSaveClickHandler(event -> {
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_CTRLTOOLBAR_SESSION_SAVE, "");
            view.closeDropdownMenu();
            Session session = new Session();
            session.saveState();
        });

        view.addSessionRestoreClickHandler(event -> {
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_CTRLTOOLBAR_SESSION_RESTORE, "");
            view.closeDropdownMenu();
            Session session = new Session();
            session.restoreState();
        });

        view.addScienceModeSwitchClickHandler(arg0 -> toggleSciMode());

        view.addScreenshotClickHandler(arg0 -> {
            String viewUrl = AladinLiteWrapper.getAladinLite().getViewURL(true);
            JavaScriptObject imageCanvas = AladinLiteWrapper.getAladinLite().getViewCanvas(true);
            ScreenshotDialogBox screenshotDialogBox = new ScreenshotDialogBox(viewUrl, imageCanvas);
            screenshotDialogBox.show();
            view.closeDropdownMenu();

            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_SCREENSHOT, "");
        });

        view.addLanguageSelectionChangeHandler((newValue, index) -> {
            view.setSelectedLanguage(index);
            GUISessionStatus.setCurrentLanguage(newValue);
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_LANGUAGE, newValue);
            Window.open(UrlUtils.getUrlForCurrentState(), "_self", "");
        });

        view.addWarningButtonClickHandler(event -> CommonEventBus.getEventBus().fireEvent(new ToggleServerProblemBannerEvent()));

        view.addUserAreaClickHandler(event -> {
            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_HEADER, GoogleAnalytics.ACT_HEADER_USERAREA, "");
            getView().getUserAreaPresenter().togglePanel();
            view.closeDropdownMenu();
        });

    }

    private void setInitialValues() {
        view.setAvailableCoordinateFrames(new LinkedList<>(
                        Arrays.asList(
                                new SelectionEntry("J2000", AladinLiteConstants.CoordinateFrame.J2000.toString()),
                                new SelectionEntry("GAL", AladinLiteConstants.CoordinateFrame.GALACTIC.toString())
                        )
                )
        );

        Map<String, String> map = new HashMap<>();
        map.put("Spheric", "SIN");
        map.put("Tangential", "TAN");
        map.put("Aitoff", "AIT");
        map.put("Zenital equal-area", "ZEA");
        map.put("Stereographic", "STG");
        map.put("Mercator", "MER");
        map.put("Mollweide", "MOL");


        view.setAvailableProjections(map);

        if (coordinateFrameFromUrl != null && coordinateFrameFromUrl.toLowerCase().contains("gal")) {
            view.selectCoordinateFrameNoEvent(1);
        }

        view.setHipsName(EsaSkyConstants.ALADIN_DEFAULT_HIPS_MAP);
        boolean isKiosk = Modules.getModule(EsaSkyWebConstants.MODULE_KIOSK_BUTTONS);
        view.setAvailableLanguages(EsaSkyConstants.getAvailableLanguages(isKiosk));
        for (SimpleEntry<String, String> entry : EsaSkyConstants.getAvailableLanguages(isKiosk)) {
            if (entry.getKey().equalsIgnoreCase(GUISessionStatus.getCurrentLanguage())) {
                view.setSelectedLanguage(EsaSkyConstants.getAvailableLanguages(isKiosk).indexOf(entry));
                break;
            }
        }
    }

    private void toggleSciMode() {
        isSciModeChecked = !isSciModeChecked;
        view.setIsInScienceMode(isSciModeChecked);
        GUISessionStatus.setIsInScienceMode(isSciModeChecked);

        if (isSciModeChecked) {
            CommonEventBus.getEventBus().fireEvent(new CloseImageListEvent(CloseImageListEvent.Sender.ALL));
        }
    }

    private void setCoordinates(double ra, double dec) {
        String coordinate;
        RaPosition raPosition = new RaPosition(ra);
        DecPosition decPosition = new DecPosition(dec);
        coordinate = raPosition.getDegreeStringWithoutDegreeSymbol() + " " + decPosition.getDegreeStringWithoutDegreeSymbol();

        if (view.getSelectedCoordinateFrame().equals(CoordinateFrame.J2000.toString()) && !GUISessionStatus.isShowingCoordinatesInDegrees()) {
            coordinate = raPosition.getSpacedHmsString() + " " + decPosition.getSpacedDmsString();
        }

        view.setCoordinate(coordinate);
    }

    private String formatFov(double fovDeg) {
        if (fovDeg >= 100) {
            NumberFormat format = NumberFormat.getFormat("#00");
            return format.format(fovDeg) + "&deg;";
        } else if (fovDeg >= 10.0) {
            NumberFormat format = NumberFormat.getFormat("#00");
            return format.format(fovDeg) + "&deg;";

        } else if (fovDeg >= 1.0) {
            NumberFormat format = NumberFormat.getFormat("#0.0");
            return format.format(fovDeg) + "&deg;";

        } else if (fovDeg * 6 >= 1.0) {
            NumberFormat format = NumberFormat.getFormat("#00");
            return format.format(fovDeg * 60) + "'";

        } else if (fovDeg * 60 >= 1.0) {
            NumberFormat format = NumberFormat.getFormat("#0.0");
            return format.format(fovDeg * 60) + "'";

        } else if (fovDeg * 600 >= 1.0) {
            NumberFormat format = NumberFormat.getFormat("#00");
            return format.format(fovDeg * 60 * 60) + "''";

        } else {
            NumberFormat format = NumberFormat.getFormat("#0.0");
            return format.format(fovDeg * 60 * 60) + "''";
        }
    }

    public class SelectionEntry {
        private String text;
        private String value;

        public SelectionEntry(String text, String value) {
            this.text = text;
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public String getValue() {
            return value;
        }
    }

    public interface StringValueSelectionChangedHandler {
        public void onSelectionChanged(String newValue, int index);
    }

    public void updateModuleVisibility() {
        view.updateModuleVisibility();
    }

    public HeaderPanel getView() {
        return (HeaderPanel) view;
    }
}
