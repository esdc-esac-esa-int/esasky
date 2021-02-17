package esac.archive.esasky.cl.web.client.view.allskypanel;

import java.util.LinkedList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.CoordinatesObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.ExternalServices;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.AutoHidePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;

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
	private int lastKnownNumberOfTouches = 0;
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

		@Source("allSkyFocusPanel.css")
		@CssResource.NotStrict
		CssResource style();
	}

	private AllSkyFocusPanel() {
		super();
		style = resources.style();
		style.ensureInjected();
		createPopupMenu();
		sinkEvents(Event.ONMOUSEUP | Event.ONCONTEXTMENU | Event.ONTOUCHSTART | Event.ONTOUCHMOVE | Event.ONMOUSEDOWN
				| Event.ONTOUCHCANCEL | Event.ONTOUCHEND | Event.ONCLICK | Event.ONFOCUS | Event.TOUCHEVENTS);

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
			break;

		default:
			break;

		}
		notifyObservers();
	}

	public final void openContextMenu(final Event event) {
		mouseX = event.getClientX() - MainLayoutPanel.getMainAreaAbsoluteLeft();
		mouseY = event.getClientY() - MainLayoutPanel.getMainAreaAbsoluteTop();

		DisplayUtils.showInsideMainAreaPointingAtPosition(contextMenu, mouseX, mouseY);

		Log.debug(AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getRaDeg() + " dec "
				+ AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getDecDeg());
	}

	private void createPopupMenu() {

		searchInSimbadButton = new EsaSkyButton(this.resources.simbad());
		searchInSimbadButton.setTitle(TextMgr.getInstance().getText("AllSkyFocusPanel_searchInSimbad"));
		searchInSimbadButton.setVeryBigStyle();
		searchInSimbadButton.addStyleName("allSkyButton");
		searchInSimbadButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				contextMenu.hide();

				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ContextMenu,
						GoogleAnalytics.ACT_ContextMenu_SearchInSimbad,
						"RA: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getRaDeg()
								+ " Dec: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY)
										.getDecDeg());
				Window.open(ExternalServices.buildSimbadURLWithRaDec(
						AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getRaDeg(),
						AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getDecDeg(),
						AladinLiteWrapper.getAladinLite().getCooFrame()), "_blank", "");
			}
		});

		searchInNedButton = new EsaSkyButton(this.resources.ned());
		searchInNedButton.setTitle(TextMgr.getInstance().getText("AllSkyFocusPanel_searchInNed"));
		searchInNedButton.addStyleName("allSkyButton");
		searchInNedButton.setVeryBigStyle();
		searchInNedButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				contextMenu.hide();

				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ContextMenu, GoogleAnalytics.ACT_ContextMenu_SearchInNed,
						"RA: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getRaDeg()
								+ " Dec: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY)
										.getDecDeg());
				Window.open(ExternalServices.buildNedURL(
						AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getRaDeg(),
						AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getDecDeg(),
						AladinLiteWrapper.getAladinLite().getCooFrame()), "_blank", "");
			}
		});

		searchInVizierPhotometryButton = new EsaSkyButton(this.resources.vizierPhotometry());
		searchInVizierPhotometryButton
				.setTitle(TextMgr.getInstance().getText("AllSkyFocusPanel_searchInVizierPhotometry"));
		searchInVizierPhotometryButton.setVeryBigStyle();
		searchInVizierPhotometryButton.addStyleName("allSkyButton");
		searchInVizierPhotometryButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				contextMenu.hide();
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ContextMenu,
						GoogleAnalytics.ACT_ContextMenu_SearchInVizierPhotometry,
						"RA: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getRaDeg()
								+ " Dec: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY)
										.getDecDeg());
				Window.open(ExternalServices.buildVizierPhotometryURL(
						AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getRaDeg(),
						AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getDecDeg(),
						AladinLiteWrapper.getAladinLite().getCooFrame()), "_blank", "");

			}
		});

		EsaSkyButton searchInVizierButton = new EsaSkyButton(this.resources.vizier());
		searchInVizierButton.setTitle(TextMgr.getInstance().getText("AllSkyFocusPanel_searchInVizier"));
		searchInVizierButton.setVeryBigStyle();
		searchInVizierButton.addStyleName("allSkyButton");
		searchInVizierButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				contextMenu.hide();
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ContextMenu,
						GoogleAnalytics.ACT_ContextMenu_SearchInVizier,
						"RA: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getRaDeg()
								+ " Dec: " + AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY)
										.getDecDeg());
				Window.open(ExternalServices.buildVizierURL(
						AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getRaDeg(),
						AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX, mouseY).getDecDeg(),
						AladinLiteWrapper.getAladinLite().getCooFrame()), "_blank", "");

			}
		});

		EsaSkyButton wwtButton = new EsaSkyButton(this.resources.wwtLogo());
		wwtButton.setVeryBigStyle();
		wwtButton.addStyleName("allSkyButton");

		wwtButton.setTitle(TextMgr.getInstance().getText("header_viewInWWTFull"));
		wwtButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				CoordinatesObject coordinateOfPress = AladinLiteWrapper.getAladinLite().convertMouseXYToRaDecDeg(mouseX,
						mouseY);
				Coordinate j2000Coordinate = CoordinateUtils.getCoordinateInJ2000(coordinateOfPress.getRaDeg(),
						coordinateOfPress.getDecDeg());
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_ContextMenu, GoogleAnalytics.ACT_ContextMenu_ViewInWwt,
						"RA: " + j2000Coordinate.ra + " Dec: " + j2000Coordinate.dec);
				Window.open(ExternalServices.buildWwtURLJ2000(j2000Coordinate.ra, j2000Coordinate.dec), "_blank", "");
			}
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

		Label searchLabel = new Label(TextMgr.getInstance().getText("AllSkyFocusPanel_searchIn"));
		FlowPanel container = new FlowPanel();
		container.add(searchLabel);
		container.add(searchButtons);
		contextMenu.add(container);
		contextMenu.addStyleName("AllSkyRightClickPopup");

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

}
