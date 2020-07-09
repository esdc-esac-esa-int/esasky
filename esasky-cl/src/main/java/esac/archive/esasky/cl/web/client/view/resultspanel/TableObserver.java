package esac.archive.esasky.cl.web.client.view.resultspanel;

public interface TableObserver {
	public void numberOfShownRowsChanged(int numberOfShownRows);
	public void onSelection(ITablePanel selectedTablePanel);
	public void onUpdateStyle(ITablePanel panel);
}
