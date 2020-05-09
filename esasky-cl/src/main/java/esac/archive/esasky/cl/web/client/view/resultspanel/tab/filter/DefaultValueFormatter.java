package esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter;

public class DefaultValueFormatter implements ValueFormatter{

    @Override
    public String formatValue(double value) {
        return new Double(value).toString();
    }

    @Override
    public double getValueFromFormat(String formattedValue) {
        return new Double(formattedValue);
    }
}
