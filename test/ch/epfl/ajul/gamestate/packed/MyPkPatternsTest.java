package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyPkPatternsTest {

    @Test
    void testEmptyValueIsZero() {
        assertEquals(0, PkPatterns.EMPTY);
    }

    @Test
    void testSizeOnEmptyPatternsIsZeroForAllLines() {
        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            assertEquals(0, PkPatterns.size(PkPatterns.EMPTY, line));
        }
    }

    @Test
    void testWithAddedTilesIncreasesSizeCorrectly() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_1, 1, TileKind.Colored.A);
        assertEquals(1, PkPatterns.size(p, TileDestination.Pattern.PATTERN_1));
    }

    @Test
    void testWithAddedTilesDoesNotAffectOtherLines() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_3, 2, TileKind.Colored.B);
        assertEquals(0, PkPatterns.size(p, TileDestination.Pattern.PATTERN_1));
        assertEquals(0, PkPatterns.size(p, TileDestination.Pattern.PATTERN_2));
        assertEquals(0, PkPatterns.size(p, TileDestination.Pattern.PATTERN_4));
        assertEquals(0, PkPatterns.size(p, TileDestination.Pattern.PATTERN_5));
    }

    @Test
    void testWithAddedTilesCumulatesTilesOnSameLine() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_5, 2, TileKind.Colored.E);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_5, 3, TileKind.Colored.E);
        assertEquals(5, PkPatterns.size(p, TileDestination.Pattern.PATTERN_5));
    }

    @Test
    void testColorReturnsCorrectColorAfterAddingTiles() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_4, 1, TileKind.Colored.D);
        assertEquals(TileKind.Colored.D, PkPatterns.color(p, TileDestination.Pattern.PATTERN_4));
    }

    @Test
    void testColorMultipleLines() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_2, 1, TileKind.Colored.A);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_3, 1, TileKind.Colored.B);
        assertEquals(TileKind.Colored.A, PkPatterns.color(p, TileDestination.Pattern.PATTERN_2));
        assertEquals(TileKind.Colored.B, PkPatterns.color(p, TileDestination.Pattern.PATTERN_3));
    }

    @Test
    void testIsFullReturnsFalseForEmptyLines() {
        assertFalse(PkPatterns.isFull(PkPatterns.EMPTY, TileDestination.Pattern.PATTERN_1));
        assertFalse(PkPatterns.isFull(PkPatterns.EMPTY, TileDestination.Pattern.PATTERN_5));
    }

    @Test
    void testIsFullReturnsFalseForPartiallyFilledLines() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_3, 2, TileKind.Colored.C);
        assertFalse(PkPatterns.isFull(p, TileDestination.Pattern.PATTERN_3));
    }

    @Test
    void testIsFullReturnsTrueForCompletelyFilledLines() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_5, 5, TileKind.Colored.E);
        assertTrue(PkPatterns.isFull(p, TileDestination.Pattern.PATTERN_5));
    }

    @Test
    void testCanContainReturnsTrueForEmptyLineAnyColor() {
        assertTrue(PkPatterns.canContain(PkPatterns.EMPTY, TileDestination.Pattern.PATTERN_2, TileKind.Colored.B));
    }

    @Test
    void testCanContainReturnsTrueForSameColor() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_4, 1, TileKind.Colored.C);
        assertTrue(PkPatterns.canContain(p, TileDestination.Pattern.PATTERN_4, TileKind.Colored.C));
    }

    @Test
    void testCanContainReturnsFalseForDifferentColor() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_4, 1, TileKind.Colored.C);
        assertFalse(PkPatterns.canContain(p, TileDestination.Pattern.PATTERN_4, TileKind.Colored.D));
    }

    @Test
    void testCanContainIgnoresIfLineIsFull() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_1, 1, TileKind.Colored.A);
        assertTrue(PkPatterns.canContain(p, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A));
    }

    @Test
    void testWithEmptyLineClearsCorrectLine() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_2, 2, TileKind.Colored.A);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_3, 3, TileKind.Colored.B);
        p = PkPatterns.withEmptyLine(p, TileDestination.Pattern.PATTERN_2);

        assertEquals(0, PkPatterns.size(p, TileDestination.Pattern.PATTERN_2));
        assertEquals(3, PkPatterns.size(p, TileDestination.Pattern.PATTERN_3));
    }

    @Test
    void testWithEmptyLineOnAlreadyEmptyLineDoesNothing() {
        int p = PkPatterns.EMPTY;
        int pAfter = PkPatterns.withEmptyLine(p, TileDestination.Pattern.PATTERN_5);
        assertEquals(PkPatterns.EMPTY, pAfter);
    }

    @Test
    void testAsPkTileSetOnEmptyPatterns() {
        assertEquals(PkTileSet.EMPTY, PkPatterns.asPkTileSet(PkPatterns.EMPTY));
    }

    @Test
    void testAsPkTileSetAggregatesCorrectly() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_2, 2, TileKind.Colored.A);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_4, 3, TileKind.Colored.A);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_5, 1, TileKind.Colored.E);

        int tileSet = PkPatterns.asPkTileSet(p);
        assertEquals(6, PkTileSet.size(tileSet));
        assertEquals(5, PkTileSet.countOf(tileSet, TileKind.Colored.A));
        assertEquals(1, PkTileSet.countOf(tileSet, TileKind.Colored.E));
    }

    @Test
    void testToStringEmpty() {
        assertEquals("[., .., ..., ...., .....]", PkPatterns.toString(PkPatterns.EMPTY));
    }

    @Test
    void testToStringExampleFromInstructions() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_1, 1, TileKind.Colored.C);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_2, 2, TileKind.Colored.A);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_3, 3, TileKind.Colored.A);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_4, 3, TileKind.Colored.E);

        assertEquals("[C, AA, AAA, EEE., .....]", PkPatterns.toString(p));
    }

    @Test
    void testToStringFullBoard() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_1, 1, TileKind.Colored.A);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_2, 2, TileKind.Colored.B);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_3, 3, TileKind.Colored.C);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_4, 4, TileKind.Colored.D);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_5, 5, TileKind.Colored.E);

        assertEquals("[A, BB, CCC, DDDD, EEEEE]", PkPatterns.toString(p));
    }

    @Test
    void testBinaryValueExampleFromInstructions() {
        int p = PkPatterns.EMPTY;
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_1, 1, TileKind.Colored.C);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_2, 2, TileKind.Colored.A);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_3, 3, TileKind.Colored.A);
        p = PkPatterns.withAddedTiles(p, TileDestination.Pattern.PATTERN_4, 3, TileKind.Colored.E);

        int expectedBinary = 0b000000000100011000011000010010001;
        assertEquals(expectedBinary, p);
    }
}