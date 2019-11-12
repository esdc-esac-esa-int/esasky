package esac.archive.esasky.ifcs.model.coordinatesutils;

import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.RegexClass;
import esac.archive.esasky.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;

public class CoordinatesParser {

    public static Double[] parseCoords(RegexClass regexClass, String userInput, CoordinatesFrame cooFrame) {

        return convertCoordsToDegrees(regexClass, userInput, cooFrame, CoordinatesFrame.J2000);
    }

    public static Double[] convertCoordsToDegrees(RegexClass regexClass, String userInput, CoordinatesFrame inputCooFrame,
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

        if (inputType == SearchInputType.SPACE_RAhhmmssDECddmmss
                || inputType == SearchInputType.COLUMN_RAhhmmssDECddmmss
                || inputType == SearchInputType.LETTERS_RAhhmmssDECddmmss
                || inputType == SearchInputType.SPACE_RAhhmmssDECdeg
                || inputType == SearchInputType.COLUMN_RAhhmmssDECdeg)
        {

            if (CoordinatesFrame.GALACTIC == inputCooFrame) {
                raDeg = CoordinatesConversion.convertGalacticRAdddmmssToDecimal(raString);
            } else {
                raDeg = CoordinatesConversion.convertEquatorialRAhhmmssToDecimal(raString);
            }
        } else {
            raDeg = Double.parseDouble(raString);
        }

        if (inputType == SearchInputType.SPACE_RAhhmmssDECddmmss
                || inputType == SearchInputType.COLUMN_RAhhmmssDECddmmss
                || inputType == SearchInputType.LETTERS_RAhhmmssDECddmmss
                || inputType == SearchInputType.SPACE_RAdegDECddmmss
                || inputType == SearchInputType.COLUMN_RAdegDECddmmss)
        {
            decDeg = CoordinatesConversion.convertDECddmmssToDecimal(decString);
        } else {
            decDeg = Double.parseDouble(decString);
        }
        Double[] raDecDeg = null;
        if (raDeg != null && decDeg != null) {
            raDecDeg = new Double[] { raDeg, decDeg };
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
