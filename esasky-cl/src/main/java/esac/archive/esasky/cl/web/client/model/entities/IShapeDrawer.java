package esac.archive.esasky.cl.web.client.model.entities;

import java.util.List;
import java.util.Set;

import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;

public interface IShapeDrawer {

	public void setColor(String color);
	public void setSizeRatio(double size);
	public double getSize();
	public void removeAllShapes();
	public void addShapes(TapRowList shapeList, GeneralJavaScriptObject javaScriptObject);
	public void selectShapes(Set<ShapeId> shapesToSelect);
    public void deselectShapes(Set<ShapeId> shapesToDeselect);
    public void deselectAllShapes();
    public void showShape(int shapeId);
    public void showShapes(List<Integer> shapeIds);
    public void showAndHideShapes(List<Integer> shapesIdsToShow, List<Integer> shapeIdsToHide);
    public void hideShape(int rowId);
    public void hideShapes(List<Integer> shapeIds);
    public void hideAllShapes();
    public void setShapeBuilder(ShapeBuilder shapeBuilder);
    public void hoverStart(int hoveredShapeId);
    public void hoverStop(int hoveredShapeId);
    public String getShapeType();
    public void setShapeType(String shapeType);
}
