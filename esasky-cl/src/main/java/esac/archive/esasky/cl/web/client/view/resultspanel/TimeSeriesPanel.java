package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.MovableResizablePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.HelpButton;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;

public class TimeSeriesPanel extends MovableResizablePanel<TimeSeriesPanel> {
    private final Resources resources = GWT.create(Resources.class);
    private CssResource style;

    public interface Resources extends ClientBundle {
        @Source("timeSeriesPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    private final CloseButton closeButton;
    private boolean imageHasLoaded = false;
    private Label headerLabel;
    private FlowPanel headerLabelAndHelpButton = new FlowPanel();
    private String[] dataInfo = new String[3];
    
    private FlowPanel contentAndCloseButton;
    private Element timevizElement; 
    private static TimeSeriesPanel timeSeriesPanel = null;
    
    public static TimeSeriesPanel openTimeSeriesData(String mission, String dataId, String productUrl) {
    	if(timeSeriesPanel == null || !timeSeriesPanel.isShowing()) {
    		timeSeriesPanel = new TimeSeriesPanel(mission, dataId, productUrl);
    	} else {
    		String[] dataInfo = {mission, dataId, productUrl};
    		timeSeriesPanel.addData(dataInfo);
    	}
    	return timeSeriesPanel;
    }
    
    public TimeSeriesPanel(String mission, String dataId, String productUrl) {
    	super(GoogleAnalytics.CAT_TIMESERIES, true);
        this.style = this.resources.style();
        this.style.ensureInjected();
        
        this.dataInfo[0] = mission;
        this.dataInfo[1] = dataId;
        this.dataInfo[2] = productUrl;
        setSnapping(false);
        closeButton = new CloseButton();
        closeButton.addStyleName("timeSeriesCloseButton");
        closeButton.addClickHandler(event -> hide());
        
        headerLabel = new Label("Time-Series Viewer");
        headerLabel.setStyleName("timeSeriesHeaderLabel");
        headerLabelAndHelpButton.addStyleName("timeSeriesHeader");
        headerLabelAndHelpButton.add(headerLabel);
        HelpButton helpButton = new HelpButton(TextMgr.getInstance().getText("timeViz_helpDescription"),
        		TextMgr.getInstance().getText("timeViz_title"));
        headerLabelAndHelpButton.add(helpButton);
        
        contentAndCloseButton = new FlowPanel();
        contentAndCloseButton.addStyleName("timeseriesContent");
        movableContainer.addStyleName("timeseriesContentContainer");
        contentAndCloseButton.add(headerLabelAndHelpButton);
        contentAndCloseButton.add(closeButton);
        add(contentAndCloseButton);
        getElement().setId("timeseriesPanel");
    	show();
    }
    
    @Override
    protected void onAttach() {
    	super.onAttach();
    	this.timevizElement = Document.get().createElement("timeviz-element");
    	contentAndCloseButton.getElement().appendChild(timevizElement);
    	Scheduler.get().scheduleFinally(() -> addDataToTimeViz(timevizElement, dataInfo));
    }
    
    public void addData(String [] dataInfo) {
    	addDataToTimeViz(timevizElement, dataInfo);
    }
    
    private native void addDataToTimeViz(Element timevizElement, String[] id) /*-{
		timevizElement.addData = id;
	}-*/;
    
	@Override
	public void show() {
		super.show();
		MainLayoutPanel.addElementToMainArea(this);
	}
	
	@Override
	public void hide() {
		super.hide();
		MainLayoutPanel.removeElementFromMainArea(this);
	}

    @Override
    protected Element getMovableElement() {
        return headerLabelAndHelpButton.getElement();
    }

    @Override
    protected Element getResizeElement() {
        return this.getElement();
    }

}
