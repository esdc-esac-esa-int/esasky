package esac.archive.esasky.cl.web.client.view.resultspanel;


import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TableColumnHelper;
import esac.archive.esasky.cl.web.client.model.TableElement;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.model.entities.EsaSkyEntity;
import esac.archive.esasky.cl.web.client.model.entities.ExtTapEntity;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.ImageColumn;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class ExtTapTablePanel extends AbstractTablePanel {

	
	/** Current entity. */
	private EsaSkyEntity entity;

	public ExtTapTablePanel(final String inputLabel, final String inputEsaSkyUniqID,
			final EsaSkyEntity inputentity) {
		super(inputLabel, inputEsaSkyUniqID, inputentity);

		this.entity = inputentity;
		initView();

//		String sourceLimitDescription = entity.getSourceLimitDescription();

//		notShowingCompleteDataSetText.setText(TextMgr.getInstance().getText(sourceLimitDescription).replace("$sourceLimit$", entity.getDescriptor().getShapeLimit() + ""));

		addHoverFromDataPanelHandler();
	}

	protected void initView() {
		createCentreViewColumn();

		createMetadataColumns();
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
				TableElement fovElem =  row.getElementByTapName(EsaSkyConstants.OBSCORE_FOV);
				TableElement regionElem =  row.getElementByTapName(EsaSkyConstants.OBSCORE_SREGION);
				
				double fov = AladinLiteWrapper.getInstance().getFovDeg();
				if(fovElem != null && fovElem.getValue() != ""  && regionElem != null && !regionElem.getValue().startsWith("POSITION")) {
					fov = Double.parseDouble(fovElem.getValue()) * 4;
				}
				
				AladinLiteWrapper.getInstance().goToTarget(
						row.getElementByTapName(entity.getDescriptor().getTapRaColumn())
						.getValue(), 
						row.getElementByTapName(entity.getDescriptor().getTapDecColumn())
						.getValue(),
						fov, false,
						AladinLiteWrapper.getInstance().getCooFrame());
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TabRow_Recenter, getFullId(), row.getElementByTapName(getDescriptor().getUniqueIdentifierField()).getValue());
			}
		});

		this.table.addColumn(centreViewColumn, "");
		this.table.setColumnWidth(centreViewColumn,
				TableColumnHelper.COLUMN_WIDTH_ICON_DEFAULT_SIZE, Unit.PX);
	}
	
	@Override
	public void insertData(List<TableRow> data, String url) {
		super.insertData(data, url);

		if (data.size() >= entity.getDescriptor().getShapeLimit()) {
			notShowingCompleteDataSetMouseOverDetector.setVisible(true);
		} else {
			notShowingCompleteDataSetMouseOverDetector.setVisible(false);
		}
	}

	
	@Override
	public final ExtTapDescriptor getDescriptor() {
		return (ExtTapDescriptor)entity.getDescriptor();
	}

	@Override
	public final EsaSkyEntity getEntity() {
		return entity;
	}

}
