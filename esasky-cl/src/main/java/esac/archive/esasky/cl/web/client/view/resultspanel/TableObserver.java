package esac.archive.esasky.cl.web.client.view.resultspanel;

public interface TableObserver {
	void numberOfShownRowsChanged(int numberOfShownRows);
	void onSelection(ITablePanel selectedTablePanel);
	void onUpdateStyle(ITablePanel panel);
	void onDataLoaded(int numberOfRows);
}
