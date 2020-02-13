package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.Set;

public interface RowsFilterObserver{
	public void onRowsFiltered(Set<Integer> rowsToRemove, Set<Integer> rowsToAdd);
	public void onFilterChanged(String filter);
}
