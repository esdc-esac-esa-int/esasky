package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.ESASkyMultiRangeSlider;
import esac.archive.esasky.cl.web.client.view.common.EsaSkySwitch;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.ExtTapTreeMap;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.TreeMapChanged;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.TapDescriptorBase;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.LinkedList;
import java.util.List;

public class ExtTapPanel extends MovableResizablePanel<ExtTapPanel> {

    private PopupHeader<ExtTapPanel> header;
    FlowPanel mainContainer;
    TabLayoutPanel tabPanel;

    ExtTapTreeMap treeMap;
    GlobalTapPanel registryPanel;
    GlobalTapPanel vizierPanel;
    GlobalTapPanel esaPanel;

    private int selectedTabIndex = 0;

    private ESASkyMultiRangeSlider slider;

    private final List<TreeMapChanged> treemapObservers = new LinkedList<>();
    private boolean fovLimiterEnabled;
    public enum TabIndex {TREEMAP, REGISTRY, VIZIER, ESA}

    public interface Resources extends ClientBundle {
        @Source("extTapPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public ExtTapPanel() {
        super("", false);
        Resources resources = GWT.create(Resources.class);
        CssResource style = resources.style();
        style.ensureInjected();
        initView();
        hide();
    }

    private void initView() {
        mainContainer = new FlowPanel();
        mainContainer.addStyleName("extTapPanel__container");
        mainContainer.getElement().setId("extTapPanel_container_1");
        header = new PopupHeader<>(this, TextMgr.getInstance().getText("treeMap_EXT_TAP"),
                TextMgr.getInstance().getText("treeMapContainer_help_EXT_TAP"),
                TextMgr.getInstance().getText("treeMap_EXT_TAP"));


        EsaSkyButton resetTreemapBtn = new EsaSkyButton(Icons.getUndoArrowIcon());
        resetTreemapBtn.setTitle(TextMgr.getInstance().getText("extTapPanel_resetTreemapBtn"));
        resetTreemapBtn.addClickHandler(event -> {
            DescriptorRepository.getInstance().resetExternalDataCenterDescriptors();
            header.removeActionWidget(resetTreemapBtn);
        });



        fovLimiterEnabled = true;
        EsaSkySwitch switchBtn = new EsaSkySwitch("fovLimiterSwitch", fovLimiterEnabled,
                TextMgr.getInstance().getText("global_tap_panel_toggle_fov_restricted"),
                TextMgr.getInstance().getText("global_tap_panel_toggle_fov_restricted_tooltip"));
        switchBtn.addStyleName("globalTapPanel__fovSwitch");


        tabPanel = new TabLayoutPanel(50, Style.Unit.PX );
        tabPanel.addStyleName("extTapPanel__tabPanel");

        FlowPanel treeMapContainer = new FlowPanel();
        treeMap = new ExtTapTreeMap(EntityContext.EXT_TAP);
        treeMap.registerHeaderObserver(text -> {
            if (tabPanel.getSelectedIndex() == TabIndex.REGISTRY.ordinal()) {
                header.setText(TextMgr.getInstance().getText("treeMap_" + EntityContext.EXT_TAP) + text);
            }
        });

        treeMapContainer.add(treeMap);
        FlowPanel sliderContainer = initSliderContainer();
        treeMapContainer.add(sliderContainer);

        tabPanel.add(treeMapContainer, "Dashboard");

        registryPanel = new GlobalTapPanel();
        registryPanel.addTreeMapNewDataHandler(event -> tabPanel.selectTab(0));
        tabPanel.add(registryPanel, "TAP Registry");

        vizierPanel = new GlobalTapPanel(GlobalTapPanel.Modes.VIZIER);
        registryPanel.addTreeMapNewDataHandler(event -> tabPanel.selectTab(0));
        tabPanel.add(vizierPanel, "VizieR");

        esaPanel = new GlobalTapPanel(GlobalTapPanel.Modes.ESA);
        esaPanel.addTreeMapNewDataHandler(event -> tabPanel.selectTab(0));
        tabPanel.add(esaPanel, "ESA");

        tabPanel.selectTab(TabIndex.TREEMAP.ordinal());

        tabPanel.addSelectionHandler(event -> {
            selectedTabIndex = event.getSelectedItem();

            if (selectedTabIndex == TabIndex.REGISTRY.ordinal()) {
                registryPanel.loadData();
            } else if (selectedTabIndex == TabIndex.VIZIER.ordinal()) {
                vizierPanel.loadData();
            } else if (selectedTabIndex == TabIndex.ESA.ordinal()) {
                esaPanel.loadData();
            }

            if (selectedTabIndex != TabIndex.TREEMAP.ordinal()) {
                header.addActionWidget(switchBtn);
                header.setText(TextMgr.getInstance().getText("treeMap_" + EntityContext.EXT_TAP));
                header.removeActionWidget(resetTreemapBtn);
            } else {
                header.removeActionWidget(switchBtn);
                if (hasCustomDescriptor()) {
                    header.addActionWidget(resetTreemapBtn);
                }

            }

        });


        switchBtn.addClickHandler(event -> {
            setFovLimiterEnabled(!fovLimiterEnabled);
            switchBtn.setChecked(fovLimiterEnabled);
        });

        setFovLimiterEnabled(fovLimiterEnabled);

        mainContainer.add(header);
        mainContainer.add(tabPanel);
        this.add(mainContainer);
        this.addStyleName("extTapPanel");

        setMaxSize();

    }

    public void openTab(int tabIndex) {
        tabPanel.selectTab(tabIndex);
    }

    private FlowPanel initSliderContainer() {
        FlowPanel sliderContainer = new FlowPanel();

        FlowPanel textPanel = new FlowPanel();
        textPanel.addStyleName("treeMap__filter__text__container");

        Label leftLabel = new Label();
        leftLabel.setText(TextMgr.getInstance().getText("wavelength_GAMMA_RAY"));
        leftLabel.addStyleName("treeMap__filter__text__left");

        Label centerLabel = new Label();
        centerLabel.setText(TextMgr.getInstance().getText("wavelength_OPTICAL"));
        centerLabel.addStyleName("treeMap__filter__text__center");

        Label rightLabel = new Label();
        rightLabel.setText(TextMgr.getInstance().getText("wavelength_RADIO"));
        rightLabel.addStyleName("treeMap__filter__text__right");

        textPanel.add(leftLabel);
        textPanel.add(centerLabel);
        textPanel.add(rightLabel);

        sliderContainer.add(textPanel);

        slider = new ESASkyMultiRangeSlider(0, ESASkyColors.maxIndex() , 300);
        slider.addStyleName("treeMap__slider");

        sliderContainer.add(slider);
        treeMap.addSliderObserver(slider);
        slider.registerValueChangeObserver(this::updateSliderColor);

        return sliderContainer;
    }


    @Override
    public void onLoad() {
        super.onLoad();
        treeMap.firstTimeOpen();
        if (slider != null) {
            slider.firstOpening();
            updateSliderColor(0, ESASkyColors.maxIndex());
        }
        setMaxSize();
    }

    public void addTreeMapData(List<CommonTapDescriptor> descriptors, List<Integer> counts) {
        treeMap.addData(descriptors, counts);
    }

    public void clearTreeMapData() {
        treeMap.clearData();;
    }

    public void setTreeMapSliderValues(double x, double y) {
        if (slider != null) {
            slider.setSliderValue(x, y);
        }
    }

    public Double[] getTreeMapSliderValues() {
        return treeMap.getSliderValues();
    }

    @Override
    protected Element getMovableElement() {
        return header.getElement();
    }

    @Override
    protected Element getResizeElement() {
        return mainContainer.getElement();
    }

    @Override
    public void show() {
        super.show();
        setDefaultSize();
        updateTreeMapSize();
        updateDeckSize();
    }

    @Override
    public void hide() {
        super.hide();
        notifyClosed();
    }


    @Override
    public void setMaxSize() {
        Style elementStyle = mainContainer.getElement().getStyle();
        int maxWidth = MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15;
        int maxHeight = MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15;
        elementStyle.setPropertyPx("maxWidth", maxWidth);
        elementStyle.setPropertyPx("maxHeight", maxHeight);
        updateDeckSize();
        updateTreeMapSize();
    }

    private void setDefaultSize() {
        if(DeviceUtils.isMobile()) {
            mainContainer.getElement().getStyle().setWidth(EsaSkyConstants.DEFAULT_TREEMAP_WIDTH_MOBILE, Style.Unit.PX);
            mainContainer.getElement().getStyle().setHeight(EsaSkyConstants.DEFAULT_TREEMAP_HEIGHT_MOBILE, Style.Unit.PX);
        } else if(DeviceUtils.isTablet()){
            mainContainer.getElement().getStyle().setWidth(EsaSkyConstants.DEFAULT_TREEMAP_WIDTH_TABLET, Style.Unit.PX);
            mainContainer.getElement().getStyle().setHeight(EsaSkyConstants.DEFAULT_TREEMAP_HEIGHT_TABLET, Style.Unit.PX);
        } else {
            mainContainer.getElement().getStyle().setWidth(EsaSkyConstants.DEFAULT_TREEMAP_WIDTH_DESKTOP , Style.Unit.PX);
            mainContainer.getElement().getStyle().setHeight(EsaSkyConstants.DEFAULT_TREEMAP_HEIGHT_DESKTOP, Style.Unit.PX);
        }
    }

    private void updateTreeMapSize() {
        treeMap.setSize(mainContainer.getOffsetWidth(), mainContainer.getOffsetHeight() - header.getOffsetHeight() - 105);
        if (slider != null) {
            slider.updateSize(mainContainer.getOffsetWidth() - 30);
        }
    }


    private void updateDeckSize() {
        Style elementStyle = tabPanel.getElement().getStyle();
        int maxWidth = mainContainer.getOffsetWidth() - 4;
        int maxHeight = mainContainer.getOffsetHeight() - header.getOffsetHeight();
        if (maxHeight > 0 && maxWidth > 0) {
            elementStyle.setPropertyPx("maxWidth", maxWidth);
            elementStyle.setPropertyPx("maxHeight", maxHeight);
        }
    }

    public void updateSliderColor(double low, double high) {
        double botPosition = (1 - (low - Math.floor(low))) / (high - low);
        double topPosition = (1 - (Math.ceil(high) - high)) / (high - low);

        StringBuilder styleString = new StringBuilder("linear-gradient(to right,");
        int nShown = 0;

        for (int i = (int) Math.floor(low); i <= Math.ceil(high); i++) {
            styleString.append(ESASkyColors.getColor(i));
            if (nShown == 1) {
                styleString.append(" ").append(botPosition * 100).append("%");
            } else if (nShown == (int) Math.ceil(high) - (int) Math.floor(low) - 1) {
                styleString.append(" ").append(100 - topPosition * 100).append("%");
            }
            nShown++;
            styleString.append(",");
        }
        styleString = new StringBuilder(styleString.substring(0, styleString.length() - 1));

        styleString.append(")");
        slider.setSliderColor(styleString.toString());
    }

    @Override
    public void onResize() {
        setMaxSize();
    }


    public void registerObserver(TreeMapChanged observer){
        treemapObservers.add(observer);
    }

    private void notifyClosed(){
        for(TreeMapChanged observer : treemapObservers){
            observer.onClose();
        }
    }

    public void setFovLimiterEnabled(boolean enabled) {
        fovLimiterEnabled = enabled;
        registryPanel.setFovLimiterEnabled(fovLimiterEnabled);
        vizierPanel.setFovLimiterEnabled(fovLimiterEnabled);
        esaPanel.setFovLimiterEnabled(fovLimiterEnabled);
    }

    private boolean hasCustomDescriptor() {
        return DescriptorRepository.getInstance().getDescriptors(EsaSkyWebConstants.CATEGORY_EXTERNAL).stream().anyMatch(TapDescriptorBase::isCustom);
    }
}
