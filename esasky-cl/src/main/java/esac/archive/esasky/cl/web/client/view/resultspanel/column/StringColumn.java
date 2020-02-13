package esac.archive.esasky.cl.web.client.view.resultspanel.column;


import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import com.google.gwt.cell.client.TextCell;

import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.model.TableElement;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.view.resultspanel.RowsFilterObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.FilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.StringFilterDialogBox;

public class StringColumn extends SortableColumn<String>{
	
	private final StringFilterDialogBox stringFilter;
	
	public StringColumn(String tapName, String label, String filterButtonId, RowsFilterObserver rowsFilterObserver){
		super(tapName, label, new TextCell(), rowsFilterObserver);
		this.stringFilter = new StringFilterDialogBox(tapName, label, filterButtonId, new FilterObserver() {
			
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
            	if(element.getValue() != null) {
            		return element.getValue();
            	}else {
            		return "";
            	}
            }
        }
        return "";
	}

	private boolean match(String value, String filter) {
		value = value.toLowerCase().trim();
		filter = filter.trim();
		if(!filter.contains("|")) {
			return value.contains(filter.toLowerCase());
		}
		String[] separateFilters = filter.split("\\|");
		for(String oneFilter : separateFilters) {
			if(value.contains(oneFilter.trim().toLowerCase())) {
				return true;
			} 
		}
		return false;
	}
	
	protected void filter() {
		String filter = stringFilter.getFilterString();
		Set<Integer> rowsIdsToRemove = new HashSet<Integer>();
		Set<Integer> rowsIdsToAdd = new HashSet<Integer>();
		
		Iterator<TableRow> iterator = originalRows.iterator();
		while (iterator.hasNext()) {
			TableRow row = iterator.next();
			if (match(getValue(row), filter) && removedRowIds.contains(row.getShapeId())) {  
				removedRowIds.remove(row.getShapeId());
				rowsIdsToAdd.add(row.getShapeId());
				setDirty();
			} else if (!match(getValue(row), filter) && !removedRowIds.contains(row.getShapeId())){
				rowsIdsToRemove.add(row.getShapeId());
				removedRowIds.add(row.getShapeId());
				setDirty();
			}
		}
		
		if(isTableDirty()) {
			notifyDirty(rowsIdsToRemove, rowsIdsToAdd);
		}
		
		String tapFilter = this.tapName  + " = \'" + filter + "\'";
		notifyFilterChanged(tapFilter);
	}
	
	@Override
	protected void applyFilterOnNewDataSet() {
		if(stringFilter.isFilterActive()) {
			filter();
		}
	}
	
	@Override
	public void showFilter() {
		stringFilter.show();
	}
	
	public void createFilter(Double min, Double max) {
		//DUMMY
	}
	
	@Override 
	public void ensureCorrectFilterButtonStyle() {
		stringFilter.ensureCorrectFilterButtonStyle();
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

	@Override
	protected FilterDialogBox getFilterBox() {
		return stringFilter;
	}
	
}
