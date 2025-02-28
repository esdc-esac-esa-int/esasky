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

package esac.archive.esasky.ifcs.model.coordinatesutils;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.RegexClass;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;

public class CoordinatesParser {

    public static double[] parseCoords(RegexClass regexClass, String userInput, CoordinatesFrame cooFrame) {

        return convertCoordsToDegrees(regexClass, userInput, cooFrame, CoordinatesFrame.J2000);
    }

    public static double[] convertCoordsToDegrees(RegexClass regexClass, String userInput, CoordinatesFrame inputCooFrame,
            CoordinatesFrame outputCooFrame) {

        CoordinateValidator.SearchInputType inputType = CoordinateValidator.checkInputType(regexClass,
                userInput.trim(), inputCooFrame);

        String raString = CoordinatesConversion.getRaFromCoords(userInput).trim();
        String decString = CoordinatesConversion.getDecFromCoords(userInput).trim();
        if (null == raString || null == decString) {
            return null;
        }
        Double raDeg = null;
        Double decDeg = null;

        if (inputType == SearchInputType.SPACE_RA_HHMMSS_DEC_DDMMSS
                || inputType == SearchInputType.COLUMN_RA_HHMMSS_DEC_DDMMSS
                || inputType == SearchInputType.NO_SPACE_RA_HHMMSS_DEC_DDMMSS
                || inputType == SearchInputType.LETTERS_RA_HHMMSS_DEC_DDMMSS
                || inputType == SearchInputType.SPACE_RA_HHMMSS_DEC_DEG
                || inputType == SearchInputType.COLUMN_RA_HHMMSS_DEC_DEG)
        {
            if (CoordinatesFrame.GALACTIC == inputCooFrame) {
                raDeg = CoordinatesConversion.convertGalacticRAdddmmssToDecimal(raString);
            } else {
                raDeg = CoordinatesConversion.convertEquatorialRAhhmmssToDecimal(raString);
            }
        } else {
            raDeg = Double.parseDouble(raString);
        }

        if (inputType == SearchInputType.SPACE_RA_HHMMSS_DEC_DDMMSS
                || inputType == SearchInputType.COLUMN_RA_HHMMSS_DEC_DDMMSS
                || inputType == SearchInputType.LETTERS_RA_HHMMSS_DEC_DDMMSS
                || inputType == SearchInputType.NO_SPACE_RA_HHMMSS_DEC_DDMMSS
                || inputType == SearchInputType.SPACE_RA_DEG_DEC_DDMMSS
                || inputType == SearchInputType.COLUMN_RA_DEG_DEC_DDMMSS
                || inputType == SearchInputType.SPACE_RA_DDDMMSS_DEC_DDMMSS
                || inputType == SearchInputType.COLUMN_RA_DDDMMSS_DEC_DDMMSS
                || inputType == SearchInputType.LETTERS_RA_DDDMMSS_DEC_DDMMSS
                )
        {
            decDeg = CoordinatesConversion.convertDECddmmssToDecimal(decString);
        } else {
            decDeg = Double.parseDouble(decString);
        }
        double[] raDecDeg = null;
        if (raDeg != null && decDeg != null) {
            raDecDeg = new double[] { raDeg, decDeg };
            if (inputCooFrame != outputCooFrame && CoordinatesFrame.J2000 == outputCooFrame) {
                raDecDeg = CoordinatesConversion.convertPointGalacticToJ2000(raDeg, decDeg);
            } else if (inputCooFrame != outputCooFrame
                    && CoordinatesFrame.GALACTIC == outputCooFrame) {
                raDecDeg = CoordinatesConversion.convertPointEquatorialToGalactic(raDeg, decDeg);
            }

            return raDecDeg;
        }
        return null;
    }
}
