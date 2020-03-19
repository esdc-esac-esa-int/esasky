package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.HashMap;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.ifcs.model.multiretrievalbean.MultiRetrievalBean;
import esac.archive.esasky.ifcs.model.multiretrievalbean.MultiRetrievalBeanList;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ESASkySampEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.EsaSkyButtonCell;
import esac.archive.esasky.cl.web.client.model.TableColumnHelper;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.model.entities.CommonObservationEntity;
import esac.archive.esasky.cl.web.client.presenter.ResultsPresenter.MultiRetrievalBeanListMapper;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.SampConstants.SampAction;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.ImageColumn;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class CommonObservationsTablePanel extends AbstractTablePanel {

	
	/** Current entity. */
	private CommonObservationEntity entity;

	public CommonObservationsTablePanel(final String inputLabel, final String inputEsaSkyUniqID,
			final CommonObservationEntity inputentity) {
		super(inputLabel, inputEsaSkyUniqID, inputentity);

		this.entity = inputentity;
		initView();

		addHoverFromDataPanelHandler();
	}

	protected void initView() {
		createCentreViewColumn();

		if (getEntity().getDescriptor().getSampEnabled()) {
			createSendToSampColumn();
		}

		createMetadataColumns();
		super.initView();
	}

	private void createSendToSampColumn() {

		final Column<TableRow, String> sampColumn = new Column<TableRow, String>(
				new EsaSkyButtonCell(TextMgr.getInstance().getText("commonObservationTablePanel_sendRowToVOA"),
						TableColumnHelper.TOOLTIP_DELAY_MS)) {
			@Override
			public String getValue(final TableRow row) {
				return TableColumnHelper.resources.sendToSamp().getSafeUri().asString();
			}
		};

		sampColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		sampColumn.setFieldUpdater(new FieldUpdater<TableRow, String>() {

			@Override
			public void update(final int index, final TableRow row,
					final String value) {
				sendSelectedProductToSampApp(row);
			}

		});

		this.table.addColumn(sampColumn, "");
		this.table.setColumnWidth(sampColumn,
				TableColumnHelper.COLUMN_WIDTH_ICON_DEFAULT_SIZE, Unit.PX);

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
						row.getElementByTapName(getDescriptor().getTapRaColumn())
						.getValue(), 
						row.getElementByTapName(getDescriptor().getTapDecColumn())
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

	/**
	 * Send only the selected products to a Samp application.
	 * @param row Input TableRow object.
	 */
	private void sendSelectedProductToSampApp(final TableRow row) {

		String tableName = super.getLabel() + "-" + row.getElementByTapName(getDescriptor().getUniqueIdentifierField()).getValue();
		HashMap<String, String> sampUrlsPerMissionMap = new HashMap<String, String>();

		// Display top progress bar...
		Log.debug("[ObservationsTablePanel/sendSelectedProductToSampApp()] About to send 'show top progress bar...' event!!!");
		CommonEventBus.getEventBus().fireEvent(
				new ProgressIndicatorPushEvent(SampAction.SEND_PRODUCT_TO_SAMP_APP.toString(),
						TextMgr.getInstance().getText("sampConstants_sendingViaSamp")
						.replace(EsaSkyConstants.REPLACE_PATTERN, tableName)));

		GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TabRow_SendToVOTools, getFullId(), row.getElementByTapName(getDescriptor().getUniqueIdentifierField()).getValue());
		String sampUrl = null;
		if(getEntity().getDescriptor().getSampUrl() != null && !getEntity().getDescriptor().getSampUrl().isEmpty()){
			entity.executeSampFileList(row.getElementByTapName(getDescriptor().getUniqueIdentifierField()).getValue());
			return;
		}else if (!getEntity().getDescriptor().getDdBaseURL().isEmpty()) {
			sampUrl = buildSAMPURL(row);
		} else if (row.getElementByTapName("product_url").getValue() != null) {
			sampUrl = row.getElementByTapName("product_url").getValue();
		} else {
			Log.error("[ObservationsTablePanel/sendSelectedProductToSampApp()] No DD Base URL "
					+ " nor Product URL found for "
					+ super.getLabel()
					+ " obsId:" + row.getElementByTapName(getDescriptor().getUniqueIdentifierField()).getValue());
		}

		sampUrlsPerMissionMap.put(tableName, sampUrl);

		// Send all URL to Samp
		ESASkySampEvent sampEvent = new ESASkySampEvent(SampAction.SEND_PRODUCT_TO_SAMP_APP,
				sampUrlsPerMissionMap);
		CommonEventBus.getEventBus().fireEvent(sampEvent);


	}
	
    public String buildSAMPURL(TableRow row) {
        String[] archiveProductURI = getDescriptor().getDdProductURI().split("@@@");
        String tapName = archiveProductURI[1];
        String valueURI = row.getElementByTapName(tapName).getValue();
        return getDescriptor().getDdBaseURL()
                + getDescriptor().getDdProductURI().replace("@@@" + tapName + "@@@", valueURI);
    }
	
	@Override
	public final CommonObservationDescriptor getDescriptor() {
		return entity.getDescriptor();
	}

	@Override
	public final CommonObservationEntity getEntity() {
		return entity;
	}

	@Override
	public void downloadSelected(DDRequestForm ddForm) {
		
        MultiRetrievalBeanList multiRetrievalList = new MultiRetrievalBeanList();
        
        StringBuilder ddUrl = null;

        if (!getDescriptor().getDdBaseURL().isEmpty()) {
            ddUrl = new StringBuilder(getDescriptor().getDdBaseURL());
        }

        String join = "";

        for (TableRow tableRow : getSelectedRows()) {

            String url = "";

            if (ddUrl != null) {
            	String[] archiveProductURI = getDescriptor().getDdProductURI().split("@@@");
                String tapName = archiveProductURI[1];
                String valueURI = tableRow.getElementByTapName(tapName).getValue();
                ddUrl.append(join).append(getDescriptor().getDdProductURI().replace("@@@" + tapName + "@@@", valueURI));
                join = "&";
            } else if (tableRow.getElementByTapName("product_url") != null && !tableRow.getElementByTapName("product_url").getValue().trim().isEmpty()) {
                url = tableRow.getElementByTapName("product_url").getValue();
                MultiRetrievalBean multiRetrievalItem = new MultiRetrievalBean(
                        MultiRetrievalBean.TYPE_OBSERVATIONAL, getDescriptor().getMission(), url);
                multiRetrievalList.add(multiRetrievalItem);
                Log.debug("[ResultsPresenter] DD URL: " + url);
            } else {
                continue;
            }

        }

        // Send multiple file retrieval if available
        if (ddUrl != null) {
            MultiRetrievalBean multiRetrievalItem = new MultiRetrievalBean(
                    MultiRetrievalBean.TYPE_OBSERVATIONAL, getDescriptor().getMission(),
                    ddUrl.toString());
            multiRetrievalList.add(multiRetrievalItem);
            Log.debug("[ResultsPresenter] DD URL: " + ddUrl.toString());
        }

        final int files = multiRetrievalList.getMultiRetrievalBeanList().size();
        GoogleAnalytics.sendEventWithURL(GoogleAnalytics.CAT_Download_DD, getFullId(), "Files: " + files);
        
        if (files < 1) {
            Window.alert("No observations selected");
            return;
        }

        MultiRetrievalBeanListMapper mapper = GWT.create(MultiRetrievalBeanListMapper.class);

        String json = mapper.write(multiRetrievalList);

        ddForm.setAction(EsaSkyWebConstants.DATA_REQUEST_URL);
        ddForm.setMethod(FormPanel.METHOD_POST);
        ddForm.setJsonRequest(json);
        ddForm.submit();
	}

}
