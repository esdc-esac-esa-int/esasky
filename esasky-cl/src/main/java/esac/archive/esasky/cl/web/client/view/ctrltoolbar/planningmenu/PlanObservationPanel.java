package esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import esac.archive.esasky.cl.wcstransform.module.utility.Constants.Instrument;
import esac.archive.esasky.cl.wcstransform.module.utility.Constants.PlanningMission;
import esac.archive.esasky.cl.wcstransform.module.utility.InstrumentMapping;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.planning.FutureFootprintClearEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.*;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;

import java.util.List;

public class PlanObservationPanel extends MovablePanel implements Hidable<PlanObservationPanel> {

    private Resources resources;
    private CssResource style;

    private final String COMPONENT_ID = "planningMenu";
    private final String CONTAINER_ID = "planningMenuContainer";
    private final String MISSION_CONTAINER_CLASS = "missionMenuContainer";

    private boolean isShowing = false;

    private static VerticalPanel jwstPanel; 
    private final String SIAF_VERSION = "SIAF: PRDOPSSOC-036";
    
    private static PlanObservationPanel instance = null;

    private PopupHeader<PlanObservationPanel> header;

    public interface Resources extends ClientBundle {

        @Source("planObservationPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    private PlanObservationPanel() {
        super(GoogleAnalytics.CAT_PLANNINGTOOL, false, false);
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
    	header = new PopupHeader<>(this,
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
        FlowPanel addButtonAndSiafContainer = new FlowPanel();
        addButtonAndSiafContainer.addStyleName("planObservationPanel__addButtonAndSiafContainer");
        addButtonAndSiafContainer.add(addInstrumentButton);
        Label siafVersion = new Label(SIAF_VERSION);
        addButtonAndSiafContainer.add(siafVersion);
        container.add(addButtonAndSiafContainer);
        		
        this.add(container);
        this.getElement().setId(COMPONENT_ID);
        this.removeStyleName("gwt-DialogBox");
        this.hide();
        PlanObservationPanel.jwstPanel = jwstPanel;
        
        MainLayoutPanel.addMainAreaResizeHandler(event -> setMaxSize());
        setMaxSize();
    }

    @Override
    protected void onLoad() {
        this.addSingleElementAbleToInitiateMoveOperation(header.getElement());
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
                String detector = InstrumentMapping.getInstance().getDefaultApertureForInstrument(instrument);
                PlanObservationPanel.this.addInstrumentRow(Instrument.getSingleInstrument(pm, instrument), detector,jwstPanel);
                
                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_PLANNINGTOOL, GoogleAnalytics.ACT_PLANNINGTOOL_INSTRUMENTSELECTED, instrument);
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

    private void addInstrumentRow(Instrument instrument, String detector, VerticalPanel verticalPanel) {
        FutureFootprintRow fr = new FutureFootprintRow(instrument, detector, false, SIAF_VERSION);
        verticalPanel.add(fr);
    }
    
    public JSONValue addInstrumentRowAPI(String instrumentName, String detectorName, boolean showAllInstruments) {
    	return addInstrumentRowWithCoordinatesAPI(instrumentName, detectorName, showAllInstruments, null, null, null);
    }
    
    public JSONValue addInstrumentRowWithCoordinatesAPI(String instrumentName, String detectorName, boolean showAllInstruments, String ra, String dec, String rotation) {
    	final PlanningMission pm = PlanningMission.JWST;
    	Instrument instrument = Instrument.getSingleInstrument(pm, instrumentName);
    	if(instrument == null) {
    		List<Instrument> instruments = Instrument.getInstrumentsPerMission(pm);
    		JSONArray availableModules = new JSONArray();
    		int i = 0;
    		for(Instrument inst : instruments) {
    			availableModules.set(i, new JSONString(inst.getInstrumentName()));
    			i++;
    		}
    		return availableModules;
    	}
    	
    	List<String> detectors = InstrumentMapping.getInstance().getApertureListForInstrument(instrumentName);
    	boolean found = false;
    	JSONArray availableModules = new JSONArray();
    	int i = 0;
    	for (String detector : detectors) {
    		if(detector.equals(detectorName)) {
    			FutureFootprintRow fr;
    			if(ra != null && dec != null && rotation != null) {
    				fr = new FutureFootprintRow(instrument, detector, showAllInstruments, ra, dec, rotation, SIAF_VERSION);
    			}
    			else {
    				fr = new FutureFootprintRow(instrument, detector, showAllInstruments, SIAF_VERSION);
    			}
    	        PlanObservationPanel.jwstPanel.add(fr);
    			found = true;
    		}
    		availableModules.set(i, new JSONString(detector));
    		i++;
    	}
    	if(found) {
    		return null;
    	}else {
    		JSONObject obj = new JSONObject();
    		obj.put(instrumentName, availableModules);
    		return obj;
    	
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
    public void show() {
        isShowing = true;
        this.removeStyleName("displayNone");
        setMaxSize();
        ensureDialogFitsInsideWindow();
        updateHandlers();
    }

    @Override
    public void hide() {
        this.addStyleName("displayNone");
        isShowing = false;
        this.removeHandlers();
        CloseEvent.fire(this, null);
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }
    
}
