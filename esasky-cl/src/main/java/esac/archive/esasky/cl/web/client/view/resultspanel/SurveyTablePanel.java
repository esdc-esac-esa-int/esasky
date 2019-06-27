package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TAPConstant4Observation;
import esac.archive.esasky.cl.web.client.model.TableColumnHelper;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.model.entities.SurveyEntity;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.ImageColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.OnColorChangedCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.OnShapeChangedCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.OnValueChangedCallback;

public class SurveyTablePanel extends AbstractTablePanel {

	private SurveyEntity entity;
	
    public SurveyTablePanel(String label, String esaSkyUniqID, SurveyEntity entity) {
        super(label, esaSkyUniqID, entity);
        this.entity = entity;
        initView();
        addHoverFromDataPanelHandler();
    }
    
    protected void initView() {
        createCentreViewColumn();
        createMetadataColums();
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
                final String ra = row.getElementByTapName(TAPConstant4Observation.RA_DEG).getValue();
                final String dec = row.getElementByTapName(TAPConstant4Observation.DEC_DEG).getValue();
                AladinLiteWrapper.getInstance().goToTarget(ra, dec,
      					AladinLiteWrapper.getInstance().getFovDeg(), false,
      					AladinLiteWrapper.getInstance().getCooFrame());
            	    
            	    GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TabRow_Recenter, getFullId(), "ra: " + ra + ", dec:" + dec);
            }
        });

		this.table.addColumn(centreViewColumn, "");
		this.table.setColumnWidth(centreViewColumn, TableColumnHelper.COLUMN_WIDTH_ICON_DEFAULT_SIZE, Unit.PX);
	}


	@Override
	public void showStylePanel(int x, int y) {
		OnShapeChangedCallback srcShapeCallback = new OnShapeChangedCallback() {

			@Override
			public void onShapeChanged(String shape) {
				entity.setShape(shape);
			}
		};

		if(stylePanel == null) {
			stylePanel = new StylePanel(getEntity().getEsaSkyUniqId(), getEntity().getTabLabel(), 
					getEntity().getColor(), getEntity().getSize(), 
					entity.getShape(),
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
			}, srcShapeCallback,
					null, null, null,
					null, null);
		}
		stylePanel.toggle();
		stylePanel.setPopupPosition(x, y);
	}
	
	@Override
	public final CommonObservationDescriptor getDescriptor() {
		return entity.getDescriptor();
	}
}
