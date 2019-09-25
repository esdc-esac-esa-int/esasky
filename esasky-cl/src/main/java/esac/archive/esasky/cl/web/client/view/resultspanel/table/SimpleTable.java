package esac.archive.esasky.cl.web.client.view.resultspanel.table;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEvent;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEventHandler;
import esac.archive.esasky.cl.web.client.model.TableRow;

public class SimpleTable<T extends TableRow> extends ESASkyDataGrid<T> {
	private Resources resources;
	private CssResource style;
	private com.google.gwt.dom.client.Style tableContentStyle;
	
    public interface Resources extends ClientBundle {
        @Source("datagrid.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public interface ESASkyDataGridResource extends ESASkyDataGrid.Resources {

        public ESASkyDataGrid.Resources INSTANCE = GWT.create(ESASkyDataGridResource.class);

        /**
         * The styles used in this widget.
         */
        @Override
        @Source({ ESASkyDataGrid.Style.DEFAULT_CSS, "datagrid.css" })
        @CssResource.NotStrict
        CustomStyle dataGridStyle();

        interface CustomStyle extends ESASkyDataGrid.Style {
        }
    }

    public SimpleTable() {
        super(1, ESASkyDataGridResource.INSTANCE);
       
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
		CommonEventBus.getEventBus().addHandler(DataPanelResizeEvent.TYPE,
				new DataPanelResizeEventHandler() {
					
					@Override
					public void onDataPanelResize(DataPanelResizeEvent event) {
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {
							
							@Override
							public void execute() {
								refreshHeight();
							}
						});
					}
				});
        
        tableContentStyle = ((Element)getElement().getChild(2)).getStyle();
        refreshHeight();
    }
    
    public void refreshHeight() {
    	String newHeight = "calc(100% - " + tableContentStyle.getTop() + ")";
    	String currentHeight = tableContentStyle.getProperty("height");
    	if(!currentHeight.equals(newHeight)) {
    		tableContentStyle.setProperty("height", newHeight);
    	}
    	setScrollbarHeight();
    }
}
