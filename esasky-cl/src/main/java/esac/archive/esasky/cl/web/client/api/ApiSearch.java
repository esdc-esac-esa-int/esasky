package esac.archive.esasky.cl.web.client.api;

import esac.archive.esasky.cl.web.client.Controller;

public class ApiSearch extends ApiBase {
    private final Controller controller;

    public ApiSearch(Controller controller) {
        this.controller = controller;
    }

    public void openTargetList(String fileName) {
        controller.getRootPresenter().getTargetPresenter().getTargetListPanel().setSelectedFile(fileName);
        openTargetList();
    }

    public void openTargetList() {
        controller.getRootPresenter().getTargetPresenter().getTargetListPanel().show();
    }

    public void closeTargetList() {
        controller.getRootPresenter().getTargetPresenter().getTargetListPanel().hide();
    }

    public void playerStart() {
        controller.getRootPresenter().getTargetPresenter().getTargetListPanel().getPlayerPanel().play();
    }

    public void playerPause() {
        controller.getRootPresenter().getTargetPresenter().getTargetListPanel().getPlayerPanel().pause();
    }

    public void playerNext() {
        controller.getRootPresenter().getTargetPresenter().getTargetListPanel().getPlayerPanel().goToNextSurvey();
    }

    public void playerPrevious() {
        controller.getRootPresenter().getTargetPresenter().getTargetListPanel().getPlayerPanel().goToPreviousSurvey();
    }


}
