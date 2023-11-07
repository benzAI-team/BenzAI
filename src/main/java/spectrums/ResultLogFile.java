package spectrums;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ResultLogFile {

	private final String filename;
	
	private final ArrayList<Double> frequencies;
	private final ArrayList<Double> intensities;
	private final ArrayList<Double> finalEnergies;
	private final double zeroPointEnergy;

	private String amesFormat;

	private double molecularMass;
	public ResultLogFile(String filename, ArrayList<Double> frequencies, ArrayList<Double> intensities, ArrayList<Double> finalEnergies, double zeroPointEnergy) {
		this.frequencies = frequencies;
		this.intensities = intensities;
		this.finalEnergies = finalEnergies;
		this.zeroPointEnergy = zeroPointEnergy;
		this.filename = filename;
	}

	public ResultLogFile(String filename, ArrayList<Double> frequencies, ArrayList<Double> intensities, ArrayList<Double> finalEnergies, double zeroPointEnergy, double molecularMass) {
		this.frequencies = frequencies;
		this.intensities = intensities;
		this.finalEnergies = finalEnergies;
		this.zeroPointEnergy = zeroPointEnergy;
		this.filename = filename;
		this.molecularMass = molecularMass;
	}

	public String getFilename() {
		return filename;
	}
	
	public Double getFrequency(int index) {
		return frequencies.get(index);
	}
	
	public Double getIntensity(int index) {
		return intensities.get(index);
	}
	
	public int getNbFrequencies() {
		return frequencies.size();
	}
	
	public ArrayList<Double> getFinalEnergy() {
		return finalEnergies;
	}
	
	public double getZeroPointEnergy() {
		return zeroPointEnergy;
	}
	
	public String getFilenameWithoutExtension() {
		
		String [] splittedFilename = filename.split(Pattern.quote("."));
		
		StringBuilder b = new StringBuilder();
		
		for (int i = 0 ; i < splittedFilename.length - 1 ; i++) {
			b.append(splittedFilename[i]);
			if (i < splittedFilename.length - 2)
				b.append(".");
		}
		
		return b.toString();
	}
	
	public ArrayList<Double> getFrequencies() {
		return frequencies;
	}
	
	public ArrayList<Double> getIntensities() {
		return intensities;
	}

	public double getMolecularMass() {
		return molecularMass;
	}

	public String getAmesFormat() {
		return amesFormat;
	}

	public void setAmesFormat(String amesFormat) {
		this.amesFormat = amesFormat;
	}

	@Override
	public String toString() {
		
		StringBuilder b = new StringBuilder();
		
		b.append(filename + "\n");	
		b.append("frequencies : ");
		for (Double f : frequencies)
			b.append(f + "\t");
		b.append("\n");
		b.append("intensities : ");
		for (Double i : intensities)
			b.append(i + "\t");
		b.append("\n");
		b.append("energies : ");
		for (Double e : finalEnergies)
			b.append(e + "\t");
		b.append("\n");
		b.append("ZPE : " + zeroPointEnergy);
		
		return b.toString();
	}
}
