package database;

import benzenoid.AtomGeometry;
import benzenoid.Benzenoid;
import parsers.GraphParser;
import spectrums.ResultLogFile;
import spectrums.SpectrumsComputer;
import utils.Triplet;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.regex.Pattern;

public enum BuildScripts {
    ;

    private static String directory;
	private static BufferedWriter writer;

	private static long indexNames;
	private static long indexIms2d;
	private static long indexPoint;
	private static long indexAtom;

	private static void initialize() throws IOException {

		directory = "/home/adrien/Documents/old_log_files";
		writer = new BufferedWriter(new FileWriter(new File(directory + "/insert_with_inchi.sql")));
		indexNames = 0;
		indexIms2d = 0;
		indexPoint = 0;
		indexAtom = 0;
	}

	private static void insertBenzenoid(long id, Benzenoid molecule) throws IOException {

		BigDecimal irregbd;

		ArrayList<String> names = molecule.getNames();

		if (molecule.getIrregularity() != null)
			irregbd = BigDecimal.valueOf(molecule.getIrregularity().getXI()).setScale(3, RoundingMode.HALF_UP);
		else
			irregbd = BigDecimal.valueOf(-1.0).setScale(3, RoundingMode.HALF_UP);

		writer.write("INSERT INTO benzenoid (id, irregularity, nbCarbons, nbHexagons, nbHydrogens)\n");
		writer.write("\tVALUES (" + id + ", " + irregbd.doubleValue() + ", " + molecule.getNbNodes() + ", "
				+ molecule.getNbHexagons() + ", " + molecule.getNbHydrogens() + ");\n\n");

		for (String moleculeName : names) {

			writer.write("INSERT INTO name (idName, idMolecule, name)\n");
			writer.write("\tVALUES(" + indexNames + ", " + id + ", '" + moleculeName + "');\n");

			indexNames++;
		}

		writer.write("\n");

	}

	private static void insertIRSpectra(long id, File irFile, File graphFile) throws IOException {

		ResultLogFile log = SpectrumsComputer.parseLogFile(irFile.getAbsolutePath());

		Double e = log.getFinalEnergy().get(log.getFinalEnergy().size() - 1);
		BigDecimal bd = new BigDecimal(e).setScale(8, RoundingMode.HALF_UP);
		double finalEnergy = bd.doubleValue();

		StringBuilder frequencies = new StringBuilder();
		for (int i = 0; i < log.getFrequencies().size(); i++) {
			Double freq = log.getFrequencies().get(i);

			if (freq < 0.0)
				System.out.println(graphFile.getName() + " : frequence negative");

			BigDecimal bdFreq = new BigDecimal(freq).setScale(3, RoundingMode.HALF_UP);
			frequencies.append(bdFreq.doubleValue() + " ");

			if (i < log.getFrequencies().size() - 1)
				frequencies.append(" ");
		}

		StringBuilder intensities = new StringBuilder();
		for (int i = 0; i < log.getIntensities().size(); i++) {
			Double inten = log.getIntensities().get(i);
			BigDecimal bdInten = new BigDecimal(inten).setScale(3, RoundingMode.HALF_UP);
			intensities.append(bdInten.doubleValue());

			if (i < log.getIntensities().size() - 1)
				intensities.append(" ");
		}

		if ("".contentEquals(frequencies) || "".contentEquals(intensities))
			System.out.print("");

		writer.write(
				"INSERT INTO gaussian_result (final_energies, frequencies, id_molecule, intensities, zero_point_energy)\n");
		writer.write("\tVALUES (" + finalEnergy + ", '" + frequencies + "', " + id + ", '" + intensities + "', '"
				+ log.getZeroPointEnergy() + "');\n\n");

	}

	private static void insertIMS2D(long id, File ims2dFile, File pictureFile) throws IOException {

		ArrayList<String> lines = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(ims2dFile));
		String line;

		while ((line = reader.readLine()) != null)
			lines.add(line);

		reader.close();

		int lineIndex = 0;

		ArrayList<AtomGeometry> atoms = new ArrayList<>();
		Triplet<Double, Double, Double> origin;
		Triplet<Double, Double, Double> vector1;
		Triplet<Double, Double, Double> vector2;
		ArrayList<Double> points = new ArrayList<>();

		line = lines.get(lineIndex);

		while (!"".equals(line)) {
			

			String[] sl = line.split(" ");

			String label = sl[0];
			double x = Double.parseDouble(sl[1]);
			double y = Double.parseDouble(sl[2]);
			double z = Double.parseDouble(sl[3]);

			atoms.add(new AtomGeometry(label, x, y, z));
			lineIndex++;
			line = lines.get(lineIndex);
		}

		lineIndex++;

		String[] originSplit = lines.get(lineIndex).split(" ");

		double xOrigin = Double.parseDouble(originSplit[1]);
		double yOrigin = Double.parseDouble(originSplit[2]);
		double zOrigin = Double.parseDouble(originSplit[3]);
		origin = new Triplet<>(xOrigin, yOrigin, zOrigin);

		lineIndex += 2;

		String[] vector1Split = lines.get(lineIndex).split(" ");

		double xVector1 = Double.parseDouble(vector1Split[1]);
		double yVector1 = Double.parseDouble(vector1Split[2]);
		double zVector1 = Double.parseDouble(vector1Split[3]);
		vector1 = new Triplet<>(xVector1, yVector1, zVector1);

		lineIndex++;

		String[] vector2Split = lines.get(lineIndex).split(" ");

		double xVector2 = Double.parseDouble(vector2Split[1]);
		double yVector2 = Double.parseDouble(vector2Split[2]);
		double zVector2 = Double.parseDouble(vector2Split[3]);
		vector2 = new Triplet<>(xVector2, yVector2, zVector2);

		int nbPointsX = Integer.parseInt(vector1Split[4]);
		int nbPointsY = Integer.parseInt(vector2Split[4]);
		
		lineIndex ++;
		
		
		
		while (lineIndex < lines.size()) {
			line = lines.get(lineIndex);
			if (!"".equals(line)) {
				points.add(Double.parseDouble(lines.get(lineIndex)));
			}
			lineIndex ++;
		}
		
		/*
		 * Inserting geometry
		 */
		
		//idAtom` bigint(20) NOT NULL,\n  `idBenzenoid` bigint(20) NOT NULL,\n  `x` float DEFAULT NULL,\n  `y` float DEFAULT NULL,\n  `z` float DEFAULT NULL,\n  `label` varchar(255)
	
		for (AtomGeometry atom : atoms) {
			
			double x = atom.getX();
			double y = atom.getY();
			double z = atom.getZ();
			
			String label = atom.getLabel();
			
			writer.write("INSERT INTO atom (`idAtom`, `idBenzenoid`, `x`, `y`, `z`, `label`) VALUES (" + 
						  indexAtom + ", " + id + ", " + x + ", " + y + ", " + z + ", '" + label + "');\n"); 
			
			indexAtom ++;
		}
		
		writer.write("\n");
		
		/*
		 * Inserting IMS2D1A
		 */
		
		//`idIms2d1a` `idBenzenoid` `vectorX` `vectorY` `nbPointsX` `nbPointsY` `originX` `originY` `originZ` `picture`
		
		String vectorX = xVector1 + " " + yVector1 + " " + zVector1;
		String vectorY = xVector2 + " " + yVector2 + " " + zVector2;
		
		
		
		writer.write("INSERT INTO ims2d_1a (`idIms2d1a`, `idBenzenoid`, `vectorX`, `vectorY`, `nbPointsX`, `nbPointsY`, `originX`, `originY`, `originZ`) VALUES (" + 
											 indexIms2d + ", " + id + ", '" + vectorX + "', '" + vectorY + "', " + nbPointsX + ", " + nbPointsY + ", " + xOrigin + ", " + yOrigin + ", " + zOrigin + ");\n\n");
		
		
		/*
		 * Inserting picture
		 */
		
		//`idPicture` `idBenzenoid` `idIms2d1a` `picture`
		
		String picture = PictureConverter.pngToString(pictureFile.getAbsolutePath());
		
		writer.write("INSERT INTO picture_ims2d_1a (`idPicture`, `idBenzenoid`, `idIms2d1a`, `picture`) VALUES (" + 
		                                    indexIms2d + ", " + id + ", " + indexIms2d + ", '" + picture + "');\n\n"); 
		
		/*
		 * Inserting points
		 */
		
		//`idPoint` `idIms2d1a` `numPoint` `value`
		
		for (int i = 0 ; i < points.size() ; i++) {
			double value = points.get(i);
			
			writer.write("INSERT INTO point_ims2d_1a (`idPoint`, `idIms2d1a`, `numPoint`, `value`) VALUES (" + 
			              indexPoint + ", " + indexIms2d + ", " + i + ", " + value + ");\n");
			
			indexPoint ++;
		}
		
		writer.write("\n");
		
		indexIms2d ++;
	}

	private static void treatMolecule(long id, File graphFile, File irFile, File nicsFile, File ims2dFile)
			throws IOException {

		
		
		String moleculeName = graphFile.getName().split(Pattern.quote("."))[0];
		writer.write("# " + moleculeName + "\n\n");

		Benzenoid molecule = GraphParser.parseUndirectedGraph(graphFile);

		insertBenzenoid(id, molecule);
		insertIRSpectra(id, irFile, graphFile);
		
		if (ims2dFile.exists()) {
			File pictureFile = new File(ims2dFile.getAbsolutePath().replace(".txt", ".png"));
			insertIMS2D(id, ims2dFile, pictureFile);
		}
	}

	private static void treatMolecules() throws IOException {

		File dir = new File(directory);
		File[] files = dir.listFiles();
		long id = 0;

		for (File molFile : files) {
			
			if (molFile.getName().endsWith(".graph_coord")) {

				System.out.println("treating " + molFile.getName().replace(".graph_coord", ""));
				
				File irFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".log"));
				File nicsFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".nics"));
				File ims2dFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".ims2d"));

				treatMolecule(id, molFile, irFile, nicsFile, ims2dFile);

				id++;
			}
			
			
			
		}
	}

	public static void main(String[] args) throws IOException {

		initialize();
		treatMolecules();

//		File molFile = new File("/home/adrien/Documents/old_log_files/5_hexagons0.graph_coord");
//		File ims2dFile = new File("/home/adrien/Documents/old_log_files/5_hexagons0.ims2d");
//		File pictureFile = new File("/home/adrien/Documents/old_log_files/5_hexagons0_ims2d_1a.png");
//		
//		Molecule molecule = GraphParser.parseUndirectedGraph(molFile);
//		
//		insertIMS2D(0, ims2dFile, pictureFile);
		
		writer.close();
	}
}
