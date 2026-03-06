package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;

import java.util.StringJoiner;

public final class PkWall {

    public static int EMPTY = 0;
    public static int WALL_WIDTH = 5;
    public static int WALL_HEIGHT = 5;
    public static int COLOR_MASK_A = 0b10000_01000_00100_00010_00001;
    public static int COLOR_MASK_B = 0b00001_10000_01000_00100_00010;
    public static int COLOR_MASK_C = 0b00010_00001_10000_01000_00100;
    public static int COLOR_MASK_D = 0b00100_00010_00001_10000_01000;
    public static int COLOR_MASK_E = 0b01000_00100_00010_00001_10000;

    private static int countHorizontal(int pkWall, TileDestination.Pattern line, int startCol, int step) {
        int count = 0;
        int col = startCol + step;
        while (col >= 0 && col < WALL_WIDTH && hasTileAt(pkWall, line, colorAt(line, col))) {
            count++;
            col += step;
        }
        return count;
    }

    private static int countVertical(int pkWall, int col, int startRow, int step) {
        int count = 0;
        int row = startRow + step;
        while (row >= 0 && row < WALL_HEIGHT) {
            TileDestination.Pattern currentLine = TileDestination.Pattern.ALL.get(row);
            if (!hasTileAt(pkWall, currentLine, colorAt(currentLine, col))) {
                break;
            }
            count++;
            row += step;
        }
        return count;
    }

    public static int indexOf(TileDestination.Pattern line, TileKind.Colored color) {
        return column(line, color) + line.index() * WALL_WIDTH;
    }

    public static int column(TileDestination.Pattern line, TileKind.Colored color) {
        return ((line.index() + color.index()) % WALL_WIDTH);
    }

    public static TileKind.Colored colorAt(TileDestination.Pattern line, int column) {
        return TileKind.Colored.ALL.get((line.index() * 4 + column) % WALL_HEIGHT);
    }

    public static int withTileAt(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        return pkWall | (1 << (indexOf(line, color)));
    }

    public static boolean hasTileAt(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        return ((pkWall >> (indexOf(line, color)) & 1) == 1);
    }

    public static int hGroupSize(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        int col = column(line, color);
        return 1 + countHorizontal(pkWall, line, col, 1) + countHorizontal(pkWall, line, col, -1);
    }

    public static int vGroupSize(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        int col = column(line, color);
        int row = line.index();
        return 1 + countVertical(pkWall, col, row, 1) + countVertical(pkWall, col, row, -1);
    }

    public static boolean hasFullRow(int pkWall) {
        boolean hasFullRow = false;
        for (TileDestination.Pattern line : TileDestination.Pattern.ALL){
            if (hGroupSize(pkWall, line, colorAt(line, 0)) == WALL_WIDTH) hasFullRow = true; break;
        }
        return hasFullRow;
    }

    public static boolean isRowFull(int pkWall, TileDestination.Pattern line) {
        int ROW0_MASK = 0b00000_00000_00000_00000_11111;
        return PkIntSet32.containsAll(pkWall, ROW0_MASK << line.index() * WALL_WIDTH);
    }

    public static boolean isColumnFull(int pkWall, int column) {
        int COLUMN_MASK = 0b00001_00001_00001_00001_00001;
        return PkIntSet32.containsAll(pkWall, COLUMN_MASK << column);
    }



    public static boolean isColorFull(int pkWall, TileKind.Colored color) {
        return switch (color){
            case TileKind.Colored.A -> PkIntSet32.containsAll(pkWall, COLOR_MASK_A);
            case TileKind.Colored.B -> PkIntSet32.containsAll(pkWall, COLOR_MASK_B);
            case TileKind.Colored.C -> PkIntSet32.containsAll(pkWall, COLOR_MASK_C);
            case TileKind.Colored.D -> PkIntSet32.containsAll(pkWall, COLOR_MASK_D);
            case TileKind.Colored.E -> PkIntSet32.containsAll(pkWall, COLOR_MASK_E);
        };

    }

    public static int asPkTileSet(int pkWall) {
        int countA = Integer.bitCount(pkWall & COLOR_MASK_A);
        int countB = Integer.bitCount(pkWall & COLOR_MASK_B);
        int countC = Integer.bitCount(pkWall & COLOR_MASK_C);
        int countD = Integer.bitCount(pkWall & COLOR_MASK_D);
        int countE = Integer.bitCount(pkWall & COLOR_MASK_E);

        int packedA = PkTileSet.of(countA, TileKind.Colored.A);
        int packedB = PkTileSet.of(countB, TileKind.Colored.B);
        int packedC = PkTileSet.of(countC, TileKind.Colored.C);
        int packedD = PkTileSet.of(countD, TileKind.Colored.D);
        int packedE = PkTileSet.of(countE, TileKind.Colored.E);

        int unionA_B = PkTileSet.union(packedA, packedB);
        int unionC_D = PkTileSet.union(packedC, packedD);
        int unionA_B_C_D = PkTileSet.union(unionA_B, unionC_D);

        return PkTileSet.union(unionA_B_C_D, packedE);
    }

    public static String toString(int pkWall){
        StringJoiner joiner = new StringJoiner(", ", "[", "]");

        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < WALL_WIDTH; ++i) {
                TileKind.Colored color = colorAt(line, i);
                String name = color.toString();
                builder.append(hasTileAt(pkWall, line, color) ? name : name.toLowerCase());
            }
            joiner.add(builder.toString());
        }

        return joiner.toString();
    }
}
