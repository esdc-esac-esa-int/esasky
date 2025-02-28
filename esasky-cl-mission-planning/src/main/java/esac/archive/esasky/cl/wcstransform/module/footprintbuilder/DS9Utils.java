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

package esac.archive.esasky.cl.wcstransform.module.footprintbuilder;

import esac.archive.esasky.ifcs.model.descriptor.DS9Descriptor;

import java.util.Vector;

public final class DS9Utils {
    protected DS9Utils() {

    }

    public static Vector<double[]> getShapePixels(String row) {
        String cleanRow = row.trim().toLowerCase();
        cleanRow = cleanRow.replaceAll("point|circle|polygon|ellipse|box|\\(|\\)", "");
        cleanRow = cleanRow.replaceAll("#.*", ""); // Remove comments
        cleanRow = cleanRow.replaceAll("\\|\\|.*", ""); // Remove comments
        String[] pixelStr = cleanRow.split(",");

        Vector<double[]> pixels = new Vector<>();
        for (int i = 0; i < pixelStr.length; i+=2) {
            double ra = Double.parseDouble(pixelStr[i]);
            double dec = Double.parseDouble(pixelStr[i + 1]);
            double[] vertex = new double[]{ra, dec};
            pixels.add(vertex);
        }

        return pixels;
    }

    public static double[] getReferencePoint(DS9Descriptor descriptor) {
        String point = descriptor.getShapes().stream().filter(shape -> shape.trim().toLowerCase().startsWith("point")).findFirst().orElse(null);

        if (point != null) {
            return getShapePixels(point).get(0);
        } else {
            return new double[0];
        }
    }
}
