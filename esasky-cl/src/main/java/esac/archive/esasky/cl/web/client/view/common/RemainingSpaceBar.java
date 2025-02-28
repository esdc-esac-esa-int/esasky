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

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class RemainingSpaceBar extends Composite {
    private FlowPanel barPanel;
    private FlowPanel usedSpace;
    private FlowPanel unusedSpace;
    private Label spaceLabel;
    private final Resources resources = GWT.create(Resources.class);

    private CssResource style;


    public interface Resources extends ClientBundle {

        @Source("remainingSpaceBar.css")
        @CssResource.NotStrict
        CssResource style();
    }
    
    public RemainingSpaceBar() {
        this.style = this.resources.style();
        this.style.ensureInjected();


        FlowPanel container = new FlowPanel();
        barPanel = new FlowPanel();
        usedSpace = new FlowPanel();
        unusedSpace = new FlowPanel();

        usedSpace.setStyleName("usedSpace");
        unusedSpace.setStyleName("unusedSpace");

        barPanel.add(usedSpace);
        barPanel.add(unusedSpace);

        barPanel.setStyleName("remainingSpaceBar");
        barPanel.setSize("100%", "2px");

        setUsedSpace(50);

        spaceLabel = new Label();
        spaceLabel.addStyleName("spaceLabel");

        container.add(spaceLabel);
        container.add(barPanel);

        initWidget(container);
    }

    public void setUsedSpace(double percentage) {

        usedSpace.setWidth(percentage + "%");
        unusedSpace.setWidth(100 - percentage + "%");
    }

    public void setUnusedSpace(double percentage) {
        unusedSpace.setWidth(percentage + "%");
        usedSpace.setWidth(100 - percentage + "%");
    }

    public void setSpace(long quotaBytes, long sizeBytes) {
        double percentage = ((double) sizeBytes / quotaBytes) * 100;
        double quotaMb = bytesToMegabytes(quotaBytes);
        double sizeMb = bytesToMegabytes(sizeBytes);


        setUsedSpace(percentage);

        NumberFormat numberFormat = NumberFormat.getFormat("#.##");
        String title = numberFormat.format(sizeMb) + " MB used out of " + numberFormat.format(quotaMb) + " MB (" + numberFormat.format(percentage) + "%)";
        String shortTitle = numberFormat.format(sizeMb) + " MB / " + numberFormat.format(quotaMb) + " MB";

        spaceLabel.setText(shortTitle);
        this.setTitle(title);
    }

    private double bytesToMegabytes(long bytes) {
        return (double) bytes / (1024 * 1024);
    }


    public void hideLabel(boolean hide) {
        if (hide) {
            spaceLabel.addStyleName("displayNone");
        } else {
            spaceLabel.removeStyleName("displayNone");
        }
    }
}
