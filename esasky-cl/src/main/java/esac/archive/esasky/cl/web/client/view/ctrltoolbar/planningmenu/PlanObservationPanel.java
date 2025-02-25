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

package esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu;

import com.google.gwt.core.client.GWT;
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

import java.util.LinkedList;
import java.util.List;

public class PlanObservationPanel extends MovablePanel implements Hidable<PlanObservationPanel> {

    private Resources resources;
    private CssResource style;

    private final String COMPONENT_ID = "planningMenu";
    private final String CONTAINER_ID = "planningMenuContainer";
    private final String MISSION_CONTAINER_CLASS = "missionMenuContainer";

    private boolean isShowing = false;

    private  VerticalPanel container;
    private final String SIAF_VERSION = "SIAF: PRDOPSSOC-059";
    
    private static PlanObservationPanel instance = null;

    private PopupHeader<PlanObservationPanel> header;

    private EsaSkyMenuPopupPanel<Instrument> instrumentPopupMenu;

    private Label siafVersion;

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
        final DropDownMenu<PlanningMission> missionPopupMenu = new DropDownMenu<>(PlanningMission.JWST.getMissionName(), "Mission", 122, "planObservationPanel_mission_dropdown");
        missionPopupMenu.addMenuItem(new MenuItem<>(PlanningMission.JWST, PlanningMission.JWST.getMissionName(), true));
        missionPopupMenu.addMenuItem(new MenuItem<>(PlanningMission.XMM, PlanningMission.XMM.getMissionName(), true));
        missionPopupMenu.selectObject(PlanningMission.JWST);
        missionPopupMenu.registerObserver(() -> {
            changeMission(missionPopupMenu.getSelectedObject());
            siafVersion.setText(missionPopupMenu.getSelectedObject().equals(PlanningMission.JWST) ? SIAF_VERSION : "");
        });


        instrumentPopupMenu = new EsaSkyMenuPopupPanel<>(212, true);
        instrumentPopupMenu.registerObserver(() -> {
            String instrument = instrumentPopupMenu.getSelectedObject().getInstrumentName();
            String detector = InstrumentMapping.getInstance().getDefaultApertureForInstrument(instrument);
            PlanObservationPanel.this.addInstrumentRow(Instrument.getSingleInstrument(missionPopupMenu.getSelectedObject(), instrument), detector, container);

            GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_PLANNINGTOOL, GoogleAnalytics.ACT_PLANNINGTOOL_INSTRUMENTSELECTED, instrument);
        });

        header = new PopupHeader<>(this,
                TextMgr.getInstance().getText("planObservationPanel_projectFutureObservations"),
                TextMgr.getInstance().getText("planObservationPanel_helpMessageText"));

        final VerticalPanel jwstPanel = new VerticalPanel();
        jwstPanel.setStyleName(MISSION_CONTAINER_CLASS);
        jwstPanel.addStyleName(CONTAINER_ID);

        jwstPanel.add(header);

        final SignButton addInstrumentButton = createAddInstrumentButton(instrumentPopupMenu);

        VerticalPanel outerContainer = new VerticalPanel();
        outerContainer.add(jwstPanel);
        FlowPanel addButtonAndSiafContainer = new FlowPanel();
        addButtonAndSiafContainer.addStyleName("planObservationPanel__addButtonAndSiafContainer");
        addButtonAndSiafContainer.add(missionPopupMenu);
        addButtonAndSiafContainer.add(addInstrumentButton);
        siafVersion = new Label(SIAF_VERSION);
        addButtonAndSiafContainer.add(siafVersion);
        outerContainer.add(addButtonAndSiafContainer);
        		
        this.add(outerContainer);
        this.getElement().setId(COMPONENT_ID);
        this.removeStyleName("gwt-DialogBox");
        this.hide();
        this.container = jwstPanel;
        
        MainLayoutPanel.addMainAreaResizeHandler(event -> setMaxSize());
        setMaxSize();
    }

    @Override
    protected void onLoad() {
        this.addSingleElementAbleToInitiateMoveOperation(header.getElement());
        changeMission(PlanningMission.JWST);
    }

    @Override
    public void setMaxSize() {
        int marginPx = 15;
        getElement().getStyle().setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight()-this.getAbsoluteTop()-marginPx);
        getElement().getStyle().setPropertyPx("maxWidth", MainLayoutPanel.getMainAreaWidth()-this.getAbsoluteLeft());
    }

	private void changeMission(final PlanningMission pm) {

        instrumentPopupMenu.clearItems();

        List<Instrument> instrumentsNames = Instrument.getInstrumentsPerMission(pm);
        
        for (final Instrument currInstrument : instrumentsNames) {
            MenuItem<Instrument> dropdownItem = new MenuItem<>(currInstrument,
                    currInstrument.getInstrumentName(), currInstrument.getInstrumentName(), false);
            instrumentPopupMenu.addMenuItem(dropdownItem);
        }
	}

	private SignButton createAddInstrumentButton(final EsaSkyMenuPopupPanel<Instrument> instrumentPopupMenu) {
        final SignButton addInstrumentButton = new SignButton(SignButton.SignType.PLUS);
        addInstrumentButton.addStyleName("addInstrumentButton");
        addInstrumentButton.setRoundStyle();
        addInstrumentButton.setMediumStyle();
        addInstrumentButton.setTitle(TextMgr.getInstance().getText("futureFootprintRow_addJWSTInstrument"));
        addInstrumentButton.addClickHandler(event -> {
            instrumentPopupMenu.show();
            instrumentPopupMenu.setPopupPosition(addInstrumentButton.getAbsoluteLeft(), addInstrumentButton.getAbsoluteTop());
        });
		return addInstrumentButton;
	}

    private void addInstrumentRow(Instrument instrument, String detector, VerticalPanel verticalPanel) {
        FutureFootprintRow fr = new FutureFootprintRow(instrument, detector, false, siafVersion.getText());
        verticalPanel.add(fr);
    }
    
    public JSONValue addInstrumentRowAPI(String mission, String instrumentName, String detectorName, boolean showAllInstruments) {
    	return addInstrumentRowWithCoordinatesAPI(mission, instrumentName, detectorName, showAllInstruments, null, null, null);
    }
    
    public JSONValue addInstrumentRowWithCoordinatesAPI(String mission, String instrumentName, String detectorName, boolean showAllInstruments, String ra, String dec, String rotation) {
    	final PlanningMission pm = mission.equals(PlanningMission.XMM.getMissionName()) ? PlanningMission.XMM : PlanningMission.JWST;
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
    				fr = new FutureFootprintRow(instrument, detector, showAllInstruments, ra, dec, rotation, siafVersion.getText());
    			}
    			else {
    				fr = new FutureFootprintRow(instrument, detector, showAllInstruments, siafVersion.getText());
    			}
    	        this.container.add(fr);
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
    	while(i<this.container.getWidgetCount()) {
    		if(this.container.getWidget(i).getClass().equals(FutureFootprintRow.class)) {
        		FutureFootprintRow fr = (FutureFootprintRow) this.container.getWidget(i);
        		CommonEventBus.getEventBus().fireEvent(new FutureFootprintClearEvent(fr));
        		fr.removeFromParent();
    		}else {
    			i++;
    		}
    	}
    }
    
    public List<FutureFootprintRow> getAllRows(){
    	int i=0;
    	List<FutureFootprintRow> list = new LinkedList<>();
    	while(i<this.container.getWidgetCount()) {
    		if(this.container.getWidget(i).getClass().equals(FutureFootprintRow.class)) {
        		FutureFootprintRow fr = (FutureFootprintRow) this.container.getWidget(i);
        		list.add(fr);
    		}
    		i++;
    	}
    	return list;
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
