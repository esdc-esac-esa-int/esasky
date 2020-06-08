package esac.archive.esasky.cl.web.client.model.entities;

import java.util.List;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public interface IShapeDrawer {

	public void setPrimaryColor(String color);
	public void setSizeRatio(double size);
	public double getSize();
	public void removeAllShapes();
	public void addShapes(GeneralJavaScriptObject javaScriptObject);
	public void selectShapes(int shapeId);
    public void deselectShapes(int shapeId);
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
