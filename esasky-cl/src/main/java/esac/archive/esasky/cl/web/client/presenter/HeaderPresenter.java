package esac.archive.esasky.cl.web.client.presenter;


import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants.CoordinateFrame;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEventHandler;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteFoVChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteFoVChangedEventHandler;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.IsInScienceModeChangeEvent;
import esac.archive.esasky.cl.web.client.event.IsInScienceModeEventHandler;
import esac.archive.esasky.cl.web.client.event.banner.ServerProblemFoundEvent;
import esac.archive.esasky.cl.web.client.event.banner.ServerProblemFoundEventHandler;
import esac.archive.esasky.cl.web.client.event.banner.ServerProblemSolvedEvent;
import esac.archive.esasky.cl.web.client.event.banner.ServerProblemSolvedEventHandler;
import esac.archive.esasky.cl.web.client.event.banner.ToggleServerProblemBannerEvent;
import esac.archive.esasky.cl.web.client.event.banner.ToggleSkyPanelEvent;
import esac.archive.esasky.cl.web.client.event.hips.HipsChangeEvent;
import esac.archive.esasky.cl.web.client.event.hips.HipsChangeEventHandler;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.CopyToClipboardHelper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.ExternalServices;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.allskypanel.AllSkyFocusPanel;
import esac.archive.esasky.cl.web.client.view.allskypanel.AllSkyFocusPanel.AllSkyFocusPanelObserver;
import esac.archive.esasky.cl.web.client.view.header.ScreenshotDialogBox;

public class HeaderPresenter {

	private View view;        

	public interface View {
		void setFov(String fov);
		void setCoordinate(String coordinate);
		void setIsCenterCoordinateStyle();
		void setIsNotCenterCoordinateStyle();
		void setAvailableCoordinateFrames(List<SelectionEntry> coordinateFrames);
		void selectCoordinateFrame(int index);
		void setHipsName(String hipsName);
		void setIsInScienceMode(boolean isInScienceMode);
		void setAvailableLanguages(List<SimpleEntry<String, String>> languages);
		void setSelectedLanguage(int index);

		void addCoordinateFrameChangeHandler(ChangeHandler changeHandler);
		String getSelectedCoordinateFrame();

		void addMenuClickHandler(ClickHandler handler);
		void addDropdownContainerBlurHandler(BlurHandler handler);
		void addDropdownContainerMouseDownHandler(MouseDownHandler handler);

		void addHipsNameClickHandler(ClickHandler handler);
		void addShareClickHandler(ClickHandler handler);
		void addHelpClickHandler(ClickHandler handler);
		void addFeedbackClickHandler(ClickHandler handler);
		void addVideoTutorialsClickHandler(ClickHandler handler);
		void addReleaseNotesClickHandler(ClickHandler handler);
		void addNewsletterClickHandler(ClickHandler handler);
		void addAboutUsClickHandler(ClickHandler handler);
		void addViewInWwtClickHandler(ClickHandler handler);
		void addScienceModeSwitchClickHandler(ClickHandler handler);
		void addScreenshotClickHandler(ClickHandler handler);
		void addLanguageSelectionChangeHandler(StringValueSelectionChangedHandler handler);
		void addWarningButtonClickHandler(ClickHandler handler);
		void addGridButtonClickHandler(ClickHandler handler);

		void showWarningButton();
		void hideWarningButton();
		void toggleDropdownMenu();
		void closeDropdownMenu();
		void toggleGrid();
		void toggleGrid(boolean show);
		
		boolean isGridOn();

		StatusPresenter.View getStatusView();
	}


	private String coordinateFrameFromUrl;

	private long timeSinceLastMouseDownInsideDropdownContainer;
	private boolean isSciModeChecked = GUISessionStatus.getIsInScienceMode();

	public HeaderPresenter(final View inputView, String coordinateFrameFromUrl) {
		this.view = inputView;
		this.coordinateFrameFromUrl = coordinateFrameFromUrl;
		bind();
		new StatusPresenter(inputView.getStatusView());
	}

	private void bind() {
		CommonEventBus.getEventBus().addHandler(AladinLiteFoVChangedEvent.TYPE, new AladinLiteFoVChangedEventHandler () {

			@Override
			public void onChangeEvent(AladinLiteFoVChangedEvent fovEvent) {
				view.setFov(formatFov(fovEvent.getFov()));
			}
		});

		CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesChangedEvent.TYPE, new AladinLiteCoordinatesChangedEventHandler() {

			@Override
			public void onCoordsChanged(AladinLiteCoordinatesChangedEvent changeEvent) {
				setCoordinates(changeEvent.getRa(), changeEvent.getDec());
				if(changeEvent.getIsViewCenterPosition()) {
					view.setIsCenterCoordinateStyle();
				} else {
					view.setIsNotCenterCoordinateStyle();
				}
			}

		});

		CommonEventBus.getEventBus().addHandler(HipsChangeEvent.TYPE, new HipsChangeEventHandler() {

			@Override
			public void onChangeEvent(final HipsChangeEvent changeEvent) {
				view.setHipsName(changeEvent.getHiPS().getSurveyName());
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_HipsName, changeEvent.getHiPS().getSurveyName());
			}
		});
		
		CommonEventBus.getEventBus().addHandler(IsInScienceModeChangeEvent.TYPE, new IsInScienceModeEventHandler() {
			
			@Override
			public void onIsInScienceModeChanged() {
				if(GUISessionStatus.getIsInScienceMode() != isSciModeChecked) {
					toggleSciMode();
				}
			}
		});
		
		CommonEventBus.getEventBus().addHandler(ServerProblemFoundEvent.TYPE, new ServerProblemFoundEventHandler() {
			
			@Override
			public void onEvent(ServerProblemFoundEvent event) {
				view.showWarningButton();
			}
		});
		
		CommonEventBus.getEventBus().addHandler(ServerProblemSolvedEvent.TYPE, new ServerProblemSolvedEventHandler() {
			
			@Override
			public void onEvent(ServerProblemSolvedEvent event) {
				view.hideWarningButton();
			}
		});

		setInitialValues();

		view.addCoordinateFrameChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent changeEvent) {
				if(view.getSelectedCoordinateFrame().equals(CoordinateFrame.J2000.toString())){
					AladinLiteWrapper.getInstance().setCooFrame(CoordinateFrame.J2000);
				} else {
					AladinLiteWrapper.getInstance().setCooFrame(CoordinateFrame.GALACTIC);
				}
			}
		});

		view.addHipsNameClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(final ClickEvent event) {
				CommonEventBus.getEventBus().fireEvent(new ToggleSkyPanelEvent());
			}
		});
		
		view.addShareClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				String bookmarkUrl = UrlUtils.getUrlForCurrentState();
				UrlUtils.updateURLWithoutReloadingJS(bookmarkUrl);
				String hostName = Window.Location.getHost();
				CopyToClipboardHelper.getInstance().copyToClipBoard(hostName + bookmarkUrl, TextMgr.getInstance().getText("ctrlToolBar_URLClipboard"));
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_Share, "");
				view.closeDropdownMenu();
			}
		});

		view.addHelpClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				Window.open(TextMgr.getInstance().getText("headerPresenter_helpLink"), "_blank", "");
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_Help, "");
				view.closeDropdownMenu();
			}
		});


		view.addDropdownContainerMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent arg0) {
				timeSinceLastMouseDownInsideDropdownContainer = System.currentTimeMillis();
			}
		});

		view.addDropdownContainerBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent arg0) {
				if(System.currentTimeMillis() - timeSinceLastMouseDownInsideDropdownContainer > 100) {
					view.closeDropdownMenu();
				}
			}
		});

		AllSkyFocusPanel.getInstance().registerObserver(new AllSkyFocusPanelObserver() {

			@Override
			public void onAladinInteraction() {
				view.closeDropdownMenu();
			}
		});

		view.addMenuClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_Menu, "");
				view.toggleDropdownMenu();
			}
		});

		view.addFeedbackClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_Feedback, "");
				Window.open(EsaSkyWebConstants.ESA_SKY_USER_ECHO, "_blank", "");
				view.closeDropdownMenu();
			}
		});

		view.addVideoTutorialsClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_VideoTutorials, "");
				Window.open(EsaSkyWebConstants.ESA_SKY_HELP_PAGES_URL, "_blank", "");
				view.closeDropdownMenu();
			}
		});

		view.addGridButtonClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(final ClickEvent event) {
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_CoordinateGrid, Boolean.toString(!view.isGridOn()));
				view.toggleGrid();
			}
		});

		view.addReleaseNotesClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_ReleaseNotes, "");
				Window.open(EsaSkyWebConstants.ESA_SKY_RELEASE_NOTES_URL, "_blank", "");
				view.closeDropdownMenu();
			}
		});

		view.addNewsletterClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_Newsletter, "");
				Window.open(EsaSkyWebConstants.ESA_SKY_NEWSLETTER_URL, "_blank", "");
				view.closeDropdownMenu();
			}
		});

		view.addAboutUsClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_AboutUs, "");
				Window.open(EsaSkyWebConstants.ESA_SKY_ABOUTUS_URL, "_blank", "");
				view.closeDropdownMenu();
			}
		});

		view.addViewInWwtClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				Coordinate j2000Coordinate = CoordinateUtils.getCenterCoordinateInJ2000().getCoordinate();
				Window.open(ExternalServices.buildWwtURLJ2000(j2000Coordinate.ra, j2000Coordinate.dec), "_blank", "");
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_ViewInWwt, "");
				view.closeDropdownMenu();
			}
		});

		view.addScienceModeSwitchClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				toggleSciMode();
			}
		});
		
		view.addScreenshotClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				String viewUrl = AladinLiteWrapper.getAladinLite().getViewURL();
				JavaScriptObject imageCanvas = AladinLiteWrapper.getAladinLite().getViewCanvas();
				ScreenshotDialogBox screenshotDialogBox = new ScreenshotDialogBox(viewUrl, imageCanvas);
				screenshotDialogBox.show();
				view.closeDropdownMenu();
				
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_ScreenShot, "");
			}
		});
		
		view.addLanguageSelectionChangeHandler(new StringValueSelectionChangedHandler() {
			
			@Override
			public void onSelectionChanged(String newValue, int index) {
				view.setSelectedLanguage(index);
				GUISessionStatus.setCurrentLanguage(newValue);
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Header, GoogleAnalytics.ACT_Header_Language, newValue);
				Window.open(UrlUtils.getUrlForCurrentState(), "_self", "");
			}
		});
		
		view.addWarningButtonClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CommonEventBus.getEventBus().fireEvent(new ToggleServerProblemBannerEvent());
			}
		});

	}

	private void setInitialValues() {
		view.setAvailableCoordinateFrames(new LinkedList<SelectionEntry>(
				Arrays.asList(
						new SelectionEntry("J2000", AladinLiteConstants.CoordinateFrame.J2000.toString()),
						new SelectionEntry("GAL", AladinLiteConstants.CoordinateFrame.GALACTIC.toString())
						)
				)
				);
		if(coordinateFrameFromUrl != null && coordinateFrameFromUrl.toLowerCase().contains("gal")){
			view.selectCoordinateFrame(1);
		}

		view.setHipsName(EsaSkyConstants.ALADIN_DEFAULT_HIPS_MAP);
		
		List<SimpleEntry<String, String>> languageDisplayAndValue = new LinkedList<SimpleEntry<String, String>>();
		for(String lowerCaseLanguageCode : EsaSkyConstants.AVAILABLE_LANGCODES) {
			languageDisplayAndValue.add(new SimpleEntry<String, String>(lowerCaseLanguageCode.substring(0, 1).toUpperCase() + lowerCaseLanguageCode.substring(1), lowerCaseLanguageCode));
		}
		view.setAvailableLanguages(languageDisplayAndValue);
		view.setSelectedLanguage(Arrays.asList(EsaSkyConstants.AVAILABLE_LANGCODES).indexOf(GUISessionStatus.getCurrentLanguage()));
	}
	
	private void toggleSciMode() {
		isSciModeChecked = !isSciModeChecked;
		view.setIsInScienceMode(isSciModeChecked);
		GUISessionStatus.setIsInScienceMode(isSciModeChecked);
	}

	private void setCoordinates(double ra, double dec) {
		String coordinate = "";
		if(view.getSelectedCoordinateFrame().equals(CoordinateFrame.J2000.toString())) {
			coordinate = formatJ200Ra(ra).getSpacedString() + " "+ formatJ200Dec(dec);
		} else {
			Double [] coord = CoordinatesConversion.convertPointEquatorialToGalactic(ra, dec);
			coordinate = formatGalacticRa(coord[0]) + " " + formatGalacticDec(coord[1]);
		}
		view.setCoordinate(coordinate);
	}

	private class HoursMinutesSeconds{
		private final String hours;
		private final String minutes;
		private final String seconds;

		private HoursMinutesSeconds(String hours, String minutes, String seconds) {
			this.hours = hours;
			this.minutes = minutes;
			this.seconds = seconds;
		}

		private String getSpacedString() {
			return hours + " " + minutes + " " + seconds;
		}
	}

	private HoursMinutesSeconds formatJ200Ra(double ra) {
		double hours = (ra / 360) * 24;
		double minutes = (hours - (int) hours) * 60;
		Double seconds = (minutes - (int) minutes) * 60;
		String secondsFormat = "00.000";

		if(NumberFormat.getFormat(secondsFormat).format(seconds).equals("60.000")) {
			seconds = 0.0;
			minutes += 1;
			if(minutes >= 60) {
				minutes = 0;
				hours += 1;
				if(hours >= 24) {
					hours = 0;
				}
			}
		}

		return new HoursMinutesSeconds(NumberFormat.getFormat("00").format((int) hours),
				NumberFormat.getFormat("00").format((int) minutes), 
				NumberFormat.getFormat(secondsFormat).format(seconds));
	}

	private String formatJ200Dec(double degrees) {
		String sign = "+";
		if(degrees < 0) {
			sign = "-";
			degrees = Math.abs(degrees);
		}
		double minutes = (degrees - (int) degrees) * 60;
		Double seconds = (minutes - (int) minutes) * 60;
		String secondsFormat = "00.00";

		if(NumberFormat.getFormat(secondsFormat).format(seconds).equals("60.00")) {
			seconds = 0.0;
			minutes += 1;
			if(minutes >= 60) {
				minutes = 0;
				degrees += 1;
			}
		}

		return sign + NumberFormat.getFormat("00").format((int) degrees) + " " + NumberFormat.getFormat("00").format((int) minutes) + " "  + NumberFormat.getFormat(secondsFormat).format(seconds);
	}

	private String formatGalacticRa(double ra) {
		String format = "000.0000000";

		if(NumberFormat.getFormat(format).format(ra).equals("360.0000000")) {
			ra = 0.0;
		}

		return NumberFormat.getFormat(format).format(ra);
	}

	private String formatGalacticDec(double dec) {
		String sign = "+";
		if(dec < 0) {
			sign = "-";
			dec = Math.abs(dec);
		}
		String format = "00.0000000";

		if(NumberFormat.getFormat(format).format(dec).equals("90.0000000")) {
			dec = 0.0;
		}

		return sign + NumberFormat.getFormat(format).format(dec);
	}

	private String formatFov(double fovDeg) {
		NumberFormat format = NumberFormat.getFormat("#00.00");
		if(fovDeg >= 1) {
			return "FoV: " + format.format(fovDeg) + "&deg;";
		} else if (fovDeg * 60 >= 1){
			return "FoV: " + format.format(fovDeg * 60) + "'";
		} else {
			return "FoV: " + format.format(fovDeg * 60 * 60) + "''";
		}
	}

	public class SelectionEntry{
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
	
	public interface StringValueSelectionChangedHandler{
		public void onSelectionChanged(String newValue, int index);
	}
	
	public void toggleGrid(boolean show) {
		view.toggleGrid(show);		
	}
}
