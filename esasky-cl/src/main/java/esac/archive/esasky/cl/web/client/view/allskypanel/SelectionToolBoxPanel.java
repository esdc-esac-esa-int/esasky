package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteSelectSearchAreaEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.MultiSelectableDataInSkyChangedEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.animation.EsaSkyAnimation;
import esac.archive.esasky.cl.web.client.view.common.Toggler;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.HelpButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import com.allen_sauer.gwt.log.client.Log;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SelectionToolBoxPanel extends FlowPanel {

    protected AladinShape source;

    private FlowPanel shapeButtonContainer = new FlowPanel();
    private EsaSkyToggleButton boxButton;
    private EsaSkyToggleButton circleButton;
    private EsaSkyToggleButton polyButton;

    private boolean toolboxIsManuallyHidden = false;
    private boolean toolboxIsVisible = false;
    private final boolean searchSelection;

    private EsaSkyButton togglePanelButton = new EsaSkyButton(SelectionToolBoxPanel.resources.arrowIcon());
    private EsaSkyAnimation togglePanelButtonMoveAnimation;
    private EsaSkyAnimation toggleShapeButtonsMoveAnimation;

    private Toggler searchAreaDetailsToggler;
    private FlowPanel selectionDetailPanel;

    private static Resources resources = GWT.create(Resources.class);
    private CssResource style;

    public static interface Resources extends ClientBundle {
        @Source("selectionToolbox.css")
        @CssResource.NotStrict
        CssResource style();

        @Source("up_arrow_outline.png")
        @ImageOptions(flipRtl = true)
        ImageResource arrowIcon();
    }

    public SelectionToolBoxPanel() {
        this(false);
    }

    public SelectionToolBoxPanel(boolean searchSelection) {
        style = resources.style();
        style.ensureInjected();
        this.searchSelection = searchSelection;
        initView();
        setVisible(false);
        hideToolbox();
    }

    private void hideSearchAreaDetails() {
        if (searchAreaDetailsToggler != null) {
            searchAreaDetailsToggler.setToggleStatus(false);
        }
    }

    private void toggleOtherButtons(EsaSkyToggleButton buttonPressed) {
        if (buttonPressed != boxButton) {
            boxButton.setToggleStatus(false);
        }
        if (buttonPressed != circleButton) {
            circleButton.setToggleStatus(false);
        }
        if (buttonPressed != polyButton) {
            polyButton.setToggleStatus(false);
        }

        if (searchAreaDetailsToggler != null) {
            searchAreaDetailsToggler.setToggleStatus(false);
        }

        hideSearchAreaDetails();
    }

    public void deToggleAllButtons() {
        boxButton.setToggleStatus(false);
        circleButton.setToggleStatus(false);
        polyButton.setToggleStatus(false);
        hideSearchAreaDetails();
    }

    public void areaSelectionKeyboardShortcutStart() {
        String mode = AladinLiteWrapper.getAladinLite().getSelectionMode();
        if (mode.equals("BOX")) {
            boxButton.setToggleStatus(true);
            toggleOtherButtons(boxButton);
        } else if (mode.equals("CIRCLE")) {
            circleButton.setToggleStatus(true);
            toggleOtherButtons(circleButton);
        } else if (mode.equals("POLYGON")) {
            polyButton.setToggleStatus(true);
            toggleOtherButtons(polyButton);
        }

        hideSearchAreaDetails();
    }

    protected void showToolbox() {
        toolboxIsVisible = true;

        if (this.searchSelection) {
            setVisible(true);
            toggleShapeButtonsMoveAnimation.animateTo(60, 900);
            hideSearchAreaDetails();
        } else {
            togglePanelButtonMoveAnimation.animateTo(0, 500);
            togglePanelButton.setTitle(TextMgr.getInstance().getText("selectionToolbox_hideSelectionToolbox"));
            togglePanelButton.rotate(90, 500);
            toggleShapeButtonsMoveAnimation.animateTo(123, 200);
        }
    }

    protected void hideToolbox() {
        toolboxIsVisible = false;

        if (this.searchSelection) {
            toggleShapeButtonsMoveAnimation.animateTo(250, 700);
        } else {
            togglePanelButton.setTitle(TextMgr.getInstance().getText("selectionToolbox_showSelectionToolbox"));
            togglePanelButton.rotate(-90, 500);
            togglePanelButtonMoveAnimation.animateTo(-120, 400);
            togglePanelButton.setTitle(TextMgr.getInstance().getText("selectionToolbox_showSelectionToolbox"));
            togglePanelButton.rotate(-90, 500);
            togglePanelButtonMoveAnimation.animateTo(-120, 400);
            toggleShapeButtonsMoveAnimation.animateTo(0, 700);
        }


        if (AladinLiteWrapper.getAladinLite().isAttached()) {
            AladinLiteWrapper.getAladinLite().endSelectionMode();
        }
        deToggleAllButtons();
    }

    public void toggleToolbox() {
        if (toolboxIsVisible) {
            hideToolbox();
            if (this.searchSelection) {
                AladinLiteWrapper.getAladinLite().clearSearchArea();
            }
        } else {
            showToolbox();
        }
    }

    public boolean toolboxVisible() {
        return toolboxIsVisible;
    }

    private void setToolboxVisible() {
        setVisible(true);
    }

    private void setToolboxHidden() {
        setVisible(false);
    }

    private FlowPanel initSelectionDetailCircle() {

        FlowPanel detailContainer = new FlowPanel();
        detailContainer.setStyleName("selectionToolbox__detailsPanelCircle");
        Label headerLabel = new Label();
        headerLabel.setText("Cone");
        headerLabel.setStyleName("selectionToolbox__detailsPanelHeaderLabel");
        detailContainer.add(headerLabel);

        FlowPanel radiusContainer = new FlowPanel();
        Label radiusLabel = new Label();
        radiusLabel.setText("Radius");
        TextBox radiusText = new TextBox();
        radiusContainer.add(radiusLabel);
        radiusContainer.add(radiusText);

        FlowPanel raContainer = new FlowPanel();
        Label raLabel = new Label();
        raLabel.setText("RA");
        TextBox raText = new TextBox();
        raContainer.add(raLabel);
        raContainer.add(raText);


        FlowPanel decContainer = new FlowPanel();
        Label decLabel = new Label();
        decLabel.setText("DEC");
        TextBox decText = new TextBox();
        decContainer.add(decLabel);
        decContainer.add(decText);

        EsaSkyButton btn = new EsaSkyButton(TextMgr.getInstance().getText("selectionToolbox_searchArea_submitButton"));
        btn.addStyleName("selectionToolbox__detailsPanelSubmitButton");

        detailContainer.add(radiusContainer);
        detailContainer.add(raContainer);
        detailContainer.add(decContainer);
        detailContainer.add(btn);

        String inputErrorClassName = "input__error";
        btn.addClickHandler(event -> {
            try {
                detailContainer.removeStyleName(inputErrorClassName);
                AladinLiteWrapper.getAladinLite().createSearchArea("CIRCLE ICRS " + raText.getText() + " " + decText.getText() + " " + radiusText.getText());
                AladinLiteWrapper.getAladinLite().endSelectionMode();
            }catch (Exception ex) {
                detailContainer.addStyleName(inputErrorClassName);
                Log.debug(ex.getMessage(), ex);
            }

        });


        CommonEventBus.getEventBus().addHandler(AladinLiteSelectSearchAreaEvent.TYPE, searchAreaEvent -> {
            if (searchAreaEvent != null && searchAreaEvent.getSearchArea() != null && searchAreaEvent.getSearchArea().isCircle()) {
                detailContainer.removeStyleName(inputErrorClassName);
                radiusText.setText(searchAreaEvent.getSearchArea().getRadius());
                raText.setText(Double.toString(searchAreaEvent.getSearchArea().getCoordinates()[0].getRaDeg()));
                decText.setText(Double.toString(searchAreaEvent.getSearchArea().getCoordinates()[0].getDecDeg()));
            }

        });

        return detailContainer;
    }


    private FlowPanel initSlectionDetailStcs(String header) {
        FlowPanel detailContainer = new FlowPanel();
        detailContainer.setStyleName("selectionToolbox__detailsPanelPoly");

        FlowPanel headerContainer = new FlowPanel();
        headerContainer.addStyleName("selectionToolbox__detailsPanelHeaderContainer");
        Label headerLabel = new Label();
        headerLabel.setText(header);
        headerLabel.setStyleName("selectionToolbox__detailsPanelHeaderLabel");
        headerContainer.add(headerLabel);

        TextBox stcsText = new TextBox();
        stcsText.setTitle(TextMgr.getInstance().getText("selectionToolbox_searchArea_title"));

        detailContainer.add(headerContainer);
        detailContainer.add(stcsText);

        EsaSkyButton btn = new EsaSkyButton(TextMgr.getInstance().getText("selectionToolbox_searchArea_submitButton"));
        btn.addStyleName("selectionToolbox__detailsPanelSubmitButton");
        detailContainer.add(btn);


        String inputErrorClassName = "input__error";
        btn.addClickHandler(event -> {
            try {
                stcsText.removeStyleName(inputErrorClassName);
                AladinLiteWrapper.getAladinLite().createSearchArea(stcsText.getText());
                AladinLiteWrapper.getAladinLite().endSelectionMode();
            } catch (Exception ex) {
                stcsText.setText(TextMgr.getInstance().getText("selectionToolbox_searchArea_submitError"));
                stcsText.addStyleName(inputErrorClassName);
                Log.debug(ex.getMessage(), ex);
            }

        });

        CommonEventBus.getEventBus().addHandler(AladinLiteSelectSearchAreaEvent.TYPE, searchAreaEvent -> {
            if (searchAreaEvent != null && searchAreaEvent.getSearchArea() != null && !searchAreaEvent.getSearchArea().isCircle()) {
                stcsText.removeStyleName(inputErrorClassName);
                stcsText.setText(searchAreaEvent.getSearchArea().getAreaType() + " ICRS " +
                        Arrays.stream(searchAreaEvent.getSearchArea().getCoordinates())
                                .map(x -> x.getRaDeg() + " " + x.getDecDeg()).collect(Collectors.joining(" ")));
            }

        });

        return detailContainer;
    }

    private void initView() {

        if (this.searchSelection) {
            initSearchView();
        } else {
            initSelectView();
        }


        boxButton = new EsaSkyToggleButton(Icons.getDashedRectangleIcon());
        addButtonBehaviorAndStyle(boxButton, "box");
        circleButton = new EsaSkyToggleButton(Icons.getDashedCircleIcon());
        addButtonBehaviorAndStyle(circleButton, "circle");
        polyButton = new EsaSkyToggleButton(Icons.getDashedPolyIcon());
        addButtonBehaviorAndStyle(polyButton, "polygon");


        shapeButtonContainer.add(boxButton);
        shapeButtonContainer.add(circleButton);
        shapeButtonContainer.add(polyButton);
        add(shapeButtonContainer);

        if (searchAreaDetailsToggler != null && selectionDetailPanel != null) {
            add(searchAreaDetailsToggler);
            add(selectionDetailPanel);
        }

        this.getElement().setId("selectionToolbox");
    }

    private void initSearchView() {
        toggleShapeButtonsMoveAnimation = getShapeButtonsAnimationHorizontal();
        shapeButtonContainer.addStyleName("selectionToolbox__shapeButtonContainerHorizontal");
        selectionDetailPanel = new FlowPanel();

        FlowPanel circleDetails = initSelectionDetailCircle();
        FlowPanel polyDetails = initSlectionDetailStcs("Polygon");
        FlowPanel boxDetails = initSlectionDetailStcs("Box");

        FlowPanel headerContainer = new FlowPanel();
        headerContainer.addStyleName("selectionToolbox__searchHeaderContainer");
        Label headerLabel = new Label(TextMgr.getInstance().getText("selectionToolbox_searchArea_title"));
        headerLabel.addStyleName("selectionToolbox__searchHeaderLabel");
        HelpButton helpButton = new HelpButton(TextMgr.getInstance().getText("selectionToolbox_searchArea_helpText"),
                TextMgr.getInstance().getText("selectionToolbox_searchArea_helpHeader"));
        helpButton.setStyleName("selectionToolboxHelpButton");

        headerContainer.add(headerLabel);
        headerContainer.add(helpButton);

        add(headerContainer);

        searchAreaDetailsToggler = new Toggler(selectionDetailPanel);
        searchAreaDetailsToggler.addClickHandler(event -> {

            if (boxButton.getToggleStatus()) {
                selectionDetailPanel.clear();
                selectionDetailPanel.add(boxDetails);
            } else if (polyButton.getToggleStatus()) {
                selectionDetailPanel.clear();
                selectionDetailPanel.add(polyDetails);
            } else {
                selectionDetailPanel.clear();
                selectionDetailPanel.add(circleDetails);
            }
        });
    }

    private void initSelectView() {
        toggleShapeButtonsMoveAnimation = getShapeButtonsAnimation();
        FlowPanel toggleAndHelpContainer = new FlowPanel();
        togglePanelButtonMoveAnimation = getToggleButtonAnimation();
        toggleAndHelpContainer.addStyleName("selectionToolBox__toggleAndHelpContainer");
        togglePanelButton.getElement().setId("toggleSelectionToolboxPanelButton");
        togglePanelButton.setNonTransparentBackground();
        togglePanelButton.addClickHandler(arg0 -> {
            if (toolboxIsVisible) {
                toolboxIsManuallyHidden = true;
            }
            toggleToolbox();
        });

        toggleAndHelpContainer.add(togglePanelButton);

        HelpButton helpButton = new HelpButton(TextMgr.getInstance().getText("selectionToolbox_helpText"), TextMgr.getInstance().getText("selectionToolbox_helpHeader"));
        helpButton.addStyleName("selectionToolboxHelpButton");
        toggleAndHelpContainer.add(helpButton);
        add(toggleAndHelpContainer);

        CommonEventBus.getEventBus().addHandler(MultiSelectableDataInSkyChangedEvent.TYPE, event -> {
            if (event.isAtLeastOneMultiSelectableDatasetInSky()) {
                setToolboxVisible();
                if (!toolboxIsManuallyHidden) {
                    showToolbox();
                }
            } else {
                hideToolbox();
                setToolboxHidden();
            }
        });

        shapeButtonContainer.addStyleName("selectionToolbox__shapeButtonContainer");
    }

    private void addButtonBehaviorAndStyle(EsaSkyToggleButton button, String mode) {
        String typeTooltipExtra = this.searchSelection ? "searchArea_" : "";
        addCommonButtonStyle(button, TextMgr.getInstance().getText("selectionToolbox_" + typeTooltipExtra + mode + "ButtonTooltip"));
        button.addClickHandler(
                event -> {
                    if (button.getToggleStatus()) {
                        AladinLiteWrapper.getAladinLite().setSelectionMode(mode);
                        if (this.searchSelection) {
                            AladinLiteWrapper.getAladinLite().setSelectionType("SEARCH");
                        } else {
                            AladinLiteWrapper.getAladinLite().setSelectionType("SHAPE");
                        }
                        AladinLiteWrapper.getAladinLite().startSelectionMode();
                        toggleOtherButtons(button);
                    } else {
                        if (this.searchSelection) {
                            AladinLiteWrapper.getAladinLite().clearSearchArea();
                            searchAreaDetailsToggler.close();
                            hideSearchAreaDetails();
                        }
                        AladinLiteWrapper.getAladinLite().endSelectionMode();
                    }
                });
    }


    private void addCommonButtonStyle(EsaSkyButton button, String tooltip) {
        button.setNonTransparentBackground();
        button.setBigStyle();
        button.addStyleName("selectionToolboxButton");
        button.setTitle(tooltip);
    }

    protected EsaSkyAnimation getToggleButtonAnimation() {
        return new EsaSkyAnimation() {

            @Override
            protected void setCurrentPosition(double newPosition) {
                getElement().getStyle().setMarginRight(newPosition, Unit.PX);
                shapeButtonContainer.getElement().getStyle().setMarginLeft(-newPosition * 0.75, Unit.PX);
            }

            @Override
            protected Double getCurrentPosition() {
                String marginRightString = getElement().getStyle().getMarginRight();
                if (marginRightString.equals("")) {
                    marginRightString = "0px";
                }
                //remove suffix "px"
                marginRightString = marginRightString.substring(0, marginRightString.length() - 2);
                Double currentPosition = new Double(marginRightString);
                return currentPosition;
            }
        };
    }

    protected EsaSkyAnimation getShapeButtonsAnimation() {
        return new EsaSkyAnimation() {

            @Override
            protected void setCurrentPosition(double newPosition) {
                shapeButtonContainer.getElement().getStyle().setHeight(newPosition, Unit.PX);
            }

            @Override
            protected Double getCurrentPosition() {
                String heightString = shapeButtonContainer.getElement().getStyle().getHeight();
                if (heightString.equals("")) {
                    heightString = "0px";
                }
                //remove suffix "px"
                heightString = heightString.substring(0, heightString.length() - 2);
                Double currentPosition = new Double(heightString);
                return currentPosition;
            }
        };
    }

    protected EsaSkyAnimation getShapeButtonsAnimationHorizontal() {
        return new EsaSkyAnimation() {

            @Override
            protected void setCurrentPosition(double newPosition) {
                getElement().getStyle().setLeft(newPosition, Unit.PX);
                shapeButtonContainer.getElement().getStyle().setLeft(newPosition, Unit.PX);
            }

            @Override
            protected Double getCurrentPosition() {
                String heightString = shapeButtonContainer.getElement().getStyle().getLeft();
                if (heightString.equals("")) {
                    heightString = "0px";
                }
                //remove suffix "px"
                heightString = heightString.substring(0, heightString.length() - 2);
                Double currentPosition = new Double(heightString);
                return currentPosition;
            }
            @Override
            protected void onComplete() {
                super.onComplete();

                if (!toolboxIsVisible) {
                    setVisible(false);
                }
            }
        };
    }
}
