package molecules;

import classifier.Irregularity;
import solution.ClarCoverSolution;
import solveur.Aromaticity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class BenzenoidParser {

    public static void exportToGraphFile(Benzenoid benzenoid, File file) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        int [][] edgeMatrix = benzenoid.getEdgeMatrix();

        writer.write("p DIMACS " + benzenoid.getNbNodes() + " " + benzenoid.getNbEdges() + " " + benzenoid.getNbHexagons() + "\n");

        for (int i = 0; i < benzenoid.getNbNodes(); i++) {
            for (int j = (i + 1); j < benzenoid.getNbNodes(); j++) {
                if (edgeMatrix[i][j] == 1) {

                    Node u = benzenoid.getNodeRef(i);
                    Node v = benzenoid.getNodeRef(j);

                    writer.write("e " + u.getX() + "_" + u.getY() + " " + v.getX() + "_" + v.getY() + "\n");
                }
            }
        }

        for (int i = 0; i < benzenoid.getNbHexagons(); i++) {

            int[] hexagon = benzenoid.getHexagon(i);
            writer.write("h ");

            for (int j = 0; j < 6; j++) {

                Node u = benzenoid.getNodeRef(hexagon[j]);
                writer.write(u.getX() + "_" + u.getY() + " ");
            }

            writer.write("\n");
        }

        writer.close();
    }

    public static void exportProperties(Benzenoid benzenoid, File file) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        writer.write("molecule_name\t" + benzenoid.getDescription() + "\n");
        writer.write("nb_carbons\t" + benzenoid.getNbNodes() + "\n");
        writer.write("nb_hydrogens\t" + benzenoid.getNbHydrogens() + "\n");
        writer.write("nb_hexagons\t" + benzenoid.getNbHexagons() + "\n");

        String nbKekuleStructures = Double.toString(benzenoid.getNbKekuleStructures()).split(java.util.regex.Pattern.quote("."))[0];

        writer.write(
                new String(("nb_kekule_structures\t" + nbKekuleStructures).getBytes(), StandardCharsets.UTF_8)
                        + "\n");


        Irregularity irregularity = benzenoid.getIrregularity();
        writer.write("XI\t" + irregularity.getXI() + "\n");
        writer.write("#solo\t" + irregularity.getGroup(0) + "\n");
        writer.write("#duo\t" + irregularity.getGroup(1) + "\n");
        writer.write("#trio\t" + irregularity.getGroup(2) + "\n");
        writer.write("#quatuors\t" + irregularity.getGroup(3) + "\n");

        Aromaticity aromaticity = benzenoid.getAromaticity();

        if (aromaticity != null) {
            for (int i = 0; i < aromaticity.getLocalAromaticity().length; i++)
                writer.write("E(H_" + i + ")\t" + aromaticity.getLocalAromaticity()[i] + "\n");
        }

        ArrayList<ClarCoverSolution> clarCoverSolutions = benzenoid.getClarCoverSolutions();
        if (clarCoverSolutions != null) {
            writer.write("\nradicalar statistics\n");
            double[] stats = ClarCoverSolution.getRadicalarStatistics(clarCoverSolutions);
            for (int i = 0; i < Objects.requireNonNull(stats).length; i++)
                writer.write("C" + (i + 1) + " : " + stats[i] + "\n");
        }

        writer.close();

    }

}
