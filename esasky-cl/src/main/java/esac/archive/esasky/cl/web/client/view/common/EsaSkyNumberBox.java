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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;

public class EsaSkyNumberBox extends EsaSkyTextBox{

	private final CssResource style;
	private Resources resources;

	private NumberFormat numberFormat;

	private double latestValidNumber;
	private double maxNumber = Double.MAX_VALUE;
	private double minNumber = Double.MIN_VALUE;
	
	private final String INVALID_INPUT_CSS = "esaskyNumberBox__invalidInput";
	
    public static interface Resources extends ClientBundle {

        @Source("esaSkyNumberBox.css")
        @CssResource.NotStrict
        CssResource style();
    }
	
	
	public EsaSkyNumberBox(NumberFormat numberFormat, final double keyboardArrowStep){
		this(numberFormat, keyboardArrowStep, Double.MIN_VALUE, Double.MAX_VALUE);
	}
	
	public EsaSkyNumberBox(NumberFormat numberFormat, final double keyboardArrowStep, double min, double max){
		super();
		
		this.resources = GWT.create(Resources.class);
		this.style = this.resources.style();
		this.style.ensureInjected();

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
				NumberFormat numberFormat = EsaSkyNumberBox.this.numberFormat;
				try {
					double currentNumber = numberFormat.parse(getText());
					String stringValue = String.valueOf(currentNumber);
					if(stringValue.matches("[-+]?[0-9]*\\.?[0-9]+")) {
						if(currentNumber > maxNumber) {
							currentNumber = maxNumber;
							setNumber(currentNumber);
						}
						if(currentNumber < minNumber) {
							currentNumber = minNumber;
							setNumber(currentNumber);
						}
						latestValidNumber = currentNumber;
						getElement().removeClassName(INVALID_INPUT_CSS);

					}else {
						getElement().addClassName(INVALID_INPUT_CSS);
					}
				} catch (Exception ex) {
					getElement().addClassName(INVALID_INPUT_CSS);
				}

			}
		});
	}

	public double getNumber() {
		return latestValidNumber;
	}

	public String getFormattedNumber() {
		return numberFormat.format(getNumber());
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

	public void setNumberFormat(NumberFormat numberFormat) {
		NumberFormat oldNumberFormat = this.numberFormat;
		this.numberFormat = numberFormat;
		this.setNumberSilently(oldNumberFormat.parse(getText()));
	}

	@Override
	public void setText(String text) {
		Log.debug("Can't set text of a number box");
	}


	private void setNumberSilently(double number) {
		if(number > maxNumber) {
			number = maxNumber;
		}
		if(number < minNumber) {
			number = minNumber;
		}
		super.setTextSilently(numberFormat.format(number));
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
