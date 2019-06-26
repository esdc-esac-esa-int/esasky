package esac.archive.esasky.cl.web.client.view.resultspanel.tab;

/*
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * //www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
import java.util.ArrayList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CommonResources;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.animation.AnimationObserver;
import esac.archive.esasky.cl.web.client.view.animation.EsaSkyAnimation;
import esac.archive.esasky.cl.web.client.view.animation.LeftSlideAnimation;
import esac.archive.esasky.cl.web.client.view.common.buttons.ScrollDisablablePushButton;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.ResultsPanel;

public class ScrollTabLayoutPanel extends ResizeComposite implements ProvidesResize, HasSelectionHandlers<Integer> {

	/**
	 * @author ESDC team Copyright (c) 2015- European Space Agency
	 */
	private class Tab extends SimplePanel {

		private Element inner;
		private boolean replacingWidget;
		private DataPanelDraggablePanel draggableArea;

		public Tab(final Widget child) {
			this(child, TAB_STYLE, TAB_INNER_STYLE);
		}

		private Tab(final Widget child, final String tabStyle, final String tabInnerStyle) {
			super(Document.get().createDivElement());
			getElement().appendChild(this.inner = Document.get().createDivElement());

			draggableArea = new DataPanelDraggablePanel();
			FlowPanel horizontalLine = new FlowPanel();
			horizontalLine.addStyleName("draggableArea__horizontal");
			draggableArea.add(horizontalLine);
			FlowPanel secondHorizontalLine = new FlowPanel();
			secondHorizontalLine.addStyleName("draggableArea__horizontal-second");
			draggableArea.add(secondHorizontalLine);
			
			FlowPanel tabContainer = new FlowPanel();
			tabContainer.add(draggableArea);
			child.addStyleName("dataPanelTabChild");
			tabContainer.add(child);

			setWidget(tabContainer);
			setStyleName(tabStyle);
			this.inner.setClassName(tabInnerStyle);

			getElement().addClassName(CommonResources.getInlineBlockStyle());
		}
		
		public HandlerRegistration addClickHandler(final ClickHandler handler) {
			return addDomHandler(handler, ClickEvent.getType());
		}

		@Override
		public boolean remove(final Widget w) {
			/*
			 * Removal of items from the TabBar is delegated to the
			 * TabLayoutPanel to ensure consistency.
			 */
			int index = ScrollTabLayoutPanel.this.tabs.indexOf(this);
			if (this.replacingWidget || index < 0) {
				/*
				 * The tab contents are being replaced, or this tab is no longer
				 * in the panel, so just remove the widget.
				 */
				return super.remove(w);
			} else {
				// Delegate to the TabLayoutPanel.
				return ScrollTabLayoutPanel.this.remove(index);
			}
		}

		/**
		 * setSelected().
		 * 
		 * @param selected
		 *            Input boolean
		 */
		public void setSelected(final boolean selected) {
			if (selected) {
				addStyleDependentName("selected");
			} else {
				removeStyleDependentName("selected");
			}
		}

		@Override
		public void setWidget(final Widget w) {
			this.replacingWidget = true;
			super.setWidget(w);
			this.replacingWidget = false;
		}

		@Override
		protected com.google.gwt.user.client.Element getContainerElement() {
			return this.inner.cast();
		}
		
		private boolean isBeingDragged() {
			return draggableArea.isBeingDragged();
		}
	}

	/**
	 * This extension of DeckLayoutPanel overrides the public mutator methods to
	 * prevent external callers from adding to the state of the DeckPanel.
	 * <p>
	 * Removal of Widgets is supported so that WidgetCollection.WidgetIterator
	 * operates as expected.
	 * </p>
	 * <p>
	 * We ensure that the DeckLayoutPanel cannot become of of sync with its
	 * associated TabBar by delegating all mutations to the TabBar to this
	 * implementation of DeckLayoutPanel.
	 * </p>
	 */
	private class TabbedDeckLayoutPanel extends DeckLayoutPanel {

		@Override
		public void add(final Widget w) {
			throw new UnsupportedOperationException("Use TabLayoutPanel.add() to alter the DeckLayoutPanel");
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException("Use TabLayoutPanel.clear() to alter the DeckLayoutPanel");
		}

		@Override
		public void insert(final Widget w, final int beforeIndex) {
			throw new UnsupportedOperationException("Use TabLayoutPanel.insert() to alter the DeckLayoutPanel");
		}

		@Override
		public boolean remove(final Widget w) {
			/*
			 * Removal of items from the DeckLayoutPanel is delegated to the
			 * TabLayoutPanel to ensure consistency.
			 */
			return ScrollTabLayoutPanel.this.remove(w);
		}
		
		@Override
		public AbstractTablePanel getWidget(int index) {
			return (AbstractTablePanel)super.getWidget(index);
		}

		/**
		 * insertProtected().
		 * 
		 * @param w
		 *            Input Widget.
		 * @param beforeIndex
		 *            Input integer
		 */
		protected void insertProtected(final AbstractTablePanel w, final int beforeIndex) {
			super.insert(w, beforeIndex);
		}

		/**
		 * removeProtected().
		 * 
		 * @param w
		 *            Input widget.
		 */
		protected void removeProtected(final AbstractTablePanel w) {
			super.remove(w);
		}
	}

	private static final String CONTENT_CONTAINER_STYLE = "scrollTabLayoutPanelContentContainer";
	private static final String CONTENT_STYLE = "scrollTabLayoutPanelContent";
	private static final String MAIN_STYLE = "scrollTabLayoutPanel";
	private static final String SCROLLBAR_STYLE = "scrollBar";
	private static final String TAB_STYLE = "scrollTabLayoutPanelTab";
	private static final String TABS_STYLE = "scrollTabs";
	private static final String TAB_INNER_STYLE = "scrollTabLayoutPanelTabInner";

	private final TabbedDeckLayoutPanel deckPanel = new TabbedDeckLayoutPanel();
	private final FlowPanel tabBar = new FlowPanel();
	private final ArrayList<Tab> tabs = new ArrayList<Tab>();
	private int selectedIndex = -1;

	private LayoutPanel tabLayoutPanel = new LayoutPanel();
	/* scroll related variables */
	private final DockLayoutPanel scrollBar = new DockLayoutPanel(Unit.PX);
	private ScrollDisablablePushButton scrollLeftButton;
	private ScrollDisablablePushButton scrollRightButton;
	private ResizeHandler mainAreaResizeHandler;
	private static final int SCROLL_INTERVAL = 250;
	private HandlerRegistration selectionHandler;
	private boolean showScroll;
	
	private EsaSkyAnimation tabBarAnimation;

	private Resources resources = GWT.create(Resources.class);
	private CssResource style;

	/**
	 * Creates an empty tab panel.
	 *
	 * @param barHeight
	 *            the size of the tab bar
	 * @param barUnit
	 *            the unit in which the tab bar size is specified
	 */
	public ScrollTabLayoutPanel(final double barHeight, final Unit barUnit) {
		this(barHeight, barUnit, true);
	}

	/**
	 * Creates an empty tab panel.
	 *
	 * @param barHeight
	 *            the size of the tab bar
	 * @param barUnit
	 *            the unit in which the tab bar size is specified
	 * @param scroll
	 *            whether scroll is shown or not
	 */
	public ScrollTabLayoutPanel(final double barHeight, final Unit barUnit, final boolean scroll) {
		this.style = this.resources.style();
		this.style.ensureInjected();

		this.showScroll = scroll;

		initWidget(tabLayoutPanel);

		if (this.showScroll) {
			// Add the scroll bar to the panel.
			initScrollBar();
			tabLayoutPanel.add(this.scrollBar);
		}

		// Add the deck panel to the panel.
		this.deckPanel.addStyleName(CONTENT_CONTAINER_STYLE);
		tabLayoutPanel.add(this.deckPanel);
		tabLayoutPanel.setWidgetLeftRight(this.deckPanel, 0, Unit.PX, 0, Unit.PX);
		tabLayoutPanel.setWidgetTopBottom(this.deckPanel, barHeight, barUnit, 0, Unit.PX);
        
		setStyleName(MAIN_STYLE);
	}

	/**
	 * Adds a widget to the panel. If the Widget is already attached, it will be
	 * moved to the right-most index.
	 *
	 * @param child
	 *            the widget to be added
	 * @param tab
	 *            the widget to be placed in the associated tab
	 */
	public final void add(final AbstractTablePanel child, final MissionTabButtons tab) {
		insert(child, tab, deckPanel.getWidgetCount());
		selectTab(child);
		checkIfScrollButtonsNecessary();
	}

	public int getWidgetIndex(Widget widget){
		return deckPanel.getWidgetIndex(widget);
	}
	
	@Override
	public final HandlerRegistration addSelectionHandler(final SelectionHandler<Integer> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	/**
	 * Gets the index of the currently-selected tab.
	 *
	 * @return the selected index, or <code>-1</code> if none is selected.
	 */
	public final int getSelectedIndex() {
		return this.selectedIndex;
	}

	/**
	 * Returns the widget at the given index.
	 */
	public final AbstractTablePanel getWidget(final int index) {
		return this.deckPanel.getWidget(index);
	}

	/**
	 * Inserts a widget into the panel. If the Widget is already attached, it
	 * will be moved to the requested index.
	 *
	 * @param child
	 *            the widget to be added
	 * @param tab
	 *            the widget to be placed in the associated tab
	 * @param beforeIndex
	 *            the index before which it will be inserted
	 */
	public final void insert(final AbstractTablePanel child, final MissionTabButtons tab, final int beforeIndex) {
		insert(child, new Tab(tab), beforeIndex);
	}

	public final boolean remove(final int index) {
	    
		if ((index < 0) || (index >= deckPanel.getWidgetCount())) {
			return false;
		}

		AbstractTablePanel child = getWidget(index);
		this.tabBar.remove(index);
		this.deckPanel.removeProtected(child);
		child.removeStyleName(CONTENT_STYLE);

		Tab tab = this.tabs.remove(index);
		tab.getWidget().removeFromParent();

		if (index == this.selectedIndex) {
			// If the selected tab is being removed, select the first tab (if
			// there
			// is one).
			this.selectedIndex = -1;
			if (deckPanel.getWidgetCount() > 0) {
				selectTab(deckPanel.getWidget(0));
			}
		} else if (index < this.selectedIndex) {
			// If the selectedIndex is greater than the one being removed, it
			// needs
			// to be adjusted.
			--this.selectedIndex;
		}
		checkIfScrollButtonsNecessary();
		return true;
	}

	public final boolean remove(final Widget w) {
		int index = deckPanel.getWidgetIndex(w);
		if (index == -1) {
			return false;
		}
		return remove(index);
	}

	public final void selectTab(AbstractTablePanel tablePanel) {
		selectTab(deckPanel.getWidgetIndex(tablePanel));
	}

	public final void selectTab(final int newlySelectedIndex) {
		assert (newlySelectedIndex >= 0) && (newlySelectedIndex < deckPanel.getWidgetCount()) : "Index out of bounds";
		boolean dataPanelWasOpen = GUISessionStatus.isDataPanelOpen(); 
		if(!dataPanelWasOpen){
			ResultsPanel.openDataPanel();
		} 
		
		if (newlySelectedIndex == this.selectedIndex) {
			return;
		}	

		// Update the tabs being selected and unselected.
		if (this.selectedIndex != -1) {
			this.tabs.get(this.selectedIndex).setSelected(false);
		}

		this.deckPanel.showWidget(newlySelectedIndex);
		this.tabs.get(newlySelectedIndex).setSelected(true);

		if(dataPanelWasOpen){
			deckPanel.getWidget(newlySelectedIndex).selectTablePanel();
			for(int i = 0; i < deckPanel.getWidgetCount(); i++) {
				if(i != newlySelectedIndex) {
					deckPanel.getWidget(i).deselectTablePanel();
				}
			}
		}
		
		this.selectedIndex = newlySelectedIndex;
		SelectionEvent.fire(this, newlySelectedIndex);
	}

	/**
	 * insert().
	 * 
	 * @param child
	 *            Input Widget
	 * @param tab
	 *            Input Tab object
	 * @param beforeIndex
	 *            Input integer value.
	 */
	private void insert(final AbstractTablePanel child, final Tab tab, int beforeIndex) {

		assert (beforeIndex >= 0) && (beforeIndex <= deckPanel.getWidgetCount()) : "beforeIndex out of bounds";

		// Check to see if the TabPanel already contains the Widget. If so,
		// remove it and see if we need to shift the position to the left.
		int idx = deckPanel.getWidgetIndex(child);
		if (idx != -1) {
			remove(child);
			if (idx < beforeIndex) {
				beforeIndex--;
			}
		}

		this.deckPanel.insertProtected(child, beforeIndex);
		this.tabs.add(beforeIndex, tab);

		this.tabBar.insert(tab, beforeIndex);
		tab.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				if(!tab.isBeingDragged()) {
					selectTab(child);
				}
			}
		});

		child.addStyleName(CONTENT_STYLE);
		
		if (this.selectedIndex == -1) {
			selectTab(deckPanel.getWidget(0));
		} else if (this.selectedIndex >= beforeIndex) {
			// If we inserted before the currently selected tab, its index has
			// just
			// increased.
			this.selectedIndex++;
		}
	}

	@Override
	protected final void onLoad() {
		super.onLoad();
		if (this.mainAreaResizeHandler == null) {
			this.mainAreaResizeHandler = new ResizeHandler() {

				// @Override
				@Override
				public void onResize(final ResizeEvent event) {
					checkIfScrollButtonsNecessary();
				}
			};
			MainLayoutPanel.addMainAreaResizeHandler(mainAreaResizeHandler);
		}

		if (this.selectionHandler == null) {
			this.selectionHandler = addSelectionHandler(new SelectionHandler<Integer>() {

				@Override
				public void onSelection(final SelectionEvent<Integer> selectedEvent) {
					doOnSelectTab(selectedEvent.getSelectedItem());
				}
			});
		}
	}

	@Override
	protected final void onUnload() {
		super.onUnload();

		if (this.mainAreaResizeHandler != null) {
			MainLayoutPanel.removeMainAreaResizeHandler(mainAreaResizeHandler);
			this.mainAreaResizeHandler = null;
		}

		if (this.selectionHandler != null) {
			this.selectionHandler.removeHandler();
			this.selectionHandler = null;
		}
	}

	private void checkIfScrollButtonsNecessary() {
		if (!this.showScroll) {
			return;
		}
		// Defer size calculations until sizes are available, when calculating
		// immediately after
		// add(), all size methods return zero
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

			@Override
			public void execute() {
				// @Override
				boolean scrollPossible = isScrollingNecessary();
				if (scrollPossible) {
					scrollLeftButton.setVisible();
					scrollRightButton.setVisible();
					scrollBar.setStyleName("scrollBarWithButtons");

					int currentLeft = parsePosition(ScrollTabLayoutPanel.this.tabBar.getElement().getStyle().getLeft());
					int rightOfLastTab = getRightOfWidget(getLastTab());

					if (getTabBarWidth() - currentLeft >= rightOfLastTab) {
						currentLeft = getTabBarWidth() - rightOfLastTab;
					} 
					if(!tabBarAnimation.isRunning()){
						scrollTo(currentLeft);
					}
					
				} else {
					resetScrollPosition();
					scrollLeftButton.setCollapsed();
					scrollRightButton.setCollapsed();
					scrollBar.setStyleName("scrollBarNoButtons");
				}
			}
		});
	}
	
	/**
	 * createScrollClickHandler().
	 * 
	 * @param diff
	 *            Input integer
	 * @return ClickHandler.
	 */
	private ClickHandler createScrollClickHandler(final int diff) {
		return new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				Widget lastTab = getLastTab();

				int newLeft = parsePosition(ScrollTabLayoutPanel.this.tabBar.getElement().getStyle().getLeft()) + diff;
				int rightOfLastTab = getRightOfWidget(lastTab);

				// Prevent scrolling the last tab too far away form the right
				// border,
				// or the first tab further than the left border position
				if (newLeft > 0) {
					newLeft = 0;
				}
				if (getTabBarWidth() - newLeft >= rightOfLastTab) {
					newLeft = getTabBarWidth() - rightOfLastTab;
				}
				scrollTo(newLeft);
			}
		};
	}

	private void updateScrollButtonsEnabled(double currentLeft) {
		if (currentLeft >= 0) {
			scrollLeftButton.disableButton();
		} else {
			scrollLeftButton.enableButton();
		}

		if (getTabBarWidth() - currentLeft >= getRightOfWidget(getLastTab())) {
			scrollRightButton.disableButton();
		} else {
			scrollRightButton.enableButton();
		}
	}

	private void doOnSelectTab(final int selected) {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

			@Override
			public void execute() {
				if (isScrollingNecessary()) {
					Widget selectedTab = tabBar.getWidget(selected);
					int rightOfSelectedTab = getRightOfWidget(selectedTab);
					int leftOfSelectedTab = selectedTab.getElement().getOffsetLeft();
					int currentLeft = parsePosition(ScrollTabLayoutPanel.this.tabBar.getElement().getStyle().getLeft());
					if (rightOfSelectedTab > getTabBarWidth() - currentLeft) {
						int positionWithSelectedTabVisible = getTabBarWidth() - rightOfSelectedTab;
						scrollTo(positionWithSelectedTabVisible);
					} else if (leftOfSelectedTab + currentLeft < 0) {
						int positionWithSelectedTabVisible = -leftOfSelectedTab;
						scrollTo(positionWithSelectedTabVisible);
					}
				}
				checkIfScrollButtonsNecessary();
			}
		});
	}

	/**
	 *
	 */
	private void initScrollBar() {
		scrollBar.getElement().setId(SCROLLBAR_STYLE);

		// Add tab with scroll controls
		scrollLeftButton = new ScrollDisablablePushButton(
				resources.enabledScrollLeftArrow(), resources.disabledLeftScrollArrow());
		scrollLeftButton.getElement().addClassName("scrollLeftButton");
		scrollLeftButton.setNonTransparentBackground();
		scrollLeftButton.addClickHandler(createScrollClickHandler(SCROLL_INTERVAL));

		scrollRightButton = new ScrollDisablablePushButton(
				resources.enabledScrollRightArrow(), resources.disabledScrollRightArrow());
		scrollRightButton.getElement().addClassName("scrollRightButton");
		scrollRightButton.setNonTransparentBackground();
		scrollRightButton.addClickHandler(createScrollClickHandler(-1 * SCROLL_INTERVAL));

		tabBar.setStyleName(TABS_STYLE);
		tabBar.addStyleName("notSelectable");

		this.scrollBar.addWest(scrollLeftButton, 30);
		this.scrollBar.addEast(scrollRightButton, 28);
		this.scrollBar.add(tabBar);
		
		tabBarAnimation = new LeftSlideAnimation(tabBar.getElement());
		tabBarAnimation.addObserver(new AnimationObserver() {
			
			@Override
			public void onComplete(double currentPosition) {
				updateScrollButtonsEnabled(currentPosition);
			}
		});
	}

	private boolean isScrollingNecessary() {
		return  getRightOfWidget(getLastTab()) > getTabBarWidth();
	}

	/**
	 * getRightOfWidget().
	 * 
	 * @param widget
	 *            Input Widget.
	 * @return integer.
	 */
	private int getRightOfWidget(final Widget widget) {
		if(widget == null){
			return 0;
		}
		return widget.getElement().getOffsetLeft() + widget.getElement().getOffsetWidth();
	}

	/**
	 * getTabBarWidth().
	 * 
	 * @return Integer
	 */
	private int getTabBarWidth() {
		if (this.tabBar.getElement().getParentElement() != null) {
			return this.tabBar.getElement().getParentElement().getClientWidth();
		} else {
			Log.error("ScrollTabLayoutPanel.getTabBarWidth() getParentElement() is null");
			return 0;
		}
	}

	/**
	 * getLastTab().
	 * 
	 * @return Widget.
	 */
	private Widget getLastTab() {
		if (this.tabBar.getWidgetCount() == 0) {
			return null;
		}
		return this.tabBar.getWidget(this.tabBar.getWidgetCount() - 1);
	}

	/**
	 * parsePosition().
	 * 
	 * @param positionString
	 *            Input String.
	 * @return integer.
	 */
	private static int parsePosition(String positionString) {
		int position;
		try {
			for (int i = 0; i < positionString.length(); i++) {
				char c = positionString.charAt(i);
				if (c != '-' && !(c >= '0' && c <= '9')) {
					positionString = positionString.substring(0, i);
				}
			}

			position = Integer.parseInt(positionString);
		} catch (NumberFormatException ex) {
			position = 0;
		}
		return position;
	}

	/**
	 *
	 */
	private void resetScrollPosition() {
		tabBarAnimation.animateTo(0, 500);
	}

	/**
	 * scrollTo().
	 * 
	 * @param to
	 *            Input integer.
	 */
	private void scrollTo(int to) {
		if (to > 0) {
			to = 0;
		}
		tabBarAnimation.animateTo(to, 500);
		updateScrollButtonsEnabled(to);
	}

	/* END OF SCROLL-RELATED PRIVATE METHODS */


	/**
	 * Resources interface.
	 */
	public interface Resources extends ClientBundle {

		@Source("enabledScrollLeftArrow.png")
		@ImageOptions(flipRtl = true)
		ImageResource enabledScrollLeftArrow();

		@Source("disabledScrollLeftArrow.png")
		@ImageOptions(flipRtl = true)
		ImageResource disabledLeftScrollArrow();

		@Source("enabledScrollRightArrow.png")
		@ImageOptions(flipRtl = true)
		ImageResource enabledScrollRightArrow();

		@Source("disabledScrollRightArrow.png")
		@ImageOptions(flipRtl = true)
		ImageResource disabledScrollRightArrow();

		@Source("scrollTabLayoutPanel.css")
		@CssResource.NotStrict
		CssResource style();
	}

	/**
	 * Styles used by this widget.
	 */
	public interface Style extends CssResource {

		String icon();

		String tabIcon();

		String iconDisabled();

		String scrollTabLayoutPanel();
	}

	public void refreshHeight() {
		if(selectedIndex != -1){
			((AbstractTablePanel)deckPanel.getWidget(selectedIndex)).refreshHeight();
		}
	}
}