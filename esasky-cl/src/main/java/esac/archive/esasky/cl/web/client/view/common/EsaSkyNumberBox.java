package esac.archive.esasky.cl.web.client.view.common;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;

public class EsaSkyNumberBox extends EsaSkyTextBox{

	private final NumberFormat numberFormat;

	private double latestValidNumber;
	private double maxNumber = Double.MAX_VALUE;
	private double minNumber = Double.MIN_VALUE;
	
	public EsaSkyNumberBox(final NumberFormat numberFormat, final double keyboardArrowStep){
		this(numberFormat, keyboardArrowStep, Double.MIN_VALUE, Double.MAX_VALUE);
	}
	
	public EsaSkyNumberBox(final NumberFormat numberFormat, final double keyboardArrowStep, double min, double max){
		super();
		minNumber = min;
		maxNumber = max;
		this.numberFormat = numberFormat;
		final KeyDownAndHold upArrowKeyPressedTimer = new KeyDownAndHold(new Timer() {

			@Override
			public void run() {
				addNumber(keyboardArrowStep);
			}
		});

		final KeyDownAndHold downArrowKeyPressedTimer = new KeyDownAndHold(new Timer() {

			@Override
			public void run() {
				subtractNumber(keyboardArrowStep);
			}
		});
		addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				upArrowKeyPressedTimer.cancel();
				downArrowKeyPressedTimer.cancel();
			}
		});

		addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				upArrowKeyPressedTimer.cancel();
				downArrowKeyPressedTimer.cancel();
				setNumber(getNumber());
			}
		});

		addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_UP) {
					upArrowKeyPressedTimer.schedule(300);
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
					downArrowKeyPressedTimer.schedule(300);
				} else if(event.getNativeKeyCode() != KeyCodes.KEY_ONE) {
					event.stopPropagation();
				}
			}
		});
		
		addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if (!getText().matches("[-+]?[0-9]*\\.?[0-9]+")) {
					setNumber(latestValidNumber);
				} else {
					double currentNumber = Double.valueOf(getText());
					if(new Double(numberFormat.format(currentNumber)) > new Double(numberFormat.format(maxNumber))) {
						currentNumber = maxNumber;
						setNumber(currentNumber);
					}
					if(new Double(numberFormat.format(currentNumber)) < new Double(numberFormat.format(minNumber))) {
						currentNumber = minNumber;
						setNumber(currentNumber);
					}
					latestValidNumber = currentNumber;
				}
			}
		});
	}

	public double getNumber() {
		return latestValidNumber;
	}

	public void setNumber(double number) {
		if(number > maxNumber) {
			number = maxNumber;
		}
		if(number < minNumber) {
			number = minNumber;
		}
		super.setText(numberFormat.format(number));
	}

	public void addNumber(double numberToAdd) {
		setNumber(getNumber() + numberToAdd);
	}

	public void subtractNumber(double numberToSubtract) {
		setNumber(getNumber() - numberToSubtract);
	}
	
	public void setMaxNumber(double max) {
		maxNumber = max;
	}
	
	public void setMinNumber(double min) {
		minNumber = min;
	}

	@Override
	public void setText(String text) {
		Log.debug("Can't set text of a number box");
	}


	private class KeyDownAndHold extends Timer {

		Timer timerToStart;

		public KeyDownAndHold(Timer timerToStart) {
			this.timerToStart = timerToStart;
		}

		@Override
		public void run() {
			timerToStart.scheduleRepeating(100);
		}

		@Override
		public void cancel() {
			super.cancel();
			timerToStart.cancel();
		}

		@Override
		public void schedule(int delayMillis) {
			super.schedule(delayMillis);
			timerToStart.schedule(1);
		}

	}


}
