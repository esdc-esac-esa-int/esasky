package esac.archive.esasky.cl.web.client.model.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;

public class SourceDrawer implements IShapeDrawer{

    public static final int DEFAULT_SOURCE_SIZE = 8;
    private static final int MAX_SOURCE_SIZE = 40;
    
	private JavaScriptObject overlay;
	private ArrayList<Shape> shapes;
	private ShapeBuilder shapeBuilder;

	public SourceDrawer(JavaScriptObject overlay, ShapeBuilder sourceAndPopupDetails) {
		this.overlay = overlay;
		this.shapeBuilder = sourceAndPopupDetails;
	}

	@Override
	public void setColor(String color) {
		AladinLiteWrapper.getAladinLite().setOverlayColor(overlay, color);
	}

	@Override
	public void setSizeRatio(double size) {
		AladinLiteWrapper.getAladinLite().setCatalogSourceSize(overlay, (int) Math.max(1, size * MAX_SOURCE_SIZE));
	}
	
	@Override
    public double getSize() {
		return AladinLiteWrapper.getAladinLite().getCatalogSourceSize(overlay) / MAX_SOURCE_SIZE;
    }

	@Override
	public void removeAllShapes() {
        AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(overlay);
        this.getShapes().clear();
	}

	@Override
	public void addShapes(TapRowList rowList, GeneralJavaScriptObject javaScriptObject) {
		List<Shape> shapes = new LinkedList<Shape>();
		for(int i = 0; i < rowList.getData().size(); i++) {
			shapes.add(shapeBuilder.buildShape(i, rowList, null));
		}
		
		removeAllShapes();
		AladinLiteWrapper.getAladinLite().addCatalogToAladin(overlay);
		
		for (Shape shape : shapes) {
			AladinLiteWrapper.getAladinLite().newApi_addSourceToCatalogue(overlay, shape.getJsObject());
			this.getShapes().add(shape);
		}
	}
	
	@Override
	public void selectShapes(Set<ShapeId> shapesToSelect) {
		if(shapesToSelect.size() == 0) {
			deselectAllShapes();
		}
		
		for (ShapeId sourceId : shapesToSelect) {
			AladinLiteWrapper.getAladinLite().selectSource(overlay, sourceId.getShapeId());
		}
	}
	
    private ArrayList<Shape> getShapes() {
        if (null == shapes) {
            this.shapes = new ArrayList<Shape>();
        }
        return shapes;
    }

	@Override
	public void deselectShapes(Set<ShapeId> shapes) {
    	for (ShapeId shape: shapes) {
    		AladinLiteWrapper.getAladinLite().deselectSourceFromCatalogue(overlay, shape.getShapeId());
    	}
	}

	@Override
	public void deselectAllShapes() {
		AladinLiteWrapper.getAladinLite().cleanSelectionOnCatalogue(overlay);
	}
	

    @Override
    public void hideShape(int shapeId) {
        //For one source is faster iterate over shapes than crating a map
        for (Shape currentFootPol : this.getShapes()) {
            if (currentFootPol.getShapeId() == shapeId) {
                AladinLiteWrapper.getAladinLite().hideSource(currentFootPol.getJsObject());
                break;
            }
        }
    }
    
    @Override
    public void hideShapes(List<Integer> shapeIds) {
    	HashMap<Integer, JavaScriptObject> shapesMap = getShapesMap();
        for(int id : shapeIds) {
            AladinLiteWrapper.getAladinLite().hideSource(shapesMap.get(id));
        }
    }
    
    private HashMap<Integer, JavaScriptObject> getShapesMap() {
        
        // TODO: This method provably could return a precomputed/stored map instead of calculating it each time. We need to analyze it.
        
        HashMap<Integer, JavaScriptObject> shapesMap = new HashMap<Integer, JavaScriptObject>();
        
        for (Shape currentFootPol : this.getShapes()) {
            shapesMap.put(currentFootPol.getShapeId(), currentFootPol.getJsObject());
        }
        
        return shapesMap;
    }

    @Override
    public void showShape(int shapeId) {
        for (Shape currentFootPol : this.getShapes()) {
            if (currentFootPol.getShapeId() == shapeId) {
                AladinLiteWrapper.getAladinLite().showSource(currentFootPol.getJsObject());
                break;
            }
        }
    }
    
    @Override
    public void showShapes(List<Integer> shapeIds) {
    	HashMap<Integer, JavaScriptObject> shapesMap = getShapesMap();
        for(int id : shapeIds) {
            AladinLiteWrapper.getAladinLite().showSource(shapesMap.get(id));
        }
    }
    
    @Override
    public void showAndHideShapes(List<Integer> shapeIdsToShow, List<Integer> shapeIdsToHide) {
        hideShapes(shapeIdsToHide);
        showShapes(shapeIdsToShow);
    }
    
    
    @Override
	public void hoverStart(int shapeId) {
		AladinLiteWrapper.getAladinLite().hoverStartJsSource(getShapes().get(shapeId).getJsObject());
	}
	
	@Override
	public void hoverStop(int shapeId) {
		AladinLiteWrapper.getAladinLite().hoverStopJsSource(getShapes().get(shapeId).getJsObject());
	}

	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		this.shapeBuilder = shapeBuilder;
	}
	
}
