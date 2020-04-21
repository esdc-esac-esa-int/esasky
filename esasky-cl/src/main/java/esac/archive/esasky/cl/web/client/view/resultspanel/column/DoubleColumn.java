package esac.archive.esasky.cl.web.client.view.resultspanel.column;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.i18n.client.NumberFormat;

import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.model.TableElement;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.view.resultspanel.RowsFilterObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.DoubleFilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.FilterDialogBox;

public class DoubleColumn extends SortableColumn<String> {

	private DoubleFilterDialogBox doubleFilter;

	private final String tapName;
	private final String filterButtonId;
	private NumberFormat numberFormat;
	private NumberFormat scientificNumberFormat;
	private int precision;
	
	private final int ITEM_THRESHOLD_BEFORE_SIGNIFICANT_RENDERING_TIME = 500;

	public DoubleColumn(String tapName, String label, String filterButtonId, RowsFilterObserver rowsFilterObserver) {
		super(tapName, label, new TextCell(), rowsFilterObserver);
		this.tapName = tapName;
		this.filterButtonId = filterButtonId;
	}

	@Override
	public String getValue(TableRow row) {
		String value = getElement(row);
		if (value.equals("") || value.equals("0")) {
			return value;
		}
		String prefix = "";
		if(value.contains("<")) {
			prefix = "< ";
			value = removeSpecialCharacters(value);
		}
		if (Math.abs(Double.parseDouble(value)) < (1 / Math.pow(10, precision))) {
			if(scientificNumberFormat.format(Double.parseDouble(value)).equals("0E0")){
				return prefix + "0";
			}
			return prefix + scientificNumberFormat.format(Double.parseDouble(value));
		}
		if(value.equals("-Infinity")) {
			return prefix + value;
		}
		return prefix + numberFormat.format(Double.parseDouble(value));
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

	private int getPrecision(TableRow row) {
		for (TableElement element : row.getElements()) {
			if (label.equals(element.getLabel()) && element.getMaxDecimalDigits() != null) {
				return element.getMaxDecimalDigits();
			}
		}
		int maxNumberOfDecimals = 0;
		for (TableRow originalRrow : originalRows) {
			String element = getElement(originalRrow);
			if (!element.equals("")) {
				maxNumberOfDecimals = Math.max(getMaxNumberOfDecimals(element), maxNumberOfDecimals);
			}
		}
		return maxNumberOfDecimals;
	}

	private boolean match(String value, double low, double high) {
		double doubleValue;
		try {
			doubleValue = new Double(removeSpecialCharacters(value));
			if (Math.abs(doubleValue) < (1 / Math.pow(10, precision))) {
				doubleValue = new Double(scientificNumberFormat.format(doubleValue));
				return doubleValue >= new Double(scientificNumberFormat.format(low)) && doubleValue <= new Double(scientificNumberFormat.format(high));
			} else {
				doubleValue = new Double(numberFormat.format(doubleValue));
				return doubleValue >= new Double(numberFormat.format(low)) && doubleValue <= new Double(numberFormat.format(high));
			}
		} catch (NumberFormatException exception) {
			return true;
		}

	}

	protected void filter() {
		Set<Integer> rowsIdsToRemove = new HashSet<Integer>();
		Set<Integer> rowsIdsToAdd = new HashSet<Integer>();
		Iterator<TableRow> iterator = originalRows.iterator();
		while (iterator.hasNext()) {
			TableRow row = iterator.next();
			if (match(getElement(row), doubleFilter.getCurrentLow(), doubleFilter.getCurrentHigh())
					&& removedRowIds.contains(row.getShapeId())) {
				removedRowIds.remove(row.getShapeId());
				rowsIdsToAdd.add(row.getShapeId());
				setDirty();
			} else if (!match(getElement(row), doubleFilter.getCurrentLow(), doubleFilter.getCurrentHigh())
					&& !removedRowIds.contains(row.getShapeId())) {
				rowsIdsToRemove.add(row.getShapeId());
				removedRowIds.add(row.getShapeId());
				setDirty();
			}
		}

		if (isTableDirty()) {
			notifyDirty(rowsIdsToRemove, rowsIdsToAdd);
		}
		
		String tapFilter = this.tapName  + " BETWEEN  " + Double.toString(doubleFilter.getCurrentLow()) + 
				" AND " + Double.toString(doubleFilter.getCurrentHigh());
		notifyFilterChanged(tapFilter);
		
		doubleFilter.setReRenderingWouldTakeSignificantTime( 
				(originalRows.size() - removedRowIds.size()) > ITEM_THRESHOLD_BEFORE_SIGNIFICANT_RENDERING_TIME);
	}
	
	public void createFilter(Double min, Double max) {
		if (doubleFilter == null) {
			this.doubleFilter = new DoubleFilterDialogBox(tapName, label, filterButtonId, new FilterObserver() {
	
				@Override
				public void onNewFilter(String filter) {
					filter();
				}
			});
		}
		
		updateNumberFormat();
		
		if(min != null && max != null) {
			doubleFilter.setRange(min, max, numberFormat, precision);
		}else {
			doubleFilter.setRange(0.0, 100.0, numberFormat, precision);
		}
	}

	private int getMaxNumberOfDecimals(String number) {
		String[] numberParts = number.split("\\.");
		if (numberParts.length > 1) {
			String digitsAfterDot = numberParts[1];
			if (digitsAfterDot.toLowerCase().contains("e")) {
				digitsAfterDot = digitsAfterDot.substring(0, digitsAfterDot.toLowerCase().indexOf("e"));
			}
			return digitsAfterDot.length();
		} else {
			return 0;
		}
	}

	@Override
	protected void applyFilterOnNewDataSet() {
		updateNumberFormat();
		double minValue = Double.POSITIVE_INFINITY;
		double maxValue = Double.NEGATIVE_INFINITY;

		int numValuesFound = 0;
		for (TableRow row : originalRows) {
			String value = getValue(row);
			if (!value.equals("")) {
				numValuesFound++;
				double doubleValue = new Double(removeSpecialCharacters(value));
				minValue = Math.min(doubleValue, minValue);
				maxValue = Math.max(doubleValue, maxValue);
			}
		}
		
		if(numValuesFound == 0) {
			minValue = Double.NEGATIVE_INFINITY;
			maxValue = Double.POSITIVE_INFINITY;
		}
		if (doubleFilter == null) {
			this.doubleFilter = new DoubleFilterDialogBox(tapName, label, filterButtonId, new FilterObserver() {

				@Override
				public void onNewFilter(String filter) {
					filter();
				}
			});
		}
		
		doubleFilter.setRange(minValue, maxValue, numberFormat, precision);
		if(doubleFilter.isFilterActive()) {
			filter();
		}
	}

	private void updateNumberFormat() {
		StringBuilder numberPattern = new StringBuilder("0");
		if(originalRows != null && originalRows.size() > 0) {
			precision = getPrecision(originalRows.get(0));
		}else {
			precision = 3;
		}
		
		if(precision > 0) {
			numberPattern.append(".");
			for (int i = 0; i < precision; i++) {
				numberPattern.append("#");
			}
		}
		numberFormat = NumberFormat.getFormat(numberPattern.toString());
		scientificNumberFormat = NumberFormat.getFormat(numberPattern.append("E0").toString());
	}
	
	private String removeSpecialCharacters(String value) {
		return value.replaceFirst("<", "");
	}

	@Override
	public void showFilter() {
		doubleFilter.show();
	}

	@Override
	public void ensureCorrectFilterButtonStyle() {
		doubleFilter.ensureCorrectFilterButtonStyle();
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
		return Double.parseDouble(object1) > Double.parseDouble(object2) ? -1 : 1;
	}

	@Override
	protected FilterDialogBox getFilterBox() {
		return doubleFilter;
	}

}
