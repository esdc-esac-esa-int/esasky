package esac.archive.esasky.cl.web.client.view.searchpanel.targetlist;

import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;
import esac.archive.esasky.ifcs.model.shared.ESASkySearchResult;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.common.Selectable;

/**
 * @author ESDC team (c) 2016 - European Space Agency
 */
public class TargetWidget extends Composite implements Selectable {
	
	private ESASkySearchResult target;
	private FocusPanel compositePanel;
	private Label label;
	private boolean isValid;
	private String targetDescription;
	private String outreachImage;
	private int width;
	private LinkedList<TargetObserver> observers = new LinkedList<TargetObserver>();

	private Resources resources;
	private CssResource style;

	public interface Resources extends ClientBundle {

		@Source("targetWidget.css")
		@CssResource.NotStrict
		CssResource style();
	}

	public TargetWidget(final ESASkySearchResult inputTarget, int width) {
		resources = GWT.create(Resources.class);
		style = resources.style();
		style.ensureInjected();
		this.target = inputTarget;
		this.isValid = inputTarget.getValidInput();
		this.targetDescription = inputTarget.getDescription();
		this.outreachImage = inputTarget.getOutreachImage();
		this.width = width;
		initView();
	}

	/**
	 * Initialize panel view.
	 */
	private void initView() {
		this.label = new Label();
		label.getElement().getStyle().setProperty("maxWidth", width - 26.0, Unit.PX);
		
		String targetName;
		if (this.target.getUserInputType() == SearchInputType.BIBCODE
	        ||this.target.getUserInputType() == SearchInputType.AUTHOR) {
		    targetName = this.target.getSimbadMainId();
		} else {
		    targetName = this.target.getUserInput();
		}
		
		if (targetName != null && targetName.trim().length() > 0) {
			this.label.setText(targetName);
		} else {
			this.label.setText("empty");
		}

		this.compositePanel = new FocusPanel();
		this.label.setTitle(TextMgr.getInstance().getText("targetWidget_showTargetInTheSky"));
		this.compositePanel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				if(isValid){
					setSelected();
				}
			}
		});

		this.compositePanel.add(this.label);

		if (this.isValid) {
			setValidStyle();
		} else {
			setInvalidStyle();
		}

		initWidget(compositePanel);
	}

	/**
	 * getTargetObject().
	 * 
	 * @return MultiTargetObject
	 */
	public final ESASkySearchResult getTargetObject() {
		return this.target;
	}

	/**
	 * getTargetDescription().
	 * 
	 * @return String
	 */
	public final String getTargetDescription() {
		return targetDescription;
	}

	public final String getOutreachImage() {
		return outreachImage;
	}

	private void setValidStyle() {
		compositePanel.addStyleName("targetWidget");
	}

	private void setInvalidStyle() {
		label.addStyleName("invalidTarget");
		label.setTitle(TextMgr.getInstance().getText("targetWidget_targetNotFoundInSimbad"));
	}

	public void setSelectedStyle() {
		label.addStyleName("selectedTarget");
		compositePanel.addStyleName("selectedPanel");
	}

	public void removeSelectedStyle() {
		label.removeStyleName("selectedTarget");
		compositePanel.removeStyleName("selectedPanel");
	}

	public final boolean isSelected() {
		return label.getStyleName().contains("selectedTarget");
	}

	@Override
	public void setSelected() {
		notifyObservers();
	}
	
	@Override
	public boolean isValid(){
		return isValid;
	}

	@Override
	public String getNameofSelected() {
		return getTargetObject().getUserInput();
	}

	public void registerObserver(TargetObserver observer) {
		observers.add(observer);
	}

	private void notifyObservers() {
		for (TargetObserver observer : observers) {
			observer.onTargetSelectionEvent(this);
		}
	}
}
