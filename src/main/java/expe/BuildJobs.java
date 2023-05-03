package expe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public enum BuildJobs {
    ;


    public static void main(String[] args) throws IOException {

		String script = "/home/COALA/varet/run_benzenoid_generator.sh";

		String[] params = new String[] { "", "coro", "cata", "mirr", "rect", "rot60", "rot120", "rot180", "vert",
				"rot120v", "rot180e", "60mirror", "120vertexmirror", "120mirrorh", "120mirrore", "180emirror",
				"180mirror", "rhomb" };

		 params = new String[] { "mirr", "rot60", "rot120", "rot180", "vert",
		 "rot120v", "rot180e", "60mirror",
		 "120vertexmirror", "120mirrorh", "120mirrore", "180emirror", "180mirror" };

		BufferedWriter w = new BufferedWriter(new FileWriter(
				new File("/home/adrien/jobs_benzenoid_generation")));

		for (int nbHexagons = 3; nbHexagons <= 20; nbHexagons++) {

			for (int i = 0; i < params.length; i++) {

				String param = params[i];

				//w.write(script + " hexa " + nbHexagons + " " + param + "\n");
			}

		}

		for (int nbHexagons = 8; nbHexagons <= 12; nbHexagons++) {
			//w.write(script + " hexa " + nbHexagons + " " + "coro" + "\n");
			//w.write(script + " hexa " + nbHexagons + " " + "coro2" + "\n");
		}

		for (int nbHexagons = 15 ; nbHexagons <= 20 ; nbHexagons ++) {
			w.write(script + " hexa " + nbHexagons + " " + "coro " + " mirr\n");
			w.write(script + " hexa " + nbHexagons + " " + "coro2 " + " mirr\n");
		}
		
/*
		w.write("java -jar /home/COALA/varet/CarbonsHydrogenGenerator.jar\n");
		
		w.write("java -jar /home/COALA/varet/CarbonsHydrogenIrregularityGenerator.jar 0.0 0.1\n");
		w.write("java -jar /home/COALA/varet/CarbonsHydrogenIrregularityGenerator.jar 0.1 0.2\n");
		w.write("java -jar /home/COALA/varet/CarbonsHydrogenIrregularityGenerator.jar 0.2 0.3\n");
		w.write("java -jar /home/COALA/varet/CarbonsHydrogenIrregularityGenerator.jar 0.3 0.4\n");
		w.write("java -jar /home/COALA/varet/CarbonsHydrogenIrregularityGenerator.jar 0.4 0.5\n");
		w.write("java -jar /home/COALA/varet/CarbonsHydrogenIrregularityGenerator.jar 0.5 0.6\n");
		w.write("java -jar /home/COALA/varet/CarbonsHydrogenIrregularityGenerator.jar 0.6 0.7\n");
		w.write("java -jar /home/COALA/varet/CarbonsHydrogenIrregularityGenerator.jar 0.7 0.8\n");
		w.write("java -jar /home/COALA/varet/CarbonsHydrogenIrregularityGenerator.jar 0.8 0.9\n");
		w.write("java -jar /home/COALA/varet/CarbonsHydrogenIrregularityGenerator.jar 0.9 1.0\n");
*/		
		w.close();

	}
}
