import generator.GeneralModel;
import generator.SolverResults;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;

public class TestGeneration {
    public static void main(String[] args) {
        test5hexagons();

    }

    private static void test5hexagons() {
        ModelPropertySet modelPropertySet  = new ModelPropertySet();
        modelPropertySet.getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", "=", 5));
        int found = runGeneration(modelPropertySet);
        printResult("Generate molecules with 5 hexagons", 22, found);

    }

    private static int runGeneration(ModelPropertySet modelPropertySet){
        GeneralModel model = new GeneralModel(modelPropertySet);
        model.setInTestMode(true);
        SolverResults results = model.solve();
        return results.getNbTotalSolutions();
    }
    private static void printResult(String title, int expected, int found) {
        System.out.print(title + " : expecting : " + expected + " found : " + found + "-> ");
        if (found == expected)
            System.out.println("OK");
        else
            System.out.println("ERROR");
    }
}
