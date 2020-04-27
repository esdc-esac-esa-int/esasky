package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.EsaSkyButtonCell;
import esac.archive.esasky.cl.web.client.model.TableColumnHelper;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.model.entities.CatalogEntity;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.DeviceUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;

public class SourcesTablePanel extends AbstractTablePanel {

	protected CatalogEntity entity;

	private final int limitNumber;

	public SourcesTablePanel(String label, String esaSkyUniqID, CatalogEntity catEntity) {
		super(label, esaSkyUniqID, catEntity);
		this.entity = catEntity;
		initView();

		limitNumber = getRowLimit();
		String sourceLimitDescription = catEntity.getDescriptor().getShapeLimitDescription();

		if (sourceLimitDescription.contains("|")) {
			String[] sourceLimitArr = sourceLimitDescription.split("\\|");
			notShowingCompleteDataSetText.getElement().setInnerHTML(TextMgr.getInstance().getText("sourceLimitDescription")
					.replace("$sourceLimit$", limitNumber + "")
					.replace("$orderBy$", TextMgr.getInstance().getText(sourceLimitArr[1]).toLowerCase()));
		} else {
			notShowingCompleteDataSetText.getElement().setInnerHTML(TextMgr.getInstance().getText(sourceLimitDescription).replace("$sourceLimit$", limitNumber + ""));
		}

		addHoverFromDataPanelHandler();
	}

	protected void initView() {
		createCentreViewColumn();
		createMetadataColumns();

		super.initView();
	}

	protected int getRowLimit() {
		return DeviceUtils.isMobile() ? EsaSkyWebConstants.MAX_SHAPES_FOR_MOBILE : entity.getDescriptor().getShapeLimit();
	}

	@Override
	public void insertData(List<TableRow> data, String url) {
		super.insertData(data, url);

		if (data.size() >= limitNumber) {
			notShowingCompleteDataSetMouseOverDetector.setVisible(true);
		} else {
			notShowingCompleteDataSetMouseOverDetector.setVisible(false);
		}
	}

	protected void createCentreViewColumn() {

		final Column<TableRow, String> centreViewColumn = new Column<TableRow, String>(
				new EsaSkyButtonCell(
						TextMgr.getInstance().getText("SourcesTablePanel_centreOnSource"),
						TableColumnHelper.TOOLTIP_DELAY_MS)) {

			@Override
			public String getValue(final TableRow row) {
				return TableColumnHelper.resources.recenter().getSafeUri().asString();
			}
		};

		centreViewColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		centreViewColumn.setFieldUpdater(new FieldUpdater<TableRow, String>() {

			@Override
			public void update(final int index, final TableRow row,
					final String value) {
				final String ra = row.getElementByTapName(getDescriptor().getTapRaColumn()).getValue();
				final String dec = row.getElementByTapName(getDescriptor().getTapDecColumn()).getValue();

				AladinLiteWrapper.getInstance().goToTarget(ra, dec, AladinLiteWrapper.getInstance().getFovDeg(), false,
						AladinLiteWrapper.getInstance().getCooFrame());
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TabRow_Recenter, getFullId(), row.getElementByTapName(getDescriptor().getUniqueIdentifierField()).getValue());

			}

		});
		this.table.addColumn(centreViewColumn, "");
		this.table.setColumnWidth(centreViewColumn, TableColumnHelper.COLUMN_WIDTH_ICON_DEFAULT_SIZE, Unit.PX);
	}

	@Override
	public CatalogDescriptor getDescriptor() {
		return entity.getDescriptor();
	}

	@Override
	public CatalogEntity getEntity() {
		return entity;
	}

}
