package esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;

import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;

public class StringFilterDialogBox extends FilterDialogBox {
	
    private final FilterObserver filterObserver;
    private FilterTimer filterTimer = new FilterTimer();
	
    private final Resources resources = GWT.create(Resources.class);
    private CssResource style;

    private final TextBox textLine;
    
	private final CloseButton clearTextButton;

    public interface Resources extends ClientBundle {
        @Source("stringFilterDialogBox.css")
        @CssResource.NotStrict
        CssResource style();
    }
    
	public StringFilterDialogBox(final String columnName, final String filterButtonId, final FilterObserver filterObserver) {
		super(filterButtonId);
		this.filterObserver = filterObserver;
        this.style = this.resources.style();
        this.style.ensureInjected();
		
        HTML columnNameHTML = new HTML(columnName);
        columnNameHTML.addStyleName("filterColumnName");
		textLine = new TextBox();
		textLine.addStyleName("filterTextBox");
		
		textLine.addKeyUpHandler(new KeyUpHandler() {
	        @Override
	        public void onKeyUp(KeyUpEvent event) {

	        	if(event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
	        		textLine.setText("");
	        		hide();
    			} else if(event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE || event.isMetaKeyDown() || event.isControlKeyDown()){
    			} else {
    			}
	        	notifyFilterTextChanged();
	        }
	    });
		
		clearTextButton = new CloseButton();
		clearTextButton.setDarkStyle();
		clearTextButton.setDarkIcon();
		clearTextButton.addStyleName("clearFilterTextBtn");
		clearTextButton.setVisible(false);
		clearTextButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				textLine.setValue("");
				textLine.setFocus(true);
				notifyFilterTextChanged();
			}
		});
		FlowPanel stringFilterContainer = new FlowPanel();
		stringFilterContainer.addStyleName("stringFilterContainer");
		stringFilterContainer.add(columnNameHTML);
		stringFilterContainer.add(textLine); 
		stringFilterContainer.add(clearTextButton);

		setWidget(stringFilterContainer);
		
		addStyleName("stringFilterDialogBox");
	}
	
	private void notifyFilterTextChanged() {
		if(textLine.getText().equals("")) {
			clearTextButton.setVisible(false);
		} else {
			clearTextButton.setVisible(true);
		}
		filterTimer.setNewText(textLine.getText());
		ensureCorrectFilterButtonStyle();
	}
	
	public String getFilterString() {
		return textLine.getText();
	}
	
	@Override
	public void show() {
		super.show();
		textLine.setFocus(true);
	}

	@Override
	public boolean isFilterActive() {
		return !textLine.getText().equals("");
	}
	
	private class FilterTimer extends Timer{
		
		private String originalText = "";
		private String newText;
		
		@Override
		public void run() {
			if(!originalText.equals(newText)) {
				originalText = newText;
				filterObserver.onNewFilter();
			}
		}
		
		@Override 
		public void cancel() {
			super.cancel();
		}
		
		public void setNewText(String newText) {
			this.newText = newText;
			schedule(500);
		}
		
	}
}
