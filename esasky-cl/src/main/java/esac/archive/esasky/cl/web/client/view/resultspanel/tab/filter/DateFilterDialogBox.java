/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.status.ScreenSizeObserver;
import esac.archive.esasky.cl.web.client.status.ScreenSizeService;
import esac.archive.esasky.cl.web.client.status.ScreenWidth;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.SignButton.SignType;

public class DateFilterDialogBox extends FilterDialogBox {

	private final Resources resources = GWT.create(Resources.class);
	private CssResource style;

	private FilterObserver filterObserver;
	private JavaScriptObject fromCalendar;
	private JavaScriptObject toCalendar;
	private boolean hasCalendarsBeenAddedToDialogBox = false;
	private String startDate = "1800-01-01";
	private String endDate = "3000-01-01";
	private String currentFromDate = startDate;
	private String currentToDate = endDate;
	private FlowPanel dateFilterContainer = new FlowPanel();
	private FlowPanel toContainer = new FlowPanel();
	private EsaSkyButton resetButton = new EsaSkyButton(this.resources.resetIcon());

	private final String fromInputId;
	private final String toInputId;
	private TextBox fromInput = new TextBox();
	private TextBox toInput = new TextBox();

	protected FilterTimer filterTimer = new FilterTimer();

	public interface Resources extends ClientBundle {
		@Source("dateFilterDialogBox.css")
		@CssResource.NotStrict
		CssResource style();
		
		@Source("reset-outline.png")
		ImageResource resetIcon();
	}

	public DateFilterDialogBox(String tapName, String columnName, final String filterButtonId, final FilterObserver filterObserver) {
		super(tapName, filterButtonId);
		this.style = this.resources.style();
		this.style.ensureInjected();
		
		this.filterObserver = filterObserver;
		
        HTML columnNameHTML = new HTML(columnName.replaceAll("_", " "));
        columnNameHTML.addStyleName("filterColumnName");
        
        SignButton minimizeButton = new SignButton(SignType.MINUS);
        minimizeButton.addStyleName("filterButton__minimize");
        minimizeButton.setSmallStyle();
        minimizeButton.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });

		FlowPanel fromContainer = new FlowPanel();
		fromContainer.addStyleName("dateInputContainer");
		fromInput.setReadOnly(true);
		fromInput.addStyleName("dateFilterInput");
		fromInput.addStyleName("dateFromFilterInput");
		fromInputId = filterButtonId.replaceAll("(\\(|\\)| )", "_") + "fromInput";
		fromInput.getElement().setId(fromInputId);
		fromContainer.add(fromInput);

		toContainer.addStyleName("toFilterContainer");
		toContainer.addStyleName("dateInputContainer");
		toInput.setReadOnly(true);
		toInput.addStyleName("dateFilterInput");
		toInputId = filterButtonId.replaceAll("(\\(|\\)| )", "_") + "toInput";
		toInput.getElement().setId(toInputId);
		toContainer.add(toInput);

		resetButton.addStyleName("dateResetButton");
		resetButton.setTitle(TextMgr.getInstance().getText("dateFilter_resetFilter"));
		resetButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setCalendarDate(fromCalendar, startDate);
				setCalendarDate(toCalendar, endDate);
				filterTimer.setNewRange(startDate, endDate);
			}
		});
		
		FlowPanel container = new FlowPanel();
		container.add(columnNameHTML);
		container.add(minimizeButton);
		dateFilterContainer.addStyleName("dateFilterContainer");
		
		dateFilterContainer.add(fromContainer);
		dateFilterContainer.add(resetButton);
		dateFilterContainer.add(toContainer);
		addElementNotAbleToInitiateMoveOperation(dateFilterContainer.getElement());
		
		container.add(dateFilterContainer);
		
		ScreenSizeService.getInstance().registerObserver(new ScreenSizeObserver() {
			
			@Override
			public void onScreenSizeChange() {
				setResponsiveStyle();
			}
		});
		setResponsiveStyle();
		
		setWidget(container);
	}
	
	@Override
	public void show() {
		super.show();
		if(!hasCalendarsBeenAddedToDialogBox) {
			hasCalendarsBeenAddedToDialogBox = true;
			fromCalendar = createCalendar(this, fromInputId, currentFromDate);
			toCalendar = createCalendar(this, toInputId, currentToDate);
			setCalendarDate(fromCalendar, currentFromDate);
			setCalendarDate(toCalendar, currentToDate);
		}
		toInput.setFocus(true);
		fromInput.setFocus(true);
	}

	@Override
	public boolean isFilterActive() {
			return (currentFromDate != null && !currentFromDate.equals(startDate))
					|| (currentToDate != null && !currentToDate.equals(endDate));
		}

	private native JavaScriptObject createCalendar(DateFilterDialogBox instance, final String containerId, String startDate) /*-{
    	return $wnd.datepicker("#" + containerId, {startDay: 1, 
    		dateSelected: new Date(startDate), noPosition: true, onSelect: 
    			function(datePickerInstance) {
    				var date = datePickerInstance.dateSelected.getFullYear() 
    					+ "-" + ("0" +  (datePickerInstance.dateSelected.getMonth() + 1)).slice(-2) 
    					+ "-" + ("0" +  datePickerInstance.dateSelected.getDate()).slice(-2);
                  	instance.@esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.DateFilterDialogBox::fireDateChangedEvent(Ljava/lang/String;Ljava/lang/String;)(date, containerId);
    			},
    			onShow: 
                    function(datePickerInstance) {
                        instance.@esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.DateFilterDialogBox::fireShowEvent()();
                    } 
    		});
    }-*/;

	public void fireShowEvent() {
	    setSuggestedPositionCenter();
	}
	public void fireDateChangedEvent(String newDate, String datePickerId) {
		if(fromInputId.equals(datePickerId)) {
			filterTimer.setNewRange(newDate, currentToDate);	
		}
		else if(toInputId.equals(datePickerId)) {
			filterTimer.setNewRange(currentFromDate, newDate);	
		}
	}
	
	public void setStartRange(String startDate, String endDate){
		boolean filterWasActive = isFilterActive();

		if(!filterWasActive) {
			currentFromDate = startDate;
			currentToDate = endDate;
		}
		this.startDate = startDate;
		this.endDate = endDate;

		if(hasCalendarsBeenAddedToDialogBox) {
			setCalendarDate(fromCalendar, startDate);
			setCalendarDate(toCalendar, endDate);
		}

		ensureCorrectFilterButtonStyle();
	};

	public String getCurrentFromDate() {
		return currentFromDate;
	}

	public String getCurrentToDate() {
		return currentToDate;
	}

	private native void setCalendarDate(JavaScriptObject calendar, String date) /*-{
    	calendar.setDate(new Date(date));
    }-*/;
	
	private void setResponsiveStyle() {
		ScreenWidth screenWidth = ScreenSizeService.getInstance().getScreenSize().getWidth();
		if(screenWidth.getPxSize() <= ScreenWidth.SMALL.getPxSize()) {
			setSmallStyle();
		} else {
			setDefaultStyle();
		}
	}
	
	private void setSmallStyle() {
		dateFilterContainer.setHeight("462px");
		dateFilterContainer.getElement().getStyle().setDisplay(Display.BLOCK);

		toContainer.getElement().getStyle().setTop(190, Unit.PX);
		
		resetButton.getElement().getStyle().setPosition(Position.RELATIVE);
		resetButton.getElement().getStyle().setLeft(204, Unit.PX);
		resetButton.getElement().getStyle().setTop(-48, Unit.PX);
	}
	
	private void setDefaultStyle() {
		dateFilterContainer.setHeight("236px");
		dateFilterContainer.getElement().getStyle().setProperty("display", "inline-flex");
		resetButton.getElement().getStyle().setPosition(Position.STATIC);
		toContainer.getElement().getStyle().setTop(0, Unit.PX);
	}

	protected class FilterTimer extends Timer{
		
		private String lastFromDate = currentFromDate;
		private String lastToDate = currentToDate;

		@Override
		public void run() {
			if(lastFromDate != null && lastFromDate.equals(currentFromDate)
					&& lastToDate != null && lastToDate.equals(currentToDate)) {
				return;
			}
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					String filter = "";
					if(!currentFromDate.equals(startDate)) {
						filter += currentFromDate;
					}
					
					filter += ",";
					
					if(!currentToDate.equals(endDate)) {
						filter += currentToDate;
					}
					
					filterObserver.onNewFilter(filter);
					ensureCorrectFilterButtonStyle();
				}
			});
		}

		public void setNewRange(String fromDate, String toDate) {
			currentFromDate = fromDate;
			currentToDate = toDate;
			ensureCorrectFilterButtonStyle();
			schedule(100);
		}
	}
	
    @Override
    public void setValuesFromString(String filterString) {
		String[] andSplit = filterString.split(" AND ");
		String lowerString = null;
		String upperString = null;
		if(andSplit.length > 1) {
			lowerString = andSplit[0];
			upperString = andSplit[1];
		}else if(andSplit[0].contains(">")) {
			lowerString = andSplit[0];
		}else {
			upperString = andSplit[0];
		}
		
		String minValue = currentFromDate;
		String maxValue = currentToDate;
		if(lowerString != null) {
			minValue = lowerString.split(">=")[1];
			minValue = minValue.trim().replace("'","");
		}
		if(upperString != null) {
			maxValue = upperString.split("<=")[1];
			maxValue = maxValue.trim().replace("'","");
		}
		
		setValues(minValue, maxValue);
    }
    
    public void setValues(String startDate, String endDate) {
		this.currentFromDate = startDate;
		this.currentToDate = endDate;

		if(hasCalendarsBeenAddedToDialogBox) {
			setCalendarDate(fromCalendar, startDate);
			setCalendarDate(toCalendar, endDate);
		}
		filterTimer.setNewRange(startDate, endDate);
    }
    
}
