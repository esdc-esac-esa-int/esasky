package esac.archive.esasky.cl.web.client.model;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Cell based on ImageResourceCell, extended with a couple of features: <br/>
 * - adds a click handler to the image<br/>
 * - shows a pop up panel with a Widget (provided in the constructor) after a delay (also provided)
 * when mouse is placed over the image in the cell.
 *
 * @author Maria Henar Sarmiento Carrion Copyright (c) 2016 - European Space Agency
 */
public class EsaSkyButtonCell extends ButtonCell {

    /**
     * Pop up panel to be displayed when mouse is placed over the cell. The content of the pop up
     * panel is a widget passed as an argument in one of the constructors.
     */
    private PopupPanel popupPanel;

    /**
     * Timer and delay time that controls how long it will take for the pop up to be shown after the
     * mouse is placed over the cell.
     */
    private Timer showPopupPanelTimer;

    /** Popup delay. */
    private int showPopupDelayMs;

	
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;
    
    
    public static interface Resources extends ClientBundle {

        @Source("esaSkyButtonCell.css")
        @CssResource.NotStrict
        CssResource style();
    }
	
    public EsaSkyButtonCell(final String tooltipText, final int popupDelayMs) {
        super();
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.popupPanel = new PopupPanel();
        popupPanel.getElement().getStyle().setZIndex(50);
        popupPanel.getElement().getStyle().setProperty("borderRadius", "10px");
        	popupPanel.getElement().getStyle().setPadding(3, Unit.PX);
        	
        	Label tooltipLabel = new Label(tooltipText);
        	tooltipLabel.addStyleName("darkLabel");
    	
        this.popupPanel.setWidget(tooltipLabel);
        this.showPopupDelayMs = popupDelayMs;
    }

    @Override
    public final Set<String> getConsumedEvents() {
        Set<String> set = new HashSet<String>();
        set.add("click");
        set.add("mouseover");
        set.add("mouseout");
        return set;
    }

    @Override
    public final void onBrowserEvent(final Context context, final Element parent,
            final String value, final NativeEvent event,
            final ValueUpdater<String> valueUpdater) {

        // Let the parent handle the rest of events not sink by this cell
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        
        switch (DOM.eventGetType((Event) event)) {

        case Event.ONCLICK:
            hidePopupPanel();

            // In order to handle the click event, a FieldUpdater must be defined externally
            // for this column.
            // Clicking the cell will trigger the update() on the ValueUpdater on this cell
            // which, at the same time, will call the update() on the FieldUpdater for this column.

            // This is similar as how ClickableTextCell class handles the click.

            break;

        case Event.ONMOUSEOVER:
            showPopupPanel(event);
            break;

        case Event.ONMOUSEOUT:
            hidePopupPanel();
            break;
        default:
            break;
        }

    }

    /**
     * Show tooltip popup panel.
     * @param event Input event.
     */
    protected final void showPopupPanel(final NativeEvent event) {
        if (this.showPopupPanelTimer != null) {
            this.showPopupPanelTimer.cancel();
        }
        this.showPopupPanelTimer = new Timer() {

            @Override
            public void run() {
                if (EsaSkyButtonCell.this.popupPanel != null) {
                    EsaSkyButtonCell.this.popupPanel.setPopupPosition(event.getClientX(),
                            event.getClientY());
                    EsaSkyButtonCell.this.popupPanel.show();
                }
            };
        };
        this.showPopupPanelTimer.schedule(this.showPopupDelayMs);
    }

    /**
     * Hide tooltip popup panel.
     */
    protected final void hidePopupPanel() {
        if (this.showPopupPanelTimer != null) {
            this.showPopupPanelTimer.cancel();
        }
        if (this.popupPanel != null) {
            this.popupPanel.hide();
        }
    }
    
    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb){
        	final FlowPanel flowPanel = new FlowPanel();
        	flowPanel.getElement().getStyle().setCursor(Cursor.POINTER);
        	flowPanel.add(new Image(value));
        	flowPanel.addStyleName("buttonCell");
        	sb.append(SafeHtmlUtils
                .fromTrustedString(flowPanel.toString()));
    }
    

}
