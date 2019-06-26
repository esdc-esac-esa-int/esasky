package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.NumberFormat;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.Shape;
import esac.archive.ammi.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.SourceConstant;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.uploadtargetlist.MultiTargetSourceConstants;

public class MultiTargetTooltip extends Tooltip {

    private final String logPrefix = "[MultiTargetTooltip]";

    public MultiTargetTooltip(final Shape source, int left, int top) {
        super(left, top, source);
        this.source = source;
    }

    protected void fillContent(String cooFrame) {
        Log.debug("Into fillContent, cooFrame: " + cooFrame);

        Double raSource = Double.parseDouble(this.source
                .getDataDetailsByKey(MultiTargetSourceConstants.RA_DEG));
        Double decSource = Double.parseDouble(this.source
                .getDataDetailsByKey(MultiTargetSourceConstants.DEC_DEG));
        Double[] raDec = { raSource, decSource };
        Log.debug(logPrefix + " source RA: " + raDec[0] + " source DEc: " + raDec[1]);
        Log.debug("SOURCE COOFRAME:" + this.source.getDataDetailsByKey(SourceConstant.COO_FRAME));        
        String tooltipCooFrameLabel = "";
        String sourceCooFrame = this.source.getDataDetailsByKey(SourceConstant.COO_FRAME);
        if (cooFrame.equalsIgnoreCase(sourceCooFrame)) {
            Log.debug("no conversion");
            tooltipCooFrameLabel = cooFrame;
        } else if (sourceCooFrame != null){
            if (sourceCooFrame.equalsIgnoreCase(AladinLiteConstants.FRAME_GALACTIC)) {
                // convert to J2000
                Log.debug("Convert to equatorial");
                raDec = CoordinatesConversion.convertPointGalacticToJ2000(raSource, decSource);
                tooltipCooFrameLabel = AladinLiteConstants.FRAME_J2000;
            } else {
                // convert to Gal
                Log.debug("Convert to galactic");
                raDec = CoordinatesConversion.convertPointEquatorialToGalactic(raSource, decSource);
                tooltipCooFrameLabel = AladinLiteConstants.FRAME_GALACTIC;
            }
        }

        String formattedRa = NumberFormat.getFormat("##0.#####").format(raDec[0]);
        String formattedDec = NumberFormat.getFormat("##0.#####").format(raDec[1]);

        String html = this.source.getDataDetailsByKey(MultiTargetSourceConstants.USER_INPUT) 
        		+ "<hr/>" 
            + "<b>" + TextMgr.getInstance().getText("MultiTargetTooltip_nearestSimbadId") + ": </b>"
            + this.source.getDataDetailsByKey(MultiTargetSourceConstants.SIMBAD_MAIN_ID)
            + "<br/><br/>" 
            + "<b>RA:</b>&nbsp;" + formattedRa + " [" + tooltipCooFrameLabel+ "]" 
            + "<b style=\"margin-left: 10px;\">Dec:</b>&nbsp;" + formattedDec + " [" + tooltipCooFrameLabel + "]";
        
        typeSpecificContent.setHTML(html);
    }

}
