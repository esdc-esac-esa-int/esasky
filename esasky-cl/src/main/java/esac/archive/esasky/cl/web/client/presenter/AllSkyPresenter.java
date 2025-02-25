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

package esac.archive.esasky.cl.web.client.presenter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.VerticalPanel;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ImageLayer;
import esac.archive.esasky.cl.wcstransform.module.footprintbuilder.STCSAbstractGenerator;
import esac.archive.esasky.cl.wcstransform.module.footprintbuilder.STCSGeneratorFactory;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.AddShapeTooltipEvent;
import esac.archive.esasky.cl.web.client.event.UrlChangedEvent;
import esac.archive.esasky.cl.web.client.event.hips.HipsChangeEvent;
import esac.archive.esasky.cl.web.client.event.planning.FutureFootprintClearEvent;
import esac.archive.esasky.cl.web.client.event.planning.FutureFootprintEvent;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.PlanningConstant;
import esac.archive.esasky.cl.web.client.view.allskypanel.MultiTargetTooltip;
import esac.archive.esasky.cl.web.client.view.allskypanel.PlanningDetectorCenterTooltip;
import esac.archive.esasky.cl.web.client.view.allskypanel.Tooltip;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu.FutureFootprintRow;
import esac.archive.esasky.cl.web.client.view.searchpanel.targetlist.MultiTargetSourceConstants;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesFrame;
import esac.archive.esasky.ifcs.model.shared.ColorPalette;
import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class AllSkyPresenter {

    /** local instance of view. */
    private View view;

    private Map<FutureFootprintRow, Map<String, JavaScriptObject>> planningFootprintsPerInstrument = null;
    private Map<FutureFootprintRow, Map<String, List<JavaScriptObject>>> planningLabelsPerInstrument = null;

    private FutureFootprintRow previuosPlanningFootprintRow = null;
    private FutureFootprintRow currentPlanningFootprintRow = null;

    private List<SSOOverlayAndPolyline> ssoPolyline = null;
//    private HiPS currentHiPS = EsaSkyWebConstants.getInitialHiPS();
    
    private HiPS currentOverlay;
    private double currentOverlayOpacity = 0;
    
    /**
     * View interface.
     */
    public interface View {

        VerticalPanel getAllSkyContainerPanel();

        void showSourceTooltip(Tooltip tooltip);

        HasClickHandlers getZoomOutClickHandler();

        HasClickHandlers getZoomInClickHandler();

        void hideTooltip();

        void deToggleSelectionMode();

        void areaSelectionKeyboardShortcutStart();

        void updateModuleVisibility();
    }

    /**
     * Class Constructor.
     * @param inputView Input View.
     */
    public AllSkyPresenter(final View inputView) {
        this.view = inputView;
        bind();
    }

    /**
     * Bind view with presenter.
     */
    private void bind() {

        CommonEventBus.getEventBus().addHandler(FutureFootprintClearEvent.TYPE,
                event -> AllSkyPresenter.this.removePlanningFootprint(event.getFutureFootprintRow()));

        CommonEventBus.getEventBus().addHandler(FutureFootprintEvent.TYPE,
                event -> AllSkyPresenter.this.drawPlanning(event.getFutureFootprintRow()));
        CommonEventBus.getEventBus().addHandler(HipsChangeEvent.TYPE, changeEvent -> {
            changeHiPS(changeEvent.getSkyRowId(), changeEvent.getHiPS(), changeEvent.getColorPalette(), changeEvent.getOpacity());
            CommonEventBus.getEventBus().fireEvent(new UrlChangedEvent());
        });
        
        /*
         * On mouse click on top of a source show a tooltip if not publications source
         */
        CommonEventBus.getEventBus().addHandler(AladinLiteShapeSelectedEvent.TYPE, selectEvent -> {
            AladinShape obj = selectEvent.getShape();
            if (obj != null) {
                if(obj.getDataDetailsByKey(MultiTargetSourceConstants.CATALOGUE_NAME) != null &&
                        obj.getDataDetailsByKey(MultiTargetSourceConstants.CATALOGUE_NAME).equals(MultiTargetSourceConstants.OVERLAY_NAME)) {
                    AllSkyPresenter.this.view.showSourceTooltip(new MultiTargetTooltip(obj));
                } else if(obj.getDataDetailsByKey(PlanningConstant.OVERLAY_PROPERTY) != null &&
                        obj.getDataDetailsByKey(PlanningConstant.OVERLAY_PROPERTY).equals(PlanningConstant.OVERLAY_NAME)) {
                    AllSkyPresenter.this.view.showSourceTooltip(new PlanningDetectorCenterTooltip(obj));
                }
            }
        });
        
        CommonEventBus.getEventBus().addHandler(AddShapeTooltipEvent.TYPE, event -> AllSkyPresenter.this.view.showSourceTooltip(event.getTooltip()));
     
        // Click on + (ZoomIn) button
        this.view.getZoomInClickHandler().addClickHandler(event -> AladinLiteWrapper.getInstance().increaseZoom());

        // Click on - (ZoomOut) button
        this.view.getZoomOutClickHandler().addClickHandler(event -> AladinLiteWrapper.getInstance().decreaseZoom());

    }

    void removePlanningFootprint(FutureFootprintRow footprintRow) {
        planningFootprintsPerInstrument.remove(footprintRow);
        planningLabelsPerInstrument.remove(footprintRow);
        currentPlanningFootprintRow = !planningFootprintsPerInstrument.isEmpty() ? previuosPlanningFootprintRow : null;
        drawPlanningPolygons();
    }

    public String drawPlanning(FutureFootprintRow futureFootprintRow) {

        previuosPlanningFootprintRow = currentPlanningFootprintRow;
        currentPlanningFootprintRow = futureFootprintRow;

        if (planningFootprintsPerInstrument == null) {
            planningFootprintsPerInstrument = new HashMap<>();
        }

        double raDeg = futureFootprintRow.getCenterRaDeg();
        double decDeg = futureFootprintRow.getCenterDecDeg();

        if (AladinLiteWrapper.getCoordinatesFrame() == CoordinatesFrame.GALACTIC) {
            double galacticCoordinates[] = CoordinatesConversion.convertPointGalacticToJ2000(raDeg,
                    decDeg);
            raDeg = galacticCoordinates[0];
            decDeg = galacticCoordinates[1];
        }

        Map<String, String> stcsPolygonInstrumentMap = STCSGeneratorFactory.getSTCSGenerator(
                futureFootprintRow.getInstrument().getMission().getMissionName()).doAll(
                futureFootprintRow.getInstrument().getInstrumentName(),
                futureFootprintRow.getAperture(),
                futureFootprintRow.getRotationDeg(), raDeg, decDeg);

        Map<String, JavaScriptObject> polygonJsInstrumentMap = new HashMap<>();

        String individualInstrumentPolygon = "";

        for (String inst : stcsPolygonInstrumentMap.keySet()) {

            JavaScriptObject polygon = AladinLiteWrapper.getAladinLite().createFootprintFromSTCS(
                    stcsPolygonInstrumentMap.get(inst));

            if (!futureFootprintRow.getIsAllInstrumentsSelected()) {
                if (inst.equals(futureFootprintRow.getInstrument().getInstrumentName())) {

                    polygonJsInstrumentMap.put(inst, polygon);
                    planningFootprintsPerInstrument.put(futureFootprintRow, polygonJsInstrumentMap);
                }
            } else {
                polygonJsInstrumentMap.put(inst, polygon);
                planningFootprintsPerInstrument.put(futureFootprintRow, polygonJsInstrumentMap);
            }

            if (inst.equals(futureFootprintRow.getInstrument().getInstrumentName())) {
                individualInstrumentPolygon = stcsPolygonInstrumentMap.get(inst);
            }
        }


        if (planningLabelsPerInstrument == null) {
            planningLabelsPerInstrument = new HashMap<>();
        }

        String missionName = futureFootprintRow.getInstrument().getMission().getMissionName();
        String instrumentName = futureFootprintRow.getInstrument().getInstrumentName();
        double rotationDeg = futureFootprintRow.getRotationDeg();

        STCSAbstractGenerator stcsGenerator = STCSGeneratorFactory.getSTCSGenerator(missionName);
        Map<String, double[]> labelInstrumentMap = stcsGenerator.computeInstrumentLabels(instrumentName, futureFootprintRow.getAperture(), rotationDeg, raDeg, decDeg);

        Map<String, List<JavaScriptObject>> textJsInstrumentMap = new HashMap<>();
        textJsInstrumentMap.put(instrumentName, new LinkedList<>());

        for (Map.Entry<String, double[]> entry : labelInstrumentMap.entrySet()) {
            JavaScriptObject textLabelJs = AladinLiteWrapper.getAladinLite().createTextLabel(entry.getKey(), entry.getValue());
            textJsInstrumentMap.get(instrumentName).add(textLabelJs);
        }

        planningLabelsPerInstrument.put(futureFootprintRow, textJsInstrumentMap);


        drawPlanningPolygons();

        return individualInstrumentPolygon;
    }

    private void drawPlanningPolygons() {

        JavaScriptObject planningOverlay = AladinLiteWrapper.getInstance().getPlanningOverlay();
        JavaScriptObject planningOverlaySelectedInstrument = AladinLiteWrapper.getInstance()
                .getPlanningOverlaySelectedInstrument();

        AladinLiteWrapper.getInstance().removeAllFootprintsFromOverlay(planningOverlay);
        AladinLiteWrapper.getInstance().removeAllFootprintsFromOverlay(
                planningOverlaySelectedInstrument);

        AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(
                AladinLiteWrapper.getInstance().getFutureCatalog());

        AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(
                AladinLiteWrapper.getInstance().getFutureSelectedDetectorCatalogue());


        if (currentPlanningFootprintRow != null) {
            for (Entry<FutureFootprintRow, Map<String, List<JavaScriptObject>>> currEntry : planningLabelsPerInstrument
                    .entrySet()) {

                Map<String, List<JavaScriptObject>> planningTextFootprints = currEntry.getValue();
                for (List<JavaScriptObject> textFootprints : planningTextFootprints.values()) {
                    for (JavaScriptObject textFootprint : textFootprints) {
                        AladinLiteWrapper.getAladinLite().addFootprintToOverlay(
                                planningOverlaySelectedInstrument, textFootprint);

                    }
                }
            }
        }


        for (Entry<FutureFootprintRow, Map<String, JavaScriptObject>> currEntry : planningFootprintsPerInstrument
                .entrySet()) {

            if (currentPlanningFootprintRow == currEntry.getKey()) {
                double raDeg = currentPlanningFootprintRow.getCenterRaDeg();
                double decDeg = currentPlanningFootprintRow.getCenterDecDeg();
                Map<String, Vector<double[]>> detectorCenters = STCSGeneratorFactory
                        .getSTCSGenerator(
                                currentPlanningFootprintRow.getInstrument().getMission()
                                .getMissionName()).getDetectorsSkyCoordsForInstrument(
                                        raDeg, decDeg, currentPlanningFootprintRow.getRotationDeg(),
                                        currentPlanningFootprintRow.getInstrument().getInstrumentName(),
                                        currentPlanningFootprintRow.getAperture());

                if (!currentPlanningFootprintRow.getIsAllInstrumentsSelected()) {

                    for (Entry<String, Vector<double[]>> currCenter : detectorCenters.entrySet()) {

                        Log.debug("[cc] Det name " + currCenter.getKey()
                                + " coords " + currCenter.getValue().get(0)[0] + " "
                                + currCenter.getValue().get(0)[1]);

                        Map<String, Object> details = new HashMap<>();

                        details.put(PlanningConstant.INSTRUMENT, currentPlanningFootprintRow
                                .getInstrument().getInstrumentName());
                        details.put(PlanningConstant.DETECTOR, currCenter.getKey());
                        details.put(PlanningConstant.REFERENCE_RA,
                                Double.toString(currCenter.getValue().get(0)[0]));
                        details.put(PlanningConstant.REFERENCE_DEC,
                                Double.toString(currCenter.getValue().get(0)[1]));
                        details.put(PlanningConstant.OVERLAY_PROPERTY, PlanningConstant.OVERLAY_NAME);
                        details.put(PlanningConstant.COO_FRAME, AladinLiteWrapper
                                .getCoordinatesFrame().name());

                        if (currCenter.getKey().equals(currentPlanningFootprintRow.getAperture())) {

                            AladinLiteWrapper.getInstance()
                            .addSourcesToFutureSelectedDetectorCatalogue(
                                    Double.toString(currCenter.getValue().get(0)[0]),
                                    Double.toString(currCenter.getValue().get(0)[1]),
                                            details);
                        } else {
                            AladinLiteWrapper.getInstance().addSourcesToFutureCatalog(
                                    Double.toString(currCenter.getValue().get(0)[0]),
                                    Double.toString(currCenter.getValue().get(0)[1]), details);
                        }
                    }

                } else {
                    Map<String, Vector<double[]>> currDetectorCenters = STCSGeneratorFactory
                            .getSTCSGenerator(
                                    currentPlanningFootprintRow.getInstrument().getMission()
                                    .getMissionName())
                            .getDetectorsSkyCoordsForFoV(
                                    raDeg,
                                            decDeg,
                                    currentPlanningFootprintRow.getRotationDeg(),
                                            currentPlanningFootprintRow.getInstrument().getInstrumentName(),
                                            currentPlanningFootprintRow.getAperture());

                    for (Entry<String, Vector<double[]>> currCenter : currDetectorCenters
                            .entrySet()) {

                        Log.debug("[cc] Det name " + currCenter.getKey()
                                + " coords " + currCenter.getValue().get(0)[0] + " "
                                + currCenter.getValue().get(0)[1]);
                        Map<String, Object> details = new HashMap<>();

                        details.put(PlanningConstant.INSTRUMENT, currentPlanningFootprintRow.getInstrument().getInstrumentName());
                        details.put(PlanningConstant.DETECTOR, currCenter.getKey());
                        details.put(PlanningConstant.REFERENCE_RA,
                                Double.toString(currCenter.getValue().get(0)[0]));
                        details.put(PlanningConstant.REFERENCE_DEC,
                                Double.toString(currCenter.getValue().get(0)[1]));
                        details.put(PlanningConstant.COO_FRAME, AladinLiteWrapper
                                .getCoordinatesFrame().name());

                        if (currCenter.getKey().equals(currentPlanningFootprintRow.getAperture())) {
                            AladinLiteWrapper.getInstance()
                            .addSourcesToFutureSelectedDetectorCatalogue(
                                    Double.toString(currCenter.getValue().get(0)[0]),
                                    Double.toString(currCenter.getValue().get(0)[1]),
                                    details);
                        } else {
                            AladinLiteWrapper.getInstance().addSourcesToFutureCatalog(
                                    Double.toString(currCenter.getValue().get(0)[0]),
                                    Double.toString(currCenter.getValue().get(0)[1]), details);
                        }
                    }
                }
            }

            FutureFootprintRow currentFutureFootprintRow = currEntry.getKey();
            Map<String, JavaScriptObject> instrumentJsPolygons = currEntry.getValue();

            for (Entry<String, JavaScriptObject> currInstrumentJsPolygons : instrumentJsPolygons
                    .entrySet()) {
                if (currInstrumentJsPolygons.getKey().equals(
                        currentFutureFootprintRow.getInstrument().getInstrumentName())) {
                    AladinLiteWrapper.getAladinLite().addFootprintToOverlay(
                            planningOverlaySelectedInstrument, currInstrumentJsPolygons.getValue());
                } else {
                    AladinLiteWrapper.getAladinLite().addFootprintToOverlay(planningOverlay,
                            currInstrumentJsPolygons.getValue());
                }
            }
        }
    }

    /**
     * changeHiPS().
     * @param hips Input HiPS object
     * @param colorPalette Input ColorPalette object
     */
    protected final void changeHiPS(final String skyRowId, final HiPS hips, final ColorPalette colorPalette, double opacity) {

        ImageLayer imageLayer = AladinLiteWrapper.getInstance().getImageLayer(skyRowId);
    	if (imageLayer == null || !Objects.equals(imageLayer.getId(), hips.getSurveyId()))  {
            AladinLiteWrapper.getInstance().openHiPS(skyRowId, hips);
        }

        AladinLiteWrapper.getInstance().changeImageLayerOpacity(skyRowId, 1);
        AladinLiteWrapper.getInstance().setColorPalette(skyRowId, colorPalette);
        if (isReverseColorMap(colorPalette)) {
            reverseColorMap(hips);
        }
        
    }
    private void reverseColorMap(HiPS hips) {
        hips.setReversedColorMap(true);
        AladinLiteWrapper.getAladinLite().reverseColorMap();
    }

    private boolean isReverseColorMap(ColorPalette colorPalette) {
        return colorPalette.equals(ColorPalette.GREYSCALE_INV);
    }
    
    private boolean checkNotReverseAndGreyscale(HiPS hips, ColorPalette colorPalette) {
    	return !hips.isReversedColorMap() && colorPalette.equals(ColorPalette.GREYSCALE_INV);
    }
    private boolean checkReverseAndNotGreyscale(HiPS hips, ColorPalette colorPalette) {
    	return hips.isReversedColorMap()  && !colorPalette.equals(ColorPalette.GREYSCALE_INV);
    }
    
    private boolean checkSameOpacity(HiPS hips, double opacity) {
    	return currentOverlay != null && hips == currentOverlay && compareDouble(currentOverlayOpacity, opacity);
    }
    
    private boolean compareDouble(double val1, double val2) {
    	BigDecimal v1 = BigDecimal.valueOf(val1);
    	BigDecimal v2 = BigDecimal.valueOf(val2);
    	
    	return v1.equals(v2);
    }
    
    public void areaSelectionFinished(){
        view.deToggleSelectionMode();
    }
    
    public void areaSelectionKeyboardShortcutStart(){
        view.areaSelectionKeyboardShortcutStart();
    }


    /** ########### */
    /** SSO SECTION */
    /** ########### */

    protected class SSOOverlayAndPolyline {

        private String name;
        private ESASkySSOObjType type;
        private JavaScriptObject jsPolyline;
        private JavaScriptObject jsOverlay;

        public SSOOverlayAndPolyline(String name, ESASkySSOObjType type,
                JavaScriptObject jsPolyline, JavaScriptObject jsOverlay) {
            super();
            this.name = name;
            this.type = type;
            this.jsPolyline = jsPolyline;
            this.jsOverlay = jsOverlay;
        }

        public String getName() {
            return name;
        }

        public ESASkySSOObjType getType() {
            return type;
        }

        public JavaScriptObject getJsPolyline() {
            return jsPolyline;
        }

        public JavaScriptObject getJsOverlay() {
            return jsOverlay;
        }

    }

    public List<SSOOverlayAndPolyline> getSsoPolyline() {
        if (ssoPolyline == null) {
            ssoPolyline = new LinkedList<SSOOverlayAndPolyline>();
        }
        return ssoPolyline;
    }

    public void hideTooltip() {
       this.view.hideTooltip();
    }

    public void updateModuleVisibility() {
        view.updateModuleVisibility();
    }
}