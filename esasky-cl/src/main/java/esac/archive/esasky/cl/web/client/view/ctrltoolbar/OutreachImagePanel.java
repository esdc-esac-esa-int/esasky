package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.OpenSeaDragonActiveEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.ImageListEntity;
import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.ESASkySlider;
import esac.archive.esasky.cl.web.client.view.common.EsaSkySwitch;
import esac.archive.esasky.ifcs.model.descriptor.BaseDescriptor;

public class OutreachImagePanel extends BasePopupPanel {

	private BaseDescriptor outreachImageDescriptor;
	private ImageListEntity imageEntity;
	private boolean isHidingFootprints = false;
	
	private FlowPanel opacityPanel;
	private final EsaSkySwitch hideFootprintsSwitch = new EsaSkySwitch("outreachImagePanel__hideFootprintsSwitch", false, TextMgr.getInstance().getText("outreachImage_hideFootprints"), "");
	
	private final Resources resources;
	private CssResource style;


	private FlowPanel outreachImagePanel = new FlowPanel();

	public static interface Resources extends ClientBundle {
		@Source("outreachImagePanel.css")
		@CssResource.NotStrict
		CssResource style();
	}
	
	public OutreachImagePanel() {
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();
		
		initView();
		setMaxSize();
		CommonEventBus.getEventBus().addHandler(OpenSeaDragonActiveEvent.TYPE, event -> opacityPanel.setVisible(event.isActive()));
	}
	
	@Override
	public void show() {
		super.show();
		//TODO what if descriptor is not ready?
		if(outreachImageDescriptor == null) {
			outreachImageDescriptor = DescriptorRepository.getInstance().getImageDescriptors().getDescriptors().get(0);
			imageEntity = EntityRepository.getInstance().createImageListEntity(outreachImageDescriptor);
			outreachImagePanel.add(imageEntity.createTablePanel().getWidget());
			imageEntity.fetchData();
		}
		setMaxSize();
		
	}
	
	private void initView() {
		this.getElement().addClassName("outreachImagePanel");
		
		PopupHeader header = new PopupHeader(this, TextMgr.getInstance().getText("outreachImagePanel_header"), 
				TextMgr.getInstance().getText("outreachImagePanel_helpText"), 
				TextMgr.getInstance().getText("outreachImagePanel_helpTitle"));

		outreachImagePanel.add(header);
		
		ESASkySlider opacitySlider = new ESASkySlider(0, 1.0, 250);
		opacitySlider.registerValueChangeObserver(value -> imageEntity.setOpacity(value));
		
		Label opacityLabel = new Label();
		opacityLabel.setText(TextMgr.getInstance().getText("targetlist_opacity"));
		opacityLabel.setStyleName("outreachImagePanel__opacityLabel");
        opacityPanel = new FlowPanel();
        opacityPanel.addStyleName("outreachImagePanel__opacityControl");
        opacityPanel.add(opacityLabel);
        opacityPanel.add(opacitySlider);
        opacityPanel.setVisible(false);
        MainLayoutPanel.addElementToMainArea(opacityPanel);
		
        hideFootprintsSwitch.addStyleName("outreachImagePanel__footprintSwitch");
        hideFootprintsSwitch.addClickHandler(event ->
        {
        	isHidingFootprints = !isHidingFootprints;
        	hideFootprintsSwitch.setChecked(isHidingFootprints);
    		imageEntity.setIsHidingShapes(isHidingFootprints);
		});
        outreachImagePanel.add(hideFootprintsSwitch);
		this.add(outreachImagePanel);
	}
	
	@Override
	protected void setMaxSize() {
		super.setMaxSize();
	    int height = MainLayoutPanel.getMainAreaHeight();
	    if(height > 600) {
	    	height = 600;
	    }
		if (height > MainLayoutPanel.getMainAreaHeight() - 30 - 2) {
			height = MainLayoutPanel.getMainAreaHeight() - 30 - 2;
		}
		if(imageEntity != null && imageEntity.getTablePanel() != null) {
			imageEntity.getTablePanel().setMaxHeight(height);
		}
	}

}
