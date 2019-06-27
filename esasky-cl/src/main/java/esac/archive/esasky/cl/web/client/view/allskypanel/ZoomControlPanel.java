package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.VerticalPanel;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton;

public class ZoomControlPanel extends VerticalPanel {

  private SignButton zoomIn;
  private SignButton zoomOut;
  
  private Resources resources = GWT.create(Resources.class);
  private CssResource style;
  
  public static interface Resources extends ClientBundle {
      @Source("zoomControlPanel.css")
      @CssResource.NotStrict
      CssResource style();
  }

  public ZoomControlPanel() {
	  this.style = this.resources.style();
	  this.style.ensureInjected();
	  initView();
  }
  
  public HasClickHandlers getZoomInClickHandler() {
    return this.zoomIn;
  }
  
  public HasClickHandlers getZoomOutClickHandler() {
    return this.zoomOut;
  }

  private void initView(){
    setStyleName("allSkyZoomControl");
    
    this.zoomIn = new SignButton(SignButton.SignType.PLUS);
    this.zoomIn.setTitle(TextMgr.getInstance().getText("ZoomControlPanel_zoomIn"));
    this.zoomIn.addStyleName("zoomButton");
    this.zoomIn.getElement().setId("zoomInBtn");
    this.zoomIn.setOutline();
    this.zoomIn.setNonTransparentBackground();
    
    
    this.zoomOut = new SignButton(SignButton.SignType.MINUS);
    this.zoomOut.setTitle(TextMgr.getInstance().getText("ZoomControlPanel_zoomOut"));
    this.zoomOut.getElement().setId("zoomOutBtn");
    this.zoomOut.setOutline();
    this.zoomOut.setNonTransparentBackground();
    
    add(this.zoomIn);
    add(this.zoomOut);
  }
  
}
