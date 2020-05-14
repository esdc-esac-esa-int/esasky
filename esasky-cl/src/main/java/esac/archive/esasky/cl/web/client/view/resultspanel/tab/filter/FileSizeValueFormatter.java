package esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter;

import com.google.gwt.i18n.client.NumberFormat;

public class FileSizeValueFormatter implements ValueFormatter{

    @Override
    public String formatValue(double value) {
        return new Double(value).toString();
    }

    @Override
    public double getValueFromFormat(String formattedValue) {
        return new Double(formattedValue);
    }
    
//    if(Math.abs(Double.parseDouble(fromTextBox.getText())) < (1 / Math.pow(10, precision))) {
//        fromTextBox.setText(NumberFormat.getFormat(numberFormat.getPattern() + "E0").format(fromValue));
//        if(fromTextBox.getText().equals("0E0")){
//            fromTextBox.setText("0");
//        }
//    }
//    if(Math.abs(Double.parseDouble(toTextBox.getText())) < (1 / Math.pow(10, precision))) {
//        toTextBox.setText(NumberFormat.getFormat(numberFormat.getPattern() + "E0").format(toValue));
//        if(toTextBox.getText().equals("0E0")){
//            toTextBox.setText("0");
//        }
//    }
}
