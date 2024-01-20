package collection_operations;

import benzenoid.Benzenoid;
import view.collections.BenzenoidPane;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class CollectionExportPNG extends CollectionExport{
  CollectionExportPNG(Boolean collection) {
    super(".png",collection);
  }

  @Override
  public void saveMolecule (BenzenoidPane bp, Benzenoid b, String filename) throws IOException {
    bp.exportAsPNG(new File(filename));
  }
}
