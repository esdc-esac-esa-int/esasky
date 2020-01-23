package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
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
import esac.archive.esasky.cl.web.client.view.animation.AnimationObserver;
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

	/**
	 * Initialize widget view.
	 */
	private void initView() {
		Log.debug("[ResultsPanel] Initializing ResulsPanel...");

		// Wrap ResultPanel into a LayoutPanel
		DockLayoutPanel contentPanel = new DockLayoutPanel(Unit.PX);
		
		contentPanel.add(tabPanel);

		resultsLP.add(contentPanel);
		resultsLP.getElement().setId("resultPanel");

		panelAnimation.addObserver(new AnimationObserver() {
			
			@Override
			public void onComplete(double currentPosition) {
				if(GUISessionStatus.isDataPanelOpen()){
					tabPanel.refreshHeight();
				} else {
					CommonEventBus.getEventBus().fireEvent(new DataPanelAnimationCompleteEvent());
				}
			}
		});
		initWidget(resultsLP);
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				ensureDataPanelCanFit();
			}
		});
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
	public final LayoutPanel getResultsLP() {
		return resultsLP;
	}

	/**
	 * getTabPanel().
	 * 
	 * @return CloseableTabLayoutPanel
	 */
	@Override
	public final CloseableTabLayoutPanel getTabPanel() {
		return tabPanel;
	}

	@Override
    public final AbstractTablePanel addResultsTab(final GeneralEntityInterface entity, final String helpTitle, final String helpDescription) {

//        openDataPanel();
        
        AbstractTablePanel tablePanel = entity.createTablePanel();
        
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
	
	/**
	 * getTabFromTableId().
	 * 
	 * @param id
	 *            EsaSkyUniqID
	 * @return Widget
	 */
	@Override
	public final Widget getTabFromTableId(final String id) {
		return tabPanel.getAbstractTablePanelFromId(id);
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