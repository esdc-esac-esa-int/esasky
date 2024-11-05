package esac.archive.esasky.cl.web.client.view;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.*;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.HipsLayerChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ImageLayer;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.common.*;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.ChangePaletteBtn;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.HiPSDetailsPopup;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.shared.ColorPalette;

public class ImageConfigPanel extends CollapsablePanel {

    private ImageLayer layer;
    private CheckBox reverseButton;
    private DropDownMenu<String> stretchDropdownMenu;
    private DropDownMenu<String> tileFormatDropdownMenu;
    private ESASkyMultiRangeSlider cutSlider;
    private Label cutLeftLabel;
    private Label cutRightLabel;

    private ChangePaletteBtn colorMapButton;
    private ESASkySlider opacitySlider;
    private DropDownMenu<Boolean> blendMenu;
    private HiPS hips;

    public ImageConfigPanel() {
        this(false);
    }

    public ImageConfigPanel(boolean isCollapsed) {
        super(isCollapsed);
        initView();
    }

    private void initView() {
        FlexTable flexTable = new FlexTable();
        flexTable.setWidth("100%");
        flexTable.getElement().getStyle().setPadding(15, Style.Unit.PX);
        flexTable.getColumnFormatter().setWidth(0, "125px");

        // Color map
        Label colorMapLabel = new Label(TextMgr.getInstance().getText("imageConfigPanel_color_palette"));
        colorMapButton = new ChangePaletteBtn();
        colorMapButton.getWidget().getElement().getStyle().setMargin(0, Style.Unit.PX);
        colorMapButton.registerObserver(() -> {
            if (colorMapButton.getSelectedColorPalette() != null && layer != null) {
                String name = colorMapButton.getSelectedColorPalette().name();
                layer.setColorMapKeepOptions(name);
            }
        });

        flexTable.setWidget(0, 0, colorMapLabel);
        flexTable.setWidget(0, 1, colorMapButton);
        flexTable.setWidget(0, 2, createSkyDetailsBtn());

        // Reverse
        Label reverseLabel = new Label(TextMgr.getInstance().getText("imageConfigPanel_reverse"));
        reverseButton = new CheckBox();
        reverseButton.setTitle(TextMgr.getInstance().getText("imageConfigPanel_reverse"));
        reverseButton.addValueChangeHandler(event -> layer.setColorMapOptions(event.getValue(), stretchDropdownMenu.getSelectedObject()));

        flexTable.setWidget(1, 0, reverseLabel);
        flexTable.setWidget(1, 1, reverseButton);

        // Stretch
        Label stretchLabel = new Label(TextMgr.getInstance().getText("imageConfigPanel_stretch"));
        stretchDropdownMenu = new DropDownMenu<>("", TextMgr.getInstance().getText("imageConfigPanel_stretch"), 150, "stretch_fits_dropdown");
        stretchDropdownMenu.addMenuItem(new MenuItem<>("sqrt", "sqrt", true));
        stretchDropdownMenu.addMenuItem(new MenuItem<>("linear", "linear", true));
        stretchDropdownMenu.addMenuItem(new MenuItem<>("asinh", "asinh", true));
        stretchDropdownMenu.addMenuItem(new MenuItem<>("pow2", "pow2", true));
        stretchDropdownMenu.addMenuItem(new MenuItem<>("log", "log", true));
        stretchDropdownMenu.getElement().getStyle().setMargin(0, Style.Unit.PX);
        stretchDropdownMenu.getElement().getStyle().setWidth(100, Style.Unit.PCT);
        stretchDropdownMenu.registerObserver(() -> {
            String stretch = stretchDropdownMenu.getSelectedObject();
            layer.setColorMapOptions(reverseButton.getValue(), stretch);
        });
        flexTable.setWidget(2, 0, stretchLabel);
        flexTable.setWidget(2, 1, stretchDropdownMenu);

        Label cutLabel = new Label(TextMgr.getInstance().getText("imageConfigPanel_cuts"));
        flexTable.setWidget(4, 0, cutLabel);
        flexTable.setWidget(4, 1, initSlider(0, 1));


        Label blendLabel = new Label(TextMgr.getInstance().getText("imageConfigPanel_blending"));
        blendMenu = new DropDownMenu<>(TextMgr.getInstance().getText("imageConfigPanel_blending_default"), TextMgr.getInstance().getText("imageConfigPanel_blending"), 150, "blending_fits_dropdown");
        blendMenu.addMenuItem(new MenuItem<>(false, TextMgr.getInstance().getText("imageConfigPanel_blending_default"), true));
        blendMenu.addMenuItem(new MenuItem<>(true, TextMgr.getInstance().getText("imageConfigPanel_blending_additive"), true));
        blendMenu.registerObserver(() -> layer.setBlending(blendMenu.getSelectedObject()));
        blendMenu.getElement().getStyle().setMargin(0, Style.Unit.PX);
        blendMenu.getElement().getStyle().setWidth(100, Style.Unit.PCT);

        flexTable.setWidget(9, 0, blendLabel);
        flexTable.setWidget(9, 1, blendMenu);


        Label opacityLabel = new Label(TextMgr.getInstance().getText("imageConfigPanel_opacity"));
        opacitySlider = new ESASkySlider(0, 1, 150);
        opacitySlider.registerValueChangeObserver(opacity -> {
            if (layer != null) {
                layer.setOpacity(opacity);
            }
        });
        flexTable.setWidget(10, 0, opacityLabel);
        flexTable.setWidget(10, 1, opacitySlider);

        Label tileFormatLabel = new Label(TextMgr.getInstance().getText("imageConfigPanel_tile_format"));
        tileFormatDropdownMenu = new DropDownMenu<>("", TextMgr.getInstance().getText("imageConfigPanel_stretch"), 150, "stretch_fits_dropdown");
        tileFormatDropdownMenu.getElement().getStyle().setMargin(0, Style.Unit.PX);
        tileFormatDropdownMenu.getElement().getStyle().setWidth(100, Style.Unit.PCT);
        tileFormatDropdownMenu.registerObserver(() -> {
            // Register a temporary handler to observe if cut range change due to new file format
            final HandlerRegistration[] handler = new HandlerRegistration[1];
            handler[0] = CommonEventBus.getEventBus().addHandler(HipsLayerChangedEvent.TYPE, event -> {
                ImageLayer updatedLayer = event.getLayer();
                boolean cutsUpdated = cutSlider.getMaxValue() != updatedLayer.getMaxCut() || cutSlider.getMinValue() != updatedLayer.getMinCut();
                if (cutsUpdated && cutSlider.isSliderReady()) {
                        cutSlider.setMinMaxValues(updatedLayer.getMinCutLimit(), updatedLayer.getMaxCutLimit());
                        cutSlider.setSliderValue(updatedLayer.getMinCut(), updatedLayer.getMaxCut());
                }

                // Remove the handler after it is triggered
                handler[0].removeHandler();
            });

            String format = tileFormatDropdownMenu.getSelectedObject();
            layer.setImageFormat(format);
        });

        flexTable.setWidget(11, 0, tileFormatLabel);
        flexTable.setWidget(11, 1, tileFormatDropdownMenu);

        if (layer != null) {
            setDefaultValues();
        }

        this.add(flexTable);

    }

    private EsaSkyButton createSkyDetailsBtn() {
        final EsaSkyButton skyDetailsBtn = new EsaSkyButton(Icons.getInfoIcon());
        skyDetailsBtn.addStyleName("skyInfoBtn");
        skyDetailsBtn.setTitle(TextMgr.getInstance().getText("sky_skyDetails"));
        skyDetailsBtn.setRoundStyle();
        skyDetailsBtn.setSmallStyle();
        skyDetailsBtn.addClickHandler(event -> {
            HiPSDetailsPopup skyDetailsInfo = new HiPSDetailsPopup(this.hips);
            skyDetailsInfo.show();
            int defaultLeft = skyDetailsBtn.getAbsoluteLeft() + skyDetailsBtn.getOffsetWidth() / 2;
            if (defaultLeft + skyDetailsInfo.getOffsetWidth() > MainLayoutPanel.getMainAreaAbsoluteLeft() + MainLayoutPanel.getMainAreaWidth()) {
                defaultLeft -= skyDetailsInfo.getOffsetWidth();
            }
            skyDetailsInfo.setPopupPosition(defaultLeft,
                    skyDetailsBtn.getAbsoluteTop() + skyDetailsBtn.getOffsetHeight() / 2);
        });

        return skyDetailsBtn;
    }

    public void setLayerAndHiPS(ImageLayer layer, HiPS hips) {
        this.layer = layer;
        this.hips = hips;

        setDefaultValues();
    }

    public ImageLayer getLayer() {
        return layer;
    }

    @Override
    public void toggle() {
        if (layer != null) {
            super.toggle();
        }
    }

    public void setDefaultValues() {
        colorMapButton.setDefaultColorPallette(ColorPalette.valueOf(layer.getColorCfg().getColormap().toUpperCase()));
        reverseButton.setValue(layer.getColorCfg().getReversed());
        stretchDropdownMenu.selectObject(layer.getColorCfg().getStretch());

        if (cutSlider.isSliderReady()) {
            cutSlider.setMinMaxValues(layer.getMinCutLimit(), layer.getMaxCutLimit());
            cutSlider.setSliderValue(layer.getMinCut(), layer.getMaxCut());
        }

        blendMenu.selectObject(layer.getColorCfg().getAdditiveBlending() > 0);
        opacitySlider.setValue(layer.getColorCfg().getOpacity());

        tileFormatDropdownMenu.clearItems();
        String[] imgFormats = layer.getAvailableImageFormats();
        for (String imgFormat : imgFormats) {
            tileFormatDropdownMenu.addMenuItem(new MenuItem<>(imgFormat, imgFormat, true));
        }
        tileFormatDropdownMenu.selectObject(layer.getImageFormat());
    }

    public ColorPalette getSelectedColorPalette() {
        return colorMapButton.getSelectedColorPalette();
    }

    public void setDefaultColorPallette(ColorPalette colorPalette) {
        colorMapButton.setDefaultColorPallette(colorPalette);
    }

    private FlowPanel initSlider(double min, double max) {

        FlowPanel sliderContainer = new FlowPanel();

        FlowPanel textPanel = new FlowPanel();
        textPanel.addStyleName("treeMap__filter__text__container");


        cutLeftLabel = new Label();
        cutLeftLabel.setText(Double.toString(min));
        cutLeftLabel.addStyleName("treeMap__filter__text__left");

        cutRightLabel = new Label();
        cutRightLabel.setText(Double.toString(max));
        cutRightLabel.addStyleName("treeMap__filter__text__right");

        textPanel.add(cutLeftLabel);

        textPanel.add(cutRightLabel);

        sliderContainer.add(textPanel);


        cutSlider = new ESASkyMultiRangeSlider(min, max, 150);
        cutSlider.addStyleName("treeMap__slider");
        sliderContainer.add(cutSlider);

        NumberFormat numberFormat = NumberFormat.getFormat("0.0");

        cutSlider.registerValueChangeObserver((low, high) -> {
            layer.setCuts(low, high);
            cutLeftLabel.setText(numberFormat.format(low));
            cutRightLabel.setText(numberFormat.format(high));
        });

        return sliderContainer;

    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (cutSlider != null) {
            cutSlider.firstOpening();
        }
    }
}
