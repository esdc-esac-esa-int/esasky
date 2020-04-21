package esac.archive.esasky.cl.web.client.view.resultspanel.column;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.cell.client.TextCell;

import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.model.TableElement;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.view.resultspanel.RowsFilterObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.FilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.IntegerFilterDialogBox;

public class IntegerColumn extends SortableColumn<String> {

	private IntegerFilterDialogBox intFilter;

	private final String filterButtonId;
	private final String tapName;
	
	private final int ITEM_THRESHOLD_BEFORE_SIGNIFICANT_RENDERING_TIME = 500;

	public IntegerColumn(String tapName, String label, String filterButtonId, RowsFilterObserver rowsFilterObserver) {
		super(tapName, label, new TextCell(), rowsFilterObserver);
		this.tapName = tapName;
		this.filterButtonId = filterButtonId;
	}

	@Override
	public String getValue(TableRow row) {
		return getElement(row);
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

	private boolean match(String value, int low, int high) {
		try {
			int intValue  = Integer.parseInt(value);
			return intValue >= low && intValue <= high;
		} catch (NumberFormatException exception) {
			return true;
		}
	}
	
	public void createFilter(Double min, Double max) {
		if (intFilter == null) {
			this.intFilter = new IntegerFilterDialogBox(tapName, label, filterButtonId, new FilterObserver() {
	
				@Override
				public void onNewFilter(String filter) {
					filter();
				}
			});
		}
		
		if(min != null && max != null) {
			intFilter.setRange(min.intValue(), max.intValue());
		}else {
			intFilter.setRange(0, 100);
		}
	}

	protected void filter() {
		Set<Integer> rowsIdsToRemove = new HashSet<Integer>();
		Set<Integer> rowsIdsToAdd = new HashSet<Integer>();
		Iterator<TableRow> iterator = originalRows.iterator();
		while (iterator.hasNext()) {
			TableRow row = iterator.next();
			if (match(getElement(row), intFilter.getCurrentLow(), intFilter.getCurrentHigh())
					&& removedRowIds.contains(row.getShapeId())) {
				removedRowIds.remove(row.getShapeId());
				rowsIdsToAdd.add(row.getShapeId());
				setDirty();
			} else if (!match(getElement(row), intFilter.getCurrentLow(), intFilter.getCurrentHigh())
					&& !removedRowIds.contains(row.getShapeId())) {
				rowsIdsToRemove.add(row.getShapeId());
				removedRowIds.add(row.getShapeId());
				setDirty();
			}
		}

		if (isTableDirty()) {
			notifyDirty(rowsIdsToRemove, rowsIdsToAdd);
		}
		
		String tapFilter = this.tapName  + " BETWEEN  " + Integer.toString(intFilter.getCurrentLow()) + 
				" AND " + Integer.toString(intFilter.getCurrentHigh());
		notifyFilterChanged(tapFilter);
		
		intFilter.setReRenderingWouldTakeSignificantTime( 
				(originalRows.size() - removedRowIds.size()) > ITEM_THRESHOLD_BEFORE_SIGNIFICANT_RENDERING_TIME);
	}

	@Override
	protected void applyFilterOnNewDataSet() {
		int minValue = Integer.MAX_VALUE;
		int maxValue = Integer.MIN_VALUE;

		int numValuesFound = 0;
		for (TableRow row : originalRows) {
			String value = getValue(row);
			if (!value.equals("")) {
				numValuesFound++;
				int doubleValue = new Integer(removeSpecialCharacters(value));
				minValue = Math.min(doubleValue, minValue);
				maxValue = Math.max(doubleValue, maxValue);
			}
		}
		if(numValuesFound == 0) {
			minValue = Integer.MAX_VALUE;
			maxValue = Integer.MIN_VALUE;
		}
		if (intFilter == null) {
			this.intFilter = new IntegerFilterDialogBox(tapName, label, filterButtonId, new FilterObserver() {

				@Override
				public void onNewFilter(String filter) {
					filter();
				}
			});
		}
		
		intFilter.setRange(minValue, maxValue);
		if(intFilter.isFilterActive()) {
			filter();
		}
	}

	
	private String removeSpecialCharacters(String value) {
		return value.replaceFirst("<", "");
	}

	@Override
	public void showFilter() {
		intFilter.show();
	}

	@Override
	public void ensureCorrectFilterButtonStyle() {
		intFilter.ensureCorrectFilterButtonStyle();
	}
	
	@Override
	protected String getValueForSorting(TableRow row) {
		for (TableElement element : row.getElements()) {
			if (getLabel().equals(element.getLabel())) {
				return removeSpecialCharacters(element.getValue());
			}
		}
		return "";
	}

	@Override
	protected int compare(String object1, String object2) {
		if(object1.equals("") && object2.equals("")
				|| (object1 == null && object2 == null)
				|| (object1.equalsIgnoreCase("null") && object2.equalsIgnoreCase("null"))
				) {
			return 0;
		}
		if(object1.equals("")
			|| (object1 == null)
			|| (object1.equalsIgnoreCase("null"))
			) {
			return 1;
		}
		if(object2.equals("")
			|| (object2 == null)
			|| (object2.equalsIgnoreCase("null"))
			) {
			return -1;
		}
		return Integer.parseInt(object1) > Integer.parseInt(object2) ? -1 : 1;
	}

	@Override
	protected FilterDialogBox getFilterBox() {
		return intFilter;
	}

}
