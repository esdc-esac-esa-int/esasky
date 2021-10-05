package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.CheckBox;
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
import esac.archive.esasky.cl.web.client.view.common.EsaSkySliderObserver;
import esac.archive.esasky.cl.web.client.view.common.EsaSkySwitch;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TableObserver;
import esac.archive.esasky.ifcs.model.descriptor.BaseDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.GwDescriptorList;

public class OutreachImagePanel extends BasePopupPanel {

	public interface GwDescriptorListMapper extends ObjectMapper<GwDescriptorList> {}
	private BaseDescriptor gwDescriptor;
	private ImageListEntity imageEntity;
	private boolean isHidingFootprints = false;
	
	private FlowPanel opacityPanel;
	//TODO internationalization
//	private final CheckBox checkBox = new CheckBox(TextMgr.getInstance().getText("WelcomeDialog_checkbox"));
//	private final CheckBox checkBox = new CheckBox("Hide footprints");
	//TODO rename
	private final EsaSkySwitch checkBox = new EsaSkySwitch("outreachImagePanel__hideFootprintsSwitch", false, "Hide Footprints", "");
	
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
		if(gwDescriptor == null) {
			gwDescriptor = DescriptorRepository.getInstance().getImageDescriptors().getDescriptors().get(0);
			imageEntity = EntityRepository.getInstance().createImageListEntity(gwDescriptor);
			//TODO do not add one every time...
			outreachImagePanel.add(imageEntity.createTablePanel().getWidget());
			imageEntity.fetchData();
		}
		setMaxSize();
		
	}
	
	private void initView() {
		this.getElement().addClassName("outreachImagePanel");
		
//TODO create this for Outreach
		PopupHeader header = new PopupHeader(this, TextMgr.getInstance().getText("gwPanel_header"), 
				TextMgr.getInstance().getText("gwPanel_helpText"), 
				TextMgr.getInstance().getText("gwPanel_helpTitle"));

		outreachImagePanel.add(header);
		
		ESASkySlider opacitySlider = new ESASkySlider(0, 1.0, 250);
		//TODO style in correct css
		opacitySlider.addStyleName("hipsSlider");
		opacitySlider.registerValueChangeObserver(new EsaSkySliderObserver() {

			@Override
			public void onValueChange(double value) {
				imageEntity.setOpacity(value);
			}
		});
		
		Label opacityLabel = new Label();
		//TODO Internationalization
		opacityLabel.setText(TextMgr.getInstance().getText("targetlist_opacity"));
		//TODO style in correct css
		opacityLabel.setStyleName("opacityLabel");
        opacityPanel = new FlowPanel();
        opacityPanel.addStyleName("outreachImagePanel__opacityControl");
        opacityPanel.add(opacityLabel);
        opacityPanel.add(opacitySlider);
        opacityPanel.setVisible(false);
        MainLayoutPanel.addElementToMainArea(opacityPanel);
		
        checkBox.addStyleName("outreachImagePanel__checkBox");
        checkBox.addClickHandler(event ->
        {
        	isHidingFootprints = !isHidingFootprints;
        	checkBox.setChecked(isHidingFootprints);
    		imageEntity.setIsHidingShapes(isHidingFootprints);
		});
        outreachImagePanel.add(checkBox);
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
