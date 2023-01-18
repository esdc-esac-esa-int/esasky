package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.Size;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.ESASkyMultiRangeSlider;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.ExtTapTreeMap;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.TreeMapChanged;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.treemap.TreeMapContainer;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.LinkedList;
import java.util.List;

public class ExtTapPanel extends MovableResizablePanel<ExtTapPanel> {

    private PopupHeader<ExtTapPanel> header;
    FlowPanel mainContainer;
    TabPanel tabPanel;

    ExtTapTreeMap treeMap;
    GlobalTapPanel globalTapPanel;

    private int selectedTabIndex = 0;

    private ESASkyMultiRangeSlider slider;

    private final List<TreeMapChanged> observers = new LinkedList<>();

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
        mainContainer.getElement().setId("someId234");
        header = new PopupHeader<>(this, TextMgr.getInstance().getText("gwPanel_header"),
                TextMgr.getInstance().getText("gwPanel_helpText"),
                TextMgr.getInstance().getText("gwPanel_helpTitle"));



        tabPanel = new TabPanel();
        tabPanel.addSelectionHandler(event -> {
            selectedTabIndex = event.getSelectedItem();
            if (selectedTabIndex != 0) {
                header.setText(TextMgr.getInstance().getText("treeMap_" + EntityContext.EXT_TAP));
            }

            setDefaultSize();
        });

        tabPanel.addStyleName("extTapPanel__tabPanel");

        FlowPanel treeMapContainer = new FlowPanel();
        treeMap = new ExtTapTreeMap(EntityContext.EXT_TAP);
        treeMap.registerHeaderObserver(text -> {
            if (tabPanel.getTabBar().getSelectedTab() == 0) {
                header.setText(TextMgr.getInstance().getText("treeMap_" + EntityContext.EXT_TAP) + text);
            }
        });
        treeMapContainer.add(treeMap);
        FlowPanel sliderContainer = initSliderContainer();
        treeMapContainer.add(sliderContainer);

        tabPanel.add(treeMapContainer, "Treemap");

        globalTapPanel = new GlobalTapPanel();
        tabPanel.add(new GlobalTapPanel(), "TAP Registry");

        tabPanel.getDeckPanel().setStyleName("extTapPanel__deck");
        tabPanel.selectTab(0);

        mainContainer.add(header);
        mainContainer.add(tabPanel);
        this.add(mainContainer);
        this.addStyleName("extTapPanel");

        setMaxSize();

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


    public TreeMapContainer getTreeMapContainer() {
        return null;
    }

    public void addTreeMapData(List<CommonTapDescriptor> descriptors, List<Integer> counts) {
        treeMap.addData(descriptors, counts);
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
        setMaxHeight();

        updateTreeMapSize();
    }

    private void setMaxHeight() {
        int headerSize = 50;
        int height = mainContainer.getOffsetHeight() - headerSize - 5;

        if (height > MainLayoutPanel.getMainAreaHeight()) {
            height = MainLayoutPanel.getMainAreaHeight() - headerSize - 5;
        }

        tabPanel.getElement().getStyle().setPropertyPx("height", height);
    }


    private void setDefaultSize() {
        if (selectedTabIndex == 0) {
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
        } else {
            Size size = super.getDefaultSize();
            mainContainer.setWidth(size.width + "px");
            mainContainer.setHeight(size.height + "px");

            Style containerStyle = mainContainer.getElement().getStyle();
            containerStyle.setPropertyPx("minWidth", 350);
            containerStyle.setPropertyPx("minHeight", 300);
        }

    }

    private void updateTreeMapSize() {
        treeMap.setSize(mainContainer.getOffsetWidth(), mainContainer.getOffsetHeight() - header.getOffsetHeight() - 98);
        if (slider != null) {
            slider.updateSize(mainContainer.getOffsetWidth() - 30);
        }
    }


    private void updateDeckSize() {
        Style elementStyle = tabPanel.getDeckPanel().getElement().getStyle();
        int maxWidth = mainContainer.getOffsetWidth() - 4;
        int maxHeight = mainContainer.getOffsetHeight() - header.getOffsetHeight() - 34;
        if (maxHeight > 0 && maxWidth > 0) {

            tabPanel.getDeckPanel().setHeight(maxHeight + "px");
            tabPanel.getDeckPanel().setWidth(maxWidth + "px");
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
        observers.add(observer);
    }

    private void notifyClosed(){
        for(TreeMapChanged observer : observers){
            observer.onClose();
        }
    }
}
