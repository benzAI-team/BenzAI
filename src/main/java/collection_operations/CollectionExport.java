package collection_operations;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import benzenoid.Benzenoid;
import view.collections.BenzenoidPane;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

abstract public class CollectionExport extends CollectionOperation{
  String extension;
  ArrayList<BenzenoidPane> exportList;
  Boolean collection;
  
  CollectionExport(String label, Boolean collection) {
    super(label+((collection)?" ":""));
    this.extension = label;
    this.exportList = new ArrayList<BenzenoidPane>();
    this.collection = collection;
  }
    
    
  @Override
  public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
    BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();

    // we build the list of benzenoids to be exported
    if (this.collection) {
      // we export all the benzenoids in the current collection
      for (int i = 0; i < currentPane.getBenzenoidPanes().size(); i++) {
        exportList.add(currentPane.getBenzenoidPanes().get(i));
      }
    }
    else {
      if (currentPane.getSelectedBenzenoidPanes().size() == 0) {
        // the selection is empty, so we consider the hover benzenoid
        if (collectionManagerPane.getHoveringPane() != null) {
          exportList.add(collectionManagerPane.getHoveringPane());
        }
      }
      else
       {
         // we export all the selected benzenoids
         for (int i = 0; i < currentPane.getSelectedBenzenoidPanes().size(); i++) {
           exportList.add(currentPane.getSelectedBenzenoidPanes().get(i));
         }
       }
    }

    // we select the filename or the directory (depending on the number of benzenoids to export)
    String filename = "";
    String directoryPath = ""; 

    if (exportList.size() == 1) {
      if (collectionManagerPane.getHoveringPane() != null) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(collectionManagerPane.getApplication().getStage());
        if (file != null) {
          filename = file.getAbsolutePath();
        }
        System.out.println(filename);
      }
    }
    else {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      File directory = directoryChooser.showDialog(collectionManagerPane.getApplication().getStage());

      if (directory != null) {
        directoryPath = directory.getAbsolutePath();
      }
    }

    // we export each desired benzenoid
    for (int i = 0; i < exportList.size(); i++) {
      BenzenoidPane bp = exportList.get(i);
      Benzenoid molecule = currentPane.getMolecule(bp.getIndex());

      System.out.println ("Filename "+filename);
      System.out.println ("DirectoryPathFile "+directoryPath);

      if (! directoryPath.equals(""))
        filename = directoryPath+File.separator+molecule.getNames().get(0)+this.extension;
      
      if (! filename.contains(this.extension)) {
        filename += this.extension;
      }

      System.out.println ("File "+filename);

      try {
        saveMolecule (bp, molecule, filename);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  abstract public void saveMolecule (BenzenoidPane bp, Benzenoid b, String filename) throws IOException;


  //~ @Override
  //~ public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
      //~ DirectoryChooser directoryChooser = new DirectoryChooser();
      //~ File directory = directoryChooser.showDialog(collectionManagerPane.getApplication().getStage());

      //~ if (directory != null)
          //~ collectionManagerPane.getSelectedTab().export(directory);
  //~ }
}
