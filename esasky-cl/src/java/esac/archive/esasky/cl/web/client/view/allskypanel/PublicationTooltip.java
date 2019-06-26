package esac.archive.esasky.cl.web.client.view.allskypanel;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.Shape;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;

public class PublicationTooltip extends Tooltip {

    public PublicationTooltip(final Shape source, int left, int top) {
        super(left, top, source, false);
    }

    protected void fillContent(String cooFrame) {

        StringBuilder sb = new StringBuilder();
        sb.append(this.source.getSourceName());
        sb.append("<hr/>");

        String[] keys = null;
        if (this.source.getKeys() != null) {
            keys = this.source.getKeys().split(",");
            for (String cKey : keys) {
                if (this.source.getDataDetailsByKey(cKey) != null) {
                    final String textKey = "PublicationTooltip_" + cKey;
                    sb.append(" <b style=margin-left: 10px;\">" + TextMgr.getInstance().getText(textKey) + "</b>&nbsp;"
                            + this.source.getDataDetailsByKey(cKey));
                }
            }
        }
        typeSpecificContent.setHTML(sb.toString());
    }
}
