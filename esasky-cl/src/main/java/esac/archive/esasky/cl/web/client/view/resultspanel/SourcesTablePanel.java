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
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.OnCheckChangedCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.OnColorChangedCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.OnShapeChangedCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.OnValueChangedCallback;

public class SourcesTablePanel extends AbstractTablePanel {

	protected CatalogEntity entity;

	private final int limitNumber;

	public SourcesTablePanel(String label, String esaSkyUniqID, CatalogEntity catEntity) {
		super(label, esaSkyUniqID, catEntity);
		this.entity = catEntity;
		initView();

		limitNumber = getRowLimit();
		String sourceLimitDescription = catEntity.getDescriptor().getSourceLimitDescription();

		if (sourceLimitDescription.contains("|")) {
			String[] sourceLimitArr = sourceLimitDescription.split("\\|");
			notShowingCompleteDataSetText.setText(TextMgr.getInstance().getText("sourceLimitDescription")
					.replace("$sourceLimit$", limitNumber + "")
					.replace("$orderBy$", TextMgr.getInstance().getText(sourceLimitArr[1]).toLowerCase()));
		} else {
			notShowingCompleteDataSetText.setText(TextMgr.getInstance().getText(sourceLimitDescription).replace("$sourceLimit$", limitNumber + ""));
		}

		addHoverFromDataPanelHandler();
	}

	protected void initView() {
		createCentreViewColumn();
		createMetadataColumns();

		super.initView();
	}

	protected int getRowLimit() {
		return DeviceUtils.isMobile() ? EsaSkyWebConstants.MAX_SOURCES_FOR_MOBILE : entity.getDescriptor().getSourceLimit();
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
				final String ra = row.getElementByTapName(getDescriptor().getPolygonRaTapColumn()).getValue();
				final String dec = row.getElementByTapName(getDescriptor().getPolygonDecTapColumn()).getValue();

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

	@Override
	public void showStylePanel(int x, int y) {

		OnShapeChangedCallback srcShapeCallback = new OnShapeChangedCallback() {

			@Override
			public void onShapeChanged(String shape) {
				entity.setShape(shape);
			}
		};

		String arrowColor = null;
		Double arrowScale = null;
		Boolean arrowAvgChecked = null;
		Boolean useMedianOnAvgChecked = null;
		OnColorChangedCallback arrowColorCallback = null;
		OnValueChangedCallback arrowScaleCallback = null;
		OnCheckChangedCallback checkChangedCallback = null;

		arrowColor = entity.getArrowColor();
		if (arrowColor != null) {
			arrowScale = entity.getArrowScale();
			arrowAvgChecked = entity.getShowAvgProperMotion();
			useMedianOnAvgChecked = entity.getUseMedianOnAvgProperMotion();

			arrowColorCallback = new OnColorChangedCallback() {

				@Override
				public void onColorChanged(String color) {
					entity.setArrowColor(color);
				}
			};

			arrowScaleCallback = new OnValueChangedCallback() {

				@Override
				public void onValueChanged(double value) {
					entity.setArrowScale(value);
				}
			};

			checkChangedCallback = new OnCheckChangedCallback() {

				@Override
				public void onCheckChanged(boolean checkedOne, boolean checkedTwo) {
					entity.setShowAvgProperMotion(checkedOne, checkedTwo);
				}
			};
		}

		stylePanel = new StylePanel(entity.getEsaSkyUniqId(), entity.getTabLabel(), 
				entity.getColor(), entity.getSize(), entity.getShape(),
				arrowColor, arrowScale, arrowAvgChecked, useMedianOnAvgChecked, null, null,
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
		}, srcShapeCallback,
				arrowColorCallback, arrowScaleCallback, checkChangedCallback,
				null, null);
		stylePanel.toggle();
		stylePanel.setPopupPosition(x, y);
	}
}
