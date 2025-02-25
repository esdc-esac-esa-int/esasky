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

public class EsaSkyCheckButton extends Widget{

    private Resources resources;
    private CssResource style;

    private String groupName;
    private String id;

    private InputElement checkElement;

    public static interface Resources extends ClientBundle {

        @Source("esaSkyCheckButton.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public EsaSkyCheckButton() {
        this(null);
    }
    public EsaSkyCheckButton(String groupName) {
        super();
        this.groupName = groupName;

        Element container = DOM.createDiv();
        Element label = DOM.createLabel();
        Element span = DOM.createSpan();
        checkElement = InputElement.as(DOM.createInputCheck());
        id = UUID.randomUUID().toString();
        checkElement.setId(id);

        label.appendChild(checkElement);
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
                "/* When the checkbox is checked, add a blue background */\n" +
                ".container input:checked ~ .checkmark {\n" +
                "  background-color: #20a4d8;\n" +
                "  border: 0px;\n"
                + "	transition: background-color 0.5s ease;\n" +
                "	-webkit-transition: background-color 0.5s ease;\n" +
                "}\n" +
                "\n" +
                "/* Show the indicator (checkmark) when checked */\n" +
                ".container input:checked ~ .checkmark:after {\n" +
                "  display: block;\n" +
                "}");

        label.addClassName("container");
        span.addClassName("checkmark");
        addStyleName("esaSkyCheckButton");
    }


    private native void addListener(String id, EsaSkyCheckButton button) /*-{
        $wnd.$('#' + id).on('click touch', function(){
            var isSelected = button.@esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyCheckButton::isSelected()();
            button.@esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyCheckButton::pressed()();
            var groupName = button.@esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyCheckButton::getGroupName()();
            if (groupName && isSelected) {
                var checkboxes = $wnd.document.getElementsByName(groupName);
                checkboxes.forEach(function(cb) {
                    var cbInst = cb.__esasky_check_button_inst;
                    if (cbInst && cbInst !== button) {
                        cbInst.@esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyCheckButton::setSelected(*)(false, false);
                    }
                });
            }
        });
    }-*/;


    private native void deselectOthers(EsaSkyCheckButton button, String groupName)  /*-{
        if (groupName) {
            var checkboxes = $wnd.document.getElementsByName(groupName);
            checkboxes.forEach(function(cb) {
                var cbInst = cb.__esasky_check_button_inst;
                if (cbInst && cbInst !== button) {
                    cbInst.@esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyCheckButton::setSelected(*)(false, false);
                }
            });
        }
    }-*/;

    public String getGroupName() {
        return groupName;
    }

    public void pressed() {
        notifyObservers();
    }

    public void deselectAll() {
        if (groupName != null && !groupName.isEmpty()) {
            deselectOthers(null, groupName);
        }
    }

    public void setSelected(boolean selected, boolean notifyObservers) {
        boolean oldValue = checkElement.isChecked();
        checkElement.setChecked(selected);
        checkElement.setDefaultChecked(selected);

        if (selected && this.groupName != null && !this.groupName.isEmpty()) {
            deselectOthers(this, groupName);
        }

        if(notifyObservers && selected != oldValue) {
            notifyObservers();
        }
    }

    public boolean isSelected() {
        return checkElement.isChecked();
    }

    @Override
    protected void onLoad() {
        if (groupName != null && !groupName.isEmpty()) {
            this.getElement().setAttribute("name", groupName);
        }
        this.getElement().setPropertyObject("__esasky_check_button_inst", this);
        addListener(id, this);
    }

    private LinkedList<EsaSkyButtonValueObserver> observers = new LinkedList<EsaSkyButtonValueObserver>();

    public void registerValueChangeObserver(EsaSkyButtonValueObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for(EsaSkyButtonValueObserver observer : observers) {
            observer.onValueChange(checkElement.isChecked());
        }
    }
}
