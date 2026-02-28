package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileKind;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyPkFloorTest {

    @Test
    void testEmptyValueIsZero() {
        assertEquals(0, PkFloor.EMPTY);
    }

    @Test
    void testSizeOnEmptyFloorIsZero() {
        assertEquals(0, PkFloor.size(PkFloor.EMPTY));
    }

    @Test
    void testSizeAfterAddingTiles() {
        int f = PkFloor.EMPTY;
        int ts = PkTileSet.of(3, TileKind.Colored.B);
        f = PkFloor.withAddedTiles(f, ts);
        assertEquals(3, PkFloor.size(f));
    }

    @Test
    void testSizeCapsAtSeven() {
        int f = PkFloor.EMPTY;
        int ts = PkTileSet.of(10, TileKind.Colored.A);
        f = PkFloor.withAddedTiles(f, ts);
        assertEquals(7, PkFloor.size(f));
    }

    @Test
    void testTileAtFirstPosition() {
        int f = PkFloor.EMPTY;
        int ts = PkTileSet.of(1, TileKind.Colored.D);
        f = PkFloor.withAddedTiles(f, ts);
        assertEquals(TileKind.Colored.D, PkFloor.tileAt(f, 0));
    }

    @Test
    void testTileAtMultiplePositionsPreservesOrder() {
        int f = PkFloor.EMPTY;
        int ts = PkTileSet.union(PkTileSet.of(2, TileKind.Colored.B), PkTileSet.of(1, TileKind.Colored.C));
        f = PkFloor.withAddedTiles(f, ts);

        assertEquals(TileKind.Colored.B, PkFloor.tileAt(f, 0));
        assertEquals(TileKind.Colored.B, PkFloor.tileAt(f, 1));
        assertEquals(TileKind.Colored.C, PkFloor.tileAt(f, 2));
    }

    @Test
    void testWithAddedTilesCumulatesProperly() {
        int f = PkFloor.EMPTY;
        f = PkFloor.withAddedTiles(f, PkTileSet.of(1, TileKind.Colored.A));
        f = PkFloor.withAddedTiles(f, PkTileSet.of(2, TileKind.Colored.E));

        assertEquals(3, PkFloor.size(f));
        assertEquals(TileKind.Colored.A, PkFloor.tileAt(f, 0));
        assertEquals(TileKind.Colored.E, PkFloor.tileAt(f, 1));
        assertEquals(TileKind.Colored.E, PkFloor.tileAt(f, 2));
    }

    @Test
    void testContainsFirstPlayerMarkerWhenEmpty() {
        assertFalse(PkFloor.containsFirstPlayerMarker(PkFloor.EMPTY));
    }

    @Test
    void testContainsFirstPlayerMarkerWhenPresent() {
        int f = PkFloor.EMPTY;
        int ts = PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER);
        f = PkFloor.withAddedTiles(f, ts);
        assertTrue(PkFloor.containsFirstPlayerMarker(f));
    }

    @Test
    void testContainsFirstPlayerMarkerWhenNotPresent() {
        int f = PkFloor.EMPTY;
        int ts = PkTileSet.of(5, TileKind.Colored.C);
        f = PkFloor.withAddedTiles(f, ts);
        assertFalse(PkFloor.containsFirstPlayerMarker(f));
    }

    @Test
    void testMarkerAddedReplacesLastTileWhenFloorIsFull() {
        int f = PkFloor.EMPTY;
        int fullSet = PkTileSet.of(7, TileKind.Colored.A);
        f = PkFloor.withAddedTiles(f, fullSet);

        int markerSet = PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER);
        f = PkFloor.withAddedTiles(f, markerSet);

        assertEquals(7, PkFloor.size(f));
        assertEquals(TileKind.FIRST_PLAYER_MARKER, PkFloor.tileAt(f, 6));
        assertEquals(TileKind.Colored.A, PkFloor.tileAt(f, 5));
    }

    @Test
    void testMarkerAddedSimultaneouslyWithExcessTilesReplacesLast() {
        int f = PkFloor.EMPTY;
        int largeSet = PkTileSet.union(PkTileSet.of(10, TileKind.Colored.B), PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));
        f = PkFloor.withAddedTiles(f, largeSet);

        assertEquals(7, PkFloor.size(f));
        assertEquals(TileKind.Colored.B, PkFloor.tileAt(f, 0));
        assertEquals(TileKind.FIRST_PLAYER_MARKER, PkFloor.tileAt(f, 6));
    }

    @Test
    void testMarkerAlwaysPushedAtTheEndDueToEnumOrder() {
        int f = PkFloor.EMPTY;
        int mixedSet = PkTileSet.union(PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER), PkTileSet.of(1, TileKind.Colored.A));
        f = PkFloor.withAddedTiles(f, mixedSet);

        assertEquals(2, PkFloor.size(f));
        assertEquals(TileKind.Colored.A, PkFloor.tileAt(f, 0));
        assertEquals(TileKind.FIRST_PLAYER_MARKER, PkFloor.tileAt(f, 1));
    }

    @Test
    void testAsPkTileSetOnEmptyFloor() {
        assertEquals(PkTileSet.EMPTY, PkFloor.asPkTileSet(PkFloor.EMPTY));
    }

    @Test
    void testAsPkTileSetWithSeveralTiles() {
        int f = PkFloor.EMPTY;
        int ts = PkTileSet.union(PkTileSet.of(2, TileKind.Colored.B), PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));
        f = PkFloor.withAddedTiles(f, ts);

        int resultTs = PkFloor.asPkTileSet(f);
        assertEquals(3, PkTileSet.size(resultTs));
        assertEquals(2, PkTileSet.countOf(resultTs, TileKind.Colored.B));
        assertEquals(1, PkTileSet.countOf(resultTs, TileKind.FIRST_PLAYER_MARKER));
    }

    @Test
    void testToStringEmpty() {
        assertEquals("[]", PkFloor.toString(PkFloor.EMPTY));
    }

    @Test
    void testToStringExampleFromInstructions() {
        int f = PkFloor.EMPTY;
        f = PkFloor.withAddedTiles(f, PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));
        f = PkFloor.withAddedTiles(f, PkTileSet.of(2, TileKind.Colored.B));

        assertEquals("[FIRST_PLAYER_MARKER, B, B]", PkFloor.toString(f));
    }

    @Test
    void testToStringFullFloor() {
        int f = PkFloor.EMPTY;
        f = PkFloor.withAddedTiles(f, PkTileSet.of(7, TileKind.Colored.D));
        assertEquals("[D, D, D, D, D, D, D]", PkFloor.toString(f));
    }

    @Test
    void testBinaryValueExampleFromInstructions() {
        int f = PkFloor.EMPTY;
        f = PkFloor.withAddedTiles(f, PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));
        f = PkFloor.withAddedTiles(f, PkTileSet.of(2, TileKind.Colored.B));

        int expectedBinary = 0b000000000000000000000001001101011;
        assertEquals(expectedBinary, f);
    }
}