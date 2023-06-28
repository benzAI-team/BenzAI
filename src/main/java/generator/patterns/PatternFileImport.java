package generator.patterns;

import benzenoid.Node;
import utils.RelativeMatrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public enum PatternFileImport {
    ;

    /***
     * import pattern from the given file
     * @return the pattern
     */
    public static Pattern importPattern(File file) throws IOException {
        ArrayList<String>[] lineArray = readPatternFile(file);
        int degree = getDegree(lineArray[0]);
        int[][] matrix = getMatrix(lineArray[1]);
        PatternLabel[] labels = getLabels(lineArray[2]);
        Node[] nodesRefs = getNodesRefs(lineArray[3]);
        Node centerNode = getCenterNode(lineArray[4], nodesRefs);
        int[][] neighborGraph = getNeighborGraph(lineArray[5]);
        return new Pattern(matrix, labels, nodesRefs, centerNode, neighborGraph, degree);
    }
    private static ArrayList<String>[] readPatternFile(File file) throws IOException {
        ArrayList<String>[] lineArray = new ArrayList[6];
        for (int i = 0; i < 6; i++)
            lineArray[i] = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int step = 0;
        HashMap<String, Integer> lineTypes = new HashMap<>();
        lineTypes.put("DEGREE", 0);
        lineTypes.put("MATRIX", 1);
        lineTypes.put("LABELS", 2);
        lineTypes.put("NODES", 3);
        lineTypes.put("CENTER", 4);
        lineTypes.put("NEIGHBORS", 5);
        while ((line = reader.readLine()) != null)
            if (lineTypes.containsKey(line))
                step = lineTypes.get(line);
            else
                lineArray[step].add(line);
        reader.close();
        return lineArray;
    }
    private static int getDegree(ArrayList<String> degreeList) {
        return Integer.parseInt(degreeList.get(0));
    }
    public static int[][] getMatrix(ArrayList<String> matrixLines) {
        int[][] matrix = new int[matrixLines.size()][matrixLines.size()];
        for (int i = 0; i < matrixLines.size(); i++) {
            String[] adjacencyStrings = matrixLines.get(i).split(" ");
            for (int j = 0; j < adjacencyStrings.length; j++)
                matrix[i][j] = Integer.parseInt(adjacencyStrings[j]);
        }
        return matrix;
    }

    private static PatternLabel[] getLabels(ArrayList<String> labelsLines) {
        String[] labelStrings = labelsLines.get(0).split(" ");
        PatternLabel[] labels = new PatternLabel[labelStrings.length];
        for (int i = 0; i < labels.length; i++)
            labels[i] = PatternLabel.valueOf(labelStrings[i]);
        return labels;
    }

    public static Node[] getNodesRefs(ArrayList<String> nodesLines) {
        int nbNodes = nodesLines.size();
        Node[] nodesRefs = new Node[nbNodes];
        // TODO *8 ???
        RelativeMatrix relativeMatrix = new RelativeMatrix(8 * nbNodes + 1, 16 * nbNodes + 1,
                4 * nbNodes, 8 * nbNodes);

        for (int i = 0; i < nbNodes; i++) {
            String[] coordString = nodesLines.get(i).split(" ");
            int x = Integer.parseInt(coordString[0]);
            int y = Integer.parseInt(coordString[1]);
            nodesRefs[i] = new Node(x, y, i);
            relativeMatrix.set(x, y, i);
        }
        return nodesRefs;
    }

    private static Node getCenterNode(ArrayList<String> centerLines, Node[] nodesRefs) {
        return nodesRefs[Integer.parseInt(centerLines.get(0))];
    }

    private static int[][] getNeighborGraph(ArrayList<String> neighborsLines) {
        int[][] neighborGraph = new int[neighborsLines.size()][6];
        for (int i = 0; i < neighborGraph.length; i++) {
            String[] neighborStrings = neighborsLines.get(i).split(" ");
            for (int j = 0; j < 6; j++)
                neighborGraph[i][j] = Integer.parseInt(neighborStrings[j]);
        }
        return neighborGraph;
    }
}