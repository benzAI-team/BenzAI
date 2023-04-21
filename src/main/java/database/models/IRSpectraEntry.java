package database.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import http.Post;
import molecules.Molecule;
import parsers.GraphParser;
import spectrums.ResultLogFile;

public class IRSpectraEntry {

	private final int idMolecule;
	private final String moleculeName;
	private final int nbHexagons;
	private final int nbCarbons;
	private final int nbHydrogens;

	private final double irregularity;
	private final int idGaussianResult;
	private final ArrayList<Double> finalEnergies;
	private final ArrayList<Double> frequencies;
	private final ArrayList<Double> intensities;
	private final double zeroPointEnergy;

	private String amesFormat;

	/*
	 * Constructor
	 */

	public IRSpectraEntry(int idMolecule, String moleculeName, int nbHexagons, int nbCarbons, int nbHydrogens,
			double irregularity, int idGaussianResult, ArrayList<Double> finalEnergies, ArrayList<Double> frequencies,
			ArrayList<Double> intensities, double zeroPointEnergy, String amesFormat) {

		this.idMolecule = idMolecule;
		this.moleculeName = moleculeName;
		this.nbHexagons = nbHexagons;
		this.nbCarbons = nbCarbons;
		this.nbHydrogens = nbHydrogens;
		this.irregularity = irregularity;
		this.idGaussianResult = idGaussianResult;
		this.finalEnergies = finalEnergies;
		this.frequencies = frequencies;
		this.intensities = intensities;
		this.zeroPointEnergy = zeroPointEnergy;
		this.amesFormat = amesFormat;
	}

	/*
	 * Getters
	 */

	public int getIdMolecule() {
		return idMolecule;
	}

	public String getMoleculeName() {
		return moleculeName;
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

	public int getIdGaussianResult() {
		return idGaussianResult;
	}

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

	public int getNbCarbons() {
		return nbCarbons;
	}

	public String getAmesFormat() {return amesFormat;}

	/*
	 * Class methods
	 */

	@SuppressWarnings("rawtypes")
	public static IRSpectraEntry buildQueryContent(Map result) {

		int idMolecule = (int) ((double) result.get("idBenzenoid"));
		// String name = (String) result.get("inchie");
		String name = "";
		int nbHexagons = (int) ((double) result.get("nbHexagons"));
		int nbCarbons = (int) ((double) result.get("nbCarbons"));
		int nbHydrogens = (int) ((double) result.get("nbHydrogens"));
		double irregularity = (double) result.get("irregularity");

		int idSpectrum = (int) ((double) result.get("idSpectra"));
		String frequenciesString = (String) result.get("frequencies");
		String intensitiesString = (String) result.get("intensities");
		double finalEnergy = (double) result.get("finalEnergy");
		double zeroPointEnergy = (double) result.get("zeroPointEnergy");
		String amesFormat = (String) result.get("amesFormat");

		// Récupérer le nom
		String url = "https://benzenoids.lis-lab.fr/find_name/";
		String json = "{\"idBenzenoid\":" + idMolecule + "}";
		List<Map> results = null;
		try {
			results = Post.post(url, json);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Map map : results) {
			name = (String) map.get("name");
		}

		ArrayList<Double> frequencies = new ArrayList<>();

		if (!frequenciesString.equals("")) {
			String[] splittedFrequencies = frequenciesString.split("\\s+");
			for (String frequency : splittedFrequencies)
				frequencies.add(Double.parseDouble(frequency));
		}

		ArrayList<Double> intensities = new ArrayList<>();

		if (!intensitiesString.equals("")) {
			String[] splittedIntensities = intensitiesString.split("\\s+");
			for (String intensity : splittedIntensities)
				intensities.add(Double.parseDouble(intensity));
		}

		// String[] splittedEnergies = finalEnergiesString.split("\\s+");
		ArrayList<Double> finalEnergies = new ArrayList<>();
		finalEnergies.add(finalEnergy);

//		for (String energy : splittedEnergies)
//			finalEnergies.add(Double.parseDouble(energy));

		return new IRSpectraEntry(idMolecule, name, nbHexagons, nbCarbons, nbHydrogens, irregularity, idSpectrum,
				finalEnergies, frequencies, intensities, zeroPointEnergy, amesFormat);
	}

	public static IRSpectraEntry buildQueryContent(ArrayList<String> result) {

		int idMolecule = -1;
		String moleculeName = "";
		int nbHexagons = -1;
		int nbCarbons = -1;
		int nbHydrogens = -1;
		double irregularity = -1.0;

		int idGaussianResult = -1;
		ArrayList<Double> finalEnergies = new ArrayList<>();
		ArrayList<Double> frequencies = new ArrayList<>();
		ArrayList<Double> intensities = new ArrayList<>();
		double zeroPointEnergy = -1.0;

		String amesFormat = "";

		for (String line : result) {

			String[] splittedLine = line.split(Pattern.quote(" = "));

			if (splittedLine[0].equals("id_molecule"))
				idMolecule = Integer.parseInt(splittedLine[1]);

			else if (splittedLine[0].equals("molecule_name"))
				moleculeName = splittedLine[1];

			else if (splittedLine[0].equals("nb_hexagons"))
				nbHexagons = Integer.parseInt(splittedLine[1]);

			else if (splittedLine[0].equals("nb_carbons"))
				nbCarbons = Integer.parseInt(splittedLine[1]);

			else if (splittedLine[0].equals("nb_hydrogens"))
				nbHydrogens = Integer.parseInt(splittedLine[1]);

			else if (splittedLine[0].equals("irregularity"))
				irregularity = Double.parseDouble(splittedLine[1]);

			else if (splittedLine[0].equals("id_gaussian_result"))
				idGaussianResult = Integer.parseInt(splittedLine[1]);

			else if (splittedLine[0].equals("final_energies")) {

				String[] splittedResult = splittedLine[1].split("\\s+");

				for (String value : splittedResult)
					finalEnergies.add(Double.parseDouble(value));
			}

			else if (splittedLine[0].equals("frequencies")) {

				String[] splittedResult = splittedLine[1].split("\\s+");

				for (String value : splittedResult)
					frequencies.add(Double.parseDouble(value));
			}

			else if (splittedLine[0].equals("intensities")) {

				String[] splittedResult = splittedLine[1].split("\\s+");

				for (String value : splittedResult)
					intensities.add(Double.parseDouble(value));
			}

			else if (splittedLine[0].equals("zero_point_energy"))
				zeroPointEnergy = Double.parseDouble(splittedLine[1]);

			else if (splittedLine[0].equals("amesFormat"))
				amesFormat = splittedLine[1];
		}

		return new IRSpectraEntry(idMolecule, moleculeName, nbHexagons, nbCarbons, nbHydrogens, irregularity,
				idGaussianResult, finalEnergies, frequencies, intensities, zeroPointEnergy, amesFormat);
	}

	public Molecule buildMolecule() throws IOException {
		return GraphParser.parseBenzenoidCode(moleculeName);
	}

	public ResultLogFile buildResultLogFile() {
		return new ResultLogFile("unknown.log", frequencies, intensities, finalEnergies, zeroPointEnergy);
	}
}
