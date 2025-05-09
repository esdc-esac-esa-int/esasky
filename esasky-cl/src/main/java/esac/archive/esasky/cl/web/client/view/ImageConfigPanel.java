/*
ESASky
Copyright (C) 2025 European Space Agency

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

package esac.archive.esasky.cl.web.client.view;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.*;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.HipsLayerChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ImageLayer;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.common.*;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.ChangePaletteBtn;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.HiPSDetailsPopup;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.shared.ColorPalette;

import java.util.Objects;

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

    private boolean blockCutSliderEvent = false;

    public ImageConfigPanel(boolean isCollapsed, boolean showTileFormat) {
        super(isCollapsed, showTileFormat ? 255 : 223);
        initView(showTileFormat);
    }

    private void initView(boolean showTileFormat) {
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
                if (cutSlider.isSliderReady()) {
                    blockCutSliderEvent = true;
                    cutSlider.setMinMaxValues(updatedLayer.getMinCut(), updatedLayer.getMaxCut());
                    cutSlider.setSliderValue(updatedLayer.getMinCut(), updatedLayer.getMaxCut());
                    blockCutSliderEvent = false;
                }

                // Remove the handler after it is triggered
                handler[0].removeHandler();
            });

            String format = tileFormatDropdownMenu.getSelectedObject();
            layer.setImageFormat(format);
        });

        if(showTileFormat) {
	        flexTable.setWidget(11, 0, tileFormatLabel);
	        flexTable.setWidget(11, 1, tileFormatDropdownMenu);
        }

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

    public ImageLayer discoverLayer(String skyRowId) {
        this.layer = AladinLiteWrapper.getAladinLite().getImageLayer(skyRowId);
        this.layer.setCutLimits();
        this.setDefaultValues();
        return this.layer;
    }

    public void setLayerAndHiPS(ImageLayer layer, HiPS hips) {
        if (this.layer == null || !Objects.equals(this.hips, hips)) {
            this.layer.setCutLimits();
            this.layer = layer;
            this.hips = hips;
            setDefaultValues();
        }
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
            blockCutSliderEvent = true;
            cutSlider.setMinMaxValues(layer.getMinCutLimit(), layer.getMaxCutLimit());
            cutSlider.setSliderValue(layer.getMinCutLimit(), layer.getMaxCutLimit());
            blockCutSliderEvent = false;
        }

        blendMenu.selectObject(layer.getColorCfg().getAdditiveBlending() > 0);
        opacitySlider.setValue(layer.getColorCfg().getOpacity());

        tileFormatDropdownMenu.clearItems();
        String[] imgFormats = layer.getAvailableImageFormats();
        if (imgFormats != null) {
            for (String imgFormat : imgFormats) {
                tileFormatDropdownMenu.addMenuItem(new MenuItem<>(imgFormat, imgFormat, true));
            }
        }
        tileFormatDropdownMenu.selectObject(layer.getImageFormat());
    }

    public ColorPalette getSelectedColorPalette() {
        return colorMapButton.getSelectedColorPalette();
    }

    public void setDefaultColorPallette(ColorPalette colorPalette) {
        colorMapButton.setDefaultColorPallette(colorPalette);
    }

    public boolean getReversed() {
        return reverseButton.getValue();
    }

    public void setReversed(boolean reversed) {
        reverseButton.setValue(reversed);
    }

    public String getStretch() {
        return stretchDropdownMenu.getSelectedObject();
    }

    public void setStretch(String stretch) {
        stretchDropdownMenu.selectObject(stretch);
    }

    public double[] getCuts() {
        return new double[] {
                cutSlider.getCurrentLowValue(), cutSlider.getCurrentHighValue()
        };
    }
    public void setCuts(double[] cuts) {
        if (cutSlider.isSliderReady()) {
            int dynamicDecimalPlaces = calculateDecimalPlaces(cuts[0], cuts[1]);
            NumberFormat numberFormat = NumberFormat.getFormat(createFormatString(dynamicDecimalPlaces));

            cutSlider.setHandleValues(cuts[0], cuts[1]);
            layer.setCuts(cuts[0], cuts[1]);
            cutLeftLabel.setText(numberFormat.format(cuts[0]));
            cutRightLabel.setText(numberFormat.format(cuts[1]));
        }
    }

    public double[] getCutLimits() {
        return new double[] {
                layer.getMinCutLimit(), layer.getMaxCutLimit()
        };
    }

    public void setCutLimits(double[] cuts) {
        if (cutSlider.isSliderReady()) {
            cutSlider.setMinMaxValues(cuts[0], cuts[1]);
            cutSlider.setSliderValue(cuts[0], cuts[1]);
        }
    }

    public boolean getBlending() {
        return blendMenu.getSelectedObject();
    }

    public void setBlending(boolean blending) {
        blendMenu.selectObject(blending);
    }

    public double getOpacity() {
        return opacitySlider.getCurrentValue();
    }

    public void setOpacity(double opacity) {
        opacitySlider.setValue(opacity);
    }

    public String getTileFormat() {
        return tileFormatDropdownMenu.getSelectedObject();
    }

    public void setTileFormat(String tileFormat) {
        tileFormatDropdownMenu.selectObject(tileFormat);
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
        
        cutSlider.registerValueChangeObserver((low, high) -> {
            if (!blockCutSliderEvent) {
                layer.setCuts(low, high);
            }
            int dynamicDecimalPlaces = calculateDecimalPlaces(low, high);
            NumberFormat dynamicNumberFormat = NumberFormat.getFormat(createFormatString(dynamicDecimalPlaces));

            cutLeftLabel.setText(dynamicNumberFormat.format(low));
            cutRightLabel.setText(dynamicNumberFormat.format(high));
        });

        return sliderContainer;

    }
    
    private int calculateDecimalPlaces(double min, double max) {
        double range = Math.abs(max - min);
        if (range >= 100) {
            return 0;
        } else if (range >= 1) {
            return 1;
        } else {
            return 5;
        }
    }

    private String createFormatString(int decimalPlaces) {
        StringBuilder format = new StringBuilder("0");
        if (decimalPlaces > 0) {
            format.append(".");
            for (int i = 0; i < decimalPlaces; i++) {
                format.append("0");
            }
        }
        return format.toString();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (cutSlider != null) {
            cutSlider.firstOpening();
        }
    }
}
