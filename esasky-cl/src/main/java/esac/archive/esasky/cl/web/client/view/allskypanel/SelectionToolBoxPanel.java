package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.ui.FlowPanel;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.MultiSelectableDataInSkyChangedEvent;
import esac.archive.esasky.cl.web.client.event.MultiSelectableDataInSkyChangedEventHandler;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.animation.EsaSkyAnimation;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyToggleButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.HelpButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

public class SelectionToolBoxPanel extends FlowPanel{

    protected AladinShape source;

    private FlowPanel shapeButtonContainer = new FlowPanel();
    private EsaSkyToggleButton boxButton;
    private EsaSkyToggleButton circleButton;
    private EsaSkyToggleButton polyButton;

    private boolean toolboxIsManuallyHidden = false;
    private boolean toolboxIsVisible = false;

    private EsaSkyButton togglePanelButton = new EsaSkyButton(SelectionToolBoxPanel.resources.arrowIcon());
    private EsaSkyAnimation togglePanelButtonMoveAnimation = new EsaSkyAnimation() {

        @Override
        protected void setCurrentPosition(double newPosition) {
            getElement().getStyle().setMarginRight(newPosition, Unit.PX);
            shapeButtonContainer.getElement().getStyle().setMarginLeft(- newPosition * 0.75, Unit.PX);
        }

        @Override
        protected Double getCurrentPosition() {
            String marginRightString = getElement().getStyle().getMarginRight();
            if (marginRightString.equals("")){
                marginRightString = "0px";
            }
            //remove suffix "px"
            marginRightString = marginRightString.substring(0, marginRightString.length() - 2);
            Double currentPosition = new Double(marginRightString);
            return currentPosition;
        }
    };

    private EsaSkyAnimation toggleShapeButtonsMoveAnimation = new EsaSkyAnimation() {

        @Override
        protected void setCurrentPosition(double newPosition) {
            shapeButtonContainer.getElement().getStyle().setHeight(newPosition, Unit.PX);
        }

        @Override
        protected Double getCurrentPosition() {
            String heightString = shapeButtonContainer.getElement().getStyle().getHeight();
            if (heightString.equals("")){
                heightString = "0px";
            }
            //remove suffix "px"
            heightString = heightString.substring(0, heightString.length() - 2);
            Double currentPosition = new Double(heightString);
            return currentPosition;
        }
    };

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
        style = resources.style();
        style.ensureInjected();
        initView();
        setVisible(false);
        hideToolbox();
    }

    private void toggleOtherButtons(EsaSkyToggleButton buttonPressed) {
        if(buttonPressed != boxButton) {
            boxButton.setToggleStatus(false);
        }
        if(buttonPressed != circleButton) {
            circleButton.setToggleStatus(false);
        }
        if(buttonPressed != polyButton) {
            polyButton.setToggleStatus(false);
        }
    }

    public void deToggleAllButtons() {
        boxButton.setToggleStatus(false);
        circleButton.setToggleStatus(false);
        polyButton.setToggleStatus(false);
    }

    public void areaSelectionKeyboardShortcutStart() {
        String mode = AladinLiteWrapper.getAladinLite().getSelectionMode();
        if (mode.equals("BOX")) {
            boxButton.setToggleStatus(true);
            toggleOtherButtons(boxButton);
        }
        else if (mode.equals("CIRCLE")) {
            circleButton.setToggleStatus(true);
            toggleOtherButtons(circleButton);
        }
        else if (mode.equals("POLYGON")) {
            polyButton.setToggleStatus(true);
            toggleOtherButtons(polyButton);
        }
    }

    private void showToolbox() {
        toolboxIsVisible = true;
        togglePanelButton.setTitle(TextMgr.getInstance().getText("selectionToolbox_hideSelectionToolbox"));
        togglePanelButton.rotate(90, 500);
        togglePanelButtonMoveAnimation.animateTo(0, 500);
        toggleShapeButtonsMoveAnimation.animateTo(123, 200);
    }

    private void hideToolbox() {
        toolboxIsVisible = false;
        togglePanelButton.setTitle(TextMgr.getInstance().getText("selectionToolbox_showSelectionToolbox"));
        togglePanelButton.rotate(-90, 500);
        togglePanelButtonMoveAnimation.animateTo(-120, 400);
        toggleShapeButtonsMoveAnimation.animateTo(0, 700);
        if(AladinLiteWrapper.getAladinLite().isAttached()) {
            AladinLiteWrapper.getAladinLite().endSelectionMode();
        }
        deToggleAllButtons();
    }

    private void toggleToolbox() {
        if(toolboxIsVisible) {
            hideToolbox();
        } else {
            showToolbox();
        }
    }

    private void setToolboxVisible() {
        setVisible(true);
    }

    private void setToolboxHidden() {
        setVisible(false);
    }

    private void initView() {
        FlowPanel toggleAndHelpContainer = new FlowPanel();
        toggleAndHelpContainer.addStyleName("selectionToolBox__toggleAndHelpContainer");
        togglePanelButton.getElement().setId("toggleSelectionToolboxPanelButton");
        togglePanelButton.setNonTransparentBackground();
        togglePanelButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                if(toolboxIsVisible) {
                    toolboxIsManuallyHidden = true;
                }
                toggleToolbox();
            }
        });

        CommonEventBus.getEventBus().addHandler(MultiSelectableDataInSkyChangedEvent.TYPE, event -> {
            if(event.isAtLeastOneMultiSelectableDatasetInSky()) {
                setToolboxVisible();
                if(!toolboxIsManuallyHidden) {
                    showToolbox();
                }
            } else {
                hideToolbox();
                setToolboxHidden();
            }
        });
        toggleAndHelpContainer.add(togglePanelButton);

        HelpButton helpButton = new HelpButton(TextMgr.getInstance().getText("selectionToolbox_helpText"), TextMgr.getInstance().getText("selectionToolbox_helpHeader"));
        helpButton.addStyleName("selectionToolboxHelpButton");
        toggleAndHelpContainer.add(helpButton);
        add(toggleAndHelpContainer);

        boxButton = new EsaSkyToggleButton(Icons.getDashedRectangleIcon());
        addButtonBehaviorAndStyle(boxButton, "box");
        circleButton = new EsaSkyToggleButton(Icons.getDashedCircleIcon());
        addButtonBehaviorAndStyle(circleButton, "circle");
        polyButton = new EsaSkyToggleButton(Icons.getDashedPolyIcon());
        polyButton = new EsaSkyToggleButton(Icons.getDashedPolyIcon());
        addButtonBehaviorAndStyle(polyButton, "polygon");

        shapeButtonContainer.addStyleName("selectionToolbox__shapeButtonContainer");

        shapeButtonContainer.add(boxButton);
        shapeButtonContainer.add(circleButton);
        shapeButtonContainer.add(polyButton);
        add(shapeButtonContainer);

        this.getElement().setId("selectionToolbox");
    }

    private void addButtonBehaviorAndStyle(EsaSkyToggleButton button, String mode) {
        addCommonButtonStyle(button, TextMgr.getInstance().getText("selectionToolbox_" + mode + "ButtonTooltip"));
        button.addClickHandler(
                event -> {
                    if(button.getToggleStatus()) {
                        AladinLiteWrapper.getAladinLite().setSelectionType("SHAPE");
                        AladinLiteWrapper.getAladinLite().setSelectionMode(mode);
                        AladinLiteWrapper.getAladinLite().startSelectionMode();
                        toggleOtherButtons(button);
                    } else {
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
}
