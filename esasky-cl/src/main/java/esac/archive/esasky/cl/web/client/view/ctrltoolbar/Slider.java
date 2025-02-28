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

package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;

import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.hips.HipsOpacCngEvent;

public class Slider extends Composite {

    Resources resources = GWT.create(Resources.class);
    CssResource style;

    FocusPanel bar;
    FlowPanel slider;
    FlowPanel info;
    HandlerRegistration mouseMoveReg;

    public Slider() {
        initView();
    }
    public static interface Resources extends ClientBundle {
        @Source("slider.css")
        @CssResource.NotStrict
        CssResource style();
    }

    private void initView() {
        this.style = this.resources.style();
        this.style.ensureInjected();

        this.bar = new FocusPanel();
        this.bar.getElement().setId("bar");

        this.slider = new FlowPanel();
        this.slider.getElement().setId("slider");

        this.bar.add(slider);

        this.bar.addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent event) {
                Log.debug("Mouse down");
                Slider.this.startSlider(event);
            }
        });

        this.bar.addMouseUpHandler(new MouseUpHandler() {

            @Override
            public void onMouseUp(MouseUpEvent event) {
                Log.debug("Mouse up");
                Slider.this.stopSlider(event);
            }
        });
        initWidget(bar);
    }

    protected void startSlider(MouseDownEvent event) {
        Double percentage = new Double((new Double(event.getClientX()) - new Double(this.bar
                .getElement().getOffsetLeft()))
                / new Double(this.bar.getElement().getOffsetWidth()));
        this.slider.getElement().getStyle().setWidth(percentage * 100, Unit.PCT);
        this.mouseMoveReg = Slider.this.bar.addMouseMoveHandler(new MouseMoveHandler() {

            @Override
            public void onMouseMove(MouseMoveEvent event) {
                Slider.this.moveSlider(event);
            }
        });
    }

    protected void moveSlider(MouseMoveEvent event) {
        Double percentage = new Double((new Double(event.getClientX()) - new Double(this.bar
                .getElement().getOffsetLeft()))
                / new Double(this.bar.getElement().getOffsetWidth()));
        this.slider.getElement().getStyle().setWidth(percentage * 100, Unit.PCT);
    }

    protected void stopSlider(MouseUpEvent event) {
        Double percentage = new Double((new Double(event.getClientX()) - new Double(this.bar
                .getElement().getOffsetLeft()))
                / new Double(this.bar.getElement().getOffsetWidth()));
        this.mouseMoveReg.removeHandler();
        Log.debug("value: " + percentage + " percentage: " + percentage * 100);
        this.slider.getElement().getStyle().setWidth(percentage * 100, Unit.PCT);
        CommonEventBus.getEventBus().fireEvent(new HipsOpacCngEvent(percentage));

    }

}
