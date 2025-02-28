/*
ESASky
Copyright (C) 2025 European Space Agency

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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.DataPanelAnimationCompleteEvent;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.animation.EsaSkyAnimation;
import esac.archive.esasky.cl.web.client.view.animation.HeightAnimation;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.CloseableTabLayoutPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.MissionTabButtons;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ResultsPanel extends Composite implements ResultsPresenter.View {

	/** CloseableTabLayoutPanel . */
	private final static CloseableTabLayoutPanel tabPanel = new CloseableTabLayoutPanel(40, Unit.PX, true);
	/** Results layout panel. */
	private static final LayoutPanel resultsLP = new LayoutPanel();
	
	private static EsaSkyAnimation panelAnimation = new HeightAnimation(resultsLP.getElement());
	
	private static boolean shouldBeHidden = false;

	private Resources resources = GWT.create(Resources.class);
	private CssResource style;

	public static interface Resources extends ClientBundle {

		@Source("resultsPanel.css")
		@CssResource.NotStrict
		CssResource style();
	}

	public ResultsPanel() {
		this.style = this.resources.style();
		this.style.ensureInjected();
		
		initView();
	}

	private void initView() {
		Log.debug("[ResultsPanel] Initializing ResulsPanel...");

		// Wrap ResultPanel into a LayoutPanel
		DockLayoutPanel contentPanel = new DockLayoutPanel(Unit.PX);
		
		contentPanel.add(tabPanel);

		resultsLP.add(contentPanel);
		resultsLP.getElement().setId("resultPanel");

		panelAnimation.addObserver(currentPosition -> {
            if(!GUISessionStatus.isDataPanelOpen()){
                CommonEventBus.getEventBus().fireEvent(new DataPanelAnimationCompleteEvent());
            }
        });
		initWidget(resultsLP);
		MainLayoutPanel.addMainAreaResizeHandler(event -> ensureDataPanelCanFit());
		ensureDataPanelCanFit();
	}
	
	private void ensureDataPanelCanFit() {
		if(MainLayoutPanel.getMainAreaHeight() <= 0) {
			return;
		}
		
		if(GUISessionStatus.isDataPanelOpen()){
			panelAnimation.animateTo(GUISessionStatus.getCurrentHeightForExpandedDataPanel(), 1000);
		}
	}

	@Override
	public final CloseableTabLayoutPanel getTabPanel() {
		return tabPanel;
	}

	@Override
    public final ITablePanel addResultsTab(final GeneralEntityInterface entity, final String helpTitle, final String helpDescription) {
		ITablePanel tablePanel = entity.createTablePanel();
		Log.debug("[ResultsPanel/addResultsTab()] " + tablePanel.getClass().getCanonicalName());
		tabPanel.addTab(tablePanel, helpTitle, helpDescription);
		return tablePanel;
    }

	@Override
    public final void removeTab(final String id) {
		MissionTabButtons tab = tabPanel.getTabWidgetIds().inverse().get(id);
	    if (tab != null) {
	        tabPanel.removeTab(tab);
	    }
    }

	@Override
	public void removeAllTabs() {
		Object[] tabs = tabPanel.getTabWidgetIds().inverse().values().toArray();
		for (Object tab : tabs) {
			tabPanel.removeTab((MissionTabButtons) tab);
		}
	}

	@Override
	public final Widget getTabFromTableId(final String id) {
		return tabPanel.getTablePanelFromId(id).getWidget();
	}
	
	public static final void toggleOpenCloseDataPanel(){
		if (GUISessionStatus.isDataPanelOpen()) {
			closeDataPanel();
		} else {
			openDataPanel();
		}
	}

	public static final void openDataPanel() {
		if(!shouldBeHidden) {
			Log.debug("[ResultsPanel/openDataPanelButton()] ResultPanel IS NOT visible - diplaying it");
	
			panelAnimation.animateTo(GUISessionStatus.getCurrentHeightForExpandedDataPanel(), 1000);
			GUISessionStatus.setDataPanelOpen(true);
			tabPanel.notifyDataPanelToggled();
		}
	}

	public static final void closeDataPanel() {
		Log.debug("ResultPanel IS visible - hiding it");
		panelAnimation.animateTo(40, 1000);
		
		GUISessionStatus.setDataPanelOpen(false);
		tabPanel.notifyDataPanelToggled();
	}
	
	public boolean isOpen(){
		return GUISessionStatus.isDataPanelOpen();
	}
	
	public static void shouldBeHidden(boolean input) {
		shouldBeHidden = input;
	}

	
}