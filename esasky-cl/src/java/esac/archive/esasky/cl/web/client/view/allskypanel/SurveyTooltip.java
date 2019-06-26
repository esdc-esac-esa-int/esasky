package esac.archive.esasky.cl.web.client.view.allskypanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.NumberFormat;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.Shape;
import esac.archive.esasky.cl.web.client.utility.SurveyConstant;

public class SurveyTooltip extends Tooltip {


    public SurveyTooltip(final Shape source, int left, int top) {
    	super(left, top, source);
    }

    protected void fillContent(String cooFrame) {
        Log.debug("Into fillContent");

        String formattedRa = NumberFormat.getFormat("##0.#####").format(
                Double.parseDouble(this.source.getRa()));
        String formattedDec = NumberFormat.getFormat("##0.#####").format(
                Double.parseDouble(this.source.getDec()));

        String html = this.source.getDataDetailsByKey(SurveyConstant.SURVEY_NAME)
                + "<hr/>"
                + "<b>RA:</b>&nbsp;"
                + formattedRa
                + "<b style=\"margin-left: 10px;\">Dec:</b>&nbsp;"
                + formattedDec;
        typeSpecificContent.setHTML(html);
    }
}
