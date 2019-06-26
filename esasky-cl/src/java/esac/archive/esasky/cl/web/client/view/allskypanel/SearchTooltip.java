package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.NumberFormat;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.Shape;
import esac.archive.ammi.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.cl.web.client.utility.SourceConstant;

public class SearchTooltip extends Tooltip {


    public SearchTooltip(final Shape source, int left, int top) {
    	super(left, top, source);
    }

    protected void fillContent(String cooFrame) {
        Log.debug("Into fillContent");
        Double[] raDec = { Double.parseDouble(this.source.getRa()),
                Double.parseDouble(this.source.getDec()) };
        String sourceCooFrame;
        if (cooFrame.toLowerCase().equalsIgnoreCase(
                this.source.getDataDetailsByKey(SourceConstant.COO_FRAME))) {
            Log.debug("no conversion");
            sourceCooFrame = this.source.getDataDetailsByKey(SourceConstant.COO_FRAME);
        } else if (AladinLiteConstants.FRAME_GALACTIC.toLowerCase().equalsIgnoreCase(
                this.source.getDataDetailsByKey(SourceConstant.COO_FRAME))) {
            // Convert to equatorial
            Log.debug("Convert to equatorial");
            raDec = CoordinatesConversion.convertPointGalacticToJ2000(
                    Double.parseDouble(this.source.getRa()),
                    Double.parseDouble(this.source.getDec()));
            sourceCooFrame = AladinLiteConstants.FRAME_J2000;
        } else {
            // Convert to galactic
            Log.debug("Convert to galactic");
            raDec = CoordinatesConversion.convertPointEquatorialToGalactic(
                    Double.parseDouble(this.source.getRa()),
                    Double.parseDouble(this.source.getDec()));
            sourceCooFrame = AladinLiteConstants.FRAME_GALACTIC;
        }

        String formattedRa = NumberFormat.getFormat("##0.#####").format(raDec[0]);
        String formattedDec = NumberFormat.getFormat("##0.#####").format(raDec[1]);
        String html = this.source.getSourceName() + "<b>RA:</b>&nbsp;"
                + formattedRa
                + " ["
                + sourceCooFrame
                + "]<b style=\"margin-left: 10px;\">Dec:</b>&nbsp;"
                + formattedDec
                + " ["
                + sourceCooFrame
                + "]";
        typeSpecificContent.setHTML(html);
    }
}
