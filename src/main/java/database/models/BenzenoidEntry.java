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
	private final String inchi;
	private final int nbHexagons;
	private final int nbCarbons;
	private final int nbHydrogens;
	private final double irregularity;


	/*
	 * Constructor
	 */

	public BenzenoidEntry(int idMolecule, String moleculeLabel, int nbHexagons, int nbCarbons, int nbHydrogens, double irregularity, String inchi) {

		this.idMolecule = idMolecule;
		this.moleculeLabel = moleculeLabel;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
    this.inchi = inchi;
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
	
  public String getInchi() {
		return inchi;
	}

	public int getNbHexagons() {
		return nbHexagons;
	}

	public int getNbCarbons() {
		return nbCarbons;
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
		String inchi = (String) result.get("inchi");
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

		return new BenzenoidEntry(idMolecule, label, nbHexagons, nbCarbons, nbHydrogens, irregularity, inchi);
	}


	public Benzenoid buildMolecule() throws IOException {
		Benzenoid b = GraphParser.parseBenzenoidCode(moleculeLabel);
    b.setInchi (this.inchi);
    return b;
	}

	public ResultLogFile buildResultLogFile() {
		return null; //new ResultLogFile("unknown.log", frequencies, intensities, finalEnergies, zeroPointEnergy);
	}
}
