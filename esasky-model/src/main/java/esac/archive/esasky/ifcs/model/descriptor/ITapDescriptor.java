/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.ifcs.model.descriptor;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.SearchArea;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

import java.util.List;

public interface ITapDescriptor {
    List<TapMetadataDescriptor> getMetadata();

    GeneralJavaScriptObject getRawMetadata();

    String getColor();

    void setColor(String color);

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

    String getLongNameColumn();

    String getShortNameColumn();

    String createTapUrl(String baseUrl, String query, String responseFormat);

    List<TapMetadataDescriptor> getColumnMetadata();

    void registerVisibilityObserver(MetadataVisibilityObserver observer);

    void unregisterVisibilityObserver(MetadataVisibilityObserver observer);

    boolean isColumnVisible(String columnName);

    void setColumnVisibility(String columnName, boolean visible);

}
