package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.google.gwt.i18n.client.NumberFormat;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.Shape;

public class CatalogueTooltip extends Tooltip {

    public CatalogueTooltip(final Shape source, int left, int top) {
        super(left, top, source);
    }

    protected void fillContent(String cooFrame) {

        String formattedRa = NumberFormat.getFormat("##0.#####").format(
                Double.parseDouble(this.source.getRa()));
        String formattedDec = NumberFormat.getFormat("##0.#####").format(
                Double.parseDouble(this.source.getDec()));

        StringBuilder sb = new StringBuilder();
        sb.append(this.source.getSourceName());
        sb.append("<hr/>");
        sb.append("<b>RA:</b>&nbsp;" + formattedRa);
        sb.append(" <b style=\"margin-left: 10px;\">Dec:</b>&nbsp;" + formattedDec);

        String[] keys = null;
        if (this.source.getKeys() != null) {
            keys = this.source.getKeys().split(",");
            for (String cKey : keys) {
                if (this.source.getDataDetailsByKey(cKey) != null) {
                    sb.append(" <b >" + cKey + ":</b>&nbsp;"
                            + this.source.getDataDetailsByKey(cKey));
                }
            }
        }

        typeSpecificContent.setHTML(sb.toString());
    }
}
