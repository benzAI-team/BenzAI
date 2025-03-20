package collection_operations;

import benzenoid.Benzenoid;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.util.ArrayList;
import java.util.Optional;
import java.io.*;
import parsers.GraphParser;

public class NicsTask extends CollectionTask{
    NicsTask() {
        super("NICS");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();
        
        String name = getName();
        BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(collectionManagerPane, collectionManagerPane.getBenzenoidSetPanes().size(),
                collectionManagerPane.getNextCollectionPaneLabel(currentPane.getName() + "-" + name));
        
        if (currentPane.getSelectedBenzenoidPanes().size() == 0)
            collectionManagerPane.selectAll();

        ArrayList<BenzenoidPane> panes = currentPane.getSelectedBenzenoidPanes();

        int nbNotAvailable = 0; // the number of benzenoids for which the map is not available
        for (BenzenoidPane pane : panes) {

            Benzenoid benzenoid = currentPane.getMolecule(pane.getIndex());
            Optional<String> NICS = benzenoid.getDatabaseInformation().findNICS();

            if (NICS.isPresent()) {
                try {
                  FileWriter f = new FileWriter("tmp.graph_coord");
                  f.write(benzenoid.getDatabaseInformation().findGraphFile().get());
                  f.close();
                } catch (IOException e) {
                  e.printStackTrace();
                }
                Benzenoid b = GraphParser.parseUndirectedGraph("tmp.graph_coord", null, false);
                benzenoidSetPane.addBenzenoid(b, BenzenoidCollectionPane.DisplayType.NICS);
            }
            else
                nbNotAvailable++;
        }

        if (nbNotAvailable == currentPane.getSelectedBenzenoidPanes().size()) {
            Utils.alert("No NICS values are available yet for the selection");
            return;
        } else if (nbNotAvailable >= 1)
            Utils.alert("NICS values are available for only "+(currentPane.getSelectedBenzenoidPanes().size()-nbNotAvailable)+" benzenoid(s)");

        addNewSetPane(benzenoidSetPane, collectionManagerPane);
    }
}
