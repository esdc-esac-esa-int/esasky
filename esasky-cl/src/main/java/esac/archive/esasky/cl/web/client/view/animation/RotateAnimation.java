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

import com.google.gwt.dom.client.Element;

public class RotateAnimation extends EsaSkyAnimation {

    private final Element element;
    
    public RotateAnimation(Element element)
    {
        this.element = element;
    }
 
    @Override
	protected Double getCurrentPosition() {
		String transformString = element.getStyle().getProperty("transform");
		if (transformString.equals("")){
			transformString = "rotate(0deg)";
		}
		
		//remove prefix "rotate(" and suffix ")deg"
		transformString = transformString.substring(7, transformString.length()-4);
		Double currentPosition = new Double(transformString);
		return currentPosition;
	}
    
    @Override
	protected void setCurrentPosition(double newPosition){
        	String rotateProperty = "rotate(" + newPosition + "deg)";
        	this.element.getStyle().setProperty("transform", rotateProperty);
	}
}
