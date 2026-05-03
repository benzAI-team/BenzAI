package database.models;

import benzenoid.Benzenoid;
import http.Post;
import parsers.GraphParser;
import spectrums.ResultLogFile;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BenzenoidEntry {

	private final int idMolecule;
	private final String moleculeLabel;
	private final String inchi;
	private final String smiles;
	private final String selfies;
	private final int nbHexagons;
	private final int nbCarbons;
	private final int nbHydrogens;
	private final double irregularity;
	private final String graphFile;


	/*
	 * Constructor
	 */

	public BenzenoidEntry(int idMolecule, String moleculeLabel, int nbHexagons, int nbCarbons, int nbHydrogens, double irregularity, String inchi, String graphFile, String smiles, String selfies) {

		this.idMolecule = idMolecule;
		this.moleculeLabel = moleculeLabel;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
		this.inchi = inchi;
		this.smiles = smiles;
		this.selfies = selfies;
		this.graphFile = graphFile;
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

    public String getSmiles() {
		return smiles;
	}

    public String getSelfies() {
		return selfies;
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
		String smiles = (String) result.get("smiles");
		String selfies = (String) result.get("selfies");
		String label = (String) result.get("label");
		int nbHexagons = (int) ((double) result.get("nbHexagons"));
		int nbCarbons = (int) ((double) result.get("nbCarbons"));
		int nbHydrogens = (int) ((double) result.get("nbHydrogens"));
		double irregularity = (double) result.get("irregularity");
		String graphFile = (String) result.get("graphFile");

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

		return new BenzenoidEntry(idMolecule, label, nbHexagons, nbCarbons, nbHydrogens, irregularity, inchi, graphFile, smiles, selfies);
	}


	public Benzenoid buildMolecule() throws IOException {
    try {
      FileWriter f = new FileWriter("tmp.graph_coord");
      f.write(this.graphFile);
      f.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Benzenoid b = GraphParser.parseUndirectedGraph("tmp.graph_coord", null, false);
    
    b.setInchi (this.inchi);
    b.setSmiles (this.smiles);
    b.setSelfies (this.selfies);
    b.setBenzdbId (this.idMolecule);
    return b;
	}

	public ResultLogFile buildResultLogFile() {
		return null; //new ResultLogFile("unknown.log", frequencies, intensities, finalEnergies, zeroPointEnergy);
	}
}
