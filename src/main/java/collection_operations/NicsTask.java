package collection_operations;

import benzenoid.Benzenoid;
import parsers.GraphParser;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class NicsTask extends CollectionTask{
    private final String nicsType;

    NicsTask(String nicsType) {
        super("NICS ("+nicsType+")");
        this.nicsType = nicsType;
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
            for (char type: nicsType.toCharArray()) {
                String finalType;
                BenzenoidCollectionPane.DisplayType displayType;
                System.out.println(type);
                if ((type == 'R') || (type == 'U')) {
                    if (type == 'R') {
                        finalType = "R";
                        displayType = BenzenoidCollectionPane.DisplayType.NICS_R;
                    } else {
                        finalType = "U";
                        displayType = BenzenoidCollectionPane.DisplayType.NICS_U;
                    }
                    Benzenoid benzenoid = currentPane.getMolecule(pane.getIndex());
                    Optional<String> NICS = benzenoid.getDatabaseInformation().findNICS(finalType);

                    if (NICS.isPresent()) {
                        try {
                            FileWriter f = new FileWriter("tmp.graph_coord");
                            f.write(benzenoid.getDatabaseInformation().findGraphFile().get());
                            f.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Benzenoid b = GraphParser.parseUndirectedGraph("tmp.graph_coord", null, false);
                        benzenoidSetPane.addBenzenoid(b, displayType);
                    } else
                        nbNotAvailable++;
                }
            }
        }

        if (nbNotAvailable == currentPane.getSelectedBenzenoidPanes().size()) {
            Utils.alert("No NICS values are available yet for the selection");
            return;
        } else if (nbNotAvailable >= 1)
            Utils.alert("NICS values are available for only "+(currentPane.getSelectedBenzenoidPanes().size()-nbNotAvailable)+" benzenoid(s)");

        addNewSetPane(benzenoidSetPane, collectionManagerPane);
    }
}
