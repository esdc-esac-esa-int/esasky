package esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import esac.archive.esasky.cl.wcstransform.module.utility.Constants.Detectors;
import esac.archive.esasky.cl.wcstransform.module.utility.Constants.Instrument;
import esac.archive.esasky.cl.wcstransform.module.utility.Constants.PlanningMission;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.planning.FutureFootprintClearEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.EsaSkyMenuPopupPanel;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.MenuObserver;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;

public class PlanObservationPanel extends DialogBox {

    private Resources resources;
    private CssResource style;

    private final String COMPONENT_ID = "planningMenu";
    private final String CONTAINER_ID = "planningMenuContainer";
    private final String MISSION_CONTAINER_CLASS = "missionMenuContainer";

    private final int PADDINGS_AND_MARGINS = 23;
    private static VerticalPanel jwstPanel; 
    
    private static PlanObservationPanel instance = null;

	private boolean isShowing;
	
    public interface Resources extends ClientBundle {

        @Source("planObservationPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    private PlanObservationPanel() {
        super(false, false);
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
            
        initView();
    }
    
    public static PlanObservationPanel getInstance() {
    	if(instance == null) {
    		instance = new PlanObservationPanel();
    	}
    	return instance;
    }
    

    private void initView() {
    	final PlanningMission pm = PlanningMission.JWST;
    	PopupHeader header = new PopupHeader(this,
    			TextMgr.getInstance().getText("planObservationPanel_projectFutureObservations").replace("$MISSION$", pm.getMissionName()),
    			TextMgr.getInstance().getText("planObservationPanel_helpMessageText"));
    			
        final VerticalPanel jwstPanel = new VerticalPanel();
        jwstPanel.setStyleName(MISSION_CONTAINER_CLASS);
        jwstPanel.addStyleName(CONTAINER_ID);

        jwstPanel.add(header);
        
        final EsaSkyMenuPopupPanel<Instrument> instrumentPopupMenu = createInstrumentPopupMenu(pm, jwstPanel);

        final SignButton addInstrumentButton = createAddInstrumentButton(instrumentPopupMenu);

        VerticalPanel container = new VerticalPanel();
        container.add(jwstPanel);
        container.add(addInstrumentButton);
        		
        this.add(container);
        this.getElement().setId(COMPONENT_ID);
        this.removeStyleName("gwt-DialogBox");
        this.hide();
        PlanObservationPanel.jwstPanel = jwstPanel;
        
        MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				setMaxSize();
			}
		});
        setMaxSize();
    }

	private EsaSkyMenuPopupPanel<Instrument> createInstrumentPopupMenu(final PlanningMission pm,
			final VerticalPanel jwstPanel) {
	    
		final EsaSkyMenuPopupPanel<Instrument> instrumentPopupMenu = new EsaSkyMenuPopupPanel<Instrument>(112, true);
        List<Instrument> instrumentsNames = Instrument.getInstrumentsPerMission(pm);
        
        for (final Instrument currInstrument : instrumentsNames) {
            MenuItem<Instrument> dropdownItem = new MenuItem<Instrument>(currInstrument,
                    currInstrument.getInstrumentName(), currInstrument.getInstrumentName(), false);
            instrumentPopupMenu.addMenuItem(dropdownItem);
        }
        
        instrumentPopupMenu.registerObserver(new MenuObserver() {

            @Override
            public void onSelectedChange() {
                String instrument = instrumentPopupMenu.getSelectedObject().getInstrumentName();
                Detectors detector = Detectors.getDefaultDetectorPerInstrument(instrument);
                PlanObservationPanel.this.addInstrumentRow(Instrument.getSingleInstrument(pm, instrument), detector,jwstPanel);
                
                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_PlanningTool, GoogleAnalytics.ACT_PlanningTool_InstrumentSelected, instrument);
            }
        });
        
		return instrumentPopupMenu;
	}

	private SignButton createAddInstrumentButton(final EsaSkyMenuPopupPanel<Instrument> instrumentPopupMenu) {
        final SignButton addInstrumentButton = new SignButton(SignButton.SignType.PLUS);
        addInstrumentButton.addStyleName("addInstrumentButton");
        addInstrumentButton.setRoundStyle();
        addInstrumentButton.setMediumStyle();
        addInstrumentButton.setTitle(TextMgr.getInstance().getText("futureFootprintRow_addJWSTInstrument"));
        addInstrumentButton.addClickHandler(new ClickHandler() {
        	
        	@Override
        	public void onClick(ClickEvent event) {
        		instrumentPopupMenu.show();
        		instrumentPopupMenu.setPopupPosition(addInstrumentButton.getAbsoluteLeft(), addInstrumentButton.getAbsoluteTop());
        	}
        });
		return addInstrumentButton;
	}

    private void addInstrumentRow(Instrument instrument, Detectors detector, VerticalPanel verticalPanel) {
        FutureFootprintRow fr = new FutureFootprintRow(instrument, detector, false);
        verticalPanel.add(fr);
    }
    
    public void addInstrumentRowAPI(String instrumentName, String detectorName, boolean showAllInstruments) {
    	final PlanningMission pm = PlanningMission.JWST;
    	Instrument instrument = Instrument.getSingleInstrument(pm, instrumentName);
    	List<Detectors> detectors = Detectors.getDetectorsForInstrument(instrumentName);
    	for (Detectors detector : detectors) {
    		if(detector.getDetectorName().equals(detectorName)) {
    			FutureFootprintRow fr = new FutureFootprintRow(instrument, detector, showAllInstruments);
    			PlanObservationPanel.jwstPanel.add(fr);
    		}
    	}
    }
    
    public void addInstrumentRowWithCoordinatesAPI(String instrumentName, String detectorName, boolean showAllInstruments, String ra, String dec, String rotation) {
    	final PlanningMission pm = PlanningMission.JWST;
    	Instrument instrument = Instrument.getSingleInstrument(pm, instrumentName);
    	List<Detectors> detectors = Detectors.getDetectorsForInstrument(instrumentName);
    	for (Detectors detector : detectors) {
    		if(detector.getDetectorName().equals(detectorName)) {
    	    	FutureFootprintRow fr = new FutureFootprintRow(instrument, detector, showAllInstruments, ra, dec, rotation);
    	        PlanObservationPanel.jwstPanel.add(fr);
    		}
    	}
    }
    
    public void clearAllAPI() {
    	int i=0;
    	while(i<PlanObservationPanel.jwstPanel.getWidgetCount()) {
    		if(PlanObservationPanel.jwstPanel.getWidget(i).getClass().equals(FutureFootprintRow.class)) {
        		FutureFootprintRow fr = (FutureFootprintRow) PlanObservationPanel.jwstPanel.getWidget(i);
        		CommonEventBus.getEventBus().fireEvent(new FutureFootprintClearEvent(fr));
        		fr.removeFromParent();
    		}else {
    			i++;
    		}
    	}
    }
    
    
	@Override
	protected void onLoad() {
		setMaxSize();
	}

	@Override
	public void setPopupPosition(int left, int top) {
		setMaxSize();
	}

	@Override
	public void show() {
		isShowing = true;
		this.removeStyleName("displayNone");
		setMaxSize();
	}

	@Override
	public void hide(boolean autohide) {
		this.addStyleName("displayNone");
		isShowing = false;
		CloseEvent.fire(this, null);
	}
    
	private void setMaxSize() {
		Style style = getElement().getStyle();
		int maxWidth = MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - PADDINGS_AND_MARGINS;;
		int maxHeight = MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - PADDINGS_AND_MARGINS;
		style.setPropertyPx("maxWidth", maxWidth);
		style.setPropertyPx("maxHeight", maxHeight);
	}
	
	public void toggle() {
		if(isShowing) {
			hide();
		} else {
			show();
		}
	}
}
