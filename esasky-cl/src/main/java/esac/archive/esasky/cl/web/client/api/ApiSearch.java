/*
ESASky
Copyright (C) 2025 Henrik Norman

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

package esac.archive.esasky.cl.web.client.api;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import esac.archive.esasky.cl.web.client.Controller;

public class ApiSearch extends ApiBase {

    public ApiSearch(Controller controller) {
        this.controller = controller;
    }

    public void getTargetLists(JavaScriptObject widget) {
        JSONArray targetLists = controller.getRootPresenter().getTargetPresenter().getTargetListPanel().getTargetLists();
        JSONObject result = new JSONObject();
        result.put("Available_ids", targetLists);
        sendBackSingleValueToWidget(result, widget);
    }

    public void openTargetList(String targetList) {
        controller.getRootPresenter().getTargetPresenter().showTargetList(targetList);
    }

    public void openTargetList() {
        controller.getRootPresenter().getTargetPresenter().showTargetList();
    }

    public void closeTargetList() {
        controller.getRootPresenter().getTargetPresenter().closeTargetList();
    }


    public void showSearchTool() {
        controller.getRootPresenter().getTargetPresenter().showSearchTool();
    }

    public void closeSearchTool() {
        controller.getRootPresenter().getTargetPresenter().closeSearchTool();
    }


    public void setConeSearchArea(String ra, String dec, String radius) {
        controller.getRootPresenter().getTargetPresenter().setConeSearchArea(ra, dec, radius);
    }

    public void setPolygonSearchArea(String stcs) {
        controller.getRootPresenter().getTargetPresenter().setPolygonSearchArea(stcs);
    }

    public void clearSearchArea() {
        controller.getRootPresenter().getTargetPresenter().clearSearchArea();
    }
}
