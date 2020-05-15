package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.google.gwt.i18n.client.NumberFormat;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.model.DecPosition;
import esac.archive.esasky.cl.web.client.model.RaPosition;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;

public class CatalogueTooltip extends Tooltip {

    public CatalogueTooltip(final AladinShape source) {
        super(source);
    }

    protected void fillContent(String cooFrame) {
        RaPosition raPosition = new RaPosition(Double.parseDouble(this.source.getRa()));
        DecPosition decPosition = new DecPosition(Double.parseDouble(this.source.getDec()));
        
        String formattedRa;
        String formattedDec;
        if(GUISessionStatus.isShowingCoordinatesInDegrees()) {
            formattedRa = raPosition.getDegreeString();
            formattedDec = decPosition.getDegreeString();
        } else {
            formattedRa = raPosition.getHmsString();
            formattedDec = decPosition.getSymbolDmsString();
        }
        
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
