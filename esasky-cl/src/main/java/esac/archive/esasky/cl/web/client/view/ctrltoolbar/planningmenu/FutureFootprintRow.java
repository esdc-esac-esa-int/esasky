package esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.ui.*;
import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.esasky.cl.wcstransform.module.utility.Constants.Instrument;
import esac.archive.esasky.cl.wcstransform.module.utility.InstrumentMapping;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.IsShowingCoordintesInDegreesChangeEvent;
import esac.archive.esasky.cl.web.client.event.planning.FutureFootprintClearEvent;
import esac.archive.esasky.cl.web.client.event.planning.FutureFootprintEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CopyToClipboardHelper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.*;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FutureFootprintRow extends Composite {

	private final Resources resources = GWT.create(Resources.class);
	private final CssResource style;

	private static final String CSS_INTRUMENT_NAME = "instrumentName";
	private static final String CSS_CLOSE_BUTTON = "clearButton";
	private static final String CSS_INSTRUMENT_HEADER = "instrumentHeader";
	private static final String CSS_COPY = "copyBtn";
	private static final String CSS_COPY_LABEL = "copyLabel";
	private static final String CSS_TEXTBOX = "planningTextBox";

	private static final double ROTATION_DEG_STEP = 1.0;
	private static final double RA_DEG_STEP = 0.001;
	private static final double DEC_DEG_STEP = 0.001;
	
	private double ra, dec, rotation;

	private Instrument instrument;
	private String aperture;
	private final NumberFormat raAndDecFormat = NumberFormat.getFormat("#0.000000");
	private final NumberFormat sexagesimalRaFormat = new SexagesimalNumberFormat(true);
	private final NumberFormat sexagesimalDecFormat = new SexagesimalNumberFormat(false);
	private final NumberFormat angleFormat = NumberFormat.getFormat("#0.000");

	private final String RA_TEXT = "RA";
	private final String DEC_TEXT = "Dec";
	private final String DEGREE_TEXT = "\u00B0";
	private final String ROTATION_TEXT = TextMgr.getInstance().getText("futureFootprintRow_rotation") + " \u00B0";
	private final String SIAF_VERSION;

	private final Label raLabel = new Label(RA_TEXT);
	private final Label decLabel = new Label(DEC_TEXT);
	private final Label rotationLabel = new Label("APA");

	private CheckBox allInstrumentsCheckBox;
	private EsaSkyButton copyButton;

	private EsaSkyNumberControl raControl = new EsaSkyNumberControl(RA_TEXT,
			resources.arrowIcon(), resources.arrowIcon(), RA_DEG_STEP, raAndDecFormat);
	private EsaSkyNumberControl decControl = new EsaSkyNumberControl(DEC_TEXT, 
			resources.arrowIcon(), resources.arrowIcon(), DEC_DEG_STEP, raAndDecFormat);
	private EsaSkyNumberControl rotationControl = new EsaSkyNumberControl(ROTATION_TEXT, 
			resources.rotateLeftArrow(), resources.rotateRightArrow(), ROTATION_DEG_STEP, angleFormat);

    private static final Map<Instrument, Double> INSTRUMENT_ANGLES = new HashMap<Instrument, Double>() {{
        put(Instrument.NIRSPEC, -138.5);
        put(Instrument.NIRCAM, 0.0);
        put(Instrument.NIRISS, -0.57);
        put(Instrument.MIRI, -4.45);
        put(Instrument.FGS, 0.0);
		put(Instrument.XMM_EPIC_PN, 0.0);
    }};
    
    
	

	public interface Resources extends ClientBundle {
		@Source("futureFootprintRow.css")
		@CssResource.NotStrict
		CssResource style();

		@Source("copy-icon.png")
		ImageResource copyIcon();

		@Source("upArrow.png")
		@ImageOptions(flipRtl = true)
		ImageResource arrowIcon();

		@Source("rotate_counter_clockwise.png")
		ImageResource rotateLeftArrow();

		@Source("rotate_clockwise.png")
		ImageResource rotateRightArrow();
		
		@Source("info.png")
		ImageResource info();
	}

	public FutureFootprintRow(Instrument instrument, String detector, boolean showAllInstruments, String SIAF_VERSION) {
		this.style = this.resources.style();
		this.style.ensureInjected();

		this.SIAF_VERSION = SIAF_VERSION;
		this.instrument = instrument;
		this.aperture = detector;
		
		this.ra = AladinLiteWrapper.getCenterRaDeg();
		this.dec = AladinLiteWrapper.getCenterDecDeg();
		this.rotation = 0;

		initView();
		
		if(showAllInstruments) {
			this.allInstrumentsCheckBox.setValue(true, true);
		}

		if (Instrument.getInstrumentsPerMission(instrument.getMission()).size() < 2) {
			this.allInstrumentsCheckBox.setVisible(false);
		}

	}
	
	public FutureFootprintRow(Instrument instrument, String detector, boolean showAllInstruments, String ra, String dec, String rotation, String SIAF_VERSION) {
		this.style = this.resources.style();
		this.style.ensureInjected();

		this.SIAF_VERSION = SIAF_VERSION;
		this.instrument = instrument;
		this.aperture = detector;
		
		this.ra = Float.parseFloat(ra);
		this.dec = Float.parseFloat(dec);
		this.rotation = Float.parseFloat(rotation);
		
		initView();
		
		if(showAllInstruments) {
			this.allInstrumentsCheckBox.setValue(true, true);
		}
	}

	private void initView() {
	    rotationLabel.getElement().setTitle(ROTATION_TEXT);
		VerticalPanel container = new VerticalPanel();
		container.setStyleName("planningMenu");

		HorizontalPanel headerContainer = new HorizontalPanel();
		HorizontalPanel controlsContainer = new HorizontalPanel();
		controlsContainer.addStyleName("controlsContainer");
		HorizontalPanel copyCoordinatesContainer = new HorizontalPanel();

		VerticalPanel variablesContainer = new VerticalPanel();
		variablesContainer.addStyleName("variablesContainer");
		VerticalPanel controlOfVariablesContainer = new VerticalPanel();

		HorizontalPanel instrumentHeader = createInstrumentHeader(); 

		initializeNumberBox(raControl.getNumberBox(), this.ra);
		initializeNumberBox(decControl.getNumberBox(), this.dec);
		initializeNumberBox(rotationControl.getNumberBox(), this.rotation);

		Grid variables = new Grid(3,3);
		variables.setWidget(0, 0, raLabel);
		variables.setWidget(0, 1, raControl.getNumberBox());
		variables.setWidget(1, 0, decLabel);
		variables.setWidget(1, 1, decControl.getNumberBox());
		variables.setWidget(2, 0, rotationLabel);
		variables.setWidget(2, 1, rotationControl.getNumberBox());
		variables.setWidget(2, 2, createInstrumentDetailsBtn());

		HorizontalPanel copyOnClipboardCoordinatesPanel = createCopyOnClipboardCoordinatesPanel();
		HorizontalPanel controlsPanel = createControlPanel();

		headerContainer.add(instrumentHeader);
		variablesContainer.add(variables);

		controlOfVariablesContainer.add(controlsPanel);
		copyCoordinatesContainer.add(copyOnClipboardCoordinatesPanel);

		controlsContainer.add(variablesContainer);
		controlsContainer.add(controlOfVariablesContainer);

		container.add(headerContainer);
		container.add(controlsContainer);
		container.add(copyCoordinatesContainer);

		container.addStyleName("futureFootprintRow");
		initWidget(container);

		CommonEventBus.getEventBus().fireEvent(new FutureFootprintEvent(FutureFootprintRow.this));

		CommonEventBus.getEventBus().addHandler(IsShowingCoordintesInDegreesChangeEvent.TYPE, this::updateCoordinateFormat);
	}


	@Override
	public void onLoad() {
		updateCoordinateFormat();
	}



	private void updateCoordinateFormat() {
		if(GUISessionStatus.isShowingCoordinatesInDegrees()) {
			raControl.setNumberFormat(raAndDecFormat);
			decControl.setNumberFormat(raAndDecFormat);
		} else {
			raControl.setNumberFormat(sexagesimalRaFormat);
			decControl.setNumberFormat(sexagesimalDecFormat);
		}
	}

	private HorizontalPanel createInstrumentHeader() {
		HorizontalPanel instrumentHeader = new HorizontalPanel();
		instrumentHeader.setStyleName(CSS_INSTRUMENT_HEADER);

		Label instrumentName = new Label(instrument.getInstrumentName());
		instrumentName.setStyleName(CSS_INTRUMENT_NAME);

		instrumentHeader.add(instrumentName);
		instrumentHeader.add(createDetectorDropDownMenu());
		instrumentHeader.add(createAllInstrumentsCheckBox());
		instrumentHeader.add(createCloseInstrumentButton());
		return instrumentHeader;
	}

	private DropDownMenu<String> createDetectorDropDownMenu() {
		final DropDownMenu<String> aperturesDropDownMenu = new DropDownMenu<>(
				TextMgr.getInstance().getText("futureFootprintRow_aperture"), TextMgr.getInstance().getText("futureFootprintRow_aperture"), 135, "aperturesDropDownMenu");

		List<String> aperturesNames = InstrumentMapping.getInstance().getApertureListForInstrument(instrument.getInstrumentName());

		int counter = 0;
		int apertureIndex = 0;
		for (final String currentAperture : aperturesNames) {
			MenuItem<String> dropdownItem = new MenuItem<>(currentAperture,
					currentAperture, currentAperture, true);
			aperturesDropDownMenu.addMenuItem(dropdownItem);
			if (currentAperture.equals(aperture)) {
				apertureIndex = counter;
			}
			counter++;
		}
		aperturesDropDownMenu.selectObject(aperturesDropDownMenu.getMenuItems().get(apertureIndex).getItem());

		aperturesDropDownMenu.registerObserver(() -> {
			String apertureName = aperturesDropDownMenu.getSelectedObject();

			for (String aperture : InstrumentMapping.getInstance().getApertureListForInstrument(instrument.getInstrumentName())) {

				if (aperture.equals(apertureName)) {
					FutureFootprintRow.this.aperture = aperture;

					CommonEventBus.getEventBus().fireEvent(
							new FutureFootprintEvent(FutureFootprintRow.this));
					break;
				}
			}

			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_PLANNINGTOOL, GoogleAnalytics.ACT_PLANNINGTOOL_DETECTORSELECTED, instrument.getInstrumentName() + " - " + apertureName);
		});
		return aperturesDropDownMenu;
	}

	private CheckBox createAllInstrumentsCheckBox() {
		allInstrumentsCheckBox = new CheckBox(TextMgr.getInstance().getText("futureFootprintRow_allInstruments"));
		allInstrumentsCheckBox.addStyleName("allInstrumentsCheckBox");

		allInstrumentsCheckBox.addValueChangeHandler(event -> {
			GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_PLANNINGTOOL, GoogleAnalytics.ACT_PLANNINGTOOL_ALLINSTRUMENTSCLICK, instrument.getInstrumentName());

			CommonEventBus.getEventBus().fireEvent(
					new FutureFootprintEvent(FutureFootprintRow.this));

		});
		
		return allInstrumentsCheckBox;
	}

	private CloseButton createCloseInstrumentButton() {
		CloseButton closeButton = new CloseButton();
		closeButton.addStyleName(CSS_CLOSE_BUTTON);
		closeButton.setTitle(TextMgr.getInstance().getText("futureFootprintRow_removeFootprint"));
		closeButton.addClickHandler(arg0 -> {
			CommonEventBus.getEventBus().fireEvent(
					new FutureFootprintClearEvent(FutureFootprintRow.this));
			FutureFootprintRow.this.removeFromParent();
		});
		return closeButton;
	}
	
	private void initializeNumberBox(EsaSkyNumberBox numberBox, double number) {

		numberBox.setNumber(number);
		numberBox.removeStyleName("gwt-TextBox");
		numberBox.setStyleName(CSS_TEXTBOX);

		numberBox.addValueChangeHandler(event -> CommonEventBus.getEventBus().fireEvent(
				new FutureFootprintEvent(FutureFootprintRow.this)));
	}

	private HorizontalPanel createControlPanel() {
		HorizontalPanel controlPanel = new HorizontalPanel();

		VerticalPanel rotationControlsPanel = new VerticalPanel();

		raControl.getIncreaseNumberButton().rotate(270);
		raControl.getDecreaseNumberButton().rotate(90);
		decControl.getIncreaseNumberButton().rotate(0);
		decControl.getDecreaseNumberButton().rotate(180);
		
		Grid directionalControls = new Grid(3, 3);
		directionalControls.addStyleName("directionalControls");

		directionalControls.setWidget(1, 0, raControl.getIncreaseNumberButton());
		directionalControls.setWidget(1, 2, raControl.getDecreaseNumberButton());
		directionalControls.setWidget(0, 1, decControl.getIncreaseNumberButton());
		directionalControls.setWidget(2, 1, decControl.getDecreaseNumberButton());
		controlPanel.add(directionalControls);

		rotationControl.getDecreaseNumberButton().addStyleName("rotationButton");
		rotationControl.getIncreaseNumberButton().addStyleName("rotationButton");
        rotationControlsPanel.add(rotationControl.getDecreaseNumberButton());
        rotationControlsPanel.add(rotationControl.getIncreaseNumberButton());
		controlPanel.add(rotationControlsPanel);

		return controlPanel;
	}

	private HorizontalPanel createCopyOnClipboardCoordinatesPanel() {

		HorizontalPanel copyOnClipboardCoordinates = new HorizontalPanel();
		copyOnClipboardCoordinates.addStyleName("copyOnClipboardCoordinates");

		Label copyCoordinatesLabel = new Label(TextMgr.getInstance().getText("futureFootprintRow_copyCoordinatesAndData"));
		copyCoordinatesLabel.addStyleName(CSS_COPY_LABEL);

		copyButton = new EsaSkyButton(this.resources.copyIcon());
		copyButton.setMediumStyle();
		copyButton.getElement().setId(CSS_COPY);
		copyButton.setTitle(copyCoordinatesLabel.getText());

		copyButton.addClickHandler(new ClickHandler() {
			private String coordinatesFormat;

			@Override
			public void onClick(final ClickEvent event) {
				if(AladinLiteWrapper.getAladinLite().getCooFrame().equals(AladinLiteConstants.FRAME_J2000)){
					coordinatesFormat = AladinLiteConstants.FRAME_J2000;
				}else {
					coordinatesFormat = AladinLiteConstants.FRAME_GALACTIC;
				}

				String degreeText = GUISessionStatus.isShowingCoordinatesInDegrees() ? DEGREE_TEXT : "";
				String texToCopy = TextMgr.getInstance().getText("futureFootprintRow_centre") + ": " + RA_TEXT + " " + degreeText + " " + raControl.getFormattedValue()
						+ "  " + DEC_TEXT + " " + degreeText + " " + decControl.getFormattedValue()
						+ "  ; APA (" + ROTATION_TEXT + "): " + rotationControl.getValue()
						+ " ; " + TextMgr.getInstance().getText("futureFootprintRow_coordinateSystem") + " " + coordinatesFormat 
						+ " ; " + SIAF_VERSION;
				CopyToClipboardHelper.getInstance().copyToClipBoard(texToCopy, TextMgr.getInstance().getText("futureFootprintRow_dataCopiedToClipboard"));
				
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_PLANNINGTOOL, GoogleAnalytics.ACT_PLANNINGTOOL_COPYCOORDINATES, instrument.getInstrumentName());
			}
		});

		copyOnClipboardCoordinates.add(copyCoordinatesLabel);
		copyOnClipboardCoordinates.add(copyButton);

		return copyOnClipboardCoordinates;
	}
	
	private EsaSkyButton createInstrumentDetailsBtn() {
		final EsaSkyButton instrumentDetailsBtn = new EsaSkyButton(Icons.getInfoIcon());
		instrumentDetailsBtn.addStyleName("instrumentInfoBtn");
		instrumentDetailsBtn.setTitle(TextMgr.getInstance().getText("futureFootprintRow_tooltip").replace("$MISSION$", instrument.getMission().getMissionName()));
		instrumentDetailsBtn.setRoundStyle();
		instrumentDetailsBtn.setSmallStyle();
		instrumentDetailsBtn.addClickHandler(event -> {
			InstrumentDetailsPopup skyDetailsInfo = new InstrumentDetailsPopup(instrument.getMission().getMissionName(), rotationControl.getValue(), INSTRUMENT_ANGLES.get(instrument), getRotationDeg());
			skyDetailsInfo.show();
			int defaultLeft = instrumentDetailsBtn.getAbsoluteLeft() + instrumentDetailsBtn.getOffsetWidth() / 2;
			if (defaultLeft + skyDetailsInfo.getOffsetWidth() > MainLayoutPanel.getMainAreaAbsoluteLeft() + MainLayoutPanel.getMainAreaWidth()) {
				defaultLeft -= skyDetailsInfo.getOffsetWidth();
			}
			skyDetailsInfo.setPopupPosition(defaultLeft,
					instrumentDetailsBtn.getAbsoluteTop() + instrumentDetailsBtn.getOffsetHeight() / 2);

		});

		return instrumentDetailsBtn;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public String getAperture() {
		return aperture;
	}

	public Double getRotationDeg() {
//		updateTooltip();
		return rotationControl.getValue() + INSTRUMENT_ANGLES.get(this.instrument);
	}
	

	public boolean getIsAllInstrumentsSelected() {
		return allInstrumentsCheckBox.getValue();
	}

	public double getCenterRaDeg() {
		return raControl.getValue();
	}

	public double getCenterDecDeg() {
		return decControl.getValue();
	}

}
