package gaussbuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import molecules.Molecule;
import utils.Triplet;

public class NewFormat {

	public static void Generate(Molecule molecule, String outputPath) throws IOException {
		Geometry geometry = GeometryBuilder.buildGeometry(molecule);
		File f = new File(outputPath);
		BufferedWriter w = new BufferedWriter(new FileWriter(f));
		
		int nbAtoms = geometry.getCarbons().length + geometry.getHydrogens().size();
		w.write(nbAtoms + "\n");
		
		int index = 1;
		for (Triplet<Double, Double, Double> carbon : geometry.getCarbons()) {
			w.write(index + " " + "C " + carbon.getX() + " " + carbon.getY() + " " + carbon.getZ() + " Csp2 ");
		}
		
		w.close();
	}
}
