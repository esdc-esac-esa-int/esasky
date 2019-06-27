package esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky;

import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;

import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.SkiesMenu;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.hips.HipsChangeEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.presenter.SelectSkyPanelPresenter;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.ESASkyPlayerPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.DisablablePushButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.PopupHeader;

public class SelectSkyPanel extends DialogBox implements SkyObserver, SelectSkyPanelPresenter.View {

	private PopupHeader header;
	private DragFlexTable skyTable;
	private final Resources resources;
	private CssResource style;

	private boolean isShowing;

	private ESASkyPlayerPanel player;
	private DisablablePushButton addSkyButton;

	private String hipsFromUrl = null;
	private SkiesMenu skiesMenu;

	private FlowPanel selectSkyPanel = new FlowPanel();

	private static List<SkyRow> skies = new LinkedList<SkyRow>();

	public static interface Resources extends ClientBundle {

		@Source("information.png")
		ImageResource info();

		@Source("plus-sign-light.png")
		ImageResource addSky();

		@Source("selectSkyPanel.css")
		@CssResource.NotStrict
		CssResource style();
	}

	public SelectSkyPanel(String defaultHiPS) {
		super(false, false);
		this.hipsFromUrl = defaultHiPS;

		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();

		initView();
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				setMaxSize();
			}
		});
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		setMaxSize();
	}

	private void initView() {
		this.removeStyleName("gwt-DialogBox");
		this.getElement().setId("allSkiesMenu");

		selectSkyPanel.getElement().setId("allSkiesMenuContainer");

		header = new PopupHeader(this, TextMgr.getInstance().getText("sky_loadingSkies"),
				TextMgr.getInstance().getText("sky_selectSky_help"));
		selectSkyPanel.add(header);
		
		skyTable = new DragFlexTable();
		selectSkyPanel.add(skyTable);
		selectSkyPanel.add(createAddSkyBtn());
		selectSkyPanel.add(createPlayer());

		this.add(selectSkyPanel);
	}

	private DisablablePushButton createAddSkyBtn() {
		addSkyButton = new DisablablePushButton(this.resources.addSky(), this.resources.addSky());
		addSkyButton.setRoundStyle();
		addSkyButton.addStyleName("addSkyBtn");
		addSkyButton.setMediumStyle();
		addSkyButton.disableButton();
		addSkyButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
			    final SkyRow newSky = createSky();
				GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_SkiesMenu, GoogleAnalytics.ACT_SkiesMenu_AddSkyClick, newSky.getFullId());
			}
		});
		return addSkyButton;
	}

	public void fillAllSkyPanelEntries(final SkiesMenu skiesMenu) {
		this.skiesMenu = skiesMenu;
		header.setText(TextMgr.getInstance().getText("sky_selectSky"));
	       
		createSky();

		addSkyButton.enableButton();
	}
	
	private SkyRow createSky(){
		SkyRow newSky = new SkyRow(skiesMenu, hipsFromUrl);
		newSky.registerObserver(this);
		skyTable.insertItem(newSky);
		player.addEntryToPlayer(newSky);
		skies.add(newSky);
		newSky.setSelected();
		ensureCorrectSkyStyle();
		return newSky;
	}

	private void ensureCorrectSkyStyle() {
		if(skies.size() <= 1){
			for(SkyRow sky: skies){
				sky.addOnlyOneSkyActiveStyle();
			}
		} else {
			for(SkyRow sky: skies){
				sky.removeOnlyOneSkyActiveStyle();
			}
		}
	}

	private ESASkyPlayerPanel createPlayer() {
		player = new ESASkyPlayerPanel();
		player.addStyleName("skyPlayer");
		return player;
	}

	private void removeSky(SkyRow skyToRemove){
		skyTable.removeSky(skyToRemove);
		player.removeEntry(skyToRemove);
		skies.remove(skyToRemove);
		ensureCorrectSkyStyle();
	}

	@Override
	public void onUpdateSkyEvent(SkyRow sky) {
		CommonEventBus.getEventBus().fireEvent(
				new HipsChangeEvent(sky.getSelectedHips(), sky.getSelectedPalette()));
	}

	@Override
	public void onCloseEvent(SkyRow sky) {
		removeSky(sky);
	}

	public static String getNameOfSelectedHips(){
		final SkyRow sky = getSelectedSky();
		return (sky != null) ? sky.getNameofSelected() : EsaSkyConstants.ALADIN_DEFAULT_SURVEY_NAME;
	}

	public static SkyRow getSelectedSky(){
		for(SkyRow sky : skies){
			if (sky.isSelected()){
				return sky;
			}
		}

		Log.debug("No sky yet selected");
		return null;
	}

	public static void setHiPSFromAPI (HiPS hips, boolean newHips) {
		final SkyRow sky = getSelectedSky();
		sky.setHiPSFromAPI(hips, true, newHips);
	}
	
	public static void setSelectedHipsName (String hipsName) {
		final SkyRow sky = getSelectedSky();
		if (sky != null) {
			if (!hipsName.equals(sky.getNameofSelected())) {
				// TODO: Review this code... what happens if selected sky hasn't the hipsName passed, probably we need to change the selected Sky...
				sky.setSelectHips(hipsName, true, false);
			}
		}
	}

	private void setMaxSize() {
		Style style = getElement().getStyle();
		style.setPropertyPx("maxWidth", MainLayoutPanel.getMainAreaWidth() + MainLayoutPanel.getMainAreaAbsoluteLeft() - getAbsoluteLeft() - 15);
		style.setPropertyPx("maxHeight", MainLayoutPanel.getMainAreaHeight() + MainLayoutPanel.getMainAreaAbsoluteTop() - getAbsoluteTop() - 15);
	}

	@Override
	public void setPopupPosition(int left, int top) {
		setMaxSize();
	}

	@Override
	public void show() {
		isShowing = true;
		this.removeStyleName("displayNone");
		setMaxSize();
	}

	@Override
	public void hide(boolean autohide) {
		this.addStyleName("displayNone");
		isShowing = false;
		CloseEvent.fire(this, null);
	}

	@Override
	public void toggle() {
		if(isShowing()) {
			hide();
		} else {
			show();
		}
	}

	@Override
	public boolean isShowing() {
		return isShowing;
	}

	@Override
	public HasClickHandlers getAddSkyRowButton() {
		return addSkyButton;
	}

}
