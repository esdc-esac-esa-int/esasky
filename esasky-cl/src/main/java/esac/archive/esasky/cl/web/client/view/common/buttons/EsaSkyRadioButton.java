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

package esac.archive.esasky.cl.web.client.view.common.buttons;

import java.util.LinkedList;
import java.util.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class EsaSkyRadioButton extends Widget{
	
    private Resources resources;
    private CssResource style;
    
    private String groupName;
    private String id;

    private InputElement radioElement;
    
    public static interface Resources extends ClientBundle {

        @Source("esaSkyRadioButton.css")
        @CssResource.NotStrict
        CssResource style();
    }

   public EsaSkyRadioButton(String groupName) {
	   super();
	   Element container = DOM.createDiv();
	   Element label = DOM.createLabel();
	   Element span = DOM.createSpan();
	   radioElement = InputElement.as(DOM.createInputRadio(groupName));
	   this.groupName = groupName;
	   id = UUID.randomUUID().toString();
	   radioElement.setId(id);
	   
	   label.appendChild(radioElement);
	   label.appendChild(span);
	   container.appendChild(label);
	   setElement(container);
	   
	   this.resources = GWT.create(Resources.class);
	   this.style = this.resources.style();
	   this.style.ensureInjected();
	   
	   //GWT compiler does not allow ~ in css files, so these styles are injected after compilation
	   StyleInjector.injectAtEnd("\n" + 
	   		"/* On mouse-over, add a grey background color */\n" + 
	   		".container:hover input ~ .checkmark {\n" + 
	   		"  background-color: rgba(255, 255, 255, 0.15);\n" + 
	   		"}\n" + 
	   		"\n" + 
	   		"/* When the radio button is checked, add a blue background */\n" + 
	   		".container input:checked ~ .checkmark {\n" + 
	   		"  background-color: #20a4d8;\n" +
	   		"  border: 0px;\n"
	   		+ "	transition: background-color 0.5s ease;\n" + 
	   		"	-webkit-transition: background-color 0.5s ease;" + 
	   		"}\n" + 
	   		"\n" + 
	   		"/* Show the indicator (dot/circle) when checked */\n" + 
	   		".container input:checked ~ .checkmark:after {\n" + 
	   		"  display: block;\n" + 
	   		"}");
	   
	   label.addClassName("container");
	   span.addClassName("checkmark");
	   addStyleName("esaSkyRadioButton");
   }
   
   private native void addListener(String id, EsaSkyRadioButton button) /*-{
    $wnd.$('#' + id).on('click touch', function(){
    	button.@esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyRadioButton::pressed()();
    });
   }-*/;
   
   public void pressed() {
	   notifyObservers();
   }
   
   public void setSelected(boolean selected) {
	   boolean oldValue = radioElement.isChecked();
	   radioElement.setChecked(selected);
	   radioElement.setDefaultChecked(selected);
	   if(selected != oldValue) {
		   notifyObservers();
	   }
   }
   
   public boolean isSelected() {
	   return radioElement.isChecked();
   }
   
   @Override
   protected void onLoad() {
	   addListener(id, this);
   }
   
   private LinkedList<EsaSkyButtonValueObserver> observers = new LinkedList<EsaSkyButtonValueObserver>();
   
   public void registerValueChangeObserver(EsaSkyButtonValueObserver observer) {
	   observers.add(observer);
   }
   
   private void notifyObservers() {
	   for(EsaSkyButtonValueObserver observer : observers) {
		   observer.onValueChange(radioElement.isChecked());
	   }
   }
}
