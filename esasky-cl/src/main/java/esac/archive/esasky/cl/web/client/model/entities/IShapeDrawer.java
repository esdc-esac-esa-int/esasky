package esac.archive.esasky.cl.web.client.model.entities;

import java.util.LinkedList;
import java.util.List;

import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public interface IShapeDrawer {

	public void setPrimaryColor(String color);
	public String getPrimaryColor();
	public void setSecondaryColor(String color);
	public String getSecondaryColor();
	public void setSizeRatio(double size);
	public double getSize();
	public void removeAllShapes();
	public void addShapes(GeneralJavaScriptObject javaScriptObject);
	public void selectShapes(int shapeId);
    public void deselectShapes(int shapeId);
    public void deselectAllShapes();
    public LinkedList<Integer>  selectShapes(String shapeName);
    public LinkedList<Integer> deselectShapes(String shapeName);
    public Shape getShape(int shapeId);
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
    public String getLineStyle();
    public void setLineStyle(String lineStyle);
}
