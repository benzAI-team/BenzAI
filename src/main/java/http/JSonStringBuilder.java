package http;
import jakarta.json.*;


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

	public static String buildNewJsonString(String id, String label, String nbHexagons, String nbCarbons, String nbHydrogens, String irregularity, String frequency, String intensity, String inchi,
        String opeId, String opeLabel, String opeHexagons, String opeCarbons, String opeHydrogens, String opeIrregularity, String opeFrequency, String opeIntensity, String opeInchi) {
    JsonObjectBuilder json = Json.createObjectBuilder();

		if (!"".equals(opeId))
			json.add("idBenzenoid",opeId+" "+id);

		if (!"".equals(opeLabel))
			json.add("label",opeLabel+" "+label);

		if (!"".equals(opeHexagons))
			json.add("nbHexagons",opeHexagons+" "+nbHexagons);

		if (!"".equals(opeCarbons))
			json.add("nbCarbons",opeCarbons+" "+nbCarbons);

		if (!"".equals(opeHydrogens))
			json.add("nbHydrogens",opeHydrogens+" "+nbHydrogens);

		if (!"".equals(opeIrregularity))
			json.add("irregularity",opeIrregularity+" "+irregularity);

		if (!"".equals(opeFrequency))
			json.add("frequency",opeFrequency+" "+frequency);

		if (!"".equals(opeIntensity))
			json.add("intensity",opeIntensity+" "+intensity);

		if (!"".equals(opeInchi))
			json.add("inchi",opeInchi+" "+inchi);

		return json.build().toString();
	}
}
