package esac.archive.esasky.cl.web.client.view.common.buttons;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;


public class EsaSkyRepeatButton extends EsaSkyButton{
	
	public interface RepeatAction{
		public void onRepeat();
	}
	
	private final Timer timer;
	
	
	public EsaSkyRepeatButton(ImageResource image, final RepeatAction repeatAction, final int repetitionMilliSeconds){
		super(image);
		
		timer = new Timer() {
			
			@Override
			public void run() {
				repeatAction.onRepeat();
			}
		};
		
		setOnClick(new ClickAction() {
			
			@Override
			public void action() {
				timer.cancel();
			}
		});
		
		setOnClickCancel(new ClickAction() {
			
			@Override
			public void action() {
				timer.cancel();
				
			}
		});
		
		setOnClickStart(new ClickAction() {
			
			@Override
			public void action() {
				timer.run();
				timer.scheduleRepeating(repetitionMilliSeconds);
			}
		});
	}

}
