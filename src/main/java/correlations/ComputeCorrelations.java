package correlations;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public enum ComputeCorrelations {
    ;

    private static void usage() {
		System.err.println("build all correlations");
		System.err.println("java -jar file curent_directory");
	}
	
	public static void main(String [] args) throws IOException {
		
		if (args.length == 0) {
			usage();
			System.exit(1);
		}
		
		File dir = new File(args[0]);
		File [] files = dir.listFiles();
		
		BufferedWriter w1 = new BufferedWriter(new FileWriter(dir + "/coeff_lin_linfan.txt", true));
		BufferedWriter w2 = new BufferedWriter(new FileWriter(dir + "/coeff_lin_nics.txt", true));
		BufferedWriter w3 = new BufferedWriter(new FileWriter(dir + "/coeff_linfan_nics.txt", true));
		BufferedWriter w4 = new BufferedWriter(new FileWriter(dir + "/coeff_lin_linfan_ns.txt", true));
		BufferedWriter w5 = new BufferedWriter(new FileWriter(dir + "/coeff_lin_nics_ns.txt", true));
		BufferedWriter w6 = new BufferedWriter(new FileWriter(dir + "/coeff_linfan_nics_ns.txt", true));
		
		BufferedWriter w1d = new BufferedWriter(new FileWriter(dir + "/coeff_lin_linfan_details.txt", true));
		BufferedWriter w2d = new BufferedWriter(new FileWriter(dir + "/coeff_lin_nics_details.txt", true));
		BufferedWriter w3d = new BufferedWriter(new FileWriter(dir + "/coeff_linfan_nics_details.txt", true));
		BufferedWriter w4d = new BufferedWriter(new FileWriter(dir + "/coeff_lin_linfan_ns_details.txt", true));
		BufferedWriter w5d = new BufferedWriter(new FileWriter(dir + "/coeff_lin_nics_ns_details.txt", true));
		BufferedWriter w6d = new BufferedWriter(new FileWriter(dir + "/coeff_linfan_nics_ns_details.txt", true));

		assert files != null;
		for (File linFile : files) {
			if (linFile.getName().endsWith("_lin.dat") /*&& !linFile.getName().contains("molecule_19")*/) {
				
				if (linFile.getName().contains("14") || linFile.getName().contains("15") || linFile.getName().contains("16") || linFile.getName().contains("17")
					|| linFile.getName().contains("18") || linFile.getName().contains("19") || linFile.getName().contains("20"))
					System.out.println();
				
				//System.out.println("treating " + linFile.getName().replace("_lin_opt.dat", ""));
				
				if (linFile.getName().contains("molecule_19"))
					System.out.print("");
				
				File linFanFile = new File(linFile.getAbsolutePath().replace("_lin.dat", "_lin_fan.dat"));
				File nicsFile = new File(linFile.getAbsolutePath().replace("_lin.dat", "_nics.dat"));

				boolean exists = linFanFile.exists() && nicsFile.exists();
				
				if (exists) {
				
				int nbHexagons = 0;
				BufferedReader r = new BufferedReader(new FileReader(linFile));
				String line;
				
				while((line = r.readLine()) != null) {
					if (line.split(" ").length == 2)
						nbHexagons ++;
				}
				
				r.close();
				
				ArrayList<Double> linValues = new ArrayList<>();
				ArrayList<Double> linFanValues = new ArrayList<>();
				ArrayList<Double> nicsValues = new ArrayList<>();
				
				ArrayList<Double> linValuesNS = new ArrayList<>();
				ArrayList<Double> linFanValuesNS = new ArrayList<>();
				ArrayList<Double> nicsValuesNS = new ArrayList<>();
				
				//reading lin dat
				r = new BufferedReader(new FileReader(linFile));
					HashMap<Double, ArrayList<Integer>> map = new HashMap<>();
				
				while ((line = r.readLine()) != null) {
					
					Integer hexagon = Integer.parseInt(line.split(" ")[0]);
					Double value = Double.parseDouble(line.split(" ")[1]);
					linValues.add(value);
					//linValuesA[hexagon] = value;
					
					if (map.get(value) == null) {
						map.put(value, new ArrayList<>());
						map.get(value).add(hexagon);
					}
					
					else 
						map.get(value).add(hexagon);
				}
				
				r.close();
				
				//reading linfan dat
				r = new BufferedReader(new FileReader(linFanFile));
					map = new HashMap<>();
				
				while ((line = r.readLine()) != null) {
					
					Integer hexagon = Integer.parseInt(line.split(" ")[0]);
					Double value = Double.parseDouble(line.split(" ")[1]);
					linFanValues.add(value);
					
					if (map.get(value) == null) {
						map.put(value, new ArrayList<>());
						map.get(value).add(hexagon);
					}
					
					else 
						map.get(value).add(hexagon);
				}
				
				r.close();
				
				//reading nics dat
				r = new BufferedReader(new FileReader(nicsFile));
					map = new HashMap<>();
				
				while ((line = r.readLine()) != null) {
					
					Integer hexagon = Integer.parseInt(line.split(" ")[0]);
					Double value = Double.parseDouble(line.split(" ")[1]);
					nicsValues.add(value);
					
					if (map.get(value) == null) {
						map.put(value, new ArrayList<>());
						map.get(value).add(hexagon);
					}
					
					else 
						map.get(value).add(hexagon);
				}
				
				r.close();
				
				//if (linValues.size() == linFanValues.size() && linValues.size() == nicsValues.size()) {
				
				//list wo symmetries
				for (Entry<Double, ArrayList<Integer>> entry : map.entrySet()) {
					ArrayList<Integer> value = entry.getValue();
					int hexagon = value.get(0);
		    	
					linValuesNS.add(linValues.get(hexagon));
					if (linFanValues.size() == linValues.size())
						linFanValuesNS.add(linFanValues.get(hexagon));
					if (nicsValues.size() == linValues.size())
						nicsValuesNS.add(nicsValues.get(hexagon));
				}
			
				
				
				double coeff;
				
				//lin - linfan
				if (linValues.size() == linFanValues.size()) {
					coeff = CorrelationCoefficient.correlationCoefficient(linValues, linFanValues, linValues.size());
				
					if (coeff < 0)
						System.out.print("");
					
					w1.write(coeff + "\n");
					w1d.write(linFile.getName().replace("_lin.dat", "") + "\n");
					w1d.write(coeff + "\n");
				}
				
				//lin - nics
				if (linValues.size() == nicsValues.size()) {
					coeff = CorrelationCoefficient.correlationCoefficient(linValues, nicsValues, linValues.size());
					
					if (coeff < 0)
						System.out.print("");
					
					w2.write(coeff + "\n");
					w2d.write(linFile.getName().replace("_lin.dat", "") + "\n");
					w2d.write(coeff + "\n");
				}
				
				//linfan - nics
				if (linFanValues.size() == nicsValues.size()) {
					coeff = CorrelationCoefficient.correlationCoefficient(linFanValues, nicsValues, linFanValues.size());
					
					if (coeff < 0)
						System.out.print("");
					
					w3.write(coeff + "\n");
					w3d.write(linFile.getName().replace("_lin.dat", "") + "\n");
					w3d.write(coeff + "\n");
				}
				
				//lin - linfan NS
				if (linValuesNS.size() == linFanValuesNS.size()) {
					coeff = CorrelationCoefficient.correlationCoefficient(linValuesNS, linFanValuesNS, linValuesNS.size());
					
					if (coeff < 0)
						System.out.print("");
					
					w4.write(coeff + "\n");
					w4d.write(linFile.getName().replace("_lin.dat", "") + "\n");
					w4d.write(coeff + "\n");
				}
					
				
				//lin - nics
				if (linValuesNS.size() == nicsValuesNS.size()) {
					coeff = CorrelationCoefficient.correlationCoefficient(linValuesNS, nicsValuesNS, linValuesNS.size());
					
					if (coeff < 0)
						System.out.print("");
					
					w5.write(coeff + "\n");
					w5d.write(linFile.getName().replace("_lin.dat", "") + "\n");
					w5d.write(coeff + "\n");
				}
				
				//linfan - nics
				if (linFanValuesNS.size() == nicsValuesNS.size()) {
					coeff = CorrelationCoefficient.correlationCoefficient(linFanValuesNS, nicsValuesNS, linFanValuesNS.size());
					
					if (coeff < 0)
						System.out.print("");
					
					w6.write(coeff + "\n");
					w6d.write(linFile.getName().replace("_lin.dat", "") + "\n");
					w6d.write(coeff + "\n");
				}
				
				System.out.println(linFile.getName().replace("_lin.dat", "") + " treated");
				//}
				}
				
				else {
					System.out.println(linFanFile.getName() + " or " + nicsFile.getName() + " doesnt exists");
				}
			}
		}
		
		w1.close();
		w2.close();
		w3.close();
		w4.close();
		w5.close();
		w6.close();
		
		w1d.close();
		w2d.close();
		w3d.close();
		w4d.close();
		w5d.close();
		w6d.close();
	}
	
}
