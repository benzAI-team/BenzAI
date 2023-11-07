import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.impl.UndirectedNodeInducedGraphVarImpl;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;

public class TestNbCC {
    public static void main(String[] args) {
        Model model = new Model();
        UndirectedGraph GUB = new UndirectedGraph(model, 3,
                SetType.LINKED_LIST, false);
        UndirectedGraph GLB = new UndirectedGraph(model, 3,
                SetType.LINKED_LIST, false);
        GLB.addNode(0);// GLB.addNode(2); //GLB.addNode(1);
        GUB.addNode(0); GUB.addNode(1); GUB.addNode(2);
        GUB.addEdge(0,1); GUB.addEdge(1,2);

        System.out.println(GLB);
        System.out.println(GUB);
        UndirectedNodeInducedGraphVarImpl graphVar = model.nodeInducedGraphVar("G", GLB, GUB);
        //UndirectedGraphVar graphVar = model.graphVar("G", GLB, GUB);
        IntVar nbCC = model.intVar("nbCC", 2);
        model.nbConnectedComponents(graphVar, nbCC).post();
        Solver solver = model.getSolver();
        while(solver.solve()){
            System.out.println(nbCC);
            System.out.println(graphVar);
        }
    }
}
