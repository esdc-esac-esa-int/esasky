package esac.archive.esasky.cl.web.client.view.common;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;

public class ESASkyPlayerPanel extends Composite {

    private Resources resources;
    private final CssResource style;

    private EsaSkyButton play;
    private EsaSkyButton pause;
    private EsaSkyButton previous;
    private EsaSkyButton next;
    private FlowPanel player = new FlowPanel();
    protected int blinkCounter;
    protected List<Selectable> entries;
    private String playerId = UUID.randomUUID().toString();
    private SurveyBlinkTimer timer = new SurveyBlinkTimer();
    
    /**
     * Timer inner class to execute the blinking.
     *
     */
    private class SurveyBlinkTimer extends Timer {

        public SurveyBlinkTimer() {
        }

        @Override
        public void run() {
        	ESASkySlider slider = SelectSkyPanel.getInstance().slider;
        	if(slider.getCurrentValue()/slider.getMaxValue()<0.01) {
        		//To avoid getting trapped in the start of the slider
        		slider.setValue(slider.getMaxValue()*0.011);
        	}
        	else if(slider.getMaxValue()>slider.getCurrentValue()+0.02) {
        		slider.setValue(slider.getCurrentValue()+0.02);
        	}else {
        		slider.setValue(0.0);
        	}
        }
    }

    /**
     * A ClientBundle that provides images for this widget.
     */
    public static interface Resources extends ClientBundle {

        @Source("previous.png")
        @ImageOptions(flipRtl = true)
        ImageResource previous();

        @Source("next.png")
        @ImageOptions(flipRtl = true)
        ImageResource next();

        @Source("play.png")
        @ImageOptions(flipRtl = true)
        ImageResource play();

        @Source("pause.png")
        @ImageOptions(flipRtl = true)
        ImageResource pause();

        @Source("esaSkyPlayerPanel.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public ESASkyPlayerPanel() {
        this.resources = GWT.create(Resources.class);
        this.style = this.resources.style();
        this.style.ensureInjected();

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

    }

	private void initializeButtons() {
        initializePrevious();
        initializePlay();
        initializePause();
        initializeNext();
	}

	private void initializeNext() {
		this.next = new EsaSkyButton(this.resources.next());
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
		this.pause = new EsaSkyButton(this.resources.pause());
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
		this.play = new EsaSkyButton(this.resources.play());
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
		this.previous = new EsaSkyButton(this.resources.previous());
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
    }

    public final void goToPreviousSurvey() {
        decreaseCounter();
        select();
    }

	private void decreaseCounter() {
		this.blinkCounter = getIndexOfSelected() - 1;

        if (this.blinkCounter < 0) {
            this.blinkCounter = this.entries.size() - 1;
        }
        
		if(!entries.get(blinkCounter).isValid()){
			decreaseCounter();
		}
	}
    
	private void increaseCounter() {
		this.blinkCounter = getIndexOfSelected() + 1;
		
		if (this.blinkCounter >= this.entries.size()) {
		    this.blinkCounter = 0;
		}
		
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
    	    timer.scheduleRepeating(50);
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

    private void pause() {
        stopTimer();
        hidePauseButton();
        showPlayButton();
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent(playerId));
    }

    private void play() {
        	showPauseButton();
        	hidePlayButton();
        startTimer();
        updateProgressIndicatorMessage(TextMgr.getInstance().getText("playerPanel_surveyTourStarted"));
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
    
    protected boolean isPlaying(){
    	    return timer.isRunning();
    }

}
