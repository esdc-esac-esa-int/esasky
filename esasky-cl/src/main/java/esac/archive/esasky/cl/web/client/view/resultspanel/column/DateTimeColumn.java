package esac.archive.esasky.cl.web.client.view.resultspanel.column;


import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.i18n.client.DateTimeFormat;

import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.model.TableElement;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.view.resultspanel.RowsFilterObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.DateFilterDialogBox;

public class DateTimeColumn extends SortableColumn<String>{
	
	private final DateFilterDialogBox dateFilter;
	
	public DateTimeColumn(String label, String filterButtonId, RowsFilterObserver rowsFilterObserver){
		super(label, new TextCell(), rowsFilterObserver);
		this.dateFilter = new DateFilterDialogBox(label, filterButtonId, new FilterObserver() {
			
			@Override
			public void onNewFilter() {
				filter();
			}
		});
	}

	@Override
	public String getValue(TableRow row) {
        for (TableElement element : row.getElements()) {
            if (label.equals(element.getLabel())) {
                return element.getValue();
            }
        }
        return "";
	}
	
	private boolean match(String date, String minDate, String maxDate) {
		if(date.substring(0, 10).compareTo(minDate) < 0
				|| date.substring(0, 10).compareTo(maxDate) > 0) {
			return false;
		}
		return true;
	}
	
	protected void filter() {
		Set<Integer> rowsIdsToRemove = new HashSet<Integer>();
		Set<Integer> rowsIdsToAdd = new HashSet<Integer>();
		
		Iterator<TableRow> iterator = originalRows.iterator();
		while (iterator.hasNext()) {
			TableRow row = iterator.next();
			if (match(getElement(row), dateFilter.getCurrentFromDate(), dateFilter.getCurrentToDate()) 
					&& removedRowIds.contains(row.getShapeId())) {  
				removedRowIds.remove(row.getShapeId());
				rowsIdsToAdd.add(row.getShapeId());
				setDirty();
			} else if (!match(getElement(row), dateFilter.getCurrentFromDate(), dateFilter.getCurrentToDate()) 
					&& !removedRowIds.contains(row.getShapeId())){
				rowsIdsToRemove.add(row.getShapeId());
				removedRowIds.add(row.getShapeId());
				setDirty();
			}
		}
		
		if(isTableDirty()) {
			notifyDirty(rowsIdsToRemove, rowsIdsToAdd);
		}
	}
	
	@Override
	protected void applyFilterOnNewDataSet() {
		String startDate= DateTimeFormat.getFormat("yyyy-MM-dd").format(new Date());
		String endDate = "1800-01-01";
		for(TableRow row : originalRows) {
			String element = getElement(row);
			if(!element.equals("")) {
				if(element.compareTo(startDate) < 0) {
					startDate = element.substring(0, 10);
				}
				if(element.compareTo(endDate) > 0) {
					endDate = element.substring(0, 10);
				}
			}
		}

		dateFilter.setStartRange(startDate, endDate);
		filter();
	}
	
	private String getElement(TableRow row) {
		for (TableElement element : row.getElements()) {
			if (label.equals(element.getLabel())) {
				String elementValue = element.getValue();
				if ((elementValue == null || elementValue.equals("null"))) {
					elementValue = "";
				}
				return elementValue;
			}
		}
		throw new IllegalArgumentException("Table Element not found");
	}
	
	@Override
	public void showFilter() {
		dateFilter.show();
	}
	
	@Override 
	public void ensureCorrectFilterButtonStyle() {
		dateFilter.ensureCorrectFilterButtonStyle();
	}
	
	@Override
	protected int compare(String object1, String object2) {
		if (("".equals(object1) && "".equals(object2)) || (null == object1 && null == object2)) {
			return 0;
		}
		if ("".equals(object1) || null == object1) {
			return -1;
		}
		if ("".equals(object2) || null == object2) {
			return 1;
		}
		return object1.compareToIgnoreCase(object2);
	}
	
}
