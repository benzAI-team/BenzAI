package database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class BuildScripts {

	private static String directory;
	private static BufferedWriter writer;

	private static void initialize() throws IOException {

		directory = "";
		writer = new BufferedWriter(new FileWriter(new File(directory + "/insert.sql")));

		writer.close();
	}

	private static void treatMolecule(File graphFile, File irFile, File nicsFile, File ims2dFile) throws IOException {

		String moleculeName = graphFile.getName().split(Pattern.quote("."))[0];
		writer.write("# " + moleculeName + "\n\n");
	}

	private static void treatMolecules() {

	}

	public static void main(String[] args) throws IOException {

		initialize();
		treatMolecules();
	}
}
