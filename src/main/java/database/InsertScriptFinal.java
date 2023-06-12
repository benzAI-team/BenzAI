package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

import classifier.Irregularity;
import benzenoid.Benzenoid;
import parsers.GraphParser;
import spectrums.ResultLogFile;
import spectrums.SpectrumsComputer;
import utils.Triplet;
import utils.Utils;

public enum InsertScriptFinal {
    ;

    private static int idBenzenoid;
	private static int idName;
	private static int idSpectra;
	private static int idIMS2D1A;
	private static int nbNics;
	private static BufferedWriter out;
	private static BufferedWriter updateNICS;
	private static BufferedWriter updateClar;
	private static BufferedWriter missingIms;

	private static BufferedWriter updateAmes;

	public static String quote(String str) {
		return "\"" + str + "\"";
	}
	
	private static String getInchi(File inchiFile) throws IOException{
		
		BufferedReader r = new BufferedReader(new FileReader(inchiFile));
		String line = r.readLine().replace("InChI=", "");
		r.close();
		
		return line;
	}
	
	public static String getGeometry(File comFile) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(comFile));
		String line;
		StringBuilder geometry = new StringBuilder();
		boolean geo = false;
		
		while((line = reader.readLine()) != null) {
			if (line.startsWith("0 1") || line.startsWith("0 2"))
				geo = true;
			
			else {	
				if (geo && "".equals(line))
					geo = false;
			
				else if (geo) {
					String [] split = Utils.splitBySeparators(line);
					geometry.append(split[1] + "_" + split[2] + "_" + split[3] + "_" + split[4] + " ");
				}
			}
		}
		
		reader.close();
		return geometry.toString();
	}
	
	public static String fileToString(File file) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		
		while((line = reader.readLine()) != null) {
			builder.append(line + "\n");
		}
		reader.close();
		return builder.toString();
	}
	
	public static String nicsFileToString(File nicsFile) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(nicsFile));
		String line;
		while((line = reader.readLine()) != null) {
			builder.append(line + " ");
		}
		
		reader.close();
		return builder.toString();
	}
	
	public static void insertIMS2D1A(File ims2d1aFile, File mapFile) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(ims2d1aFile));
		String line;
		
		while((line = reader.readLine())!= null)
			if ("".equals(line))
				break;
		
		line = reader.readLine();
		String [] splitOrigin = Utils.splitBySeparators(line);
		Triplet<Double, Double, Double> origin = new Triplet<>(Double.parseDouble(splitOrigin[1]), Double.parseDouble(splitOrigin[2]), Double.parseDouble(splitOrigin[3]));
		String originStr = origin.getX() + " " + origin.getY() + " " + origin.getZ();
		
		line = reader.readLine();
		line = reader.readLine();

		String [] splitv1 = Utils.splitBySeparators(line);
		Triplet<Double, Double, Double> v1 = new Triplet<>(Double.parseDouble(splitv1[1]), Double.parseDouble(splitv1[2]), Double.parseDouble(splitv1[3]));
		
		line = reader.readLine();

		String [] splitv2 = Utils.splitBySeparators(line);
		Triplet<Double, Double, Double> v2 = new Triplet<>(Double.parseDouble(splitv2[1]), Double.parseDouble(splitv2[2]), Double.parseDouble(splitv2[3]));
		
		String v1Str = v1.getX() + " " + v1.getY() + " " + v1.getZ();
		String v2Str = v2.getX() + " " + v2.getY() + " " + v2.getZ();
		
		line = reader.readLine();
		ArrayList<ArrayList<Double>> points = new ArrayList<>();
		ArrayList<Double> pointsLine = new ArrayList<>();
		
		int nbPointsX = 0;
		int nbPointsY = 0;
		boolean first = true;
		
		while((line = reader.readLine()) != null) {
			
			if ("".equals(line)) {
				first = false;
				points.add(pointsLine);
				pointsLine = new ArrayList<>();
				nbPointsY ++;
			}
			
			else {
				if (first)
					nbPointsX ++;
				pointsLine.add(Double.parseDouble(line));
			}
			
		}
		
		StringBuilder pointsStr = new StringBuilder();
		for (ArrayList<Double> row : points) {
			for (Double d : row) {
				pointsStr.append(d + " ");
			}
		}
		
		reader.close();
		
		String picture = PictureConverter.pngToString(mapFile.getAbsolutePath());

        String insert = "INSERT INTO ims2d_1a (idIms2d1a, idBenzenoid, vectorX, vectorY, nbPointsX, nbPointsY, origin, points, picture) VALUES (\n" +
                idIMS2D1A + ", " + idBenzenoid + ", " + quote(v1Str) + ", " + quote(v2Str) + ", " + nbPointsX + ", " + nbPointsY + ", " + quote(originStr) + ", " +
                quote(pointsStr.toString()) + ", " + quote(picture) +
                ");";
		
		out.write(insert);
		
		idIMS2D1A ++;
	}
	
	// Fill the tables `benzenoid` and `name`
	public static void insertBenzenoid(File molFile, File inchiFile) throws IOException {
		
		Benzenoid molecule = GraphParser.parseUndirectedGraph(molFile);
		
		
		BigDecimal irregBD;
		Irregularity irreg = Utils.computeParameterOfIrregularity(molecule);
		
		if (irreg == null)
			irregBD = BigDecimal.valueOf(-1.0).setScale(3, RoundingMode.HALF_UP);
		else
			irregBD = BigDecimal.valueOf(irreg.getXI()).setScale(3, RoundingMode.HALF_UP);
		
		int nbHexagons = molecule.getNbHexagons();
		int nbCarbons = molecule.getNbNodes();
		int nbHydrogens = molecule.getNbHydrogens();
		String inchie = getInchi(inchiFile);
		double irregularity = irregBD.doubleValue();
		String graphFileContent = fileToString(molFile);
		String nics = "";
		
		/*
		 * Writing NICS
		 */
		
		File nicsFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".nics"));
		if (nicsFile.exists()) {
			nics = nicsFileToString(nicsFile);

            updateNICS.write("UPDATE benzenoid SET nics = " + quote(nics).replace("  ", " ") + " WHERE idBenzenoid = " + idBenzenoid + ";" + "\n");
			
		}
		
		if (!"".equals(nics))
			nbNics ++;
		
		/*
		 * Writing clar cover
		 */
		
		File clarFile = new File(molFile.getAbsolutePath().replace(".graph_coord", "_clar.png"));
		if (clarFile.exists()) {
			
			String picture = PictureConverter.pngToString(clarFile.getAbsolutePath());
            updateClar.write("UPDATE benzenoid SET clar_cover = " + quote(picture) + " WHERE idBenzenoid = " + idBenzenoid + ";\n" + "\n");

		}
		
		
		File comFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".com"));
		
		String geometry = getGeometry(comFile);
		
		ArrayList<String> names = molecule.getNames();

        String insertBenzenoid = "INSERT INTO benzenoid (idBenzenoid, nbHexagons, nbCarbons, nbHydrogens, inchie, irregularity, graphFile, nics, geometry) VALUES (\n" +
                idBenzenoid + ", " + nbHexagons + ", " + nbCarbons + ", " + nbHydrogens + ", " + quote(inchie) + ", " + irregularity + ", " +
                quote(graphFileContent) + ", " + quote(nics) + ", " + quote(geometry) + "\n);";
		
		StringBuilder insertNames = new StringBuilder();
		
		for (String name : names) {
			insertNames.append("INSERT INTO name (idName, idBenzenoid, name) VALUES (" + idName + ", " + idBenzenoid + ", " + quote(name) + ");\n");
			idName ++;
		}
		
		out.write(insertBenzenoid + "\n");
		out.write(insertNames + "\n");
		
		
	}

	private static String buildAmesFormat(File molFile, ResultLogFile log, File comFile) throws IOException {

		if (!molFile.exists() || !comFile.exists())
			return "unknown";

		Benzenoid molecule = GraphParser.parseUndirectedGraph(molFile);
		Optional<Irregularity> irregularity = molecule.getIrregularity();
		String geometry = getGeometry(comFile);
		String [] geometries = geometry.split(" ");
		ArrayList<Double> frequencies = log.getFrequencies();
		ArrayList<Double> intensities = log.getIntensities();


		StringBuilder builder = new StringBuilder();

		String test = "<specie uid=\\\"" + idBenzenoid + "\\\">";
		
		builder.append("<specie uid=\\\"" + idBenzenoid + "\\\">");

		builder.append("<comments>");
		builder.append("<comment># b3lyp/6-31g opt freq</comment>");
		builder.append("</comments>");

		double finalEnergy = log.getFinalEnergy().get(log.getFinalEnergy().size() - 1);

		builder.append("<weight>" + log.getMolecularMass() + "</weight>");
		builder.append("<total_e>" + finalEnergy + "</total_e>");
		builder.append("<vib_e>" + log.getZeroPointEnergy() + "</vib_e>");

		builder.append("<formula>C" + molecule.getNbNodes() + "H" + molecule.getNbHydrogens() + "</formula>");
		builder.append("<charge>0</charge>");
		builder.append("<method>B3LYP</method>");

		if (irregularity.isPresent()) {
			Irregularity irregularityData = irregularity.get();
			builder.append("<n_solo>" + irregularityData.getGroup(0) + "</n_solo>");
			builder.append("<n_duo>" + irregularityData.getGroup(1) + "</n_duo>");
			builder.append("<n_trio>" + irregularityData.getGroup(2) + "</n_trio>");
			builder.append("<n_quartet>" + irregularityData.getGroup(3) + "</n_quartet>");
			builder.append("<n_quintet>" + 0 + "</n_quintet>");
		}

		else {
			builder.append("<n_solo>" + 0 + "</n_solo>");
			builder.append("<n_duo>" + 0 + "</n_duo>");
			builder.append("<n_trio>" + 0 + "</n_trio>");
			builder.append("<n_quartet>" + 0 + "</n_quartet>");
			builder.append("<n_quintet>" + 0 + "</n_quintet>");
		}

		builder.append("<geometry>");
		int position = 1;

		for (String geometryStr : geometries) {
			String [] split = geometryStr.split(Pattern.quote("_"));
			String atom = split[0];
			String x = split[1];
			String y = split[2];
			String z = split[3];

			String atomType;
			if ("C".equals(atom))
				atomType = "6";
			else
				atomType = "1";

			builder.append("<atom>");
			builder.append("<position>" + position + "</position>");
			builder.append("<x>" + x + "</x>");
			builder.append("<y>" + y + "</y>");
			builder.append("<z>" + z + "</z>");
			builder.append("<type>" + atomType + "</type>");
			builder.append("</atom>");

			position ++;
		}

		builder.append("</geometry>");

		builder.append("<transitions>");
		for (int i = 0 ; i < frequencies.size() ; i++) {
			Double frequency = frequencies.get(i);
			Double intensity = intensities.get(i);

			builder.append("<mode>");
			builder.append("<frequency>" + frequency + "</frequency>");
			builder.append("<intensity>" + intensity + "</intensity>");
			builder.append("<symmetry>unknown</symmetry>");
			builder.append("</mode>");
		}

		builder.append("</transitions>");

		builder.append("</specie>");

		return builder.toString();
	}



	//fill ir_spectra table
	public static void insertIRSpectra(File molFile, File comFile) throws IOException {
		File logFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".log"));
		
		ResultLogFile log = SpectrumsComputer.parseLogFile(logFile.getAbsolutePath());
		
		StringBuilder frequencies = new StringBuilder();
		for (Double frequency : log.getFrequencies())
			frequencies.append(frequency + " ");
		
		StringBuilder intensities = new StringBuilder();
		for (Double intensity : log.getIntensities())
			intensities.append(intensity + " ");
		
		double zpe = log.getZeroPointEnergy();
		
		double finalEnergy = log.getFinalEnergy().get(log.getFinalEnergy().size() - 1);
		
		StringBuilder insert = new StringBuilder();

		String amesFormat = buildAmesFormat(molFile, log, comFile);

		updateAmes.write("UPDATE ir_spectra SET amesFormat = " + quote(amesFormat) + " WHERE idSpectra = " + idSpectra + ";\n");

		insert.append("INSERT INTO ir_spectra (idSpectra, idBenzenoid, frequencies, intensities, zeroPointEnergy, finalEnergy) VALUES (\n");
		insert.append(idSpectra + ", " + idBenzenoid + ", " + quote(frequencies.toString()) + ", " + quote(intensities.toString()) + ", " + zpe + ", " + finalEnergy);
		insert.append(");\n");



		out.write(insert + "\n");
		
		idSpectra ++;
	}
	
	public static String insertNICS(File molFile, File nicsFile) throws IOException{
		
		return "unknown";
	}
	
	public static void main(String [] args) throws IOException {
		
		File dir = new File("/home/adrien/Documents/old_log_files");
		out = new BufferedWriter(new FileWriter(new File("/home/adrien/Documents/old_log_files/new_insert.sql")));
		updateNICS = new BufferedWriter(new FileWriter(new File("/home/adrien/Documents/old_log_files/update_nics.sql")));
		updateClar = new BufferedWriter(new FileWriter(new File("/home/adrien/Documents/old_log_files/update_clar.sql")));
		missingIms = new BufferedWriter(new FileWriter(new File("/home/adrien/Documents/old_log_files/cp_missing_ims.sh")));
		updateAmes = new BufferedWriter(new FileWriter(new File("/home/adrien/Documents/old_log_files/update_ames_format.sql")));

		File [] files = dir.listFiles();
		
		idBenzenoid = 0;
		idName = 0;
		idIMS2D1A = 0;
		
		nbNics = 0;
		
		boolean first = true;
		
		missingIms.write("mkdir missing_ims\n");
		
		for (File molFile : files) {
			if (molFile.getName().endsWith(".graph_coord")) {
				//if (first) {
				System.out.println("Treating " + molFile.getName());
				
				File inchiFile = new File(molFile.getAbsolutePath().replace(".graph_coord", "_coord.cml.inchi"));
				File comFile = new File(molFile.getAbsolutePath().replace(".graph_coord", ".com"));

				insertBenzenoid(molFile, inchiFile);
				insertIRSpectra(molFile, comFile);
				
				File ims2dTextFile = new File(molFile.getAbsolutePath().replace(".graph_coord", "_ims2d1a.txt"));
				File ims2dMapFile = new File(molFile.getAbsolutePath().replace(".graph_coord", "_ims2d1a.png"));
				
				if (ims2dTextFile.exists() && ims2dMapFile.exists())
					insertIMS2D1A(ims2dTextFile, ims2dMapFile);
				else {
					
					if (molFile.getName().contains("hexagons")) {
						String nbHexagons = molFile.getName().split(Pattern.quote("_"))[0];
						missingIms.write("cp " + nbHexagons + "_hexagons/" + molFile.getName().replace(".graph_coord", ".com") + " missing_ims/\n");
					}
					
					else {
						missingIms.write("cp 1_4_hexagons/" + molFile.getName().replace(".graph_coord", ".com") + " missing_ims/\n");
					}
					
				}
				
				idBenzenoid ++;
				//}
				
				//first = false;
			}
		}
		
		System.out.println("Terminated, " + idIMS2D1A + "ims maps");
		System.out.println(nbNics + " nics");
		
		out.close();
		updateNICS.close();
		updateClar.close();
		missingIms.close();
		updateAmes.close();
	}

	
}
