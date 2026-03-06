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

    @Test
    void sizeTrivialAndZeroTests(){
        int expected = 2;
        int pkPattern = 0b000_000_000_000_010_010_000_000_011_001;
        assertEquals(expected, PkPatterns.size(pkPattern, TileDestination.Pattern.PATTERN_3));
        expected=1;
        assertEquals(expected, PkPatterns.size(pkPattern, TileDestination.Pattern.PATTERN_1));
        expected=0;
        assertEquals(expected, PkPatterns.size(pkPattern, TileDestination.Pattern.PATTERN_2));
    }

    @Test
    void colorTrivialTest(){
        TileKind.Colored expected = TileKind.Colored.C;
        int pkPattern = 0b000_000_000_000_010_010_000_000_011_001;
        assertEquals(expected, PkPatterns.color(pkPattern, TileDestination.Pattern.PATTERN_3));
        expected = TileKind.Colored.D;
        assertEquals(expected, PkPatterns.color(pkPattern, TileDestination.Pattern.PATTERN_1));
    }

    @Test
    void isFullTrivialTests(){
        int pkPattern = 0b001_101_010_010_000_010_110_001_001_001;
        assertTrue(PkPatterns.isFull(pkPattern, TileDestination.Pattern.PATTERN_5));
        assertFalse(PkPatterns.isFull(pkPattern, TileDestination.Pattern.PATTERN_2));
    }

    @Test
    void canContainTrivialTest(){
        int pkPattern = 0b001_101_010_010_000_010_110_001_001_001;
        assertTrue(PkPatterns.canContain(pkPattern, TileDestination.Pattern.PATTERN_3, TileKind.Colored.A));
        assertFalse(PkPatterns.canContain(pkPattern, TileDestination.Pattern.PATTERN_3, TileKind.Colored.B));
    }

    @Test
    void withAddedTilesTrivialTest(){
        int pkPattern = 0b001_101_010_010_000_010_110_001_001_001;
        int count = 2;
        TileDestination.Pattern pattern = TileDestination.Pattern.PATTERN_4;
        TileKind.Colored color = TileKind.Colored.C;
        int expected = 0b001_101_010_100_000_010_110_001_001_001;
        assertEquals(expected, PkPatterns.withAddedTiles(pkPattern, pattern, count, color));
    }

    @Test
    void withEmptyLineTrivialTest(){
        int pkPattern = 0b001_101_010_010_000_010_110_001_001_001;
        TileDestination.Pattern pattern = TileDestination.Pattern.PATTERN_5;
        int expected = 0b000_000_010_010_000_010_110_001_001_001;
        assertEquals(expected, PkPatterns.withEmptyLine(pkPattern, pattern));
    }

    @Test
    void asPkTileSetTrivialTest(){
        int pkPattern = 0b001_101_010_010_000_010_100_001_001_001;
        int expected = 0b00_000001_000000_000010_000110_000010;
        assertEquals(expected, PkPatterns.asPkTileSet(pkPattern));
    }

    @Test
    void toStringTrivialTest(){
        int pkPattern = 0b001_101_010_010_000_010_100_001_001_001;
        String expected = "[B, E., AA., CC.., BBBBB]";
        assertEquals(expected, PkPatterns.toString(pkPattern));
    }

    @Test
    void emptyLinesHandlingTest() {
        int pkPattern = 0b000_000_000_000_000_000_100_001_001_001;

        String expectedStr = "[B, E., ..., ...., .....]";
        assertEquals(expectedStr, PkPatterns.toString(pkPattern));

        int expectedSet = 0b00_000001_000000_000000_000001_000000;
        assertEquals(expectedSet, PkPatterns.asPkTileSet(pkPattern));
    }

    @Test
    void withAddedTilesOnEmptyLineTest() {
        int pkPattern = PkPatterns.EMPTY;
        int expected = 0b000_000_000_000_000_011_000_000_000_000;

        int result = PkPatterns.withAddedTiles(pkPattern, TileDestination.Pattern.PATTERN_3, 3, TileKind.Colored.A);
        assertEquals(expected, result);
    }

    @Test
    void allEmptyTrivialTest() {
        int pkPattern = PkPatterns.EMPTY;

        assertEquals("[., .., ..., ...., .....]", PkPatterns.toString(pkPattern));
        assertEquals(PkTileSet.EMPTY, PkPatterns.asPkTileSet(pkPattern));
        assertTrue(PkPatterns.canContain(pkPattern, TileDestination.Pattern.PATTERN_1, TileKind.Colored.C));
    }

    private static int randomPkPatterns(java.util.random.RandomGenerator rng) {
        var pkPatterns = 0;
        for (var line : TileDestination.Pattern.ALL) {
            var capacity = line.capacity();
            var count = rng.nextInt(capacity + 1);
            var colorIndex = (count == 0) ? 0 : rng.nextInt(TileKind.Colored.COUNT);

            var lineVal = (colorIndex << 3) | count;
            pkPatterns |= (lineVal << (line.index() * 6));
        }
        return pkPatterns;
    }


    @Test
    void pkPatternsSizeAndColorWorksWithRandom() {
        var rng = java.util.random.RandomGeneratorFactory.getDefault().create(2026);
        for (var i = 0; i < 1000; i += 1) {
            var pkPatterns = randomPkPatterns(rng);

            for (var line : TileDestination.Pattern.ALL) {
                var offset = line.index() * 6;

                var expectedSize = (pkPatterns >> offset) & 0b111;
                assertEquals(expectedSize, PkPatterns.size(pkPatterns, line));

                if (expectedSize > 0) {
                    var expectedColorIndex = (pkPatterns >> (offset + 3)) & 0b111;
                    var expectedColor = TileKind.Colored.ALL.get(expectedColorIndex);
                    assertEquals(expectedColor, PkPatterns.color(pkPatterns, line));
                }
            }
        }
    }

    @Test
    void pkPatternsWithAddedTilesWorksRandomly() {
        var rng = java.util.random.RandomGeneratorFactory.getDefault().create(2026);
        for (var i = 0; i < 1000; i += 1) {
            var pkPatterns = randomPkPatterns(rng);

            for (var line : TileDestination.Pattern.ALL) {
                var currentSize = PkPatterns.size(pkPatterns, line);
                var remainingSpace = line.capacity() - currentSize;

                if (remainingSpace > 0) {
                    var addCount = rng.nextInt(1, remainingSpace + 1);
                    var color = (currentSize > 0) ?
                            PkPatterns.color(pkPatterns, line) :
                            TileKind.Colored.ALL.get(rng.nextInt(TileKind.Colored.COUNT));

                    var newPatterns = PkPatterns.withAddedTiles(pkPatterns, line, addCount, color);

                    assertEquals(currentSize + addCount, PkPatterns.size(newPatterns, line));
                    assertEquals(color, PkPatterns.color(newPatterns, line));
                }
            }
        }
    }

    @Test
    void pkPatternsWithEmptyLineWorksRandomly() {
        var rng = java.util.random.RandomGeneratorFactory.getDefault().create(2026);
        for (var i = 0; i < 1000; i += 1) {
            var pkPatterns = randomPkPatterns(rng);

            for (var line : TileDestination.Pattern.ALL) {
                var clearedPatterns = PkPatterns.withEmptyLine(pkPatterns, line);

                assertEquals(0, PkPatterns.size(clearedPatterns, line));

                for (var otherLine : TileDestination.Pattern.ALL) {
                    if (otherLine != line) {
                        assertEquals(PkPatterns.size(pkPatterns, otherLine), PkPatterns.size(clearedPatterns, otherLine));
                        if (PkPatterns.size(pkPatterns, otherLine) > 0) {
                            assertEquals(PkPatterns.color(pkPatterns, otherLine), PkPatterns.color(clearedPatterns, otherLine));
                        }
                    }
                }
            }
        }
    }


    @Test
    void pkPatternsCanContainWorksExhaustivelyOnEmpty() {
        var remainingCases = 5 * 5; // 5 lignes de motif * 5 couleurs possibles
        var emptyPattern = PkPatterns.EMPTY;

        for (var line : TileDestination.Pattern.ALL) {
            for (var color : TileKind.Colored.ALL) {
                assertTrue(PkPatterns.canContain(emptyPattern, line, color));
                remainingCases -= 1;
            }
        }
        assertEquals(0, remainingCases);
    }

    @Test
    void pkPatternsIsFullWorksExhaustively() {
        var remainingCases = 5;

        for (var line : TileDestination.Pattern.ALL) {
            var capacity = line.capacity();
            var pkPatterns = capacity << (line.index() * 6);

            assertTrue(PkPatterns.isFull(pkPatterns, line));

            if (capacity > 1) {
                var notFullPatterns = (capacity - 1) << (line.index() * 6);
                assertFalse(PkPatterns.isFull(notFullPatterns, line));
            }

            remainingCases -= 1;
        }
        assertEquals(0, remainingCases);
    }


}