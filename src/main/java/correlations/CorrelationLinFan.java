package correlations;

import molecules.Molecule;
import parsers.GraphParser;

import java.io.*;
import java.util.regex.Pattern;

public enum CorrelationLinFan {
    ;

    private static void usage() {
		System.err.println("convert .lin file to .dat file");
		System.err.println("java -jar file list_lin_log_files curent_path");
	}
	
	public static void main(String [] args) throws IOException {
		
		if (args.length < 2) {
			usage();
			System.exit(1);
		}
		
		File listFile = new File(args[0]);
		String dir = args[1];
		
		BufferedReader r = new BufferedReader(new FileReader(listFile));
		String line;
		
		while((line = r.readLine()) != null) {
			
			File linFile = new File(dir + "/" + line);
			File molFile = new File(dir + "/" + line.replace("_0", "").replace(".lin_fan", ".graph_coord"));
			File datFile = new File(molFile.getAbsolutePath().replace(".graph_coord", "_lin_fan.dat"));
			
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
				
				if (!in && line2.startsWith("NORMALIZED RESULTS")) {
					in = true;
				}
				
				else if (in && !line2.startsWith("time")) {
					double e = Double.parseDouble(line2.split(Pattern.quote(" : "))[1]);
					w.write(i + " " + e + "\n");
					i++;
				}
				
				else if (in && line2.startsWith("time"))
					in = false;
			}
			
			r2.close();
			w.close();
			
			System.out.println(datFile.getAbsolutePath() + " treated\n");
		}
		
		r.close();
	}
}
