package parsers;

import molecules.Node;
import utils.RelativeMatrix;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class GraphCoordFileBuilder {

    private final String inputFileName;
    private final String outputFileName;

    private int nbNodes = -1;
    private int nbEdges = -1;
    private int nbHexagons = -1;

    private int[][] adjacencyMatrix = null;
    private molecules.Node[] nodes = null;
    private ArrayList<String> hexagonsString;

    private RelativeMatrix nodesCoord = null;
    private int[][] hexagons = null;
    private int[] hexagonsCovered = null;

    public GraphCoordFileBuilder(String inputFilename, String outputFilename) {
        this.inputFileName = inputFilename;
        this.outputFileName = outputFilename;
    }

    private boolean isCommentary(String[] splittedLine) {
        return "c".equals(splittedLine[0]);
    }

    public boolean isEdge(String[] splittedLine) {
        return "e".equals(splittedLine[0]);
    }

    private boolean isHexagon(String[] splittedLine) {
        return "h".equals(splittedLine[0]);
    }

    private boolean isHeader(String[] splittedLine) {
        return "p".equals(splittedLine[0]);
    }

    public int invert(int position) {
        return (position + 3) % 6;
    }

    private Point transition(int x, int y, int position) {

        //Constants
        int h = 0;
        if (position == h) return new Point(x + 1, y + 1);
        int HD = 1;
        if (position == HD) return new Point(x, y + 1);
        int BD = 2;
        if (position == BD) return new Point(x - 1, y + 1);
        int b = 3;
        if (position == b) return new Point(x - 1, y - 1);
        int BG = 4;
        if (position == BG) return new Point(x, y - 1);
        int HG = 5;
        if (position == HG) return new Point(x + 1, y - 1);

        return null;
    }

    private void readInput() {
        try {

            BufferedReader r = new BufferedReader(new FileReader(inputFileName));
            String line;

            int hexagonIndex = 0;

            while ((line = r.readLine()) != null) {

                String[] splittedLine = line.split(" ");

                if (!isCommentary(splittedLine)) {

                    if (isHeader(splittedLine)) {

                        nbNodes = Integer.parseInt(splittedLine[2]);
                        nbEdges = Integer.parseInt(splittedLine[3]);
                        nbHexagons = Integer.parseInt(splittedLine[4]);

                        nodes = new Node[nbNodes];
                        nodesCoord = new RelativeMatrix(8 * nbHexagons + 1, 16 * nbHexagons + 1, 4 * nbHexagons, 8 * nbHexagons);
                        adjacencyMatrix = new int[nbNodes][nbNodes];
                        hexagonsCovered = new int[nbHexagons];

                        hexagons = new int[nbHexagons][6];
                    } else if (isHexagon(splittedLine)) {

                        for (int i = 0; i < 6; i++)
                            hexagons[hexagonIndex][i] = Integer.parseInt(splittedLine[i + 1]);

                        hexagonIndex++;
                    }

                }
            }

            r.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setFirstHexagon() {
        //Adding firstHexagon
        nodesCoord.set(0, 0, hexagons[0][0]);
        nodesCoord.set(1, 1, hexagons[0][1]);
        nodesCoord.set(1, 2, hexagons[0][2]);
        nodesCoord.set(0, 3, hexagons[0][3]);
        nodesCoord.set(-1, 2, hexagons[0][4]);
        nodesCoord.set(-1, 1, hexagons[0][5]);

        nodes[hexagons[0][0]] = new Node(0, 0, hexagons[0][0]);
        nodes[hexagons[0][1]] = new Node(1, 1, hexagons[0][1]);
        nodes[hexagons[0][2]] = new Node(1, 2, hexagons[0][2]);
        nodes[hexagons[0][3]] = new Node(0, 3, hexagons[0][3]);
        nodes[hexagons[0][4]] = new Node(-1, 2, hexagons[0][4]);
        nodes[hexagons[0][5]] = new Node(-1, 1, hexagons[0][5]);

        adjacencyMatrix[hexagons[0][0]][hexagons[0][1]] = 1;
        adjacencyMatrix[hexagons[0][1]][hexagons[0][0]] = 1;
        adjacencyMatrix[hexagons[0][1]][hexagons[0][2]] = 1;
        adjacencyMatrix[hexagons[0][2]][hexagons[0][1]] = 1;
        adjacencyMatrix[hexagons[0][2]][hexagons[0][3]] = 1;
        adjacencyMatrix[hexagons[0][3]][hexagons[0][2]] = 1;
        adjacencyMatrix[hexagons[0][3]][hexagons[0][4]] = 1;
        adjacencyMatrix[hexagons[0][4]][hexagons[0][3]] = 1;
        adjacencyMatrix[hexagons[0][4]][hexagons[0][5]] = 1;
        adjacencyMatrix[hexagons[0][5]][hexagons[0][4]] = 1;
        adjacencyMatrix[hexagons[0][5]][hexagons[0][0]] = 1;
        adjacencyMatrix[hexagons[0][0]][hexagons[0][5]] = 1;

        hexagonsString.add("h 0_0 1_1 1_2 0_3 -1_2 -1_1");

        hexagonsCovered[0] = 1;
    }

    private void saveResult() {

        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(outputFileName));

            //System.out.println("p DIMACS " + nbNodes + " " + nbEdges + " " + nbHexagons);
            w.write("p DIMACS " + nbNodes + " " + nbEdges + " " + nbHexagons + "\n");

            for (int i = 0; i < adjacencyMatrix.length; i++) {
                for (int j = i + 1; j < adjacencyMatrix.length; j++) {
                    if (adjacencyMatrix[i][j] == 1) {
                        //System.out.println("e " + nodes[i].getDimacsStr() +  " " + nodes[j].getDimacsStr());
                        w.write("e " + nodes[i].getDimacsStr() + " " + nodes[j].getDimacsStr() + "\n");
                    }
                }
            }

            for (String str : hexagonsString) {
                //System.out.println(str);
                w.write(str + "\n");
            }

            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void convertInstance() {

        //System.out.println(inputFileName);

        hexagonsString = new ArrayList<>();

        readInput();

        setFirstHexagon();

        ArrayList<Integer> candidats = new ArrayList<>();
        candidats.add(0);

        while (!candidats.isEmpty()) {
            int candidat = candidats.get(0);

            for (int hexagon = 0; hexagon < nbHexagons; hexagon++) {
                if (hexagon != candidat && hexagonsCovered[hexagon] == 0) {

                    for (int i = 0; i < 6; i++) {
                        int j = (i + 1) % 6;

                        //CHERCHER SI hexagon est adjacent à candidat

                        for (int i2 = 0; i2 < 6; i2++) {
                            int j2 = (i2 + 1) % 6;

                            if ((hexagons[candidat][i] == hexagons[hexagon][i2] &&
                                    hexagons[candidat][j] == hexagons[hexagon][j2]) ||
                                    (hexagons[candidat][i] == hexagons[hexagon][j2] &&
                                            hexagons[candidat][j] == hexagons[hexagon][i2])) {

                                Node[] nodesHexagon = new Node[6];

                                if (hexagons[candidat][i] == hexagons[hexagon][i2] &&
                                        hexagons[candidat][j] == hexagons[hexagon][j2]) {

                                    nodesHexagon[i2] = nodes[hexagons[candidat][i]];
                                    nodesHexagon[j2] = nodes[hexagons[candidat][j]];

                                }

                                if (hexagons[candidat][i] == hexagons[hexagon][j2] &&
                                        hexagons[candidat][j] == hexagons[hexagon][i2]) {

                                    nodesHexagon[i2] = nodes[hexagons[candidat][j]];
                                    nodesHexagon[j2] = nodes[hexagons[candidat][i]];

                                }

                                //On cherche si il existe d'autres hexagons déja traités à part candidat qui sont adjacents à $hexagone
                                for (int hexagon2 = 0; hexagon2 < nbHexagons; hexagon2++) {
                                    if (hexagon2 != candidat && hexagon2 != hexagon && hexagonsCovered[hexagon2] == 1) {

                                        //test d'adjacence
                                        for (int i3 = 0; i3 < 6; i3++) {
                                            int j3 = (i3 + 1) % 6;

                                            for (int i4 = 0; i4 < 6; i4++) {
                                                int j4 = (i4 + 1) % 6;

                                                if (hexagons[hexagon][i3] == hexagons[hexagon2][i4] &&
                                                        hexagons[hexagon][j3] == hexagons[hexagon2][j4]) {

                                                    nodesHexagon[i3] = nodes[hexagons[hexagon2][i4]];
                                                    nodesHexagon[j3] = nodes[hexagons[hexagon2][j4]];
                                                }

                                                if (hexagons[hexagon][i3] == hexagons[hexagon2][j4] &&
                                                        hexagons[hexagon][j3] == hexagons[hexagon2][i4]) {

                                                    nodesHexagon[i3] = nodes[hexagons[hexagon2][j4]];
                                                    nodesHexagon[j3] = nodes[hexagons[hexagon2][i4]];
                                                }

                                            }
                                        }
                                    }
                                }

                                //puis on ajoute les noeuds non renseignés par rapport à ceux déja connus

                                int firstIndex = 0;
                                for (int index = 0; index < 6; index++) {
                                    if (nodesHexagon[index] != null) {
                                        firstIndex = index;
                                        break;
                                    }
                                }

                                int cpt = 0;
                                while (cpt < 6) {
                                    int nextIndex = (firstIndex + 1) % 6;
                                    if (nodesHexagon[nextIndex] == null) {
                                        assert nodesHexagon[firstIndex] != null;
                                        Point newCoord = transition(nodesHexagon[firstIndex].getX(), nodesHexagon[firstIndex].getY(), firstIndex);
                                        int nodeId = hexagons[hexagon][nextIndex];
                                        nodesHexagon[nextIndex] = new Node((int) newCoord.getX(), (int) newCoord.getY(), nodeId);
                                    }
                                    firstIndex = (firstIndex + 1) % 6;
                                    cpt++;
                                }

                                StringBuilder builder = new StringBuilder();
                                builder.append("h ");
                                for (int index = 0; index < nodesHexagon.length; index++) {
                                    Node node = nodesHexagon[index];
                                    Node node2 = nodesHexagon[(index + 1) % 6];

									assert node != null;
									adjacencyMatrix[node.getIndex()][node2.getIndex()] = 1;
                                    adjacencyMatrix[node2.getIndex()][node.getIndex()] = 1;

                                    builder.append(node.getX()).append("_").append(node.getY());
                                    if (index < nodesHexagon.length - 1)
                                        builder.append(" ");

                                    nodesCoord.set(node.getX(), node.getY(), node.getIndex());
                                    nodesCoord.set(node2.getX(), node2.getY(), node2.getIndex());

                                    nodes[node.getIndex()] = node;
                                }

                                hexagonsString.add(builder.toString());
                                //on ajoute hexagone aux candidats
                                candidats.add(hexagon);
                                hexagonsCovered[hexagon] = 1;
                            }
                        }
                    }
                }
            }
            candidats.remove(0);
        }

        saveResult();
    }

}
