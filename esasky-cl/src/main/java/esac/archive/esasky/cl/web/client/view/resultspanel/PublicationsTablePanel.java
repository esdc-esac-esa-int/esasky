package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.List;

import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class PublicationsTablePanel extends TabulatorTablePanel {
    
    public PublicationsTablePanel(String label, String esaSkyUniqID, GeneralEntityInterface entity) {
        super(label, esaSkyUniqID, entity);
        selectTablePanel();
    }
    
    @Override
    public void onRowMouseEnter(int rowId) {
        //Do nothing
    }

    @Override
    public void onRowMouseLeave(int rowId) {
        //Do nothing
    }
    
    @Override
    public void hoverStartRow(int rowId) {
        //Do nothing
    }

    @Override
    public void hoverStopRow(int rowId) {
        //Do nothing
    }
    
    @Override
    public void onRowSelection(final GeneralJavaScriptObject row) {
        //Do nothing
    }
    
    @Override
    public void onRowDeselection(GeneralJavaScriptObject row) {
        //Do nothing
    }
    
    @Override
    public void onDataFiltered(List<Integer> indexArray) {
        //Do nothing
    }

	@Override
	public void deselectTablePanel() {
		super.deselectTablePanel();
		entity.deselectAllShapes();
	}
	
	@Override
	public void selectTablePanel() {
		super.selectTablePanel();
		entity.selectShapes(0);
	}
	
}
