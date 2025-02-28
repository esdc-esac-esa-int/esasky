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

package esac.archive.esasky.ifcs.model.shared;

import java.util.ArrayList;
import java.util.List;

public class MultiTargetEntity {

    String mainSimbadName;
    List<String> simbadAliases = new ArrayList<String>();
    String userInput;
    String raDeg;
    String decDeg;
    String galdimMajAxis;
    String galdimMinAxis;
    String galdimAngle;
    String description;
    Boolean resolvedByESASky = false;
    double FoVDeg;
    String errorMessage;
    String cooFrame;

    public String getCooFrame() {
        return cooFrame;
    }

    public void setCooFrame(String cooFrame) {
        this.cooFrame = cooFrame;
    }

    public String getRaDeg() {
        return raDeg;
    }

    public void setRaDeg(String raDeg) {
        this.raDeg = raDeg;
    }

    public String getDecDeg() {
        return decDeg;
    }

    public void setDecDeg(String decDeg) {
        this.decDeg = decDeg;
    }

    public String getGaldimMajAxis() {
        return galdimMajAxis;
    }

    public void setGaldimMajAxis(String galdimMajAxis) {
        this.galdimMajAxis = galdimMajAxis;
    }

    public String getGaldimMinAxis() {
        return galdimMinAxis;
    }

    public void setGaldimMinAxis(String galdimMinAxis) {
        this.galdimMinAxis = galdimMinAxis;
    }

    public String getGaldimAngle() {
        return galdimAngle;
    }

    public void setGaldimAngle(String galdimAngle) {
        this.galdimAngle = galdimAngle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainSimbadName() {
        return mainSimbadName;
    }

    public void setMainSimbadName(String mainSimbadName) {
        this.mainSimbadName = mainSimbadName;
    }

    public List<String> getSimbadAliases() {
        return simbadAliases;
    }

    public void setSimbadAliases(List<String> simbadAliases) {
        this.simbadAliases = simbadAliases;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public Boolean getResolvedByESASky() {
        return resolvedByESASky;
    }

    public void setResolvedByESASky(Boolean resolvedByESASky) {
        this.resolvedByESASky = resolvedByESASky;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public double getFoVDeg() {
        double fovDeg = EsaSkyConstants.DEFAULT_FOV; // default value

        String majorAxisArcmin = getGaldimMajAxis();
        if (majorAxisArcmin != null && majorAxisArcmin.trim().length() > 0) {
            fovDeg = (Double.parseDouble(majorAxisArcmin) / 60.);

        }
        return (fovDeg < EsaSkyConstants.MIN_ALLOWED_DEFAULT_FOV) ? EsaSkyConstants.DEFAULT_FOV : fovDeg;
    }

    public void setFoVDeg(double foVDeg) {
        FoVDeg = foVDeg;
    }

}
