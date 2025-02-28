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
