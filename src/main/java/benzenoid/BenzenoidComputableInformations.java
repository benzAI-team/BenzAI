package benzenoid;

import solveur.LinAlgorithm;

public class BenzenoidComputableInformations {

    private Benzenoid benzenoid;
    private double nbKekuleStructures = -1;


    public BenzenoidComputableInformations(Benzenoid benzenoid) {
        this.benzenoid = benzenoid;
    }

    public double getNbKekuleStructures() {
        if (nbKekuleStructures == -1) {
            int[] disabledVertices = new int[benzenoid.getNbNodes()];
            int[] degrees = benzenoid.getDegrees();

            SubGraph subGraph = new SubGraph(benzenoid.getEdgeMatrix(), disabledVertices, degrees, LinAlgorithm.PerfectMatchingType.DET);

            nbKekuleStructures = subGraph.getNbPerfectMatchings();
        }

        return nbKekuleStructures;
    }
}
