package correlations;

import java.io.*;
import java.util.regex.Pattern;

public class ReadNicsTime {

	public static void main(String [] args) throws IOException {
		
		BufferedReader r = new BufferedReader(new FileReader(new File("/home/adrien/Documents/comparaisons_constraints/times_nics.txt")));
		String line;
		int state = 0;
		double [] time = new double[4];
		String name1 = null, name2 = null;
		while((line = r.readLine()) != null) {
			
			if (line.startsWith("#") || line.equals("")) {
				state = 0;
				
				for (int i = 0 ; i < time.length ; i++)
					time[i] = 0.0;
				
				System.out.println(line);
			}
			
			else {
				String [] splittedLine = line.split(": ");
				String [] timeStr = splittedLine[1].split(" ");
				
//				for (String s : timeStr)
//					System.out.print(s + " ");
//				System.out.println("");
				
				if (state == 0) {
					name1 = splittedLine[0].split(Pattern.quote("."))[0].replace("_opt", "");
					time[0] += Double.parseDouble(timeStr[0]);
					time[1] += Double.parseDouble(timeStr[2]);
					time[2] += Double.parseDouble(timeStr[4]);
					time[3] += Double.parseDouble(timeStr[6]);
					//System.out.println(line);
					state = 1;
				}
				
				else if (state == 1) {
					name2 = splittedLine[0].split(Pattern.quote("."))[0].replace("_opt", "");
					
					if (!name1.equals(name2))
						System.out.println();
					
					time[0] += Double.parseDouble(timeStr[0]);
					time[1] += Double.parseDouble(timeStr[2]);
					time[2] += Double.parseDouble(timeStr[4]);
					time[3] += Double.parseDouble(timeStr[6]);
					
					double sum = 0;
					sum += time[0] * 86400.0;
					sum += time[1] * 3600.0;
					sum += time[2] * 60.0;
					sum += time[3];
					
					//System.out.println(line);
					System.out.println(name1 + ": " + sum + " sec.");
					//System.out.println(name + ": " + time[0] + " Days " + time[1] + " Hours " + time[2] + " minutes " + time[3] + " seconds\n");
					
					for (int i = 0 ; i < time.length ; i++)
						time[i] = 0.0;
					
					state = 0;
				}
			}
			
		}
		
		r.close();
	}
}
