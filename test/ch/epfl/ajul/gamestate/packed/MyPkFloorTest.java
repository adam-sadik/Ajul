package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileKind;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyPkFloorTest {

    @Test
    void testEmptyValueIsZero() {
        Assertions.assertEquals(0, PkFloor.EMPTY);
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

    @Test
    void emptyConstantsAndSizeWork() {
        assertEquals(0, PkFloor.EMPTY);
        assertEquals(0, PkFloor.size(PkFloor.EMPTY));
        assertFalse(PkFloor.containsFirstPlayerMarker(PkFloor.EMPTY));
        assertEquals("[]", PkFloor.toString(PkFloor.EMPTY));
        assertEquals(PkTileSet.EMPTY, PkFloor.asPkTileSet(PkFloor.EMPTY));
    }

    @Test
    void withAddedTilesNormalAdditionWorks() {
        int floor = PkFloor.EMPTY;
// Ajout de 2 tuiles B et 1 tuile D
        int tilesToAdd = PkTileSet.union(PkTileSet.of(2, TileKind.Colored.B), PkTileSet.of(1, TileKind.Colored.D));
        floor = PkFloor.withAddedTiles(floor, tilesToAdd);

        assertEquals(3, PkFloor.size(floor));
        assertEquals(TileKind.Colored.B, PkFloor.tileAt(floor, 0));
        assertEquals(TileKind.Colored.B, PkFloor.tileAt(floor, 1));
        assertEquals(TileKind.Colored.D, PkFloor.tileAt(floor, 2));
    }

    @Test
    void withAddedTilesOverflowIgnoresExcess() {
// On remplit la ligne plancher avec 7 tuiles 'A'
        int floor = PkFloor.withAddedTiles(PkFloor.EMPTY, PkTileSet.of(7, TileKind.Colored.A));
        assertEquals(7, PkFloor.size(floor));

// On tente d'ajouter 3 tuiles 'C' supplémentaires
        int newFloor = PkFloor.withAddedTiles(floor, PkTileSet.of(3, TileKind.Colored.C));

// La taille doit rester de 7, et la dernière tuile (index 6) doit TOUJOURS être un 'A'
        assertEquals(7, PkFloor.size(newFloor));
        assertEquals(TileKind.Colored.A, PkFloor.tileAt(newFloor, 6));
    }

    @Test
    void withAddedTilesOverflowForcesFirstPlayerMarker() {
// Ligne plancher remplie de 7 tuiles 'B'
        int floor = PkFloor.withAddedTiles(PkFloor.EMPTY, PkTileSet.of(7, TileKind.Colored.B));

// On tente d'ajouter 2 tuiles 'C' ET le marqueur 1er joueur
        int tilesToAdd = PkTileSet.union(PkTileSet.of(2, TileKind.Colored.C), PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));
        int newFloor = PkFloor.withAddedTiles(floor, tilesToAdd);

// La taille reste 7. Les 'C' ont été ignorés, mais le marqueur a remplacé la dernière tuile !
        assertEquals(7, PkFloor.size(newFloor));
        assertEquals(TileKind.Colored.B, PkFloor.tileAt(newFloor, 5)); // L'avant-dernière est intacte
        assertEquals(TileKind.FIRST_PLAYER_MARKER, PkFloor.tileAt(newFloor, 6)); // La dernière a été écrasée
        assertTrue(PkFloor.containsFirstPlayerMarker(newFloor));
    }

    @Test
    void containsFirstPlayerMarkerDetectsProperly() {
        int floor = PkFloor.withAddedTiles(PkFloor.EMPTY, PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));
        assertTrue(PkFloor.containsFirstPlayerMarker(floor));

        int floor2 = PkFloor.withAddedTiles(PkFloor.EMPTY, PkTileSet.of(3, TileKind.Colored.E));
        assertFalse(PkFloor.containsFirstPlayerMarker(floor2));
    }

    @Test
    void asPkTileSetSymmetryTest() {
        int floor = PkFloor.EMPTY;
        int originalSet = PkTileSet.union(PkTileSet.of(3, TileKind.Colored.A), PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));

        floor = PkFloor.withAddedTiles(floor, originalSet);
        int generatedSet = PkFloor.asPkTileSet(floor);

// L'ensemble empaqueté généré depuis la ligne plancher doit être strictement égal à celui qu'on y a inséré
        assertEquals(originalSet, generatedSet);
    }

    @Test
    void toStringFormatsCorrectly() {
        int floor = PkFloor.EMPTY;
        floor = PkFloor.withAddedTiles(floor, PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));
        floor = PkFloor.withAddedTiles(floor, PkTileSet.union(PkTileSet.of(1, TileKind.Colored.B), PkTileSet.of(1, TileKind.Colored.A)));


// Rappel : withAddedTiles insère par ordre des valeurs de l'enum (B d'abord, puis le marqueur)
        assertEquals("[FIRST_PLAYER_MARKER, A, B]", PkFloor.toString(floor));
    }

    @Test
    void sizeTrivialTest() {
// 3 tuiles -> taille = 011 en binaire (3 en décimal)
// Les autres bits n'ont pas d'importance pour la taille
        int pkFloor = 0b101_010_001_011;
        assertEquals(3, PkFloor.size(pkFloor));
    }

    @Test
    void tileAtTrivialTest() {
// Taille = 011 (3)
// Tuile 0 = 001 (B, index 1)
// Tuile 1 = 010 (C, index 2)
// Tuile 2 = 101 (FIRST_PLAYER_MARKER, index 5)
        int pkFloor = 0b101_010_001_011;

        assertEquals(TileKind.Colored.B, PkFloor.tileAt(pkFloor, 0));
        assertEquals(TileKind.Colored.C, PkFloor.tileAt(pkFloor, 1));
        assertEquals(TileKind.FIRST_PLAYER_MARKER, PkFloor.tileAt(pkFloor, 2));
    }

    @Test
    void containsFirstPlayerMarkerTrivialTest() {
// Contient le marqueur à la 3ème position (101)
        int pkFloorWithMarker = 0b101_010_001_011;
        assertTrue(PkFloor.containsFirstPlayerMarker(pkFloorWithMarker));

// Ne contient que des tuiles B (001) et C (010), taille 2
        int pkFloorWithoutMarker = 0b010_001_010;
        assertFalse(PkFloor.containsFirstPlayerMarker(pkFloorWithoutMarker));
    }

    @Test
    void asPkTileSetTrivialTest() {
// Ligne contenant : 1 B, 1 C, 1 Marqueur (taille 3)
        int pkFloor = 0b101_010_001_011;

// On attend un PkTileSet avec 1 B, 1 C et le bit du marqueur allumé.
// B est à l'offset 6, C est à l'offset 12, le marqueur est le 31ème bit (bit de poids fort)
        int expectedPkTileSet = 0b01_000000_000000_000001_000001_000000;

        assertEquals(expectedPkTileSet, PkFloor.asPkTileSet(pkFloor));
    }

    @Test
    void toStringTrivialTest() {
// Ligne contenant : B, C, FIRST_PLAYER_MARKER
        int pkFloor = 0b101_010_001_011;

        String expected = "[B, C, FIRST_PLAYER_MARKER]";
        assertEquals(expected, PkFloor.toString(pkFloor));
    }
}