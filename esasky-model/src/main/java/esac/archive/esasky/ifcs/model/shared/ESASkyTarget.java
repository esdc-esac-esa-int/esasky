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

package esac.archive.esasky.ifcs.model.shared;

public class ESASkyTarget {

    String name;
    String cooFrame;
    String ra;
    String dec;
    String fovDeg;
    String hipsName;
    String title;
    String description;
    
    /**
     * Defaults constructor.
     */
    public ESASkyTarget() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCooFrame() {
        return cooFrame;
    }

    public void setCooFrame(String cooFrame) {
        this.cooFrame = cooFrame;
    }
    
    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }
    
    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }
    
    public String getFovDeg() {
        return fovDeg;
    }

    public void setFovDeg(String fovDeg) {
        this.fovDeg = fovDeg;
    }
    
    public String getHipsName() {
        return hipsName;
    }

    public void setHipsName(String hipsName) {
        this.hipsName = hipsName;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return (title != null && !title.isEmpty()) ? title : name;
    }
}
