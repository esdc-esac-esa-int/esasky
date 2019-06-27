package esac.archive.esasky.cl.web.client.view.resultspanel.column;


import com.google.gwt.user.cellview.client.Column;

import esac.archive.esasky.cl.web.client.model.EsaSkyButtonCell;
import esac.archive.esasky.cl.web.client.model.TableColumnHelper;
import esac.archive.esasky.cl.web.client.model.TableRow;

public class ImageColumn extends Column<TableRow, String>{

	private String image;
	public ImageColumn(String tooltip, String image){
		super(new EsaSkyButtonCell(tooltip, TableColumnHelper.TOOLTIP_DELAY_MS));
		this.image = image;
	}

            
	@Override
	public String getValue(TableRow row) {
		return image;
	}
}
