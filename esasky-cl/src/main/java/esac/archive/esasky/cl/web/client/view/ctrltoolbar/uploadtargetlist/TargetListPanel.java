package esac.archive.esasky.cl.web.client.view.ctrltoolbar.uploadtargetlist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorHandler;
import org.moxieapps.gwt.uploader.client.events.UploadErrorEvent;
import org.moxieapps.gwt.uploader.client.events.UploadErrorHandler;
import org.moxieapps.gwt.uploader.client.events.UploadProgressEvent;
import org.moxieapps.gwt.uploader.client.events.UploadProgressHandler;
import org.moxieapps.gwt.uploader.client.events.UploadStartEvent;
import org.moxieapps.gwt.uploader.client.events.UploadStartHandler;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessEvent;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessHandler;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.MultiTargetClickEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.TargetDescriptionEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.ParseUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.animation.ScrollPanelAnimation;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.ESASkyPlayerPanel;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;

/**
 * @author ESDC team (c) 2016 - European Space Agency
 */
public class TargetListPanel extends DialogBox {

    private String preparedFilenamesBaseUrl = GWT.getHostPageBaseURL() + "targetlist/";

    
    private final String [] fileNames = {"SpiralGalaxies", "PeculiarGalaxies", "InteractingGalaxies", "GalaxyClusters", "BrightNebulae",
    		"DarkNebulae", "GlobularClusters", "OpenClusters", "StarFormationRegions", "SupernovaRemnants", "SupermassiveBlackHoles",
    		"BrownDwarfs", "BrownDwarfsInMultipleSystems", "ClosestExoplanetarySystems", "CESAR_ISM", "CESAR_Galaxies", "CESAR_Colours"};
    private FlowPanel container;
    private FlowPanel uploadContainer;
    private EsaSkyUploader uploader;
    private DropDownMenu<String> preparedListDropDown;
    private Resources resources;
    private CssResource style;
    private VerticalPanel targetsContainer;
    private CloseButton closeBtn;
    private HTML targetListTitle;
    private ScrollPanel targetListScrollPanel;
    private ScrollPanelAnimation scrollPanelAnimation;
    private FlexTable targetListTable;
    private int WIDTH = 275;
    private ESASkyPlayerPanel playerPanel;
    private Image simbadLogo;
    private boolean isShowing = false;
    private boolean tryingBackupLanguage = false;

    private static final String TARGETLIST_FILES_URL = Dictionary.getDictionary("serverProperties")
            .get("targetListFilesLocation");

    public interface Resources extends ClientBundle {

        @Source("targetListPanel.css")
        @CssResource.NotStrict
        CssResource style();
        
		@Source("simbad.png")
		ImageResource simbadLogo();
    }

    public TargetListPanel() {
        super(false, false);
        resources = GWT.create(Resources.class);
        style = resources.style();
        style.ensureInjected();

        if (null != TARGETLIST_FILES_URL && !TARGETLIST_FILES_URL.isEmpty()) {
            preparedFilenamesBaseUrl = TARGETLIST_FILES_URL;
        }

        initView();
    }

    private void initView() {
        removeStyleName("gwt-DialogBox");
        setWidth(WIDTH + "px");

        closeBtn = new CloseButton();
        closeBtn.getElement().setId("closeTargetsListIcon");
        closeBtn.setTitle(TextMgr.getInstance().getText("uploadTargetList_removeTargetList"));
        closeBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                hideTargetsAndPlayerPanel();
                AladinLiteWrapper.getInstance().removeMultitargetPointer();
            }
        });

        targetsContainer = new VerticalPanel();
        targetsContainer.getElement().setId("targetsAndPlayerPanel");

        FlowPanel buttonPanel = new FlowPanel();
        buttonPanel.getElement().setId("targetAndPlayerHeader");

        targetListTitle = new HTML(TextMgr.getInstance().getText("uploadTargetList_targetList"));
        targetListTitle.getElement().setId("targetListHeaderLabel");

        buttonPanel.add(closeBtn);
        buttonPanel.add(targetListTitle);
        targetsContainer.add(buttonPanel);

        simbadLogo = new Image(resources.simbadLogo());
        simbadLogo.addStyleName("targetListPanel__simbadLogo");
        targetsContainer.add(simbadLogo);
        
        targetListScrollPanel = new ScrollPanel();
        targetsContainer.add(targetListScrollPanel);
        scrollPanelAnimation = new ScrollPanelAnimation(targetListScrollPanel);

        playerPanel = new ESASkyPlayerPanel("TargetListPlayer");
        playerPanel.setStyleName("uploadTargetPlayer");
        targetsContainer.add(playerPanel);

        container = new FlowPanel();

        container.add(new PopupHeader(this, 
                TextMgr.getInstance().getText("uploadTargetList_title"), 
                TextMgr.getInstance().getText("uploadTargetList_targetListDescriptionText")));

        uploadContainer = new FlowPanel();
        uploadContainer.setStyleName("uploadContainer");

        uploadContainer.add(createPreparedListDropDown());

        Label orUploadLabel = new Label();
        orUploadLabel.setText(TextMgr.getInstance().getText("uploadTargetList_orUpload"));
        orUploadLabel.setStyleName("orUploadLabel");
        uploadContainer.add(orUploadLabel);

        initUploader();
        uploadContainer.add(uploader);

        container.add(uploadContainer);
        container.add(targetsContainer);

        hideTargetsAndPlayerPanel();

        getElement().setId("uploadTargetListPanel");
        add(container);

        MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {
                updateMaxSize();
            }
        });
    }

    private void updateMaxSize() {
        int MIN_MARGIN_RIGHT = 30;
        int MIN_MARGIN_BOTTOM = 30;
        int maxWidth = MainLayoutPanel.getMainAreaAbsoluteLeft() + MainLayoutPanel.getMainAreaWidth() - MIN_MARGIN_RIGHT - getAbsoluteLeft();
        int maxHeight = MainLayoutPanel.getMainAreaAbsoluteTop() + MainLayoutPanel.getMainAreaHeight() - MIN_MARGIN_BOTTOM - getAbsoluteTop();

        getElement().getStyle().setProperty("maxWidth", maxWidth + "px");
        getElement().getStyle().setProperty("maxHeight", maxHeight + "px");
    }

    /**
     * initUploader().
     */
    private void initUploader() {

        uploader = new EsaSkyUploader();
        uploader.getElement().setId("uploader");

        uploader.setUploadURL(EsaSkyWebConstants.FILE_RESOLVER_URL);

        uploader.setFileSizeLimit("50 MB");
        uploader.setFileTypes("*.*;*.txt");
        uploader.setButtonText("<span class=\"uploaderButton\">"
                                    + TextMgr.getInstance().getText("uploadTargetList_uploadTargetList") 
                             + "</span>");

        uploader.setUploadProgressHandler(new UploadProgressHandler() {

            @Override
            public boolean onUploadProgress(final UploadProgressEvent uploadProgressEvent) {
                String progress = "...";
                if (uploadProgressEvent.getBytesTotal() > 0) {
                    progress = NumberFormat.getPercentFormat().format(
                            uploadProgressEvent.getBytesComplete()
                                    / uploadProgressEvent.getBytesTotal());
                }
                Log.info("Into onUploadProgress(" + progress + ")...");
                return true;
            }
        });

        uploader.setFileQueueErrorHandler(new FileQueueErrorHandler() {

            @Override
            public boolean onFileQueueError(final FileQueueErrorEvent fileQueueErrorEvent) {
                Log.info("Into onFileQueueError()...");
                Window.alert("Upload of file "
                        + fileQueueErrorEvent.getFile().getName()
                        + " "
                        + TextMgr.getInstance().getText("uploadTargetList_uploadTargetListFailed") 
                        + " [" + fileQueueErrorEvent.getErrorCode().toString() + "]: "
                        + fileQueueErrorEvent.getMessage());

                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TargetList,
                        GoogleAnalytics.ACT_TargetList_UploadError, fileQueueErrorEvent.getFile()
                                .getName()
                                + " - QueueError: "
                                + fileQueueErrorEvent.getErrorCode().toString());

                return true;
            }
        });

        uploader.setUploadErrorHandler(new UploadErrorHandler() {

            @Override
            public boolean onUploadError(final UploadErrorEvent uploadErrorEvent) {
                Log.info("Into onUploadError()...");
                CommonEventBus.getEventBus().fireEvent(
                        new ProgressIndicatorPopEvent("upload"));
                Window.alert("Upload of file "
                        + uploadErrorEvent.getFile().getName()
                        + " "
                        + TextMgr.getInstance().getText("uploadTargetList_uploadTargetListFailed") 
                        + " [" + uploadErrorEvent.getErrorCode().toString() + "]: "
                        + uploadErrorEvent.getMessage());

                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TargetList,
                        GoogleAnalytics.ACT_TargetList_UploadError, uploadErrorEvent.getFile()
                                .getName()
                                + " - Error: "
                                + uploadErrorEvent.getErrorCode().toString());

                return true;
            }
        });

        uploader.setUploadStartHandler(new UploadStartHandler() {

            @Override
            public boolean onUploadStart(UploadStartEvent uploadStartEvent) {
                playerPanel.resetPlayerEntries();
                return true;
            }
        });

        uploader.setUploadSuccessHandler(new UploadSuccessHandler() {

            @Override
            public boolean onUploadSuccess(final UploadSuccessEvent uploadSuccessEvent) {
                Log.debug("[TargetPresenter][onUploadSuccess]");

                final String fileName = uploadSuccessEvent.getFile().getName();

                Log.info("File "
                        + fileName
                        + " uploaded successfully at "
                        + NumberFormat.getDecimalFormat().format(
                                uploadSuccessEvent.getFile().getAverageSpeed() / 1024)
                        + " kilobit per second");

                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("upload"));

                final List<ESASkySearchResult> searchResult = ParseUtils
                        .parseJsonSearchResults(uploadSuccessEvent.getServerData());

                setTargetsTableData(
                        searchResult,
                        TextMgr.getInstance().getText("uploadTargetList_targetList") + "<br>" + fileName);

                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TargetList,
                        GoogleAnalytics.ACT_TargetList_UploadSuccess, fileName);

                return true;
            }
        });

        uploader.setFileDialogCompleteHandler(new FileDialogCompleteHandler() {

            @Override
            public boolean onFileDialogComplete(
                    final FileDialogCompleteEvent fileDialogCompleteEvent) {
                Log.debug("[TargetPresenter][onFileDialogComplete]");
                if (fileDialogCompleteEvent.getTotalFilesInQueue() > 0
                        && uploader.getStats().getUploadsInProgress() <= 0) {
                    Log.info("0%");
                    CommonEventBus.getEventBus().fireEvent(
                            new ProgressIndicatorPushEvent("upload", TextMgr.getInstance().getText(
                                    "uploadTargetList_resolvingTargetList")));
                    if (AladinLiteWrapper.getAladinLite().getCooFrame()
                            .equals(AladinLiteConstants.FRAME_J2000)) {
                        Log.debug("Setting J2000 UPLOADER ");
                        uploader.setUploadURL(EsaSkyWebConstants.FILE_RESOLVER_J2000_URL);
                    } else {
                        Log.debug("Setting GALACTIC UPLOADER ");
                        uploader.setUploadURL(EsaSkyWebConstants.FILE_RESOLVER_GALACTIC_URL);
                    }
                    uploader.startUpload();
                }
                return true;
            }
        });
    }

    private DropDownMenu<String> createPreparedListDropDown() {

        preparedListDropDown = new DropDownMenu<String>(
                TextMgr.getInstance().getText("uploadTargetList_selectList"), 
                TextMgr.getInstance().getText("uploadTargetList_selectList"), 207, "preparedListDropDown");

        preparedListDropDown.registerObserver(new MenuObserver() {

            @Override
            public void onSelectedChange() {
            	tryingBackupLanguage = false;
                getPreparedTargetList(TextMgr.getInstance().getLangCode());
            }
        });


        for (String filename : fileNames) {
            String title = TextMgr.getInstance().getText(filename);
            MenuItem<String> dropdownItem = new MenuItem<String>(filename, title, title, true);
            preparedListDropDown.addMenuItem(dropdownItem);
        }

        return preparedListDropDown;
    }

    private final void setSelectedTarget(final int index) {
        int TARGET_OVERLAP_SIZE = 2;
        double widgetPosition = 0;
        TargetWidget widget;
        for (int i = 0; i < targetListTable.getRowCount(); i++) {
            widget = (TargetWidget) targetListTable.getWidget(i, 0);
            widget.removeSelectedStyle();
            if (i < index) {
                widgetPosition += (widget.getOffsetHeight() - TARGET_OVERLAP_SIZE);
            }
        }

        final TargetWidget selectedWidget = (TargetWidget) targetListTable.getWidget(index, 0);
        selectedWidget.setSelectedStyle();
        CommonEventBus.getEventBus().fireEvent(
                new MultiTargetClickEvent(selectedWidget.getTargetObject(), index, false));
        if (widgetPosition < targetListScrollPanel.getVerticalScrollPosition()) {
            scrollTo(widgetPosition);

        } else if (widgetPosition + selectedWidget.getOffsetHeight() > targetListScrollPanel
                .getVerticalScrollPosition() + targetListScrollPanel.getOffsetHeight()) {
            if (widgetPosition > targetListScrollPanel.getMaximumVerticalScrollPosition()) {
                scrollTo(targetListScrollPanel.getMaximumVerticalScrollPosition());
            } else {
                scrollTo(widgetPosition - targetListScrollPanel.getOffsetHeight()
                        + selectedWidget.getOffsetHeight());
            }
        }

        if (selectedWidget.getTargetDescription() != null
                && !selectedWidget.getTargetDescription().isEmpty()) {
            CommonEventBus.getEventBus().fireEvent(
            		new TargetDescriptionEvent(selectedWidget.getNameofSelected(), selectedWidget.getTargetDescription()));
        }
    }

    private void scrollTo(double to) {
        scrollPanelAnimation.animateTo(to, 300);
    }

    public void setTargetsTableData(List<ESASkySearchResult> inputData, String title) {
        Log.debug("Setting table data...");

        targetsContainer.setVisible(true);
        playerPanel.resetPlayerEntries();
        if (targetListScrollPanel != null) { // remove previous component
            targetListScrollPanel.clear();
            AladinLiteWrapper.getInstance().removeMultitargetPointer();
        }

        targetListTable = new FlexTable();
        targetListTable.getElement().setId("targetListTable");

        Log.debug(inputData.size() + " target(s) found");

        ESASkySearchResult firstValidTarget = null;
        for (ESASkySearchResult currTarget : inputData) {
            final int index = inputData.indexOf(currTarget);
            TargetWidget currTargetWidget = new TargetWidget(currTarget, WIDTH);
            currTargetWidget.registerObserver(new TargetObserver() {

                @Override
                public void onTargetSelectionEvent(TargetWidget newlySelectedTarget) {
                    setSelectedTarget(index);
                }
            });
            targetListTable.setWidget(index, 0, currTargetWidget);
            if (currTarget.getValidInput()) {
                addPolygons(currTarget, index);
                playerPanel.addEntryToPlayer(currTargetWidget);
                if(firstValidTarget == null) {
                	firstValidTarget = currTarget;
                }
            }
        }

        targetListScrollPanel.getElement().setId("targetListScrollPanel");
        targetListScrollPanel.add(targetListTable);

        targetListTitle.setHTML(title);
        uploadContainer.setVisible(false);

        if (inputData.size() > 0 && firstValidTarget != null) {
            setSelectedTarget(inputData.indexOf(firstValidTarget));
        }
    }

    private void addPolygons(ESASkySearchResult currEntity, Integer idx) {

        Map<String, String> details = new HashMap<String, String>();

        details.put(MultiTargetSourceConstants.SIMBAD_MAIN_ID, currEntity.getSimbadMainId());
        details.put(
                MultiTargetSourceConstants.CATALOGUE_NAME,
                TextMgr.getInstance().getText("uploadTargetListPanel_MultiTargetCatalog"));
        details.put(MultiTargetSourceConstants.SOURCE_IDX, idx.toString());
        String targetName;
        if (currEntity.getUserInputType() == SearchInputType.BIBCODE
                || currEntity.getUserInputType() == SearchInputType.AUTHOR) {
            targetName = currEntity.getSimbadMainId();
        } else {
            targetName = currEntity.getUserInput();
        }
        details.put(MultiTargetSourceConstants.USER_INPUT, targetName);
        details.put(EsaSkyWebConstants.SOURCE_TYPE,
                EsaSkyWebConstants.SourceType.MULTITARGET.toString());

        details.put("cooFrame", currEntity.getCooFrame());
        String jsRa = currEntity.getUserRaDeg();
        String jsDec = currEntity.getUserDecDeg();

        jsRa = currEntity.getSimbadRaDeg();
        jsDec = currEntity.getSimbadDecDeg();

        if (currEntity.getCooFrame() != null &&currEntity.getCooFrame().equals(AladinLiteConstants.FRAME_GALACTIC)) {
    		Double[] raDecDegJ2000 = CoordinatesConversion.convertPointEquatorialToGalactic(
    				Double.parseDouble(jsRa), Double.parseDouble(jsDec));
    		details.put(MultiTargetSourceConstants.RA_DEG, Double.toString(raDecDegJ2000[0]));
    		details.put(MultiTargetSourceConstants.DEC_DEG, Double.toString(raDecDegJ2000[1]));
        } else {
        	details.put(MultiTargetSourceConstants.RA_DEG, jsRa);
        	details.put(MultiTargetSourceConstants.DEC_DEG, jsDec);
        }

        JavaScriptObject sourceObj = AladinLiteWrapper.getAladinLite().newApi_createSourceJSObj(
                jsRa, jsDec, details);
        AladinLiteWrapper.getAladinLite().newApi_addSourceToCatalogue(
                AladinLiteWrapper.getInstance().getMultiTargetCatalogue(), sourceObj);
    }
    
    private final void hideTargetsAndPlayerPanel() {
        playerPanel.resetPlayerEntries();
        targetsContainer.setVisible(false);
        uploadContainer.setVisible(true);
        preparedListDropDown.clearSelection();
    }

    public void toggle() {
        if (isShowing) {
            hide();
        } else {
            show();
        }
    }

	@Override
	public void show() {
		isShowing = true;
		this.removeStyleName("displayNone");
		updateMaxSize();
	}

	@Override
	public void hide(boolean autohide) {
		this.addStyleName("displayNone");
		isShowing = false;
		CloseEvent.fire(this, null);
	}
	
	@Override
	public void setPopupPosition(int left, int top) {
	}

    public void readPreparedTargetList(String url, final String title) {

        Log.debug("readPreparedTargetList from url: " + url);

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {

            builder.sendRequest(null, new RequestCallback() {

                public void onError(Request request, Throwable ex) {
                    Log.error("readPreparedTargetList onError", ex);
                    if(!TextMgr.getInstance().getLangCode().equalsIgnoreCase("en") && !tryingBackupLanguage) {
                    	tryingBackupLanguage = true;
                    	getPreparedTargetList("en");
                    }
                }

                public void onResponseReceived(Request request, Response response) {
                    try {
                        final List<ESASkySearchResult> searchResult = ParseUtils
                                .parseJsonSearchResults(response.getText());
                        setTargetsTableData(searchResult, title);

                    } catch (Exception ex) {
                    	onError(request, ex);
                    }
                }
            });

        } catch (RequestException ex) {
            Log.error("readPreparedTargetList error:", ex);
        }
    }
    
    private void getPreparedTargetList(String langCode) {
    	final String filename = preparedFilenamesBaseUrl
                + preparedListDropDown.getSelectedObject() + "_"
                + langCode + ".json?v=" + GWT.getModuleName();
        readPreparedTargetList(
                filename,
                TextMgr.getInstance().getText(
                        preparedListDropDown.getSelectedObject()));
        GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TargetList,
                GoogleAnalytics.ACT_TargetList_ListSelected,
                preparedListDropDown.getSelectedObject());
    }

}
