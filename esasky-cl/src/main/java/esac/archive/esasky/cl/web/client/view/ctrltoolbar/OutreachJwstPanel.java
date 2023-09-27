package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.callback.ICommand;
import esac.archive.esasky.cl.web.client.event.ImageListSelectedEvent;
import esac.archive.esasky.cl.web.client.event.OpenSeaDragonActiveEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.Size;
import esac.archive.esasky.cl.web.client.model.entities.ImageListEntity;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.ESASkySlider;
import esac.archive.esasky.cl.web.client.view.common.EsaSkySwitch;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TableObserver;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.Objects;

public class OutreachJwstPanel extends MovableResizablePanel<OutreachJwstPanel> {

    private CommonTapDescriptor outreachJwstDescriptor;
    private ImageListEntity imageEntity;
    private boolean isHidingFootprints = false;
    private static String outreachImageIdToBeOpened;
    private static boolean defaultHideFootprints = false;
    private static boolean startupMinimized = false;
    private static String outreachImageNameToBeOpened;
    private FlowPanel opacityPanel;
    private final EsaSkySwitch hideFootprintsSwitch = new EsaSkySwitch("outreachJwstPanel__hideFootprintsSwitch", false, TextMgr.getInstance().getText("outreachImage_hideFootprints"), "");
    private final FlowPanel mainContainer = new FlowPanel();
    private final FlowPanel tableContainer = new FlowPanel();
    private PopupHeader<OutreachJwstPanel> header;

    private final OutreachJwstPanel.Resources resources;
    private CssResource style;

    public static interface Resources extends ClientBundle {
        @Source("outreachJwstPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public OutreachJwstPanel() {
        super(GoogleAnalytics.CAT_JWSTOUTREACHIMAGES, false);
        this.resources = GWT.create(OutreachJwstPanel.Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();

        initView();
        setMaxSize();
        CommonEventBus.getEventBus().addHandler(OpenSeaDragonActiveEvent.TYPE, event -> opacityPanel.setVisible(event.isActive() && super.isShowing()));
        CommonEventBus.getEventBus().addHandler(ImageListSelectedEvent.TYPE, (entity -> {
            if (Objects.equals(entity.getSelectedEntity(), imageEntity)) {
                if (!isShowing()) {
                    show();
                }
            } else if (isShowing()) {
                hide();
            }
        }));

        MainLayoutPanel.addMainAreaResizeHandler(event -> setDefaultSize());
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if(outreachImageIdToBeOpened != null || outreachImageNameToBeOpened != null) {
            show();
        }
        this.addSingleElementAbleToInitiateMoveOperation(header.getElement());
    }

    @Override
    protected void onResize() {
        setMaxHeight();
    }

    @Override
    protected Element getResizeElement() {
        return mainContainer.getElement();
    }

    private void setDefaultSize() {
        Size size = getDefaultSize();
        mainContainer.setWidth(size.width + "px");
        mainContainer.setHeight(size.height + "px");

        Style containerStyle = mainContainer.getElement().getStyle();
        containerStyle.setPropertyPx("minWidth", 150);
        containerStyle.setPropertyPx("minHeight", 100);
    }

    @Override
    protected Element getMovableElement() {
        return header.getElement();
    }

    private void getData() {
        if(outreachJwstDescriptor == null) {
            if(DescriptorRepository.getInstance().hasDescriptors(EsaSkyWebConstants.CATEGORY_IMAGES)) {
                fetchData();
            } else {
                DescriptorRepository.getInstance()
                        .registerDescriptorLoadedObserver(EsaSkyWebConstants.CATEGORY_IMAGES, this::fetchData);
            }
        }
    }

    private void fetchData() {
        if(outreachJwstDescriptor != null) {
            return;
        }

        outreachJwstDescriptor = DescriptorRepository.getInstance().getFirstDescriptor(EsaSkyWebConstants.CATEGORY_IMAGES, EsaSkyConstants.JWST_MISSION);

        if (outreachJwstDescriptor == null) {
            return;
        }

        imageEntity = EntityRepository.getInstance().createImageListEntity(outreachJwstDescriptor);
        if(outreachImageIdToBeOpened != null) {
            imageEntity.setIdToBeOpened(outreachImageIdToBeOpened, startupMinimized);
        } else if (outreachImageNameToBeOpened != null) {
            imageEntity.setNameToBeOpened(outreachImageNameToBeOpened);
        }
        tableContainer.add(imageEntity.createTablePanel().getWidget());
        imageEntity.fetchData();
        setMaxSize();
        hideFootprints(defaultHideFootprints);
    }
    public void show() {
        super.show();
        getData();
        if(imageEntity != null) {
            imageEntity.setIsPanelClosed(false);
        }
    }


    public void close() {
        if(imageEntity != null) {
            imageEntity.setIsPanelClosed(true);
        }
        super.hide();
    }

    private void initView() {
        this.getElement().addClassName("outreachJwstPanel");

        header = new PopupHeader<>(this, TextMgr.getInstance().getText("outreachJwstPanel_header"),
                TextMgr.getInstance().getText("outreachJwstPanel_helpText"),
                TextMgr.getInstance().getText("outreachJwstPanel_helpTitle"),
                event -> close(), TextMgr.getInstance().getText("outreachJwstPanel_closePanel"));




        ESASkySlider opacitySlider = new ESASkySlider(0, 1.0, 250);
        opacitySlider.registerValueChangeObserver(value -> imageEntity.setOpacity(value));

        Label opacityLabel = new Label();
        opacityLabel.setText(TextMgr.getInstance().getText("targetlist_opacity"));
        opacityLabel.setStyleName("outreachJwstPanel__opacityLabel");
        opacityPanel = new FlowPanel();
        opacityPanel.addStyleName("outreachJwstPanel__opacityControl");
        opacityPanel.add(opacityLabel);
        opacityPanel.add(opacitySlider);
        opacityPanel.setVisible(false);
        MainLayoutPanel.addElementToMainArea(opacityPanel);

        hideFootprintsSwitch.addStyleName("outreachJwstPanel__footprintSwitch");
        hideFootprintsSwitch.addClickHandler(event -> hideFootprints(!isHidingFootprints));
        header.addActionWidget(hideFootprintsSwitch);
        mainContainer.add(header);
        mainContainer.add(tableContainer);
        mainContainer.getElement().setId("outreachJwstPanelContainer");
        this.add(mainContainer);
    }

    @Override
    public void setMaxSize() {
        Style elementStyle = mainContainer.getElement().getStyle();
        int maxWidth = MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15;
        elementStyle.setPropertyPx("maxWidth", maxWidth);
        elementStyle.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15);
        setMaxHeight();
    }

    private void setMaxHeight() {
        int headerSize = header.getOffsetHeight();
        int height = mainContainer.getOffsetHeight() - headerSize - 5;

        if (height > MainLayoutPanel.getMainAreaHeight()) {
            height = MainLayoutPanel.getMainAreaHeight() - headerSize - 5;
        }

        tableContainer.getElement().getStyle().setPropertyPx("height", height);
    }

    public static void setStartupId(String id) {
        outreachImageIdToBeOpened = id;
    }

    public static void setStartupId(String id, boolean hideFootprints) {
        outreachImageIdToBeOpened = id;
        defaultHideFootprints = hideFootprints;
    }

    public static void setStartupId(String id, boolean hideFootprints, boolean minimized) {
        outreachImageIdToBeOpened = id;
        defaultHideFootprints = hideFootprints;
        startupMinimized = minimized;
    }

    public static void setStartupName(String name) {
        outreachImageNameToBeOpened = name;
    }


    public JSONArray getAllImageIds(ICommand command) {
        return getAllImageAttribute(command, true);
    }

    public JSONArray getAllImageNames(ICommand command) {
        return getAllImageAttribute(command, false);
    }

    public JSONArray getAllImageAttribute(ICommand command, boolean id) {
        if (imageEntity == null) {
            getData();
            imageEntity.getTablePanel().registerObserver(new TableObserver() {
                @Override
                public void numberOfShownRowsChanged(int numberOfShownRows) {
                    // Not needed here
                }

                @Override
                public void onSelection(ITablePanel selectedTablePanel) {
                    // Not needed here
                }

                @Override
                public void onUpdateStyle(ITablePanel panel) {
                    // Not needed here
                }

                @Override
                public void onDataLoaded(int numberOfRows) {
                    if (numberOfRows > 0) {
                        command.onResult(id ? imageEntity.getIds() : imageEntity.getNames());
                        imageEntity.setIsPanelClosed(true);
                        imageEntity.getTablePanel().unregisterObserver(this);
                    }
                }

                @Override
                public void onRowSelected(GeneralJavaScriptObject row) {
                    // Not needed here
                }

                @Override
                public void onRowDeselected(GeneralJavaScriptObject row) {
                    // Not needed here
                }
            });

        } else {
            return id ? imageEntity.getIds() : imageEntity.getNames();
        }

        return null;
    }

    public void selectShapeByName(String name) {
        if (imageEntity != null) {
            imageEntity.selectShape(imageEntity.getIdFromName(name));
        } else {
            OutreachJwstPanel.setStartupName(name);
        }

        if (!isShowing()) {
            show();
        }
    }
    public void selectShape(String id) {
        if(imageEntity != null) {
            imageEntity.selectShape(id);
        } else {
            OutreachJwstPanel.setStartupId(id);
        }

        if (!isShowing()) {
            show();
        }
    }

    public void selectShapeMinimized(String id) {
        if (imageEntity != null) {
            imageEntity.showImage(id);
        } else {
            OutreachJwstPanel.setStartupId(id, true, true);
            getData();
        }
    }

    private void hideFootprints(boolean hide) {
        if (hide != isHidingFootprints) {
            isHidingFootprints = hide;
            hideFootprintsSwitch.setChecked(hide);
            imageEntity.setIsHidingShapes(hide);
        }
    }
}
