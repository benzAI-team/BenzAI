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
		
		if (!opeId.equals(""))
			json.append("\"idCriterion\": \"" + opeId + " " + id + "\", ");
		else
			json.append("\"idCriterion\": \"\", ");
		
		if (!opeName.equals(""))
			json.append("\"nameCriterion\": \"" + opeName + " " + name + "\", ");
		else
			json.append("\"nameCriterion\": \"\", ");
		
		
		if (!opeHexagons.equals(""))
			json.append("\"nbHexagonsCriterion\": \"" + opeHexagons + " " + nbHexagons + "\", ");
		else
			json.append("\"nbHexagonsCriterion\": \"\", ");
		
		
		if (!opeCarbons.equals(""))
			json.append("\"nbCarbonsCriterion\": \"" + opeCarbons + " " + nbCarbons + "\", ");
		else
			json.append("\"nbCarbonsCriterion\": \"\", ");

		
		if (!opeHydrogens.equals(""))
			json.append("\"nbHydrogensCriterion\": \"" + opeHydrogens + " " + nbHydrogens + "\", ");
		else
			json.append("\"nbHydrogensCriterion\": \"\", ");
		
		if (!opeIrregularity.equals(""))
			json.append("\"irregularityCriterion\": \"" + opeIrregularity + " " + irregularity + "\"");
		else
			json.append("\"irregularityCriterion\": \"\"");
		
		json.append("}");
		
		return json.toString();
	}
}
