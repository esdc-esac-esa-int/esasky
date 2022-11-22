package esac.archive.esasky.cl.web.client.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.presenter.BannerPresenter;
import esac.archive.esasky.cl.web.client.presenter.HeaderPresenter.View;
import esac.archive.esasky.cl.web.client.presenter.MainPresenter;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
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

	private static WelcomeDialog welcomeDialogBox;

	private boolean hideWelcome;

	private boolean isEvaRight = true;

	private int size = 390;

	private boolean isDragging = false;

	private Image dragEvaImage;

	private static final String DRAG_IMAGE_LANDSCAPE_CLASS = "dragImageLandscape";

	private static final String DRAG_IMAGE_VERTICAL_CLASS = "dragImageVertical";

	private static final String TRANSFORM_STRING = "transform";

	public interface Resources extends ClientBundle {

		@Source("mainlayoutpanel.css")
		@CssResource.NotStrict
		CssResource style();

		@Source("eva_resize.png")
		@ImageOptions(flipRtl = true)
		ImageResource resize_icon();
	}

	/**
	 * ConstructorClass.
	 * 
	 * @param HiPSFromURL Input Hips
	 * @param inputTarget Input String
	 */
	public MainLayoutPanel(final String HiPSFromURL, final String targetFromURL, String fovFromUrl,
			final String coordinateFrameFromUrl, boolean hideWelcome) {
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

		Window.addResizeHandler(new ResizeHandler() {

			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					if (dragEvaImage != null) {
						setPositionForEva();
					}

				}
			};

			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.cancel();
				resizeTimer.schedule(250);
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
		if (Window.getClientWidth() < 800) {
			bottomBanner.setWidth("50px");
		}
		rightSideBanner.setWidth("50px");

		RootPanel.get().add(rightSideBanner);

		if (!hideWelcome) {
			welcomeDialogBox = new WelcomeDialog();
			welcomeDialogBox.show();
		}

		if (Objects.equals(Modules.getMode().toUpperCase(), EsaSkyWebConstants.MODULE_MODE_KIOSK)) {
			disableAnchorElements();
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
	public final void initEvaPanel() {

		bottomBanner.hideCloseButton();
		rightSideBanner.hideCloseButton();

		dragEvaImage = new Image(resources.resize_icon());
		dragEvaImage.getElement().setId("dragEvaImage");
		dragEvaImage.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		dragEvaImage.getElement().setClassName(DRAG_IMAGE_LANDSCAPE_CLASS);
		dragEvaImage.getElement().getStyle().setPosition(Position.FIXED);
		dragEvaImage.getElement().getStyle().setRight(390, Unit.PX);
		dragEvaImage.getElement().getStyle().setTop(50, Unit.PCT);
		dragEvaImage.getElement().getStyle().setZIndex(1000);
//        dragEvaImage.getElement().setDraggable(Element.DRAGGABLE_FALSE);

//       

		this.addClickHandlerForEva();
		
		

		skeletonPanel.add(dragEvaImage);

		this.toggleEvaPanel();
	}

	private void addClickHandlerForEva() {
		this.dragEvaImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
				toggleEvaPanelWithDrag();
			}
		});
	}
	
	@Override
	public void toggleEvaPanelWithDrag() {
		toggleEvaPanel();
		if (evaPanel.isShowing()) {
			setDragImageOnClick(size);
			// dragEvaImage.getElement().getStyle().setRight(size, Unit.PX);
		} else {
			// setDragImageOnClick(0);
			if (isLandscape()) {
				dragEvaImage.getElement().getStyle().setRight(0, Unit.PX);
			} else {
				dragEvaImage.getElement().getStyle().setBottom(0, Unit.PX);
			}

		}
	}

	public void toggleEvaPanel() {
		if (!evaPanel.isShowing()) {
			if (!evaPanel.hasBeenInitialised()) {
				if (!isLandscape()) {
					bottomBanner.setWidget(evaPanel);
					bottomBanner.setSize(size);
					bottomBanner.setWidth("100%");
					bottomBanner.getElement().addClassName("evaBanner");

					this.isEvaRight = false;
				} else {
					rightSideBanner.setWidget(evaPanel);
					rightSideBanner.setSize(size);
					rightSideBanner.getElement().addClassName("evaBanner");
				}
//    			bottomBanner.addCloseButtonClickHandler(event -> {
//	    			clickEvaHide=true;
//	    			toggleEvaPanel();
//	    		});
//    			rightSideBanner.addCloseButtonClickHandler(event -> {
//	    			clickEvaHide=true;
//	    			toggleEvaPanel();
//	    		});

				evaPanel.init();
			}
			if (!isLandscape()) {
				dragEvaImage.getElement().getStyle().setBottom(size, Unit.PX);
				dragEvaImage.getElement().getStyle().setLeft(0, Unit.PX);
				dragEvaImage.getElement().getStyle().clearRight();
				dragEvaImage.getElement().getStyle().clearTop();
				dragEvaImage.getElement().getStyle().setProperty(TRANSFORM_STRING, "rotate(90deg)");
				dragEvaImage.getElement().setClassName(DRAG_IMAGE_VERTICAL_CLASS);
				bottomBanner.show();

			} else {

				dragEvaImage.getElement().getStyle().setRight(size, Unit.PX);
				dragEvaImage.getElement().getStyle().setTop(50, Unit.PCT);
				dragEvaImage.getElement().getStyle().clearLeft();
				dragEvaImage.getElement().getStyle().clearBottom();
				dragEvaImage.getElement().getStyle().setProperty(TRANSFORM_STRING, "rotate(0deg)");
				dragEvaImage.getElement().setClassName(DRAG_IMAGE_LANDSCAPE_CLASS);
				rightSideBanner.show();
			}

			evaPanel.setShowing(true);
		} else {
			if (!isLandscape()) {
				bottomBanner.hide();
			} else {
				rightSideBanner.hide();
			}
//    		rightSideBanner.hide();
			evaPanel.setShowing(false);
		}
	}

	private void setPositionForEva() {
		if (!this.isDragging) {
			size = 390;
			if (!isLandscape()) {
				setEvaForVerticalScreens();
			} else {
				setEvaForLandscapeScreens();
			}
		}
	}

	private void setEvaForVerticalScreens() {
		if (this.isEvaRight) {
			this.evaPanel.removeFromParent();
			this.isEvaRight = false;
			bottomBanner.setWidget(evaPanel);
			bottomBanner.setSize(size);
			bottomBanner.setWidth("100%");
			dragEvaImage.getElement().getStyle().setLeft(0, Unit.PCT);
			dragEvaImage.getElement().getStyle().clearRight();
			dragEvaImage.getElement().getStyle().setBottom(size, Unit.PX);
			dragEvaImage.getElement().getStyle().clearTop();
			dragEvaImage.getElement().setClassName(DRAG_IMAGE_VERTICAL_CLASS);
//			bottomBanner.addCloseButtonClickHandler(event -> {
//    			clickEvaHide=true;
//    			toggleEvaPanel();
//    		});
			if (this.evaPanel.isShowing()) {
				dragEvaImage.getElement().getStyle().setBottom(size, Unit.PX);

				bottomBanner.show();
				rightSideBanner.hide();
			} else {
				dragEvaImage.getElement().getStyle().setBottom(0, Unit.PX);
			}
			dragEvaImage.getElement().getStyle().setProperty(TRANSFORM_STRING, "rotate(90deg)");

		}
	}

	private void setEvaForLandscapeScreens() {
		if (!this.isEvaRight) {
			this.evaPanel.removeFromParent();
			this.isEvaRight = true;
			rightSideBanner.setWidget(evaPanel);
			rightSideBanner.setSize(size);
			rightSideBanner.setHeight("100%");

			dragEvaImage.getElement().getStyle().clearLeft();
			dragEvaImage.getElement().getStyle().setTop(50, Unit.PCT);
			dragEvaImage.getElement().getStyle().clearBottom();
			dragEvaImage.getElement().setClassName(DRAG_IMAGE_LANDSCAPE_CLASS);
			if (this.evaPanel.isShowing()) {
				dragEvaImage.getElement().getStyle().setRight(size, Unit.PX);
				rightSideBanner.show();
				bottomBanner.hide();
			} else {
				dragEvaImage.getElement().getStyle().setRight(0, Unit.PX);
			}
			dragEvaImage.getElement().getStyle().setProperty(TRANSFORM_STRING, "rotate(0deg)");

//    		rightSideBanner.addCloseButtonClickHandler(event -> {
//    			clickEvaHide=true;
//    			toggleEvaPanel();
//    		});

		}
	}

	private boolean isLandscape() {
		return Window.getClientWidth() > 0.9*Window.getClientHeight();
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
	
	@Override
	public boolean isEvaShowing() {
		return evaPanel.isShowing();
	}

	private void setDragImageOnClick(int inputSize) {
		Timer timer = new Timer() {
			@Override
			public void run() {
				if (isLandscape()) {
					dragEvaImage.getElement().getStyle().setRight(inputSize, Unit.PX);
				} else {
					dragEvaImage.getElement().getStyle().setBottom(inputSize, Unit.PX);
				}
			}
		};

		timer.schedule(1000);
	}

	private native void disableAnchorElements() /*-{
		var styles = "a { pointer-events: none; } img { pointer-events: none; }"

		var styleSheet = $doc.createElement("style")
		styleSheet.innerText = styles
		$doc.head.appendChild(styleSheet)
	}-*/;
	

}
