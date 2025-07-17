package database.models;

import benzenoid.Benzenoid;

import java.io.IOException;
import java.util.Map;

public class PropertiesEntry extends BenzenoidEntry {

    private final double homo;
    private final double lumo;
    private final double moment;
    private final int clarNumber;

    /*
     * Constructor
     */

    public PropertiesEntry(int idMolecule, String moleculeLabel, int nbHexagons, int nbCarbons, int nbHydrogens,
                          double irregularity, String inchi, String graphFile, double homo, double lumo, double moment, int clarNumber) {
        super(idMolecule, moleculeLabel, nbHexagons, nbCarbons, nbHydrogens, irregularity, inchi, graphFile);
        this.homo = homo;
        this.lumo = lumo;
        this.moment = moment;
        this.clarNumber = clarNumber;
    }

    /*
     * Getters
     */



    public double getHomo() {
        return homo;
    }

    public double getLumo() {
        return lumo;
    }

    public double getMoment() {
        return moment;
    }
    public int getClarNumber() {
        return clarNumber;
    }


    /*
     * Class methods
     */

    @SuppressWarnings("rawtypes")
    public static PropertiesEntry buildQueryContent(Map result) {

        int idMolecule = (int) ((double) result.get("idBenzenoid"));
        String inchi = (String) result.get("inchi");
        String label = (String) result.get("label");
        int nbHexagons = (int) ((double) result.get("nbHexagons"));
        int nbCarbons = (int) ((double) result.get("nbCarbons"));
        int nbHydrogens = (int) ((double) result.get("nbHydrogens"));
        double irregularity = (double) result.get("irregularity");
        String graphFile = (String) result.get("graphFile");

        double homo = (double) result.get("homo");
        double lumo = (double) result.get("lumo");
        double moment = (double) result.get("moment");
        int clarNumber = (int) ((double) result.get("clarNumber"));

//        // Récupérer le nom
//        String service = "find_propertiesa/";
//        String json = "{\"idBenzenoid\": \"= " + idMolecule + "\"}";
//        List<Map> results = null;
//        try {
//            results = Post.post(service, json);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return new PropertiesEntry(idMolecule, label, nbHexagons, nbCarbons, nbHydrogens, irregularity, inchi,	graphFile, homo, lumo, moment, clarNumber);
    }

    public Benzenoid buildMolecule() throws IOException {
        System.out.println("Build ....");
        Benzenoid b = super.buildMolecule();
        b.setClarNumber(this.clarNumber);
        b.setHomo(this.homo);
        b.setLumo(this.lumo);
        b.setMoment(this.moment);
        return b;
    }
}
