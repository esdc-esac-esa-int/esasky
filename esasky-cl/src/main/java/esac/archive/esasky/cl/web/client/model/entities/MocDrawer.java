package esac.archive.esasky.cl.web.client.model.entities;

import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;

import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class MocDrawer implements IShapeDrawer{

	public static final int DEFAULT_LINEWIDTH = 2;
    public static final int DEFAULT_SOURCE_SIZE = 8;
	public static final int MAX_LINEWIDTH = 12;

	private double ratio = DEFAULT_LINEWIDTH / MAX_LINEWIDTH;
	JavaScriptObject moc;


	public MocDrawer(String color) {
		String options = "{\"opacity\":0.2, \"color\":\"" + color + "\"}";
		moc = AladinLiteWrapper.getAladinLite().createMOC(options);
	}

	@Override
	public void setPrimaryColor(String color) {
		AladinLiteWrapper.getAladinLite().setOverlayColor(moc, color);
	}

	@Override
	public void setSizeRatio(double size) {
		ratio = size;
		AladinLiteWrapper.getAladinLite().setMOCOpacity(moc, Math.min(1, ratio));
	}
	
	@Override
    public double getSize() {
		return ratio;
    }

	@Override
	public void removeAllShapes() {
		if(moc != null) {
			AladinLiteWrapper.getAladinLite().clearMOC(moc);
		}
	}

	@Override
	public void addShapes(TapRowList rowList, GeneralJavaScriptObject javaScriptObject) {
		removeAllShapes();
		
		if(rowList != null) {

			int healpixOrderIndex = rowList.getColumnIndex(EsaSkyConstants.HEALPIX_ORDER);
			int healpixIndex = rowList.getColumnIndex(EsaSkyConstants.HEALPIX_IPIX);
			if(healpixIndex != -1) {
				String healpixOrder = "";
				boolean first = true;
				String mocJSON = "{";
				for(int i = 0; i < rowList.getData().size(); i++) {
					String currentOrder = (String) rowList.getDataRow(i).get(healpixOrderIndex);
					if(currentOrder != healpixOrder) {
						healpixOrder = currentOrder;
						if(!first) {
							mocJSON += "], ";
						}
						mocJSON += "\" " + healpixOrder + "\":[";
						first = false;
					}
					mocJSON += rowList.getDataRow(i).get(healpixIndex) + ",";
				}
				mocJSON = mocJSON.substring(0,mocJSON.length()-1) + "]}";
				AladinLiteWrapper.getAladinLite().addMOCData(moc, mocJSON);
				AladinLiteWrapper.getAladinLite().addMOC(moc);
			}
		}
	}
	
	@Override
	public void selectShapes(Set<ShapeId> shapesToSelect) {
		//TODO
	}
	
	@Override
	public void deselectShapes(Set<ShapeId> shapesToDeSelect) {
		//TODO
	}

	@Override
	public void deselectAllShapes() {
		//TODO
	}

    @Override
    public void hideShape(int shapeId) {
		//TODO
    }
    
    @Override
    public void hideShapes(List<Integer> shapeIds) {
		//TODO
    }

    @Override
    public void hideAllShapes() {
    	//TODO
    }
    
    @Override
    public void showShape(int shapeId) {
		//TODO
    }
    
    @Override
    public void showShapes(List<Integer> shapeIds) {
		//TODO
    }
    
    @Override
    public void showAndHideShapes(List<Integer> shapeIdsToShow, List<Integer> shapeIdsToHide) {
		//TODO
    }
    
    @Override
	public void hoverStart(int shapeId) {
		//TODO
	}
	
	@Override
	public void hoverStop(int shapeId) {
		//TODO
	}

	@Override
	public void setShapeBuilder(ShapeBuilder shapeBuilder) {
		//TODO
	}

    @Override
    public String getShapeType() {
        return null;
    }

    @Override
    public void setShapeType(String shapeType) {
    }

}
