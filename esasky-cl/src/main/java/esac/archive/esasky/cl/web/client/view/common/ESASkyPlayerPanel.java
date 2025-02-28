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

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;

public class ESASkyPlayerPanel extends Composite {

    private Resources resources;
    private final CssResource style;

    private EsaSkyButton play;
    private EsaSkyButton pause;
    private EsaSkyButton previous;
    private EsaSkyButton next;
    private FlowPanel player = new FlowPanel();
    protected int blinkCounter;
    private int waitTime = 5000; //milliseconds; 5000 per default;
    private double value = 0.0;
    private double increaser = 1.0;
    protected List<Selectable> entries;
    private String playerId = UUID.randomUUID().toString();
    private String playerName;
    private String googleAnalyticsCat;
    private SurveyBlinkTimer timer = new SurveyBlinkTimer();
    private long timeSincePlayerStarted = 0;
    /**
     * Timer inner class to execute the blinking.
     *
     */
    private class SurveyBlinkTimer extends Timer {

        public SurveyBlinkTimer() {
        }

        @Override
        public void run() {
        	int oldCounter = blinkCounter;
        	increaseValue();
        	if(oldCounter != blinkCounter) {
        		select();
        	}
        }
    }

    /**
     * A ClientBundle that provides images for this widget.
     */
    public static interface Resources extends ClientBundle {

        @Source("esaSkyPlayerPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public ESASkyPlayerPanel(String playerName) {
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        this.playerName = playerName;

        initView();
    }
    
    public ESASkyPlayerPanel(int waitTime, double increaseValue, String playerName) {
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();
        
        this.waitTime = waitTime;
        this.increaser = increaseValue;
        this.playerName = playerName;

        initView();
    }

    private void initView() {
        initializeButtons();
        player.add(previous);
        player.add(play);
        player.add(pause);
        player.add(next);

        this.entries = new LinkedList<Selectable>();
        hidePlayer();
        pause();
        initWidget(player);
        setGoogleAanalyticsProperties();

    }
    
    private void setGoogleAanalyticsProperties() {
    	if ("TargetListPlayer".equals(playerName)) {
    		googleAnalyticsCat = GoogleAnalytics.CAT_TARGETLIST;
    	}else {
    		googleAnalyticsCat = GoogleAnalytics.CAT_SKIESMENU;
    	}
    }

	private void initializeButtons() {
        initializePrevious();
        initializePlay();
        initializePause();
        initializeNext();
	}

	private void initializeNext() {
		this.next = new EsaSkyButton(Icons.getNextIcon());
        this.next.setTitle(TextMgr.getInstance().getText("playerPanel_next"));
        this.next.setSmallStyle();
        this.next.addStyleName("playerButton");
        this.next.addStyleName("nextAndPreviousButton");
        this.next.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                goToNextSurvey();
            }
        });
	}

	private void initializePause() {
		this.pause = new EsaSkyButton(Icons.getPauseIcon());
		this.pause.setRoundStyle();
        this.pause.setTitle(TextMgr.getInstance().getText("playerPanel_pause"));
        this.pause.setMediumStyle();
        this.pause.addStyleName("playerButton");
        this.pause.addStyleName("roundButton");
        this.pause.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                pause();
            }
        });
	}

	private void initializePlay() {
		this.play = new EsaSkyButton(Icons.getPlayIcon());
		this.play.setRoundStyle();
        this.play.setTitle(TextMgr.getInstance().getText("playerPanel_play"));
        this.play.setMediumStyle();
        this.play.addStyleName("playerPlayButton");
        this.play.addStyleName("playerButton");
        this.play.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                play();
            }
        });
	}

	private void initializePrevious() {
		this.previous = new EsaSkyButton(Icons.getPreviousIcon());
        this.previous.setTitle(TextMgr.getInstance().getText("playerPanel_previous"));
        this.previous.setSmallStyle();
        this.previous.addStyleName("playerButton");
        this.previous.addStyleName("nextAndPreviousButton");
        this.previous.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                goToPreviousSurvey();
            }
        });
	}

    public void resetPlayerEntries() {
        this.entries = new LinkedList<Selectable>();
        pause();
        hidePlayer();
    }

    public void removeEntry(Selectable entry) {
        this.entries.remove(entry);
        if (this.entries.size() <= 1) {
            pause();
            hidePlayer();
        } 
    }

    public void addEntryToPlayer(Selectable entry) {
        this.entries.add(entry);
        if (this.entries.size() > 1) {
            this.showPlayer();
        }
    }
    
    public final void goToNextSurvey() {
        increaseCounter();
        select();
        GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PLAYER_NEXT);
    }

    public final void goToPreviousSurvey() {
        decreaseCounter();
        select();
        GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PLAYER_PREVIOUS);
    }
    
    private void increaseValue() {
		this.value += this.increaser;
		if (this.value > this.entries.size() - 1) {
            this.value = 0.0;
        }
		notifyObservers();
		
		this.blinkCounter = (int) Math.floor(this.value);
		
		if(!entries.get(blinkCounter).isValid()){
			increaseCounter();
		}
    }

	private void decreaseCounter() {
		this.blinkCounter = getIndexOfSelected() - 1;

        if (this.blinkCounter < 0) {
            this.blinkCounter = this.entries.size() - 1;
        }
        
        this.value = this.blinkCounter;
		notifyObservers();
        
		if(!entries.get(blinkCounter).isValid()){
			decreaseCounter();
		}
	}
    
	private void increaseCounter() {
		this.blinkCounter = getIndexOfSelected() + 1;
		
		if (this.blinkCounter >= this.entries.size()) {
		    this.blinkCounter = 0;
		}
		
		this.value = this.blinkCounter;
		notifyObservers();

		if(!entries.get(blinkCounter).isValid()){
			increaseCounter();
		}
	}
	
	private int getIndexOfSelected(){
		for(Selectable selectable : entries){
			if(selectable.isSelected()){
				return entries.indexOf(selectable);
			}
		}
		return -1;
	}

    private void startTimer(){
	    timer.scheduleRepeating(this.waitTime);
    }
    
    private void stopTimer(){
	    timer.cancel();
    }
    
	private void select() {
		Selectable currentlySelected = entries.get(blinkCounter);
		currentlySelected.setSelected();
		if(isPlaying()){
			updateProgressIndicatorMessage(currentlySelected.getNameofSelected());
		}
	}

    public void pause() {
        stopTimer();
        hidePauseButton();
        showPlayButton();
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(playerId));
        double timePlayed = (System.currentTimeMillis()- timeSincePlayerStarted ) / 1000.0 ;
        if(timeSincePlayerStarted > 0.1) {
            GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PLAYER_PAUSE, Double.toString(timePlayed));
        }
    }

    public void play() {
    	showPauseButton();
    	hidePlayButton();
        startTimer();
        updateProgressIndicatorMessage(TextMgr.getInstance().getText("playerPanel_surveyTourStarted"));
        timeSincePlayerStarted = System.currentTimeMillis();
        GoogleAnalytics.sendEventWithURL(googleAnalyticsCat, GoogleAnalytics.ACT_PLAYER_PLAY);
    }

	private void updateProgressIndicatorMessage(String message) {
		CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(playerId));
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPushEvent(playerId, message));
	}

    private void showPauseButton() {
        pause.removeStyleName("hideItem");
    }
    
    private void hidePauseButton() {
        pause.addStyleName("hideItem");
    }

    private void showPlayButton() {
        play.removeStyleName("hideItem");
    }
    
    private void hidePlayButton() {
        play.addStyleName("hideItem");
    }
    
    private void showPlayer() {
        this.player.removeStyleName("hideItem");
    }

    private void hidePlayer() {
        this.player.addStyleName("hideItem");
    }
    
    public boolean isPlaying(){
    	    return timer.isRunning();
    }

	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public double getIncreaser() {
		return increaser;
	}

	public void setIncreaser(double increaser) {
		this.increaser = increaser;
	}
    
    private LinkedList<EsaSkyPlayerObserver> observers = new LinkedList<EsaSkyPlayerObserver>();
    
    public void registerValueChangeObserver(EsaSkyPlayerObserver observer) {
 	   observers.add(observer);
    }
    
    private void notifyObservers() {
 	   for(EsaSkyPlayerObserver observer : observers) {
 		   observer.onValueChange(this.value);
 	   }
    }

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
    
}
