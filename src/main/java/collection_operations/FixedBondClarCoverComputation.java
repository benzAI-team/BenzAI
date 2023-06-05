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
        super("Clar cover with fixed bond");
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
                int[][] bonds = new int[molecule.getNbNodes()][molecule.getNbNodes()];
                for (ClarCoverSolution solution : clarCoverSolutions) {

                    for (int i = 0; i < molecule.getNbHexagons(); i++) {
                        if (solution.isCircle(i)) {
                            for (int j = 0; j < 6; j++) {
                                int k = (j + 1) % 6;

                                int u = molecule.getHexagon(i)[j];
                                int v = molecule.getHexagon(i)[k];

                                bonds[u][v] = -1;
                                bonds[v][u] = -1;
                            }

                            if (circles[i] == 0) // non défini
                                circles[i] = 2;

                            if (circles[i] == 1) // pas de rond
                                circles[i] = -1;
                        }

                        else {
                            if (circles[i] == 0) // non défini
                                circles[i] = 1;

                            if (circles[i] == 2) // rond
                                circles[i] = -1;
                        }
                    }

                    for (int i = 0; i < molecule.getNbNodes(); i++) {
                        for (int j = (i + 1); j < molecule.getNbNodes(); j++) {
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
