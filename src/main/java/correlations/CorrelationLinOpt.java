package correlations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import molecules.Molecule;
import parsers.GraphParser;

public class CorrelationLinOpt {

	private static void usage() {
		System.err.println("convert .lin file to .dat file");
		System.err.println("java -jar file list_lin_log_files curent_path");
	}
	
	public static void main(String [] args) throws IOException {
		
		if (args.length < 2) {
			usage();
			System.exit(1);
		}
		
		Double[] RI = new Double[4];
		Double[] RIopt = new Double [] { 0.869, 0.246, 0.100, 0.041};
		for (int i = 0 ; i < 4 ; i ++) {
			double denom = (i + 1) * (i + 1);
			RI[i] = 1.0 / denom;
		}
		
		
		
		File listFile = new File(args[0]);
		String dir = args[1];
		
		BufferedReader r = new BufferedReader(new FileReader(listFile));
		String line;
		
		while((line = r.readLine()) != null) {
			
			File linFile = new File(dir + "/" + line);
			File molFile = new File(dir + "/" + line.replace("_0", "").replace(".lin", ".graph_coord"));
			File datFile = new File(molFile.getAbsolutePath().replace(".graph_coord", "_lin_opt.dat"));
			
			System.out.println("lin file : " + linFile.getAbsolutePath());
			System.out.println("mol file : " + molFile.getAbsolutePath());
			System.out.println("dat file : " + datFile.getAbsolutePath());
			
			Molecule molecule = GraphParser.parseUndirectedGraph(molFile);
			double nbStructures = molecule.getNbKekuleStructures();
			System.out.println("nb structures : " + nbStructures);
			
			int i = 0;
			
			BufferedWriter w = new BufferedWriter(new FileWriter(datFile));
			
			BufferedReader r2 = new BufferedReader(new FileReader(linFile));
			String line2;
			boolean in = false;
			
			while((line2 = r2.readLine()) != null) {
				
				if (!in && line2.startsWith("LOCAL ENERGY")) {
					in = true;
				}
				
				else if (in && !line2.equals("")) {
					String [] vStr = line2.split(Pattern.quote(" : "))[1].split(" ");
					double e = 0.0;
					for (int j = 0 ; j < vStr.length ; j++) {
						String s = vStr[j];
						e += Double.parseDouble(s) * RIopt[j];
					}
					e = e / nbStructures;
					w.write(i + " " + e + "\n");
					i++;
				}
				
				else if (in && line2.equals(""))
					in = false;
			}
			
			r2.close();
			w.close();
			
			System.out.println(datFile.getAbsolutePath() + " treated\n");
		}
		
		r.close();
	}
}
