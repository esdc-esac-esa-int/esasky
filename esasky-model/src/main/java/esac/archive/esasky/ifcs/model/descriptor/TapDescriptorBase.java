package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gwt.http.client.URL;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.CoordinatesObject;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.shared.ESASkyColors;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Base descriptor with default implementations of common descriptor functionality.
 */
public abstract class TapDescriptorBase {
    @JsonIgnore
    private String id;

    @JsonIgnore
    private SearchArea searchArea;

    @JsonIgnore
    private final List<MetadataVisibilityObserver> visibilityObservers = new LinkedList<>();

    @JsonIgnore
    protected String color;


    public abstract List<TapMetadataDescriptor> getMetadata();

    public void setSearchArea(SearchArea searchArea) {
        this.searchArea = searchArea;
    }

    public SearchArea getSearchArea() {
        return this.searchArea;
    }

    public boolean hasSearchArea() {
        return getSearchArea() != null;
    }

    public String getSearchAreaShape() {
        String shape = "";
        if (hasSearchArea()) {
            if (searchArea.isCircle()) {
                CoordinatesObject coordinate = searchArea.getJ2000Coordinates()[0];
                shape =  "CIRCLE('ICRS'," + coordinate.getRaDeg()+ "," + coordinate.getDecDeg() + "," + searchArea.getRadius() + ")";
            } else {
                CoordinatesObject[] coordinates = searchArea.getJ2000Coordinates();
                String coordinateStr = Arrays.stream(coordinates)
                        .map(point -> point.getRaDeg() + "," + point.getDecDeg())
                        .collect(Collectors.joining(","));

                shape = "POLYGON('ICRS'," + coordinateStr + ")";
            }
        }
        return shape;
    }

    private TapMetadataDescriptor getColumn(String columnName) {
        return getMetadata().stream()
                .filter(cm -> cm.getName().equals(columnName)).findFirst().orElse(null);
    }

    public String getId() {
        if (id == null) {
            id = "TAP_DESCRIPTOR_" + UUID.randomUUID();
        }
        return id;
    }

    public int getShapeLimit() {
        return 1500;
    }


    public String createTapUrl(String baseUrl, String query, String responseFormat) {
        long currentTime = System.currentTimeMillis();
        String encodedQuery = URL.encodeQueryString(query);

        return baseUrl + "/tap/sync?request=doQuery&lang=ADQL&format="
                + responseFormat + "&query=" + encodedQuery + "&timecall=" + currentTime;
    }


    public void registerVisibilityObserver(MetadataVisibilityObserver observer) {
        visibilityObservers.add(observer);
    }

    public void unregisterVisibilityObserver(MetadataVisibilityObserver observer) {
        visibilityObservers.remove(observer);
    }

    private void notifyVisibilityObservers(String column, boolean visible) {
        visibilityObservers.forEach(vo -> vo.onVisibilityChange(column, visible));
    }


    public boolean isColumnVisible(String columnName) {
        TapMetadataDescriptor column = getColumn(columnName);
        return column == null || column.isPrincipal();
    }

    public void setColumnVisibility(String columnName, boolean visible) {
        TapMetadataDescriptor column = getColumn(columnName);
        if (column != null) {
            column.setPrincipal(visible);
            notifyVisibilityObservers(columnName, visible);
        }

    }

    public String getColor() {
        return this.color != null ? color : ESASkyColors.getNext();
    }

    public void setColor(String color) {
        this.color = color;
    }

}
