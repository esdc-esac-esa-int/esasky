package esac.archive.esasky.cl.web.client.view.common.buttons;

import com.google.gwt.resources.client.ImageResource;

import java.util.LinkedList;

public class EsaSkyImageToggleButton extends ChangeableIconButton {

    private final LinkedList<EsaSkyImageToggleButtonObserver> observers = new LinkedList<>();

    boolean isToggled = false;

    public EsaSkyImageToggleButton(ImageResource primaryImage, ImageResource secondaryImage) {
        this(primaryImage, secondaryImage, false);
    }

    public EsaSkyImageToggleButton(ImageResource primaryImage, ImageResource secondaryImage, boolean isToggled) {
        super(primaryImage, secondaryImage);
        this.isToggled = isToggled;
        setOnClick(this::toggle);
    }


    public void setToggled(boolean toggled) {
        setToggled(toggled, true);
    }

    public void setToggled(boolean toggled, boolean notifyObservers) {
        this.isToggled = toggled;

        if (isToggled) {
            setPrimaryIcon();
        } else {
            setSecondaryIcon();
        }

        if (notifyObservers) {
            notifyObservers();
        }
    }

    public boolean isToggled() {
        return isToggled;
    }

    public void toggle() {
        setToggled(!isToggled);
    }


    public void registerValueChangeObserver(EsaSkyImageToggleButtonObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for(EsaSkyImageToggleButtonObserver observer : observers) {
            observer.onValueChange(isToggled);
        }
    }
}
