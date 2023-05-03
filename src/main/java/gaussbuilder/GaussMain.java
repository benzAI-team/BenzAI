package gaussbuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import molecules.Molecule;
import parsers.ComConverter.ComType;
import parsers.GraphParser;
import utils.Triplet;

public enum GaussMain {
    ;

    private static void help() {
		System.out.println("# GaussianBuilder");
		System.out.println("Usage: ");
		System.out.println(" --com-ir nb_electrons *.[graph_coord|out]");
		System.out.println(" --com-re *.[graph_coord|out]");
		System.out.println(" --check-geom *.com");
		System.out.println(" --build-ampac *.[graph_coord|out]");
	}
	
	public static void main(String [] args) throws IOException {
	
		try {
			if (args.length == 0) 
				help();
		
			else {
				
				if ("--com-ir".equals(args[0])) {
					
					int nbElectrons = Integer.parseInt(args[1]);
					String inputPath = args[2];
					Geometry geometry = null;
					
					//converti un fichier graph_coord en fichier com(ir)
					if (inputPath.endsWith(".graph_coord") || inputPath.endsWith(".graph")) {
						
						File inputFile = new File(inputPath);
						Molecule molecule = GraphParser.parseUndirectedGraph(inputFile);
						geometry = GeometryBuilder.buildGeometry(molecule);
						
					}
					
					//converti un fichier out (ampac) en fichier com(ir)
					else if (inputPath.endsWith(".out"))	
						geometry = AmpacBuilder.parseAmpacGeometry(inputPath);
					
					
					if (geometry != null) {
						String outputPath = inputPath.split(Pattern.quote("."))[0] + ".com";
						String title = inputPath.split(Pattern.quote("."))[0];
						ComBuilder.buildComFile(geometry, outputPath, nbElectrons, ComType.IR, title);
					}
				}
				
				else if ("--com-re".equals(args[0])) {
					
					String inputPath = args[1];
					Geometry geometry = null;
					
					//converti un fichier graph_coord en fichier com(ir)
					if (inputPath.endsWith(".graph_coord") || inputPath.endsWith(".graph")) {
						
						File inputFile = new File(inputPath);
						Molecule molecule = GraphParser.parseUndirectedGraph(inputFile);
						geometry = GeometryBuilder.buildGeometry(molecule);
						
					}
					
					//converti un fichier out (ampac) en fichier com(ir)
					else if (inputPath.endsWith(".out"))	
						geometry = AmpacBuilder.parseAmpacGeometry(inputPath);
					
					
					if (geometry != null) {
						String outputPath = inputPath.split(Pattern.quote("."))[0] + ".com";
						String title = inputPath.split(Pattern.quote("."))[0];
						ComBuilder.buildComFile(geometry, outputPath, 0, ComType.ER, title);
					}
					
				}
				
				else if ("--check-geom".equals(args[0])) {
					String inputPath = args[1];
					String graphFile = args[1].replace(".com", ".graph_coord");
					Molecule molecule = GraphParser.parseUndirectedGraph(new File(graphFile));
					Geometry geometry = GeometryBuilder.buildGeometry(molecule);
					GaussChecker.checkGeometry(inputPath,molecule, geometry);
				}
				
				else if ("--build-ampac".equals(args[0])) {
					String inputPath = args[1];
					Molecule molecule = GraphParser.parseUndirectedGraph(new File(inputPath));
					AmpacBuilder.buildAmpacFile(molecule, inputPath.split(Pattern.quote("."))[0] + ".dat");
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
			help();
		}
	}
}
