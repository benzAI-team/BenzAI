package toolbox;

import java.io.*;

public class BuildScript {

	private static void buildScript(String filename) throws IOException {
		
		BufferedReader r = new BufferedReader(new FileReader(filename));
		String line;
		
		System.out.println("#! /bin/bash\n");
		System.out.println("mkdir good_molecules");
		
		while((line = r.readLine()) != null) {
			String [] splittedLine = line.split(" ");
			System.out.println("cp " + splittedLine[0] + " good_molecules/");
		}
		
		r.close();
	}
	
	public static void main(String [] args) throws IOException {
		buildScript(args[0]);
	}
}
