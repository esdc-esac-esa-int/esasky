package esac.archive.esasky.cl.web.client.model.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;

public class FootprintDrawer implements IShapeDrawer{

	public static final int DEFAULT_LINEWIDTH = 2;
	public static final int MAX_LINEWIDTH = 12;
	private double ratio = DEFAULT_LINEWIDTH / MAX_LINEWIDTH;
	
	private JavaScriptObject overlay;
	private ArrayList<Shape> shapes = new ArrayList<Shape>();
	private ShapeBuilder shapeBuilder;
	
	public FootprintDrawer(JavaScriptObject overlay, ShapeBuilder shapeBuilder) {
		this.overlay = overlay;
		this.shapeBuilder = shapeBuilder;
	}
		
	@Override
	public void setColor(String color) {
		AladinLiteWrapper.getAladinLite().setOverlayColor(overlay, color);
	}

	@Override
	public void setSizeRatio(double size) {
		ratio = size;
		AladinLiteWrapper.getAladinLite().setOverlayLineWidth(overlay, (int) Math.max(1, ratio * MAX_LINEWIDTH));
	}
	
	@Override
	public double getSize() {
		return ratio;
	}

	@Override
	public void removeAllShapes() {
		 AladinLiteWrapper.getAladinLite().removeAllFootprintsFromOverlay(overlay);
		 shapes.clear();
	}

	@Override
	public void addShapes(TapRowList rowList, GeneralJavaScriptObject javaScriptObject) {
        removeAllShapes();
        for (int i = 0; i < rowList.getData().size(); i++) {
        	Shape polygon = shapeBuilder.buildShape(i, rowList, null);
        	
        	AladinLiteWrapper.getAladinLite().addFootprintToOverlay(overlay, polygon.getJsObject());
        	shapes.add(i, polygon);
        }
	}
	
	@Override
	public void selectShapes(Set<ShapeId> shapesIds) {
    	if(shapesIds.size() == 0) {
    		deselectAllShapes();
    	}
    	
        for (ShapeId shape: shapesIds) {
        	AladinLiteWrapper.getAladinLite().selectJsFootprint(shapes.get(shape.getShapeId()).getJsObject());
        }
	}

	@Override
	public void deselectShapes(Set<ShapeId> shapesIds) {
    	for (ShapeId shape: shapesIds) {
    		AladinLiteWrapper.getAladinLite().deselectJsFootprint(shapes.get(shape.getShapeId()).getJsObject());
    	}

	}

	@Override
	public void deselectAllShapes() {
		for (Shape currentFootPol : shapes) {
			AladinLiteWrapper.getAladinLite().deselectJsFootprint(currentFootPol.getJsObject());
		}
	}

	@Override
	public void showShape(int shapeId) {
    	AladinLiteWrapper.getAladinLite().showFootprint(shapes.get(shapeId).getJsObject());
	}

	@Override
    public void showShapes(List<Integer> shapeIds) {
      for(int id : shapeIds) {
          AladinLiteWrapper.getAladinLite().showFootprint(shapes.get(id).getJsObject());
      }
	}

	@Override
	public void showAndHideShapes(List<Integer> shapeIdsToShow, List<Integer> shapeIdsToHide) {
        hideShapes(shapeIdsToHide);
        showShapes(shapeIdsToShow);
	}

	@Override
	public void hideShape(int shapeId) {
    	AladinLiteWrapper.getAladinLite().hideFootprint(shapes.get(shapeId).getJsObject());
	}

	@Override
    public void hideShapes(List<Integer> shapeIds) {
        for(int id : shapeIds) {
            AladinLiteWrapper.getAladinLite().hideFootprint(shapes.get(id).getJsObject());
        }
    }
    
	@Override
	public void hoverStart(int shapeId) {
		AladinLiteWrapper.getAladinLite().hoverStartJsFootprint(shapes.get(shapeId).getJsObject());
	}

	@Override
	public void hoverStop(int shapeId) {
		AladinLiteWrapper.getAladinLite().hoverStopJsFootprint(shapes.get(shapeId).getJsObject());
	}

	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		this.shapeBuilder = shapeBuilder;
	}
}

