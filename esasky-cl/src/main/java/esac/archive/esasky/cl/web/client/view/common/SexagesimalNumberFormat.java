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

package esac.archive.esasky.cl.web.client.view.common;


import com.google.gwt.i18n.client.DefaultCurrencyData;
import com.google.gwt.i18n.client.NumberFormat;
import esac.archive.esasky.cl.web.client.model.DecPosition;
import esac.archive.esasky.cl.web.client.model.RaPosition;


public class SexagesimalNumberFormat extends NumberFormat {

    final boolean isRa;
    public SexagesimalNumberFormat(boolean isRa) {
        super("", new DefaultCurrencyData("", ""), false);

        this.isRa = isRa;
    }

    @Override
    public String format(double number) {
        return isRa ? new RaPosition(number).getHmsString() : new DecPosition(number).getSymbolDmsString();
    }

    @Override
    public double parse(String text) throws NumberFormatException {
        return isRa ? new RaPosition(text).getRaDeg() : new DecPosition(text, true).getDecDegFix();
    }
}
