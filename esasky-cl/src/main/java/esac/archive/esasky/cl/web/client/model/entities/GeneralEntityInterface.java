package esac.archive.esasky.cl.web.client.model.entities;

import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.stylemenu.StylePanel;

public interface GeneralEntityInterface extends IShapeDrawer {

    public SkyViewPosition getSkyViewPosition();
    public void setSkyViewPosition(SkyViewPosition skyViewPosition);
    
    public String getHistoLabel();
    public void setHistoLabel(String histoLabel);
    public String getEsaSkyUniqId();
    public void setEsaSkyUniqId(String esaSkyUniqId);
    public TapRowList getMetadata();
    public void setMetadata(TapRowList metadata);
    public String getTabLabel();

    public Image getTypeLogo();

    public Object getTAPDataByTAPName(TapRowList tapRowList, int rowIndex, String tapName);
    
    public Double getDoubleByTAPName(TapRowList tapRowList, int rowIndex, String tapName, Double defaultValue);
    
    public CountStatus getCountStatus();

    public IDescriptor getDescriptor();

    public void clearAll();
    
    public String getColor();
    public void setPrimaryColor(String color);
    
    public void fetchData();
    public void fetchData(String adql);
    public void fetchDataWithoutMOC();
    public void coneSearch(SkyViewPosition conePos);
    
    public ITablePanel createTablePanel();
    public ITablePanel getTablePanel();
    public void setTablePanel(ITablePanel panel);
    
    public StylePanel createStylePanel();
	public void setStylePanelVisibility();
    public void select();
    
    public boolean isSampEnabled();
    public boolean isRefreshable();
    public void setRefreshable(boolean isRefreshable);
	public boolean isCustomizable();
	
	public void onShapeSelection(AladinShape shape);
	public void onShapeDeselection(AladinShape shape);
	public void onShapeHover(AladinShape shape);
	public void onShapeUnhover(AladinShape shape);
	
	public String getHelpText();
	
	public void registerColorChangeObserver(ColorChangeObserver colorChangeObserver);

	
}
