package esac.archive.esasky.cl.web.client.view.resultspanel.column;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;

import esac.archive.esasky.cl.web.client.model.TableElement;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.view.resultspanel.RowsFilterObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.FilterDialogBox;

public abstract class SortableColumn<C> extends Column<TableRow, C>{

	protected String label;
	protected List<TableRow> originalRows = new LinkedList<TableRow>();
	protected Set<Integer> removedRowIds = new HashSet<Integer>();
	
	private final RowsFilterObserver rowFilterObserver;
	
	private boolean isTableDirty;
	
	public SortableColumn(String label, Cell<C> cell, RowsFilterObserver rowsFilterObserver){
		super(cell);
		this.label = label;
		this.rowFilterObserver = rowsFilterObserver;
        setSortable(true);
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setColumnData(List<TableRow> rows) {
		originalRows = rows;
		removedRowIds.clear();
		applyFilterOnNewDataSet();
	}
	
	public boolean hasFilteredAwayId(Integer rowId) {
		return removedRowIds.contains(rowId);
	}
	
	public String getAdqlForFilterCondition() {
		return getFilterBox().getAdqlForFilterCondition();
	}
	
	protected abstract FilterDialogBox getFilterBox();
	
	protected void applyFilterOnNewDataSet() {
		filter();
	}
	
	protected abstract void filter();
	
	public abstract void showFilter();
	
	protected void notifyDirty(Set<Integer> rowsToRemove, Set<Integer> rowsToAdd) {
		rowFilterObserver.onRowsFiltered(rowsToRemove, rowsToAdd);
		isTableDirty = false;
	}
	
	protected boolean isTableDirty() {
		return isTableDirty;
	}
	
	protected void setDirty() {
		isTableDirty = true;
	}
	
	public abstract void ensureCorrectFilterButtonStyle();
	
	protected String getValueForSorting(TableRow row) {
		for (TableElement element : row.getElements()) {
			if (getLabel().equals(element.getLabel())) {
				return element.getValue();
			}
		}
		return "";
	}
	
	public int compare(TableRow o1, TableRow o2) {
		if (o1 == o2) {
			return 0;
		}

		if (o1 != null) {
			if (o2 == null) {
				return 1;
			}
			String o1Value = getValueForSorting(o1);
			String o2Value = getValueForSorting(o2);
			return compare(o1Value, o2Value);
		}
		return -1;
	}
	
	protected abstract int compare(String object1, String object2);
}
