package esac.archive.esasky.cl.web.client.model.entities;

import esac.archive.esasky.cl.web.client.model.Shape;
import esac.archive.esasky.cl.web.client.model.TapRowList;

public interface ShapeBuilder {
	Shape buildShape(int rowId, TapRowList rowList);
}
