package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.RowHoverEvent;
import com.google.gwt.user.cellview.client.RowHoverEvent.Handler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.DefaultSelectionEventManager.SelectAction;

import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.MetadataDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColumnType;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEvent;
import esac.archive.esasky.cl.web.client.event.DataPanelResizeEventHandler;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.ShapeId;
import esac.archive.esasky.cl.web.client.model.TableColumnHelper;
import esac.archive.esasky.cl.web.client.model.TableElement;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.animation.OpacityAnimation;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.MovablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.DateTimeColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.DecColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.DoubleColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.ImageColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.IntegerColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.Link2ArchiveColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.LinkListColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.RaColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.SortableColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.column.StringColumn;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.table.DataPanelPager;
import esac.archive.esasky.cl.web.client.view.resultspanel.table.SimpleTable;

public abstract class AbstractTablePanel extends Composite {

	private final MultiSelectionModel<TableRow> selectionModel = new MultiSelectionModel<TableRow>();
	private int separatorIndex;
	private FlowPanel groupOneHeader = new FlowPanel();
	private FlowPanel groupTwoHeader = new FlowPanel();
	private boolean isGroupHeaderActive;
	private Label groupTwoLabel = new Label();
	private Timer loadingTimer = new Timer() {
		
		@Override
		public void run() {
			resizeColumnGroupHeader();
		}
	};
	
	private class SelectTimer extends Timer {
		private final int rowId;

		public SelectTimer(int rowId) {
			super();
			this.rowId = rowId;
		}

		@Override
		public void run() {
			for(int i = 0; i < table.getVisibleItemCount(); i++) {
				if(table.getVisibleItem(i).getShapeId() == rowId) {
					TableRowElement element = table.getRowElement(i);
					element.scrollIntoView();
					break;
				}
			}
		}
	}

	public interface IPreviewClickedHandler {
		public void onPreviewClicked(final String id);
	}

	/** is the esaSkyUniqID, the same saved into the Entities. */
	private String esaSkyUniqID;
	/** label. */
	private String tabTitle;
	private String adqlQueryUrl;

	protected final SimpleTable<TableRow> table = new SimpleTable<TableRow>();
	protected ListDataProvider<TableRow> dataProvider = new ListDataProvider<TableRow>();
	protected List<TableRow> filteredList = new LinkedList<TableRow>();
	private List<AbstractTableObserver> observers = new LinkedList<AbstractTableObserver>();

	private boolean isTableDirty = false;
	private Set<Integer> rowsRemovedByAtLeastOneFilter = new HashSet<Integer>();
	
	private DataPanelPager pager;
	private final MovablePanel pagerMovableArea = new MovablePanel(GoogleAnalytics.CAT_DataPanel_Pager, false);
	protected final Label notShowingCompleteDataSetText = new Label();
	protected final FocusPanel notShowingCompleteDataSetMouseOverDetector = new FocusPanel();
	protected Label emptyTableLabel = new Label();
	protected LoadingSpinner loadingSpinner = new LoadingSpinner(false);
	
	private FlowPanel tableNotShowingContainer = new FlowPanel();
	private FlowPanel columnGroupHeader = new FlowPanel();
	private FlowPanel tableAndGroupHeader = new FlowPanel();

	private int lastHoveredRowId;
	private boolean isHidingTable = false;
	private boolean hasBeenClosed = false;
	protected boolean isShowing = false;
	
	private IPreviewClickedHandler previewClickedHandler;
	private List<TableRow> originalList;
	private GeneralEntityInterface entity;
	
	public AbstractTablePanel(final String inputLabel, final String inputEsaSkyUniqID, GeneralEntityInterface entity) {
		this.esaSkyUniqID = inputEsaSkyUniqID;
		this.tabTitle = inputLabel;
		this.entity = entity;
		exposeOpenFilterBoxMethodToJs(this);
		dataProvider.addDataDisplay(table);
		table.setAutoHeaderRefreshDisabled(true);
		table.setAutoFooterRefreshDisabled(true);
		table.setAlwaysShowScrollBars(false);
		table.getElement().getStyle().setProperty("height", "100%");

		Label noMatchingDataLabel = new Label(TextMgr.getInstance().getText("abstractTablePanel_noDataMatching"));
		noMatchingDataLabel.addStyleName("noMatchingDataLabel");
		table.setEmptyTableWidget(noMatchingDataLabel);

		FlowPanel container = new FlowPanel();
		container.addStyleName("dataPanelContainer");
		
		tableNotShowingContainer.addStyleName("tableNotShowingContainer");
		tableNotShowingContainer.add(loadingSpinner);
		emptyTableLabel.setText(TextMgr.getInstance().getText("abstractTablePanel_loadingData"));
		tableNotShowingContainer.add(emptyTableLabel);
		container.add(tableNotShowingContainer);
		
		columnGroupHeader.addStyleName("tablePanel_groupHeader");
		columnGroupHeader.setVisible(false);
		
		tableAndGroupHeader.addStyleName("tableAndColumnGroupHeader");
		tableAndGroupHeader.add(columnGroupHeader);
		tableAndGroupHeader.add(table);
		
		FocusPanel hoverDetector = new FocusPanel();
		hoverDetector.addStyleName("dataPanelHoverDetector");
		hoverDetector.add(tableAndGroupHeader);
		container.add(hoverDetector);
		
		final FlowPanel notShowingCompleteDataSetContainer = new FlowPanel();
		notShowingCompleteDataSetContainer.addStyleName("resultInformation");
		notShowingCompleteDataSetContainer.add(notShowingCompleteDataSetText);

		notShowingCompleteDataSetMouseOverDetector.setVisible(false);
        final OpacityAnimation resultInformationAnimation = new OpacityAnimation(notShowingCompleteDataSetMouseOverDetector.getElement());
        
        final Timer resultInformationAreaTimer = new Timer() {
    		
    		@Override
    		public void run() {
				resultInformationAnimation.animateTo(1, 500);
				notShowingCompleteDataSetMouseOverDetector.getElement().getStyle().setProperty("pointerEvents", "auto");
				cancel();
    		}
    	};
	
    	notShowingCompleteDataSetMouseOverDetector.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent arg0) {
				resultInformationAnimation.animateTo(0, 500);
				resultInformationAreaTimer.schedule(4000);
				notShowingCompleteDataSetMouseOverDetector.getElement().getStyle().setProperty("pointerEvents", "none");
			}
    	});
    

    	notShowingCompleteDataSetMouseOverDetector.addStyleName("resultInformationArea");
    	notShowingCompleteDataSetMouseOverDetector.add(notShowingCompleteDataSetContainer);

    	container.add(notShowingCompleteDataSetMouseOverDetector);
    	
		pager = new DataPanelPager();
		pager.setDisplay(table);
		pagerMovableArea.addStyleName("pagerContainer");
		pagerMovableArea.add(pager);
		pagerMovableArea.setSuggestedPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent arg0) {
				refreshHeightTimer.schedule(10);
			}
		});

		pager.addStyleName("dataPanelPager");

		CommonEventBus.getEventBus().addHandler(DataPanelResizeEvent.TYPE, new DataPanelResizeEventHandler() {

			@Override
			public void onDataPanelResize(DataPanelResizeEvent event) {
				if (event.getNewHeight() > 40) {
					pagerMovableArea.setVisible(true);
				} else {
					pagerMovableArea.setVisible(false);
				}
			}
		});
		
		hoverDetector.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				hoverStopRow(lastHoveredRowId);
			}
		});
		hoverDetector.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				hoverStopRow(lastHoveredRowId);
			}
		});

		
		table.addStyleName("displayNone");
		pagerMovableArea.addStyleName("displayNone");
		
		groupOneHeader.addStyleName("tablePanel_group_observation");
		groupOneHeader.add(new Label(TextMgr.getInstance().getText("tableGroup_Observation")));

		groupTwoHeader.addStyleName("tablePanel_group_sso");
		groupTwoHeader.add(groupTwoLabel);
		
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				resizeColumnGroupHeader();
			}
		});

		Column<TableRow, Boolean> checkColumn = new Column<TableRow, Boolean>(
				new CheckboxCell()) {

			@Override
			public Boolean getValue(final TableRow object) {
				return AbstractTablePanel.this.table.getSelectionModel()
						.isSelected(object);
			}
		};
		table.setSelectionModel(selectionModel);
		this.table.setSelectionModel(this.table.getSelectionModel(),
				DefaultSelectionEventManager.createCustomManager(
						new DefaultSelectionEventManager.CheckboxEventTranslator<TableRow>(0){
							@Override
							public SelectAction translateSelectionEvent(CellPreviewEvent<TableRow> event) {
								SelectAction action = super.translateSelectionEvent(event);
								if (action.equals(SelectAction.IGNORE) || action.equals(SelectAction.TOGGLE)) {
									Column<TableRow, ?> clickedColumn = table.getColumn(event.getColumn());
									if(clickedColumn instanceof DecColumn
											|| clickedColumn instanceof DoubleColumn
											|| clickedColumn instanceof RaColumn
											|| clickedColumn instanceof StringColumn
											|| clickedColumn.getCell() instanceof CheckboxCell) {
										boolean gettingSelected = !selectionModel.isSelected(event.getValue());
										selectionModel.setSelected(event.getValue(), gettingSelected);

										HashSet<ShapeId> changedRows = new HashSet<ShapeId>();
										changedRows.add(event.getValue());
										fireSelectionEvent(gettingSelected, changedRows);
									}
									return SelectAction.IGNORE;
								}
								return action;
							}
						}));

		CheckboxCell headerCheckbox = new CheckboxCell();
		Header<Boolean> selectPageHeader = new Header<Boolean>(headerCheckbox) {
			@Override
			public Boolean getValue() {
				return selectionModel.getSelectedSet().size() >= dataProvider.getList().size()
						&& table.getVisibleItemCount() > 0 ? true : false;
			}
		};
		selectPageHeader.setUpdater(new ValueUpdater<Boolean>() {

			@Override
			public void update(final Boolean value) {
				for (TableRow item : dataProvider.getList()) {
					AbstractTablePanel.this.table.getSelectionModel()
					.setSelected(item, value);
				}

				Set<ShapeId> changedRows;

				if(value) {
					changedRows = new HashSet<ShapeId>(getSelectedRows());
				} else {
					changedRows = new HashSet<ShapeId>(dataProvider.getList());
				}
				fireSelectionEvent(value, changedRows);
			}
		});

		this.table.addColumn(checkColumn, selectPageHeader);
		this.table.setColumnWidth(checkColumn,
				TableColumnHelper.COLUMN_WIDTH_CHECKBOX_DEFAULT_SIZE, Unit.PX);
		
		initWidget(container);
	}
	
	Timer refreshHeightTimer = new Timer() {
		
		@Override
		public void run() {
			refreshHeight();
		}
	};
	
	Timer arrangeHeaderTimer = new Timer() {
		
		@Override
		public void run() {
			arrangeHeaderView();
		}
	};
	
	private void arrangeHeaderView() {
		for(int i = 0; i < table.getColumnCount(); i++) {
			Header<?> header = table.getHeader(i);
			String headerStyleNames = header.getHeaderStyleNames();
			if(headerStyleNames == null) {
				headerStyleNames = "";
			}
			if(table.getOffsetWidth() == 0) {
				arrangeHeaderTimer.schedule(10);
				return;
			}
			if(table.getOffsetWidth() / (table.getColumnCount() - 1) > 70) {
				headerStyleNames = headerStyleNames.replace("filterButtonOnTop", "");
				headerStyleNames = headerStyleNames + " filterButtonToTheRight";
			} else {
				headerStyleNames = headerStyleNames.replace("filterButtonToTheRight", "");
				headerStyleNames = headerStyleNames + " filterButtonOnTop";
			}
			header.setHeaderStyleNames(headerStyleNames);
		}
		table.redrawHeaders();
		reactivateActiveFilterButtonStyles();
	}
	
	private void setTableMinWidth() {
		tableAndGroupHeader.getElement().getStyle().setProperty("minWidth", (table.getColumnCount() - 1) * 55  + "px");
	}
	
	public void toggleVisibilityOfFreeFlowingElements() {
		if(GUISessionStatus.isDataPanelOpen()) {
			pagerMovableArea.setVisible(true);
		} else {
			pagerMovableArea.setVisible(false);
		}
	}

	private ColumnSortList columnSortList = null;

	private int scrollLeft = 0;
	protected void initView() {
		table.addColumnSortHandler(new ColumnSortEvent.Handler() {

			boolean originalListRestored = false;

			@Override
			public void onColumnSort(ColumnSortEvent sortEvent) {
				if (!originalListRestored) {
					originalListRestored = true;
					Element scrollableElement = table.getHorizontalScrollableElement();
					scrollLeft = scrollableElement.getScrollLeft();

					dataProvider.getList().clear();
					dataProvider.getList().addAll(originalList);
					ColumnSortEvent.fire(table, sortEvent.getColumnSortList());
					originalList = new LinkedList<TableRow>(dataProvider.getList());

					applyAllFilters();
					originalListRestored = false;
					
					//Resets the scrollposition of the table after all updates are done.
					Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
						
						@Override
						public void execute() {
							Element scrollableElement = table.getHorizontalScrollableElement();
							scrollableElement.setScrollLeft(scrollLeft);
						}
					});
				}

				columnSortList = sortEvent.getColumnSortList();
			}
		});

	}

	private void reactivateActiveFilterButtonStyles() {
		Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
			
			@Override
			public void execute() {
				for (int i = 0; i < table.getColumnCount(); i++) {
					if (table.getColumn(i) instanceof SortableColumn) {
						((SortableColumn<?>) table.getColumn(i)).ensureCorrectFilterButtonStyle();
					}
				}
			}
		});
	}

	public void setPreviewClickedHandler(IPreviewClickedHandler previewClickedHandler) {
		this.previewClickedHandler = previewClickedHandler;
	}

	public native void exposeOpenFilterBoxMethodToJs(AbstractTablePanel tab) /*-{
		$wnd.openFilterBox = function(columnNumber) {
			tab.@esac.archive.esasky.cl.web.client.view.resultspanel.CommonObservationsTablePanel::openFilterBox(I)(columnNumber);
		}
	}-*/;

	public void insertData(List<TableRow> data) {
		if(data == null || data.size() == 0) {
			clearTable();
			emptyTableLabel.setText(TextMgr.getInstance().getText("resultsPresenter_noDataFound"));
			loadingSpinner.setVisible(false);
			return;
		}
		if(table.getColumnSortList().size() == 1) {
			createMetadataColums();
		}
		
		originalList = new LinkedList<TableRow>(data);
		rowsRemovedByAtLeastOneFilter.clear();

		// Add the data to the data provider, which automatically pushes it to the
		// widget.
		List<TableRow> list = dataProvider.getList();
		list.clear();
		list.addAll(data);
		isTableDirty = true;
		filteredList = dataProvider.getList();

		ColumnSortList clmSortList = table.getColumnSortList();

		for (int i = 0; i < clmSortList.size(); i++) {
			ColumnSortInfo clmInfo = clmSortList.get(i);
			@SuppressWarnings("unchecked")
			Column<TableRow, ?> col = (Column<TableRow, ?>) clmInfo.getColumn();
			int colIdx = table.getColumnIndex(col);
			Header<?> hdr = table.getHeader(colIdx);
			createSortableColumn(list, col, hdr);
		}

		if (columnSortList != null) {
			ColumnSortEvent.fire(table, columnSortList);
		}

		table.setRowData(list);
		table.setPageSize(50);
		table.redrawFooters();

		for (int i = 0; i < table.getColumnCount(); i++) {
			if (table.getColumn(i) instanceof SortableColumn) {
				((SortableColumn<?>) table.getColumn(i)).setColumnData(data);
			}
		}
		
		table.removeStyleName("displayNone");
		pagerMovableArea.removeStyleName("displayNone");
		tableNotShowingContainer.addStyleName("displayNone");
		arrangeHeaderTimer.run();
		setTableMinWidth();
		resizeColumnGroupHeader();
		notifyObserversRowsChange(table.getRowCount());
	}

	protected String getLabelTextFromHeader(final Header<?> hdr) {
		String label = "";
		try {
			String headerString = ((SafeHtml)hdr.getValue()).asString();
			label = headerString.substring(headerString.indexOf("dataPanelHeaderColumnTitle"));
			label = label.substring(label.indexOf(">") + 1, label.indexOf("</div>"));
		} catch (Exception e) {
		}
		
		return label;
	}

	public void createSortableColumn(List<TableRow> list, Column<TableRow, ?> col, final Header<?> hdr) {
		List<TableRow> catRowsList = (List<TableRow>) list;
		Column<TableRow, ?> columns = (Column<TableRow, ?>) col;

		final String columnLabel = getLabelTextFromHeader(hdr);
		final SortableColumn<?> sortableColumn = getColumn(columnLabel);
		
		ListHandler<TableRow> columnSortHandler = new ListHandler<TableRow>(catRowsList);
		columnSortHandler.setComparator(columns, new Comparator<TableRow>() {

			@Override
			public int compare(TableRow o1, TableRow o2) {
				return sortableColumn.compare(o1, o2);
			}
		});

		table.addColumnSortHandler(columnSortHandler);
	}

	public IDescriptor getDescriptor() {
		return entity.getDescriptor();
	}

	public GeneralEntityInterface getEntity() {
		return entity;
	}

	public final Set<TableRow> getSelectedRows() {
		Set<TableRow> visibleSet = new HashSet<TableRow>();

		for (TableRow item : dataProvider.getList()) {
			if (table.getSelectionModel().isSelected(item)) {
				visibleSet.add(item);
			}
		}
		return visibleSet;
	}

	public final List<TableRow> getFilteredRows() {
		return filteredList;
	}

	public void clearSelectionModel() {
		selectionModel.clear();
	}

	public final void clearTable() {
		dataProvider.getList().clear();
		table.addStyleName("displayNone");
		pagerMovableArea.addStyleName("displayNone");
		tableNotShowingContainer.removeStyleName("displayNone");
		emptyTableLabel.setText(TextMgr.getInstance().getText("abstractTablePanel_loadingData"));
		loadingSpinner.setVisible(true);
	}

	protected void createMetadataColums() {
		for (MetadataDescriptor currentMTD : getEntity().getDescriptor().getMetadata()) {

			if (currentMTD.getVisible()) {
				final String labelKey = currentMTD.getLabel();
				String labelTmp;
				if(getEntity().getDescriptor() instanceof ExtTapDescriptor) {
					labelTmp = labelKey;
				}else {
					labelTmp = TextMgr.getInstance().getText(labelKey);
				}
				final String label = labelTmp;

				final int columnNumber = table.getColumnCount();
				final String filterButtonId = getEntity().getEsaSkyUniqId() + columnNumber;

				String filterButton = "" + "<div id=\'" + filterButtonId
						+ "\' class=\"filterButton defaultEsaSkyButton darkStyle smallButton squaredButton gwt-PushButton-up\" "
						+ "title=\"" + TextMgr.getInstance().getText("abstractTablePanel_filterTooltip") + "\""
						+ "onmouseover=\"arguments[0].stopPropagation();"
						+ "if(this.className.indexOf(\'gwt-PushButton-up-hovering\') == -1){"
						+ "this.className += \' gwt-PushButton-up-hovering\'; }" + "var element = this;"
						+ "while ((element = element.parentElement) && (element.outerHTML.indexOf(\'th\') != 1));"
						+ "var header = element;"
						+ "header.className = header.className.replace( /(?:^|\\s)dataPanelHeaderHover(?!\\S)/g , '' ); "
						+ "\" " + "onmouseenter=\"arguments[0].stopPropagation(); "
						+ "if(this.className.indexOf(\'gwt-PushButton-up-hovering\') == -1){"
						+ "this.className += \' gwt-PushButton-up-hovering\'; }" + "\" "
						+ "onmousedown=\"arguments[0].stopPropagation(); "
						+ "if(this.className.indexOf(\'gwt-PushButton-down\') == -1){"
						+ "this.className += \' gwt-PushButton-down\'; }"
						+ "this.className = this.className.replace( /(?:^|\\s)gwt-PushButton-up(?!\\S)/g , '' ); "
						+ "this.className = this.className.replace( /(?:^|\\s)gwt-PushButton-up-hovering(?!\\S)/g , '' ); "
						+ "\" " + "onmouseup=\"arguments[0].stopPropagation(); "
						+ "if(this.className.indexOf(\'gwt-PushButton-up\') == -1){"
						+ "this.className += \' gwt-PushButton-up\'; }"
						+ "if(this.className.indexOf(\'gwt-PushButton-up-hovering\') == -1){"
						+ "this.className += \' gwt-PushButton-up-hovering\'; }"
						+ "this.className = this.className.replace( /(?:^|\\s)gwt-PushButton-down(?!\\S)/g , '' ); "
						+ "\" " + "onmouseout=\"arguments[0].stopPropagation(); "
						+ "this.className = this.className.replace( /(?:^|\\s)gwt-PushButton-down(?!\\S)/g , '' ); "
						+ "this.className = this.className.replace( /(?:^|\\s)gwt-PushButton-up-hovering(?!\\S)/g , '' ); "
						+ "var element = this;"
						+ "while ((element = element.parentElement) && (element.outerHTML.indexOf(\'th\') != 1));"
						+ "var header = element;" + "if(header.className.indexOf(\'dataPanelHeaderHover\') == -1){"
						+ "header.className += \' dataPanelHeaderHover\'; }"
						+ "if(this.className.indexOf(\'gwt-PushButton-up\') == -1){"
						+ "this.className += \' gwt-PushButton-up\'; }" + "\" "
						+ "onmousemove=\"arguments[0].stopPropagation();\" "
						+ "onclick=\"arguments[0].stopPropagation(); " + "window.openFilterBox(" + columnNumber + ");"
						+ "\"" + ">" + "<img src=\"" + TableColumnHelper.resources.filterIcon().getSafeUri().asString()
						+ "\" class=\"fillParent\" />" + "</div>";

				SafeHtml header = SafeHtmlUtils.fromSafeConstant("<div style=\"position:relative\">" + filterButton + "<div class=\"dataPanelHeaderColumnTitle\" style=\"word-break:break-word\">"+  label + "</div></div>");
				
				final ColumnType type = currentMTD.getType();

				if (ColumnType.DOWNLOAD.equals(type)) {

					final ImageColumn linkColumn = new ImageColumn(
							TextMgr.getInstance().getText("abstractTablePanel_downloadRow"),
							TableColumnHelper.resources.download().getSafeUri().asString());
					table.addColumn(linkColumn, "");
					table.setColumnWidth(linkColumn, TableColumnHelper.COLUMN_WIDTH_ICON_DEFAULT_SIZE, Unit.PX);

					linkColumn.setFieldUpdater(new FieldUpdater<TableRow, String>() {

						@Override
						public void update(final int index, final TableRow row, final String value) {
							final String url = row.getElementByLabel(label).getValue();
							Window.open(url, "_blank", "");
							GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TabRow_Download, getFullId(), url);
						}

					});

				}else if (ColumnType.DATALINK.equals(type)) {

					StringColumn linkColumn = new StringColumn(label, filterButtonId, new RowsFilterObserver() {

						@Override
						public void onRowsFiltered(Set<Integer> rowsToRemove, Set<Integer> rowsToAdd) {
							calculateChangedRows(rowsToRemove, rowsToAdd);
						}
					});
					table.getColumnSortList().push(linkColumn);
					table.addColumn(linkColumn, header);

				} else if (ColumnType.LINK.equals(type)) {

					String tooltipMsg = TextMgr.getInstance().getText("abstractTablePanel_preview");
					ImageResource icon = TableColumnHelper.resources.previewIcon();
					IDescriptor descriptor = getEntity().getDescriptor();
					if (descriptor.getAdsAuthorUrl() != null && !descriptor.getAdsAuthorUrl().isEmpty()) {
						tooltipMsg = TextMgr.getInstance().getText("abstractTablePanel_sourceInPublication");
						icon = TableColumnHelper.resources.targetListIcon();
					}

					final ImageColumn linkColumn = new ImageColumn(tooltipMsg, icon.getSafeUri().asString());
					table.addColumn(linkColumn, "");
					table.setColumnWidth(linkColumn, TableColumnHelper.COLUMN_WIDTH_ICON_DEFAULT_SIZE, Unit.PX);

					linkColumn.setFieldUpdater(new FieldUpdater<TableRow, String>() {

						@Override
						public void update(final int index, final TableRow row, final String value) {

							if (!labelKey.endsWith("_preview")) {
								String url = row.getElementByLabel(label).getValue();
								TableElement searchParam = row.getElementContainingLabel("observation_id");
								if (searchParam == null) {
									searchParam = row
											.getElementContainingLabel(TextMgr.getInstance().getText("observation_id"));
								}
								if (searchParam == null) {
									searchParam = row.getElementContainingLabel(
											TextMgr.getInstance().getText("abstractTablePanel_name"));
								}

								new PreviewDialogBox(url, searchParam.getValue());
								GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Download_Preview, getFullId(),
										searchParam.getValue());

							} else {
								// Show UploadTargetList if label if kind of "BIBCODE_preview"
								final String textKey = labelKey.replace("_preview", "");
								final String textValue = TextMgr.getInstance().getText(textKey);
								TableElement searchParam = row.getElementContainingLabel(textValue);

								if (searchParam != null && AbstractTablePanel.this.previewClickedHandler != null) {
									AbstractTablePanel.this.previewClickedHandler
											.onPreviewClicked(searchParam.getValue());
									GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_TabRow_SourcesInPublication,
											getFullId(), searchParam.getValue());
								}
							}
						}

					});

				} else if (ColumnType.STRING.equals(type) || ColumnType.CHAR.equals(type)) {
					StringColumn stringColumn = new StringColumn(label, filterButtonId, new RowsFilterObserver() {

						@Override
						public void onRowsFiltered(Set<Integer> rowsToRemove, Set<Integer> rowsToAdd) {
							calculateChangedRows(rowsToRemove, rowsToAdd);
						}
					});
					table.getColumnSortList().push(stringColumn);
					table.addColumn(stringColumn, header);
					
				} else if (ColumnType.DATETIME.equals(type)) {
					DateTimeColumn dateTimeColumn = new DateTimeColumn(label, filterButtonId,
							new RowsFilterObserver() {

								@Override
								public void onRowsFiltered(Set<Integer> rowsToRemove, Set<Integer> rowsToAdd) {
									calculateChangedRows(rowsToRemove, rowsToAdd);
								}
							});
					table.getColumnSortList().push(dateTimeColumn);
					table.addColumn(dateTimeColumn, header);

				} else if (ColumnType.RA.equals(type)) {
					RaColumn raColumn = new RaColumn(label, filterButtonId, new RowsFilterObserver() {

						@Override
						public void onRowsFiltered(Set<Integer> rowsToRemove, Set<Integer> rowsToAdd) {
							calculateChangedRows(rowsToRemove, rowsToAdd);
						}
					});
					table.getColumnSortList().push(raColumn);
					table.addColumn(raColumn, header);

				} else if (ColumnType.DEC.equals(type)) {
					DecColumn decColumn = new DecColumn(label, filterButtonId, new RowsFilterObserver() {

						@Override
						public void onRowsFiltered(Set<Integer> rowsToRemove, Set<Integer> rowsToAdd) {
							calculateChangedRows(rowsToRemove, rowsToAdd);
						}
					});
					table.getColumnSortList().push(decColumn);
					table.addColumn(decColumn, header);

				} else if (ColumnType.DOUBLE.equals(type)) {
					final DoubleColumn doubleColumn = new DoubleColumn(label, filterButtonId,
							new RowsFilterObserver() {

								@Override
								public void onRowsFiltered(Set<Integer> rowsToRemove, Set<Integer> rowsToAdd) {
									calculateChangedRows(rowsToRemove, rowsToAdd);
								}
							});
					table.getColumnSortList().push(doubleColumn);
					table.addColumn(doubleColumn, header);

				} else if (ColumnType.INT.equals(type) || ColumnType.INTEGER.equals(type) || ColumnType.LONG.equals(type)) {
					final IntegerColumn intColumn = new IntegerColumn(label, filterButtonId,
							new RowsFilterObserver() {

								@Override
								public void onRowsFiltered(Set<Integer> rowsToRemove, Set<Integer> rowsToAdd) {
									calculateChangedRows(rowsToRemove, rowsToAdd);
								}
							});
					table.getColumnSortList().push(intColumn);
					table.addColumn(intColumn, header);
					
				}else if (ColumnType.LINK2ARCHIVE.equals(type)) {
					Link2ArchiveColumn link2ArchiveColumn = new Link2ArchiveColumn(label,
							getEntity().getDescriptor(), filterButtonId, new RowsFilterObserver() {

								@Override
								public void onRowsFiltered(Set<Integer> rowsToRemove, Set<Integer> rowsToAdd) {
									calculateChangedRows(rowsToRemove, rowsToAdd);
								}
							});
					table.getColumnSortList().push(link2ArchiveColumn);
					table.addColumn(link2ArchiveColumn, header);

				} else if (ColumnType.LINKLIST.equals(type)) {
					IDescriptor descriptor = getEntity().getDescriptor();
					LinkListColumn linkListColumn = new LinkListColumn(label, descriptor.getAdsAuthorSeparator(),
							descriptor.getAdsAuthorUrl(), descriptor.getAdsAuthorUrlReplace(),
							EsaSkyWebConstants.PUBLICATIONS_SHOW_ALL_AUTHORS_TEXT,
							EsaSkyWebConstants.PUBLICATIONS_MAX_AUTHORS, filterButtonId, new RowsFilterObserver() {

								@Override
								public void onRowsFiltered(Set<Integer> rowsToRemove, Set<Integer> rowsToAdd) {
									calculateChangedRows(rowsToRemove, rowsToAdd);
								}
							});
					table.getColumnSortList().push(linkListColumn);
					table.addColumn(linkListColumn, header);

				}

				if (type.equals(ColumnType.STRING) || type.equals(ColumnType.RA) || type.equals(ColumnType.DEC)
						|| type.equals(ColumnType.DOUBLE) || type.equals(ColumnType.LINK2ARCHIVE)
						|| type.equals(ColumnType.LINKLIST) || type.equals(ColumnType.CHAR)
						|| type.equals(ColumnType.INT) || type.equals(ColumnType.LONG)){
					int currentIndex = table.getColumnCount() - 1;
					table.getHeader(currentIndex)
							.setHeaderStyleNames(table.getHeader(currentIndex).getHeaderStyleNames()
									+ " dataPanelHeader dataPanelHeaderHover");
				}
			}
		}

		/*
		 * When a table was opened, the last column showed the sorted icon (but it
		 * wasn't sorted) In order to get rid of that icon a column was created with a
		 * width of 0px If a better approach is found, delete these next four columns:
		 */
		ImageColumn lColumn = new ImageColumn("", "");
		table.getColumnSortList().push(lColumn);
		table.addColumn(lColumn, SafeHtmlUtils.fromSafeConstant(""));
		table.setColumnWidth(table.getColumn(table.getColumnCount() - 1), 0 + "px");
	}

	public void refreshHeight() {
		table.refreshHeight();
		arrangeHeaderTimer.run();
		resizeColumnGroupHeader();
	}

	/*
	 * Sets a timer to scroll to element, since Gwt's Datagrid scrollIntoView
	 * doesn't work if EsaSky has multiple tabs open, and user selects item in a tab
	 * that isn't visible
	 */
	protected void deferredScrollIntoView(int i) {
		new SelectTimer(i).schedule(100);
	}

	/**
	 * getEsaSkyUniqID().
	 * 
	 * @return String.
	 */
	public final String getEsaSkyUniqID() {
		return this.esaSkyUniqID;
	}

	public final String getLabel() {
		return tabTitle;
	}

	public final String getADQLQueryUrl() {
		return adqlQueryUrl;
	}

	public final void setADQLQueryUrl(final String inputADQLQueryUrl) {
		this.adqlQueryUrl = inputADQLQueryUrl;
	}

	public final String getADQLQueryForChosenRows() {
		String uniqueIdentifierField = getEntity().getDescriptor().getUniqueIdentifierField();
		IDescriptor descriptor = getEntity().getDescriptor();

		String adql = "SELECT * FROM " + descriptor.getTapTable() + " WHERE " + uniqueIdentifierField + " IN(";
		List<TableRow> subset = new ArrayList<TableRow>(getSelectedRows());
		if (subset.size() == 0) {
			subset = getFilteredRows();
		}

		for (TableRow row : subset) {
			adql += "'" + row.getElementByTapName(uniqueIdentifierField).getValue() + "',";
		}
		adql = adql.substring(0, adql.length() - 1) + ")";
		return adql;
	}

	public void selectRow(int rowId) {
		if (rowId >= 0) {
			for(int i = 0; i < dataProvider.getList().size(); i++) {
				if(dataProvider.getList().get(i).getShapeId() == rowId) {
					if(i < pager.getPageStart() || i >= pager.getPageStart() + pager.getPageSize()) {
						if(i >= pager.getDisplay().getRowCount() - pager.getPageSize()) {
							pager.lastPageStart();
						} else {
							pager.setPageStart(i);
						}
					}
					TableRow obsObject = dataProvider.getList().get(i);
					this.table.getSelectionModel().setSelected(obsObject, true);
					getEntity().selectShapes(new HashSet<ShapeId>(getSelectedRows()));
					afterForeignRowSelection(true, obsObject);
					
					deferredScrollIntoView(rowId);
					break;
				}
			}
		}
	}

	public void deselectRow(int rowId) {
		if (rowId >= 0) {
			for(int i = 0; i < dataProvider.getList().size(); i++) {
				if(dataProvider.getList().get(i).getShapeId() == rowId) {
					TableRow obsObject = dataProvider.getList().get(i);
					this.table.getSelectionModel().setSelected(obsObject, false);
					afterForeignRowSelection(false, obsObject);
					break;
				}
			}
		}
	}

	public void hoverStartRow(int rowId) {
		if (rowId >= 0) {
			for (int i = 0; i < this.table.getVisibleItems().size(); i++) {
				if (this.table.getVisibleItem(i).getShapeId() == rowId) {
					setStyleName(this.table.getRowElement(i), "dataGridHoveredRowUserDefined", true);
					getEntity().hoverStart(rowId);
					lastHoveredRowId = rowId;
					break;
				}
			}
		}
	}

	public void hoverStopRow(int rowId) {
		if (rowId >= 0) {
			for (int i = 0; i < this.table.getVisibleItems().size(); i++) {
				if (this.table.getVisibleItem(i).getShapeId() == rowId) {
			        setStyleName(this.table.getRowElement(i), "dataGridHoveredRowUserDefined", false);
					getEntity().hoverStop(rowId);
					break;
				}
			}
		}
	}

	public void hoverStartEntity(RowHoverEvent hoverEvent) {
		int rowIndex = hoverEvent.getHoveringRow().getRowIndex();
		lastHoveredRowId = this.table.getVisibleItem(rowIndex).getShapeId();
		getEntity().hoverStart(lastHoveredRowId);
	}

	public void hoverStopEntity(RowHoverEvent hoverEvent) {
		int rowIndex = hoverEvent.getHoveringRow().getRowIndex();
		getEntity().hoverStop(this.table.getVisibleItem(rowIndex).getShapeId());
	}

	protected void addHoverFromDataPanelHandler() {
		table.addRowHoverHandler(new Handler() {

			@Override
			public void onRowHover(RowHoverEvent hoverEvent) {
				if (hoverEvent.isUnHover()) {
					hoverStopEntity(hoverEvent);
					setStyleName(hoverEvent.getHoveringRow(), "dataGridHoveredRowUserDefined", false);
				} else {
					setStyleName(hoverEvent.getHoveringRow(), "dataGridHoveredRowUserDefined", true);
					hoverStartEntity(hoverEvent);
				}
			}
		});
	}

	private void calculateChangedRows(Set<Integer> rowsToRemove, Set<Integer> rowsToAdd) {
		for (int idOfRowToRemove : rowsToRemove) {
			if (!rowsRemovedByAtLeastOneFilter.contains(idOfRowToRemove)) {
				isTableDirty = true;
				break;
			}
		}
		rowsRemovedByAtLeastOneFilter.addAll(rowsToRemove);

		for (Integer idOfRowToAdd : rowsToAdd) {
			boolean shouldAddRow = true;
			for (int i = 0; i < table.getColumnCount(); i++) {
				if (table.getColumn(i) instanceof SortableColumn
						&& ((SortableColumn<?>) table.getColumn(i)).hasFilteredAwayId(idOfRowToAdd)) {
					shouldAddRow = false;
					break;
				}
			}
			if (shouldAddRow) {
				isTableDirty = true;
				rowsRemovedByAtLeastOneFilter.remove(idOfRowToAdd);
			}
		}

		if (isTableDirty) {
			applyAllFilters();
			notifyObserversRowsChange(filteredList.size());
			if (getEntity().getContext() != EntityContext.PUBLICATIONS) {
				getEntity().showAndHideShapes(new LinkedList<Integer>(rowsToAdd),
						new LinkedList<Integer>(rowsToRemove));
			}
		}
	}

	private void applyAllFilters() {
		dataProvider.getList().clear();
		dataProvider.getList().addAll(originalList);
		filteredList = dataProvider.getList();
		Iterator<TableRow> rowIterator = filteredList.iterator();

		while (rowIterator.hasNext()) {
			TableRow row = rowIterator.next();
			if (rowsRemovedByAtLeastOneFilter.contains(row.getShapeId())) {
				rowIterator.remove();
			}
		}
		dataProvider.setList(filteredList);
		reactivateActiveFilterButtonStyles();
	}

	private SortableColumn<?> getColumn(String label) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			if (table.getColumn(i) instanceof SortableColumn) {
				SortableColumn<?> column = (SortableColumn<?>) table.getColumn(i);
				if (column.getLabel().equals(label)) {
					return column;
				}
			}
		}
		return null;
	}

	public void openFilterBox(int columnNumber) {
		((SortableColumn<?>) table.getColumn(columnNumber)).showFilter();
	}

	public void selectTablePanel() {
		isShowing = true;
		table.setAutoHeaderRefreshDisabled(true);
		exposeOpenFilterBoxMethodToJs(this);
		MainLayoutPanel.addElementToMainArea(pagerMovableArea);
		if (!isHidingTable) {
			refreshHeight();
		}
	}
	
	protected StylePanel stylePanel;
	public void deselectTablePanel() {
		isShowing = false;
		MainLayoutPanel.removeElementFromMainArea(pagerMovableArea);
		if(stylePanel != null) {
			stylePanel.removeFromParent();
		}
	}

	public void closeTablePanel() {
		isShowing = false;
		hasBeenClosed = true;
		MainLayoutPanel.removeElementFromMainArea(pagerMovableArea);
		if(stylePanel != null) {
			stylePanel.removeFromParent();
		}
		getEntity().clearAll();
	}

	public boolean hasBeenClosed() {
		return hasBeenClosed;
	}

	public void removeData() {
		dataProvider.getList().clear();
		filteredList.clear();
	}

	public boolean getIsHidingTable() {
		return isHidingTable;
	}

	public void registerObserver(AbstractTableObserver observer) {
		observers.add(observer);
	}

	public void unregisterObserver(AbstractTableObserver observer) {
		if(observers.contains(observer)) {
			observers.remove(observer);
		}
	}

	private void notifyObserversRowsChange(int numberOfVisibleRows) {
		for (AbstractTableObserver observer : observers) {
			observer.numberOfShownRowsChanged(numberOfVisibleRows);
		}
	}
	
	public JSONObject exportAsJSON() {
		JSONObject jsonData = new JSONObject();
		TapRowList rowList = getEntity().getMetadata();
		if (rowList == null) {
			return new JSONObject();
		}
		
		for (int rowIndex = 0; rowIndex < rowList.getData().size(); rowIndex++) {
			JSONObject rowData = new JSONObject();
			for (int cellIndex = 0; cellIndex < rowList.getMetadata().size(); cellIndex++) {
				Object cellData = rowList.getData().get(rowIndex).get(cellIndex); 
				String cellString;
				if(cellData != null) {
					cellString = cellData.toString();
				}else {
					cellString = "";
				}
				rowData.put(rowList.getMetadata().get(cellIndex).getName(), new JSONString(cellString));
			}
			jsonData.put(Integer.toString(rowIndex), rowData);
		}
		return jsonData;
	}

	public void exportAsCSV() {

		String csvData = "data:text/csv;charset=utf-8,";
		final String separator = getEntity().getDescriptor().getAdsAuthorSeparator();

		// Adds headers to csv
		int addedCols = 0;
		for (int cellIndex = 0; cellIndex < table.getColumnCount(); cellIndex++) {
			final String label = getLabelTextFromHeader(table.getHeader(cellIndex));
			if (!label.isEmpty()) {
				csvData += ((addedCols == 0) ? "" : ",") + getLabelTextFromHeader(table.getHeader(cellIndex));
				addedCols++;
			}
		}
		csvData += "\n";

		// Adds data to csv
		TapRowList rowList = getEntity().getMetadata();
		for (int rowIndex = 0; rowIndex < rowList.getData().size(); rowIndex++) {
			for (int cellIndex = 0; cellIndex < rowList.getMetadata().size(); cellIndex++) {
				csvData += ((cellIndex == 0) ? "" : ",") + "\""
						+ (rowList.getData().get(rowIndex)).get(cellIndex).toString().replaceAll(separator, ";") + "\"";
			}
			csvData += "\n";
		}

		UrlUtils.saveRawToFile(UrlUtils.getValidFilename(getEntity().getEsaSkyUniqId()) + ".csv", csvData);
	}

	public void exportAsVOTABLE() {

		String votData = "data:text/xml;charset=utf-8,";
		final String separator = getEntity().getDescriptor().getAdsAuthorSeparator();

		// Add VOT XML Schema
		votData += "<VOTABLE version=\"1.3\" xmlns=\"//www.ivoa.net/xml/VOTable/v1.3\">\n";
		votData += "<RESOURCE type=\"" + getEntity().getEsaSkyUniqId() + " publications\">\n";
		votData += "<TABLE>\n";

		// Adds headers to xml
		for (int cellIndex = 0; cellIndex < table.getColumnCount(); cellIndex++) {
			final String label = getLabelTextFromHeader(table.getHeader(cellIndex));
			if (!label.isEmpty()) {
				votData += "<FIELD arraysize=\"*\" datatype=\"char\" name=\""
						+ SafeHtmlUtils.htmlEscape(getLabelTextFromHeader(table.getHeader(cellIndex))) + "\"/>\n";
			}
		}

		// Adds data to csv
		votData += "<DATA>\n";
		votData += "<TABLEDATA>\n";
		TapRowList rowList = getEntity().getMetadata();
		for (int rowIndex = 0; rowIndex < rowList.getData().size(); rowIndex++) {
			votData += "    <TR>\n";
			for (int cellIndex = 0; cellIndex < rowList.getMetadata().size(); cellIndex++) {
				votData += "        <TD>"
						+ SafeHtmlUtils.htmlEscape(
								(rowList.getData().get(rowIndex)).get(cellIndex).toString().replaceAll(separator, ";"))
						+ "</TD>\n";
			}
			votData += "    </TR>\n";
		}
		votData += "</TABLEDATA>\n";
		votData += "</DATA>\n";
		votData += "</TABLE>\n";
		votData += "</RESOURCE>\n";
		votData += "</VOTABLE>\n";

		UrlUtils.saveRawToFile(UrlUtils.getValidFilename(getEntity().getEsaSkyUniqId()) + ".vot", votData);
	}

	public String getFullId() {
		return getEntity().getContext().toString() + "-" + getLabel();
	}
	
	protected void addColumnGroupHeader(Widget header) {
		columnGroupHeader.add(header);
		columnGroupHeader.setVisible(true);
	}
	
	protected void setColumnGroupHeight(int heightInPx) {
		columnGroupHeader.setHeight(heightInPx + "px");
		table.getElement().getStyle().setProperty("height", "calc(100% - " + heightInPx + "px)");
	}
	
	public void setEmptyTable(String emptyTableText) {
		loadingSpinner.setVisible(false);
		emptyTableLabel.setText(emptyTableText);
	}
	
    public void resizeColumnGroupHeader() {
    	if(isGroupHeaderActive) {
    		Element filterButton = Document.get().getElementById(getEntity().getEsaSkyUniqId() + separatorIndex);
    		Element startElement = filterButton;
    		while(filterButton != null && !startElement.hasTagName("th")) {
    			startElement = startElement.getParentElement();
    		}
    		if(startElement != null && startElement.getOffsetLeft() > 0) {
    			groupOneHeader.setWidth(startElement.getOffsetLeft() + "px");
    			groupTwoHeader.setWidth((table.getElement().getOffsetWidth() - startElement.getOffsetLeft()) + "px");
    			groupTwoHeader.getElement().getStyle().setLeft(startElement.getOffsetLeft(), Unit.PX);
    			setColumnGroupHeight(groupTwoHeader.getElement().getOffsetHeight());
    		} else {
    			loadingTimer.schedule(10);
    		}
    	}
    }
    
	public void setSeparator(int index) {
		this.separatorIndex = index;
		groupTwoLabel.setText(GUISessionStatus.getTrackedSso().name);
	}
	
	public void activateGroupHeaders() {
		isGroupHeaderActive = true;
		addColumnGroupHeader(groupOneHeader);
		addColumnGroupHeader(groupTwoHeader);
	}
	
	public abstract void showStylePanel(int x, int y);
	
	public void downloadSelected(DDRequestForm ddForm) {
	}
	
	public void updateData() {
		clearTable();
		getEntity().fetchData(this);
	}
	
	private void afterForeignRowSelection(boolean gettingSelected, ShapeId changedRow) {
		Set<ShapeId> changedRows = new HashSet<ShapeId>();
		changedRows.add(changedRow);
		fireSelectionEvent(gettingSelected, changedRows);
	}

	private void fireSelectionEvent(boolean gettingSelected, Set<ShapeId> changedRows) {
		if(gettingSelected) {
			getEntity().selectShapes(changedRows);
		} else {
			getEntity().deselectShapes(changedRows);
		}
	}


}
