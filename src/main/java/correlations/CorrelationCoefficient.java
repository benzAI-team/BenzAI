package correlations;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public enum CorrelationCoefficient {
	;


	static double correlationCoefficient(ArrayList<Double> X, ArrayList<Double> Y, double n) {

		 double sum_X = 0.0, sum_Y = 0.0, sum_XY = 0.0;
		 double squareSum_X = 0.0, squareSum_Y = 0.0;

		 for (int i = 0; i < n; i++) {
			 // sum of elements of array X.
			 sum_X = sum_X + X.get(i);

			 // sum of elements of array Y.
			 sum_Y = sum_Y + Y.get(i);

			 // sum of X[i] * Y[i].
			 sum_XY = sum_XY + X.get(i) * Y.get(i);

			 // sum of square of array elements.
			 squareSum_X = squareSum_X + X.get(i) * X.get(i);
			 squareSum_Y = squareSum_Y + Y.get(i) * Y.get(i);
		 }

		 // use formula for calculating correlation 
		 // coefficient.
		 double corr = (n * sum_XY - sum_X * sum_Y) / Math.sqrt((n * squareSum_X - sum_X * sum_X) * (n * squareSum_Y - sum_Y * sum_Y));
		 return Math.abs(corr);
	 }
	 
	 public static void buildFiles() throws IOException {
		 
		 BufferedReader r = new BufferedReader(new FileReader("/home/adrien/Bureau/corelations/ruiz_morales/resultats_nics.txt"));
		 String line;
		 int state = 0;
		 
		 String name = null;
		 String hexagons = null;
		 String values = null;
		 
		 while((line = r.readLine()) != null) {
			 
			 if (state == 0) {
				 name = line;
				 state ++;
			 }
			 
			 else if (state == 1) {
				 hexagons = line;
				 state ++;
			 }
			 
			 else if (state == 2) {
				 values = line;
				 state ++;
			 }
			 
			 else if (state == 3) {
				 state = 0;
				 
				 
				 BufferedWriter w = new BufferedWriter(new FileWriter("/home/adrien/Bureau/corelations/ruiz_morales/" + name + "_nics.dat"));

				 assert hexagons != null;
				 String [] splittedHexagons = hexagons.split(Pattern.quote(" < "));
				 assert values != null;
				 String [] splittedValues = values.split(Pattern.quote(" < "));
				 
				 int nbHexagons = 0;
				 for (String s : splittedHexagons) {
					 String [] ss = s.split(Pattern.quote(" = "));
					 nbHexagons += ss.length;
				 }
				 
				 Double [] energies = new Double[nbHexagons];
				 
				 for (int i = 0 ; i < splittedHexagons.length ; i++) {
					 
					 String hexagonsSymm = splittedHexagons[i];
					 Double value = Double.parseDouble(splittedValues[i]);
					 
					 hexagonsSymm = hexagonsSymm.substring( 1, hexagonsSymm.length() - 1 );
					 
					 String [] hexagonsSymmetrics = hexagonsSymm.split(Pattern.quote(" = "));
					 
					 for (String s : hexagonsSymmetrics) {
						 energies[Integer.parseInt(s)] = value;
					 }
				 }
				 
				 for (int i = 0 ; i < energies.length ; i++) {
					 w.write(i + " " + energies[i] + "\n");
				 }
				 
				 w.close();
			 }
			 
		 }
		 
		 r.close();
	 }

	 public static void corelation(File folder) throws IOException {
		 
		 HashMap<String, File []> map = new HashMap<>();
		 
		 File[] files = folder.listFiles();

		 assert files != null;
		 for (File file : files) {
			 if (file.isFile() && file.getName().endsWith("_lin.dat")) {

				 String key = file.getName().substring(0, file.getName().length() - 8);
				 map.computeIfAbsent(key, k -> new File[2]);
				 map.get(key)[0] = file;

			 } else if (file.isFile() && file.getName().endsWith("_nics.dat")) {

				 String key = file.getName().substring(0, file.getName().length() - 9);
				 map.computeIfAbsent(key, k -> new File[2]);
				 map.get(key)[1] = file;
			 }

		 }
		 
		 
		 for (Map.Entry<String, File []> entry : map.entrySet()) {
			    
			 String key = entry.getKey();
			 File [] value = entry.getValue();
			    
			 boolean ok = true;
			 for (File file : value)
				 if (file == null) {
					 ok = false;
					 break;
				 }
			 
			 if (ok) {
			 
				 File linFile = value[0];
				 File nicsFile = value[1];
			 
				 System.out.println(key);
				 //System.out.println(linFile.getName() + " & " + nicsFile.getName());
			 
				 ArrayList<Double> linValues = new ArrayList<>();
				 ArrayList<Double> nicsValues = new ArrayList<>();
			 
				 BufferedReader r = new BufferedReader(new FileReader(linFile));
				 String line;
			 
				 while((line = r.readLine()) != null)
					 linValues.add(Double.parseDouble(line.split(" ")[1]));
			 
				 r.close();
			 
				 r = new BufferedReader(new FileReader(nicsFile));
			 
				 while((line = r.readLine()) != null)
					 nicsValues.add(Double.parseDouble(line.split(" ")[1]));
			 
				 double corelation = correlationCoefficient(linValues, nicsValues, linValues.size());
				 System.out.println(corelation + "\n");
			 
				 r.close();
			 }
		}	 
	 }
	 
	 public static void main(String [] args) throws IOException {
		 //buildFiles();
		 corelation(new File("/home/adrien/Bureau/corelations/rectangle/lin/"));
	 }
}
