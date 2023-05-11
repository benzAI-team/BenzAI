package generator.patterns;

import molecules.Node;
import utils.RelativeMatrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PatternFileImport {
    /***
     * import pattern from the given file
     * @return the pattern
     */
    public static Pattern importPattern(File file) throws IOException {
        ArrayList<String>[] lineArray = readPatternFile(file);
        int degree = getDegree(lineArray);
        int[][] matrix = getMatrix(lineArray);
        int[] labels = getLabels(lineArray);
        Node[] nodesRefs = getNodesRefs(lineArray);
        Node centerNode = getCenterNode(lineArray, nodesRefs);
        int[][] neighborGraph = getNeighborGraph(lineArray);
        return new Pattern(matrix, labels, nodesRefs, centerNode, neighborGraph, degree);
    }

    public static int[][] getNeighborGraph(ArrayList<String>[] lineArray) {
        String[] splittedLine;
        String line;
        ArrayList<String> neighborsLines = lineArray[5];
        int[][] neighborGraph = new int[neighborsLines.size()][6];

        for (int i = 0; i < neighborGraph.length; i++) {
            line = neighborsLines.get(i);
            splittedLine = line.split(" ");
            for (int j = 0; j < 6; j++)
                neighborGraph[i][j] = Integer.parseInt(splittedLine[j]);
        }
        return neighborGraph;
    }

    public static Node getCenterNode(ArrayList<String>[] lineArray, Node[] nodesRefs) {
        ArrayList<String> centerLines = lineArray[4];
        return nodesRefs[Integer.parseInt(centerLines.get(0))];
    }

    public static Node[] getNodesRefs(ArrayList<String>[] lineArray) {
        String[] splittedLine;
        String line;
        ArrayList<String> nodesLines = lineArray[3];
        Node[] nodesRefs = new Node[nodesLines.size()];
        RelativeMatrix relativeMatrix = new RelativeMatrix(8 * nodesLines.size() + 1, 16 * nodesLines.size() + 1,
                4 * nodesLines.size(), 8 * nodesLines.size());

        for (int i = 0; i < nodesLines.size(); i++) {
            line = nodesLines.get(i);
            splittedLine = line.split(" ");
            int x = Integer.parseInt(splittedLine[0]);
            int y = Integer.parseInt(splittedLine[1]);
            nodesRefs[i] = new Node(x, y, i);
            relativeMatrix.set(x, y, i);
        }
        return nodesRefs;
    }

    public static int[] getLabels(ArrayList<String>[] lineArray) {
        String line;
        ArrayList<String> labelsLines = lineArray[2];
        line = labelsLines.get(0);
        String[] splittedLine = line.split(" ");

        int[] labels = new int[splittedLine.length];
        for (int i = 0; i < labels.length; i++)
            labels[i] = Integer.parseInt(splittedLine[i]);
        return labels;
    }

    public static int[][] getMatrix(ArrayList<String>[] lineArray) {
        String line;
        ArrayList<String> matrixLines = lineArray[1];
        int[][] matrix = new int[matrixLines.size()][matrixLines.size()];

        for (int i = 0; i < matrixLines.size(); i++) {
            line = matrixLines.get(i);
            String[] splittedLine = line.split(" ");
            for (int j = 0; j < splittedLine.length; j++)
                matrix[i][j] = Integer.parseInt(splittedLine[j]);
        }
        return matrix;
    }

    public static int getDegree(ArrayList<String>[] lineArray) {
        ArrayList<String> degreeLines = lineArray[0];
        return Integer.parseInt(degreeLines.get(0));
    }

    public static ArrayList<String>[] readPatternFile(File file) throws IOException {
        ArrayList<String>[] lineArray = new ArrayList[6];
        for (int i = 0; i < 6; i++)
            lineArray[i] = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int step = 0;
        HashMap<String, Integer> lineTypes = new HashMap<String, Integer>();
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
}