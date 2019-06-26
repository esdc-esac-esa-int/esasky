package esac.archive.ammi.ifcs.model.coordinatesutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String args[]) throws IOException {

        byte[] encoded = Files.readAllBytes(Paths
                .get("src/java/esac/archive/ammi/ifcs/model/coordinatesutils/prova"));
        // File fileInput = new File("prova");
        String input = new String(encoded, StandardCharsets.UTF_8);
        String[] raDec = input.split("\\|");
        StringBuilder sb = new StringBuilder();
        sb.append("overlay.add(A.polyline([");

        int count = 0;

        Double previusRaDeg = null;
        Double previusDecDeg = null;

        for (int i = 0; i < raDec.length; i++) {

            String raHMS = raDec[i].split(",")[0];
            String decDMS = raDec[i].split(",")[1];
            Double raDeg = CoordinatesConversion.convertEquatorialRAhhmmssToDecimal(raHMS);
            Double decDeg = CoordinatesConversion.convertEquatorialRAhhmmssToDecimal(decDMS);

            System.out.println("count " + count);

            if (count == 0 && i > 0) {
                System.out.println("YES");
                sb.append("[" + previusRaDeg + "," + previusDecDeg + "],[" + raDeg + "," + decDeg
                        + "]]));\n overlay.add(A.polyline([");
            }

            if (count == 50) {
                sb.append("[" + raDeg + "," + decDeg + "]]));\n overlay.add(A.polyline([");
                previusRaDeg = raDeg;
                previusDecDeg = decDeg;
                count = -1;
            } else if (i == raDec.length - 1) {
                sb.append("[" + raDeg + "," + decDeg + "]]));");
            } else {
                sb.append("[" + raDeg + "," + decDeg + "],");
            }
            count++;

        }

        File output = new File("src/java/esac/archive/ammi/ifcs/model/coordinatesutils/out");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(output));
            writer.write(sb.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
        // System.out.println(sb.toString());
    }
}
