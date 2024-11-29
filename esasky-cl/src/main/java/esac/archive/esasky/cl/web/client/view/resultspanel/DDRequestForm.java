package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;

/**
 * DDRequestForm.
 * @author ESDC team Copyright (c) 2015 - European Space Agency
 */
public class DDRequestForm extends FormPanel {

    private FlowPanel fp = new FlowPanel();
    private Hidden field = new Hidden();

    /**
     * Class constructor.
     * @param targetName Input String
     */
    public DDRequestForm(final String targetName) {
        super(targetName);
        this.setWidget(fp);
        field.setName("REQUEST");
        fp.add(field);
    }

    @Override
    public final void add(final Widget field) {
        fp.add(field);
    }

    /**
     * setJSonRequest().
     * @param json Input string.
     */
    public final void setJsonRequest(final String json) {
        field.setValue(json);
    }
    
    
    public final void setField(final String name, final String value) {
    	field.setName(name);
        field.setValue(value);
    }
}
