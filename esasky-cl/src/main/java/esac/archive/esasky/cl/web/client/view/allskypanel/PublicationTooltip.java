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
            button.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
                    // TODO: Fix
//					CommonEventBus.getEventBus().fireEvent(new AladinLiteShapeSelectedEvent(new Integer(source.getId()), EntityRepository.getInstance().getPublications().getEsaSkyUniqId(), source));
				}
			});
            typeSpecificFlowPanel.add(button);
        }
        
    }
}
