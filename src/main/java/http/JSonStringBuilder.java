package http;

public class JSonStringBuilder {

	public static String buildJsonString(Long id, String name, int nbHexagons, int nbCarbons, int nbHydrogens,
			double irregularity, String opeId, String opeName, String opeHexagons, String opeCarbons,
			String opeHydrogens, String opeIrregularity) {

		String json = "{\"id\": " + id + ", \"name\": \"" + name + "\", \"nbHexagons\": " + nbHexagons
				+ ", \"nbCarbons\": " + nbCarbons + ", \"nbHydrogens\": " + nbHydrogens + ", \"irregularity\": "
				+ irregularity + ", \"operatorId\": \"" + opeId + "\", \"operatorName\": \"" + opeName
				+ "\", \"operatorHexagons\": \"" + opeHexagons + "\", \"operatorCarbons\": \"" + opeCarbons
				+ "\", \"operatorHydrogens\": \"" + opeHydrogens + "\", \"operatorIrregularity\": \"" + opeIrregularity
				+ "\"}";

		return json;
	}
	
	public static String buildNewJsonString(Long id, String name, int nbHexagons, int nbCarbons, int nbHydrogens,
			double irregularity, String opeId, String opeName, String opeHexagons, String opeCarbons,
			String opeHydrogens, String opeIrregularity) {
		
		StringBuilder json = new StringBuilder();
		
		json.append("{");
		json.append("\"id\": \"" + opeId + " " + id + "\", ");
		json.append("\"name\": \"" + opeName + " " + name + "\", ");
		json.append("\"nbHexagons\": \"" + opeHexagons + " " + nbHexagons + "\", ");
		json.append("\"nbCarbons\": \"" + opeCarbons + " " + nbCarbons + "\", ");
		json.append("\"nbHydrogens\": \"" + opeHydrogens + " " + nbHydrogens + "\", ");
		json.append("\"irregularity\": \"" + opeIrregularity + " " + irregularity + "\"");
		json.append("}");
		
		return json.toString();
	}
}
