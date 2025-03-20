package collection_operations;

import benzenoid.Benzenoid;
import view.collections.BenzenoidPane;
import benzenoid.BenzenoidParser;

import java.io.File;
import java.io.IOException;

public class CollectionExportGraph extends CollectionExport{
  public CollectionExportGraph(Boolean collection) {
    super(".graph",collection);
  }

  @Override
  public void saveMolecule (BenzenoidPane bp, Benzenoid b, String filename) throws IOException {
    BenzenoidParser.exportToGraphFile(b, new File(filename));
  }
}
