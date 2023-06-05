package molecules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

}
