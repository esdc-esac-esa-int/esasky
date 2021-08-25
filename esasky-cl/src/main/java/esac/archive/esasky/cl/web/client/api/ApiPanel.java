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
					if(numberOfShownRows > 0) {
						JSONObject callback = tablePanel.exportAsJSON();
						if(callback.size() > 0) {
							sendBackValuesToWidget(callback, widget);
							tablePanel.unregisterObserver(this);
						}
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
			String id = tablePanel.getEntity().getEsaSkyUniqId();
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
