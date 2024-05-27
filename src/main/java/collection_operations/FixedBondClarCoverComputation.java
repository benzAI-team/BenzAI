package collection_operations;

import benzenoid.Benzenoid;
import solution.ClarCoverSolution;
import solveur.ClarCoverSolver;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.util.ArrayList;

public class FixedBondClarCoverComputation extends CollectionComputation{

    FixedBondClarCoverComputation() {
        super("A Clar cover (with fixed bond)");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();

        if (currentPane.getBenzenoidPanes().size() == 0) {
            Utils.alert("There is no benzenoid!");
            return;
        }

        ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

        String name = "Clar cover - fixed bonds";
        BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(collectionManagerPane, collectionManagerPane.getBenzenoidSetPanes().size(),
                collectionManagerPane.getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

        if (selectedBenzenoidPanes.size() == 0) {
            collectionManagerPane.selectAll();
        }

        ArrayList<BenzenoidPane> panes = new ArrayList<>(selectedBenzenoidPanes);

        int size = panes.size();

        System.out.println("Computing Clar Cover of " + size + "benzenoids");
        collectionManagerPane.log("Clar Cover (" + size + "benzenoids)", true);

        for (BenzenoidPane benzenoidPane : panes) {
            Benzenoid molecule = currentPane.getMolecule(benzenoidPane.getIndex());
            ArrayList<ClarCoverSolution> clarCoverSolutions = ClarCoverSolver.solve(molecule);
            if (clarCoverSolutions.size() > 0) {
                // 0 = non défini // 1 = pas de cercle // 2 = cercle
                int[] circles = new int[molecule.getNbHexagons()];
                // (i,j) = 1 => full simple // (i,j) = 2 => full double
                int[][] bonds = new int[molecule.getNbCarbons()][molecule.getNbCarbons()];
                for (ClarCoverSolution solution : clarCoverSolutions) {

                    for (int hexagonIndex = 0; hexagonIndex < molecule.getNbHexagons(); hexagonIndex++) {
                        if (solution.isCircle(hexagonIndex)) {
                            for (int carbonIndex = 0; carbonIndex < 6; carbonIndex++) {
                                int nextCarbonIndex = (carbonIndex + 1) % 6;

                                int u = molecule.getHexagon(hexagonIndex)[carbonIndex];
                                int v = molecule.getHexagon(hexagonIndex)[nextCarbonIndex];

                                bonds[u][v] = -1;
                                bonds[v][u] = -1;
                            }

                            if (circles[hexagonIndex] == 0) // non défini
                                circles[hexagonIndex] = 2;

                            if (circles[hexagonIndex] == 1) // pas de rond
                                circles[hexagonIndex] = -1;
                        }

                        else {
                            if (circles[hexagonIndex] == 0) // non défini
                                circles[hexagonIndex] = 1;

                            if (circles[hexagonIndex] == 2) // rond
                                circles[hexagonIndex] = -1;
                        }
                    }

                    for (int i = 0; i < molecule.getNbCarbons(); i++) {
                        for (int j = (i + 1); j < molecule.getNbCarbons(); j++) {
                            if (molecule.getEdgeMatrix()[i][j] == 1) {
                                if (solution.isDoubleBond(i, j)) {
                                    if (bonds[i][j] == 0) {
                                        bonds[i][j] = 2;
                                        bonds[j][i] = 2;
                                    } else if (bonds[i][j] == 1) {
                                        bonds[i][j] = -1;
                                        bonds[j][i] = -1;
                                    }
                                } else {
                                    if (bonds[i][j] == 0) {
                                        bonds[i][j] = 1;
                                        bonds[j][i] = 1;
                                    } else if (bonds[i][j] == 2) {
                                        bonds[i][j] = -1;
                                        bonds[j][i] = -1;
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.print("");
                molecule.setFixedBonds(bonds);
                molecule.setFixedCircles(circles);

                ClarCoverSolution clarCoverSolution = clarCoverSolutions.get(clarCoverSolutions.size() - 1);
                molecule.setClarCoverSolution(clarCoverSolution);
                benzenoidSetPane.addBenzenoid(molecule, BenzenoidCollectionPane.DisplayType.CLAR_COVER_FIXED);

            }
        }
        addNewSetPane(benzenoidSetPane, collectionManagerPane);
    }
}
