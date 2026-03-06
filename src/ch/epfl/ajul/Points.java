package ch.epfl.ajul;

public final class Points {

    public static final int FULL_ROW_BONUS_POINTS = 2;
    public static final int FULL_COLUMN_BONUS_POINTS = 7;
    public static final int FULL_COLOR_BONUS_POINTS = 10;

    private static final int FLOOR_PENALTY = 0x3322211;
    private static final int TOTAL_FLOOR_PENALTY = 0xEB864210;


    public static int newWallTilePoints(int hGroupSize, int vGroupSize) {
        if (vGroupSize == 1) { return hGroupSize; }
        else if (hGroupSize == 1) { return vGroupSize; }
        else return hGroupSize + vGroupSize;
    }

    public static int floorPenalty(int tileIndex) {
        return (FLOOR_PENALTY >>> tileIndex) & 0xF;
    }

    public static int totalFloorPenalty(int tilesCount) {
        return (TOTAL_FLOOR_PENALTY >>> tilesCount) & 0xF;
    }
}
