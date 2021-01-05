package esac.archive.esasky.cl.web.client.view.animation;

import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.utility.NumberFormatter;

public class NumberAnimation extends EsaSkyAnimation {

    private final Label label;
    private long currentNumber = 0;
 
    public NumberAnimation(Label label)
    {
        this.label = label;
    }
 
    @Override
	protected Double getCurrentPosition() {
        return new Double(currentNumber);
	}
    
    @Override
	protected void setCurrentPosition(double newPosition){
        currentNumber = Math.round(newPosition);
        label.setText(NumberFormatter.formatToNumberWithSpaces(Long.toString(currentNumber)));
	}
}
