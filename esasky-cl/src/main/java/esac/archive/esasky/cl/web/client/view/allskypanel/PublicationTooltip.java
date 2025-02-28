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

package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteShapeSelectedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;

public class PublicationTooltip extends Tooltip {

    public PublicationTooltip(final AladinShape source) {
        super(source, false);
    }

    protected void fillContent(String cooFrame) {

        StringBuilder sb = new StringBuilder();
        sb.append(this.source.getSourceName());
        typeSpecificContent.setHTML(sb.toString());
 
        StringBuilder buttonStringBuilder = new StringBuilder();
        String[] keys = null;
        if (this.source.getKeys() != null) {
            keys = this.source.getKeys().split(",");
            for (String cKey : keys) {
                if (this.source.getDataDetailsByKey(cKey) != null) {
                    final String textKey = "PublicationTooltip_" + cKey;
                    buttonStringBuilder.append(TextMgr.getInstance().getText(textKey)
                            + this.source.getDataDetailsByKey(cKey));
                }
            }
            EsaSkyStringButton button = new EsaSkyStringButton(buttonStringBuilder.toString());
            button.getElement().setId("publicationTooltip__button");
            button.addClickHandler(event -> CommonEventBus.getEventBus().fireEvent(new AladinLiteShapeSelectedEvent(Integer.parseInt(source.getId()), EntityRepository.getInstance().getPublications().getId(), source)));
            typeSpecificFlowPanel.add(button);
        }
        
    }
}
