package collection_operations;

import benzenoid.Benzenoid;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.util.ArrayList;
import java.util.Optional;

public class Ims2d1aComputation extends CollectionComputation{
    private final String mapType;   // the type of maps
  
    Ims2d1aComputation (String mapType) {
      super("Ims2D_1A ("+mapType+" maps)");
      this.mapType = mapType; 
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {

        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();

        String name = getName();
        BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(collectionManagerPane, collectionManagerPane.getBenzenoidSetPanes().size(),
                collectionManagerPane.getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

        if (currentPane.getSelectedBenzenoidPanes().size() == 0)
            collectionManagerPane.selectAll();

        ArrayList<BenzenoidPane> panes = new ArrayList<>(currentPane.getSelectedBenzenoidPanes());

        int nbNotAvailable = 0; // the number of benzenoids for which the map is not available
        for (BenzenoidPane pane : panes) {
          for (char type: mapType.toCharArray()) {
            String finalType;
            BenzenoidCollectionPane.DisplayType displayType;
            if ((type == 'R') || (type == 'U')) {
              if (type == 'R') {
                finalType = "R";
                displayType = BenzenoidCollectionPane.DisplayType.IMS2D1A_R;
              }
              else {
                finalType = "U";
                displayType = BenzenoidCollectionPane.DisplayType.IMS2D1A_U;
              }
              Benzenoid benzenoid = currentPane.getMolecule(pane.getIndex());
              Optional<String> imsMap = benzenoid.getDatabaseInformation().findimsMap(finalType);

              if (imsMap.isPresent()) {
                  benzenoidSetPane.addBenzenoid(benzenoid, displayType);
              }
              else
                  nbNotAvailable++;
            }
          }
        }

        if (nbNotAvailable == currentPane.getSelectedBenzenoidPanes().size()) {
            Utils.alert("No map is available yet for the selection");
            return;
        } else if (nbNotAvailable == 1)
            Utils.alert("No map is available yet for one benzenoid of the selection");
        else if (nbNotAvailable > 1)
            Utils.alert("No map is available yet for " + nbNotAvailable + " benzenoids of the selection");

        addNewSetPane(benzenoidSetPane, collectionManagerPane);
    }
}
