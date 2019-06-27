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

    /** instance to flow panel. */
    private FlowPanel fp = new FlowPanel();
    /** jsonRequest. */
    private Hidden jsonRequest = new Hidden();

    /**
     * Class constructor.
     * @param targetName Input String
     */
    public DDRequestForm(final String targetName) {
        super(targetName);
        this.setWidget(fp);
        jsonRequest.setName("REQUEST");
        fp.add(jsonRequest);
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
        jsonRequest.setValue(json);
    }
}
