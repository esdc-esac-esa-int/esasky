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

import com.allen_sauer.gwt.log.client.Log;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.PlanningConstant;

public class PlanningDetectorCenterTooltip extends Tooltip {

    private final String logPrefix = "[PlanningTooltip]";

    public PlanningDetectorCenterTooltip(final AladinShape source) {
        super(source, false);
    }

    @Override
    protected void fillContent(String cooFrame) {
        Log.debug(logPrefix + "Into fillContent");

        Double raSource = Double.parseDouble(this.source
                .getDataDetailsByKey(PlanningConstant.REFERENCE_RA));
        Double decSource = Double.parseDouble(this.source
                .getDataDetailsByKey(PlanningConstant.REFERENCE_DEC));
        double[] raDec = { raSource, decSource };
        Log.debug(logPrefix + " source RA: " + raDec[0] + " source DEc: " + raDec[1]);
        Log.debug(logPrefix + "SOURCE COOFRAME:"
                + this.source.getDataDetailsByKey(PlanningConstant.COO_FRAME));
        String sourceCooFrame = this.source.getDataDetailsByKey(PlanningConstant.COO_FRAME);
        if (cooFrame.equalsIgnoreCase(sourceCooFrame)) {
            Log.debug(logPrefix + "no conversion");
        } else {
            if (sourceCooFrame.equalsIgnoreCase(AladinLiteConstants.FRAME_GALACTIC)) {
                // convert to J2000
                Log.debug(logPrefix + "Convert to equatorial");
                raDec = CoordinatesConversion.convertPointGalacticToJ2000(raSource, decSource);
            } else {
                // convert to Gal
                Log.debug(logPrefix + "Convert to galactic");
                raDec = CoordinatesConversion.convertPointEquatorialToGalactic(raSource, decSource);
            }
        }
        
        String html = "" 
                + "<b>" + TextMgr.getInstance().getText("PlanningDetectorCenterTooltip_instrument") + "</b> "
                + this.source.getDataDetailsByKey(PlanningConstant.INSTRUMENT) 
                + "<br/><br/>"

                + "<b>" + TextMgr.getInstance().getText("PlanningDetectorCenterTooltip_detector") + "</b> "
                + this.source.getDataDetailsByKey(PlanningConstant.DETECTOR) 
                + "<br/><br/>"

                + "<b>" + TextMgr.getInstance().getText("PlanningDetectorCenterTooltip_referenceRA") + "</b> "
                + this.source.getDataDetailsByKey(PlanningConstant.REFERENCE_RA)
                + "(" + this.source.getDataDetailsByKey(PlanningConstant.COO_FRAME) + ")" 
                + "<br/><br/>"

                + "<b>" + TextMgr.getInstance().getText("PlanningDetectorCenterTooltip_referenceDEC") + "</b> "
                + this.source.getDataDetailsByKey(PlanningConstant.REFERENCE_DEC) 
                + "(" + this.source.getDataDetailsByKey(PlanningConstant.COO_FRAME) + ")" 
                + "<br/><br/>";

        typeSpecificContent.setHTML(html);
    }
}
