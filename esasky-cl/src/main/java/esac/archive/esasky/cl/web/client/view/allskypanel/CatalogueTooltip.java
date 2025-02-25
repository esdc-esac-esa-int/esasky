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

package esac.archive.esasky.cl.web.client.view.allskypanel;


import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.model.DecPosition;
import esac.archive.esasky.cl.web.client.model.RaPosition;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;

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
                    sb.append("<b>&nbsp;&nbsp;" + cKey + ":</b>&nbsp;"
                            + this.source.getDataDetailsByKey(cKey));
                }
            }
        }

        typeSpecificContent.setHTML(sb.toString());
    }
    
    @Override
	protected Coordinate getJ2000Coordinate() {
		return new Coordinate(Double.parseDouble(source.getRa()), Double.parseDouble(source.getDec()));
	}

}
