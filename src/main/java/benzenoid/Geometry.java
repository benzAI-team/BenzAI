package benzenoid;

import utils.Triplet;

import java.util.ArrayList;
import java.util.List;

public class Geometry {

    private List<Triplet<Double, Double, Double>> carbons;
    private List<Triplet<Double, Double, Double>> hydrogens;

    public Geometry(List<Triplet<Double, Double, Double>> carbons, List<Triplet<Double, Double, Double>> hydrogens) {
        this.carbons = carbons;
        this.hydrogens = hydrogens;
    }

    public Geometry() {
        carbons = new ArrayList<>();
        hydrogens = new ArrayList<>();
    }

    public List<Triplet<Double, Double, Double>> getCarbons() {
        return carbons;
    }

    public List<Triplet<Double, Double, Double>> getHydrogens() {
        return hydrogens;
    }

    public int getNbCarbons() {
        return carbons.size();
    }

    public int getNbHydrogens() {
        return hydrogens.size();
    }

    public void addCarbon(Triplet<Double, Double, Double> carbon) {
        carbons.add(carbon);
    }

    public void addHydrogen(Triplet<Double, Double, Double> hydrogen) {
        hydrogens.add(hydrogen);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Triplet<Double, Double, Double> carbon : carbons)
            builder.append("C " + carbon.getX() + " " + carbon.getY() + carbon.getZ() + "\n");

        for (Triplet<Double, Double, Double> hydrogen : hydrogens)
            builder.append("H " + hydrogen.getX() + " " + hydrogen.getY() + hydrogen.getZ() + "\n");

        return builder.toString();
    }

}
