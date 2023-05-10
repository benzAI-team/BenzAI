package utils;

public enum HexNeighborhood {
    UPRIGHT(0,0, -1), RIGHT(1, 1, 0), DOWNRIGHT(2, 1,1), DOWNLEFT(3, 0,1), LEFT(4, -1,0), UPLEFT(5,-1,-1);
    private final int index;
    private final int deltax;
    private final int deltay;
    HexNeighborhood(int index, int deltax, int deltay){
        this.index = index;
        this.deltax = deltax;
        this.deltay = deltay;
    }

    public int dx(){
        return deltax;
    }

    public int dy(){
        return deltay;
    }

    public int getIndex() {
        return index;
    }
}
