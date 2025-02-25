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

package esac.archive.esasky.cl.web.client.status;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;

import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;

public final class ScreenSizeService {

	private static ScreenSizeService _instance = null;
	
	private List<ScreenSizeObserver> observers = new LinkedList<ScreenSizeObserver>();
	
	private ScreenSize currentScreenSize;
	
	private ScreenSizeService() {
		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent arg0) {
				updateCurrentScreenSize();
			}
		});
	}
	
	public static ScreenSizeService getInstance() {
		if(_instance == null) {
			_instance = new ScreenSizeService();
		}
		return _instance;
	}
	
	private void updateCurrentScreenSize() {
		int mainAreaHeight = MainLayoutPanel.getMainAreaHeight();
		int mainAreaWidth = MainLayoutPanel.getMainAreaWidth();
		ScreenHeight height = ScreenHeight.MINI;
		if(mainAreaHeight >= ScreenHeight.MINI.getPxSize()) {
			height = ScreenHeight.SMALL;
		}
		if(mainAreaHeight >= ScreenHeight.SMALL.getPxSize()) {
			height = ScreenHeight.FULL_SIZE;
		}
		
		ScreenWidth width = ScreenWidth.MINI;
		if(mainAreaWidth >= ScreenWidth.MINI.getPxSize()) {
			width = ScreenWidth.SMALL;
		} 
		if(mainAreaWidth >= ScreenWidth.SMALL.getPxSize()) {
			width = ScreenWidth.MEDIUM;
		} 
		if(mainAreaWidth >= ScreenWidth.MEDIUM.getPxSize()) {
			width = ScreenWidth.LARGE;
		}
		if(mainAreaWidth >= ScreenWidth.LARGE.getPxSize()) {
			width = ScreenWidth.FULL_SIZE;
		}
		
		setScreenSize(width, height);

	}
	
	private void setScreenSize(ScreenWidth screenSizeWidth, ScreenHeight screenSizeHeight) {
		if(currentScreenSize == null 
				|| currentScreenSize.getWidth() != screenSizeWidth 
				|| currentScreenSize.getHeight() != screenSizeHeight) {
			currentScreenSize = new ScreenSize(screenSizeWidth, screenSizeHeight);
			notifyObservers();
		}
	}
	
	public ScreenSize getScreenSize() {
		if(currentScreenSize == null) {
			updateCurrentScreenSize();
		}
		return currentScreenSize;
	}
	
	public void registerObserver(ScreenSizeObserver observer) {
		observers.add(observer);
	}
	
	public void removeObserver(ScreenSizeObserver observer) {
		observers.remove(observer);
	}

	
	private void notifyObservers() {
		for(ScreenSizeObserver observer : observers) {
			observer.onScreenSizeChange();
		}
	}
	

}
