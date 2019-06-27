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
