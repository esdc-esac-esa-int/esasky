package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.event.ShowEvaEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.AllSkyPresenter;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class AllSkyPanel extends ResizeLayoutPanel implements AllSkyPresenter.View {

    private ZoomControlPanel zoomControlPanel;
    private SelectionToolBoxPanel selectionPanel;
    private static Resources resources = GWT.create(Resources.class);
    private CssResource style;
    private VerticalPanel allSkyContainerPanel;
    private AllSkyFocusPanel aladinLiteFocusPanel;

    private String initialTarget;
    private String fovFromUrl;
    private String coordinateFrameFromUrl;

    private Image esaLogo;
    private EsaSkyButton evaButton;
    private Tooltip tooltip;

    /**
     * A ClientBundle that provides images for this widget.
     */
    public interface Resources extends ClientBundle {

        @Source("ESA_logo_2020_White.png")
        @ImageOptions(flipRtl = true)
        ImageResource logo();

        @Source("allSkyPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    /**
     * AllSkyPanel().
     * @param inputInitialHiPS Input HiPS object.
     * @param inputTarget Input String
     */
    public AllSkyPanel(final String inputTarget, final String fovFromUrl, final String coordinateFrameFromUrl) {
        this.initialTarget = inputTarget;
        this.fovFromUrl = fovFromUrl;
        this.coordinateFrameFromUrl = coordinateFrameFromUrl;

        style = resources.style();
        style.ensureInjected();

        initView();
    }

    private class ResizeAladinTimer extends Timer {

    	private int width;
    	private int height;

    	public void setNewSize(int width, int height) {
    		this.width = width;
    		this.height = height;
    		resizeAladinTimer.schedule(50);
    	}

		@Override
		public void run() {
        	Scheduler.get().scheduleFinally(() -> resizeAladin(width, height));
		}
	};

	private ResizeAladinTimer resizeAladinTimer = new ResizeAladinTimer();

    /**
     * initView().
     */
    private void initView() {
        this.allSkyContainerPanel = new VerticalPanel();
        AladinLiteWrapper.init(this.allSkyContainerPanel, null, initialTarget, fovFromUrl, coordinateFrameFromUrl);
        this.aladinLiteFocusPanel = AllSkyFocusPanel.getInstance();

        addResizeHandler(event -> resizeAladinTimer.setNewSize(event.getWidth(), event.getHeight()));

        FlowPanel zoomAndSelectionToolBox = new FlowPanel();
        zoomAndSelectionToolBox.addStyleName("zoomAndSelectionContainer");
        // build zoom control panel
        this.zoomControlPanel = new ZoomControlPanel();
        this.selectionPanel = new SelectionToolBoxPanel();
        if(!DeviceUtils.isMobileOrTablet()) {
            zoomAndSelectionToolBox.add(zoomControlPanel);
        }
        zoomAndSelectionToolBox.add(selectionPanel);

        esaLogo = new Image(resources.logo());
        esaLogo.getElement().setId("allSkyESALogo");
        esaLogo.addClickHandler(event -> Window.open("//www.esa.int", "_blank", ""));


        if(Modules.getModule(EsaSkyWebConstants.MODULE_EVA_MENU)) {
            evaButton = new EsaSkyButton(Icons.getChatIcon());
            evaButton.getElement().setId("allSkyEvaButton");
            evaButton.setTitle(TextMgr.getInstance().getText("header_eva"));
            evaButton.setBigStyle();
            evaButton.addClickHandler(event -> {
                CommonEventBus.getEventBus().fireEvent(new ShowEvaEvent());
                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_EVA, GoogleAnalytics.ACT_SKIES_PANEL_EVA_TOGGLE, "");
            });

            this.allSkyContainerPanel.add(evaButton);
        }



        // Get aladinLite instance a wrap it into an aladinLiteFocusPanel
        this.aladinLiteFocusPanel.add(AladinLiteWrapper.getAladinLite());

        // wrap everything into the 'allSkyContainerPanel'
        this.allSkyContainerPanel.add(this.aladinLiteFocusPanel);

        this.allSkyContainerPanel.add(zoomAndSelectionToolBox);

        this.allSkyContainerPanel.add(esaLogo);


        add(this.allSkyContainerPanel);

        MainLayoutPanel.addMainAreaResizeHandler(event -> updateElementsWithAladinLogo());
        updateElementsWithAladinLogo();
        addStyleName("notSelectable");
    }

    private void updateElementsWithAladinLogo() {
    	if(MainLayoutPanel.getMainAreaWidth() <= 800) {
            esaLogo.getElement().getStyle().setWidth(57, Unit.PX);
            esaLogo.getElement().getStyle().setRight(37, Unit.PX);

            if(evaButton != null) {
                esaLogo.getElement().getStyle().setRight(96, Unit.PX);
                evaButton.setSmallStyle();
                evaButton.getElement().getStyle().setRight(18, Unit.PX);
                evaButton.getElement().getStyle().setBottom(5, Unit.PX);
                moveAladinLogo();
            }
    	} else {
            esaLogo.getElement().getStyle().setWidth(94, Unit.PX);
            esaLogo.getElement().getStyle().setRight(86, Unit.PX);

            if(evaButton != null) {
                esaLogo.getElement().getStyle().setRight(136, Unit.PX);
                evaButton.setBigStyle();
                evaButton.getElement().getStyle().setRight(10, Unit.PX);
                evaButton.getElement().getStyle().setBottom(10, Unit.PX);
                moveAladinLogo();
            }
    	}
    }

    @Override
    protected final void onLoad() {
        Log.debug("[AllSkyPanel] Inside onLoad()");
        super.onLoad();
    }

    /**
     * resizeAladin().
     * @param width Input Integer
     * @param height Input height.
     */
    private void resizeAladin(final int width, final int height) {
        AladinLiteWrapper.getAladinLite().resize(width, height, Unit.PX);
    }

    @Override
    public final VerticalPanel getAllSkyContainerPanel() {
        return this.allSkyContainerPanel;
    }

    @Override
    public final void showSourceTooltip(final Tooltip newTooltip) {
        if (tooltip != null) {
            tooltip.removeFromParent();
        }

        tooltip = newTooltip;

        if (tooltip != null) {
            tooltip.show(AladinLiteWrapper.getAladinLite().getCooFrame());
        }
    }

    @Override
    public final void deToggleSelectionMode() {
    	this.selectionPanel.deToggleAllButtons();
    }

    @Override
    public final void areaSelectionKeyboardShortcutStart() {
        this.selectionPanel.areaSelectionKeyboardShortcutStart();
    }

    @Override
    public final HasClickHandlers getZoomInClickHandler() {
        return this.zoomControlPanel.getZoomInClickHandler();
    }

    @Override
    public final HasClickHandlers getZoomOutClickHandler() {
        return this.zoomControlPanel.getZoomOutClickHandler();
    }

    @Override
    public final void hideTooltip() {
        if (tooltip != null) {
            tooltip.hide();
        }
    }

    private void moveAladinLogo() {
        Element logoContainer =  findAladinLogoContainer(false);
        if(logoContainer != null && !logoContainer.getClassName().contains("aladin-logo-container-eva")) {
            logoContainer.setClassName("aladin-logo-container-eva");
        }
    }
    private native Element findAladinLogoContainer(boolean moveRight) /*-{
         return $doc.querySelector(".aladin-logo-container");
    }-*/;

}
