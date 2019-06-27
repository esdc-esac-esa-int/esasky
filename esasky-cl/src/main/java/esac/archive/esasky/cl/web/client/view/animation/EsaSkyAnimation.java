package esac.archive.esasky.cl.web.client.view.animation;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.animation.client.Animation;

public abstract class EsaSkyAnimation extends Animation {

    private double from;
    private double currentPosition;
    private double to;
    
    private List<AnimationObserver> observers = new LinkedList<AnimationObserver>();
 
    public void animateTo(double to, int milliseconds)
    {
        if(!isRunning()){
            this.from = getCurrentPosition();
            this.currentPosition = this.from;
        } else {
            	this.from = this.currentPosition;
            	milliseconds /= 2;
        }
        this.to = to;
        if(Math.abs(from - to) < 1e-6) {
        	cancel();
        	onComplete();
        }
        run(milliseconds);
    }
 
    @Override
    protected void onUpdate(double progress)
    {
        currentPosition = from
        		- (progress * from)
        		+ (progress * (to));
        
        setCurrentPosition(currentPosition);
    }
 
    @Override
    protected void onComplete()
    {
        super.onComplete();
        if(Math.abs(this.currentPosition - this.to) < 1){
            from = to;
            setCurrentPosition(to);
            for(AnimationObserver observer : observers){
                observer.onComplete(currentPosition);
            }
        }
        
    }
    
    @Override
    protected void onCancel(){
    	
    }
    
    public void addObserver(AnimationObserver observer){
    	    observers.add(observer);
    }
    
    public void removeObserver(AnimationObserver observer){
        observers.remove(observer);
    }
    
    
	protected abstract Double getCurrentPosition();
	protected abstract void setCurrentPosition(double newPosition);
}
