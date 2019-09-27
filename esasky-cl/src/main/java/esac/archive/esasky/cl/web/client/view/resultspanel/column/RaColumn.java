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
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.StringFilterDialogBox;

public class RaColumn extends SortableColumn<String>{

	private final StringFilterDialogBox stringFilter;
	
	public RaColumn(String label, String filterButtonId, RowsFilterObserver rowsFilterObserver){
		super(label, new TextCell(), rowsFilterObserver);
		this.stringFilter = new StringFilterDialogBox(label, filterButtonId, new FilterObserver() {
			
			@Override
			public void onNewFilter() {
				filter();
			}
		});
	}

	@Override
	public String getValue(TableRow row) {
        String value = "";
        for (TableElement element : row.getElements()) {
            if (label.equals(element.getLabel())) {
                value = element.getValue();
            }
        }

        String s = "";
        if (!"".equals(value)) {
            double raDeg = Double.parseDouble(value);
            if (raDeg < 0) {
                raDeg = Math.abs(raDeg);
                s += "-";
            }
            // conversion to hours (with decimal)
            Double conversion2Hours = raDeg / 15.0;
            String conversion2HoursString = Double.toString(conversion2Hours);
            String[] conversion2HoursStringToken = conversion2HoursString
                    .split("\\.");
            // hours (whole number)
            Integer hours = Integer.parseInt(conversion2HoursStringToken[0]);

            // conversion to minutes (with decimal)
            Double decimalPart4Minutes = (conversion2Hours - hours);
            Double conversion2Minutes = decimalPart4Minutes * 60;
            // minutes (whole number)
            Integer minutes = Integer.parseInt((Double
                    .toString(conversion2Minutes)).split("\\.")[0]);

            // seconds
            Double decimalPart4Seconds = (conversion2Minutes - minutes);
            Double seconds = decimalPart4Seconds * 60;

            s += NumberFormat.getFormat("00").format(hours) + "h ";
            s += NumberFormat.getFormat("00").format(minutes) + "m ";
            s += NumberFormat.getFormat("00.00").format(seconds) + "s";
        }
        return s;
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
	}

	@Override
	public void showFilter() {
		stringFilter.show();
	}
	
	@Override 
	public void ensureCorrectFilterButtonStyle() {
		stringFilter.ensureCorrectFilterButtonStyle();
	}
	
	@Override
	protected void applyFilterOnNewDataSet() {
		if(stringFilter.isFilterActive()) {
			filter();
		}
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

}
