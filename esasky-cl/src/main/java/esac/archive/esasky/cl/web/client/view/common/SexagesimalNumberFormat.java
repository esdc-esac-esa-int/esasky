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
        return isRa ? new RaPosition(text).getRaDeg() : new DecPosition(text).getDecDeg();
    }
}
