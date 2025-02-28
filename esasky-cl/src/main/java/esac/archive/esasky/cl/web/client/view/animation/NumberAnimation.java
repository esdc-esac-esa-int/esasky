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

package esac.archive.esasky.cl.web.client.view.animation;

import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.utility.NumberFormatter;

public class NumberAnimation extends EsaSkyAnimation {

    private final Label label;
    private long currentNumber = 0;
 
    public NumberAnimation(Label label)
    {
        this.label = label;
    }
 
    @Override
	protected Double getCurrentPosition() {
        return new Double(currentNumber);
	}
    
    @Override
	protected void setCurrentPosition(double newPosition){
        currentNumber = Math.round(newPosition);
        label.setText(NumberFormatter.formatToNumberWithSpaces(Long.toString(currentNumber)));
	}
}
