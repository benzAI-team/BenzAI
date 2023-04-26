package http;

public class JSonStringBuilder {

	public static String buildJsonString(Long id, String name, int nbHexagons, int nbCarbons, int nbHydrogens,
			double irregularity, String opeId, String opeName, String opeHexagons, String opeCarbons,
			String opeHydrogens, String opeIrregularity) {

		return "{\"id\": " + id + ", \"name\": \"" + name + "\", \"nbHexagons\": " + nbHexagons
				+ ", \"nbCarbons\": " + nbCarbons + ", \"nbHydrogens\": " + nbHydrogens + ", \"irregularity\": "
				+ irregularity + ", \"operatorId\": \"" + opeId + "\", \"operatorName\": \"" + opeName
				+ "\", \"operatorHexagons\": \"" + opeHexagons + "\", \"operatorCarbons\": \"" + opeCarbons
				+ "\", \"operatorHydrogens\": \"" + opeHydrogens + "\", \"operatorIrregularity\": \"" + opeIrregularity
				+ "\"}";
	}

	public static String buildNewJsonString(Long id, String name, String nbHexagons, String nbCarbons,
			String nbHydrogens, String irregularity, String frequency, String intensity, String opeId, String opeName,
			String opeHexagons, String opeCarbons, String opeHydrogens, String opeIrregularity, String opeFrequency,
			String opeIntensity) {

		StringBuilder json = new StringBuilder();

		json.append("{");

		if (!opeId.equals(""))
			json.append("\"id\": \"").append(opeId).append(" ").append(id).append("\", ");
		else
			json.append("\"id\": \"\", ");

		if (!opeName.equals(""))
			json.append("\"name\": \"").append(opeName).append(" ").append(name).append("\", ");
		else
			json.append("\"name\": \"\", ");

		if (!opeHexagons.equals(""))
			json.append("\"nbHexagons\": \"").append(opeHexagons).append(" ").append(nbHexagons).append("\", ");
		else
			json.append("\"nbHexagons\": \"\", ");

		if (!opeCarbons.equals(""))
			json.append("\"nbCarbons\": \"").append(opeCarbons).append(" ").append(nbCarbons).append("\", ");
		else
			json.append("\"nbCarbons\": \"\", ");

		if (!opeHydrogens.equals(""))
			json.append("\"nbHydrogens\": \"").append(opeHydrogens).append(" ").append(nbHydrogens).append("\", ");
		else
			json.append("\"nbHydrogens\": \"\", ");

		if (!opeIrregularity.equals(""))
			json.append("\"irregularity\": \"").append(opeIrregularity).append(" ").append(irregularity).append("\", ");
		else
			json.append("\"irregularity\": \"\", ");

		// new criterions

		if (!opeFrequency.equals(""))
			json.append("\"frequency\": \"").append(opeFrequency).append(" ").append(frequency).append("\", ");
		else
			json.append("\"frequency\": \"\", ");

		if (!opeIntensity.equals(""))
			json.append("\"intensity\": \"").append(opeIntensity).append(" ").append(intensity).append("\"");
		else
			json.append("\"intensity\": \"\"");

		json.append("}");

		System.out.println(json);
		return json.toString();
	}
}
