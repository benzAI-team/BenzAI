package collection_operations;

import benzenoid.Benzenoid;
import view.collections.BenzenoidPane;
import parsers.ComConverter;

import java.io.File;
import java.io.IOException;

public class CollectionExportCOM extends CollectionExport{
  CollectionExportCOM(Boolean collection) {
    super(".com",collection);
  }

  @Override
  public void saveMolecule (BenzenoidPane bp, Benzenoid b, String filename) throws IOException {
    ComConverter.generateComFile(b, new File (filename), 0, ComConverter.ComType.ER, filename);
  }
}
