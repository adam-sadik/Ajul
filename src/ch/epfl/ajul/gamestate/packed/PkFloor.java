package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;

public final class PkFloor {

    public static final int EMPTY = 0;

    public static int size(int pkFloor){
        return 0b111 & pkFloor;
    }

    public static TileKind tileAt(int pkFloor, int i){
        int tileKindIndex = (pkFloor >>> (3 * (i + 1))) & 0b111;
        return TileKind.ALL.get(tileKindIndex);
    }

    public static int withAddedTiles(int pkFloor, int pkTileSet) {
        int currentFloor = pkFloor;
        for (TileKind kind : TileKind.ALL) {
            int count = PkTileSet.countOf(pkTileSet, kind);
            for (int k = 0; k < count; k++) {
                int currentSize = size(currentFloor);
                if (currentSize < 7) {
                    int shift = 3 * (currentSize + 1);
                    currentFloor = currentFloor | (kind.index() << shift);
                    currentFloor = (currentFloor & ~0b111) | (currentSize + 1);
                } else if (kind == TileKind.FIRST_PLAYER_MARKER) {
                    int shift = 3 * 7;
                    currentFloor = currentFloor & ~(0b111 << shift);
                    currentFloor = currentFloor | (kind.index() << shift);
                }
            }
        }
        return currentFloor;
    }

    public static boolean containsFirstPlayerMarker(int pkFloor) {
        int floorSize = size(pkFloor);
        for (int i = 0; i < floorSize; i++) {
            if (tileAt(pkFloor, i) == TileKind.FIRST_PLAYER_MARKER) {
                return true;
            }
        }
        return false;
    }


}
