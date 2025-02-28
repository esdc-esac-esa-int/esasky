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
