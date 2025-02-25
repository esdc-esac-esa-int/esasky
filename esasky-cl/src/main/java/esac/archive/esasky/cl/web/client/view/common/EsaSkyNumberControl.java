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

package esac.archive.esasky.cl.web.client.view.common;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ImageResource;

import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyRepeatButton;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyRepeatButton.RepeatAction;


public class EsaSkyNumberControl{

	private EsaSkyNumberBox numberBox;
	private EsaSkyRepeatButton increaseNumberButton;
	private EsaSkyRepeatButton decreaseNumberButton;


	public EsaSkyNumberControl(String title, ImageResource increaseNumberImage, ImageResource decreaseNumberImage, final double step, final NumberFormat numberFormat){
		this(title, increaseNumberImage, decreaseNumberImage, step, numberFormat, -Double.MAX_VALUE, Double.MAX_VALUE);
	}
	
	public EsaSkyNumberControl(String title, ImageResource increaseNumberImage, ImageResource decreaseNumberImage, final double step, final NumberFormat numberFormat, double min, double max){
		numberBox = new EsaSkyNumberBox(numberFormat, step, min, max);
		numberBox.setTitle(title);

		increaseNumberButton = new EsaSkyRepeatButton(increaseNumberImage, new RepeatAction() {

			@Override
			public void onRepeat() {
				numberBox.addNumber(step);
			}
		}, 100);

		increaseNumberButton.setTitle(TextMgr.getInstance().getText("EsaSkyNumberControl_increaseWithLabel").replace("$TITLE$", title).replace("$STEP$", Double.toString(step)));
		increaseNumberButton.setMediumStyle();

		decreaseNumberButton = new EsaSkyRepeatButton(decreaseNumberImage, new RepeatAction() {

			@Override
			public void onRepeat() {
				numberBox.subtractNumber(step);
			}
		}, 100);

		decreaseNumberButton.setTitle(TextMgr.getInstance().getText("EsaSkyNumberControl_decreaseWithLabel").replace("$TITLE$", title).replace("$STEP$", Double.toString(step)));
		decreaseNumberButton.setMediumStyle();
	}

	public EsaSkyNumberBox getNumberBox() {
		return numberBox;
	}

	public EsaSkyRepeatButton getIncreaseNumberButton() {
		return increaseNumberButton;
	}

	public EsaSkyRepeatButton getDecreaseNumberButton() {
		return decreaseNumberButton;
	}

	public double getValue() {
		return numberBox.getNumber();
	}

	public String getFormattedValue() {
		return numberBox.getFormattedNumber();
	}

	public void setNumberFormat(NumberFormat numberFormat) {
		numberBox.setNumberFormat(numberFormat);
	}
}
