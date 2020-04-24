package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.HashSet;
import java.util.List;

import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;

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
    public void onRowSelection(final int rowId) {
        //Do nothing
    }
    
    @Override
    public void onRowDeselection(int rowId) {
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
		HashSet<ShapeId> shapes = new HashSet<ShapeId>();
		shapes.add(new ShapeId() {
			
			@Override
			public int getShapeId() {
				return 0;
			}
		});
		entity.selectShapes(shapes);
	}
	
}
