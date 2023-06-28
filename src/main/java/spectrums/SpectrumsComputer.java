package spectrums;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

import classifier.PAHClass;
import benzenoid.Benzenoid;
import utils.Couple;

public enum SpectrumsComputer {
    ;

    /*
	 * Parameters
	 */

	public static ResultLogFile parseLogFile(String inputFilename) throws IOException {

		ArrayList<Double> frequencies = new ArrayList<Double>();
		ArrayList<Double> intensities = new ArrayList<Double>();
		ArrayList<Double> finalEnergies = new ArrayList<Double>();
		double molecularWeight = -1;
		double zeroPointEnergy = -1;

		BufferedReader r = new BufferedReader(new FileReader(new File(inputFilename)));

		String line = null;

		while ((line = r.readLine()) != null) {

			String[] splittedLine = line.split("\\s+");
			int size = splittedLine.length;

			if (size > 1 && "Frequencies".equals(splittedLine[1])) {

				frequencies.add(Double.parseDouble(splittedLine[3]));
				frequencies.add(Double.parseDouble(splittedLine[4]));
				frequencies.add(Double.parseDouble(splittedLine[5]));
			}

			else if (size > 2 && "IR".equals(splittedLine[1]) && "Inten".equals(splittedLine[2])) {

				intensities.add(Double.parseDouble(splittedLine[4]));
				intensities.add(Double.parseDouble(splittedLine[5]));
				intensities.add(Double.parseDouble(splittedLine[6]));
			}

			else if (size > 2 && "SCF".equals(splittedLine[1]) && "Done:".equals(splittedLine[2])) {

				finalEnergies.add(Double.parseDouble(splittedLine[5]));
			}

			else if (size > 2 && "Zero-point".equals(splittedLine[1]) && "correction=".equals(splittedLine[2])) {

				zeroPointEnergy = Double.parseDouble(splittedLine[3]);
			}

			//7_hexagons194.log: Molecular mass:   352.12520 amu.
			if (line.contains("Molecular mass")) {
				molecularWeight = Double.parseDouble(splittedLine[3]);
			}
		}

		r.close();

		return new ResultLogFile(inputFilename, frequencies, intensities, finalEnergies, zeroPointEnergy, molecularWeight);
	}

	public static double g(ResultLogFile result, int i, int V, int FWHM) {

		double Vi = result.getFrequency(i);
		double Ii = result.getIntensity(i);
		double alpha = 1.0 / (2.0 * FWHM * FWHM);
		// double alpha = 1.0 / (double)(2.0 * FWHM);

		double g = Ii * Math.exp((-1.0 * alpha) * (((double) V - Vi) * ((double) V - Vi)));

		return g;
	}

	public static double spectre(ResultLogFile result, int V, Parameter parameter) {

		double sum = 0.0;

		for (int i = 0; i < result.getNbFrequencies(); i++) {

			double gi = g(result, i, V, parameter.getFWHM());
			sum += gi;
		}

		return sum;
	}

	public static ArrayList<Double> spectres(ResultLogFile result, Parameter parameter) {

		ArrayList<Double> spectres = new ArrayList<Double>();

		for (int V = parameter.getVMin(); V <= parameter.getVMax(); V += parameter.getStep()) {

			double spectre = spectre(result, V, parameter);
			spectres.add(spectre);
		}

		return spectres;
	}

	public static void writeResults(ResultLogFile result, ArrayList<Double> spectres) throws IOException {

		String outputFilename = result.getFilenameWithoutExtension() + ".spc";
		BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputFilename)));

		int iMax = -1;
		int iMin = -1;

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double avg = 0;

		for (int i = 0; i < spectres.size(); i++) {
			double spectre = spectres.get(i);
			w.write("spectre(" + (600 + i) + ") = " + spectre + "\n");

			if (spectre < min) {
				min = spectre;
				iMin = 600 + i;
			}

			if (spectre > max) {
				max = spectre;
				iMax = 600 + i;
			}

			avg += spectre;
		}

		avg = avg / (double) spectres.size();

		w.write("\n");

		w.write("min : spectre(" + iMin + ") = " + min + "\n");
		w.write("max : spectre(" + iMax + ") = " + max + "\n");
		w.write("avg = " + avg + "\n");

		w.close();
	}

	public static double hartreeToJoules(double hartreesValue) {
		double factor = 4.35974434 * Math.pow(10, -18);
		double joulesValue = hartreesValue * factor;
		return joulesValue;
	}

	public static double Ni(double Ei, double Eref, Parameter parameter) {

		// double factor1 = (parameter.getN() / parameter.getZT()) * parameter.getGi();
		double factor1 = parameter.getN();

		double EiJoules = hartreeToJoules(Ei);
		double ErefJoules = hartreeToJoules(Eref);

		double factor2 = Math.exp((-1 * (EiJoules - ErefJoules)) / (parameter.getKb() * parameter.getT()));

		double Ni = factor1 * factor2;

		return Ni;
	}

	public static void writeSpectresOfAClass(HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> map,
			String outputFilename, Parameter parameter) throws IOException {

		BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputFilename)));

		w.write("PARAMETERS\n");
		w.write("VMin = " + parameter.getVMin() + "\n");
		w.write("VMax = " + parameter.getVMax() + "\n");
		w.write("step = " + parameter.getStep() + "\n");
		w.write("FWHM = " + parameter.getFWHM() + "\n\n");

		for (Entry<String, Couple<ArrayList<Double>, ResultLogFile>> entry : map.entrySet()) {

			String filename = entry.getKey();
			w.write(filename + "\n");

			int V = parameter.getVMin();
			for (Double s : entry.getValue().getX()) {
				w.write("spectre(" + V + ") = " + s + "\n");
				V += parameter.getStep();
			}

			w.write("\n");
		}

		w.close();
	}

	public static HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> computeSpectresOfAClass(
			String fileNameListFile, Parameter parameter) throws IOException {

		HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> map = new HashMap<String, Couple<ArrayList<Double>, ResultLogFile>>();

		BufferedReader r = new BufferedReader(new FileReader(new File(fileNameListFile)));
		String line;

		while ((line = r.readLine()) != null) {
			ResultLogFile result = parseLogFile(line);
			ArrayList<Double> spectres = spectres(result, parameter);
			map.put(line, new Couple<ArrayList<Double>, ResultLogFile>(spectres, result));
		}

		r.close();

		writeSpectresOfAClass(map, "spectres.txt", parameter);

		return map;
	}

	public static HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> computeSpectresOfAClass(PAHClass PAHClass,
			Parameter parameter) throws IOException {

		HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> map = new HashMap<String, Couple<ArrayList<Double>, ResultLogFile>>();

		for (int index = 0; index < PAHClass.size(); index++) {

			String logFilename = PAHClass.getLogFilename(index);
			ResultLogFile result = parseLogFile(logFilename);
			ArrayList<Double> spectres = spectres(result, parameter);
			map.put(logFilename, new Couple<ArrayList<Double>, ResultLogFile>(spectres, result));
		}

		return map;

	}

	public static double getResultEnergy(ResultLogFile result) {
		return result.getFinalEnergy().get(result.getFinalEnergy().size() - 1);
	}

	public static Couple<String, Double> computeEref(HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> map) {

		String molecule = "";
		double maximalEnergy = Double.MAX_VALUE;

		for (Entry<String, Couple<ArrayList<Double>, ResultLogFile>> entry : map.entrySet()) {

			ResultLogFile result = entry.getValue().getY();
			double Ei = getResultEnergy(result);

			if (Ei < maximalEnergy) {
				maximalEnergy = Ei;
				molecule = entry.getKey();
			}
		}

		return new Couple<String, Double>(molecule, maximalEnergy);
	}

	public static void writeFinalResult(ArrayList<Double> energies, double Eref, String ErefMolecule,
			String outputFilename, Parameter parameter) throws IOException {

		BufferedWriter w = new BufferedWriter(new FileWriter(new File(outputFilename)));

		w.write("PARAMETERS\n");
		w.write("ZT = " + parameter.getZT() + "\n");
		w.write("gi = " + parameter.getGi() + "\n");
		w.write("N = " + parameter.getN() + "\n");
		w.write("T = " + parameter.getT() + "\n");
		w.write("kb = " + parameter.getKb() + "\n");
		w.write("Eref = " + Eref + " (" + ErefMolecule + ")\n");
		w.write("VMin = " + parameter.getVMin() + "\n");
		w.write("VMax = " + parameter.getVMax() + "\n");
		w.write("step = " + parameter.getStep() + "\n");
		w.write("FWHM = " + parameter.getFWHM() + "\n\n");

		for (int V = parameter.getVMin(); V <= parameter.getVMax(); V += parameter.getStep()) {
			int index = V - parameter.getVMin();
			w.write("Result(" + V + ") = " + energies.get(index) + "\n");
		}

		w.close();

	}

	public static ArrayList<Double> computeEnergyOfAClass(HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> map,
			Parameter parameter) throws IOException {

		ArrayList<Double> energies = new ArrayList<Double>();

		Couple<String, Double> coupleERef = computeEref(map);
		String ErefMolecule = coupleERef.getX();
		double Eref = coupleERef.getY();

		for (int V = parameter.getVMin(); V <= parameter.getVMax(); V += parameter.getStep()) {

			double energy = 0;

			for (Entry<String, Couple<ArrayList<Double>, ResultLogFile>> entry : map.entrySet()) {

				int index = V - parameter.getVMin();

				ResultLogFile result = entry.getValue().getY();
				double Ei = getResultEnergy(result);

				double spectre = entry.getValue().getX().get(index);
				double Ni = Ni(Ei, Eref, parameter);
				double moleculeEnergy = spectre * Ni;

				energy += moleculeEnergy;
			}

			energies.add(energy);

		}

		writeFinalResult(energies, Eref, ErefMolecule, "results.txt", parameter);

		return energies;
	}

	public static ArrayList<Double> computeEnergyOfAClass(HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> map,
			Parameter parameter, ResultSpectrums resultSpectrums) {

		ArrayList<Double> energies = new ArrayList<Double>();

		Couple<String, Double> coupleERef = computeEref(map);
		String ErefMolecule = coupleERef.getX();
		double Eref = coupleERef.getY();

		for (int V = parameter.getVMin(); V <= parameter.getVMax(); V += parameter.getStep()) {

			double energy = 0;

			for (Entry<String, Couple<ArrayList<Double>, ResultLogFile>> entry : map.entrySet()) {

				int index = V - parameter.getVMin();

				ResultLogFile result = entry.getValue().getY();
				double Ei = getResultEnergy(result);

				double spectre = entry.getValue().getX().get(index);
				double Ni = Ni(Ei, Eref, parameter);
				double moleculeEnergy = spectre * Ni;

				energy += moleculeEnergy;
			}

			energies.add(energy);

		}

		// writeFinalResult(energies, Eref, ErefMolecule, "results.txt", parameter);

		resultSpectrums.setMoleculeEref(ErefMolecule);
		resultSpectrums.setErefValue(Eref);

		return energies;
	}

	public static Parameter initializeParameters(String parametersFilename) throws IOException {

		ArrayList<String> lines = new ArrayList<String>();

		BufferedReader r = new BufferedReader(new FileReader(new File(parametersFilename)));
		String line;

		while ((line = r.readLine()) != null) {
			lines.add(line);
		}

		r.close();

		Parameter parameter = new Parameter();

		parameter.setVMin(Integer.parseInt(lines.get(0).split(Pattern.quote(" = "))[1]));
		parameter.setVMax(Integer.parseInt(lines.get(1).split(Pattern.quote(" = "))[1]));
		parameter.setStep(Integer.parseInt(lines.get(2).split(Pattern.quote(" = "))[1]));
		parameter.setFWHM(Integer.parseInt(lines.get(3).split(Pattern.quote(" = "))[1]));

		parameter.setZT(Double.parseDouble(lines.get(4).split(Pattern.quote(" = "))[1]));
		parameter.setGi(Double.parseDouble(lines.get(5).split(Pattern.quote(" = "))[1]));
		parameter.setN(Double.parseDouble(lines.get(6).split(Pattern.quote(" = "))[1]));
		parameter.setT(Double.parseDouble(lines.get(7).split(Pattern.quote(" = "))[1]));

		String splittedKb = lines.get(8).split(Pattern.quote(" = "))[1];
		double factor = Double.parseDouble(splittedKb.split(" ")[0]);
		int exponent = Integer.parseInt(splittedKb.split(" ")[1]);

		parameter.setKb(factor * Math.pow(10, exponent));

		return parameter;
	}

	public static void treatClass(String fileNameListFilename, String parametersFilename) throws IOException {

		Parameter parameter = initializeParameters(parametersFilename);

		System.out.println(parameter.getVMin() + " " + parameter.getVMax() + " " + parameter.getStep() + " "
				+ parameter.getFWHM() + " " + parameter.getZT() + " " + parameter.getGi() + " " + parameter.getN() + " "
				+ parameter.getT());

		HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> map = computeSpectresOfAClass(fileNameListFilename,
				parameter);
		computeEnergyOfAClass(map, parameter);
	}

	public static ResultSpectrums treatClass(PAHClass PAHClass, Parameter parameter) throws IOException {

		ResultSpectrums result = new ResultSpectrums(PAHClass, parameter);

		HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> map = computeSpectresOfAClass(PAHClass, parameter);
		result.setSpectres(map);

		ArrayList<Double> energies = computeEnergyOfAClass(map, parameter);
		result.setEnergies(energies);

		return result;
	}

	/*
	 * Fonctions pour le catalogue
	 */
	public static HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> computeSpectresOfAClass(PAHClass PAHClass,
			Parameter parameter, ArrayList<ResultLogFile> results) {

		HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> map = new HashMap<String, Couple<ArrayList<Double>, ResultLogFile>>();

		for (int index = 0; index < results.size(); index++) {
			ResultLogFile result = results.get(index);
			ArrayList<Double> spectres = spectres(result, parameter);
			map.put(PAHClass.getMoleculesNames().get(index),
					new Couple<ArrayList<Double>, ResultLogFile>(spectres, result));
		}

		return map;
	}

	public static HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> computeSpectresOfAClass(
            ArrayList<Benzenoid> molecules, Parameter parameter) {

		HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> map = new HashMap<>();

		for (Benzenoid molecule : molecules) {

			Optional<ResultLogFile> IRSpectra = molecule.getDatabaseInformation().findIRSpectra();
			if (IRSpectra.isPresent()) {
				ArrayList<Double> spectres = spectres(IRSpectra.get(), parameter);
				map.put(molecule.getNames().get(0), new Couple<>(spectres, IRSpectra.get()));
			}
		}
		return map;
	}

	public static ResultSpectrums treatClass(PAHClass PAHClass, Parameter parameter, ArrayList<ResultLogFile> results)
			throws IOException {

		ResultSpectrums result = new ResultSpectrums(PAHClass, parameter);

		HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> map = computeSpectresOfAClass(PAHClass, parameter,
				results);
		result.setSpectres(map);

		ArrayList<Double> energies = computeEnergyOfAClass(map, parameter);
		result.setEnergies(energies);

		return result;
	}

	/*
	 * Méthode pour l'appli
	 */
	public static IRSpectra buildSpectraData(String className, ArrayList<Benzenoid> molecules, Parameter parameter)
			throws IOException {

		IRSpectra spectraData = new IRSpectra(parameter, className, molecules);

		HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> map = computeSpectresOfAClass(molecules, parameter);
		spectraData.setSpectres(map);

		ArrayList<Double> energies = computeEnergyOfAClass(map, parameter);
		spectraData.setEnergies(energies);

		return spectraData;
	}

	public static void displayUsage() {

		System.out.println("# Computation of optical spectrums");
		System.out.println();
		System.out.println("# Compute the optical spectrums of a given class of PAH");
		System.out.println();
		System.out.println("# USAGE: java -jar SpectrumsComputer.jar ${inputFileName} ${parametersFileName}");
		System.out.println();
		System.out.println("## ${inputFileName} must contains one .log file per line");
		System.out.println(
				"## ${parametersFileName} must contains all the parameters required by the computation (you can use ls *.log > ${parametersFilename} to generate it)");
		System.out.println();
		System.out.println("# Syntax of ${parametersFileName} : ");
		System.out.println("#\t VMin = ${VMin}");
		System.out.println("#\t VMax = ${VMax}");
		System.out.println("#\t step = ${step}");
		System.out.println("#\t FWHM = ${FWHM}");
		System.out.println("#\t ZT = ${ZT}");
		System.out.println("#\t gi = ${gi}");
		System.out.println("#\t N = ${N}");
		System.out.println("#\t T = ${T}");
		System.out.println("#\t kb = ${factor} ${exponent} => ${factor} * 10 ^ ${exponent}");
		System.out.println();
		System.out.println("# outputs : ");
		System.out.println("#\t spectrums -> ./spectres.txt");
		System.out.println("#\t final results -> ./results.txt");

	}

	public static void main(String[] args) throws IOException {

		if (args.length != 2) {
			displayUsage();
			System.exit(1);
		}

		String inputFilename = args[0];
		String parametersFilename = args[1];
		treatClass(inputFilename, parametersFilename);
	}
}
