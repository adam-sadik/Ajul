package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import java.util.StringJoiner;

public final class PkPatterns {

    public static final int EMPTY = 0;

    public static int size(int pkPatterns, TileDestination.Pattern line) {
        return (pkPatterns >>> (6 * line.index())) & 0b111;
    }

    public static TileKind.Colored color(int pkPatterns, TileDestination.Pattern line) {
        assert size(pkPatterns, line) > 0;
        int pkColor = (pkPatterns >>> ((6 * line.index())) + 3) & 0b111;
        return TileKind.Colored.values()[pkColor];
    }

    public static boolean isFull(int pkPatterns, TileDestination.Pattern line) {
        return size(pkPatterns, line) == line.capacity();
    }

    public static boolean canContain(int pkPatterns, TileDestination.Pattern line, TileKind.Colored color){
        return size(pkPatterns, line) == 0 || color(pkPatterns, line) == color;
    }

    public static int withAddedTiles(int pkPatterns, TileDestination.Pattern line, int tileCount, TileKind.Colored color){
        assert tileCount > 0;
        assert canContain(pkPatterns, line, color);
        assert size(pkPatterns, line) + tileCount <= line.capacity();
        int newSize = size(pkPatterns, line) + tileCount;
        int newColorIndex = color.ordinal();
        int newBlock = newSize | (newColorIndex << 3);
        int clearedPatterns = pkPatterns & ~(0b111111 << 6 * line.index());
        return clearedPatterns | (newBlock << 6 * line.index());
    }

    public static int withEmptyLine(int pkPatterns, TileDestination.Pattern line){
        return pkPatterns & ~(0b111111 << (6 * line.index()));
    }

    public static int asPkTileSet(int pkPatterns) {
        int packedTileSet = PkTileSet.EMPTY;
        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            int lineSize = size(pkPatterns, line);
            if (lineSize > 0) {
                TileKind.Colored lineColor = color(pkPatterns, line);
                int lineSet = PkTileSet.of(lineSize, lineColor);
                packedTileSet = PkTileSet.union(packedTileSet, lineSet);
            }
        }
        return packedTileSet;
    }

    public static String toString(int pkPatterns) {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            int currentSize = size(pkPatterns, line);
            int emptySpots = line.capacity() - currentSize;
            String lineRepresentation = "";

            if (currentSize > 0) {
                TileKind.Colored color = color(pkPatterns, line);
                lineRepresentation += color.name().repeat(currentSize);
            }
            lineRepresentation += ".".repeat(emptySpots);
            sj.add(lineRepresentation);
        }
        return sj.toString();
    }

}
