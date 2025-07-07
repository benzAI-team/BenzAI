package constraints;

import generator.GeneralModel;
import generator.patterns.Pattern;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import java.util.ArrayList;

public class ExclusionPatternConstraint  extends PatternConstraint {

    public ExclusionPatternConstraint (Pattern pattern) {
        super(pattern);
    }

    public void postConstraints() {
        GeneralModel generalModel = getGeneralModel();

        ArrayList<Integer[]> occurrences = getPatternOccurences().getOccurrences();

        for (int i = 0; i < occurrences.size(); i++) {
            Integer[] occurrence = occurrences.get(i);

            ArrayList<Integer> present = new ArrayList<>();
            ArrayList<Integer> absent = new ArrayList<>();

            for (Integer hexagon : getPresentHexagons()) {
                if (occurrence[hexagon] != -1)
                    present.add(occurrence[hexagon]);
            }

            for (Integer hexagon : getAbsentHexagons()) {
                if (occurrence[hexagon] != -1)
                    absent.add(occurrence[hexagon]);
            }

            BoolVar[] varClause = new BoolVar[present.size() + absent.size()];
            IntIterableRangeSet[] valClause = new IntIterableRangeSet[present.size() + absent.size()];
            int index = 0;

            for (Integer j : absent) {
                varClause[index] = generalModel.getBenzenoidVerticesBVArray(j);
                valClause[index] = new IntIterableRangeSet(1);
                index++;
            }

            for (Integer j : present) {
                varClause[index] = generalModel.getBenzenoidVerticesBVArray(j);
                valClause[index] = new IntIterableRangeSet(0);
                index++;
            }

            generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
        }
    }
}
