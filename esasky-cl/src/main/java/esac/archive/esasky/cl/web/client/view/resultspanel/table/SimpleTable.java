package esac.archive.esasky.cl.web.client.view.resultspanel.table;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.cellview.client.DataGrid;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEvent;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEventHandler;
import esac.archive.esasky.cl.web.client.model.TableRow;

public class SimpleTable<T extends TableRow> extends DataGrid<T> {
	private Resources resources;
	private CssResource style;
	private com.google.gwt.dom.client.Style tableContentStyle;
	
    public interface Resources extends ClientBundle {
        @Source("datagrid.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public interface DataGridResource extends DataGrid.Resources {

        public DataGrid.Resources INSTANCE = GWT.create(DataGridResource.class);

        /**
         * The styles used in this widget.
         */
        @Override
        @Source({ DataGrid.Style.DEFAULT_CSS, "datagrid.css" })
        CustomStyle dataGridStyle();

        interface CustomStyle extends DataGrid.Style {

        }
    }

    public SimpleTable() {
        super(1, DataGridResource.INSTANCE);
       
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
								//activate and deactivate to get gwt datagrid to remove/add necessary scrollbar
								setAlwaysShowScrollBars(true);
								setAlwaysShowScrollBars(false);
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
    }
}
