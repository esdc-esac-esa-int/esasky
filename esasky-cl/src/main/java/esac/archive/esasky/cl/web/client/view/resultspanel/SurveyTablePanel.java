package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.TableColumnHelper;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.model.entities.SurveyEntity;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.ImageColumn;

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
        createMetadataColumns();
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
                final String ra = row.getElementByTapName(getDescriptor().getTapRaColumn()).getValue();
                final String dec = row.getElementByTapName(getDescriptor().getTapDecColumn()).getValue();
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
	public final CommonObservationDescriptor getDescriptor() {
		return entity.getDescriptor();
	}
}
