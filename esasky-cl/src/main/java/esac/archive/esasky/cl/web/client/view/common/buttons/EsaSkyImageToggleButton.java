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
