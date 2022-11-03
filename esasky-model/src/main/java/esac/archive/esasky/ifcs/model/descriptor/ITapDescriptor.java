package esac.archive.esasky.ifcs.model.descriptor;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.shared.ObsCore;
import esac.archive.esasky.ifcs.model.shared.UCD;

import java.util.List;

public interface ITapDescriptor {
    List<TapMetadataDescriptor> getMetadata();

    GeneralJavaScriptObject getRawMetadata();

    Double getWavelengthCenter();

    String getWavelengthColor();

    void setSearchArea(SearchArea searchArea);

    SearchArea getSearchArea();

    boolean hasSearchArea();

    String getSearchAreaShape();

    String getId();

    int getShapeLimit();

    String getTableName();

    String getSchemaName();

    String getMission();

    String getRaColumn();

    String getDecColumn();

    String getRegionColumn();

    String getIdColumn();

    String getLongName();

    String getShortName();

    String createTapUrl(String baseUrl, String query, String responseFormat);

    List<TapMetadataDescriptor> getColumnMetadata();

    void registerVisibilityObserver(MetadataVisibilityObserver observer);

    void unregisterVisibilityObserver(MetadataVisibilityObserver observer);

    boolean isColumnVisible(String columnName);

    void setColumnVisibility(String columnName, boolean visible);

}
