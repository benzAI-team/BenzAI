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
			double irregularity, String frequency, String intensity, String opeId, String opeName, String opeHexagons,
			String opeCarbons, String opeHydrogens, String opeIrregularity, String opeFrequency, String opeIntensity) {

		StringBuilder json = new StringBuilder();

		json.append("{");

		if (!opeId.equals(""))
			json.append("\"idBenzenoid\": \"" + opeId + " " + id + "\", ");
		else
			json.append("\"idBenzenoid\": \"\", ");

		if (!opeName.equals(""))
			json.append("\"inchie\": \"" + opeName + " " + name + "\", ");
		else
			json.append("\"inchie\": \"\", ");

		if (!opeHexagons.equals(""))
			json.append("\"nbHexagons\": \"" + opeHexagons + " " + nbHexagons + "\", ");
		else
			json.append("\"nbHexagons\": \"\", ");

		if (!opeCarbons.equals(""))
			json.append("\"nbCarbons\": \"" + opeCarbons + " " + nbCarbons + "\", ");
		else
			json.append("\"nbCarbons\": \"\", ");

		if (!opeHydrogens.equals(""))
			json.append("\"nbHydrogens\": \"" + opeHydrogens + " " + nbHydrogens + "\", ");
		else
			json.append("\"nbHydrogens\": \"\", ");

		if (!opeIrregularity.equals(""))
			json.append("\"irregularity\": \"" + opeIrregularity + " " + irregularity + "\"");
		else
			json.append("\"irregularity\": \"\"");

		// new criterions

		if (!opeFrequency.equals(""))
			json.append("\"frequency\": \"" + opeFrequency + " " + frequency + "\"");
		else
			json.append("\"frequency\": \"\"");

		if (!opeIntensity.equals(""))
			json.append("\"intensity\": \"" + opeIntensity + " " + intensity + "\"");
		else
			json.append("\"intensity\": \"\"");

		json.append("}");

		System.out.println(json.toString());
		return json.toString();
	}
}
