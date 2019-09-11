package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
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
		super();
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();
		this.switchId = switchId;
		createSwitch(isChecked, "esasky__switch", label, tooltip);
	}

	public void setChecked(boolean checked) {
		Document.get().getElementById(switchId).setPropertyBoolean("checked", checked);
	}
	
	private void createSwitch(boolean isChecked, String cssPrefix, String label, String tooltip) {
		FlowPanel switchContainer = new FlowPanel();
		switchContainer.addStyleName(cssPrefix + "__container");

		HTML switchLabel = new HTML(
				"<label class=\"" + cssPrefix + "__label unselectable\" for=\"" + switchId + "\"> " 
						+ label + "</label>");
		switchLabel.addStyleName(cssPrefix + "__label-container");
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

		switchContainer.addStyleName(cssPrefix + "__clickable-area");
		switchContainer.setTitle(tooltip);

		add(switchContainer);
	}
	
}
