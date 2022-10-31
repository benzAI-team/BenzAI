package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.Gson;

public class AddFrequencies {

	private static int idIrData;
	
	private static List<Map> readJson(File file) throws IOException {
		
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line = r.readLine();
		r.close();
		
		List<Map> maps = new ArrayList<>();
		
		line = line.substring(2, line.length() - 2);

		String[] results = line.split(Pattern.quote("},{"));
		
		for (int i = 0; i < results.length; i++) {
			results[i] = "{" + results[i] + "}";
			Gson gson = new Gson();
			Map map = gson.fromJson(results[i], Map.class);
			maps.add(map);
		}
		
		return maps;
	}
	
	private static void writeScript(List<Map> maps) throws IOException {
		
		BufferedWriter w = new BufferedWriter(new FileWriter(new File("/home/adrien/Documents/old_log_files/insert_ir_data.sql")));
		
		for (Map map : maps) {
			
			double idBenzenoid = (Double) map.get("idBenzenoid");
			String frequenciesStr = (String) map.get("frequencies");
			String intensitiesStr = (String) map.get("intensities");
			
			if (frequenciesStr.equals("") || intensitiesStr.equals("")) 
				System.out.println("Error on id " + idBenzenoid);
			
			else {
			
				String [] sFrequencies = frequenciesStr.split(" ");
				String [] sIntensities = intensitiesStr.split(" ");
			
				if (sFrequencies.length != sIntensities.length || sFrequencies.length == 0)
					System.out.println("Error on id " + idBenzenoid);
				else {
					for (int i = 0 ; i < sFrequencies.length ; i++) {
					
						NumberFormat formatter = new DecimalFormat("#0.0000");     
						Double baseFreq = Double.parseDouble(sFrequencies[i]);
						Double baseInten = Double.parseDouble(sIntensities[i]);
					
						String freq = formatter.format(baseFreq).replace(",", ".");
						String inten = formatter.format(baseInten).replace(",", ".");
					
						w.write("INSERT INTO `ir_data` (`idIrData`, `idBenzenoid`, `frequency`, `intensity`) VALUES (");
						w.write(idIrData + ", " + (long) idBenzenoid + ", " + Double.parseDouble(freq) + ", " + Double.parseDouble(inten) + ");\n");
					
						idIrData ++;
					}
				}
			
			}
		}
		
		w.close();
	}
	
	/*
	 * CREATE TABLE `ir_data` (
	`idIrData` bigint(20) PRIMARY KEY,
	`idBenzenoid` bigint(20) NOT NULL,
	`frequency` float(24),
	`intensity` float(24),
	CONSTRAINT `foreign_ir_data` FOREIGN KEY (`idBenzenoid`) REFERENCES `benzenoid` (`idBenzenoid`)
)ENGINE=InnoDB DEFAULT CHARSET=latin1;

	 */
	
	public static void main(String [] args) throws IOException {
		
		idIrData = 0;
		
		File file = new File("/home/adrien/Bureau/find_all_ir.json");
		List<Map> maps = readJson(file);
		writeScript(maps);
	}
	
}
