import java.util.Random;

public class Shape {

    protected enum Tetrominoe {
        NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape
    }

    private Tetrominoe pieceShape;
    private int[][] coords;

    public Shape() {
        coords = new int[4][2];
        setShape(Tetrominoe.NoShape);
    }

    int[][][] coordsTable = {
        {{0,0},{0,0},{0,0},{0,0}},
        {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},
        {{0, -1}, {0, 0}, {1, 0}, {1, 1}},
        {{0, -1}, {0, 0}, {0, 1}, {0, 2}},
        {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},
        {{0, 0}, {1, 0}, {0, 1}, {1, 1}},
        {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},
        {{1, -1}, {0, -1}, {0, 0}, {0, 1}}
    };

    void setShape(Tetrominoe shape) {
    int idx = shape.ordinal();
    for (int i = 0; i < 4; i++) {
        coords[i][0] = coordsTable[idx][i][0];
        coords[i][1] = coordsTable[idx][i][1];
    }
    pieceShape = shape;
}

    int getX(int index) {
        return coords[index][0];
    }

    int getY(int index) {
        return coords[index][1];
    }

    public Tetrominoe getShape() {
    return pieceShape;
}

    private void setX(int index, int x) {
    coords[index][0] = x;
}

    private void setY(int index, int y) {
    coords[index][1] = y;
}

    void setRandomShape() {
        Random random = new Random();
        int randomIndex = random.nextInt(7) +1;
        Tetrominoe[] values = Tetrominoe.values();
        setShape(values[randomIndex]);
    }

    public int minX() {
        int m = coords[0][0];
        for (int i = 1; i < 4; i++) {
            m = Math.min(m, coords[i][0]);
        }
        return m;
    }
   
    int minY() {
        int m = coords[0][1];
        for (int i = 1; i < 4; i++) {
            m = Math.min(m, coords[i][1]);
        }
        return m;
    }

    Shape rotateLeft() {
        if (pieceShape == Tetrominoe.SquareShape) {
            return this;
        }
        Shape result = new Shape();
        result.pieceShape = pieceShape;
        for (int i = 0; i < 4; i++) {
            result.setX(i, getY(i));
            result.setY(i,-getX(i));
        }
        return result;
    }

    Shape rotateRight() {
    if (pieceShape == Tetrominoe.SquareShape) {
        return this;
    }
    Shape result = new Shape();
    result.pieceShape = pieceShape;
    for (int i = 0; i < 4; i++) {
        result.setX(i, -getY(i));
        result.setY(i, getX(i));
    }
    return result;
}

}