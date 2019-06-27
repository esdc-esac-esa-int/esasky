package esac.archive.esasky.cl.web.client.view.resultspanel.table;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.common.buttons.DisablablePushButton;

public class DataPanelPager extends AbstractPager {

	public static interface Resources extends ClientBundle {

		@Source("nextPage.png")
		@ImageOptions(flipRtl = true)
		ImageResource nextPage();

		@Source("nextPageDisabled.png")
		@ImageOptions(flipRtl = true)
		ImageResource nextPageDisabled();

		@Source("previousPage.png")
		@ImageOptions(flipRtl = true)
		ImageResource previousPage();

		@Source("previousPageDisabled.png")
		@ImageOptions(flipRtl = true)
		ImageResource previousPageDisabled();

		@Source("dataPanelPager.css")
		Style simplePagerStyle();
	}

	public static interface Style extends CssResource {
		String button();
		String disabledButton();
		String pageDetails();
	}

	private final Resources resources = GWT.create(Resources.class);
	private final Style style;
	
	private final DisablablePushButton nextPage;
	private final DisablablePushButton prevPage;

	private final HTML label = new HTML();


	public DataPanelPager() {
		this.style = resources.simplePagerStyle();
		this.style.ensureInjected();

		nextPage = new DisablablePushButton(resources.nextPage(),
				resources.nextPageDisabled());
		nextPage.setTitle(TextMgr.getInstance().getText("dataPanelPager_nextPagerTooltip"));
		nextPage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				nextPage();
				event.stopPropagation();
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_DataPanel_Pager, GoogleAnalytics.ACT_DataPanel_Pager_NextPage, "");
			}
		});
		prevPage = new DisablablePushButton(resources.previousPage(),
				resources.previousPageDisabled());
		prevPage.setTitle(TextMgr.getInstance().getText("dataPanelPager_previousPagerTooltip"));
		prevPage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				previousPage();
				event.stopPropagation();
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_DataPanel_Pager, GoogleAnalytics.ACT_DataPanel_Pager_PreviousPage, "");
			}
		});

		HorizontalPanel layout = new HorizontalPanel();
		layout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		initWidget(layout);
		layout.add(prevPage);
		layout.add(label);
		layout.add(nextPage);
		prevPage.getElement().getParentElement().addClassName(style.button());
		label.getElement().getParentElement().addClassName(style.pageDetails());
		nextPage.getElement().getParentElement().addClassName(style.button());

		// Disable the buttons by default.
		setDisplay(null);
	}

	protected String createText() {
		NumberFormat formatter = NumberFormat.getFormat("#,###");
		HasRows display = getDisplay();
		Range range = display.getVisibleRange();
		int pageStart = range.getStart() + 1;
		int pageSize = range.getLength();
		int dataSize = display.getRowCount();
		int endIndex = Math.min(dataSize, pageStart + pageSize - 1);
		endIndex = Math.max(pageStart, endIndex);
		return formatter.format(pageStart) + "-" + formatter.format(endIndex)
		+ " " + TextMgr.getInstance().getText("dataPanelPager_of") + " " + formatter.format(dataSize); 
	}

	@Override
	protected void onRangeOrRowCountChanged() {
		HasRows display = getDisplay();
		label.setText(createText());

		setPrevPageButtonsDisabled(!hasPreviousPage());

		if (isRangeLimited() || !display.isRowCountExact()) {
			setNextPageButtonsDisabled(!hasNextPage());
		}
	}

	private void setNextPageButtonsDisabled(boolean disabled) {
		nextPage.setDisabled(disabled);
	}
	
	private void setPrevPageButtonsDisabled(boolean disabled) {
		prevPage.setDisabled(disabled);
	}

	@Override
	public void firstPage() {
		super.firstPage();
	}

	@Override
	public int getPage() {
		return super.getPage();
	}

	@Override
	public int getPageCount() {
		return super.getPageCount();
	}

	@Override
	public boolean hasNextPage() {
		return super.hasNextPage();
	}

	@Override
	public boolean hasNextPages(int pages) {
		return super.hasNextPages(pages);
	}

	@Override
	public boolean hasPage(int index) {
		return super.hasPage(index);
	}

	@Override
	public boolean hasPreviousPage() {
		return super.hasPreviousPage();
	}

	@Override
	public boolean hasPreviousPages(int pages) {
		return super.hasPreviousPages(pages);
	}

	@Override
	public void lastPage() {
		super.lastPage();
	}

	@Override
	public void lastPageStart() {
		super.lastPageStart();
	}

	@Override
	public void nextPage() {
		super.nextPage();
	}

	@Override
	public void previousPage() {
		super.previousPage();
	}

	@Override
	public void setDisplay(HasRows display) {
		boolean disableButtons = (display == null || display.getRowCount() == 0);
		setNextPageButtonsDisabled(disableButtons);
		setPrevPageButtonsDisabled(disableButtons);
		super.setDisplay(display);
	}

	@Override
	public void setPage(int index) {
		super.setPage(index);
	}

	@Override
	public void setPageSize(int pageSize) {
		super.setPageSize(pageSize);
	}

	@Override
	public void setPageStart(int index) {
		super.setPageStart(index);
	}
}
