package benzenoid;

import classifier.Irregularity;
import solveur.Aromaticity;
import solveur.LinAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BenzenoidComputableInformations {

    private Benzenoid benzenoid;
    private double nbKekuleStructures = -1;

    private Optional<Irregularity> irregularity;

    private Optional<Aromaticity> aromaticity;

    private List<Integer> verticesSolutions;

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

    public Optional<Irregularity> computeParameterOfIrregularity() {

        if (irregularity == null) {

            if (benzenoid.getNbHexagons() == 1)
                irregularity = Optional.empty();

            int[] N = new int[4];
            int[] checkedNodes = new int[benzenoid.getNbNodes()];

            ArrayList<Integer> V = new ArrayList<>();

            for (int u = 0; u < benzenoid.getNbNodes(); u++) {
                int degree = benzenoid.degree(u);
                if (degree == 2 && !V.contains(u)) {
                    V.add(u);
                    checkedNodes[u] = 0;
                }

                else if (degree != 2)
                    checkedNodes[u] = -1;
            }

            ArrayList<Integer> candidats = new ArrayList<>();

            while (true) {

                int firstVertice = -1;
                for (Integer u : V) {
                    if (checkedNodes[u] == 0) {
                        firstVertice = u;
                        break;
                    }
                }

                if (firstVertice == -1)
                    break;

                candidats.add(firstVertice);
                checkedNodes[firstVertice] = 1;

                int nbNeighbors = 1;

                while (!candidats.isEmpty()) {

                    int candidat = candidats.get(0);

                    for (int i = 0; i < benzenoid.getNbNodes(); i++) {
                        if (benzenoid.getEdgeMatrix()[candidat][i] == 1 && checkedNodes[i] == 0) {

                            checkedNodes[i] = 1;
                            nbNeighbors++;
                            candidats.add(i);
                        }
                    }

                    candidats.remove(candidats.get(0));
                }

                N[nbNeighbors - 1] += nbNeighbors;
            }

            double XI = ((double) N[2] + (double) N[3]) / ((double) N[0] + (double) N[1] + (double) N[2] + (double) N[3]);
            Irregularity irregularityData = new Irregularity(N, XI);
            irregularity = Optional.of(irregularityData);
        }

        return irregularity;
    }

    public Optional<Aromaticity> getAromaticity() {
        if (aromaticity == null) {
            try {
                aromaticity = Optional.of(LinAlgorithm.solve(benzenoid, LinAlgorithm.PerfectMatchingType.DET));
                aromaticity.get().normalize(getNbKekuleStructures());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return aromaticity;
    }
}
