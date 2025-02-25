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

package esac.archive.esasky.cl.web.client.view.animation;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

public class LeftSlideAnimation extends EsaSkyAnimation {

    private final Element element;
    
    public LeftSlideAnimation(Element element)
    {
        this.element = element;
    }
 
    @Override
	protected Double getCurrentPosition() {
		String leftString = element.getStyle().getLeft();
		if (leftString.equals("")){
			leftString = "0px";
		}
		//remove suffix "px"
		leftString = leftString.substring(0, leftString.length()-2);
		Double currentPosition = new Double(leftString);
		return currentPosition;
	}
    
    @Override
	protected void setCurrentPosition(double newPosition){
      this.element.getStyle().setLeft(newPosition, Style.Unit.PX);
	}
}
