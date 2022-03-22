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


    public void coneSearch(String ra, String dec, String radius) {
        controller.getRootPresenter().getTargetPresenter().coneSearch(ra, dec, radius);
    }

    public void polygonSearch(String stcs) {
        controller.getRootPresenter().getTargetPresenter().polygonSearch(stcs);
    }

    public void clearSearchArea() {
        controller.getRootPresenter().getTargetPresenter().clearSearchArea();
    }
}
