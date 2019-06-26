package esac.archive.ammi.ifcs.model.coordinatesutils;

import esac.archive.ammi.ifcs.model.coordinatesutils.CoordinateValidator.RegexClass;
import esac.archive.ammi.ifcs.model.coordinatesutils.CoordinateValidator.SearchInputType;

public class CoordinatesParser {

    public static Double[] parseCoords(RegexClass regexClass, String userInput, CoordinatesFrame cooFrame) {

        CoordinateValidator.SearchInputType inputType = CoordinateValidator.checkInputType(
                regexClass, userInput.trim(), cooFrame);

        String raString = CoordinatesConversion.getRaFromCoords(userInput).trim();
        String decString = CoordinatesConversion.getDecFromCoords(userInput).trim();
        if (null == raString || null == decString) {
            return null;
        }
        Double raDeg = null;
        Double decDeg = null;

        if (inputType == SearchInputType.SPACE_RAhhmmssDECddmmss
                || inputType == SearchInputType.COLUMN_RAhhmmssDECddmmss
                || inputType == SearchInputType.SPACE_RAhhmmssDECdeg
                || inputType == SearchInputType.COLUMN_RAhhmmssDECdeg
                || inputType == SearchInputType.SPACE_RAdddmmssDECddmmss
                || inputType == SearchInputType.COLUMN_RAdddmmssDECddmmss
                || inputType == SearchInputType.SPACE_RAdddmmssDECdeg
                || inputType == SearchInputType.COLUMN_RAdddmmssDECdeg) {

            if (CoordinatesFrame.GALACTIC.getValue().equals(cooFrame.getValue())) {
                raDeg = CoordinatesConversion.convertGalacticRAdddmmssToDecimal(raString);
            } else {
                raDeg = CoordinatesConversion.convertEquatorialRAhhmmssToDecimal(raString);
            }

        } else {
            raDeg = Double.parseDouble(raString);
        }

        if (inputType == SearchInputType.SPACE_RAhhmmssDECddmmss
                || inputType == SearchInputType.COLUMN_RAhhmmssDECddmmss
                || inputType == SearchInputType.SPACE_RAdegDECddmmss
                || inputType == SearchInputType.COLUMN_RAdegDECddmmss
                || inputType == SearchInputType.SPACE_RAdddmmssDECddmmss
                || inputType == SearchInputType.COLUMN_RAdddmmssDECddmmss
                || inputType == SearchInputType.SPACE_RAdddmmssDECdeg
                || inputType == SearchInputType.COLUMN_RAdddmmssDECdeg) {
            decDeg = CoordinatesConversion.convertDECddmmssToDecimal(decString);
        } else {
            decDeg = Double.parseDouble(decString);
        }

        Double[] raDecDeg = null;
        if (raDeg != null && decDeg != null) {
            raDecDeg = new Double[] { raDeg, decDeg };
            if (CoordinatesFrame.GALACTIC.getValue().equalsIgnoreCase(cooFrame.getValue())) {
                // convert to J2000 because we always work in J2000
                raDecDeg = CoordinatesConversion.convertPointGalacticToJ2000(raDeg, decDeg);
            }
            return raDecDeg;
        }
        return null;
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
                || inputType == SearchInputType.SPACE_RAhhmmssDECdeg
                || inputType == SearchInputType.COLUMN_RAhhmmssDECdeg
                || inputType == SearchInputType.SPACE_RAdddmmssDECddmmss
                || inputType == SearchInputType.COLUMN_RAdddmmssDECddmmss
                || inputType == SearchInputType.SPACE_RAdddmmssDECdeg
                || inputType == SearchInputType.COLUMN_RAdddmmssDECdeg) {

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
                || inputType == SearchInputType.SPACE_RAdegDECddmmss
                || inputType == SearchInputType.COLUMN_RAdegDECddmmss
                || inputType == SearchInputType.SPACE_RAdddmmssDECddmmss
                || inputType == SearchInputType.COLUMN_RAdddmmssDECddmmss
                || inputType == SearchInputType.SPACE_RAdddmmssDECdeg
                || inputType == SearchInputType.COLUMN_RAdddmmssDECdeg) {
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
