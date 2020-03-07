//package esac.archive.esasky.cl.web.client.view.resultspanel;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import com.allen_sauer.gwt.log.client.Log;
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.core.client.JavaScriptObject;
//import com.google.gwt.core.client.JsonUtils;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.event.logical.shared.ResizeEvent;
//import com.google.gwt.event.logical.shared.ResizeHandler;
//import com.google.gwt.http.client.URL;
//import com.google.gwt.resources.client.ClientBundle;
//import com.google.gwt.resources.client.CssResource;
//import com.google.gwt.user.client.Timer;
//import com.google.gwt.user.client.ui.Anchor;
//import com.google.gwt.user.client.ui.CheckBox;
//import com.google.gwt.user.client.ui.FlowPanel;
//import com.google.gwt.user.client.ui.HTML;
//import com.google.gwt.user.client.ui.Label;
//
//import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
//import esac.archive.esasky.cl.web.client.view.common.AutoHidingMovablePanel;
//import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
//import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
//import esac.archive.esasky.cl.web.client.view.resultspanel.TabulatorWrapper.TabulatorCallback;
//import esac.archive.esasky.cl.web.client.CommonEventBus;
//import esac.archive.esasky.cl.web.client.callback.StandardCallback;
//import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
//import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
//import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
//import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
//import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
//import esac.archive.esasky.cl.web.client.utility.JSONUtils;
//
//public class ToggleColumnsDialogBox extends AutoHidingMovablePanel {
//	private final Resources resources = GWT.create(Resources.class);
//	private CssResource style;
//
//	public interface Resources extends ClientBundle {
//		@Source("toggleColumnsDialogBox.css")
//		@CssResource.NotStrict
//		CssResource style();
//	}
//
//	private final CloseButton closeButton;
//	private ColumnSettingInfo[] columns;
//	private ColumnSettingInfo[] columnDefinitions;
//	private Label missionLabel;
//	private final FlowPanel contentContainer = new FlowPanel();
//	
//	private TabulatorCallback onRowSelection;
//	private TabulatorCallback onRowDeselection;
//	
//	
//	public ToggleColumnsDialogBox(String mission, ColumnSettingInfo[] columns, ColumnSettingInfo[] columnDefinitions,
//		TabulatorCallback onRowSelection, TabulatorCallback onRowDeselection) {
//		super(GoogleAnalytics.CAT_ToggleColumns);
//		this.style = this.resources.style();
//		this.style.ensureInjected();
//		this.columns = columns;
//		this.columnDefinitions = columnDefinitions;
//		this.onRowSelection = onRowSelection;
//		this.onRowDeselection = onRowDeselection;
//
//		contentContainer.addStyleName("toggleColumns__contentContainer");
//		contentContainer.getElement().setId("toggleColumns__contentContainer");
//
//		closeButton = new CloseButton();
//		closeButton.addStyleName("toggleColumns__closeButton");
//		closeButton.addClickHandler(new ClickHandler() {
//			public void onClick(final ClickEvent event) {
//				hide();
//			}
//		});
//
//		missionLabel = new Label(mission);
//		missionLabel.setStyleName("toggleColumns__missionLabel");
//
//		FlowPanel contentAndCloseButton = new FlowPanel();
//		contentAndCloseButton.add(missionLabel);
//		contentAndCloseButton.add(closeButton);
//		contentAndCloseButton.add(contentContainer);
//		add(contentAndCloseButton);
//
//		addStyleName("toggleColumns__dialogBox");
//		
////		FlowPanel header = new FlowPanel();
////		
////		FlowPanel showHideHeader = new FlowPanel();
////		Label showLabel = new Label("Visible");
////		CheckBox selectAllCheckBox = new CheckBox();
////		showHideHeader.add(showLabel);
////		showHideHeader.add(selectAllCheckBox);
////		showHideHeader.addStyleName("toggleColumns__header");
////		header.add(showHideHeader);
////		
////		HTML columnNameHeader = new HTML("Column name");
////		columnNameHeader.addStyleName("toggleColumns__header");
////		header.add(columnNameHeader);
////		
////		Label columnDescriptionHeader = new Label("Description");//TODO internationalization
////		columnDescriptionHeader.addStyleName("toggleColumns__header");
////		header.add(columnDescriptionHeader);
////		
////		contentContainer.add(header);
////		
////		for(ColumnSettingInfo column : columns) {
////			FlowPanel row = new FlowPanel();
////			
////			CheckBox checkbox = new CheckBox();
////			checkbox.addStyleName("toggleColumns__checkbox");
////			row.add(checkbox);
////			
////			HTML columnName = new HTML(column.label);
////			columnName.addStyleName("toggleColumns__columnName");
////			row.add(columnName);
////			
////			Label columnDescription = new Label(column.description);
////			columnDescription.addStyleName("toggleColumns__columnDescription");
////			row.add(columnDescription);
////			
////			contentContainer.add(row);
////		}
//		
//
//		addElementNotAbleToInitiateMoveOperation(contentContainer.getElement());
//		show();
//
//		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
//
//			@Override
//			public void onResize(ResizeEvent arg0) {
//				setMaxHeight(arg0.getHeight());
//			}
//		});
//	}
//
//	@Override
//	protected void onLoad() {
//		super.onLoad();
////		new TabulatorWrapper("toggleColumns__contentContainer", columns, columnDefinitions, onRowSelection, onRowDeselection);
//		
//	}
//
//	private void setMaxHeight(int height) {
//		if (height > MainLayoutPanel.getMainAreaHeight() - 30 - 2 - missionLabel.getOffsetHeight()) {
//			height = MainLayoutPanel.getMainAreaHeight() - 30 - 2 - missionLabel.getOffsetHeight();
//		}
//		contentContainer.getElement().getStyle().setPropertyPx("maxHeight", height);
//	}
//	
//}
