package generator.properties.model;

import java.util.ArrayList;

import constraints.Permutation;
import utils.Coords;

public class SymmetryHandler {
    /***
     * checks if a molecule has a symmetry according to permutation p
     * @param moleculeIndices : indices of the hexagons
     * @param p : permutation
     * @param couronnes : nb of crowns in the embedding coronenoid
     * @return true iff it has this symmetry
     */
    public static boolean hasSymmetry(ArrayList<Integer> moleculeIndices, Permutation p, int couronnes) {
        for (Integer index : moleculeIndices) {
            Coords image = p.from(new Coords(index % (2 * couronnes - 1), index / (2 * couronnes - 1)));
            System.out.println("center : " + p.getCenter()+ " : " + index + "(" + index % (2 * couronnes - 1) + "," + index / (2 * couronnes - 1) + ")->" + (image.getX() + image.getY() * (2 * couronnes - 1)) + "" + image);
            if (!moleculeIndices.contains(image.getX() + image.getY() * (2 * couronnes - 1)))
                return false;
        }
        //System.out.println("ok");
        return true;
    }

    public static boolean hasEdgeAxisSymmetry(ArrayList<Integer> moleculeIndices, int couronnes) {
        for (int x = -couronnes; x < 3 * couronnes - 1; x++)
            for (int direction = 0; direction < 3; direction++)
                if (hasSymmetry(moleculeIndices, new Permutation(new Coords(x, x), direction) {
                    @Override
                    public Coords from(Coords point) {
                        return edgeAxis(point);
                    }
                }, couronnes))
                    return true;
        for (int x = -couronnes; x < 3 * couronnes - 1; x++)
            for (int direction = 0; direction < 3; direction++)
                if (hasSymmetry(moleculeIndices, new Permutation(new Coords(x, x + 1), direction) {
                    @Override
                    public Coords from(Coords point) {
                        return edgeAxis(point);
                    }
                }, couronnes))
                    return true;
        return false;
    }

    public static boolean hasHexagonAxisSymmetry(ArrayList<Integer> moleculeIndices, int couronnes) {
        for (int x = 0; x < 2 * couronnes - 1; x++)
            for (int direction = 0; direction < 3; direction++)
                if (hasSymmetry(moleculeIndices, new Permutation(new Coords(x, 0), direction) {
                    @Override
                    public Coords from(Coords point) {
                        return hexAxis(point);
                    }
                }, couronnes))
                    return true;
        for (int x = 0; x < 2 * couronnes - 1; x++)
            for (int direction = 0; direction < 3; direction++)
                if (hasSymmetry(moleculeIndices, new Permutation(new Coords(0, x), direction) {
                    @Override
                    public Coords from(Coords point) {
                        return hexAxis(point);
                    }
                }, couronnes))
                    return true;
        return false;
    }

    public static boolean hasRot60Symmetry(ArrayList<Integer> moleculeIndices, int couronnes) {
        for (int x = 0; x < 2 * couronnes - 1; x++)
            for (int y = 0; y < 2 * couronnes - 1; y++)
                if (hasSymmetry(moleculeIndices, new Permutation(new Coords(x, y), 1) {
                    @Override
                    public Coords from(Coords point) {
                        return rot(point);
                    }
                }, couronnes))
                    return true;
        return false;
    }

    public static boolean hasRot120Symmetry(ArrayList<Integer> moleculeIndices, int couronnes) {
        for (int x = 0; x < 2 * couronnes - 1; x++)
            for (int y = 0; y < 2 * couronnes - 1; y++)
                if (hasSymmetry(moleculeIndices, new Permutation(new Coords(x, y), 2) {
                    @Override
                    public Coords from(Coords point) {
                        return rot(point);
                    }
                }, couronnes))
                    return true;
        return false;
    }

    public static boolean hasRot180Symmetry(ArrayList<Integer> moleculeIndices, int couronnes) {
        for (int x = 0; x < 2 * couronnes - 1; x++)
            for (int y = 0; y < 2 * couronnes - 1; y++)
                if (hasSymmetry(moleculeIndices, new Permutation(new Coords(x, y), 3) {
                    @Override
                    public Coords from(Coords point) {
                        return rot(point);
                    }
                }, couronnes))
                    return true;
        return false;
    }



    public static boolean hasRot120VertexSymmetry(ArrayList<Integer> moleculeIndices, int couronnes) {
        for (int x = 0; x < 2 * couronnes - 1; x++)
            for (int y = 0; y < 2 * couronnes - 1; y++)
                if (hasSymmetry(moleculeIndices, new Permutation(new Coords(x, y)) {
                    @Override
                    public Coords from(Coords point) {
                        return rot120topVertex(point);
                    }
                }, couronnes))
                    return true;
        for (int x = 0; x < 2 * couronnes - 1; x++)
            for (int y = 0; y < 2 * couronnes - 1; y++)
                if (hasSymmetry(moleculeIndices, new Permutation(new Coords(x, y)) {
                    @Override
                    public Coords from(Coords point) {
                        return rot120bottomVertex(point);
                    }
                }, couronnes))
                    return true;
        return false;
    }

    public static boolean hasRot180EdgeSymmetry(ArrayList<Integer> moleculeIndices, int couronnes) {
        for (int x = 0; x < 2 * couronnes - 1; x++)
            for (int y = 0; y < 2 * couronnes - 1; y++)
                if (hasSymmetry(moleculeIndices, new Permutation(new Coords(x, y)) {
                    @Override
                    public Coords from(Coords point) {
                        return rot180edge(point);
                    }
                }, couronnes))
                    return true;
        return false;
    }

}
