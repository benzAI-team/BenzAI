package parsers;

import benzenoid.Benzenoid;
import benzenoid.Node;
import utils.Couple;
import utils.HexNeighborhood;
import utils.RelativeMatrix;
import utils.Utils;

import java.util.Arrays;
import java.util.List;

public class SolutionConverter {

    private List<Integer> solution;
    private int nbCrowns;

    private int diameter;

    private int [][] solutionMatrix;

    private int [][] adjacencyMatrix;

    private int [] hexagonsCovered;

    private Node[] verticesCoordinates;

    private RelativeMatrix nodesCoordinates;

    public SolutionConverter(List<Integer> solution, int nbCrowns) {
        this.solution = solution;
        this.nbCrowns = nbCrowns;
        buildCoordinatesMatrix();
    }

    public Benzenoid buildBenzenoid() {

        int nbNodes = solution.size();

        int [] checkedHexagons = new int[diameter * diameter];
        Arrays.fill(checkedHexagons, -1);

        for (int i = 0 ; i < solution.size() ; i++) {
            if (solution.get(i) == 1)
                checkedHexagons[i] = 0;
        }

        int n = 0;
        int indexNode = 0;

        int solutionSize = solution.stream().reduce(0, Integer::sum);

        int [][] hexagons = new int [solution.size()][6];
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

        verticesCoordinates = new Node[nbNodes];
        nodesCoordinates = new RelativeMatrix(8 * nbHexagons + 1, 16 * nbHexagons + 1, 4 * nbHexagons, 8 * nbHexagons);


        return null;
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
}
