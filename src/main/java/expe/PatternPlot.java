package expe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class PatternPlot {

	public static double getTime(File datFile) throws IOException {
		
		BufferedReader r = new BufferedReader(new FileReader(datFile));
		String line;
		
		while((line = r.readLine()) != null) {
			if (line.contains("Resolution time")) {
				String [] splittedLine = line.split(Pattern.quote(" : "));
				String stime = splittedLine[1].replace("s", "").replace(",", ".").replace(" ", "");
				double time = Double.parseDouble(stime);
				return time;
			}
		}
		
		return 7200.0;
	}
	
	public static void main(String [] args) throws IOException {
		
		String [] patterns = new String [] {
				"armchair_edge", "c3_h3_protusion", "c4_h4_protusion", "deep_bay", "shallow_armchair_bay",
				"ultra_deep_bay", "zigzag_bay", "zigzag_edge"
		};
		
		File dir = new File("/home/adrien/Documents/forbidden_patterns_september");
		
		File out = new File(dir.getAbsolutePath() + "/temps_m2.txt");
		
		BufferedWriter w = new BufferedWriter(new FileWriter(out));
		
		for (int i = 0 ; i < patterns.length ; i++) {
			//for (int j = 0 ; j < patterns.length ; j++) {
				
				String p1 = patterns[i];
				//String p2 = patterns[j];
				
				for (int nbHexagons = 2 ; nbHexagons <= 9 ; nbHexagons ++) {
				
					for (int model = 1 ; model <= 3 ; model ++) {
					
						if (model == 3) {
						
						for (int hVar = 1 ; hVar <= 4 ; hVar ++) {
					
							// FF
							//if (hVar == 2) {
						
								for (int hVal = 1 ; hVal <= 2 ; hVal ++) {
							
									//String datName = p1 + "_" + p2 + "_" + nbHexagons + "_" + model + "_" + hVar + "_" + hVal + "_1.dat";
									String datName = p1 + "_" + nbHexagons + "_" + model + "_" + hVar + "_" + hVal + "_1_forbidden.dat";
									File file = new File(dir.getAbsolutePath() + "/" + datName);
									
									if (file.exists()) {
										double time = getTime(file);
										w.write(datName + " " + time + "\n");
									}
									
								
								}
						
							//}
						}
						}
					}
				}
			//}
		}
		
		w.close();
		
	}
	
}
