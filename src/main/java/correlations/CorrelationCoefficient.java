package correlations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CorrelationCoefficient {

	
	static double correl2(ArrayList<Double> X, ArrayList<Double> Y, double n) {
		
		double sumM1 = 0.0;
		double sumM2 = 0.0;
		double sumM1M2 = 0.0;
		
		for (int i = 0 ; i < n ; i++) {
			sumM1 += X.get(i);
			sumM2 += Y.get(i);
			sumM1M2 += X.get(i) * Y.get(i);
		}
		
		double num = (n * sumM1M2) - (sumM1 * sumM2);
		
		double denum1 = Math.sqrt((n * sumM1) - (sumM1 * sumM1));
		
		double denum2 = Math.sqrt((n * sumM2) - (sumM2 * sumM2));
		
		double correl = num / (denum1 * denum2);
		
		return Math.abs(correl);
	}
	
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
		 
		 double num = (double)(n * sum_XY - sum_X * sum_Y);
		 double denom = (double)(Math.sqrt((n * squareSum_X - sum_X * sum_X) * (n * squareSum_Y - sum_Y * sum_Y)));
		 
//		 if (num == 0.0 && denom == 0.0)
//			 return 1.0;
		 
		 double corr = (double)(n * sum_XY - sum_X * sum_Y) / (double)(Math.sqrt((n * squareSum_X - sum_X * sum_X) * (n * squareSum_Y - sum_Y * sum_Y)));
		 return Math.abs(corr);
	 }
	 
	 public static void buildFiles() throws IOException {
		 
		 BufferedReader r = new BufferedReader(new FileReader(new File("/home/adrien/Bureau/corelations/ruiz_morales/resultats_nics.txt")));
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
				 
				 
				 BufferedWriter w = new BufferedWriter(new FileWriter(new File("/home/adrien/Bureau/corelations/ruiz_morales/" + name + "_nics.dat")));
				 
				 String [] splittedHexagons = hexagons.split(Pattern.quote(" < "));
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
		 
		 for (int i = 0; i < files.length; i++) {
			 File file = files[i];
			 if (file.isFile() && file.getName().endsWith("_lin.dat")) {
				 
				 String key = file.getName().substring(0, file.getName().length() - 8);
				 if (map.get(key) != null) {
					 map.get(key)[0] = file;
				 }
				 else {
					 map.put(key, new File[2]);
					 map.get(key)[0] = file;
				 }
				 
			 }
				
			 
			 else if (file.isFile() && file.getName().endsWith("_nics.dat")) {
				 
				 String key = file.getName().substring(0, file.getName().length() - 9);
				 if (map.get(key) != null) {
					 map.get(key)[1] = file;
				 }
				 else {
					 map.put(key, new File[2]);
					 map.get(key)[1] = file;
				 }
			 }
				
		 }
		 
		 
		 for (Map.Entry<String, File []> entry : map.entrySet()) {
			    
			 String key = entry.getKey();
			 File [] value = entry.getValue();
			    
			 boolean ok = true;
			 for (int i = 0 ; i < value.length ; i++) 
				 if (value[i] == null)
					 ok = false;
			 
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
