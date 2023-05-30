package generator.patterns;

import molecules.Node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PatternFileWriter {
    private final Pattern pattern;

    PatternFileWriter(Pattern pattern) {
        this.pattern = pattern;
    }

    /***
     * export pattern in the given file
     */
    public void export(File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writeDegree(writer);
        writeMatrix(writer, "MATRIX\n", pattern.getMatrix());
        writeLabels(writer);
        writeNodes(writer);
        writeCenter(writer);
        writeMatrix(writer, "NEIGHBORS\n", pattern.getNeighborGraph());
        writer.close();
    }

    private void writeCenter(BufferedWriter writer) throws IOException {
        writer.write("CENTER\n");
        if (pattern.getCenter() != null)
            writer.write(pattern.getCenter().getIndex() + "\n");
        else
            writer.write("0\n");
    }

    private void writeNodes(BufferedWriter writer) throws IOException {
        writer.write("NODES\n");
        for (Node node : pattern.getNodesRefs())
            writer.write(node.getX() + " " + node.getY() + "\n");
    }

    private void writeLabels(BufferedWriter writer) throws IOException {
        writer.write("LABELS\n");
        for (PatternLabel label : pattern.getLabels()) writer.write(label.name() +  " ");
        writer.write("\n");
    }

    private void writeDegree(BufferedWriter writer) throws IOException {
        writer.write("DEGREE\n");
        writer.write(pattern.getOrder() + "\n");
    }

    private void writeMatrix(BufferedWriter writer, String str, int[][] neighborGraph) throws IOException {
        writer.write(str);
        for (int[] ints : neighborGraph) {
            for (int anInt : ints)
                writer.write(anInt + " ");
            writer.write("\n");
        }
    }
}