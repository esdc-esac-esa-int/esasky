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
