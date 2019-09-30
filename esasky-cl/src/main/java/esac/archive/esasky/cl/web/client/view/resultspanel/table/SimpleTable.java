package esac.archive.esasky.cl.web.client.view.resultspanel.table;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEvent;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEventHandler;
import esac.archive.esasky.cl.web.client.model.TableRow;

public class SimpleTable<T extends TableRow> extends ESASkyDataGrid<T> {
	private Resources resources;
	private CssResource style;
	private com.google.gwt.dom.client.Style tableContentStyle;
	
	private final int MIN_COLUMN_SIZE = 20;
	
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
    
    private TableCellElement changingColumn;
    private boolean ignoreClick = false;
    int offset = 0;
    
    @Override
    protected void onBrowserEvent2(Event event) {

    	boolean hasTriggered = false;
        EventTarget eventTarget = event.getEventTarget();
        if (!Element.is(eventTarget)) {
          return;
        }
        final Element target = event.getEventTarget().cast();
        Element cell = ensureCellIsTarget(target);
        if(cell != null) {
	        int xValue = event.getClientX();
	        int xEndCell = cell.getAbsoluteLeft() + cell.getClientWidth();
	        
	        String eventType = event.getType();
	        if (Math.abs(xValue - xEndCell) < 5){
	        	if(BrowserEvents.MOUSEDOWN.equals(eventType)) {
	        		hasTriggered = true;
	        		changingColumn = target.cast();     
	        		offset = xValue - xEndCell;
	        		DOM.setCapture(this.getElement());
	        		
	        	}else if (BrowserEvents.MOUSEOVER.equals(eventType)){
	        		cell.addClassName("dataGridChangeColumnsSize");
	        	}
	        	else if (BrowserEvents.MOUSEOUT.equals(eventType)){
	        		cell.removeClassName("dataGridChangeColumnsSize");
	        	}
	        	
	        }else if (BrowserEvents.MOUSEMOVE.equals(eventType)){
	        	int newWidth = xValue - changingColumn.getAbsoluteLeft() - offset;
	        	int oldWidth = changingColumn.getClientWidth();
	        	if(newWidth > MIN_COLUMN_SIZE) {
	        		int col = changingColumn.getCellIndex();
	        		doSetColumnWidth(col,Integer.toString(newWidth)+"px");
		        	int change = newWidth - oldWidth - 1;
		        	int oldTableWidth = getTableBodyElement().getClientWidth();
		    		setTableWidth(oldTableWidth + change, Unit.PX);
	        	}
	    		hasTriggered = true;
	    		
	    	}else {
	    		cell.removeClassName("dataGridChangeColumnsSize");
	        }
	    	if (BrowserEvents.MOUSEUP.equals(eventType)){
	    		DOM.releaseCapture(this.getElement());
	    		hasTriggered = true;
	    		ignoreClick = true;
	    		
	    	}else if (BrowserEvents.CLICK.equals(eventType) && ignoreClick){
	    		ignoreClick = false;
	    		hasTriggered = true;
	    	}
        
        }
        
        if(!hasTriggered) {
        	super.onBrowserEvent2(event);
        }
    }

    //Mainly copied from AbstractCellTable
    private Element ensureCellIsTarget(Element target) {
    	Element maybeTableCell = null;
        Element cur = target;
        TableSectionElement tbody = getTableBodyElement();
        TableSectionElement tfoot = getTableFootElement();
        TableSectionElement thead = getTableHeadElement();
        TableCellElement targetTableCell = null;

        /*
         * If an event happens in the TD element but outside the cell's div, we want 
         * to handle it as if it happened within the table cell.
         */
        if (TableCellElement.TAG_TD.equalsIgnoreCase(cur.getTagName())) {
          cur = cur.getFirstChildElement();
        }
        
        while (cur != null) {
          /*
           * Found the table section. Return the most recent cell element that we
           * discovered.
           */
          if (cur == tbody || cur == tfoot || cur == thead) {
            if (maybeTableCell != null) {
              targetTableCell = maybeTableCell.cast();
              break;
            }
          }
          
          String tagName = cur.getTagName();
          if (TableCellElement.TAG_TD.equalsIgnoreCase(tagName)
              || TableCellElement.TAG_TH.equalsIgnoreCase(tagName)) {
            /*
             * Found a table cell, but we can't return yet because it may be part
             * of a sub table within the a CellTable cell.
             */
            maybeTableCell = cur;
          }
          // Iterate.
          cur = cur.getParentElement();
        }
        
        return targetTableCell;
    }
    
    // Default setWidth function doesn't work. 
    // Sort event sets the the width of this element which causes multiple scrollers to appear
    // Hopefully this is stable for all browsers
    @Override
    public void setTableWidth(double value, Unit unit) {
    	getTableHeadElement().getParentElement().getParentElement().getParentElement().getParentElement().getStyle().setWidth(value, unit);
      }
    
    public Element getHorizontalScrollableElement() {
    	Element currElement = getTableHeadElement();
    	Element oldElement = currElement;
    	while(true) {
    		if(currElement != null) {
    			oldElement = currElement;
    			currElement = currElement.getParentElement();
    			if(currElement.hasClassName("dataPanelHoverDetector")) {
    				break;
    			}
    		}else {
    			currElement = oldElement;
    			break;
    		}
    	}
    	return currElement;
    }
    
}
