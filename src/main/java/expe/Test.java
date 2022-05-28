package expe;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

public class Test {

	public static String pictureToString(String filePath) throws IOException {
		byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		return encodedString;
	}
	
	public static void stringToPicture(String encodedString, String outputFileName) throws IOException {
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
		FileUtils.writeByteArrayToFile(new File(outputFileName), decodedBytes);
	}
	
	public static void main(String[] args) throws IOException {
		String path = "/home/adrien/Bureau/pour_adrien/dossier_resultat/U_5_hexagons0_irreg.png";
		String encoded = pictureToString(path);
		
		System.out.println(encoded.length());
		
		stringToPicture(encoded, "test.png");
	}
}
