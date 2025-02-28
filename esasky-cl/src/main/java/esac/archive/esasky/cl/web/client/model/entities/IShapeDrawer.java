/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
	public void addShapes(GeneralJavaScriptObject javaScriptObject, GeneralJavaScriptObject metadata);
	public void selectShapes(int shapeId);
    public void deselectShapes(int shapeId);
    public void deselectAllShapes();
    public LinkedList<Integer>  selectShapes(String shapeName);
    public LinkedList<Integer> deselectShapes(String shapeName);
    public int getNumberOfShapes();
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
