package esac.archive.esasky.cl.web.client.model.entities;

import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.model.SelectableImage;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;

public interface GeneralEntityInterface extends IShapeDrawer {

    public SkyViewPosition getSkyViewPosition();
    public void setSkyViewPosition(SkyViewPosition skyViewPosition);
    
    public String getHistoLabel();
    public void setHistoLabel(String histoLabel);
    public String getEsaSkyUniqId();
    public void setEsaSkyUniqId(String esaSkyUniqId);
    public TapRowList getMetadata();
    public void setMetadata(TapRowList metadata);
    public Long getLastUpdate();
    public void setLastUpdate(Long lastUpdate);
    public String getTabLabel();
    public int getTabNumber();

    public void setTabNumber(int number);

    public String getMetadataAdql();

    public SelectableImage getTypeIcon();
    public Image getTypeLogo();

    public Object getTAPDataByTAPName(TapRowList tapRowList, int rowIndex, String tapName);
    
    public Double getDoubleByTAPName(TapRowList tapRowList, int rowIndex, String tapName, Double defaultValue);
    
    public CountStatus getCountStatus();

    public IDescriptor getDescriptor();
    public EntityContext getContext();

    public void clearAll();
    
    public String getColor();
    public void setColor(String color);
    
    public void fetchData(ITablePanel tablePanel);
    public void coneSearch(ITablePanel tablePanel, SkyViewPosition conePos);
    public void refreshData(ITablePanel tablePanel);
    
    public ITablePanel createTablePanel();
    
    public boolean isSampEnabled();
    public boolean isRefreshable();
    public boolean hasDownloadableDataProducts();
	public boolean isCustomizable();
}
