package esac.archive.esasky.cl.web.client.model.entities;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.cl.web.client.model.SelectableImage;
import esac.archive.esasky.cl.web.client.model.SourceShape;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.SourceConstant;
import esac.archive.esasky.cl.web.client.view.resultspanel.AbstractTablePanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.PublicationsTablePanel;

public class PublicationsBySourceEntity extends CatalogEntity {

    private final Resources resources = GWT.create(Resources.class);

    private TapRowList sourceMetadata;

    public interface Resources extends ClientBundle {

        @Source("publications_light.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabDefaultPublicationsIcon();

        @Source("publications_dark.png")
        @ImageOptions(flipRtl = true)
        ImageResource tabSelectedPublicationsIcon();
    }

    public PublicationsBySourceEntity(PublicationsDescriptor pubDescriptor,
            CountStatus countStatus, JavaScriptObject pubOverlay, SkyViewPosition skyViewPosition,
            String esaSkyUniqObsId, Long lastUpdate, EntityContext context) {
        super(pubDescriptor, countStatus, pubOverlay, skyViewPosition, esaSkyUniqObsId,
                lastUpdate, context);
    }

    @Override
    public SelectableImage getTypeIcon() {
        return new SelectableImage(resources.tabDefaultPublicationsIcon(),
                resources.tabSelectedPublicationsIcon());
    }

    public void setSourceMetadata(TapRowList sourceMetadata) {
        this.sourceMetadata = sourceMetadata;
    }

    public TapRowList getSourceMetadata() {
        return this.sourceMetadata;
    }

    @Override
    public SourceShape buildShape(int rowId, TapRowList sourceList) {
        SourceShape mySource = new SourceShape();
        mySource.setShapeId(rowId);
        mySource.setDec((getTAPDataByTAPName(sourceList, rowId,
                descriptor.getPolygonDecTapColumn())).toString());
        mySource.setRa((getTAPDataByTAPName(sourceList, rowId,
                descriptor.getPolygonRaTapColumn())).toString());
        mySource.setSourceName(((String) getTAPDataByTAPName(sourceList, rowId,
                descriptor.getUniqueIdentifierField())).toString());

        Map<String, String> details = new HashMap<String, String>();

        details.put(SourceConstant.SOURCE_NAME, mySource.getSourceName());

        details.put(EsaSkyWebConstants.SOURCE_TYPE,
                EsaSkyWebConstants.SourceType.PUBLICATION.toString());
        details.put(SourceConstant.CATALOGE_NAME, getEsaSkyUniqId());
        details.put(SourceConstant.IDX, Integer.toString(rowId));

        details.put(SourceConstant.EXTRA_PARAMS, "BIBCOUNT");
        details.put("BIBCOUNT", sourceList.getDataValue(SourceConstant.BIBCOUNT, rowId));

        mySource.setJsObject(AladinLiteWrapper.getAladinLite().newApi_createSourceJSObj(
                mySource.getRa(), mySource.getDec(), details, rowId));
        return mySource;
    }
    
    @Override
    public AbstractTablePanel createTablePanel() {
    	return new PublicationsTablePanel(getTabLabel(), getEsaSkyUniqId(), this);
    }
    
    @Override
    public boolean isCustomizable() {
    	return false;
    }
    
    @Override
    public String getTabLabel() {
    	return getEsaSkyUniqId();
    }
    
    @Override
    public boolean isRefreshable() {
    	return false;
    }
    
    @Override
    public boolean isSampEnabled() {
    	return false;
    }
    
    @Override
    public Image getTypeLogo() {
    	return new Image("images/cds.png");
    }
}
