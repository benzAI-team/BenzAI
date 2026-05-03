package database.models;

import http.Post;
import spectrums.ResultLogFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IRSpectraEntry extends BenzenoidEntry {

	private final ArrayList<Double> finalEnergies;
	private final ArrayList<Double> frequencies;
	private final ArrayList<Double> intensities;
	private final double zeroPointEnergy;
	private final String amesFormat;

	/*
	 * Constructor
	 */

	public IRSpectraEntry(int idMolecule, String moleculeLabel, int nbHexagons, int nbCarbons, int nbHydrogens,
			double irregularity, String inchi, String graphFile, String smiles, String selfies, ArrayList<Double> finalEnergies, ArrayList<Double> frequencies,
			ArrayList<Double> intensities, double zeroPointEnergy, String amesFormat) {
    super(idMolecule, moleculeLabel, nbHexagons, nbCarbons, nbHydrogens, irregularity, inchi, graphFile, smiles, selfies);
		this.finalEnergies = finalEnergies;
		this.frequencies = frequencies;
		this.intensities = intensities;
		this.zeroPointEnergy = zeroPointEnergy;
		this.amesFormat = amesFormat;
	}

	/*
	 * Getters
	 */


	public ArrayList<Double> getFinalEnergies() {
		return finalEnergies;
	}

	public ArrayList<Double> getFrequencies() {
		return frequencies;
	}

	public ArrayList<Double> getIntensities() {
		return intensities;
	}

	public double getZeroPointEnergy() {
		return zeroPointEnergy;
	}


	public String getAmesFormat() {return amesFormat;}

	/*
	 * Class methods
	 */

	@SuppressWarnings("rawtypes")
	public static IRSpectraEntry buildQueryContent(Map result) {

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

		String frequenciesString = (String) result.get("frequencies");
		String intensitiesString = (String) result.get("intensities");
		double finalEnergy = (double) result.get("finalEnergy");
		double zeroPointEnergy = (double) result.get("zeroPointEnergy");
		String amesFormat = (String) result.get("amesFormat");

		// Récupérer le nom
		String service = "find_ir/";
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

		ArrayList<Double> frequencies = new ArrayList<>();

		if (!"".equals(frequenciesString)) {
			String[] splittedFrequencies = frequenciesString.split("\\s+");
			for (String frequency : splittedFrequencies)
				frequencies.add(Double.parseDouble(frequency));
		}

		ArrayList<Double> intensities = new ArrayList<>();

		if (!"".equals(intensitiesString)) {
			String[] splittedIntensities = intensitiesString.split("\\s+");
			for (String intensity : splittedIntensities)
				intensities.add(Double.parseDouble(intensity));
		}

		ArrayList<Double> finalEnergies = new ArrayList<>();
		finalEnergies.add(finalEnergy);

		return new IRSpectraEntry(idMolecule, label, nbHexagons, nbCarbons, nbHydrogens, irregularity, inchi, graphFile, smiles, selfies, finalEnergies, frequencies, intensities, zeroPointEnergy, amesFormat);
	}


	public ResultLogFile buildResultLogFile() {
		return new ResultLogFile("unknown.log", frequencies, intensities, finalEnergies, zeroPointEnergy);
	}
}
