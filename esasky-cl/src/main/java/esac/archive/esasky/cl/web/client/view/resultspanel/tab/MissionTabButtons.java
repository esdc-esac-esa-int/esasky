package esac.archive.esasky.cl.web.client.view.resultspanel.tab;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.descriptor.ColorChangeObserver;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.SelectableImage;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.LabelWithHelpButton;

public class MissionTabButtons extends Composite {

    protected EsaSkyButton styleButton;
    protected CloseButton closeButton;
    protected HorizontalPanel compositePanel;
    protected final String esaSkyUniqId;
    protected SelectableImage selectableImage;
    private LabelWithHelpButton tabTitleLabel;

    public MissionTabButtons(final String helpTitle, final String helpDescription, GeneralEntityInterface entity) {
        
        this.esaSkyUniqId = entity.getEsaSkyUniqId();
        this.compositePanel = new HorizontalPanel();
        
        this.tabTitleLabel = new LabelWithHelpButton(entity.getTabLabel(), helpDescription, helpTitle, "labelWithHelpButtonLabelNoFontSize");
        this.compositePanel.add(this.tabTitleLabel);

        
        if (entity.isCustomizable()) {
            styleButton = new EsaSkyButton("#000000", true);
            styleButton.setSmallStyle();
            styleButton.setRoundStyle();
            styleButton.addStyleName("styleButton");
            compositePanel.add(styleButton);
            setColor(entity.getColor());
        } 
        if(entity.getTypeLogo() != null) {
        	Image logo = entity.getTypeLogo();
        	logo.addStyleName("tabLogo");
        	compositePanel.add(logo);
        }
        
        this.closeButton = new CloseButton();
        this.closeButton.setTitle(TextMgr.getInstance().getText("closeableTabLayoutPanel_closeTab"));
        closeButton.addStyleName("closeTabButton");
        compositePanel.add(closeButton);
        
        initWidget(this.compositePanel);
        
        this.selectableImage = entity.getTypeIcon();
        selectableImage.addStyleName("tabIcon");
        this.compositePanel.insert(selectableImage, 0);
        
        entity.getDescriptor().registerColorChangeObservers(new ColorChangeObserver() {
			
			@Override
			public void onColorChange(IDescriptor descriptor, String newColor) {
				setColor(newColor);
			}
		});
    }
    
    public final String getId() {
        return esaSkyUniqId;
    }
    
    public String getTitle() {
    	return tabTitleLabel.getText();
    }
    
    public void setCloseClickHandler(ClickHandler handler) {
        this.closeButton.addClickHandler(handler);
    }

    public void setStyleClickHandler(ClickHandler handler) {
        if (this.styleButton != null) {
            this.styleButton.addClickHandler(handler);
        }
    }
    
    public void updateStyle(final boolean selected) {
        if (selected) {
            tabTitleLabel.addStyleName("darkLabel");
            tabTitleLabel.removeStyleName("whiteLabel");
            tabTitleLabel.setButtonDarkIconAndStyle();
            if (styleButton != null) {
            	styleButton.setDarkStyle();
            }
            closeButton.setDarkIcon();
            closeButton.setDarkStyle();
            if(selectableImage != null){
                selectableImage.setSelected();
            }
        } else {
            tabTitleLabel.addStyleName("whiteLabel");
            tabTitleLabel.removeStyleName("darkLabel");
            tabTitleLabel.setButtonLightIconAndStyle();
            if (styleButton != null) {
            	styleButton.setLightStyle();
            }
            closeButton.setLightIcon();
            closeButton.setLightStyle();
            if(selectableImage != null){
                selectableImage.setDefault();
            }
        }
    }

    private void setColor (String color) {
        if (styleButton != null) {
        	styleButton.setBackgroundColor(color);
        }
    }
}