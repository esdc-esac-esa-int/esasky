package esac.archive.esasky.ifcs.model.coordinatesutils;

public class CoordinatesConversion {

    private final static Double[] GALACTIC_TO_J2000 = { -0.0548755604024359, 0.4941094279435681,
            -0.8676661489811610, -0.8734370902479237, -0.4448296299195045, -0.1980763734646737,
            -0.4838350155267381, 0.7469822444763707, 0.4559837762325372 };

    private final static Double[] J2000_TO_GALACTIC = { -0.0548755604024359, -0.873437090247923,
        -0.4838350155267381, 0.4941094279435681, -0.4448296299195045, 0.7469822444763707,
        -0.8676661489811610, -0.1980763734646737, 0.4559837762325372 };

    public static Double[] convertPointGalacticToJ2000(Double latitude, Double longitude) {

        latitude = latitude * Math.PI / 180;
        longitude = longitude * Math.PI / 180;
        Double[] r0 = { Math.cos(latitude) * Math.cos(longitude),
                Math.sin(latitude) * Math.cos(longitude), Math.sin(longitude) };

        Double[] s0 = {
                r0[0] * GALACTIC_TO_J2000[0] + r0[1] * GALACTIC_TO_J2000[1] + r0[2]
                        * GALACTIC_TO_J2000[2],
                r0[0] * GALACTIC_TO_J2000[3] + r0[1] * GALACTIC_TO_J2000[4] + r0[2]
                        * GALACTIC_TO_J2000[5],
                r0[0] * GALACTIC_TO_J2000[6] + r0[1] * GALACTIC_TO_J2000[7] + r0[2]
                        * GALACTIC_TO_J2000[8] };

        Double r = Math.sqrt(s0[0] * s0[0] + s0[1] * s0[1] + s0[2] * s0[2]);

        Double[] result = { 0.0, 0.0 };
        result[1] = Math.asin(s0[2] / r); // New dec in range -90.0 -- +90.0
        // or use sin^2 + cos^2 = 1.0
        Double cosaa = ((s0[0] / r) / Math.cos(result[1]));
        Double sinaa = ((s0[1] / r) / Math.cos(result[1]));
        result[0] = Math.atan2(sinaa, cosaa);
        if (result[0] < 0.0)
            result[0] = result[0] + 2 * Math.PI;

        result[0] = result[0] * 180 / Math.PI;
        result[1] = result[1] * 180 / Math.PI;

        return result;
    }

    public static Double[] convertPointEquatorialToGalactic(Double latitude, Double longitude) {
        latitude = latitude * Math.PI / 180;
        longitude = longitude * Math.PI / 180;
        Double[] r0 = { Math.cos(latitude) * Math.cos(longitude),
                Math.sin(latitude) * Math.cos(longitude), Math.sin(longitude) };

        Double[] s0 = {
                r0[0] * J2000_TO_GALACTIC[0] + r0[1] * J2000_TO_GALACTIC[1] + r0[2]
                        * J2000_TO_GALACTIC[2],
                r0[0] * J2000_TO_GALACTIC[3] + r0[1] * J2000_TO_GALACTIC[4] + r0[2]
                        * J2000_TO_GALACTIC[5],
                r0[0] * J2000_TO_GALACTIC[6] + r0[1] * J2000_TO_GALACTIC[7] + r0[2]
                        * J2000_TO_GALACTIC[8] };

        Double r = Math.sqrt(s0[0] * s0[0] + s0[1] * s0[1] + s0[2] * s0[2]);

        Double[] result = { 0.0, 0.0 };
        result[1] = Math.asin(s0[2] / r); // New dec in range -90.0 -- +90.0
        // or use sin^2 + cos^2 = 1.0
        Double cosaa = ((s0[0] / r) / Math.cos(result[1]));
        Double sinaa = ((s0[1] / r) / Math.cos(result[1]));
        result[0] = Math.atan2(sinaa, cosaa);
        if (result[0] < 0.0)
            result[0] = result[0] + 2 * Math.PI;

        result[0] = result[0] * 180 / Math.PI;
        result[1] = result[1] * 180 / Math.PI;

        return result;
    }

    public static String convertPointListGalacticToJ2000(String commaSeparatedList) {

        String result = "";
        String[] token = commaSeparatedList.split(",");
        for (int i = 0; i < token.length; i = i + 2) {
            Double[] pointConverted = CoordinatesConversion.convertPointGalacticToJ2000(
                    Double.parseDouble(token[0]), Double.parseDouble(token[1]));
            result += pointConverted[0] + "," + pointConverted[1];
            if (i < token.length - 2) {
                result += ",";
            }
        }
        return result;
    }

    /**
     * getRaFromCoords().
     * @param coords Input String
     * @return String
     */
    public static String getRaFromCoords(final String coords) {

        String[] aux;
        String auxRa = "";

        coords.trim();

        if (coords.substring(0, 1).matches("\\+")) {
            aux = coords.split("\\+|-");
            auxRa = aux[1].trim();
        } else {
            aux = coords.split("\\+|-");
            auxRa = aux[0].trim();
        }

        return auxRa;
    }

    /**
     * getDecFromCoords().
     * @param coords Input String
     * @return String
     */
    public static String getDecFromCoords(final String coords) {

        String[] aux;
        String auxDec = "";

        coords.trim();
        String decSign = "+";
        int decSignIndex = coords.indexOf("+", 1);
        if (decSignIndex < 0) {
            decSignIndex = coords.indexOf("-", 1);
            decSign = "-";
        }
        if (coords.substring(0, 1).matches("\\+")) {
            aux = coords.split("\\+|-");

            auxDec = decSign + aux[2].trim();
        } else {
            aux = coords.split("\\+|-");
            auxDec = decSign + aux[1].trim();
        }

        return auxDec;
    }

    /**
     * @param raHMSInput
     * @return
     */
    public static Double convertEquatorialRAhhmmssToDecimal(String raHMSInput) {
        Double result = null;
        String[] tokens = (raHMSInput.trim()).split(":");
        if (tokens.length > 1) {
            double H = Double.parseDouble(tokens[0]);
            double decDegrees = H * 15;

            double mmHMS = Double.parseDouble(tokens[1]);
            double mmDAminAsec = mmHMS * 15;
            decDegrees += mmDAminAsec / 60;

            if (tokens.length == 3) {
                double ssHMS = Double.parseDouble(tokens[2]);
                double ssDAminAsec = ssHMS * 15;
                decDegrees += ssDAminAsec / 3600;
            }
            result = decDegrees;
            return result;
        }
        tokens = (raHMSInput.trim()).split("\\s");
        if (tokens.length > 1) {
            double H = Double.parseDouble(tokens[0]);
            double decDegrees = H * 15;

            double mmHMS = Double.parseDouble(tokens[1]);
            double mmDAminAsec = mmHMS * 15;
            decDegrees += mmDAminAsec / 60;

            if (tokens.length == 3) {
                double ssHMS = Double.parseDouble(tokens[2]);
                double ssDAminAsec = ssHMS * 15;
                decDegrees += ssDAminAsec / 3600;
            }
            result = decDegrees;
            return result;
        }
        return Double.parseDouble(raHMSInput);
    }

    /**
     * @param decDMSInput
     * @return
     */
    public static Double convertDECddmmssToDecimal(String decDMSInput) {
        Double result = null;
        boolean isNegative = decDMSInput.trim().startsWith("-");
        String[] tokens = (decDMSInput.trim()).split(":|\\s");
        if (tokens.length > 1) {
            double D = Double.parseDouble(tokens[0]);
            double decDegrees = D;

            double mmDAminAsec = Double.parseDouble(tokens[1]);
            
           
            if (isNegative) {
                decDegrees -= mmDAminAsec / 60;
            } else {
                decDegrees += mmDAminAsec / 60;
            }

            if (tokens.length == 3) {
                double ssDAminAsec = Double.parseDouble(tokens[2]);
                if (isNegative) {
                    decDegrees -= ssDAminAsec / 3600;
                } else {
                    decDegrees += ssDAminAsec / 3600;
                }

            }
            result = decDegrees;
            return result;
        }

        return Double.parseDouble(decDMSInput);
    }

    /**
     * @param raDMSInput
     * @return
     */
    public static Double convertGalacticRAdddmmssToDecimal(String raDMSInput) {
        Double result = null;
        boolean isNegative = raDMSInput.trim().startsWith("-");
        String[] tokens = (raDMSInput.trim()).split(":|\\s");
        if (tokens.length > 1) {
            double D = Double.parseDouble(tokens[0]);
            double decDegrees = D;

            double mmDAminAsec = Double.parseDouble(tokens[1]);
            if (isNegative) {
                decDegrees -= mmDAminAsec / 60;
            } else {
                decDegrees += mmDAminAsec / 60;
            }

            if (tokens.length == 3) {
                double ssDAminAsec = Double.parseDouble(tokens[2]);
                if (isNegative) {
                    decDegrees -= ssDAminAsec / 3600;
                } else {
                    decDegrees += ssDAminAsec / 3600;
                }

            }
            result = decDegrees;
            if (result < 0) {
                result = 360 + result;
            }
            return result;
        }

        return Double.parseDouble(raDMSInput);
    }

}
