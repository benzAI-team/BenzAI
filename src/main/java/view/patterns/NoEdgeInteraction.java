package view.patterns;

import generator.patterns.PatternOccurrences;
import utils.Couple;

import java.util.HashSet;
import java.util.Scanner;

public class NoEdgeInteraction extends Interaction {
    @Override
    public String getLabel () {
        return "no edge ";
    }

    @Override
    public boolean interact (PatternOccurrences patternOccurrences1, PatternOccurrences patternOccurrences2, int i, int j ) {
        if ((i < patternOccurrences1.size()) && (j < patternOccurrences2.size())) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Test "+i + " " + j);
            System.out.print("Edge 1:");
            for (Couple<Integer,Integer> v : patternOccurrences1.getAllEdgeCoords().get(i)) System.out.print(" "+v.getX() +"," + v.getY());
            System.out.println();
            System.out.print("Edge 2:");
            for (Couple<Integer,Integer> v : patternOccurrences2.getAllEdgeCoords().get(j)) System.out.print(" "+v.getX() +","+ v.getY());
            System.out.println();

            boolean empty = true;
            for (int a = 0; (a < patternOccurrences1.getAllEdgeCoords().get(i).size()) && (empty); a++) {
                Couple<Integer,Integer> coord1 = patternOccurrences1.getAllEdgeCoords().get(i).get(a);
                for (int b = 0; (b < patternOccurrences1.getAllEdgeCoords().get(j).size()) && (empty); b++) {
                    Couple<Integer, Integer> coord2 = patternOccurrences1.getAllEdgeCoords().get(j).get(b);
                    empty = (coord1.getX() != coord2.getX()) || (coord1.getY() != coord2.getY());
                }

            }
            System.out.println("Empty "+empty);

            if (empty) {
                System.out.println(false);
//                scanner.nextLine();
                return false;
            }
            else {
                // we check if the two pattern occurrences share positive hexagons that are neighbors of edges hexagons
                HashSet<Integer> intersection2 = new HashSet<>(patternOccurrences1.getAllEdgePositiveNeighborHexagons().get(i));
                intersection2.retainAll(patternOccurrences2.getAllEdgePositiveNeighborHexagons().get(j));

                System.out.print("Neig 1:");
                for (int v : patternOccurrences1.getAllEdgePositiveNeighborHexagons().get(i)) System.out.print(" "+v);
                System.out.println();
                System.out.print("Neig 2:");
                for (int v : patternOccurrences2.getAllEdgePositiveNeighborHexagons().get(j)) System.out.print(" "+v);
                System.out.println();

                System.out.print("Inter:");
                for (int v : intersection2) System.out.print(" "+v);
                System.out.println();

                System.out.println(!intersection2.isEmpty());
//                scanner.nextLine();
                return !intersection2.isEmpty();
            }
        }
        else {
            System.out.println(false);
            return false;
        }
    }

    @Override
    public int getType() {
        return 1;
    }
}
