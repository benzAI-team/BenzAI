package utils;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.RealVar;

public class Ibex {

    public static void main(String[] args) {
        Model model = new Model("Test0");

        int nbPoints = 4;
        double p = 0.01d;
        RealVar [] xs = new RealVar[nbPoints];
        RealVar [] ys = new RealVar[nbPoints];
        RealVar [] zs = new RealVar[nbPoints];
        int i;
        for(i = 0; i < nbPoints ; i++) {
            xs[i] = model.realVar("x" + i, -1, 1, p);
            ys[i] = model.realVar("y" + i, -1, 1, p);
            zs[i] = model.realVar("z" + i, -1, 1, p);
        }

        int j;
        for(i = 0; i < nbPoints - 1 ; i++)
            for(j = i + 1; j < nbPoints; j++) {
                //dx^2+dy^2+dz^2=1
                xs[i].sub(xs[j]).mul(xs[i].sub(xs[j]))
                .add(ys[i].sub(ys[j]).mul(ys[i].sub(ys[j])))
                .add(zs[i].sub(zs[j]).mul(zs[i].sub(zs[j])))
                .eq(1).equation().post();
            }

        // Pour éviter les symétries par translation et rotation :
        // on fixe le point 0 ...
        xs[0].eq(0).equation().post();
        ys[0].eq(0).equation().post();
        zs[0].eq(0).equation().post();
        // ... on oblige le point 1 à être sur l'axe des x positifs...
        xs[1].ge(0).equation().post();
        ys[1].eq(0).equation().post();
        zs[1].eq(0).equation().post();
        // ... on oblige le point 2 à être dans le plan xy positif.
        xs[2].ge(0).equation().post();
        ys[2].ge(0).equation().post();
        zs[2].eq(0).equation().post();
        // et on oblige le point 3 à être au dessus du plan xy
        zs[3].ge(0).equation().post();

        Solver solver = model.getSolver();
        while(solver.solve()) {
            Solution solution = new Solution(model);
            solution.record();
            System.out.println(solution);
        }
    }

}

