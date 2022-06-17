package esac.archive.esasky.cl.web.client.view.ctrltoolbar.planningmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;

public class InstrumentDetailsPopup extends PopupPanel {

    private final String CSS_ID = "instrumentDbox";
    private final VerticalPanel instrumentDetailsPanel;
    private Resources resources = GWT.create(Resources.class);
    private CssResource style;
    private double offsetAngle;
    private double instrumentAngle;
    private double totalAngle;
    private final NumberFormat angleFormat = NumberFormat.getFormat("#0.00");
    private final String DEGREE = "\u00B0\n";
    private final String BOLD_OPEN = "<b>";
    private final String BOLD_CLOSED = "</b>";
    private final String LINE_BREAK = "<br/>";

    public static interface Resources extends ClientBundle {
        @Source("instrumentDetailsPopup.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public InstrumentDetailsPopup(double offsetAngle, double instrumentAngle, double totalAngle) {
        this.style = this.resources.style();
        this.style.ensureInjected();

        this.offsetAngle=offsetAngle;
        this.instrumentAngle=instrumentAngle;
        this.totalAngle=totalAngle;
        
        this.instrumentDetailsPanel = new VerticalPanel();
        initView();
    }

    private void initView() {
        this.getElement().setId(CSS_ID);
        this.setAutoHideEnabled(true);

        CloseButton closeBtn = new CloseButton();
        closeBtn.setTitle(TextMgr.getInstance().getText("instrumentDetailsPopup_close"));
        closeBtn.addStyleName("closeinstrumentDetails");
        closeBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                InstrumentDetailsPopup.this.hide();
            }
        });

        instrumentDetailsPanel.setWidth("100%");

        instrumentDetailsPanel.add(closeBtn);

        instrumentDetailsPanel.add(new HTML(BOLD_OPEN + TextMgr.getInstance().getText("futureFootprintRow_tooltip") + "BOLD_CLOSED"));
        instrumentDetailsPanel.add(new HTML(LINE_BREAK));
        
        addInstrumentAngle(instrumentAngle);
        addOffsetAngle(offsetAngle);
        addTotalRotation(totalAngle);
        instrumentDetailsPanel.add(new HTML(LINE_BREAK));
        addInfoLink();

        this.add(instrumentDetailsPanel);

        MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {
                setMaxHeight();
            }
        });
        getContainerElement().getStyle().setOverflow(Overflow.AUTO);

        setWidth(170 + "px");
    }

    private void setMaxHeight() {
        getContainerElement().getStyle().setProperty("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getContainerElement().getAbsoluteTop() - 10 + "px");
    }
    
    private void addInstrumentAngle(double instrumentAngle) {
    	instrumentDetailsPanel.add(new HTML(BOLD_OPEN + TextMgr.getInstance().getText("futureFootprintRow_instrAngle") + BOLD_CLOSED + angleFormat.format(instrumentAngle) + DEGREE));
    }
    
    private void addOffsetAngle(double offsetAngle) {
    	instrumentDetailsPanel.add(new HTML(BOLD_OPEN + TextMgr.getInstance().getText("futureFootprintRow_offsetAngle") + BOLD_CLOSED + angleFormat.format(offsetAngle) + DEGREE));
    }
    
    private void addTotalRotation(double totalAngle) {
    	instrumentDetailsPanel.add(new HTML(BOLD_OPEN + TextMgr.getInstance().getText("futureFootprintRow_totalRotation") + BOLD_CLOSED + angleFormat.format(totalAngle) + DEGREE));
    }

    private void addInfoLink() {
    	instrumentDetailsPanel.add(new HTML(TextMgr.getInstance().getText("futureFootprintRow_instrAngleInfo")));
    }
    

    @Override
    public void show() {
        super.show();
        setMaxHeight();
    }

    @Override
    public void setPopupPosition(int left, int top) {
        super.setPopupPosition(left, top);
        setMaxHeight();
    }

}
