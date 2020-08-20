package esac.archive.esasky.cl.gwidgets.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.NumberFormatter;
import esac.archive.esasky.cl.web.client.view.resultspanel.DDRequestForm;

public class SaveAllView {

	/** CSS Style Constants. */
	private static final String POPUP_ANCHOR_CSS_STYLE = "resultsToolbar-Anchor";
	private static final String RESULTS_TOOLBAR_POPUP_PANEL_CSS_STYLE = "resultsToolbar-PopupPanel";

	private Anchor saveAsVOTableAnchor;
	private Anchor saveAsCSVAnchor;
	private Anchor downloadProdAnchor;
	private PopupPanel saveOrDownloadPopupPanel;
	private DDRequestForm ddRequestForm;

	private Resources resources = GWT.create(Resources.class);
	private CssResource style;

	public static interface Resources extends ClientBundle {
		@Source("saveAllView.css")
		@CssResource.NotStrict
		CssResource style();
	}

	public SaveAllView() {
		this.style = this.resources.style();
		this.style.ensureInjected();
		initView();
	}

	private void initView() {
        this.saveAsCSVAnchor = new Anchor(TextMgr.getInstance().getText("saveAllView_save_In_CSV_Format"));
		this.saveAsCSVAnchor.setStyleName(POPUP_ANCHOR_CSS_STYLE);
		this.saveAsCSVAnchor.setTitle(TextMgr.getInstance().getText("saveAllView_save_In_CSV_Format_Title"));

        this.saveAsVOTableAnchor = new Anchor(TextMgr.getInstance().getText("saveAllView_save_In_VOTABLE_Format"));
		this.saveAsVOTableAnchor.setStyleName(POPUP_ANCHOR_CSS_STYLE);
		this.saveAsVOTableAnchor.setTitle(TextMgr.getInstance().getText("saveAllView_save_In_VOTABLE_Format_Title"));

		this.downloadProdAnchor = new Anchor(TextMgr.getInstance().getText("saveAllView_Download_Btn_Text"));
		this.downloadProdAnchor.setStyleName(POPUP_ANCHOR_CSS_STYLE);
		this.downloadProdAnchor.setTitle(TextMgr.getInstance().getText("saveAllView_Download_Btn_Title"));
		this.downloadProdAnchor.setEnabled(false);

		// Initialize DD request form.
		ddRequestForm = new DDRequestForm("_self");
		ddRequestForm.add(downloadProdAnchor);

		// add items to pop-up
		FlowPanel metadataPanel = new FlowPanel();
		metadataPanel.add(this.saveAsCSVAnchor);
		metadataPanel.add(this.saveAsVOTableAnchor);

		// Save section.
		FlowPanel saveButtonDataPanel = new FlowPanel();
		saveButtonDataPanel.add(this.ddRequestForm);

		// create pop-up panel
		this.saveOrDownloadPopupPanel = new PopupPanel();
		this.saveOrDownloadPopupPanel.setStyleName(RESULTS_TOOLBAR_POPUP_PANEL_CSS_STYLE);
		this.saveOrDownloadPopupPanel.setAutoHideEnabled(true);

		// add items to pop-up
		FlowPanel popupContainer = new FlowPanel();
		popupContainer.add(metadataPanel);
		popupContainer.add(saveButtonDataPanel);

		this.saveOrDownloadPopupPanel.add(popupContainer);
		this.saveOrDownloadPopupPanel.hide();
		
	}

	public final void updateNumberOfSelectedElementsLabel(final int newNumberOfSelectedElements) {
		enableDownloadProductsAnchor(newNumberOfSelectedElements > 0);
		final String count = (newNumberOfSelectedElements > 0) ? newNumberOfSelectedElements + "" : "0";
		downloadProdAnchor.setText(TextMgr.getInstance().getText("DownloadDataProducts").replace("$COUNT$", NumberFormatter.formatToNumberWithSpaces(count)));
	}
	
	public void setProductsDownloadVisible(boolean visible) {
		ddRequestForm.setVisible(visible);
	}

	public final Anchor getSaveAsVOTableAnchor() {
		return saveAsVOTableAnchor;
	}

	public final Anchor getSaveAsCSVAnchor() {
		return saveAsCSVAnchor;
	}

	public final DDRequestForm getDDRequestForm() {
		return ddRequestForm;
	}

	public final Anchor getDowloadProductsAnchor() {
		return this.downloadProdAnchor;
	}

	public final PopupPanel getSaveOrDownloadDialog() {
		return this.saveOrDownloadPopupPanel;
	}

	private final void enableDownloadProductsAnchor(final Boolean enable) {
		if(enable) {
			downloadProdAnchor.getElement().setId("");
		} else {
			downloadProdAnchor.getElement().setId("downloadProductsDisabled");
		}
	}
}
