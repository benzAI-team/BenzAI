package correlations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RunCorrelationSimple {

	public static void main(String [] args) throws IOException {
		
		String dir1Name = "/home/adrien/Documents/bench_benzenoids/catacondenseds/results_rbo";
		//String dir1Name = "/home/adrien/Documents/bench_benzenoids/chisom/results_rbo";
		//String dir2Name = "/home/adrien/Documents/comparaisons_constraints/instances/chisom";
		
		//String dir2Name = "/home/adrien/Documents/comparaisons_constraints/results_finaux/resultats_normalises/catacondenses";
		String dir2Name = "/home/adrien/Documents/bench_benzenoids/catacondenseds/results_clar";
		
		File dir1 = new File(dir1Name);
		File dir2 = new File(dir2Name);
		
		File [] files = dir1.listFiles();
		
		for (File linFile : files) {
			if (linFile.getName().endsWith("_rbo.dat")) {
				
				if (linFile.getName().contains("molecule_4"))
					System.out.print("");
				
				String fileName = linFile.getAbsolutePath().replace(dir1Name, dir2Name).replace("_rbo.dat", "_clar.dat");
				
				File nicsFile = new File(fileName);
				
				ArrayList<Double> linValues = new ArrayList<>();
				ArrayList<Double> nicsValues = new ArrayList<>();
				
				BufferedReader r = new BufferedReader(new FileReader(linFile));
				String line;
				
				while((line = r.readLine()) != null) {
					if (!line.contains("ms.") && !line.equals("")) {
						String [] sl = line.split(" ");
						linValues.add(Double.parseDouble(sl[1]));
					}
				}
				r.close();
				
				r = new BufferedReader(new FileReader(nicsFile));
				line = null;
				
				while((line = r.readLine()) != null) {
					if (!line.contains("ms.") && !line.equals("")) {
						String [] sl = line.split(" ");
						nicsValues.add(Double.parseDouble(sl[1]));
					}
				}
				
				r.close();
				
				int size = linValues.size();
				
				//double coeff = CorrelationCoefficient.correlationCoefficient(linValues, nicsValues, size);
				
				if (linValues.size() == nicsValues.size()) {
					double coeff = CorrelationCoefficient.correlationCoefficient(linValues, nicsValues, size);
					System.out.println(linFile.getName().replace("_lin.dat", "") + " " + coeff);
				}
				else {
					System.out.println(linFile.getName().replace("_lin.dat", "") + " unknown");
				}
			}
		}
	}
}
