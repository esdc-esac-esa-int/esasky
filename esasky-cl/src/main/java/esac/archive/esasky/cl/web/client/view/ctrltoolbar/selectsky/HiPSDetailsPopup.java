package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;

public class HiPSDetailsPopup extends PopupPanel {
	
    private HiPS hips;
    private final String CSS_ID = "hipsDbox";
    
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;

    public static interface Resources extends ClientBundle {
        @Source("hipsDetailsPopup.css")
        @CssResource.NotStrict
        CssResource style();
    }
    
    public HiPSDetailsPopup(HiPS hips) {
        this.style = this.resources.style();
        this.style.ensureInjected();
        
        this.hips = hips;

        initView();
    }

    private void initView() {
        this.getElement().setId(CSS_ID);
        this.setAutoHideEnabled(true);

        CloseButton closeBtn = new CloseButton();
        closeBtn.setTitle(TextMgr.getInstance().getText("hiPSDetailsPopup_close"));
        closeBtn.addStyleName("closeHipsDetails");
        closeBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                HiPSDetailsPopup.this.hide();
            }
        });

        VerticalPanel hipsDetailsPanel = new VerticalPanel();
        hipsDetailsPanel.setWidth("100%");

        hipsDetailsPanel.add(closeBtn);

        hipsDetailsPanel.add(new HTML("<b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_mapName") + "</b>"));
        hipsDetailsPanel.add(new HTML(hips.getSurveyName()));
        hipsDetailsPanel.add(new HTML("<br/>"));

        if (hips.getMissionURL() != null && !"".equals(hips.getMissionURL())) {
            hipsDetailsPanel.add(new HTML("<b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_mission") + "</b>"));
            hipsDetailsPanel.add(new HTML("<b><a target='_blank' href='" + hips.getMissionURL() + "'>"
                    + hips.getMission() + "</a></b>"));
            hipsDetailsPanel.add(new HTML("<br/>"));
        }

        if (hips.getInstrument() != null && !"".equals(hips.getInstrument())) {
            hipsDetailsPanel.add(new HTML("<b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_instrument") + "</b>"));
            hipsDetailsPanel.add(new HTML(hips.getInstrument()));
            hipsDetailsPanel.add(new HTML("<br/>"));
        }

        if (hips.getWavelengthRange() != null && !"".equals(hips.getWavelengthRange())) {
            hipsDetailsPanel.add(new HTML("<b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_wavFreq") + "</b>"));
            hipsDetailsPanel.add(new HTML(hips.getWavelengthRange() + "<br/><br/>"));
        }

        if (hips.getCreatorURL() != null && !"".equals(hips.getCreatorURL())) {
            hipsDetailsPanel.add(new HTML("<b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_mapCreatedBy") + "</b>"));
            if (hips.getCreationDate() != null && !"".equals(hips.getCreationDate())) {
                hipsDetailsPanel.add(new HTML("<b><a target='_blank' href='" + hips.getCreatorURL() + "'>"
                        + hips.getCreator() + "</a></b> on " + hips.getCreationDate()));
            } else {
                hipsDetailsPanel.add(new HTML("<b><a target='_blank' href='" + hips.getCreatorURL() + "'>"
                        + hips.getCreator() + "</a></b>"));
            }
            hipsDetailsPanel.add(new HTML("<br/>"));
        }

        if (hips.getMoreInfoURL() != null && !"".equals(hips.getMoreInfoURL())) {
            hipsDetailsPanel.add(new HTML("<b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_forMoreInformation") + "</b><a target='_blank' href='"
                    + hips.getMoreInfoURL() + "'><b>" + TextMgr.getInstance().getText("hiPSDetailsPopup_here") + "</b></a>"));
            hipsDetailsPanel.add(new HTML("<br/>"));
        }

        this.add(hipsDetailsPanel);
        
        MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				setMaxHeight();
			}
		});
        getContainerElement().getStyle().setOverflow(Overflow.AUTO);
        
        setWidth(170 + "px");
    }
    
    private void setMaxHeight() {
    	getContainerElement().getStyle().setProperty("maxHeight", MainLayoutPanel.getMainAreaHeight() +MainLayoutPanel.getMainAreaAbsoluteTop() - getContainerElement().getAbsoluteTop() - 10 + "px");
    }
    
    @Override
    public void show(){
    	super.show();
    	setMaxHeight();
    }
    
    @Override
    public void setPopupPosition(int left, int top) {
    	super.setPopupPosition(left, top);
    	setMaxHeight();
    }

}
