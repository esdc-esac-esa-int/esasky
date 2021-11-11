package esac.archive.esasky.cl.web.client.api;

import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.presenter.SelectSkyPanelPresenter;
import esac.archive.esasky.cl.web.client.view.common.ESASkyPlayerPanel;
import esac.archive.esasky.cl.web.client.view.searchpanel.targetlist.TargetListPanel;

public class ApiPlayer extends ApiBase{

    public ApiPlayer(Controller controller) {
        this.controller = controller;
    }


    public ESASkyPlayerPanel getActivePlayer() {
        TargetListPanel targetListPanel = getTargetListPanel();
        SelectSkyPanelPresenter hipsPanel = getHipsPanel();
        if (targetListPanel.isShowing()) {
            return targetListPanel.getPlayerPanel();
        } else if (hipsPanel.isShowing()){
            return hipsPanel.getPlayerPanel();
        } else {
            return null;
        }
     }


    public void play() {
        ESASkyPlayerPanel player = getActivePlayer();
        if (player != null) {
            player.play();
        }
    }

    public void pause() {
        ESASkyPlayerPanel player = getActivePlayer();
        if (player != null) {
            player.pause();
        }
    }

    public void next() {
        ESASkyPlayerPanel player = getActivePlayer();
        if (player != null) {
            player.goToNextSurvey();
        }
    }

    public void previous() {
        ESASkyPlayerPanel player = getActivePlayer();
        if (player != null) {
            player.goToPreviousSurvey();
        }
    }

    private TargetListPanel getTargetListPanel() {
        return  controller.getRootPresenter().getTargetPresenter().getTargetListPanel();
    }

    private SelectSkyPanelPresenter getHipsPanel() {
        return controller.getRootPresenter().getCtrlTBPresenter().getSelectSkyPresenter();
    }

}
