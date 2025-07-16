import constraints.ExistencePatternConstraint;
import generator.GeneralModel;
import generator.SolverResults;
import generator.patterns.Pattern;
import generator.patterns.PatternFileImport;
import generator.properties.model.ModelProperty;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.*;
import view.patterns.PatternGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public enum TestGeneration {
    ;
    private static long date;
    public static void main(String[] args) {
        String diagnostic =
                test5hexagons()
                        + testCarbonsInf24()
                        + testHydrogensInf12()
                        + test6hexagonsCatacondensed()
                        + testCoronenoid_inf3()
                        + testCoronoid_hex9()
                        + testDiameter3()
                        + testIrregularitySup60()
                        + testRectangle_inf5Xinf5()
                        + testRhombusInf5()
                        + testRot60()
                        + testPattern3();
                        //+ testKekule()
                        //+ testConcealed();
        System.out.println(diagnostic);
    }

    /***
     * TESTS
     */

    private static String testDiameter3() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("diameter").addExpression(new BinaryNumericalExpression("diameter", "=", 3));
        int found = runGeneration(modelPropertySet);
        return diagnostic("Generate molecules with diameter = 3", 102, found);
    }
    private static String testIrregularitySup60() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", "=", 5));
        modelPropertySet.getById("irregularity").addExpression(new IrregularityExpression("irregularity", "XI", ">", 60));
        int found = runGeneration(modelPropertySet);
        return diagnostic("Generate molecules with hex=5, Xi>0.6", 10, found);
    }
    private static String testPattern3() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", "=", 5));
        Pattern pattern = null;
        try {
            pattern = PatternFileImport.importPattern(new File("./pattern3test"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Pattern> patterns = new ArrayList<>();
        patterns.add(pattern);
        
        PatternGroup patternGroup = new PatternGroup(patterns.get(0),2);
        ((ModelProperty)modelPropertySet.getById("pattern")).setConstraint(new ExistencePatternConstraint(patternGroup));
        modelPropertySet.getById("pattern").addExpression(new PatternExpression("SINGLE_PATTERN"));
        int found = runGeneration(modelPropertySet);
        return diagnostic("Generate molecules with triangle3 pattern and #hex=5", 10, found);
    }

    private static String test5hexagons() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", "=", 5));
        int found = runGeneration(modelPropertySet);
        return diagnostic("Generate molecules with 5 hexagons", 22, found);
    }

    private static String test6hexagonsCatacondensed() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", "=", 6));
        modelPropertySet.getById("catacondensed").addExpression(new SubjectExpression("catacondensed"));
        int found = runGeneration(modelPropertySet);
        return diagnostic("Generate molecules with 6 hexagons catacondensed", 36, found);
    }

    private static String testRectangle_inf5Xinf5() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("rectangle").addExpression((new RectangleExpression("rectangle", "<=", 5, "<=", 5)));
        int found = runGeneration(modelPropertySet);
        return diagnostic("Generate rectangles h<=5 X w<=5 : ", 15, found);
    }
    private static String testRhombusInf5() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("rhombus").addExpression((new RhombusExpression("rhombus", "<=", 5)));
        int found = runGeneration(modelPropertySet);
        return diagnostic("Generate rhombus size<=5 : ", 5, found);
    }


    private static String testCoronenoid_inf3() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("coronenoid").addExpression((new BinaryNumericalExpression("coronenoid", "<=", 3)));
        int found = runGeneration(modelPropertySet);
        return diagnostic("Generate coronenoid size<=3 : ", 3, found);
    }
    private static String testCoronoid_hex9() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", "=", 9));
        modelPropertySet.getById("coronoid").addExpression((new BinaryNumericalExpression("coronoid", "=", 1)));
        int found = runGeneration(modelPropertySet);
        return diagnostic("Generate coronoid hex=9 hole=1 : ", 5, found);
    }

    private static String testCarbonsInf24() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("carbons").addExpression(new BinaryNumericalExpression("carbons", "<=", 24));
        int found = runGeneration(modelPropertySet);
        return diagnostic("Generate molecules with <=24 carbons", 56, found);
    }
    private static String testHydrogensInf12() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("hydrogens").addExpression(new BinaryNumericalExpression("hydrogens", "<=", 12));
        int found = runGeneration(modelPropertySet);
        return diagnostic("Generate molecules with <=12 hydrogens", 20, found);
    }
    private static String testRot60() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", "<=", 25));
        modelPropertySet.getById("symmetry").addExpression(new ParameterizedExpression("symmetry", "C_6h=\"(face)-60-rotation\""));
        int found = runGeneration(modelPropertySet);
        return diagnostic("Generate molecules with hex<=25 and sym=rot60", 18, found);
    }

    /***
     *  Run the generation of benzenoids according to the given property set (constraints)
     * @param modelPropertySet : constraints
     * @return the number of solution
     */
    private static int runGeneration(ModelPropertySet modelPropertySet){
        GeneralModel model = new GeneralModel(modelPropertySet);
        model.setInTestMode(true);
        date = System.currentTimeMillis();
        SolverResults results = model.solve();
        return results.getNbTotalSolutions();
    }

    /***
     *
     * @param title : name of the test
     * @param expected : expected number of solutions
     * @param found : number of solutions found
     * @return the String characterizing the diagnostic
     */
    private static String diagnostic(String title, int expected, int found) {
        long endDate = System.currentTimeMillis();
        String diagnostic = title + " : expecting : " + expected + " found : " + found + " in " + (endDate - date) + "ms -> ";
        if (found == expected)
            diagnostic = diagnostic + "OK\n";
        else {
            diagnostic = diagnostic + "ERROR\n";
            //System.exit(0);
        }
        return diagnostic;
    }
}
