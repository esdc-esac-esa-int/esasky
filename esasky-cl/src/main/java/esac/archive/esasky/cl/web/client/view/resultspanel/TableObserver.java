package esac.archive.esasky.cl.web.client.view.resultspanel;

import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

import java.util.List;

public interface TableObserver {
	default void numberOfShownRowsChanged(int numberOfShownRows) {}
	default void onSelection(ITablePanel selectedTablePanel) {}
	default void onUpdateStyle(ITablePanel panel) {}
	default void onDataLoaded(int numberOfRows) {}
	default void onRowSelected(GeneralJavaScriptObject row) {}
	default void onRowDeselected(GeneralJavaScriptObject row) {}
	default void onDataFilterChanged(List<Integer> filteredIndexList) {}

}
