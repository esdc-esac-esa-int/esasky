package esac.archive.esasky.cl.web.client.view.resultspanel;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TableColumnHelper;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.entities.ExtTapEntity;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.ImageColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.OnColorChangedCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.OnValueChangedCallback;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ExtTapTablePanel extends AbstractTablePanel {

	
	/** Current entity. */
	private ExtTapEntity entity;

	public ExtTapTablePanel(final String inputLabel, final String inputEsaSkyUniqID,
			final ExtTapEntity inputentity) {
		super(inputLabel, inputEsaSkyUniqID, inputentity);

		this.entity = inputentity;
		initView();

		addHoverFromDataPanelHandler();
	}

	protected void initView() {
		createCentreViewColumn();

		createMetadataColums();
		super.initView();
	}

	private void createCentreViewColumn() {
		ImageColumn centreViewColumn = new ImageColumn( 
				TextMgr.getInstance().getText("commonObservationTablePanel_centreOnObservation"),
				TableColumnHelper.resources.recenter().getSafeUri().asString());
		centreViewColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		centreViewColumn.setFieldUpdater(new FieldUpdater<TableRow, String>() {

			@Override
			public void update(final int index, final TableRow row,
					final String value) {
				AladinLiteWrapper.getInstance().goToTarget(
						row.getElementByTapName(entity.getDescriptor().getTapRaColumn())
						.getValue(), 
						row.getElementByTapName(entity.getDescriptor().getTapDecColumn())
						.getValue(),
						AladinLiteWrapper.getInstance().getFovDeg(), false,
						AladinLiteWrapper.getInstance().getCooFrame());
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TabRow_Recenter, getFullId(), row.getElementByTapName(getDescriptor().getUniqueIdentifierField()).getValue());
			}
		});

		this.table.addColumn(centreViewColumn, "");
		this.table.setColumnWidth(centreViewColumn,
				TableColumnHelper.COLUMN_WIDTH_ICON_DEFAULT_SIZE, Unit.PX);
	}
	
	@Override
	public final ExtTapDescriptor getDescriptor() {
		return entity.getDescriptor();
	}

	@Override
	public final ExtTapEntity getEntity() {
		return entity;
	}

	@Override
	public void showStylePanel(int x, int y) {

		if(stylePanel == null) {

			stylePanel = new StylePanel(getEntity().getEsaSkyUniqId(), getEntity().getTabLabel(), 
					getDescriptor().getHistoColor(), getEntity().getSize(), null,
					null, null, null, null, null, null,
					new OnColorChangedCallback() {

				@Override
				public void onColorChanged(String color) {
					getDescriptor().setHistoColor(color);
				}
			}, new OnValueChangedCallback() {

				@Override
				public void onValueChanged(double value) {
					getEntity().setSizeRatio(value);
				}
			}, null,
					null, null, null,
					null, null);
		}		
		stylePanel.toggle();
		stylePanel.setPopupPosition(x, y);
	}
	
	@Override
	public void exportAsCSV() {

		String csvData = "data:text/csv;charset=utf-8,";
		final String separator = ";";

		// Adds headers to csv
		int addedCols = 0;
		for (int cellIndex = 0; cellIndex < table.getColumnCount(); cellIndex++) {
			final String label = getLabelTextFromHeader(table.getHeader(cellIndex));
			if (!label.isEmpty()) {
				csvData += ((addedCols == 0) ? "" : separator) + getLabelTextFromHeader(table.getHeader(cellIndex));
				addedCols++;
			}
			
		}
		csvData += "\n";

		// Adds data to csv
		TapRowList rowList = getEntity().getMetadata();
		for (int rowIndex = 0; rowIndex < rowList.getData().size(); rowIndex++) {
			for (int cellIndex = 0; cellIndex < rowList.getMetadata().size(); cellIndex++) {
				csvData += ((cellIndex == 0) ? "" : separator) + "\"";
				Object cell = rowList.getData().get(rowIndex).get(cellIndex);
				if(cell != null) {
					String cellStr = cell.toString();
					csvData += cellStr + "\"";
				}else {
					csvData += " \"";
				}
			}
			csvData += "\n";
		}

		UrlUtils.saveRawToFile(UrlUtils.getValidFilename(getEntity().getEsaSkyUniqId()) + ".csv", csvData);
	}
	
}
