package esac.archive.esasky.cl.web.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import esac.archive.esasky.cl.web.client.event.exttap.ColumnSelectionEvent;
import esac.archive.esasky.cl.web.client.event.exttap.ColumnSelectionEventHandler;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.common.BaseMovablePopupPanel;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyRadioButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;
import esac.archive.esasky.ifcs.model.descriptor.TapMetadataDescriptor;

import java.util.List;

public class ColumnSelectorPopupPanel extends BaseMovablePopupPanel {
    private final Resources resources;
    private final CssResource style;

    FlowPanel raDecDropdownContainer;
    FlowPanel regionDropdownContainer;

    DropDownMenu<String> raDropdownMenu;
    DropDownMenu<String> decDropdownMenu;
    DropDownMenu<String> regionDropdownMenu;
    private final List<TapMetadataDescriptor> metadataDescriptorList;


    private boolean isRegionSelected;


    public interface Resources extends ClientBundle {
        @Source("columnSelectorPopupPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public ColumnSelectorPopupPanel(String headerText, String helpText, List<TapMetadataDescriptor> metadataDescriptorList) {
        super(GoogleAnalytics.CAT_GLOBALTAP_SELECTCOLUMNPANEL, headerText, helpText);
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.metadataDescriptorList = metadataDescriptorList;
        initView();
    }

    public void initView() {
        raDecDropdownContainer = new FlowPanel();

        FlowPanel raDropdownContainer = new FlowPanel();
        raDropdownContainer.setStyleName("columnSelectorPopupPanel__dropdownContainer");
        raDropdownMenu = createDropdownMenu(metadataDescriptorList, "RA");
        Label raLabel = new Label("RA:");
        raLabel.setStyleName("columnSelectorPopupPanel__label");
        raDropdownContainer.add(raLabel);
        raDropdownContainer.add(raDropdownMenu);

        FlowPanel decDropdownContainer = new FlowPanel();
        decDropdownContainer.setStyleName("columnSelectorPopupPanel__dropdownContainer");
        decDropdownMenu = createDropdownMenu(metadataDescriptorList, "Dec");
        Label decLabel = new Label("Dec:");
        decLabel.setStyleName("columnSelectorPopupPanel__label");
        decDropdownContainer.add(decLabel);
        decDropdownContainer.add(decDropdownMenu);

        raDecDropdownContainer.add(raDropdownContainer);
        raDecDropdownContainer.add(decDropdownContainer);


        regionDropdownContainer = new FlowPanel();
        regionDropdownContainer.setStyleName("columnSelectorPopupPanel__dropdownContainer");
        regionDropdownMenu = createDropdownMenu(metadataDescriptorList, "Region");
        Label dropDownregionLabel = new Label("Region:");
        dropDownregionLabel.setStyleName("columnSelectorPopupPanel__label");
        regionDropdownContainer.add(dropDownregionLabel);
        regionDropdownContainer.add(regionDropdownMenu);

        FlowPanel radioContainer = new FlowPanel();
        radioContainer.setStyleName("columnSelectorPopupPanel__radioGroupContainer");

        FlowPanel raDecRadioContainer = new FlowPanel();
        raDecRadioContainer.addStyleName("columnSelectorPopupPanel__radioContainer");
        EsaSkyRadioButton raDecRadio = new EsaSkyRadioButton("columnSelectGroup");
        raDecRadio.registerValueChangeObserver(isSelected -> {
            if (isSelected) {
                isRegionSelected = false;
                setRegionContainerVisibility(false);
                setRaDecContainerVisibility(true);
            }
        });

        Label raDecLabel = new Label("RA, Dec");
        raDecLabel.setStyleName("columnSelectorPopupPanel__label");
        raDecLabel.addClickHandler(event -> raDecRadio.setSelected(true));

        raDecRadioContainer.add(raDecLabel);
        raDecRadioContainer.add(raDecRadio);

        FlowPanel regionRadioContainer = new FlowPanel();
        regionRadioContainer.setStyleName("columnSelectorPopupPanel__radioContainer");
        EsaSkyRadioButton regionRadio = new EsaSkyRadioButton("columnSelectGroup");
        regionRadio.registerValueChangeObserver(isSelected -> {
            if (isSelected) {
                isRegionSelected = true;
                setRaDecContainerVisibility(false);
                setRegionContainerVisibility(true);
            }
        });

        Label regionLabel = new Label("STC-S Region");
        regionLabel.setStyleName("columnSelectorPopupPanel__label");
        regionLabel.addClickHandler(event -> regionRadio.setSelected(true));
        regionRadioContainer.add(regionLabel);
        regionRadioContainer.add(regionRadio);

        radioContainer.add(raDecRadioContainer);
        radioContainer.add(regionRadioContainer);
        raDecRadio.setSelected(true);


        FlowPanel buttonContainer = new FlowPanel();
        buttonContainer.setStyleName("columnSelectorPopupPanel__buttonContainer");
        EsaSkyStringButton cancelButton = new EsaSkyStringButton("Cancel");
        cancelButton.addStyleName("columnSelectorPopupPanel__button");
        cancelButton.setMediumStyle();
        cancelButton.addClickHandler(event -> {
            this.hide();
        });


        EsaSkyStringButton okButton = new EsaSkyStringButton("Ok");
        okButton.addStyleName("columnSelectorPopupPanel__button");
        okButton.setMediumStyle();
        okButton.addClickHandler(event -> {
            this.fireEvent(new ColumnSelectionEvent(isRegionSelected, raDropdownMenu.getSelectedObject(), decDropdownMenu.getSelectedObject(), regionDropdownMenu.getSelectedObject()));
            this.hide();
        });

        buttonContainer.add(cancelButton);
        buttonContainer.add(okButton);

        container.add(radioContainer);
        container.add(raDecDropdownContainer);
        container.add(regionDropdownContainer);
        container.add(buttonContainer);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        setDefaultSize();
    }


    private DropDownMenu<String> createDropdownMenu(List<TapMetadataDescriptor> metadataDescriptorList, String title) {
        DropDownMenu<String> dropdownMenu = new DropDownMenu<>("", title, 550, "columnSelectorPopupPanel__dropDown");
        for (TapMetadataDescriptor metadataDescriptor : metadataDescriptorList) {
            MenuItem<String> menuItem = new MenuItem<>(metadataDescriptor.getName(), metadataDescriptor.getName(), metadataDescriptor.getDescription(), true);
            menuItem.addStyleName("columnSelectorPopupPanel__dropDown");
            dropdownMenu.addMenuItem(menuItem);

            if (dropdownMenu.getSelectedObject() == null) {
                dropdownMenu.selectObject(metadataDescriptor.getName());
            }
        }

        return dropdownMenu;
    }


    private void setDefaultSize() {
        Style containerStyle = container.getElement().getStyle();
        containerStyle.setPropertyPx("minWidth", 550);
        containerStyle.setPropertyPx("minHeight", 150);
    }

    private void setRaDecContainerVisibility(boolean visible) {
        if (visible) {
            raDecDropdownContainer.removeStyleName("displayNone");
        } else {
            raDecDropdownContainer.addStyleName("displayNone");
        }
    }

    private void setRegionContainerVisibility(boolean visible) {
        if (visible) {
            regionDropdownContainer.removeStyleName("displayNone");
        } else {
            regionDropdownContainer.addStyleName("displayNone");
        }
    }


    public HandlerRegistration addColumnSelectionHandler(ColumnSelectionEventHandler handler) {
        return addHandler(handler, ColumnSelectionEvent.TYPE);
    }

}
