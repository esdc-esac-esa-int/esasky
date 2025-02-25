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

package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.hips.HipsAddedEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.HipsParser;
import esac.archive.esasky.cl.web.client.utility.HipsParserObserver;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.EsaSkyTextBox;
import esac.archive.esasky.cl.web.client.view.common.Hidable;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;


public class HipsUrlPanel extends PopupPanel implements Hidable<PopupPanel> {

	private EsaSkyTextBox textBox;
	private LoadingSpinner loadingSpinner = new LoadingSpinner(false);
	private HTML errorLabel;
	
	private final Resources resources;
	private CssResource style;

	public static interface Resources extends ClientBundle {

		@Source("hipsUrlPanel.css")
		@CssResource.NotStrict
		CssResource style();
	}

	public HipsUrlPanel() {
		super();
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();
		initView();
	}
	
	Timer timer = new Timer() {
		
		@Override
		public void run() {
			urlChanged(textBox.getText());
		}
		
		@Override
		public void schedule(int delayMillis) {
			super.cancel();
			super.schedule(delayMillis);
		}
	};
	
	private void initView() {
		this.getElement().setId("hipsUrlPanel");
		
		setModal(false);
        setAutoHideEnabled(true);
        
        FlowPanel container = new FlowPanel();
        
        FlowPanel textAndSpinnerPanel = new FlowPanel();
        textAndSpinnerPanel.addStyleName("addUrl_textAndSpinner");
        
        PopupHeader<PopupPanel> header = new PopupHeader<>(this, TextMgr.getInstance().getText("addUrl_Header"),
        		TextMgr.getInstance().getText("addUrl_Description"));
        
        textBox = createTextBox();
        textBox.addStyleName("hipsTextBox");
        loadingSpinner.setVisible(false);
        textAndSpinnerPanel.add(textBox);
        textAndSpinnerPanel.add(loadingSpinner);
        errorLabel = new HTML();
        errorLabel.setVisible(false);
        errorLabel.addStyleName("hipsErrorText");
        
        container.add(header);
        container.add(textAndSpinnerPanel);
        container.add(errorLabel);
		add(container);
		
        MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
            
            @Override
            public void onResize(ResizeEvent event) {
                setMaxSize();
            }
        });
		
	}
	
	private EsaSkyTextBox createTextBox() {
		EsaSkyTextBox textBox = new EsaSkyTextBox();
		textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				timer.schedule(500);
			}
		});
		return textBox;
	}

	private void urlChanged(final String url) {
		loadingSpinner.setVisible(true);
		HipsParser parser = new HipsParser(new HipsParserObserver() {
			
			@Override
			public void onSuccess(HiPS hips) {
				loadingSpinner.setVisible(false);
				CommonEventBus.getEventBus().fireEvent(new HipsAddedEvent(hips, HipsWavelength.USER));
				errorLabel.setVisible(false);
				hide();
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SKIESMENU, GoogleAnalytics.ACT_SKIESMENU_ADDURL, url);
			}
			
			@Override
			public void onError(String errorMsg) {
				loadingSpinner.setVisible(false);
				errorLabel.setVisible(true);
				String fullErrorText = TextMgr.getInstance().getText("addSky_errorParsingProperties");
				fullErrorText = fullErrorText.replace("$DUE_TO$", errorMsg);
				errorLabel.setHTML(fullErrorText);
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SKIESMENU, GoogleAnalytics.ACT_SKIESMENU_ADDURL_FAIL, url);

				Log.error(errorMsg);
				
			}
		});
		parser.loadProperties(url);
	}
	
	public void focus() {
		textBox.setFocus(true);
	}
	
    private void setMaxSize() {
        getElement().getStyle().setPropertyPx("maxWidth", getMaxPossibleWidth());
    }
    
    private int getMaxPossibleWidth() {
        return MainLayoutPanel.getMainAreaWidth() - getElement().getAbsoluteLeft();
    }
	
	


}
