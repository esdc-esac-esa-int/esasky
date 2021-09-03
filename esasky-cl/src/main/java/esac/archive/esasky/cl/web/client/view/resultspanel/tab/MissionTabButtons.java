package esac.archive.esasky.cl.web.client.view.resultspanel.tab;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.ToggleImage;
import esac.archive.esasky.cl.web.client.model.entities.ColorChangeObserver;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.utility.WavelengthUtils;
import esac.archive.esasky.cl.web.client.view.common.ESASkyJavaScriptLibrary;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.LabelWithHelpButton;

public class MissionTabButtons extends Composite {

    protected EsaSkyButton styleButton;
    protected CloseButton closeButton;
    protected FlowPanel compositePanel;
    protected final String esaSkyUniqId;
    protected ToggleImage toggleImage;
    private LabelWithHelpButton tabTitleLabel;
    private String canvasId;
    private IDescriptor descriptor;

    public MissionTabButtons(final String helpTitle, final String helpDescription, GeneralEntityInterface entity) {
        
        this.esaSkyUniqId = entity.getEsaSkyUniqId();
        this.compositePanel = new FlowPanel();
        
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
        if(entity.getDescriptor().getWavelengths() != null) {
            canvasId = esaSkyUniqId + "_wavelengthCanvas";
            FlowPanel wavelengthCanvas = new FlowPanel("canvas");
            wavelengthCanvas.getElement().setAttribute("height", "25");
            wavelengthCanvas.getElement().setAttribute("width", "30");
            wavelengthCanvas.getElement().setId(canvasId);
            wavelengthCanvas.addStyleName("missionTab__wavelengthCanvas");
            compositePanel.add(wavelengthCanvas);
            descriptor = entity.getDescriptor();
            compositePanel.setTitle(WavelengthUtils.getLongName(descriptor));
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
        
        this.toggleImage = new ToggleImage(new Image("images/" + entity.getDescriptor().getIcon() + ".png"), 
                new Image("images/" + entity.getDescriptor().getIcon() + "_toggled.png"));
        toggleImage.addStyleName("tabIcon");
        this.compositePanel.insert(toggleImage, 0);
        
        entity.registerColorChangeObserver(new ColorChangeObserver() {
			
			@Override
			public void onColorChange(String newColor) {
				setColor(newColor);
			}
		});
    }
    
    @Override
    protected void onLoad() {
        super.onLoad();
        plotWavelengthWave(true);
    }
    
    private void plotWavelengthWave(boolean isSelected) {
        if(canvasId != null) {
            double minWavelengthAllowed = WavelengthUtils.getMinWavelengthRange();
            double maxWavelengthAllowed = WavelengthUtils.getMaxWavelengthRange();
            double normalizedWavelength = (WavelengthUtils.getMeanWavelength(descriptor) - minWavelengthAllowed) 
                    / (maxWavelengthAllowed - minWavelengthAllowed); // Normalized range to 0-1
            double invertedMean = -1 * (normalizedWavelength - 1);
            
            String color = isSelected ? "black" : "white";
            ESASkyJavaScriptLibrary.plotSine(canvasId, 12, invertedMean * 3.5 + 1, color); // Accepted frequency is 1-4.5
        }
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
            if(toggleImage != null){
                toggleImage.setToggled();
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
            if(toggleImage != null){
                toggleImage.setDefault();
            }
        }
        plotWavelengthWave(selected);
    }

    private void setColor (String color) {
        if (styleButton != null) {
        	styleButton.setBackgroundColor(color);
        }
    }
}