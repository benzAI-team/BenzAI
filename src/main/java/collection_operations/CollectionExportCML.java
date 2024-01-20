package collection_operations;

import benzenoid.Benzenoid;
import view.collections.BenzenoidPane;
import parsers.CMLConverter;

import java.io.File;
import java.io.IOException;

public class CollectionExportCML extends CollectionExport{
  CollectionExportCML(Boolean collection) {
    super(".cml",collection);
  }

  @Override
  public void saveMolecule (BenzenoidPane bp, Benzenoid b, String filename) throws IOException {
    CMLConverter.generateCmlFile(b, new File(filename));
  }
}
