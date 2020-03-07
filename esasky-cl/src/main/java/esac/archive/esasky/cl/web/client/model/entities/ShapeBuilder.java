package esac.archive.esasky.cl.web.client.model.entities;

import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;

public interface ShapeBuilder {
	Shape buildShape(int rowId, TapRowList rowList, GeneralJavaScriptObject row);
}
