package esac.archive.esasky.cl.web.client.view;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.presenter.BannerPresenter;
import esac.archive.esasky.cl.web.client.presenter.HeaderPresenter.View;
import esac.archive.esasky.cl.web.client.presenter.MainPresenter;
import esac.archive.esasky.cl.web.client.view.allskypanel.AllSkyPanel;
import esac.archive.esasky.cl.web.client.view.banner.Banner;
import esac.archive.esasky.cl.web.client.view.banner.Banner.Side;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.CtrlToolBar;
import esac.archive.esasky.cl.web.client.view.evapanel.EvaPanel;
import esac.archive.esasky.cl.web.client.view.header.HeaderPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ResultsPanel;
import esac.archive.esasky.cl.web.client.view.searchpanel.SearchPanel;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class MainLayoutPanel extends Composite implements MainPresenter.View {

    private AllSkyPanel allSkyPanel;

    private SearchPanel searchPanel;

    private EvaPanel evaPanel;

    /** This panel handles the entire page layout. */
    private FlowPanel skeletonPanel;

    private CtrlToolBar ctrlToolBar;
    
    private ResultsPanel resultsPanel;
    
    private HeaderPanel header;
    
    private Banner banner;

    private String targetFromURL;

    private String fovFromUrl;
    
    private String HiPSFromURL;

    private String coordinateFrameFromUrl;
    
    private static FlowPanel mainArea = new FlowPanel();
    
    private static ResizeLayoutPanel mainAreaResizeDetector = new ResizeLayoutPanel();
    
    public static final Resources resources = GWT.create(Resources.class);

    private final CssResource style;

    private static  WelcomeDialog welcomeDialogBox;
    
    private boolean hideWelcome;
    
    public interface Resources extends ClientBundle {

        @Source("mainlayoutpanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    /**
     * ConstructorClass.
     * @param HiPSFromURL Input Hips
     * @param inputTarget Input String 
     */
    public MainLayoutPanel(final String HiPSFromURL,
            final String targetFromURL, String fovFromUrl, final String coordinateFrameFromUrl, boolean hideWelcome) {
        this.HiPSFromURL = HiPSFromURL;
        this.targetFromURL = targetFromURL;
        this.fovFromUrl = fovFromUrl;
        this.coordinateFrameFromUrl = coordinateFrameFromUrl;
        this.hideWelcome = hideWelcome;

        this.style = resources.style();
        this.style.ensureInjected();

        initView();
    }

    private void initView() {

        skeletonPanel = new FlowPanel();
        skeletonPanel.getElement().setId("skeletonPanel");

        allSkyPanel = new AllSkyPanel(targetFromURL, fovFromUrl, coordinateFrameFromUrl);
        
        banner = new Banner(Side.TOP);
        skeletonPanel.add(banner);
        
        mainAreaResizeDetector.getElement().setId("everythingExceptBanner");
        header = new HeaderPanel();
        mainArea.add(header);

        allSkyPanel.setHeight("100%");
        allSkyPanel.setWidth("100%");
        allSkyPanel.setVisible(true);

        ctrlToolBar = new CtrlToolBar(HiPSFromURL);

        ctrlToolBar.getElement().setClassName("ctrlToolBar");
        ctrlToolBar.getElement().setId("ctrlToolBar");
        ctrlToolBar.setStyleName("ctrlToolBar");

        searchPanel = new SearchPanel();
        
        evaPanel = new EvaPanel();
        evaPanel.getElement().setClassName("evaPanel");
        evaPanel.getElement().setId("evaPanel");
        evaPanel.setStyleName("evaPanel");
        
        resultsPanel = new ResultsPanel();
        
        FlowPanel openSeadragonCanvas = new FlowPanel();
        openSeadragonCanvas.getElement().setId("openseadragonCanvas");
        mainArea.add(openSeadragonCanvas);
        mainArea.add(ctrlToolBar);
        mainArea.add(allSkyPanel);
        mainArea.add(searchPanel);
        mainArea.add(resultsPanel);
        mainAreaResizeDetector.addResizeHandler(new ResizeHandler() {
        	
        	@Override
        	public void onResize(ResizeEvent event) {
        		notifyMainAreaResized(event);
        	}
        });
        
        mainAreaResizeDetector.add(mainArea);
        skeletonPanel.add(mainAreaResizeDetector);
        skeletonPanel.add(bottomBanner);

        // Ending tasks
        // ----------------------------------------------------------------------------------------

        // Get rid of scrollbars, and clear out the window's built-in margin,
        // because we want to take advantage of the entire client area.
        Window.enableScrolling(false);
        Window.setMargin("0px");
        initWidget(skeletonPanel);
        
//        leftBanner.setHeight("100%");
        leftBanner.setWidth("50px");
        RootPanel.get().add(leftBanner);
    }
    
    @Override 
    protected void onLoad() {
    	super.onLoad();
//        rightSideBanner.setHeight("100%");
        rightSideBanner.setWidth("50px");
        RootPanel.get().add(rightSideBanner);
        if(!hideWelcome) {
        	welcomeDialogBox = new WelcomeDialog();
        	welcomeDialogBox.show();
        }
    }
    
    private Banner leftBanner = new Banner(Side.LEFT);
    private Banner rightSideBanner = new Banner(Side.RIGHT);
    private Banner bottomBanner = new Banner(Side.BOTTOM);
    
    @Override
    public final AllSkyPanel getAllSkyPanel() {
        return this.allSkyPanel;
    }

    @Override
    public final SearchPanel getSearchPanel() {
        return this.searchPanel;
    }
    
    @Override
    public final void toggleEvaPanel() {
    	if(!evaPanel.isShowing()) {
    		if(!evaPanel.hasBeenInitialised()) {
	    		rightSideBanner.setWidget(evaPanel);
	    		rightSideBanner.setSize(350);
	    		rightSideBanner.getElement().addClassName("evaBanner");
	    		rightSideBanner.addCloseButtonClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						toggleEvaPanel();
					}
				});
	    		evaPanel.init();
    		}
    		rightSideBanner.show();
    		evaPanel.setShowing(true);
    	}
    	else {
    		rightSideBanner.hide();
    		evaPanel.setShowing(false);
    	}
    }

    @Override
    public final CtrlToolBar getCtrlToolBar() {
        return this.ctrlToolBar;
    }

	@Override
	public View getHeaderPanel() {
		return header;
	}
	
	@Override
	public BannerPresenter.View getBannerPanel() {
		return banner;
	}
	
	public static void addElementToMainArea(Widget widget) {
		mainArea.add(widget);
	}
	
	public static void removeElementFromMainArea(Widget widget) {
		mainArea.remove(widget);
	}
	
	public static int getMainAreaHeight() {
		return mainArea.getOffsetHeight();
	}
	
	public static int getMainAreaAbsoluteTop() {
		return mainArea.getElement().getAbsoluteTop();
	}
	
	public static int getMainAreaWidth() {
		return mainArea.getOffsetWidth();
	}
	
	public static int getMainAreaAbsoluteLeft() {
		return mainArea.getElement().getAbsoluteLeft();
	}
	
	private static List<ResizeHandler> mainAreaResizeHandlers = new LinkedList<ResizeHandler>();
	
	public static void addMainAreaResizeHandler(ResizeHandler resizeHandler) {
		mainAreaResizeHandlers.add(resizeHandler);
	}
	
	public static void removeMainAreaResizeHandler(ResizeHandler resizeHandler) {
		mainAreaResizeHandlers.remove(resizeHandler);
	}
	
	public void notifyMainAreaResized(ResizeEvent event) {
		for (ResizeHandler resizeHandler : mainAreaResizeHandlers) {
			resizeHandler.onResize(event);
		}
	}

	@Override
	public esac.archive.esasky.cl.web.client.presenter.BannerPresenter.View getBannerPanelLeftSide() {
		return leftBanner;
	}
	
	@Override
	public esac.archive.esasky.cl.web.client.presenter.BannerPresenter.View getBannerPanelRightSide() {
		return rightSideBanner;
	}
	
	@Override
	public esac.archive.esasky.cl.web.client.presenter.BannerPresenter.View getBannerPanelBottom() {
		return bottomBanner;
	}

	@Override
	public ResultsPanel getResultsPanel() {
		return resultsPanel;
	}
}
