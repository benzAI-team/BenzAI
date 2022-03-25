package expe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import molecules.Molecule;
import parsers.GraphParser;

public class Test {

	public static void main(String[] args) throws IOException {
/*
		File f = new File("/home/adrien/Bureau/list");
		BufferedReader r = new BufferedReader(new FileReader(f));
		String l;
		
		while((l = r.readLine()) != null) {
			System.out.println("/home/COALA/varet/compute_structures.sh /home/COALA/varet/instances_calculs_structures/all_instances/" + l + " 0 0");
			System.out.println("/home/COALA/varet/compute_structures.sh /home/COALA/varet/instances_calculs_structures/all_instances/" + l + " 1 0");
			System.out.println("/home/COALA/varet/compute_structures.sh /home/COALA/varet/instances_calculs_structures/all_instances/" + l + " 1 1");
			System.out.println("/home/COALA/varet/compute_structures.sh /home/COALA/varet/instances_calculs_structures/all_instances/" + l + " 1 2");
		}
		
		
		r.close();
*/
		
		Molecule m = GraphParser.parseUndirectedGraph(new File("/home/adrien/Documents/old_log_files/9_hexagons6395.graph_coord"));
		ArrayList<String> s = m.getNames();
		int x = 10;
	}
}
