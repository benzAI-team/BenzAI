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
        String solo, String duo, String trio, String quartet, String kekule, String catacondensed, String coronenoid, String coronoid, String symmetry,
        String opeId, String opeLabel, String opeHexagons, String opeCarbons, String opeHydrogens, String opeIrregularity, String opeFrequency, String opeIntensity, String opeInchi,
        String opeSolo, String opeDuo, String opeTrio, String opeQuartet, String opeKekule, String opeCatacondensed, String opeCoronenoid, String opeCoronoid, String opeSymmetry) {
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
      
		if (!"".equals(opeSolo))
			json.add("solo",opeSolo+" "+solo);
      
		if (!"".equals(opeDuo))
			json.add("duo",opeDuo+" "+duo);
      
		if (!"".equals(opeTrio))
			json.add("trio",opeTrio+" "+trio);
      
		if (!"".equals(opeQuartet))
			json.add("quartet",opeQuartet+" "+quartet);
      
		if (!"".equals(opeKekule))
			json.add("kekule",opeKekule+" "+kekule);

		if (!"".equals(opeCatacondensed))
			json.add("catacondensed",opeCatacondensed+" "+catacondensed);

		if (!"".equals(opeCoronenoid))
			json.add("coronenoid",opeCoronenoid+" "+coronenoid);

		if (!"".equals(opeCoronoid))
			json.add("coronoid",opeCoronoid+" "+coronoid);
      
		if (!"".equals(opeSymmetry))
			json.add("symmetry",opeSymmetry+" "+symmetry);

		return json.build().toString();
	}
}
