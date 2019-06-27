package esac.archive.esasky.cl.web.client.view.resultspanel;

import esac.archive.esasky.cl.web.client.model.entities.SSOEntity;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.OnColorChangedCallback;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel.OnValueChangedCallback;

public class SSOObservationsTablePanel extends CommonObservationsTablePanel {

	private SSOEntity entity;
    public SSOObservationsTablePanel(final String inputLabel, final String inputEsaSkyUniqID,
            final SSOEntity inputObsEntity) {
        super(inputLabel, inputEsaSkyUniqID, inputObsEntity);
        this.entity = inputObsEntity;
		activateGroupHeaders();
    }
    
    @Override
    public void selectTablePanel() {
    	super.selectTablePanel();
        AladinLiteWrapper.getInstance().addPlolyline2SSOverlay(entity.getOrbitPolyline());
    }
    
    @Override
    public void closeTablePanel() {
    	super.closeTablePanel();
    	AladinLiteWrapper.getInstance().cleanSSOOverlay();
    }
    
    @Override
    public void showStylePanel(int x, int y) {
    	OnColorChangedCallback orbitColorCallback = new OnColorChangedCallback() {
			
			@Override
			public void onColorChanged(String color) {
				entity.setSsoOrbitColor(color);
			}
		};
		OnValueChangedCallback orbitScaleCallback = new OnValueChangedCallback() {
			
			@Override
			public void onValueChanged(double value) {
				entity.setSsoOrbitLineRatio(value);
			}
		};
		
		if(stylePanel == null) {

			stylePanel = new StylePanel(getEntity().getEsaSkyUniqId(), getEntity().getTabLabel(), 
					getDescriptor().getHistoColor(), getEntity().getSize(), null,
					null, null, null, null, entity.getSsoOrbitColor(), entity.getSsoOrbitLineRatio(),
					new OnColorChangedCallback() {

				@Override
				public void onColorChanged(String color) {
					getDescriptor().setHistoColor(color);
				}
			}, new OnValueChangedCallback() {

				@Override
				public void onValueChanged(double value) {
					getEntity().setSizeRatio(value);
				}
			}, null,
					null, null, null,
					orbitColorCallback, orbitScaleCallback);
		}		
		stylePanel.toggle();
		stylePanel.setPopupPosition(x, y);
    }
}
