/*
ESASky
Copyright (C) 2025 Henrik Norman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    private String[] initialDataInfo = new String[3];
    
    private FlowPanel contentAndCloseButton;
    private Element timevizElement; 
    private static TimeSeriesPanel mainTimeSeriesPanel = null;
    //CHEOPS data is normalized, and this cannot be combined with any other mission
    private static TimeSeriesPanel cheopsTimeSeriesPanel = null;
    private final Set<String> currentData = new HashSet<>();

    private static TimeSeriesPanel getTimeSeriesPanel(String mission) {
    	if("CHEOPS".equals(mission)) {
    		if(cheopsTimeSeriesPanel == null || !cheopsTimeSeriesPanel.isShowing()) {
    			cheopsTimeSeriesPanel = new TimeSeriesPanel(true);
    		}
    		return cheopsTimeSeriesPanel;
    	} else {
    		if(mainTimeSeriesPanel == null || !mainTimeSeriesPanel.isShowing()) {
    			mainTimeSeriesPanel = new TimeSeriesPanel(false);
    		}
    		return mainTimeSeriesPanel;
    	}
    }
    
    public static TimeSeriesPanel getTimeSeriesPanelOrNull(String mission) {
    	if("CHEOPS".equals(mission)) {
    		return cheopsTimeSeriesPanel;
    	} else {
    		return mainTimeSeriesPanel;
    	}
    }
    
    public static TimeSeriesPanel toggleTimeSeriesData(String mission, String dataId, String secondIdentifier) {
    	TimeSeriesPanel timeSeriesPanel = getTimeSeriesPanel(mission);
        String[] dataInfo = {mission, dataId, secondIdentifier};
        if (timeSeriesPanel.currentData.contains(String.join(",", dataInfo))) {
        	timeSeriesPanel.currentData.remove(String.join(",", dataInfo));
        	timeSeriesPanel.removeData(dataInfo);
            if (timeSeriesPanel.currentData.isEmpty()) {
            	timeSeriesPanel.hide();
            }
        } else {
        	timeSeriesPanel.currentData.add(String.join(",", dataInfo));
        	timeSeriesPanel.addData(dataInfo);
        }
        return timeSeriesPanel;
    }

    public static boolean dataIsVisible(String mission, String dataId, String productUrl) {
    	TimeSeriesPanel timeSeriesPanel = getTimeSeriesPanelOrNull(mission);
        if(timeSeriesPanel == null || !timeSeriesPanel.isShowing()) {
            return false;
        }
        String[] dataInfo = {mission, dataId, productUrl};
        return timeSeriesPanel.currentData.contains(String.join(",", dataInfo));
    }
    
    public TimeSeriesPanel(boolean isCheops) {
    	super(GoogleAnalytics.CAT_TIMESERIES, true);
        this.style = this.resources.style();
        this.style.ensureInjected();
        
        setSnapping(false);
        closeButton = new CloseButton();
        closeButton.addStyleName("timeSeriesCloseButton");
        closeButton.addClickHandler(event -> hide());
        String cheopsPrefix = isCheops ? "CHEOPS " : "";
        headerLabel = new Label(cheopsPrefix + "Time-Series Viewer");
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
    	Scheduler.get().scheduleFinally(() -> addDataToTimeViz(timevizElement, initialDataInfo));
    }
    
    public void addData(String [] dataInfo) {
    	if(isAttached()) {
    		addDataToTimeViz(timevizElement, dataInfo);
    	} else {
    	    this.initialDataInfo = dataInfo;
    	}
    }

    public Set<String[]> getCurrentData() {
        return currentData.stream().map(s -> s.split(",")).collect(Collectors.toSet());
    }

    public void removeData(String [] dataInfo) {
        removeDataFromTimeViz(timevizElement, dataInfo);
    }
    
    private native void addDataToTimeViz(Element timevizElement, String[] id) /*-{
		timevizElement.addData = id;
	}-*/;

    private native void removeDataFromTimeViz(Element timevizElement, String[] id) /*-{
        timevizElement.removeData = id;
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
