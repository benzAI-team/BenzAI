package database.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import http.Post;
import benzenoid.Benzenoid;
import parsers.GraphParser;
import spectrums.ResultLogFile;

public class BenzenoidEntry {

	private final int idMolecule;
	private final String moleculeLabel;
	private final int nbHexagons;
	private final int nbCarbons;
	private final int nbHydrogens;
	private final double irregularity;


	/*
	 * Constructor
	 */

	public BenzenoidEntry(int idMolecule, String moleculeLabel, int nbHexagons, int nbCarbons, int nbHydrogens, double irregularity) {

		this.idMolecule = idMolecule;
		this.moleculeLabel = moleculeLabel;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
	}

	/*
	 * Getters
	 */

	public int getIdMolecule() {
		return idMolecule;
	}

	public String getMoleculeLabel() {
		return moleculeLabel;
	}

	public int getNbHexagons() {
		return nbHexagons;
	}

	public int getNbHydrogens() {
		return nbHydrogens;
	}

	public double getIrregularity() {
		return irregularity;
	}


	/*
	 * Class methods
	 */

	@SuppressWarnings("rawtypes")
	public static BenzenoidEntry buildQueryContent(Map result) {

		int idMolecule = (int) ((double) result.get("idBenzenoid"));
		//~ String label = (String) result.get("inchie");
		String label = (String) result.get("label");
		int nbHexagons = (int) ((double) result.get("nbHexagons"));
		int nbCarbons = (int) ((double) result.get("nbCarbons"));
		int nbHydrogens = (int) ((double) result.get("nbHydrogens"));
		double irregularity = (double) result.get("irregularity");

		// Récupérer le nom
		String service = "find_benzenoids/";
		String json = "{\"idBenzenoid\": \"= " + idMolecule + "\"}";
		List<Map> results = null;
		try {
			results = Post.post(service, json);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Map map : results) {
			label = (String) map.get("label");
		}

		return new BenzenoidEntry(idMolecule, label, nbHexagons, nbCarbons, nbHydrogens, irregularity);
	}

	public static BenzenoidEntry buildQueryContent(ArrayList<String> result) {

		int idMolecule = -1;
		String moleculeLabel = "";
		int nbHexagons = -1;
		int nbCarbons = -1;
		int nbHydrogens = -1;
		double irregularity = -1.0;


		for (String line : result) {

			String[] splittedLine = line.split(Pattern.quote(" = "));

			if ("id_molecule".equals(splittedLine[0]))
				idMolecule = Integer.parseInt(splittedLine[1]);

			else if ("molecule_label".equals(splittedLine[0]))
				moleculeLabel = splittedLine[1];

			else if ("nb_hexagons".equals(splittedLine[0]))
				nbHexagons = Integer.parseInt(splittedLine[1]);

			else if ("nb_carbons".equals(splittedLine[0]))
				nbCarbons = Integer.parseInt(splittedLine[1]);

			else if ("nb_hydrogens".equals(splittedLine[0]))
				nbHydrogens = Integer.parseInt(splittedLine[1]);

			else if ("irregularity".equals(splittedLine[0]))
				irregularity = Double.parseDouble(splittedLine[1]);
		}

		return new BenzenoidEntry(idMolecule, moleculeLabel, nbHexagons, nbCarbons, nbHydrogens, irregularity);
	}

	public Benzenoid buildMolecule() throws IOException {
		return GraphParser.parseBenzenoidCode(moleculeLabel);
	}

	public ResultLogFile buildResultLogFile() {
		return null; //new ResultLogFile("unknown.log", frequencies, intensities, finalEnergies, zeroPointEnergy);
	}
}
