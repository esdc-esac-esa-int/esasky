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

	private NumberFormat numberFormat;

	private double latestValidNumber;
	
	public EsaSkyNumberBox(NumberFormat numberFormat, final double keyboardArrowStep){
		super();
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
					latestValidNumber = Double.valueOf(getText());
				}
			}
		});
	}

	public double getNumber() {
		return latestValidNumber;
	}

	public void setNumber(double number) {
		super.setText(numberFormat.format(number));
	}

	public void addNumber(double numberToAdd) {
		setNumber(getNumber() + numberToAdd);
	}

	public void subtractNumber(double numberToSubtract) {
		setNumber(getNumber() - numberToSubtract);
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
