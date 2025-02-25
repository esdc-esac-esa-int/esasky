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

package esac.archive.esasky.cl.web.client.api;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ResultsPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.TableObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.MissionTabButtons;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class ApiPanel extends ApiBase{
	
	
	public ApiPanel(Controller controller) {
		this.controller = controller;
	}
	
	
	public void getResultPanelData(final JavaScriptObject widget) {
		GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_GETRESULTPANELDATA);
		final ITablePanel tablePanel = controller.getRootPresenter().getResultsPresenter().getTabPanel().getSelectedWidget();
		if(tablePanel == null) {
			sendBackErrorMsgToWidget(ApiConstants.PANEL_DATA_ERROR, widget);
			return;
		}
		JSONObject callback = tablePanel.exportAsJSON();

		
		if(callback.size() == 0) {
			tablePanel.registerObserver( new TableObserver() {

				@Override
				public void numberOfShownRowsChanged(int numberOfShownRows) {
					JSONObject callback = tablePanel.exportAsJSON();
					if(numberOfShownRows > 0 && callback.size() > 0) {
						sendBackValuesToWidget(callback, widget);
						tablePanel.unregisterObserver(this);
					}
				}

                @Override
                public void onSelection(ITablePanel selectedTablePanel) {
                	//Don't need this here
                }

                @Override
                public void onUpdateStyle(ITablePanel panel) {
                	//Don't need this here
                }

				@Override
				public void onDataLoaded(int numberOfRows) {
					//Don't need this here
				}

				@Override
				public void onRowSelected(GeneralJavaScriptObject row) {
					// Not needed here
				}

				@Override
				public void onRowDeselected(GeneralJavaScriptObject row) {
					// Not needed here
				}
			});
		}else {
			sendBackValuesToWidget(callback, widget);
		}
	}
	
	public void hideResultPanel() {
		ResultsPanel.closeDataPanel();
	}
	
	public void showResultPanel() {
		ResultsPanel.openDataPanel();
	}
	
	public void setDataPanelHidden(boolean input) {
		ResultsPanel.shouldBeHidden(input);
	}
	
	public void closeResultPanelTab(int index) {
		final ITablePanel tablePanel;
		try {
			if(index == -1) {
				tablePanel = controller.getRootPresenter().getResultsPresenter().getTabPanel().getSelectedWidget();
			}else {
				tablePanel = controller.getRootPresenter().getResultsPresenter().getTabPanel().getWidget(index);
			}
			
			tablePanel.closeTablePanel();
			String id = tablePanel.getEntity().getId();
			MissionTabButtons tab = controller.getRootPresenter().getResultsPresenter().getTabPanel().getTabFromId(id);
			controller.getRootPresenter().getResultsPresenter().getTabPanel().removeTab(tab);
		}catch(IndexOutOfBoundsException e) {
			Log.error(e.toString(), e);
		}
	}

	public void closeResultPanelTabById(String id, JavaScriptObject widget) {
		
		ITablePanel tablePanel = controller.getRootPresenter().getResultsPresenter().getTabPanel().getTablePanelFromId(id);
		
		if(tablePanel== null) {
			sendBackMessageToWidget("Tab not found with id: " + id, widget);
		}else {
			tablePanel.closeTablePanel();
		}
	}
	
	public void closeAllResultPanelTabs() {
		while(true) {
			try {
				ITablePanel tablePanel = controller.getRootPresenter().getResultsPresenter().getTabPanel().getSelectedWidget();
				if(tablePanel == null) {
					break;
				}
				tablePanel.closeTablePanel();
			}catch(Exception e) {
				Log.debug(e.getMessage(), e);
				break;
			}
		}
	}
}
