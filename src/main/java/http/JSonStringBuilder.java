package http;

public enum JSonStringBuilder {
    ;

    public static String buildJsonString(Long id, String name, int nbHexagons, int nbCarbons, int nbHydrogens,
			double irregularity, String opeId, String opeName, String opeHexagons, String opeCarbons,
			String opeHydrogens, String opeIrregularity) {

		return "{\"idBenzenoid\": " + id + ", \"label\": \"" + name + "\", \"nbHexagons\": " + nbHexagons
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

		if (!"".equals(opeId))
			json.append("\"idBenzenoid\": \"").append(opeId).append(" ").append(id).append("\", ");
		else
			json.append("\"idBenzenoid\": \"\", ");

		if (!"".equals(opeName))
			json.append("\"label\": \"").append(opeName).append(" ").append(name).append("\", ");
		else
			json.append("\"label\": \"\", ");

		if (!"".equals(opeHexagons))
			json.append("\"nbHexagons\": \"").append(opeHexagons).append(" ").append(nbHexagons).append("\", ");
		else
			json.append("\"nbHexagons\": \"\", ");

		if (!"".equals(opeCarbons))
			json.append("\"nbCarbons\": \"").append(opeCarbons).append(" ").append(nbCarbons).append("\", ");
		else
			json.append("\"nbCarbons\": \"\", ");

		if (!"".equals(opeHydrogens))
			json.append("\"nbHydrogens\": \"").append(opeHydrogens).append(" ").append(nbHydrogens).append("\", ");
		else
			json.append("\"nbHydrogens\": \"\", ");

		if (!"".equals(opeIrregularity))
			json.append("\"irregularity\": \"").append(opeIrregularity).append(" ").append(irregularity).append("\", ");
		else
			json.append("\"irregularity\": \"\", ");

		// new criterions

		if (!"".equals(opeFrequency))
			json.append("\"frequency\": \"").append(opeFrequency).append(" ").append(frequency).append("\", ");
		else
			json.append("\"frequency\": \"\", ");

		if (!"".equals(opeIntensity))
			json.append("\"intensity\": \"").append(opeIntensity).append(" ").append(intensity).append("\"");
		else
			json.append("\"intensity\": \"\"");

		json.append("}");

		System.out.println(json);
		return json.toString();
	}
}
