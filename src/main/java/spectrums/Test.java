package spectrums;

import java.io.*;
import java.util.regex.Pattern;

public enum Test {
    ;

    public static void main(String [] args) throws IOException {

		BufferedReader r = new BufferedReader(new FileReader(new File("C:\\Users\\adrie\\Documents\\These\\molecules\\bdd_app\\log_files\\tmp.txt")));
		
		BufferedWriter w = new BufferedWriter(new FileWriter("C:\\Users\\adrie\\Documents\\These\\molecules\\bdd_app\\log_files\\rename.bat"));
		
		String line;
		
		while ((line = r.readLine()) != null) {
			
			String [] splittedLine = line.split(Pattern.quote("_"));

			String newName = "";
			
			for (int i = 0 ; i < splittedLine.length - 1 ; i++) {
				newName += splittedLine[i];
				if (i < splittedLine.length - 2)
					newName += "_";
			}
			
			newName += ".log";
			
			w.write("ren " + line + " " + newName + "\n");
		}
		
		w.close();
		
		r.close();
	}
}
