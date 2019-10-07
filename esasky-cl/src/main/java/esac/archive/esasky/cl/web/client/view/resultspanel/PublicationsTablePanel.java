package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.HashSet;

import com.google.gwt.user.cellview.client.RowHoverEvent;

import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.entities.CatalogEntity;

public class PublicationsTablePanel extends SourcesTablePanel {
    
    public PublicationsTablePanel(String label, String esaSkyUniqID, CatalogEntity catEntity) {
        super(label, esaSkyUniqID, catEntity);
    }
    
    @Override
    protected void createCentreViewColumn() {
        //Do nothing
    }

	@Override
	public void clearSelectionModel() {
	    //Do nothing
	}
	
	@Override
	public void hoverStartEntity(RowHoverEvent hoverEvent) {
		//Do nothing
	}
	
	@Override
	public void hoverStopEntity(RowHoverEvent hoverEvent) {
		//Do nothing
	}
	
	public void hoverStartRow(int rowId) {
		if (rowId >= 0) {
			for (int i = 0; i < this.table.getVisibleItems().size(); i++) {
				if (this.table.getVisibleItem(i).getShapeId() == rowId) {
					setStyleName(this.table.getRowElement(i), "dataGridHoveredRowUserDefined", true);
					lastHoveredRowId = rowId;
					break;
				}
			}
		}
	}

	public void hoverStopRow(int rowId) {
		if (rowId >= 0) {
			for (int i = 0; i < this.table.getVisibleItems().size(); i++) {
				if (this.table.getVisibleItem(i).getShapeId() == rowId) {
			        setStyleName(this.table.getRowElement(i), "dataGridHoveredRowUserDefined", false);
					break;
				}
			}
		}
	}
	

	@Override
    protected int getRowLimit() {
    	return entity.getDescriptor().getAdsPublicationsMaxRows();
    }
	
	@Override
	public void selectRow(int rowId) {
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
