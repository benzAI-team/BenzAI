package parsers;

import benzenoid.Benzenoid;
import benzenoid.Node;
import utils.Couple;
import utils.HexNeighborhood;
import utils.RelativeMatrix;
import utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolutionConverter {

    private List<Integer> solution;
    private int nbCrowns;

    private int diameter;

    private int [][] solutionMatrix;

    private int [][] hexagons;

    private int [][] adjacencyMatrix;

    private int [] hexagonsCovered;

    private Node[] verticesCoordinates;

    private RelativeMatrix nodesCoordinates;

    private List<String> hexagonsString;

    public SolutionConverter(List<Integer> solution, int nbCrowns) {
        this.solution = solution;
        this.nbCrowns = nbCrowns;
        buildCoordinatesMatrix();
    }

    public Benzenoid buildBenzenoid() {

        //int nbNodes = solution.size();

        int [] checkedHexagons = new int[diameter * diameter];
        Arrays.fill(checkedHexagons, -1);

        for (int i = 0 ; i < solution.size() ; i++) {
            if (solution.get(i) == 1)
                checkedHexagons[i] = 0;
        }

        int n = 0;
        int indexNode = 0;

        int solutionSize = solution.stream().reduce(0, Integer::sum);

        hexagons = new int [solution.size()][6];
        for (int[] hexagon : hexagons) Arrays.fill(hexagon, -1);

        while (n < solutionSize) {
            int hexagon = findCandidate(checkedHexagons);
            int [] neighborhood = neighborhood(hexagon);

            makeNeighbors(checkedHexagons, hexagons, hexagon, neighborhood);

            for (int i = 0 ; i < neighborhood.length ; i++) {
                if (hexagons[hexagon][i] == -1) {
                    hexagons[hexagon][i] = indexNode;
                    indexNode ++;
                }
            }

            checkedHexagons[hexagon] = 1;
            n++;
        }

        int [][] edgeMatrix = new int[indexNode][indexNode];
        int nbEdges = 0;
        int nbHexagons = 0;

        for (int[] hexagon : hexagons) {
            if (isHexagonFull(hexagon)) {
                nbHexagons++;
                for (int i = 0; i < 6; i++) {
                    int u = hexagon[i];
                    int v = hexagon[(i + 1) % 6];

                    if (edgeMatrix[u][v] == 0) {
                        edgeMatrix[u][v] = 1;
                        edgeMatrix[v][u] = 1;
                        nbEdges++;
                    }
                }
            }
        }

        /*
         * Computing coordinates
         */

        int nbNodes = indexNode;

        verticesCoordinates = new Node[nbNodes];
        nodesCoordinates = new RelativeMatrix(8 * nbHexagons + 1, 16 * nbHexagons + 1, 4 * nbHexagons, 8 * nbHexagons);
        adjacencyMatrix = new int[nbNodes][nbNodes];
        hexagonsCovered = new int[nbHexagons];

        hexagonsString = new ArrayList<>();

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

                                    nodesHexagon[i2] = verticesCoordinates[hexagons[candidat][i]];
                                    nodesHexagon[j2] = verticesCoordinates[hexagons[candidat][j]];

                                }

                                if (hexagons[candidat][i] == hexagons[hexagon][j2] &&
                                        hexagons[candidat][j] == hexagons[hexagon][i2]) {

                                    nodesHexagon[i2] = verticesCoordinates[hexagons[candidat][j]];
                                    nodesHexagon[j2] = verticesCoordinates[hexagons[candidat][i]];

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

                                                    nodesHexagon[i3] = verticesCoordinates[hexagons[hexagon2][i4]];
                                                    nodesHexagon[j3] = verticesCoordinates[hexagons[hexagon2][j4]];
                                                }

                                                if (hexagons[hexagon][i3] == hexagons[hexagon2][j4] &&
                                                        hexagons[hexagon][j3] == hexagons[hexagon2][i4]) {

                                                    nodesHexagon[i3] = verticesCoordinates[hexagons[hexagon2][j4]];
                                                    nodesHexagon[j3] = verticesCoordinates[hexagons[hexagon2][i4]];
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

                                    nodesCoordinates.set(node.getX(), node.getY(), node.getIndex());
                                    nodesCoordinates.set(node2.getX(), node2.getY(), node2.getIndex());

                                    verticesCoordinates[node.getIndex()] = node;
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

        int [][] finalHexagons = new int[nbHexagons][6];
        int index = 0;
        for (int [] hexagon : hexagons) {
            boolean valid = true;
            for (int i = 0 ; i < hexagon.length ; i++) {
                if (hexagon[i] == -1) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                finalHexagons[index] = hexagon;
                index ++;
            }

        }

        Benzenoid benzenoid = new Benzenoid(nbNodes, nbEdges, nbHexagons, finalHexagons, verticesCoordinates, edgeMatrix, nodesCoordinates);
        return benzenoid;
    }

    private void buildCoordinatesMatrix() {

        diameter = (2 * nbCrowns) - 1;
        solutionMatrix = new int[diameter][diameter];

        for (int i = 0 ; i < diameter ; i++)
            Arrays.fill(solutionMatrix[i], -1);

        int index = 0;
        int m = (diameter - 1) / 2;

        int shift = diameter - nbCrowns;

        for (int i = 0 ; i < m ; i++) {

            for (int j = 0 ; j < diameter - shift ; j++) {
                solutionMatrix[i][j] = index;
                index ++;
            }

            for (int j = diameter - shift ; j < diameter ; j++)
                index ++;

            shift --;
        }

        for (int j = 0 ; j < diameter ; j++) {
            solutionMatrix[m][j] = index;
            index ++;
        }

        shift = 1;

        for (int i = m + 1 ; i < diameter ; i++) {

            for (int j = 0 ; j < shift ; j++)
                index ++;

            for (int j = shift ; j < diameter ; j++) {
                solutionMatrix[i][j] = index;
                index ++;
            }
            shift ++;
        }
    }

    private int findCandidate(int [] checkedHexagons) {

        for (int i = 0 ; i < checkedHexagons.length ; i++) {
            if (checkedHexagons[i] == 0)
                return i;
        }
        return -1;
    }

    private int [] neighborhood(int hexagon) {

        int [] neighborhood = new int[6];
        for (int i = 0 ; i < 6 ; i++)
            neighborhood[i] = -1;

        Couple<Integer, Integer> coords = Utils.getHexagonCoords(hexagon, diameter);
        assert coords != null;
        int x = coords.getX();
        int y = coords.getY();

        for(HexNeighborhood neighbor : HexNeighborhood.values()) {
            int x2 = x + neighbor.dx();
            int y2 = y + neighbor.dy();
            if (x2 >= 0 && y2 >= 0 && x2 <= diameter - 1 && y2 <= diameter - 1) {
                int v = solutionMatrix[y2][x2];
                if (v != -1)
                    if (solution.get(v) == 1)
                        neighborhood[neighbor.getIndex()] = v;
            }
        }

        return neighborhood;
    }

    private void makeNeighbors(int[] checkedHexagons, int[][] hexagons, int hexagon, int[] neighborhood) {
        for (int i = 0 ; i < neighborhood.length ; i++) {

            int neighbor = neighborhood[i];

            if (neighbor != -1 && checkedHexagons[neighbor] == 1) {

                if (i == 0) {
                    hexagons[hexagon][0] = hexagons[neighbor][4];
                    hexagons[hexagon][1] = hexagons[neighbor][3];
                } else if (i == 1) {
                    hexagons[hexagon][1] = hexagons[neighbor][5];
                    hexagons[hexagon][2] = hexagons[neighbor][4];
                } else if (i == 2) {
                    hexagons[hexagon][2] = hexagons[neighbor][0];
                    hexagons[hexagon][3] = hexagons[neighbor][5];
                } else if (i == 3) {
                    hexagons[hexagon][3] = hexagons[neighbor][1];
                    hexagons[hexagon][4] = hexagons[neighbor][0];
                } else if (i == 4) {
                    hexagons[hexagon][5] = hexagons[neighbor][1];
                    hexagons[hexagon][4] = hexagons[neighbor][2];
                } else if (i == 5) {
                    hexagons[hexagon][0] = hexagons[neighbor][2];
                    hexagons[hexagon][5] = hexagons[neighbor][3];
                }
            }
        }
    }

    private boolean isHexagonFull(int [] hexagon) {

        for (int j : hexagon) {
            if (j == -1)
                return false;
        }

        return true;
    }

    private void setFirstHexagon() {
        //Adding firstHexagon
        nodesCoordinates.set(0, 0, hexagons[0][0]);
        nodesCoordinates.set(1, 1, hexagons[0][1]);
        nodesCoordinates.set(1, 2, hexagons[0][2]);
        nodesCoordinates.set(0, 3, hexagons[0][3]);
        nodesCoordinates.set(-1, 2, hexagons[0][4]);
        nodesCoordinates.set(-1, 1, hexagons[0][5]);

        verticesCoordinates[hexagons[0][0]] = new Node(0, 0, hexagons[0][0]);
        verticesCoordinates[hexagons[0][1]] = new Node(1, 1, hexagons[0][1]);
        verticesCoordinates[hexagons[0][2]] = new Node(1, 2, hexagons[0][2]);
        verticesCoordinates[hexagons[0][3]] = new Node(0, 3, hexagons[0][3]);
        verticesCoordinates[hexagons[0][4]] = new Node(-1, 2, hexagons[0][4]);
        verticesCoordinates[hexagons[0][5]] = new Node(-1, 1, hexagons[0][5]);

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
}
