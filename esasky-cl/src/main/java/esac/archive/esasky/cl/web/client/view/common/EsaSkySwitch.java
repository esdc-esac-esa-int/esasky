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

package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;

public class EsaSkySwitch extends FocusPanel{
	
    private Resources resources;
    private final CssResource style;
    private final String switchId;

    public static interface Resources extends ClientBundle {

        @Source("esaSkySwitch.css")
        @CssResource.NotStrict
        CssResource style();
    }
	
    public EsaSkySwitch(String switchId, boolean isChecked, String label, String tooltip){
    	this(switchId, isChecked, label, tooltip, null);
    }
    
	public EsaSkySwitch(String switchId, boolean isChecked, String labelOption1, String tooltip, String labelOption2){
		super();
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();
		this.switchId = switchId;
		createSwitch(isChecked, "esasky__switch", labelOption1, tooltip, labelOption2);
	}

	public void setChecked(boolean checked) {
		Document.get().getElementById(switchId).setPropertyBoolean("checked", checked);
		Element option1 = Document.get().getElementById(switchId + "__option1");
		Element option2 = Document.get().getElementById(switchId + "__option2");
		if(option1 != null && option2 != null) {
			if(checked) {
				option1.getStyle().setProperty("fontWeight", "normal");
				option2.getStyle().setProperty("fontWeight", "bold");
			} else {
				option1.getStyle().setProperty("fontWeight", "bold");
				option2.getStyle().setProperty("fontWeight", "normal");
			}
		}
	}
	
	private void createSwitch(boolean isChecked, String cssPrefix, String label, String tooltip, String labelOption2) {
		FlowPanel switchContainer = new FlowPanel();
		switchContainer.addStyleName(cssPrefix + "__container");

		HTML switchLabel = new HTML(
				"<label class=\"" + cssPrefix + "__label unselectable\" for=\"" + switchId + "\"> " 
						+ label + "</label>");
		switchLabel.addStyleName(cssPrefix + "__label-container");
		switchLabel.getElement().setId(switchId + "__option1");
		switchContainer.add(switchLabel);
		String inputHtml = "  <input type=\"checkbox\" id=\"" + switchId + "\">";
		if(isChecked) {
			inputHtml = inputHtml.replace(">", "checked>");
		}
		HTML htmlSwitch = new HTML(
				"<label class=\"" + cssPrefix + "__switch-container\">"
						+ inputHtml
						+ "  <span class=\"" + cssPrefix + "__switch-slider round\"></span>" + 
				"</label>");
		htmlSwitch.addStyleName(cssPrefix + "__switch-container");
		switchContainer.add(htmlSwitch);

		if(labelOption2 != null) {
			HTML switchLabelOption2 = new HTML(
					"<label class=\"" + cssPrefix + "__label unselectable\" for=\"" + switchId + "\"> " 
							+ labelOption2 + "</label>");
			switchLabelOption2.addStyleName(cssPrefix + "__label-container");
			switchLabelOption2.getElement().setId(switchId + "__option2");
			switchContainer.add(switchLabelOption2);
			htmlSwitch.addStyleName("esasky__switch__twoLabels");
			if(isChecked) {
				switchLabelOption2.getElement().getStyle().setProperty("fontWeight", "bold");
			} else {
				switchLabel.getElement().getStyle().setProperty("fontWeight", "bold");
			}
		}

		switchContainer.addStyleName(cssPrefix + "__clickable-area");
		switchContainer.setTitle(tooltip);

		add(switchContainer);
	}
	
}
