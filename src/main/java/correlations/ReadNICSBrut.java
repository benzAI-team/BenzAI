package correlations;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ReadNICSBrut{

	public static void main(String [] args) throws IOException {
		
		BufferedReader r = new BufferedReader(new FileReader(new File("/home/adrien/Documents/comparaisons_constraints/nics_brut.txt")));
		String line;
		ArrayList<String> lines = new ArrayList<>();
		
		while((line = r.readLine()) != null) {
			if (!line.equals(""))
				lines.add(line);
		}
		
		for (int i = 0 ; i < lines.size() ; i += 2) {
			
			String l1 = lines.get(i);
			String l2 = lines.get(i + 1);
			
			String [] splittedLine1 = l1.split(": ");
			String [] timeStr1 = splittedLine1[1].split(" ");
			
			String [] splittedLine2 = l2.split(": ");
			String [] timeStr2 = splittedLine2[1].split(" ");
			
			String name1 = splittedLine1[0].split(Pattern.quote("."))[0].replace("_opt", "");
			String name2 = splittedLine2[0].split(Pattern.quote("."))[0].replace("_opt", "");
			
			double [] time = new double[4];
			
			time[0] += Double.parseDouble(timeStr1[0]);
			time[1] += Double.parseDouble(timeStr1[2]);
			time[2] += Double.parseDouble(timeStr1[4]);
			time[3] += Double.parseDouble(timeStr1[6]);
				
			time[0] += Double.parseDouble(timeStr2[0]);
			time[1] += Double.parseDouble(timeStr2[2]);
			time[2] += Double.parseDouble(timeStr2[4]);
			time[3] += Double.parseDouble(timeStr2[6]);
			
			double sum = 0;
			sum += time[0] * 86400.0;
			sum += time[1] * 3600.0;
			sum += time[2] * 60.0;
			sum += time[3];
			
			if (!name1.equals(name2)) {
				System.out.println(name1 + " and " + name2);
			}
			
			System.out.println(name1 + ": " + sum + " s.");
		}
	}
}
