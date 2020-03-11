package esac.archive.esasky.cl.web.client.model.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.SourceShape;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;

public class CombinedSourceFootprintDrawer implements IShapeDrawer{

	public static final int DEFAULT_LINEWIDTH = 2;
    public static final int DEFAULT_SOURCE_SIZE = 8;
    private static final int MAX_SOURCE_SIZE = 40;
	public static final int MAX_LINEWIDTH = 12;

	private double ratio = DEFAULT_LINEWIDTH / MAX_LINEWIDTH;
	private JavaScriptObject sourceOverlay;
	private JavaScriptObject footPrintOverlay;
	private ArrayList<Shape> sourceShapes = new ArrayList<>();
	private ArrayList<Shape> footPrintshapes = new ArrayList<>();
	private ArrayList<Integer[]> allShapesIndexes = new ArrayList<>();
	private ShapeBuilder shapeBuilder;


	public CombinedSourceFootprintDrawer(JavaScriptObject sourceOverlay, JavaScriptObject footPrintOverlay , ShapeBuilder shapeBuilder) {
		this.sourceOverlay = sourceOverlay;
		this.footPrintOverlay = footPrintOverlay;
		this.shapeBuilder = shapeBuilder;
	}

	@Override
	public void setColor(String color) {
		AladinLiteWrapper.getAladinLite().setOverlayColor(sourceOverlay, color);
		AladinLiteWrapper.getAladinLite().setOverlayColor(footPrintOverlay, color);
	}

	@Override
	public void setSizeRatio(double size) {
		ratio = size;
		AladinLiteWrapper.getAladinLite().setCatalogSourceSize(sourceOverlay, (int) Math.max(1, ratio * MAX_SOURCE_SIZE));
		AladinLiteWrapper.getAladinLite().setOverlayLineWidth(footPrintOverlay, (int) Math.max(1, ratio * MAX_LINEWIDTH));

	}
	
	@Override
    public double getSize() {
		return ratio;
    }

	@Override
	public void removeAllShapes() {
        AladinLiteWrapper.getAladinLite().removeAllSourcesFromCatalog(sourceOverlay);
        AladinLiteWrapper.getAladinLite().removeAllFootprintsFromOverlay(footPrintOverlay);
        sourceShapes.clear();
        footPrintshapes.clear();
	}

	@Override
	public void addShapes(TapRowList rowList, GeneralJavaScriptObject javaScriptObject) {
		removeAllShapes();
		
		if(Modules.useTabulator) {
			GeneralJavaScriptObject rows = javaScriptObject.invokeFunction("getRows", null);
			GeneralJavaScriptObject [] rowArray = GeneralJavaScriptObject.convertToArray(rows);
			
			for(int i = 0; i < rowArray.length; i++) {
				Shape shape = shapeBuilder.buildShape(i, null, rowArray[i]);
				if( shape instanceof SourceShape) {
					sourceShapes.add(shape);
					allShapesIndexes.add(new Integer[] {sourceShapes.size()-1, -1});
				} else {
					footPrintshapes.add(shape);
					allShapesIndexes.add(new Integer[] {-1, footPrintshapes.size()-1});
				}
			}
		} else {
			for(int i = 0; i < rowList.getData().size(); i++) {
				Shape shape = shapeBuilder.buildShape(i, rowList, null);
				if( shape instanceof SourceShape) {
					sourceShapes.add(shape);
					allShapesIndexes.add(new Integer[] {sourceShapes.size()-1, -1});
				} else {
					footPrintshapes.add(shape);
					allShapesIndexes.add(new Integer[] {-1, footPrintshapes.size()-1});
				}
			}
		}
		
		if(sourceShapes.size() > 0) {
			AladinLiteWrapper.getAladinLite().addCatalogToAladin(sourceOverlay);
		}
		
		
		for (Shape shape : sourceShapes) {
			AladinLiteWrapper.getAladinLite().newApi_addSourceToCatalogue(sourceOverlay, shape.getJsObject());
		}
		for (Shape shape : footPrintshapes) {
			AladinLiteWrapper.getAladinLite().addFootprintToOverlay(footPrintOverlay, shape.getJsObject());
		}
	}
	
	@Override
	public void selectShapes(Set<ShapeId> shapesToSelect) {
		if(shapesToSelect.size() == 0) {
			deselectAllShapes();
		}
		
		for (ShapeId shapeId : shapesToSelect) {
			Integer[] index = allShapesIndexes.get(shapeId.getShapeId());
			
			if(index[0] != -1) {
				AladinLiteWrapper.getAladinLite().selectShape(sourceShapes.get(index[0]).getJsObject());
			}else {
				AladinLiteWrapper.getAladinLite().selectShape(footPrintshapes.get(index[1]).getJsObject());
			}
		}
	}
	
	@Override
	public void deselectShapes(Set<ShapeId> shapesToDeSelect) {
    	for (ShapeId shapeId : shapesToDeSelect) {
			Integer[] index = allShapesIndexes.get(shapeId.getShapeId());
			
			if(index[0] != -1) {
				AladinLiteWrapper.getAladinLite().deselectShape(sourceShapes.get(index[0]).getJsObject());
			}else {
				AladinLiteWrapper.getAladinLite().deselectShape(footPrintshapes.get(index[1]).getJsObject());
			}
		}
	}

	@Override
	public void deselectAllShapes() {
		AladinLiteWrapper.getAladinLite().cleanSelectionOnCatalogue(sourceOverlay);
		
		for (Shape currentFootPol : footPrintshapes) {
			AladinLiteWrapper.getAladinLite().deselectShape(currentFootPol.getJsObject());
		}
	}
	

    @Override
    public void hideShape(int shapeId) {
        Integer[] index = allShapesIndexes.get(shapeId);
		
		if(index[0] != -1) {
			AladinLiteWrapper.getAladinLite().hideShape(sourceShapes.get(index[0]).getJsObject());
		}else {
			AladinLiteWrapper.getAladinLite().hideShape(footPrintshapes.get(index[1]).getJsObject());
		}
    }
    
    @Override
    public void hideShapes(List<Integer> shapeIds) {
        for(int id : shapeIds) {
        	Integer[] index = allShapesIndexes.get(id);
    		
    		if(index[0] != -1) {
    			AladinLiteWrapper.getAladinLite().hideShape(sourceShapes.get(index[0]).getJsObject());
    		}else {
    			AladinLiteWrapper.getAladinLite().hideShape(footPrintshapes.get(index[1]).getJsObject());
    		}
        }
    }
    
    @Override
    public void showShape(int shapeId) {
        Integer[] index = allShapesIndexes.get(shapeId);
		
		if(index[0] != -1) {
			AladinLiteWrapper.getAladinLite().showShape(sourceShapes.get(index[0]).getJsObject());
		}else {
			AladinLiteWrapper.getAladinLite().showShape(footPrintshapes.get(index[1]).getJsObject());
		}
    }
    
    @Override
    public void showShapes(List<Integer> shapeIds) {
        for(int id : shapeIds) {
        	Integer[] index = allShapesIndexes.get(id);
    		
    		if(index[0] != -1) {
    			AladinLiteWrapper.getAladinLite().showShape(sourceShapes.get(index[0]).getJsObject());
    		}else {
    			AladinLiteWrapper.getAladinLite().showShape(footPrintshapes.get(index[1]).getJsObject());
    		}
        }
    }
    
    @Override
    public void showAndHideShapes(List<Integer> shapeIdsToShow, List<Integer> shapeIdsToHide) {
        hideShapes(shapeIdsToHide);
        showShapes(shapeIdsToShow);
    }
    
    
    @Override
	public void hoverStart(int shapeId) {
        Integer[] index = allShapesIndexes.get(shapeId);
		if(index[0] != -1) {
			AladinLiteWrapper.getAladinLite().hoverStart(sourceShapes.get(index[0]).getJsObject());
		}else {
			AladinLiteWrapper.getAladinLite().hoverStart(footPrintshapes.get(index[1]).getJsObject());
		}
		
	}
	
	@Override
	public void hoverStop(int shapeId) {
        Integer[] index = allShapesIndexes.get(shapeId);
		
		if(index[0] != -1) {
			AladinLiteWrapper.getAladinLite().hoverStop(sourceShapes.get(index[0]).getJsObject());
		}else {
			AladinLiteWrapper.getAladinLite().hoverStop(footPrintshapes.get(index[1]).getJsObject());
		}
	}

	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		this.shapeBuilder = shapeBuilder;
	}
	
}
