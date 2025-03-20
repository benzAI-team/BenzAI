package collection_operations;

import benzenoid.Benzenoid;
import view.collections.BenzenoidPane;
import benzenoid.BenzenoidParser;

import java.io.File;
import java.io.IOException;

public class CollectionExportDot extends CollectionExport{
  public CollectionExportDot(Boolean collection) {
    super(".dot",collection);
  }

  @Override
  public void saveMolecule (BenzenoidPane bp, Benzenoid b, String filename) throws IOException {
    BenzenoidParser.exportToDotFile(b, new File(filename));
  }
}
