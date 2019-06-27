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
		numberBox = new EsaSkyNumberBox(numberFormat, step);
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
}
