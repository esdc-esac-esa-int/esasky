package esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter;

public interface ValueFormatter{
    String formatValue(double value);
    double getValueFromFormat(String formattedValue);
}