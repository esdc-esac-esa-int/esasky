package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.DisplayUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.AutoHidingMovablePanel;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.DefaultTabulatorCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class BrowseHipsPanel extends AutoHidingMovablePanel {

	private final Resources resources = GWT.create(Resources.class);
	private CssResource style;

	public interface Resources extends ClientBundle {
		@Source("BrowseHipsPanel.css")
		@CssResource.NotStrict
		CssResource style();
	}

	private final FlowPanel contentContainer = new FlowPanel();
	private final FlowPanel tabulatorContainer = new FlowPanel();
	private final CloseButton closeButton;
	private FlowPanel contentAndCloseButton;
    private final LoadingSpinner loadingSpinner = new LoadingSpinner(true);
    
    private static String aladinGlobalHipsListCache = null;
	
	private List<BrowseHipsPanelObserver> observers = new LinkedList<BrowseHipsPanelObserver>();
	TabulatorWrapper tabulatorTable;
	
	public BrowseHipsPanel() {
		super(GoogleAnalytics.CAT_BROWSEHIPS);
		this.style = this.resources.style();
		this.style.ensureInjected();
		
        loadingSpinner.addStyleName("browseHips__loadingSpinner");
        MainLayoutPanel.addElementToMainArea(loadingSpinner);

		closeButton = new CloseButton();
		closeButton.addStyleName("browseHips__closeButton");
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				hide();	
			}
		});
		
		contentContainer.addStyleName("browseHips__contentContainer");
		tabulatorContainer.getElement().setId("browseHips__tabulatorContainer");	
		
		Label missionLabel = new Label(TextMgr.getInstance().getText("browseHips_headerLabel"));
		missionLabel.setStyleName("browseHips__missionLabel");

		contentAndCloseButton = new FlowPanel();
		contentAndCloseButton.add(missionLabel);
		contentAndCloseButton.add(closeButton);
		contentContainer.add(tabulatorContainer);
		contentAndCloseButton.add(contentContainer);
		add(contentAndCloseButton);
		addStyleName("browseHips__dialogBox");
		addElementNotAbleToInitiateMoveOperation(contentContainer.getElement());

		loadData();
		
		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SKIESMENU, GoogleAnalytics.ACT_SKIESMENU_BROWSEHIPS, "");
		show();
	}
	
	private void loadData() {
		if (aladinGlobalHipsListCache != null) {
		    onJsonLoaded(aladinGlobalHipsListCache);
		} else {
		    JSONUtils.getJSONFromUrl(EsaSkyWebConstants.HIPSLIST_URL, new IJSONRequestCallback() {
		        
		        @Override
		        public void onSuccess(String responseText) {
		            aladinGlobalHipsListCache = responseText;
		            onJsonLoaded(responseText);
		        }
		        
		        @Override
		        public void onError(String errorCause) {
		            String errorMsg = TextMgr.getInstance().getText("browseHips_errorLoadingGlobal");
		            errorMsg = errorMsg.replace("$URL$", EsaSkyWebConstants.HIPSLIST_URL);
		            DisplayUtils.showMessageDialogBox(errorMsg, TextMgr.getInstance().getText("error").toUpperCase(), UUID.randomUUID().toString(),
		                    TextMgr.getInstance().getText("error"));
		            MainLayoutPanel.removeElementFromMainArea(loadingSpinner);
		            Log.error(errorCause);
		        }
		        
		    });
		}
	}
	
	public native GeneralJavaScriptObject createMetadata() /*-{
		
		var metadata = [];
        metadata.push(
            {
                name: "ID",
                displayName: $wnd.esasky.getInternationalizationText("ID"), 
                datatype:"STRING", 
                visible: true
            });
        metadata.push(
            {
                name: "obs_title",
                displayName: $wnd.esasky.getInternationalizationText("title"), 
                datatype:"STRING", 
                visible: true
            });
        metadata.push(
            {
                name: "moc_order",
                displayName: $wnd.esasky.getInternationalizationText("hips_order"), 
                datatype:"INTEGER", 
                visible: true
            });
        metadata.push(
            {
                name: "moc_sky_fraction",
                displayName: $wnd.esasky.getInternationalizationText("hips_coverage"), 
                datatype:"PERCENT", 
                visible: true
            });
        metadata.push(
            {
                name: "em_min",
                displayName: $wnd.esasky.getInternationalizationText("hips_em_min"), 
                datatype:"DOUBLE", 
                visible: true
            });
        metadata.push(
            {
                name: "em_max",
                displayName: $wnd.esasky.getInternationalizationText("hips_em_max"), 
                datatype:"DOUBLE", 
                visible: true
            });
    	metadata.push(
            {
                name: "hips_service_url",
                displayName: "URL", 
                datatype:"HTML", 
                visible: true,
				makeHref: true
            });
           return metadata;
	}-*/;

	public void onJsonLoaded(String jsonText) {
		this.show();
		TabulatorSettings settings = new TabulatorSettings();
		tabulatorTable = new TabulatorWrapper("browseHips__tabulatorContainer", new TabulatorCallback(), settings);
		GeneralJavaScriptObject metadata = createMetadata();
		tabulatorTable.setAddHipsColumn(true);
		tabulatorTable.insertData(jsonText, metadata);
		tabulatorTable.filter("dataproduct_type", "like", "image");
		tabulatorTable.restoreRedraw();
        tabulatorTable.redrawAndReinitializeHozVDom();
	}


	@Override
	public void setMaxSize() {
		super.setMaxSize();
	    int height = MainLayoutPanel.getMainAreaHeight();
		if (height > MainLayoutPanel.getMainAreaHeight() - 30 - 2) {
			height = MainLayoutPanel.getMainAreaHeight() - 30 - 2;
		}
		contentContainer.getElement().getStyle().setPropertyPx("height", height - contentContainer.getAbsoluteTop());
		
		if(MainLayoutPanel.getMainAreaWidth() > 600) {
			contentContainer.getElement().getStyle().setPropertyPx("width", (int) (MainLayoutPanel.getMainAreaWidth() * 0.8));
			
		}
	}

	public void registerObserver(BrowseHipsPanelObserver observer) {
		observers.add(observer);
	}
	
	
	private class TabulatorCallback extends DefaultTabulatorCallback {
		
		@Override
		public void onDataLoaded(GeneralJavaScriptObject javaScriptObject) {
			MainLayoutPanel.removeElementFromMainArea(loadingSpinner);
			setSuggestedPositionCenter();
		}
		
		@Override
		public void onAddHipsClicked(GeneralJavaScriptObject rowData) {
			List<String> urls = new LinkedList<>();
			urls.add(rowData.getStringProperty("hips_service_url"));
			int i = 1;
			while(rowData.hasProperty("hips_service_url_" + i)) {
				urls.add(rowData.getStringProperty("hips_service_url_" + i));
				i++;
			}
			
			for(BrowseHipsPanelObserver observer : observers) {
				observer.onHipsAdded(urls);
			}
		}
	}
}
