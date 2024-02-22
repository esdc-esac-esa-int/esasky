package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextBox;



public class EsaSkyTextBox extends TextBox{
	
	public EsaSkyTextBox(){
		super();
		sinkEvents(Event.ONPASTE | Event.ONKEYPRESS);
	}
	
   @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        int eventNumber = DOM.eventGetType(event);
        if (eventNumber == Event.ONPASTE || eventNumber == Event.ONKEYPRESS) {
    	
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            	
                @Override
                public void execute() {
                    ValueChangeEvent.fire(EsaSkyTextBox.this, getText());
                }

            });
        }
    }
   
   @Override
   public void setText(String text) {
	   super.setText(text);
	   ValueChangeEvent.fire(EsaSkyTextBox.this, getText());
   }

   public void setTextSilently(String text) {
        super.setText(text);
   }
}
